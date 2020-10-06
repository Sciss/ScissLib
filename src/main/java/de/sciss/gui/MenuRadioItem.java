/*
 *  MenuRadioItem.java
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

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JRadioButtonMenuItem;

import de.sciss.app.AbstractWindow;

/**
 *  @author		Hanns Holger Rutz
 *  @version	0.70, 30-Aug-06
 */
public class MenuRadioItem
extends MenuCheckItem
{
    private final MenuRadioGroup g;

    public MenuRadioItem( MenuRadioGroup g, String id, Action a )
    {
        super( id, a );
        this.g	= g;
    }

    public MenuRadioGroup getRadioGroup()
    {
        return g;
    }

    public JComponent create( AbstractWindow w )
    {
        final JComponent	c	= super.create( w );
        g.add( w, (AbstractButton) c );
        return c;
    }

    public void destroy( AbstractWindow w )
    {
        final Realized r = (Realized) mapRealized.get( w );
        g.remove( w, (AbstractButton) r.c );

        super.destroy( w );
    }

    protected JComponent createComponent( Action a )
    {
        return new JRadioButtonMenuItem( a );
    }
}