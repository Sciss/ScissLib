/*
 *  PeakMeterGroup.java
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

public class PeakMeterGroup
        implements PeakMeterView {

    private final PeakMeterView[] views;
    private final int numChannels;

    public PeakMeterGroup(PeakMeterView[] views) {
        this.views = views;
        int numCh = 0;
        for (int i = 0; i < views.length; i++) numCh += views[i].getNumChannels();
        numChannels = numCh;
    }

    public int getNumChannels() {
        return numChannels;
    }

    public boolean meterUpdate(float[] peakRMSPairs, int offset, long time) {
        int dirty = 0;

        for (int i = 0; i < views.length; i++) {
            if (views[i].meterUpdate(peakRMSPairs, offset, time)) dirty++;
            offset += views[i].getNumChannels() << 1;
        }

        return (dirty > 0);
    }

    public void clearMeter() {
        for (int i = 0; i < views.length; i++) views[i].clearMeter();
    }

    public void dispose() {
        for (int i = 0; i < views.length; i++) views[i].dispose();
    }
}