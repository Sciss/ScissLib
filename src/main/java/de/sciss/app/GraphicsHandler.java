/*
 *  GraphicsHandler.java
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

package de.sciss.app;

import java.awt.Font;

public interface GraphicsHandler
{
    public static final int FONT_SYSTEM			= 0x000;
    public static final int FONT_BOLDSYSTEM		= 0x001;
    public static final int FONT_USER			= 0x002;
    public static final int FONT_LABEL			= 0x003;

    public static final int FONT_TYPE_MASK		= 0x0FF;

    public static final int FONT_MEDIUM			= 0x000;
    public static final int FONT_SMALL			= 0x100;
    public static final int FONT_MINI			= 0x200;

    public static final int FONT_SIZE_MASK		= 0xF00;

    public Font getFont( int type );
}
