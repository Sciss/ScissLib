/*
 *  MenuRadioGroup.java
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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;

import de.sciss.app.AbstractWindow;

/**
 *  @author		Hanns Holger Rutz
 *  @version	0.70, 30-Aug-06
 */
public class MenuRadioGroup
{
    private final Map mapGroups	= new HashMap();	// key = AbstractWindow, value = ButtonGroup
    private int selected = -1;

    public MenuRadioGroup()
    {
        /* empty */
    }

    protected void add( AbstractWindow w, AbstractButton b )
    {
        ButtonGroup g = (ButtonGroup) mapGroups.get( w );

        if( g == null ) {
            g = new ButtonGroup();
            mapGroups.put( w, g );
        }
        g.add( b );
        if( g.getButtonCount() == (selected + 1) ) g.setSelected( b.getModel(), true );
    }

    public void setSelected( int index )
    {
        ButtonGroup		g;
        Enumeration		buttons;
        AbstractButton	b;

        for( Iterator iter = mapGroups.values().iterator(); iter.hasNext(); ) {
            g		= (ButtonGroup) iter.next();
            buttons = g.getElements();
            b		= null;
            for( int i = 0; (i <= index) && buttons.hasMoreElements(); i++ ) {
                b	= (AbstractButton) buttons.nextElement();
            }
//			if( b != null ) b.setSelected( true );
            if( b != null ) g.setSelected( b.getModel(), true );
//			for( int i = 0; buttons.hasMoreElements(); i++ ) {
//				b	= (AbstractButton) buttons.nextElement();
//System.err.println( "index : " + i + "; b.getText() = "+b.getText() );
//				g.setSelected( b.getModel(), i == index );
//			}
        }

        selected = index;
    }

    protected void remove( AbstractWindow w, AbstractButton b )
    {
        final ButtonGroup g = (ButtonGroup) mapGroups.get( w );

        if( g == null ) throw new IllegalArgumentException( "Element was not found : " + w );

        g.remove( b );
        if( g.getButtonCount() == 0 ) {
            mapGroups.remove( w );
        }
    }
}