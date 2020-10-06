/*
 *  MenuGroup.java
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;

import de.sciss.app.AbstractWindow;

/**
 *  @author		Hanns Holger Rutz
 *  @version	0.71, 09-Aug-09
 */
public class MenuGroup
extends MenuItem // implements MenuNode
{
    protected final Map			proxies			= new HashMap();
    protected final NodeProxy	defaultProxy	= new NodeProxy( null );

    public MenuGroup( String id, Action a )
    {
        super( id, a );
    }

    public MenuGroup( String id, String text )
    {
        this( id, new DummyAction( text ));
    }

    public MenuNode get( String id )
    {
        return get( defaultProxy, id );
    }

    public MenuNode get( AbstractWindow w, String id )
    {
        return get( getProxy( w, false ), id );
    }

    private NodeProxy getProxy( AbstractWindow w, boolean create )
    {
        if( w == null ) return defaultProxy;
        NodeProxy p = (NodeProxy) proxies.get( w );
        if( p == null && create ) {
            p = new NodeProxy( w );
            proxies.put( w, p );
        }
        return p;
    }

    private MenuNode get( NodeProxy p, String id )
    {
        final int i	= id.indexOf( '.' );

        if( i == -1 ) {
            return (MenuNode) p.mapElements.get( id );
        } else {
            final MenuGroup mg = (MenuGroup) p.mapElements.get( id.substring( 0, i ));
            if( mg == null ) throw new NullPointerException( id );
            return mg.get( p.w, id.substring( i + 1 ));
        }
    }


    public int indexOf( String id )
    {
        return indexOf( defaultProxy, id );
    }

    public int indexOf( AbstractWindow w, String id )
    {
        return indexOf( getProxy( w, false ), id );
    }

    private int indexOf( NodeProxy p, String id )
    {
        final int i	= id.indexOf( '.' );

        if( i == -1 ) {
            return p.collElements.indexOf( p.mapElements.get( id ));
        } else {
            final MenuGroup mg = (MenuGroup) p.mapElements.get( id.substring( 0, i ));
            if( mg == null ) throw new NullPointerException( id );
            return mg.indexOf( id.substring( i + 1 ));
        }
    }

    public MenuNode getByAction( Action a )
    {
        return getByAction( defaultProxy, a );
    }

    public MenuNode getByAction( AbstractWindow w, Action a )
    {
        return getByAction( getProxy( w, false ), a );
    }

    private MenuNode getByAction( NodeProxy p, Action a )
    {
        MenuNode n;

        for( Iterator iter = p.collElements.iterator(); iter.hasNext(); ) {
            n = (MenuNode) iter.next();
            if( n.getAction() == a ) return n;
        }

        return null;
    }

    // adds window specific action to the tail
    public void add( AbstractWindow w, MenuNode n )
    {
        add( getProxy( w, true ), n );
    }

    // adds to the tail
    public void add( MenuNode n )
    {
        add( defaultProxy, n );
    }

    // inserts at given index
    public void add( MenuNode n, int index )
    {
        add( defaultProxy, n, index );
    }

    // inserts at given index
    public void add( AbstractWindow w, MenuNode n, int index )
    {
        add( getProxy( w, true ), n, index );
    }

    // inserts at given index
    private void add( NodeProxy p, MenuNode n )
    {
        add( p, n, p.collElements.size() );
    }

    public int size()
    {
        return defaultProxy.size();
    }

    public MenuNode get( int idx )
    {
        return (MenuNode) defaultProxy.collElements.get( idx );
    }

    // inserts at given index
    private void add( NodeProxy p, MenuNode n, int index )
    {
        if( p.mapElements.put( n.getID(), n ) != null ) throw new IllegalArgumentException( "Element already added : " + n );

        Realized r;
        final boolean isDefault = p.w == null;

        p.collElements.add( index, n );

        for( Iterator iter = mapRealized.values().iterator(); iter.hasNext(); ) {
            r = (Realized) iter.next();
            if( isDefault || (p.w == r.w) ) {
                r.c.add( n.create( r.w ), index + (isDefault ? 0 : defaultProxy.size()) );
            }
        }
    }

//	private Component createInvis()
//	{
//		final Component invis = new JMenuItem();
//		invis.setVisible( false );
//		return invis;
//	}

    public void addSeparator( AbstractWindow w )
    {
        add( w, new MenuSeparator() );
    }

    public void addSeparator()
    {
        addSeparator( null );
    }

    public void remove( int index )
    {
        remove( defaultProxy, index );
    }

    public void remove( AbstractWindow w, int index )
    {
        remove( getProxy( w, false ), index );
    }

    private void remove( NodeProxy p, int index )
    {
        final MenuNode	n = (MenuNode) p.collElements.remove( index );
        Realized		r;

        p.mapElements.remove( n.getID() );
        final boolean isDefault = p.w == null;

        for( Iterator iter = mapRealized.values().iterator(); iter.hasNext(); ) {
            r = (Realized) iter.next();
            if( isDefault || (p.w == r.w) ) {
                r.c.remove( index + (isDefault ? 0 : defaultProxy.size()) );
                n.destroy( r.w );
            }
        }
    }

    public void remove( MenuNode n )
    {
        remove( defaultProxy, n );
    }

    public void remove( AbstractWindow w, MenuNode n )
    {
        remove( getProxy( w, false ), n );
    }

    private void remove( NodeProxy p, MenuNode n )
    {
        for( int idx = 0; idx < p.collElements.size(); idx++ ) {
            if( (MenuNode) p.collElements.get( idx ) == n ) {
                remove( p, idx );
                return;
            }
        }
    }

    public JComponent create( AbstractWindow w )
    {
        final JComponent c = super.create( w );
        defaultProxy.create( c, w );
        final NodeProxy p = getProxy( w, false );
        if( p != null ) p.create( c, w );
        return c;
    }

    public void destroy( AbstractWindow w )
    {
        super.destroy( w );
        defaultProxy.destroy( w );
        final NodeProxy p = getProxy( w, false );
        if( p != null ) {
            p.destroy( w );
            if( p.isEmpty() ) {
                proxies.remove( w );
            }
        }
    }

    protected JComponent createComponent( Action a )
    {
        return new JMenu( a );
    }

    public void putMimic( String id, AbstractWindow w, Action a )
    {
        if( a == null ) return;
        final MenuItem mi = (MenuItem) get( id );
        if( mi == null ) throw new NullPointerException( id );

        final Action src = mi.getAction();
        a.putValue( Action.NAME, src.getValue( Action.NAME ));
        a.putValue( Action.SMALL_ICON, src.getValue( Action.SMALL_ICON ));
        a.putValue( Action.ACCELERATOR_KEY, src.getValue( Action.ACCELERATOR_KEY ));
        putNoNullNull( src, a, Action.MNEMONIC_KEY );
//		a.putValue( Action.MNEMONIC_KEY, src.getValue( Action.MNEMONIC_KEY ));
        a.putValue( Action.SHORT_DESCRIPTION, src.getValue( Action.SHORT_DESCRIPTION ));
        a.putValue( Action.LONG_DESCRIPTION, src.getValue( Action.LONG_DESCRIPTION ));

        mi.put( w, a );
    }

    // due to bug in java 1.5 JMenuItem
    private void putNoNullNull( Action src, Action dst, String key )
    {
        final Object srcVal = src.getValue( key );
        final Object dstVal	= dst.getValue( key );
        if( (srcVal == null) && (dstVal == null) ) return;
        dst.putValue(  key, srcVal );
    }

//	public void put( String id, AbstractWindow w, Action a )
//	{
//		final MenuItem mi = (MenuItem) get( id );
//		if( mi == null ) throw new NullPointerException( id );
//		mi.put( w, a );
//	}

    private static class NodeProxy
    {
        protected final AbstractWindow	w;
        protected final List	collElements	= new ArrayList();
        protected final Map		mapElements		= new HashMap();	// key = (String) id, value = (NodeProxy) element

        protected NodeProxy( AbstractWindow w )
        {
            this.w	= w;
        }

        protected int size()
        {
            return collElements.size();
        }

        protected boolean isEmpty()
        {
            return collElements.isEmpty();
        }

        protected void create( JComponent c, AbstractWindow w2 )
        {
            if( (w != null) && (w != w2) ) throw new IllegalArgumentException();

            for( Iterator iter = collElements.iterator(); iter.hasNext(); ) {
                c.add( ((MenuNode) iter.next()).create( w2 ));
            }
        }

        protected void destroy( AbstractWindow w2 )
        {
            if( (w != null) && (w != w2) ) throw new IllegalArgumentException();

            for( Iterator iter = collElements.iterator(); iter.hasNext(); ) {
                ((MenuNode) iter.next()).destroy( w2 );
            }
        }
    }
}