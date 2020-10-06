/*
 *  MenuItem.java
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import de.sciss.app.AbstractWindow;

/**
 *  @author		Hanns Holger Rutz
 *  @version	0.71, 27-Jul-08
 */
public class MenuItem
implements MenuNode
{
    private final String	id;
    private final Action	action;
    private final Map		mapWindowActions	= new HashMap();	// key = AbstractWindow; value = Action
    protected final Map		mapRealized			= new HashMap();

    public MenuItem( String id, Action a )
    {
        this.id		= id;
        this.action	= a;
    }

    public MenuItem( String id, String text )
    {
        this( id, new DummyAction( text ));
        setEnabled( false );
    }

    public MenuItem( String id, String text, KeyStroke stroke )
    {
        this( id, new DummyAction( text, stroke ));
        setEnabled( false );
    }

    public void setEnabled( boolean b )
    {
        action.setEnabled( b );
    }

    public String getID()
    {
        return id;
    }

    public void put( AbstractWindow w, Action a )
    {
        if( mapWindowActions.put( w, a ) != null ) throw new IllegalArgumentException( "Window specific action has already been added" );
    }

    public void remove( AbstractWindow w )
    {
        if( mapWindowActions.remove( w ) == null ) throw new IllegalArgumentException( "Window specific action not found" );
    }

    public Action getAction()
    {
        return action;
    }

    public Action getAction( AbstractWindow w )
    {
        final Action wa = (Action) mapWindowActions.get( w );
        return( wa == null ? action : wa );
    }

    public JComponent create( AbstractWindow w )
    {
        final JComponent c = createComponent( getAction( w ));
        mapRealized.put( w, new Realized( w, c ));
        return c;
    }

    protected JComponent createComponent( Action a )
    {
        return new JMenuItem( a );
    }

    public void destroy( AbstractWindow w )
    {
        if( mapRealized.remove( w ) == null ) throw new IllegalArgumentException( "Element was not found : " + w );
        mapWindowActions.remove( w );
    }

    public Iterator getRealized()
    {
        return mapRealized.values().iterator();
    }

    // -------------------- internal classes --------------------

    protected static class Realized
    {
        public final AbstractWindow	w;
        public final JComponent		c;

        protected Realized( AbstractWindow w, JComponent c )
        {
            this.w	= w;
            this.c	= c;
        }
    }
}