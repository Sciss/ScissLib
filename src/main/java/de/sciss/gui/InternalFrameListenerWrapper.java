/*
 *  InternalFrameListenerWrapper.java
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

import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import de.sciss.app.AbstractWindow;

/**
 *  @author		Hanns Holger Rutz
 *  @version	0.70, 19-Mar-08
 */
public class InternalFrameListenerWrapper
implements InternalFrameListener
{
//	private static	Map							mapListeners = null;
    private final	AbstractWindow.Listener		l;
    private final	AbstractWindow				w;

    private InternalFrameListenerWrapper( AbstractWindow.Listener l, AbstractWindow w  )
    {
        this.l	= l;
        this.w	= w;
    }

    public static void add( AbstractWindow.Listener l, AbstractWindow w )
    {
        final InternalFrameListenerWrapper iflw = new InternalFrameListenerWrapper( l, w );
        ((JInternalFrame) w.getWindow()).addInternalFrameListener( iflw );
//		if( mapListeners == null ) mapListeners = new HashMap();
//		mapListeners.put( w, iflw );
    }

    public static void remove( AbstractWindow.Listener l, AbstractWindow w )
    {
        final JInternalFrame jif = (JInternalFrame) w.getWindow();
//		final InternalFrameListenerWrapper iflw = (InternalFrameListenerWrapper) mapListeners.remove( w );
//		jif.removeInternalFrameListener( iflw );

        final InternalFrameListener[] coll = jif.getInternalFrameListeners();
        InternalFrameListenerWrapper iflw;
        for( int i = 0; i < coll.length; i++ ) {
            if( coll[ i ] instanceof InternalFrameListenerWrapper ) {
                iflw = (InternalFrameListenerWrapper) coll[ i ];
                if( iflw.l == l ) {
                    jif.removeInternalFrameListener( iflw );
                    return;
                }
            }
        }
        throw new IllegalArgumentException( "Listener was not registered " + l );
    }

    public void internalFrameOpened( InternalFrameEvent e )
    {
        l.windowOpened( wrap( e ));
    }

    public void internalFrameClosing( InternalFrameEvent e )
    {
        l.windowClosing( wrap( e ));
    }

    public void internalFrameClosed( InternalFrameEvent e )
    {
        l.windowClosed( wrap( e ));
    }

    public void internalFrameIconified( InternalFrameEvent e )
    {
        l.windowIconified( wrap( e ));
    }

    public void internalFrameDeiconified( InternalFrameEvent e )
    {
        l.windowDeiconified( wrap( e ));
    }

    public void internalFrameActivated( InternalFrameEvent e )
    {
        l.windowActivated( wrap( e ));
    }

    public void internalFrameDeactivated( InternalFrameEvent e )
    {
        l.windowDeactivated( wrap( e ));
    }

    private AbstractWindow.Event wrap( InternalFrameEvent e )
    {
        return AbstractWindow.Event.convert( w, e );
    }
}