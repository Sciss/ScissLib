/*
 *  SmartJFrame.java
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

package de.sciss.common;

import java.awt.GraphicsConfiguration;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JRootPane;

/**
 *  @author		Hanns Holger Rutz
 *  @version	0.73, 01-Feb-09
 */
public class SmartJFrame
extends JFrame
{
    private final boolean		screenMenuBar;
    private ContainerListener	barListener	= null;
    private JMenuBar			savedBar	= null;

    public SmartJFrame( boolean screenMenuBar )
    {
        super();
        this.screenMenuBar = screenMenuBar;
    }

    public SmartJFrame( GraphicsConfiguration gc, boolean screenMenuBar )
    {
        super( gc );
        this.screenMenuBar = screenMenuBar;
    }

    public SmartJFrame( String title, boolean screenMenuBar )
    {
        super( title );
        this.screenMenuBar = screenMenuBar;
    }

    public SmartJFrame( String title, GraphicsConfiguration gc, boolean screenMenuBar )
    {
        super( title, gc );
        this.screenMenuBar = screenMenuBar;
    }

    public void setJMenuBar( JMenuBar m )
    {
        if( screenMenuBar ) {
            super.setJMenuBar( m );
            return;
        }

        if( m != null ) {
            if( barListener == null ) {
                barListener = new ContainerListener() {
                    public void componentAdded( ContainerEvent e )
                    {
                        checkMenuBar( e );
                    }

                    public void componentRemoved( ContainerEvent e )
                    {
                        checkMenuBar( e );
                    }
                };
            } else if( savedBar != null ) {
                savedBar.removeContainerListener( barListener );
            }
            savedBar = m;
            if( m.getMenuCount() > 0 ) super.setJMenuBar( m );
            m.addContainerListener( barListener );

        } else {
            if( savedBar != null ) {
                savedBar.removeContainerListener( barListener );
                savedBar = null;
            }
            super.setJMenuBar( null );
        }
    }

    protected void checkMenuBar( ContainerEvent e )
    {
        final JMenuBar mb = (JMenuBar) e.getContainer();
        if( mb.getMenuCount() == 0 ) {
            if( getJMenuBar() == mb ) {
                super.setJMenuBar( null );
            }
        } else {
            if( getJMenuBar() == null ) {
                super.setJMenuBar( mb );
            }
        }
        final JRootPane rp = getRootPane();
        rp.revalidate();
        rp.repaint();
    }
}
