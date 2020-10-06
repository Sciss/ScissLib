/*
 *  MenuCheckItem.java
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

import java.util.Iterator;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;

/**
 *  @author		Hanns Holger Rutz
 *  @version	0.70, 30-Aug-06
 */
public class MenuCheckItem
extends MenuItem
{
    private boolean checked = false;

    public MenuCheckItem( String id, Action a )
    {
        super( id, a );
    }

    public void setSelected( boolean b )
    {
        Realized r;

        for( Iterator iter = mapRealized.values().iterator(); iter.hasNext(); ) {
            r = (Realized) iter.next();
            ((AbstractButton) r.c).setSelected( b );
        }

        checked = b;
    }

    public boolean isSelected()
    {
        return checked;
    }

    protected JComponent createComponent( Action a )
    {
        final JCheckBoxMenuItem cmi = new JCheckBoxMenuItem( a );
        if( checked ) cmi.setSelected( true );
        return cmi;
    }
}