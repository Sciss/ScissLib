/*
 *  BasicGraphicsHandler.java
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

package de.sciss.common;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import de.sciss.app.GraphicsHandler;

public class BasicGraphicsHandler
implements GraphicsHandler
{
    private final String	systemTypeFace;
    private final String	userTypeFace;
    private final String	labelTypeFace;
    private final int		mediumSize, smallSize, miniSize;

    private final Map		mapFonts	= new HashMap();

    public BasicGraphicsHandler()
    {
        final String os			= System.getProperty( "os.name" );
        final boolean isMacOS	= os.indexOf( "Mac OS" ) >= 0;
        final boolean isWindows	= os.indexOf( "Windows" ) >= 0;

        if( isMacOS ) {
            systemTypeFace = userTypeFace = labelTypeFace = "LucidaGrande";
        } else if( isWindows ) {
//			systemTypeFace = userTypeFace = labelTypeFace = "Arial";
            systemTypeFace = userTypeFace = labelTypeFace = "Tahoma";
        } else {
            systemTypeFace = userTypeFace = labelTypeFace = "SansSerif";
        }

        mediumSize	= 13;
        smallSize	= 11;
        miniSize	= 9;
    }

    public Font getFont( int type )
    {
        final Object key = new Integer( type );
        Font f = (Font) mapFonts.get( key );
        if( f != null ) return f;

        final int		size;
        final String	face;
        final int		style;

        switch( type & FONT_SIZE_MASK ) {
        case FONT_MEDIUM:
            size	= mediumSize;
            break;
        case FONT_SMALL:
            size	= smallSize;
            break;
        case FONT_MINI:
            size	= miniSize;
            break;
        default:
            throw new IllegalArgumentException( "Invalid type " + type );
        }

        switch( type & FONT_TYPE_MASK ) {
        case FONT_SYSTEM:
            face	= systemTypeFace;
            style	= Font.PLAIN;
            break;
        case FONT_BOLDSYSTEM:
            face	= systemTypeFace;
            style	= Font.BOLD;
            break;
        case FONT_USER:
            face	= userTypeFace;
            style	= Font.PLAIN;
            break;
        case FONT_LABEL:
            face	= labelTypeFace;
            style	= Font.PLAIN;
            break;
        default:
            throw new IllegalArgumentException( "Invalid type " + type );
        }

        f = new Font( face, style, size );
        mapFonts.put( key, f );
        return f;
    }
}