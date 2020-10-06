/*
 *  DefaultUnitViewFactory.java
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

import de.sciss.util.ParamSpace;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class DefaultUnitViewFactory
        implements ParamField.UnitViewFactory {

    public Object createView(int unit) {
        String unitStr;
        String unitStrShort = null;

        switch (unit & ParamSpace.SPECIAL_MASK) {
            case ParamSpace.BARSBEATS:
                return "";
            case ParamSpace.HHMMSS:
                return new ClockIcon();
            case ParamSpace.MIDINOTE:
                return "\u266A";
            default:
                break;
        }

        switch (unit & ParamSpace.SCALE_MASK) {
            case ParamSpace.PERCENT:
                if ((unit & ParamSpace.REL_MASK) == ParamSpace.REL) {
                    return "%";
                } else {
                    return "\u0394 %";
                }
            case ParamSpace.DECIBEL:
                return "dB";
            default:
                break;
        }

        switch (unit & ParamSpace.UNIT_MASK) {
            case ParamSpace.NONE:
                unitStr = "";
                break;
            case ParamSpace.SECS:
                unitStrShort = "s";
                unitStr = "secs";
                break;
            case ParamSpace.SMPS:
                unitStr = "smps";
                break;
            case ParamSpace.BEATS:
                unitStrShort = "b";
                unitStr = "beats";
                break;
            case ParamSpace.HERTZ:
                unitStr = "Hz";
                break;
            case ParamSpace.PITCH:
                unitStr = "pch";
                break;
            case ParamSpace.DEGREES:
                unitStr = "\u00B0";
                break;
            case ParamSpace.METERS:
                unitStr = "m";
                break;
            case ParamSpace.PIXELS:
                unitStr = "px";
                break;
            default:
                unitStr = "???";
                break;
        }

        if (unitStrShort == null) unitStrShort = unitStr;

        switch (unit & ParamSpace.SCALE_MASK) {
            case ParamSpace.MILLI:
                unitStr = "m" + unitStrShort;
                break;
            case ParamSpace.CENTI:
                unitStr = "c" + unitStrShort;
                break;
            case ParamSpace.KILO:
                unitStr = "k" + unitStrShort;
                break;
            default:
                break;
        }

        switch (unit & ParamSpace.REL_MASK) {
            case ParamSpace.REL:
                return "";
            case ParamSpace.OFF:
                return "\u0394 " + unitStr;
            default:
                return unitStr;
        }
    }

    private static class ClockIcon
            implements Icon {

        private static final Stroke strkOutline         = new BasicStroke(1.5f);
        private static final Stroke strkHand            = new BasicStroke(0.5f);

        private static final Color  colrOutlineLight    = new Color(0x00, 0x00, 0x00, 0xC0);
        private static final Color  colrOutlineDark     = new Color(0xC8, 0xC8, 0xC8, 0xC0);

        private static final Color  colrInnerLight      = Color.black;
        private static final Color  colrInnerDark       = new Color(0xC8, 0xC8, 0xC8);

        private final Color colrOutline, colrInner;

        protected ClockIcon() {
            final boolean isDark = UIManager.getBoolean("dark-skin");
            colrOutline = isDark ? colrOutlineDark : colrOutlineLight;
            colrInner   = isDark ? colrInnerDark   : colrInnerLight;
        }

        public int getIconWidth() {
            return 16;
        }

        public int getIconHeight() {
            return 16;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            final Graphics2D g2 = (Graphics2D) g;
            final Stroke strkOrig = g2.getStroke();
            final AffineTransform atOrig = g2.getTransform();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.translate(0.5f + x, 0.5f + y);    // tricky disco to blur the outlines 'bit more
            g2.setColor(colrOutline);
            g2.setStroke(strkOutline);
            g2.drawOval(x, y, 14, 14);

            g2.setStroke(strkHand);
            g2.setColor(colrInner);
            g2.drawLine(x + 7, y + 7, x + 7, y + 2);
            g2.drawLine(x + 7, y + 7, x + 10, y + 10);

            g2.setTransform(atOrig);
            g2.setStroke(strkOrig);
        }
    }
}
