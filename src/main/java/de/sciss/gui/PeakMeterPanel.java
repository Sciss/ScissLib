/*
 *  PeakMeterPanel.java
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
import javax.swing.border.Border;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class PeakMeterPanel
        extends JPanel
        implements PeakMeterView, SwingConstants {

    protected PeakMeter[]		meters			= new PeakMeter[ 0 ];
    protected PeakMeterCaption	caption;
    private int					captionPosition	= LEFT;
    private int					captionAlign	= RIGHT;
    private boolean				captionVisible	= true;
    private boolean				captionLabels	= true;
    private int					numChannels		= 0;
    private boolean				border			= false;

    private boolean				rmsPainted		= true;
    private boolean				holdPainted		= true;

    private int					orient			= VERTICAL;
    private boolean				vertical		= true;

    public PeakMeterPanel() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        setFont(new Font("SansSerif", Font.PLAIN, 12));
        addPropertyChangeListener("font", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                if (caption != null) {
                    caption.setFont(getFont());
                    final Border b = BorderFactory.createEmptyBorder(caption.getAscent(), 1, caption.getDescent(), 1);
                    for (int ch = 0; ch < meters.length; ch++) {
                        meters[ch].setBorder(b);
                    }
                }
            }
        });
    }

    public void setOrientation(int o) {
        if (o != orient) {
            if (o != HORIZONTAL && o != VERTICAL) throw new IllegalArgumentException(String.valueOf(o));
            orient = o;
            vertical = orient == VERTICAL;
            if (caption != null) caption.setOrientation(orient);
            for (int ch = 0; ch < meters.length; ch++) {
                meters[ch].setOrientation(orient);
            }
            setLayout(new BoxLayout(this, vertical ?
                    BoxLayout.X_AXIS : BoxLayout.Y_AXIS));
            updateBorders();
            revalidate();
        }
    }

    // --------------- PeakMeterView interface ---------------

    public void setRMSPainted(boolean onOff) {
        if (rmsPainted == onOff) return;

        rmsPainted = onOff;
        for (int i = 0; i < meters.length; i++) {
            meters[i].setRMSPainted(onOff);
        }
    }

    public void setHoldPainted(boolean onOff) {
        if (holdPainted == onOff) return;

        holdPainted = onOff;
        for (int i = 0; i < meters.length; i++) {
            meters[i].setHoldPainted(onOff);
        }
    }

    public boolean meterUpdate(float[] peakRMSPairs, int offset, long time) {
        final PeakMeter[] metersCopy = meters;    // = easy synchronization
        final int numMeters = Math.min(metersCopy.length, peakRMSPairs.length >> 1);
        int dirty = 0;

        for (int i = 0, j = 0; i < numMeters; i++) {
            if (metersCopy[i].setPeakAndRMS(peakRMSPairs[j++], peakRMSPairs[j++], time)) dirty++;
        }

        return (dirty > 0);
    }

    public void clearMeter()
    {
        for( int i = 0; i < meters.length; i++ ) meters[ i ].clearMeter();
    }

    public void dispose()
    {
        for( int i = 0; i < meters.length; i++ ) meters[ i ].dispose();
    }

    // --------------- public methods ---------------

    public void setBorder(boolean onOff) {
        if (border == onOff) return;

        border = onOff;
        setBorder(onOff ? new RecessedBorder() : null);
        updateBorders();
    }

    public void setCaption(boolean onOff) {
        if (onOff == (caption != null)) return;

        if (onOff) {
            caption = new PeakMeterCaption(orient);
            caption.setFont(getFont());
            caption.setVisible(captionVisible);
            caption.setHorizontalAlignment(captionAlign);
            caption.setPaintLabels(captionLabels);
        } else {
            caption = null;
        }
        rebuildMeters();
    }

    public void setCaptionPosition(int pos) {
        if (captionPosition == pos) return;

        switch (pos) {
            case LEFT:
                captionAlign = RIGHT;
                break;
            case RIGHT:
                captionAlign = LEFT;
                break;
            case CENTER:
                captionAlign = CENTER;
                break;
            default:
                throw new IllegalArgumentException(String.valueOf(pos));
        }
        captionPosition = pos;

        if (caption != null) {
            caption.setHorizontalAlignment(captionAlign);
            rebuildMeters();
        }
    }

    public void setCaptionLabels(boolean onOff) {
        if (captionLabels == onOff) return;

        captionLabels = onOff;
        if (caption != null) {
            caption.setPaintLabels(captionLabels);
        }
    }

    public void setCaptionVisible(boolean visible) {
        if (captionVisible == visible) return;

        captionVisible = visible;
        if (caption != null) {
            caption.setVisible(captionVisible);
            updateBorders();
        }
    }

    public void setNumChannels(int numChannels) {
        if (numChannels != this.numChannels) {
            this.numChannels = numChannels;
            rebuildMeters();
        }
    }

    public int getNumChannels() {
        return numChannels;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        final Insets insets = getInsets();

        g.setColor(Color.black);
        g.fillRect(insets.left, insets.top,
                getWidth() - (insets.left + insets.right),
                getHeight() - (insets.top + insets.bottom));
    }

    // -------------- private methods --------------

    private void rebuildMeters() {
        removeAll();

        final PeakMeter[] newMeters;
        final Border b1 = caption == null ? null : BorderFactory.createEmptyBorder(caption.getAscent(), 1, caption.getDescent(), 1);
        final Border b2 = caption == null ? BorderFactory.createEmptyBorder(1, 1, vertical ? 1 : 0, vertical ? 0 : 1) : BorderFactory.createEmptyBorder(caption.getAscent(), 1, caption.getDescent(), 0);
        final int bool1, bool2;

        bool1 = (!border || (captionVisible && (captionPosition == RIGHT))) ? numChannels - 1 : -1;
        bool2 = (captionVisible && (captionPosition == CENTER)) ? (numChannels >> 1) : -1;

        newMeters = new PeakMeter[numChannels];
        for (int ch = 0; ch < numChannels; ch++) {
            newMeters[ch] = new PeakMeter(orient);
            newMeters[ch].setRefreshParent(true);
            newMeters[ch].setRMSPainted(rmsPainted);
            newMeters[ch].setHoldPainted(holdPainted);
            if ((ch == bool1) || (ch == bool2)) {
                if (b1 != null) newMeters[ch].setBorder(b1);
            } else {
                newMeters[ch].setBorder(b2);
            }
            newMeters[ch].setTicks(101);
            add(newMeters[ch]);
        }
        if (caption != null) {
            switch (captionPosition) {
                case LEFT:
                    add(caption, 0);
                    break;
                case RIGHT:
                    add(caption);
                    break;
                case CENTER:
                    add(caption, getComponentCount() >> 1);
                    break;
                default:
                    assert false : captionPosition;
            }
        }
        meters = newMeters;
        revalidate();
        repaint();
    }

    private void updateBorders() {
        final Border b1 = caption == null ? BorderFactory.createEmptyBorder(1, 1, 1, 1) : BorderFactory.createEmptyBorder(caption.getAscent(), 1, caption.getDescent(), 1);
        final Border b2 = caption == null ? BorderFactory.createEmptyBorder(1, 1, vertical ? 1 : 0, vertical ? 0 : 1) : BorderFactory.createEmptyBorder(caption.getAscent(), 1, caption.getDescent(), 0);
        final int bool1, bool2;

        bool1 = (!border || (captionVisible && (captionPosition == RIGHT))) ? (numChannels - 1) : -1;
        bool2 = (captionVisible && (captionPosition == CENTER)) ? (numChannels >> 1) : -1;

        for( int ch = 0; ch < numChannels; ch++ ) {
            if( (ch == bool1) || (ch == bool2) ) {
                meters[ ch ].setBorder( b1 );
            } else {
                meters[ ch ].setBorder( b2 );
            }
            meters[ ch ].setTicks( 101 );
        }
    }
}