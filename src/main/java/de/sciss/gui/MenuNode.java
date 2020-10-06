/*
 *  MenuNode.java
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

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import de.sciss.app.AbstractWindow;

/**
 *  @author		Hanns Holger Rutz
 *  @version	0.70, 30-Aug-06
 */
public interface MenuNode
{
    public static final String	IDENTIFIER	= "de.sciss.gui.Identifier";	// Action key (expected value: a String)

    public JComponent create( AbstractWindow w );
    public void destroy( AbstractWindow w );
    public String getID();
    public void setEnabled( boolean b );
    public Action getAction();

    // ----------------- internal classes -----------------
    public static class DummyAction
    extends AbstractAction
    {
        protected DummyAction( String text )
        {
            super( text );
//			putValue( IDENTIFIER, id );
        }

        protected DummyAction( String text, KeyStroke stroke )
        {
            this( text );
            if( stroke != null ) putValue( ACCELERATOR_KEY, stroke );
        }

        public void actionPerformed( ActionEvent e ) { /* ignore */ }
    }
}