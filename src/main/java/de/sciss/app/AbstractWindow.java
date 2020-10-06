/*
 *  AbstractWindow.java
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

package de.sciss.app;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.WindowEvent;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JMenuBar;
import javax.swing.event.InternalFrameEvent;

/**
 *	Interface that unites functionality
 *	from inhomogeneous classes such as JFrame, JDialog, JInternalFrame
 *
 *	@version	0.12, 13-Feb-08
 *	@author		Hanns Holger Rutz
 */
public interface AbstractWindow
{
    /**
     *	Window type: regular full-fledged window
     */
    public static final int REGULAR		= 0;
    /**
     *	Window type: supplementary window
     *	which for example might not need menubar
     */
    public static final int SUPPORT		= 1;
    /**
     *	Window type: supplementary window
     *	which is a (possibly floating) palette window
     */
    public static final int PALETTE		= 2;

    public static final int TYPES_MASK	= 0x0F;

    public void init();
    public Container getContentPane();
    public void setContentPane( Container c );
    public void setTitle( String title );
    public String getTitle();
    public Component getWindow();
    public void pack();
    public void setDefaultCloseOperation( int mode );
    public void dispose();
    public void setVisible( boolean b );
    public boolean isVisible();
    public void toFront();
    public boolean isFloating();
    public void addListener( Listener l );
    public void removeListener( Listener l );
    public boolean isActive();
    public Dimension getSize();
    public Rectangle getBounds();
    public Point getLocation();
    public void setBounds( Rectangle r );
    public void setSize( Dimension d );
    public void setLocation( Point p );
//	public void setPreferredSize( Dimension d );
    public Insets getInsets();
    public void setJMenuBar( JMenuBar m );
    public JMenuBar getJMenuBar();
    public InputMap getInputMap( int condition );
    public ActionMap getActionMap();
    public Window[] getOwnedWindows();
    public void setFocusTraversalKeysEnabled( boolean enabled );
    public void setDirty( boolean dirty );
    public void setLocationRelativeTo( Component c );
    public void setResizable( boolean b );
    public void setUndecorated( boolean b );
    public boolean isResizable();

    public void revalidate();

    public static interface Listener
    {
        public void windowOpened( Event e );
        public void windowClosing( Event e );
        public void windowClosed( Event e );
        public void windowIconified( Event e );
        public void windowDeiconified( Event e );
        public void windowActivated( Event e );
        public void windowDeactivated( Event e );
//		public void windowGainedFocus( Event e );
//		public void windowLostFocus( Event e );
    }

    public static class Adapter
    implements Listener
    {
        public void windowOpened( Event e ) { /* empty */ }
        public void windowClosing( Event e ) { /* empty */ }
        public void windowClosed( Event e ) { /* empty */ }
        public void windowIconified( Event e ) { /* empty */ }
        public void windowDeiconified( Event e ) { /* empty */ }
        public void windowActivated( Event e ) { /* empty */ }
        public void windowDeactivated( Event e ) { /* empty */ }
//		public void windowGainedFocus( Event e ) { /* empty */ }
//		public void windowLostFocus( Event e ) { /* empty */ }
    }

    public static class Event
    extends java.awt.AWTEvent
    {
        public static int	WINDOW_FIRST		= WindowEvent.WINDOW_FIRST;
        public static int	WINDOW_OPENED		= WindowEvent.WINDOW_OPENED;
        public static int	WINDOW_CLOSING		= WindowEvent.WINDOW_CLOSING;
        public static int	WINDOW_CLOSED		= WindowEvent.WINDOW_CLOSED;
        public static int	WINDOW_ICONIFIED	= WindowEvent.WINDOW_ICONIFIED;
        public static int	WINDOW_DEICONIFIED	= WindowEvent.WINDOW_DEICONIFIED;
        public static int	WINDOW_ACTIVATED	= WindowEvent.WINDOW_ACTIVATED;
        public static int	WINDOW_DEACTIVATED	= WindowEvent.WINDOW_DEACTIVATED;
//		public static int	WINDOW_GAINED_FOCUS	= WindowEvent.WINDOW_GAINED_FOCUS;
//		public static int	WINDOW_LOST_FOCUS	= WindowEvent.WINDOW_LOST_FOCUS;

        private final AbstractWindow w;

        public Event( AbstractWindow source, int id )
        {
            super( source, id );

            w = source;
        }

        public AbstractWindow getWindow()
        {
            return w;
        }

        public static Event convert( AbstractWindow w, WindowEvent e )
        {
            return new Event( w, e.getID() );
        }

        public static Event convert( AbstractWindow w, InternalFrameEvent e )
        {
            return new Event( w, e.getID() - InternalFrameEvent.INTERNAL_FRAME_FIRST + WINDOW_FIRST );
        }
    }
}