/*
 *  ShowWindowAction.java
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

import java.awt.event.ActionEvent;

import de.sciss.app.AbstractApplication;
import de.sciss.app.AbstractWindow;
import de.sciss.gui.MenuAction;
import de.sciss.util.Disposable;

/**
 * 	@version	0.11, 28-Jun-08
 *	@author		Hanns Holger Rutz
 *
 */
public class ShowWindowAction
extends MenuAction
implements Disposable
{
    private final AbstractWindow			w;
    private final AbstractWindow.Listener	l;
    protected boolean						disposed	= false;

    public ShowWindowAction( AbstractWindow w )
    {
        super( null, null );
        this.w	= w;

        l = new AbstractWindow.Adapter() {
            public void windowActivated( AbstractWindow.Event e )
            {
                if( !disposed ) ((BasicApplication) AbstractApplication.getApplication()).getMenuFactory().setSelectedWindow( ShowWindowAction.this );
            }
        };
        w.addListener( l );
    }

    public void actionPerformed( ActionEvent e )
    {
        w.setVisible( true );
        w.toFront();
    }

    public void dispose()
    {
        disposed = true;	// the listener might still be called!
        w.removeListener( l );
    }
}