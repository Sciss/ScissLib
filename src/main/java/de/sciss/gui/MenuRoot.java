/*
 *  MenuRoot.java
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

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenuBar;

import de.sciss.app.AbstractWindow;

/**
 *  @author		Hanns Holger Rutz
 *  @version	0.70, 30-Aug-06
 */
public class MenuRoot
extends MenuGroup
{
    public MenuRoot()
    {
        super( "root", (Action) null );
    }

    public JMenuBar createBar( AbstractWindow w )
    {
        return (JMenuBar) create( w );
    }

    protected JComponent createComponent( Action a )
    {
        return new JMenuBar();
    }
}
