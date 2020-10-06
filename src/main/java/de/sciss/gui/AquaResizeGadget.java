/*
 *  AquaResizeGadget.java
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

public class AquaResizeGadget
extends JComponent
{
    private static final Dimension	d	= new Dimension( 16, 16 );
    private static final Color		c	= new Color( 0x88, 0x88, 0x88 );

    public AquaResizeGadget()
    {
        super();

        setSize( d );
        setPreferredSize( d );
        setMinimumSize( d );
        setMaximumSize( d );
        setForeground( c );

        final MouseInputAdapter mia = new MouseInputAdapter() {
            Point		initialMouse = null;
            Dimension	initialSize;

            public void mousePressed( MouseEvent e )
            {
                final Window win = SwingUtilities.getWindowAncestor( e.getComponent() );

                if( win != null ) {
                    initialMouse	= e.getPoint();
                    SwingUtilities.convertPointToScreen( initialMouse, e.getComponent() );
                    initialSize		= win.getSize();
                }
            }

            public void mouseReleased( MouseEvent e )
            {
                initialMouse	= null;
            }

            public void mouseDragged( MouseEvent e )
            {
                final Window win = SwingUtilities.getWindowAncestor( e.getComponent() );

                if( (initialMouse != null) && (win != null) ) {
                    final Point currentMouse = e.getPoint();
                    SwingUtilities.convertPointToScreen( currentMouse, e.getComponent() );
//					win.setLocation( initialLoc.x + currentMouse.x - initialMouse.x,
//									 initialLoc.y + currentMouse.y - initialMouse.y );
                    win.setSize( Math.max( 32, initialSize.width + currentMouse.x - initialMouse.x ),
                                 Math.max( 32, initialSize.height + currentMouse.y - initialMouse.y ));
                }
            }

            public void mouseMoved( MouseEvent e )
            {
                mouseDragged( e );
            }
        };

        this.addMouseListener( mia );
        this.addMouseMotionListener( mia );
    }

    public void paintComponent( Graphics g )
    {
        super.paintComponent( g );

        final Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g2.drawLine( 4, 13, 13, 4 );
        g2.drawLine( 8, 13, 13, 8 );
        g2.drawLine( 12, 13, 13, 12 );
    }
}
