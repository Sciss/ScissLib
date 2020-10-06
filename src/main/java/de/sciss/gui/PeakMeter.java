/*
 *  PeakMeter.java
 *  (ScissLib)
 *
 *  Copyright (c) 2004-2020 Hanns Holger Rutz. All rights reserved.
 *
 *  This software is published under the GNU Lesser General Public License v2.1+
 *
 *
 *	For further information, please contact Hanns Holger Rutz at
 *	contact@sciss.de
 */

package de.sciss.gui;

import de.sciss.util.Disposable;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 *	A level (volume) meter GUI component. The component
 *	is a vertical bar displaying a green-to-reddish bar
 *	for the peak amplitude and a blue bar for RMS value.
 *	<p>
 *	To animate the bar, call <code>setPeakAndRMS</code> at a
 *	regular interval, typically around every 30 milliseconds
 *	for a smooth look.
 */
public class PeakMeter
        extends JComponent
        implements PeakMeterView, Disposable, SwingConstants {

    public static final int		DEFAULT_HOLD_DUR = 2500;

    private int					holdDuration	= DEFAULT_HOLD_DUR;	// milliseconds peak hold

    private float				peak;
    private float				rms;
    private float				hold;
    private float				peakToPaint;
    private float				rmsToPaint;
    private float				holdToPaint;
    private float				peakNorm;
    private float				rmsNorm;
    private float				holdNorm;

    private int					recentLength	= 0;
    private int					recentBreadth	= 0;
    private int					calcedLength	= -1;			// recentHeight snapshot in recalcPaint()
    private int					calcedBreadth	= -1;			// recentWidth snapshot in recalcPaint()
    private long				lastUpdate		= System.currentTimeMillis();
    private long				holdEnd;

    private boolean				holdPainted		= true;
    private boolean				rmsPainted		= true;

    private final static boolean	isMac		= System.getProperty("os.name").contains("Mac OS");
    private final static boolean	isWindows	= System.getProperty("os.name").contains("Windows");
    private final static boolean	isLinux		= !(isMac || isWindows);	// Well...

    /** <tt>true</tt> for bug: https://stackoverflow.com/questions/19480076 */
    public static final boolean animationNeedsSync =
            isLinux && System.getProperty("java.version").startsWith("1.8.");

    private static void sync() {
        if (animationNeedsSync) Toolkit.getDefaultToolkit().sync();
    }

    private static final int[]	bgPixels		= { 0xFF000000, 0xFF343434, 0xFF484848, 0xFF5C5C5C, 0xFF5C5C5C,
                                                    0xFF5C5C5C, 0xFF5C5C5C, 0xFF5C5C5C, 0xFF484848, 0xFF343434,
                                                    0xFF000000, 0xFF000000, 0xFF000000, 0xFF000000, 0xFF000000,
                                                    0xFF000000, 0xFF000000, 0xFF000000, 0xFF000000, 0xFF000000 };

    private static final int[]	rmsTopColor		= {	0x000068, 0x5537B9, 0x764EE5, 0x9062E8, 0x8B63E8,
                                                    0x8360E8, 0x7C60E8, 0x8876EB, 0x594CB4, 0x403A63 };
    private static final int[]	rmsBotColor		= {	0x000068, 0x2F4BB6, 0x4367E2, 0x577FE5, 0x577AE5,
                                                    0x5874E6, 0x596FE6, 0x6B7AEA, 0x4851B1, 0x393D62 };

    private static final int[]	peakTopColor	= { 0x000000, 0xB72929, 0xFF3C3C, 0xFF6B6B, 0xFF6B6B,
                                                    0xFF6B6B, 0xFF6B6B, 0xFFA7A7, 0xFF3C3C, 0xB72929 };

    private static final int[]	peakBotColor	= { 0x000000, 0x008E00, 0x00C800, 0x02FF02, 0x02FF02,
                                                     0x02FF02, 0x02FF02, 0x68FF68, 0x00C800, 0x008E00 };

    private Paint				pntBg; // , pntRMS, pntPeak;
    private BufferedImage		imgBg, imgRMS, imgPeak;

    private static final double logPeakCorr		= 20.0 / Math.log( 10 );
    private static final double logRMSCorr		= 10.0 / Math.log( 10 );

    private Insets				insets;

    private int					holdPixPos, peakPixPos, rmsPixPos;

    private int					peakPixPosP	= 0;
    private int					rmsPixPosP	= 0;
    private int					holdPixPosP	= 0;

    private boolean				refreshParent		= false;

    private int					ticks				= 0;
    private boolean				vertical; 			// false for horizontal layout

    public PeakMeter()
    {
        this( VERTICAL );
    }

    /**
     *	Creates a new level meter with default
     *	ballistics and bounds.
     */
    public PeakMeter( int orient )
    {
        super();

        setOpaque( true );
        setBorder( BorderFactory.createEmptyBorder( 1, 1, 1, 1 ));

        if( orient != HORIZONTAL && orient != VERTICAL ) {
            throw new IllegalArgumentException( String.valueOf( orient ));
        }
        vertical = orient == VERTICAL;

        recalcPrefSize();

        addPropertyChangeListener( "border", new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent e )
            {
                recalcPrefSize();
            }
        });

        clearMeter();
    }

    public void setOrientation( int orient )
    {
        if( orient != HORIZONTAL && orient != VERTICAL ) {
            throw new IllegalArgumentException( String.valueOf( orient ));
        }
        final boolean newVertical = orient == VERTICAL;
        if( newVertical != vertical ) {
            vertical = newVertical;
            disposeImages();
            recalcPrefSize();
            clearMeter();
        }
    }

    // ------------- PeakMeterView interface -------------

    public int getNumChannels() { return 1; }

    public boolean meterUpdate(float[] peakRMSPairs, int offset, long time) {
        final int offset2 = offset + 1;
        if (offset2 >= peakRMSPairs.length) return false;
        final boolean result = setPeakAndRMS(peakRMSPairs[offset], peakRMSPairs[offset2], time);
        sync();
        return result;
    }

    /**
     *	Decides whether the peak indicator should be
     *	painted or not. By default the indicator is painted.
     *
     *	@param	onOff	<code>true</code> to have the indicator painted,
     *					<code>false</code> to switch it off
     */
    public void setHoldPainted(boolean onOff) {
        if (holdPainted != onOff) {
            holdPainted = onOff;
            repaint();
        }
    }

    /**
     *	Decides whether the blue RMS bar should be
     *	painted or not. By default the bar is painted.
     *
     *	@param	onOff	<code>true</code> to have the RMS values painted,
     *					<code>false</code> to switch them off
     */
    public void setRMSPainted( boolean onOff )
    {
        if( rmsPainted != onOff ) {
            rmsPainted	= onOff;
            repaint();
        }
    }

    /**
     *	Clears the peak, peak hold and rms values
     *	immediately (without ballistics). This
     *	way the component can be reset when the
     *	metering task is stopped without waiting
     *	for the bars to fall down.
     */
    public void clearMeter()
    {
        final int w1, h1, len1, rlen1;

        w1		= getWidth()  - (insets.left + insets.right);
        h1		= getHeight() - (insets.top + insets.bottom);
        len1	= vertical ? h1 : w1;
        rlen1	= (len1 - 1) & ~1;

        peak			= -160f;
        rms				= -160f;
        hold			= -160f;
        peakToPaint		= -160f;
        rmsToPaint		= -160f;
        holdToPaint		= -160f;
        peakNorm		= -1.0f;
        rmsNorm			= -1.0f;
        holdNorm		= -1.0f;
        holdEnd			= System.currentTimeMillis();

        holdPixPos		= (int) (holdNorm * rlen1) & ~1;
        peakPixPos		= (int) (peakNorm * rlen1) & ~1;
        rmsPixPos		= Math.min( (int) (rmsNorm  * rlen1) & ~1, peakPixPos - 4 );

        if( refreshParent ) {
            getParent().repaint( insets.left + getX(), insets.top + getY(), w1, h1 );
        } else {
            repaint( insets.left, insets.top, w1, h1 );
        }
    }

    // ----------- public methods -----------

    public void setTicks( int ticks )
    {
        this.ticks = ticks;
        recalcPrefSize();
    }

    public void setRefreshParent( boolean onOff ) {
        refreshParent = onOff;
    }

    /**
     *	Sets the peak indicator hold time. Defaults to 1800 milliseconds.
     *
     *	@param	millis	new peak hold time in milliseconds. Note that
     *					the special value <code>-1</code> means infinite
     *					peak hold. In this case, to clear the indicator,
     *					call <code>clearHold</code>
     */
    public void setHoldDuration( int millis )
    {
//		synchronized( sync ) {
            holdDuration	= millis == -1 ? Integer.MAX_VALUE: millis;
            holdEnd			= System.currentTimeMillis();
//		}
    }

    /**
     *	Clears the peak hold
     *	indicator. Note that you will need
     *	to call <code>setPeakAndRMS</code> successively
     *	for the graphics to be updated.
     */
    public void clearHold()
    {
//		synchronized( sync ) {
            hold		= -160f;
            holdNorm	= 0.0f;
//		}
    }

    protected void recalcPrefSize()
    {
        final Dimension minDim, prefDim;
        insets = getInsets();
        if( vertical ) {
            final int w = 10 + insets.left + insets.right;
            minDim  = new Dimension( 4, 2 + insets.top + insets.bottom );
            prefDim = new Dimension( w, ticks <= 0 ? getPreferredSize().height : (ticks * 2 - 1 + insets.top + insets.bottom) );
        } else {
            final int h = 10 + insets.top + insets.bottom;
            minDim  = new Dimension( 2 + insets.left + insets.right, 4 );
            prefDim = new Dimension( ticks <= 0 ? getPreferredSize().width : (ticks * 2 - 1 + insets.left + insets.right), h );
        }
        setMinimumSize( minDim );
        setPreferredSize( prefDim );
    }

    public float getPeakDecibels()
    {
//		synchronized( sync ) {
            return peak <= -160f ? Float.NEGATIVE_INFINITY : peak;
//		}
    }

    public float getHoldDecibels()
    {
//		synchronized( sync ) {
            return hold <= -160f ? Float.NEGATIVE_INFINITY : hold;
//		}
    }

    /**
     *	Updates the meter. This will call the component's paint
     *	method to visually reflect the new values. Call this method
     *	regularly for a steady animated meter.
     *	<p>
     *	If you have switched off RMS painted, you may want to
     *	call <code>setPeak</code> alternatively.
     *	<p>
     *	When your audio engine is idle, you may want to stop meter updates.
     *	You can use the following formula to calculate the maximum delay
     *	of the meter display to be safely at minimum levels after starting
     *	to send zero amplitudes:
     *	</p><UL>
     *	<LI>for peak hold indicator not painted : delay[sec] = abs(minAmplitude[dB]) / fallTime[dB/sec]
     *	+ updatePeriod[sec]</LI>
     *	<LI>for painted peak hold : the maximum of the above value and
     *	delay[sec] = abs(minAmplitude[dB]) / holdFallTime[dB/sec] + holdTime[sec] + updatePeriod[sec]
     *	</LI>
     *	</UL><P>
     *	Therefore, for the default values of 1.8 sec hold time, 15 dB/sec hold fall time and -40 dB
     *	minimum amplitude, at a display period of 30 milliseconds, this yields a
     *	delay of around 4.5 seconds. Accounting for jitter due to GUI slowdown, in ths case it should be
     *	safe to stop meter updates five seconds after the audio engine stopped.
     *
     *	synchronization:	this method is thread safe
     *
     *	@param	peak	peak amplitude (linear) between zero and one.
     *	@param	rms		mean-square amplitude (linear). note : despite the name,
     *					this is considered mean-square, not root-mean-square. this
     *					method does the appropriate conversion on the fly!
     */
    public boolean setPeakAndRMS( float peak, float rms )
    {
        return setPeakAndRMS( peak, rms, System.currentTimeMillis() );
    }

    private float paintToNorm( float paint )
    {
        if( paint >= -30f ) {
            if( paint >= -20f ) {
                return Math.min( 1f, paint * 0.025f + 1.0f ); // 50 ... 100 %
            } else {
                return paint * 0.02f + 0.9f;  // 30 ... 50 %
            }
        } else if( paint >= -50f ) {
            if( paint >= -40f ) {
//			if( paint >= -45f ) {
//				return 0f;
//			} else {
                return paint * 0.015f + 0.75f;	// 15 ... 30 %
//			}
            } else {
                return paint * 0.01f + 0.55f;	// 5 ... 15%
            }
        } else if( paint >= -60f ) {
            return paint * 0.005f + 0.3f;	// 0 ... 5 %
        } else return -1f;
    }

    public boolean setPeakAndRMS(float newPeak, float newRMS, long time) {
        if (!EventQueue.isDispatchThread()) throw new IllegalMonitorStateException();

        final boolean	result;
        final int		len1, rlen1, w1, h1;

        newPeak		= (float) (Math.log( newPeak ) * logPeakCorr);
        if( newPeak >= peak ) {
            peak	= newPeak;
        } else {
            // 20 dB in 1500 ms bzw. 40 dB in 2500 ms
            peak = Math.max( newPeak, peak - (time - lastUpdate) * (peak > -20f ? 0.013333333333333f : 0.016f ));
        }
        peakToPaint	= Math.max( peakToPaint, peak );
        peakNorm 	= paintToNorm( peakToPaint );

        if( rmsPainted ) {
            newRMS			= (float) (Math.log( newRMS ) * logRMSCorr);
            if( newRMS > rms ) {
                rms	= newRMS;
            } else {
                rms = Math.max( newRMS, rms - (time - lastUpdate) * (rms > -20f ? 0.013333333333333f : 0.016f ));
            }
            rmsToPaint	= Math.max( rmsToPaint, rms );
            rmsNorm		= paintToNorm( rmsToPaint );
        }

        if( holdPainted ) {
            if( peak >= hold ) {
                hold	= peak;
                holdEnd	= time + holdDuration;
            } else if( time > holdEnd ) {
                if( peak > hold ) {
                    hold	= peak;
                } else {
                    hold   += (hold > -20f ? 0.013333333333333f : 0.016f ) * (lastUpdate - time);
                }
            }
            holdToPaint	= Math.max( holdToPaint, hold );
            holdNorm	= paintToNorm( holdToPaint );
            result		= holdNorm >= 0f;
        } else {
            result		= peakNorm >= 0f;
        }

        lastUpdate		= time;
        w1				= getWidth() - insets.left - insets.right;
        h1				= getHeight() - insets.top - insets.bottom;
        len1			= vertical ? h1 : w1;
        rlen1			= (len1 - 1) & ~1;
        recentLength	= rlen1 + 1;

        holdPixPos		= (int) (holdNorm * rlen1) & ~1;
        peakPixPos		= (int) (peakNorm * rlen1) & ~1;
        rmsPixPos		= Math.min( (int) (rmsNorm  * rlen1) & ~1, peakPixPos - 4 );

        // repaint only if pixel coords changed
        final boolean peakPixChanged = peakPixPos != peakPixPosP;
        final boolean rmsPixChanged  = rmsPixPos  != rmsPixPosP;
        final boolean holdPixChanged = holdPixPos != holdPixPosP;

        if( peakPixChanged || rmsPixChanged || holdPixChanged ) {
            int minPixPos, maxPixPos;

            // calculate dirty span
            if( peakPixPos < peakPixPosP ) {
                minPixPos = peakPixPos;
                maxPixPos = peakPixPosP;
            } else {
                minPixPos = peakPixPosP;
                maxPixPos = peakPixPos;
            }
            if( holdPainted ) {
                if( holdPixPos < holdPixPosP ) {
                    if( holdPixPos < minPixPos )  minPixPos = holdPixPos;
                    if( holdPixPosP > maxPixPos ) maxPixPos = holdPixPosP;
                } else {
                    if( holdPixPosP < minPixPos ) minPixPos = holdPixPosP;
                    if( holdPixPos > maxPixPos )  maxPixPos = holdPixPos;
                }
            }
            if( rmsPainted ) {
                if( rmsPixPos < rmsPixPosP ) {
                    if( rmsPixPos < minPixPos )  minPixPos = rmsPixPos;
                    if( rmsPixPosP > maxPixPos ) maxPixPos = rmsPixPosP;
                } else {
                    if( rmsPixPosP < minPixPos ) minPixPos = rmsPixPosP;
                    if( rmsPixPos > maxPixPos )  maxPixPos = rmsPixPos;
                }
            }

            final Container c;
            final int offX, offY;
            if( refreshParent ) {
                c		= getParent();
                offX	= insets.left + getX();
                offY	= insets.top + getY();
            } else {
                c 		= this;
                offX	= insets.left;
                offY	= insets.top;
            }

            // trigger repaint
            if( vertical ) {
                c.repaint( offX, offY + rlen1 - maxPixPos, w1, maxPixPos - minPixPos + 2 );
            } else {
                c.repaint( offX + minPixPos, offY, maxPixPos - minPixPos + 2, h1 );
            }
        } else {
            peakToPaint		= -160f;
            rmsToPaint		= -160f;
            holdToPaint		= -160f;
        }

        return result;
    }

    /**
     *	Updates the meter. This will call the component's paint
     *	method to visually reflect the peak amplitude. Call this method
     *	regularly for a steady animated meter. The RMS value is
     *	not changed, so this method is appropriate when having RMS
     *	painting turned off.
     *
     *	synchronization:	this method is thread safe
     *
     *	@param	newPeak	peak amplitude (linear) between zero and one.
     */
    public boolean setPeak( float newPeak )
    {
//		synchronized( sync ) {
            return setPeakAndRMS( newPeak, rms );
//		}
    }

    private void recalcPaint()
    {
        final int		imgLen		= (recentLength + 1) & ~1;
        final int		imgBrdth	= recentBreadth;
        final int		imgW, imgH;
        int[]			pix;

        if( imgPeak != null ) {
            imgPeak.flush();
            imgPeak = null;
        }
        if( imgRMS != null ) {
            imgRMS.flush();
            imgRMS = null;
        }

        if( vertical ) {	// ---- vertical ----
            if( (imgBg == null) || (imgBg.getWidth() != imgBrdth) ) {
                if( imgBg != null ) {
                    imgBg.flush();
                    imgBg = null;
                }
                if( imgBrdth == 10 ) {
                    pix	= bgPixels;
                } else {
                    pix	= widenPixV( bgPixels, 10, imgBrdth, 2 );
                }
                imgBg = new BufferedImage( imgBrdth, 2, BufferedImage.TYPE_INT_ARGB );
                imgBg.setRGB( 0, 0, imgBrdth, 2, pix, 0, imgBrdth );
                pntBg = new TexturePaint( imgBg, new Rectangle( 0, 0, imgBrdth, 2 ));
            }
            imgW = imgBrdth;
            imgH = imgLen;

        } else {	// ---- horizontal ----
            if( (imgBg == null) || (imgBg.getHeight() != imgBrdth) ) {
                if( imgBg != null ) {
                    imgBg.flush();
                    imgBg = null;
                }
                pix	= widenPixH( bgPixels, 10, imgBrdth, 2 );
                imgBg = new BufferedImage( 2, imgBrdth, BufferedImage.TYPE_INT_ARGB );
                imgBg.setRGB( 0, 0, 2, imgBrdth, pix, 0, 2 );
                pntBg = new TexturePaint( imgBg, new Rectangle( 0, 0, 2, imgBrdth ));
            }
            imgW = imgLen;
            imgH = imgBrdth;
        }
        pix	= hsbFade( imgBrdth, imgLen, rmsTopColor, rmsBotColor, vertical );
        imgRMS = new BufferedImage( imgW, imgH, BufferedImage.TYPE_INT_ARGB );
        imgRMS.setRGB( 0, 0, imgW, imgH, pix, 0, imgW );

        pix	= hsbFade( imgBrdth, imgLen, peakTopColor, peakBotColor, vertical );
        imgPeak = new BufferedImage( imgW, imgH, BufferedImage.TYPE_INT_ARGB );
        imgPeak.setRGB( 0, 0, imgW, imgH, pix, 0, imgW );

        calcedLength	= recentLength;
        calcedBreadth	= recentBreadth;
    }

    private static int[] widenPixV( int[] src, int srcBrdth, int dstBrdth, int len )
    {
        final int	minBrdth	= Math.min( srcBrdth, dstBrdth );
        final int	minBrdthH	= minBrdth >> 1;
        final int	minBrdthH1	= minBrdth - minBrdthH;
        final int 	numWiden	= dstBrdth - srcBrdth;
        final int[]	dst			= new int[ dstBrdth * len ];

        for( int y = 0, srcOffL = 0, srcOffR = srcBrdth - minBrdthH1,
                 dstOffL = 0, dstOffR = dstBrdth - minBrdthH1;
             y < len;
             y++, srcOffL += srcBrdth, srcOffR += srcBrdth,
                dstOffL += dstBrdth, dstOffR += dstBrdth ) {

            System.arraycopy( src, srcOffL, dst, dstOffL, minBrdthH );
            System.arraycopy( src, srcOffR, dst, dstOffR, minBrdthH1 );
        }
        if( numWiden > 0 ) {
            int p;
            for( int y = 0, srcOff = minBrdthH, dstOff = minBrdthH; y < len;
                    y++, srcOff += srcBrdth, dstOff += srcBrdth ) {
                p = src[ srcOff ];
                for( int stop = dstOff + numWiden; dstOff < stop; dstOff++ ) {
                    dst[ dstOff ] = p;
                }
            }
        }
        return dst;
    }

    private static int[] widenPixH( int[] src, int srcBrdth, int dstBrdth, int len )
    {
        final int	minBrdth	= Math.min( srcBrdth, dstBrdth );
        final int	minBrdthH	= minBrdth >> 1;
        final int	minBrdthH1	= minBrdth - minBrdthH;
        final int	brdthDOff	= dstBrdth - minBrdthH1;
        final int	brdthSOff	= srcBrdth - minBrdthH1;
        final int[]	dst			= new int[ dstBrdth * len ];

        int dstOff = 0;
        int y = 0;
        for( ; y < minBrdthH; y++ ) { // HHH
            for( int x = 0, srcOff = y; x < len; x++, dstOff++, srcOff += srcBrdth ) {
                dst[ dstOff ] = src[ srcOff ];
            }
        }
        for( ; y < brdthDOff; y++ ) {
            for( int x = 0, srcOff = minBrdthH; x < len; x++, dstOff++, srcOff += srcBrdth ) {
                dst[ dstOff ] = src[ srcOff ];
            }
        }
        for( int srcOffS = brdthSOff; y < dstBrdth; y++, srcOffS++ ) {
            for( int x = 0, srcOff = srcOffS; x < len; x++, dstOff++, srcOff += srcBrdth ) {
                dst[ dstOff ] = src[ srcOff ];
            }
        }
        return dst;
    }

    private static int[] hsbFade( int brdth, int len, int[] topColr, int[] botColr, boolean vertical )
    {
//System.out.println( "brdth = " + brdth + "; len = " + len );
        final int[] 	pix 		= new int[ brdth * len ];
        final int[] 	sTopColr, sBotColr;
        final float[]	hsbTop		= new float[ 3 ];
        final float[]	hsbBot		= new float[ 3 ];
        final float		w3			= 1.0f / (len - 2);
        int				rgb;
        float			w1, w2;

        if( brdth == 10 ) {
            sTopColr	= topColr;
            sBotColr	= botColr;
        } else {
            sTopColr	= widenPixV( topColr, 10, brdth, 1 );
            sBotColr	= widenPixV( botColr, 10, brdth, 1 );
        }

        for( int i = 0; i < brdth; i++ ) {
            rgb = sTopColr[ i ];
            Color.RGBtoHSB( (rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF, hsbTop );
            rgb = sBotColr[ i ];
            Color.RGBtoHSB( (rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF, hsbBot );
            if( vertical ) {
                for( int pixPos = 0, off = i; pixPos < len; pixPos += 2, off += (brdth << 1) ) {
                    w2	= pixPos * w3;
                    w1	= 1.0f - w2;
                    rgb	= Color.HSBtoRGB( hsbTop[0] * w1 + hsbBot[0] * w2,
                                          hsbTop[1] * w1 + hsbBot[1] * w2,
                                          hsbTop[2] * w1 + hsbBot[2] * w2 );
                    pix[ off ] = rgb | 0xFF000000;
                    pix[ off+brdth ] = 0xFF000000;
                }
            } else {
                for( int pixPos = 0, off = i * len; pixPos < len; pixPos += 2 ) {
                    w2	= pixPos * w3;
                    w1	= 1.0f - w2;
                    rgb	= Color.HSBtoRGB( hsbTop[0] * w2 + hsbBot[0] * w1,
                                          hsbTop[1] * w2 + hsbBot[1] * w1,
                                          hsbTop[2] * w2 + hsbBot[2] * w1 );
                    pix[ off++ ] = rgb | 0xFF000000;
                    pix[ off++ ] = 0xFF000000;
                }
            }
        }

        return pix;
    }

    public void paintComponent( Graphics g )
    {
        super.paintComponent( g );
        final Graphics2D		g2;
        final AffineTransform	atOrig;
        final int				len1, rlen1, len, w, h, w1, h1;

        w	= getWidth();
        h	= getHeight();
        w1	= w - (insets.left + insets.right);
        h1	= h - (insets.top + insets.bottom);

        if( vertical ) {
            len1			= h1;
            recentBreadth	= w1;
        } else {
            len1			= w1;
            recentBreadth	= h1;
        }
        rlen1		= (len1 - 1) & ~1;
        len			= rlen1 + 1;

        g.setColor( Color.black );
        g.fillRect( 0, 0, w, h );
        if( len <= 0 ) return;

        if( len != recentLength ) {
//			holdPixPos		= (rlen1 - (int) (holdNorm * rlen1) + 1) & ~1;
//			peakPixPos		= (rlen1 - (int) (peakNorm * rlen1) + 1) & ~1;
//			rmsPixPos		= Math.max( (rlen1 - (int) (rmsNorm  * rlen1) + 1) & ~1, peakPixPos + 4 );
            holdPixPos		= (int) (holdNorm * rlen1) & ~1;
            peakPixPos		= (int) (peakNorm * rlen1) & ~1;
            rmsPixPos		= Math.min( (int) (rmsNorm  * rlen1) & ~1, peakPixPos - 4 );
            recentLength	= len;
        }
        if( (calcedLength != recentLength) || (calcedBreadth != recentBreadth) ) {
            recalcPaint();
        }

        g2		= (Graphics2D) g;
        atOrig	= g2.getTransform();

        if( vertical ) {	// ---- vertical ----
            g2.translate( insets.left, insets.top + (len1 - len) );
            g2.setPaint( pntBg );
            final int holdPixPosI = rlen1 - holdPixPos;
            final int peakPixPosI = rlen1 - peakPixPos;
            if( rmsPainted ) {
                final int rmsPixPosI  = rlen1 - rmsPixPos;
                g2.fillRect( 0, 0, recentBreadth, Math.min( len, rmsPixPosI ));
                if( holdPainted && (holdPixPos >= 0) /* && (holdPixPos <= rlen1)*/ ) {
                    g2.drawImage( imgPeak, 0, holdPixPosI, recentBreadth, holdPixPosI + 1,
                                           0, holdPixPosI, recentBreadth, holdPixPosI + 1, this );
                }
                if( peakPixPos >= 0 ) {
                    final int lenClip = Math.min( len, rmsPixPosI - 2 );
                    g2.drawImage( imgPeak, 0, peakPixPosI, recentBreadth, lenClip,
                                           0, peakPixPosI, recentBreadth, lenClip, this );
                }
                if( rmsPixPos >= 0 ) {
                    g2.drawImage( imgRMS,  0, rmsPixPosI, recentBreadth, len,
                                           0, rmsPixPosI, recentBreadth, len, this );
                }
            } else {
                g2.fillRect( 0, 0, recentBreadth, peakPixPosI );
                if( holdPainted && (holdPixPos >= 0) /* && (holdPixPos <= rlen1)*/ ) {
                    g2.drawImage( imgPeak, 0, holdPixPosI, recentBreadth, holdPixPosI + 1,
                                           0, holdPixPosI, recentBreadth, holdPixPosI + 1, this );
                }
                if( peakPixPos >= 0 ) {
                    g2.drawImage( imgPeak, 0, peakPixPosI, recentBreadth, len,
                                           0, peakPixPosI, recentBreadth, len, this );
                }
            }
        } else {	// ---- horizontal ----
            g2.translate( insets.left, insets.top );
            g2.setPaint( pntBg );
            if( rmsPainted ) {
                final int rmsPixPosC = Math.max( 0, rmsPixPos );
                g2.fillRect( rmsPixPosC, 0, len - rmsPixPosC, recentBreadth );
                if( holdPainted && (holdPixPos >= 0) /* && (holdPixPos <= rlen1)*/ ) {
                    g2.drawImage( imgPeak, holdPixPos, 0, holdPixPos + 1, recentBreadth,
                                           holdPixPos, 0, holdPixPos + 1, recentBreadth, this );
                }
                if( peakPixPos >= 0 ) {
                    final int offClip = Math.max( 0, rmsPixPos + 3 );
                    g2.drawImage( imgPeak, offClip, 0, peakPixPos + 1, recentBreadth,
                                           offClip, 0, peakPixPos + 1, recentBreadth, this );
                }
                if( rmsPixPos >= 0 ) {
                    g2.drawImage( imgRMS,  0, 0, rmsPixPos + 1, recentBreadth,
                                           0, 0, rmsPixPos + 1, recentBreadth, this );
                }
            } else {
                final int peakPixPosC = Math.max( 0, peakPixPos );
                g2.fillRect( peakPixPosC, 0, len - peakPixPosC, recentBreadth );
                if( holdPainted && (holdPixPos >= 0) /* && (holdPixPos <= rlen1)*/ ) {
                    g2.drawImage( imgPeak, holdPixPos, 0, holdPixPos + 1, recentBreadth,
                                           holdPixPos, 0, holdPixPos + 1, recentBreadth, this );
                }
                if( peakPixPos >= 0 ) {
                    g2.drawImage( imgPeak, 0, 0, peakPixPos + 1, recentBreadth,
                                           0, 0, peakPixPos + 1, recentBreadth, this );
                }
            }
        }

        peakToPaint	= -160f;
        rmsToPaint	= -160f;
        holdToPaint	= -160f;
        peakPixPosP	= peakPixPos;
        rmsPixPosP	= rmsPixPos;
        holdPixPosP	= holdPixPos;

        g2.setTransform( atOrig );
    }

    // --------------- Disposable interface ---------------

    private void disposeImages()
    {
        if( imgPeak != null ) {
            imgPeak.flush();
            imgPeak = null;
        }
        if( imgRMS != null ) {
            imgRMS.flush();
            imgRMS = null;
        }
        if( imgBg != null ) {
            imgBg.flush();
            imgBg	= null;
            pntBg	= null;
        }
        calcedLength = -1;
    }

    public void dispose()
    {
        disposeImages();
    }
}