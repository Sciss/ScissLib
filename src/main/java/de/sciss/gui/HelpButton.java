/*
 *  HelpButton.java
 *  (ScissLib)
 *
 *  Copyright (c) 2004-2016 Hanns Holger Rutz. All rights reserved.
 *
 *	This library is free software; you can redistribute it and/or
 *	modify it under the terms of the GNU Lesser General Public
 *	License as published by the Free Software Foundation; either
 *	version 2.1 of the License, or (at your option) any later version.
 *
 *	This library is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *	Lesser General Public License for more details.
 *
 *	You should have received a copy of the GNU Lesser General Public
 *	License along with this library; if not, write to the Free Software
 *	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *
 *	For further information, please contact Hanns Holger Rutz at
 *	contact@sciss.de
 */

package de.sciss.gui;

import de.sciss.app.DynamicAncestorAdapter;
import de.sciss.app.DynamicListening;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

/**
 *	An Aqua-LnF online help button. This
 *	component requires the image file <code>&quot;images/helpbutton.png&quot;</code>.
 *
 *  @author		Hanns Holger Rutz
 *  @version	0.70, 14-Apr-06
 */
public class HelpButton
extends JButton
implements DynamicListening, ActionListener
{
    private final Icon icnNormal, icnPressed, icnUnfocussed;

    private static TiledImage	imgHelpIcons	= null;

    private Window	win			= null;
    private String	file		= null;
    private URL     url		    = null;

    private final WindowAdapter	winListener;

    public HelpButton(String file) {
        this();
        setHelpFile(file);
    }

    public HelpButton(URL url) {
        this();
        setHelpFileURL(url);
    }

    public HelpButton()
    {
        super();

        if( imgHelpIcons == null ) {
            imgHelpIcons = new TiledImage( getClass().getResource( "helpbutton.png" ), 24, 24 );
        }
        icnNormal		= imgHelpIcons.createIcon( 0, 0 );
        icnPressed		= imgHelpIcons.createIcon( 0, 1 );
        icnUnfocussed	= imgHelpIcons.createIcon( 0, 2 );

//		setBorderPainted( false );
        setBorder( BorderFactory.createEmptyBorder( 2, 2, 2, 2 ));
        setMargin( new Insets( 2, 2, 2, 2 ));
        GUIUtil.constrainSize( this, 28, 28 );
        setContentAreaFilled( false );
        setFocusable( false );

        new DynamicAncestorAdapter( this ).addTo( this );

        addActionListener( this );

        winListener = new WindowAdapter() {
            public void windowActivated( WindowEvent e )
            {
                repaint();
            }

            public void windowDeactivated( WindowEvent e )
            {
                repaint();
            }
        };
    }

    public void setHelpFile(String file) {
        this.file = file;
    }

    public void setHelpFileURL(URL url) {
        this.url = url;
    }

    public String getHelpFile()
    {
        return file;
    }

    public URL getHelpFileURL() {
        return url;
    }

    public void actionPerformed(ActionEvent e) {
        if (file != null) {
            HelpFrame.openViewerAndLoadHelpFile(file);
        } else if (url != null) {
            HelpFrame.openViewerAndLoadHelpFile(url);
        }
    }

    public void startListening()
    {
        win = SwingUtilities.getWindowAncestor( this );
        if( win != null ) {
            win.addWindowListener( winListener );
        }
    }

    public void stopListening()
    {
        if( win != null ) {
            win.removeWindowListener( winListener );
        }
        win = null;
    }

    // overriden without calling super
    // to avoid lnf border painting
    // which is happening despite setting our own border
    public void paintComponent( Graphics g )
    {
        final Icon		icn;
        final Window	w	= SwingUtilities.getWindowAncestor( this );

        if( (w != null) && w.isActive() ) {
            if( getModel().isPressed() ) {
                icn = icnPressed;
            } else {
                icn = icnNormal;
            }
        } else {
            icn = icnUnfocussed;
        }
        icn.paintIcon( this, g, ((getWidth() - 24) >> 1) + 2, ((getHeight() - 24) >> 1) + 2 );
    }
}
