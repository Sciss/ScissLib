/*
 *  MenuSeparator.java
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
import javax.swing.JSeparator;

/**
 *  @author		Hanns Holger Rutz
 *  @version	0.70, 30-Aug-06
 */
public class MenuSeparator
extends MenuItem
{
    private static int uniqueID = 0;

    public MenuSeparator()
    {
        super( "_" + String.valueOf( uniqueID++ ), (Action) null );
    }

    public void setEnabled( boolean b ) { /* ignore */ }

    protected JComponent createComponent( Action a )
    {
        return new JSeparator();
    }
}
