/*
 *  Axis.java
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

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.text.MessageFormat;
import java.util.Locale;

/**
 * A GUI element for displaying
 * the timeline's axis (ruler)
 * which is used to display the
 * time indices and to allow the
 * user to position and select the
 * timeline.
 *
 * @author Hanns Holger Rutz
 * @version 0.70, 21-Jan-09
 */
public class Axis
        extends JComponent {
    private static final long[] DECIMAL_RASTER = {100000000, 10000000, 1000000, 100000, 10000, 1000, 100, 10, 1};
    private static final long[] INTEGERS_RASTER = {100000000, 10000000, 1000000, 100000, 10000, 1000};
    private static final long[] TIME_RASTER = {60000000, 6000000, 600000, 60000, 10000, 1000, 100, 10, 1};

    private static final int MIN_LAB_SPC = 16;

    private int recentWidth     = 0;
    private int recentHeight    = 0;

    private boolean doRecalculate = true;

    private String[]    labels      = new String[0];
    private int[]       labelPos    = new int[0];

    private final GeneralPath shpTicks = new GeneralPath();

    private final int orient;
    private VectorSpace space;

    private final Paint pntBackground;
    private final Paint pntTicks;
    private final Paint pntLabel;

    private final BufferedImage img;
    private final Font fntLabel;    // = new Font( "Helvetica", Font.PLAIN, 10 );

    // the following are used for Number to String conversion using MessageFormat
    private static final String[] msgNormalPattern = {"{0,number,0}",
            "{0,number,0.0}",
            "{0,number,0.00}",
            "{0,number,0.000}"};
    private static final String[] msgTimePattern = {"{0,number,integer}:{1,number,00}",
            "{0,number,integer}:{1,number,00.0}",
            "{0,number,integer}:{1,number,00.00}",
            "{0,number,integer}:{1,number,00.000}"};

    private final MessageFormat msgForm = new MessageFormat(msgNormalPattern[0], Locale.US);  // XXX US locale
    private final Object[] msgArgs = new Object[2];

    private static final int[] pntBarGradPixLight = {
            0xFFB8B8B8, 0xFFC0C0C0, 0xFFC8C8C8, 0xFFD3D3D3,
            0xFFDBDBDB, 0xFFE4E4E4, 0xFFEBEBEB, 0xFFF1F1F1,
            0xFFF6F6F6, 0xFFFAFAFA, 0xFFFBFBFB, 0xFFFCFCFC,
            0xFFF9F9F9, 0xFFF4F4F4, 0xFFEFEFEF};

    private static final int[] pntBarGradPixDark = {
            0xFF080808, 0xFF101010, 0xFF141414, 0xFF1B1B1B,
            0xFF1D1D1D, 0xFF222222, 0xFF252525, 0xFF282828,
            0xFF2B2B2B, 0xFF2D2D2D, 0xFF2D2D2D, 0xFF2E2E2E,
            0xFF2C2C2C, 0xFF2A2A2A, 0xFF272727};

    private static final int barExtent = pntBarGradPixLight.length;

    private final AffineTransform trnsVertical = new AffineTransform();

    private String[] msgPattern;
    private long[] labelRaster;
    private long labelMinRaster;
    private int flags = -1;

    private boolean flMirror;            // MIRROIR     set
    private boolean flTimeFormat;        // TIMEFORMAT  set
    private boolean flIntegers;          // INTEGERS    set
    private boolean flFixedBounds;       // FIXEDBOUNDS set

    private static final double ln10 = Math.log(10);

    /**
     * Defines the axis to have horizontal orient
     */
    public static final int HORIZONTAL = 0x00;
    /**
     * Defines the axis to have vertical orient
     */
    public static final int VERTICAL = 0x01;
    /**
     * Flag: Defines the axis to have flipped min/max values.
     * I.e. for horizontal orient, the maximum value
     * corresponds to the left edge, for vertical orient
     * the maximum corresponds to the bottom edge
     */
    public static final int MIRROIR = 0x02;
    /**
     * Flag: Requests the labels to be formatted as MIN:SEC.MILLIS
     */
    public static final int TIMEFORMAT = 0x04;
    /**
     * Flag: Requests that the label values be integers
     */
    public static final int INTEGERS = 0x08;
    /**
     * Flag: Requests that the space's min and max are always displayed
     * and hence subdivision are made according to the bounds
     */
    public static final int FIXEDBOUNDS = 0x10;

//	private static final int HV_MASK	= 0x01;

    private final ComponentHost host;

    public Axis(int orient) {
        this(orient, UIManager.getBoolean("dark-skin"));
    }

    public Axis(int orient, boolean dark) {
        this(orient, 0, dark);
    }

    /**
     * @param    orient    either HORIZONTAL or VERTICAL
     */
    public Axis(int orient, int flags) {
        this(orient, flags, UIManager.getBoolean("dark-skin"));
    }

    public Axis(int orient, int flags, boolean dark) {
        this(orient, flags, dark, null);
    }

    public Axis(int orient, int flags, ComponentHost host) {
        this(orient, flags, UIManager.getBoolean("dark-skin"), host);
    }

    public Axis(int orient, int flags, boolean dark, ComponentHost host) {
        super();

        this.orient = orient;
        this.host = host;

        int imgWidth, imgHeight;

        Font f0 = UIManager.getFont("Slider.font", Locale.US);
        fntLabel = (f0 != null) ? f0.deriveFont(Math.min(f0.getSize2D(), 9.5f)) : new Font("SansSerif", Font.PLAIN, 9);

        if (orient == HORIZONTAL) {
            setMaximumSize(new Dimension(getMaximumSize().width, barExtent));
            setMinimumSize(new Dimension(getMinimumSize().width, barExtent));
            setPreferredSize(new Dimension(getPreferredSize().width, barExtent));
            imgWidth = 1;
            imgHeight = barExtent;
        } else {
            setMaximumSize(new Dimension(barExtent, getMaximumSize().height));
            setMinimumSize(new Dimension(barExtent, getMinimumSize().height));
            setPreferredSize(new Dimension(barExtent, getPreferredSize().height));
            imgWidth = barExtent;
            imgHeight = 1;
        }

        setFlags(flags);

        img = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
        final int[] pntBarGradientPixels = dark ? pntBarGradPixDark : pntBarGradPixLight;
        img.setRGB(0, 0, imgWidth, imgHeight, pntBarGradientPixels, 0, imgWidth);
        pntBackground = new TexturePaint(img, new Rectangle(0, 0, imgWidth, imgHeight));

        pntTicks = dark ? new Color(192, 192, 192, 0xA0) : Color.lightGray;
        pntLabel = dark ? Color.lightGray : Color.black;

        setOpaque(true);
    }

    public void setFlags(int flags) {
        if (this.flags == flags) return;

        this.flags = flags;
        flMirror = (flags & MIRROIR) != 0;
        flTimeFormat = (flags & TIMEFORMAT) != 0;
        flIntegers = (flags & INTEGERS) != 0;
        flFixedBounds = (flags & FIXEDBOUNDS) != 0;

        if (flTimeFormat) {
            msgPattern = msgTimePattern;
            labelRaster = TIME_RASTER;
        } else {
            msgPattern = msgNormalPattern;
            labelRaster = flIntegers ? INTEGERS_RASTER : DECIMAL_RASTER;
        }
        labelMinRaster = labelRaster[labelRaster.length - 1];

        triggerRedisplay();
    }

    public int getFlags() {
        return flags;
    }

    public void setSpace(VectorSpace space) {
        this.space = space;
        triggerRedisplay();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        final Graphics2D g2 = (Graphics2D) g;
        final int w = getWidth();
        final int h = getHeight();
//		final Graphics2D		g2          = (Graphics2D) g;
//		final Stroke			strkOrig	= g2.getStroke();
        final AffineTransform trnsOrig = g2.getTransform();
        final FontMetrics fm = g2.getFontMetrics();

        final int y;

        g2.setFont(fntLabel);

        if (doRecalculate || (w != recentWidth) || (h != recentHeight)) {
            recentWidth = w;
            recentHeight = h;
            recalculateLabels(g);
            if (orient == VERTICAL) recalculateTransforms();
            doRecalculate = false;
        }

        g2.setPaint(pntBackground);
        g2.fillRect(0, 0, w, h);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        if (orient == VERTICAL) {
            g2.transform(trnsVertical);
            y = w - 2 /* 3 */ - fm.getMaxDescent();
        } else {
            y = h - 2 /* 3 */ - fm.getMaxDescent();
        }
        g2.setPaint(pntTicks /* Color.lightGray */);
        g2.draw(shpTicks);
//		g2.setStroke( strkOrig );

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(pntLabel /* Color.black */);

        for (int i = 0; i < labels.length; i++) {
            g2.drawString(labels[i], labelPos[i], y);
        }

        g2.setTransform(trnsOrig);
    }

    private void recalculateTransforms() {
        trnsVertical.setToRotation(-Math.PI / 2, (double) recentHeight / 2,
                (double) recentHeight / 2);
    }

    private int calcStringWidth(FontMetrics fntMetrics, double value) {
        if (flTimeFormat) {
            msgArgs[0] = (int) (value / 60);
            msgArgs[1] = value % 60;
        } else {
            msgArgs[0] = value;
        }
        return fntMetrics.stringWidth(msgForm.format(msgArgs));
    }

    private int calcMinLabSpc(FontMetrics fntMetrics, double min, double max) {
        return Math.max(calcStringWidth(fntMetrics, min), calcStringWidth(fntMetrics, max)) + MIN_LAB_SPC;
    }

    private void recalculateLabels(Graphics g) {
        final FontMetrics fntMetrics = g.getFontMetrics();
        int shift, width, height, numTicks, numLabels, patternIdx, patternIdx2, minLbDist;
        double scale, pixelOff, pixelStep, tickStep, minK, maxK;
        long raster, n;
        double valueOff, valueStep, spcMin, spcMax;

        shpTicks.reset();
        if (space == null) {
            labels = new String[0];
            labelPos = new int[0];
            return;
        }

        if (orient == HORIZONTAL) {
            if (space.hlog) {
                recalculateLogLabels();
                return;
            }
            width   = recentWidth;
            height  = recentHeight;
            spcMin  = space.hmin;
            spcMax  = space.hmax;
        } else {
            if (space.vlog) {
                recalculateLogLabels();
                return;
            }
            width   = recentHeight;
            height  = recentWidth;
            spcMin  = space.vmin;
            spcMax  = space.vmax;
        }
        scale = width / (spcMax - spcMin);
        final double kPeriod = 1000.0;
        minK = kPeriod * spcMin;
        maxK = kPeriod * spcMax;

        if (flFixedBounds) {
            n = (long) Math.abs(minK);
            if ((n % 1000) == 0) {
                patternIdx = 0;
            } else if ((n % 100) == 0) {
                patternIdx = 1;
            } else if ((n % 10) == 0) {
                patternIdx = 2;
            } else {
                patternIdx = 3;
            }
            n = (long) Math.abs(maxK);
            if ((n % 1000) == 0) {
                // nix
            } else if ((n % 100) == 0) {
                patternIdx = Math.max(patternIdx, 1);
            } else if ((n % 10) == 0) {
                patternIdx = Math.max(patternIdx, 2);
            } else {
                patternIdx = 3;
            }

            // make a first label width calculation with coarsest display
            msgForm.applyPattern(msgPattern[patternIdx]);
            minLbDist = calcMinLabSpc(fntMetrics, spcMin, spcMax);
            numLabels = Math.max(1, width / minLbDist);

            // ok, easy way : only divisions by powers of two
            for (shift = 0; numLabels > 2; shift++, numLabels >>= 1) ;
            numLabels <<= shift;
            valueStep = (maxK - minK) / numLabels;

            n = (long) valueStep;
            if ((n % 1000) == 0) {
                patternIdx2 = patternIdx;
            } else if ((n % 100) == 0) {
                patternIdx2 = Math.max(patternIdx, 1);
            } else if ((n % 10) == 0) {
                patternIdx2 = Math.max(patternIdx, 2);
            } else {
                patternIdx2 = 3;
            }

            if (patternIdx2 != patternIdx) {    // ok, labels get bigger, recalculate numLabels ...
                msgForm.applyPattern(msgPattern[patternIdx2]);
                minLbDist = calcMinLabSpc(fntMetrics, spcMin, spcMax);
                numLabels = Math.max(1, width / minLbDist);
                for (shift = 0; numLabels > 2; shift++, numLabels >>= 1) ;
                numLabels <<= shift;
                valueStep = (maxK - minK) / numLabels;

                // recalculate patternIdx, it may be that the resolution is reduced again
                n = (long) valueStep;
                if ((n % 1000) == 0) {
                    patternIdx2 = patternIdx;
                } else if ((n % 100) == 0) {
                    patternIdx2 = Math.max(patternIdx, 1);
                } else if ((n % 10) == 0) {
                    patternIdx2 = Math.max(patternIdx, 2);
                } else {
                    patternIdx2 = 3;
                }
                msgForm.applyPattern(msgPattern[patternIdx2]);
            }

            numTicks = 4;
            valueOff = minK;
            pixelOff = 0;

        } else {
            // make a first label width calculation with coarsest display
            msgForm.applyPattern(msgPattern[0]);
            minLbDist = calcMinLabSpc(fntMetrics, spcMin, spcMax);
            numLabels = Math.max(1, width / minLbDist);

            // now valueStep =^= 1000 * minStep
            valueStep = Math.ceil((maxK - minK) / numLabels);
            // magnitude of valueStep is indicator for message pattern
            patternIdx = flIntegers ? 0 : 3;
            raster = labelMinRaster;
            for (int i = 0; i < labelRaster.length; i++) {
                if (valueStep >= labelRaster[i]) {
                    patternIdx = Math.max(0, i - 5);
                    raster = labelRaster[i];
                    break;
                }
            }
            msgForm.applyPattern(msgPattern[patternIdx]);
            if (patternIdx > 0) {    // have to recheck label width!
                minLbDist = Math.max(calcStringWidth(fntMetrics, spcMin), calcStringWidth(fntMetrics, spcMax)) + MIN_LAB_SPC;
                numLabels = Math.max(1, width / minLbDist);
                valueStep = Math.ceil((maxK - minK) / numLabels);
            }
//System.err.println( "width "+width+"; numLabels "+numLabels+"; minLbDist "+minLbDist+"; valueStep "+valueStep+"; minK "+minK+"; maxK "+maxK+"; raster "+raster );

//			valueStep	= Math.max( 1, ((long) valueStep + (raster >> 1)) / raster );
// round up!
            valueStep = Math.max(1, Math.floor((valueStep + raster - 1) / raster));
            if (valueStep > 9) {
                numTicks = 5;
            } else {
                switch ((int) valueStep) {
                    case 2:
                    case 4:
                    case 8:
                        numTicks = 4;
                        break;
                    case 3:
                    case 6:
                        numTicks = 6;
                        break;
                    case 7:
                    case 9:
                        valueStep = 10;
                        numTicks = 5;
                        break;
                    default:
                        numTicks = 5;
                        break;
                }
            }
            valueStep *= raster;
//System.err.println( "now valueStep = "+valueStep );

            valueOff = Math.floor(Math.abs(minK) / valueStep) * (minK >= 0 ? valueStep : -valueStep);
            pixelOff = (valueOff - minK) / kPeriod * scale + 0.5;
        }
        pixelStep = valueStep / kPeriod * scale;
        tickStep = pixelStep / numTicks;

        numLabels = Math.max(0, (int) ((width - pixelOff + pixelStep - 1.0) / pixelStep));
        if (labels.length != numLabels) labels = new String[numLabels];
        if (labelPos.length != numLabels) labelPos = new int[numLabels];

        if (flMirror) {
            pixelOff = width - pixelOff;
            tickStep = -tickStep;
        }

//System.err.println( "valueOff = "+valueOff+"; valueStep = "+valueStep+"; pixelStep "+pixelStep+"; tickStep "+tickStep+
//					"; pixelOff "+pixelOff+"; d1 "+d1 );
        for (int i = 0; i < numLabels; i++) {
            if (flTimeFormat) {
                msgArgs[0] = (int) (valueOff / 60000);
                msgArgs[1] = (valueOff % 60000) / 1000;
            } else {
                msgArgs[0] = valueOff / kPeriod;
            }
            labels[i] = msgForm.format(msgArgs);
            labelPos[i] = (int) pixelOff + 2;
            valueOff += valueStep;
            shpTicks.moveTo((float) pixelOff, 1);
            shpTicks.lineTo((float) pixelOff, height - 2);
            pixelOff += tickStep;
            for (int k = 1; k < numTicks; k++) {
                shpTicks.moveTo((float) pixelOff, height - 4);
                shpTicks.lineTo((float) pixelOff, height - 2);
                pixelOff += tickStep;
            }
        }
    }

    private void recalculateLogLabels() {
        int numLabels, width, height, numTicks, mul, exp, newPatternIdx, patternIdx;
        double spaceOff, factor, d1, pixelOff, min, max;

        if (orient == HORIZONTAL) {
            width   = recentWidth;
            height  = recentHeight;
            min     = space.hmin;
            max     = space.hmax;
        } else {
            width   = recentHeight;
            height  = recentWidth;
            min     = space.vmin;
            max     = space.vmax;
        }

        factor = Math.pow(max / min, (double) 72 / (double) width);    // XXX
        exp = (int) (Math.log(factor) / ln10);
        mul = (int) (Math.ceil(factor / Math.pow(10, exp)) + 0.5);

//System.out.println( "orig : factor " + factor + "; exp " + exp + "; mul " + mul );

        if (mul > 5) {
            exp++;
            mul = 1;
        } else if (mul > 3) {
            mul = 4;
        } else if (mul > 2) {
            mul = 5;
        }
        factor = mul * Math.pow(10, exp);

        numLabels = (int) (Math.ceil(Math.log(max / min) / Math.log(factor)) + 0.5);

//System.out.println( "max " + max + "; min " + min + "; width " + width + "; numLabels " + numLabels + "; factor " + factor + "; exp " + exp + "; mul " + mul );

        if (labels.length != numLabels) labels = new String[numLabels];
        if (labelPos.length != numLabels) labelPos = new int[numLabels];

//		if( (min * (factor - 1.0)) % 10 == 0.0 ) {
//			numTicks	= 10;
//		} else {
        numTicks = 8;
//		}
//		tickFactor	= Math.pow( factor, 1.0 / numTicks );

//System.err.println( "factor "+factor+"; exp "+exp+"; mul "+mul+"; tickFactor "+tickFactor+"; j "+j );

        patternIdx = -1;

        for (int i = 0; i < numLabels; i++) {
            spaceOff = min * Math.pow(factor, i);
            newPatternIdx = 3;
            for (int k = 1000; k > 1 && (((spaceOff * k) % 1.0) == 0); k /= 10) {
                newPatternIdx--;
            }
            if (patternIdx != newPatternIdx) {
                msgForm.applyPattern(msgPattern[newPatternIdx]);
                patternIdx = newPatternIdx;
            }

            if (orient == HORIZONTAL) {
                pixelOff = space.hSpaceToUnity(spaceOff) * width;
            } else {
                pixelOff = space.vSpaceToUnity(spaceOff) * width;
            }
//System.err.println( "#"+i+" : spaceOff = "+spaceOff+"; pixelOff "+pixelOff );
            msgArgs[0] = spaceOff;
            labels[i] = msgForm.format(msgArgs);
            labelPos[i] = (int) pixelOff + 2;
            shpTicks.moveTo((float) pixelOff, 1);
            shpTicks.lineTo((float) pixelOff, height - 2);
            d1 = spaceOff * (factor - 1) / numTicks;
            for (int n = 1; n < numTicks; n++) {
                if (orient == HORIZONTAL) {
                    pixelOff = space.hSpaceToUnity(spaceOff + d1 * n) * width;
                } else {
                    pixelOff = space.vSpaceToUnity(spaceOff + d1 * n) * width;
                }
                shpTicks.moveTo((float) pixelOff, height - 4);
                shpTicks.lineTo((float) pixelOff, height - 2);
            }
        }
    }

    private void triggerRedisplay() {
        doRecalculate = true;
        if (host != null) {
//System.err.println( "host.update" );
            host.update(this);
        } else if (isVisible()) {
//System.err.println( "repaint" );
            repaint();
        }
    }

    // -------------- Disposable interface --------------

    public void dispose() {
        labels = null;
        labelPos = null;
        shpTicks.reset();
        img.flush();
    }
}