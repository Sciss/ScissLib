/*
 *  KeyedAction.java
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
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;

/**
 *	A special <code>Action</code> class
 *	whose constructor requires an accelerator key.
 *	When the action listener is invoked, the
 *	current keyboard focus component is queried.
 *	If a text editing component has the focus,
 *	the action is silently aborted. Otherwise
 *	the abstract method <code>validActionPerformed</code>
 *	is called. This method must be overriden by subclasses.
 *	This allows global keyboard commands without modifier keys to be installed
 *	and they won't interfer when the user edits text fields.
 *
 *  @author		Hanns Holger Rutz
 *  @version	0.37, 25-Feb-08
 */
public abstract class KeyedAction
extends AbstractAction
{
    private boolean ignoreFocus;

//	private static final int[] IF_CODES = { KeyEvent.VK_ACCEPT, KeyEvent.VK_CANCEL, KeyEvent.VK_ESCAPE };

    public KeyedAction( KeyStroke stroke )
    {
        super();
        putValue( ACCELERATOR_KEY, stroke );

        ignoreFocus = (stroke == null) ||
                      ((stroke.getModifiers() & (InputEvent.META_DOWN_MASK | InputEvent.CTRL_DOWN_MASK)) != 0);
//		if( !ignoreFocus ) {
//			
//		}
    }

    public void setIgnoreFocus( boolean ignore )
    {
        ignoreFocus	= ignore;
    }

    public boolean getIgnoreFocus()
    {
        return ignoreFocus;
    }

    public KeyStroke getStroke()
    {
        return( (KeyStroke) getValue( ACCELERATOR_KEY ));
    }

    public final void actionPerformed( ActionEvent e )
    {
        if( !ignoreFocus ) {
            final Component c = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            if( (c != null) && (c instanceof JTextComponent) && ((JTextComponent) c).isEditable() ) return;
        }
        validActionPerformed( e );
    }

    protected abstract void validActionPerformed( ActionEvent e );
}
