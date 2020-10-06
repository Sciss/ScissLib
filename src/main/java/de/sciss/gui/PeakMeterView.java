/*
 *  PeakMeterView.java
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

public interface PeakMeterView
        extends Disposable {

    public int getNumChannels();

    public void clearMeter();

    public boolean meterUpdate(float[] peakRMSPairs, int offset, long time);
}
