/*
 *  AbstractWindowHandler.java
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

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.sciss.app.AbstractApplication;
import de.sciss.app.AbstractWindow;
import de.sciss.app.GraphicsHandler;

/**
 *  A rudimentary implementation of the <code>de.sciss.app.WindowHandler</code>
 *	interface.
 *
 *  @author		Hanns Holger Rutz
 *  @version	0.17, 09-Jul-06
 */
public abstract class AbstractWindowHandler
implements de.sciss.app.WindowHandler
{
    private final List	collWindows	= new ArrayList();

    protected AbstractWindowHandler()
    {
        super();
    }

    /**
     *	synchronization:	this method is synchronized
     */
    public void addWindow( AbstractWindow w, Map options )
    {
        synchronized( collWindows ) {
            if( collWindows.contains( w )) {
                throw new IllegalArgumentException( "Duplicate window registration" );
            }
            collWindows.add( w );
        }
    }

    /**
     *	synchronization:	this method is synchronized
     */
    public void removeWindow( AbstractWindow w, Map options )
    {
        synchronized( collWindows ) {
            if( !collWindows.remove( w )) {
                throw new IllegalArgumentException( "Tried to remove unknown window" );
            }
        }
    }

    public Iterator getWindows()
    {
        synchronized( collWindows ) {
            return Collections.unmodifiableList( collWindows ).iterator();
        }
    }

    public static void setDeepFont( Container c )
    {
        setDeepFont( c, null );
    }

    public static void setDeepFont( Container c, List exclude )
    {
        setDeepFont( c,
            AbstractApplication.getApplication().getGraphicsHandler().getFont( GraphicsHandler.FONT_SYSTEM | GraphicsHandler.FONT_SMALL ), exclude );
    }

    private static void setDeepFont( Container c, Font fnt, List exclude )
    {
        final Component[] comp = c.getComponents();

        c.setFont( fnt );
        for( int i = 0; i < comp.length; i++ ) {
            if( (exclude != null) && exclude.contains( comp[ i ])) continue;

            if( comp[ i ] instanceof Container ) {
                setDeepFont( (Container) comp[i], fnt, exclude );
            } else {
                comp[ i ].setFont( fnt );
            }
        }
    }
}