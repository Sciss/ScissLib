/*
 *  AboutBox.java
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

import de.sciss.app.AbstractApplication;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;

/**
 *  About, Copyright + Credits Frame
 *
 *  @author		Hanns Holger Rutz
 *  @version	0.32, 29-Jan-09
 */
public class AboutBox
        extends AbstractAboutFrame
        implements HyperlinkListener {
    /**
     *  Value for add/getComponent(): the about box
     */
    public static final Object					COMP_ABOUTBOX	= AboutBox.class.getName();

    private static final String CREDITS_START   =
        "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\" "+
        "\"http://www.w3.org/TR/REC-html40/loose.dtd\">"+
        "<html><head><style type=\"text/css\"><!--\n"+
        "p { font-family:\"Lucida Grande\" Helvetica sans-serif;font-size:"+
        "10pt;padding:2pt 0 2pt 0;margin:0; }\n"+
        "--></style></head><body>";
    private static final String CREDITS_END		= "</body></html>";

    public AboutBox()
    {
        super( AbstractApplication.getApplication().getName(),
               String.valueOf( AbstractApplication.getApplication().getVersion() ));

        final StringBuffer				credits	= new StringBuffer( CREDITS_START );
        final de.sciss.app.Application	app		= AbstractApplication.getApplication();
        final URL						imgURL	= app.getClass().getResource( "application.png" );
//		final char						sep		= File.separatorChar;

        if( imgURL != null ) setApplicationIcon( new ImageIcon( imgURL ));
//		setApplicationIcon( new ImageIcon( "images" + File.separator + "application.png" ));
        setCopyright( app.getResourceString( "copyright" ));
        setHyperlinkListener( this );
        credits.append( app.getResourceString( "credits" ));
        credits.append( "<P>Java " );
        credits.append( System.getProperty( "java.version" ));
        credits.append( "</P>" );
        credits.append( CREDITS_END );
        setCredits( credits.toString(), "text/html" );
//System.err.println( credits.toString() );

//		pack();

        app.addComponent( COMP_ABOUTBOX, this );
    }

    public void dispose()
    {
        AbstractApplication.getApplication().removeComponent( COMP_ABOUTBOX );
        super.dispose();
    }

    public void setBuildVersion( File f )
    {
        final long build   = f.lastModified();

        if( build != 0L ) {
            setBuildVersion( DateFormat.getDateInstance( DateFormat.SHORT ).format( new Date( build )));
        }
    }

    // --------- HyperlinkListener interface ---------

    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            try {
                final URI uri = e.getURL().toURI();
                Desktop.getDesktop().browse(uri);
            } catch (Exception e1) {
                GUIUtil.displayError(this, e1, this.getTitle());
            }
        }
    }
}