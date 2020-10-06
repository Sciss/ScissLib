/*
 *  LooseFocusAction.java
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
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
 * 	@version	0.1, 03-Oct-07
 *	@author		Hanns Holger Rutz
 */
public class LooseFocusAction
extends AbstractAction
{
    private final JComponent c;

    public LooseFocusAction( JComponent c )
    {
        super();
        this.c	= c;
        c.getInputMap().put( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ), "lost" );
        c.getActionMap().put( "lost", this );
    }

    public void actionPerformed( ActionEvent e )
    {
        final JRootPane rp = SwingUtilities.getRootPane( c );
        if( rp != null ) rp.requestFocus();
    }
}