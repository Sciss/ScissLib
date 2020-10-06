/*
 *  ComponentHost.java
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

import de.sciss.util.Disposable;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ComponentHost
        extends JComponent
        implements Disposable {

    // --- top painter ---

    private Image					limited			= null;
    private boolean					imageUpdate		= false;
    private boolean					imageUpdateC	= false;
    private int						recentWidth, recentHeight;

    private final List				collTopPainters	= new ArrayList();

    private static Paint			pntBackgroundDark;
    private static Paint			pntBackgroundLight;

    private static final int[]		pntBgAquaPixels = {
            0xFFF0F0F0, 0xFFF0F0F0, 0xFFF0F0F0, 0xFFF0F0F0,
            0xFFF0F0F0, 0xFFF0F0F0, 0xFFF0F0F0, 0xFFF0F0F0,
            0xFFECECEC, 0xFFECECEC, 0xFFECECEC, 0xFFECECEC,
            0xFFECECEC, 0xFFECECEC, 0xFFECECEC, 0xFFECECEC
    };

    private static final int[]		pntBgDarkPixels = {
            0xFF0F0F0F, 0xFF0F0F0F, 0xFF0F0F0F, 0xFF0F0F0F,
            0xFF0F0F0F, 0xFF0F0F0F, 0xFF0F0F0F, 0xFF0F0F0F,
            0xFF131313, 0xFF131313, 0xFF131313, 0xFF131313,
            0xFF131313, 0xFF131313, 0xFF131313, 0xFF131313
    };

    private final Rectangle			updateRect		= new Rectangle();

    private final Paint pntBackground;
    private final Object sync = new Object();

    static {
        final BufferedImage imgDark = new BufferedImage( 4, 4, BufferedImage.TYPE_INT_ARGB );
        imgDark.setRGB( 0, 0, 4, 4, pntBgDarkPixels, 0, 4 );
        pntBackgroundDark = new TexturePaint( imgDark, new Rectangle( 0, 0, 4, 4 ));

        final BufferedImage imgLight = new BufferedImage( 4, 4, BufferedImage.TYPE_INT_ARGB );
        imgLight.setRGB( 0, 0, 4, 4, pntBgAquaPixels, 0, 4 );
        pntBackgroundLight = new TexturePaint( imgLight, new Rectangle( 0, 0, 4, 4 ));
    }

    public ComponentHost() {
        super();

        final boolean isDark = UIManager.getBoolean("dark-skin");
        pntBackground = isDark ? pntBackgroundDark : pntBackgroundLight;

        setOpaque(true);
        setDoubleBuffered(false);
    }

    public void update(Component c) {
        synchronized (sync) {
            final Rectangle r = c.getBounds();
            if (updateRect.isEmpty()) {
                updateRect.setBounds(r);
            } else {
                updateRect.setBounds(updateRect.union(r));
            }
            imageUpdate = true;
//System.err.println( "Repaint "+r.x+", "+r.y+", "+r.width+", "+r.height );
            repaint(r);
        }
    }

    public void update(Rectangle r) {
        synchronized (sync) {
            if (updateRect.isEmpty()) {
                updateRect.setBounds(r);
            } else {
                updateRect.setBounds(updateRect.union(r));
            }
            imageUpdate = true;
            repaint(r);
        }
    }

    public void updateAll() {
        synchronized (sync) {
            updateRect.setBounds(0, 0, getWidth(), getHeight());
            imageUpdate = true;
            repaint();
        }
    }

    private void redrawImage() {
        if (limited == null) return;

        final Graphics2D		g2			= (Graphics2D) limited.getGraphics();
        final Shape				clipOrig	= g2.getClip();
        final AffineTransform	atOrig		= g2.getTransform();
//		LightInfo				li;
        Component				c;
        Rectangle				r;

        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        synchronized( sync ) {
            if( imageUpdateC ) {
                updateRect.setBounds( 0, 0, recentWidth, recentHeight );
                imageUpdateC	= false;
            }
//			for( int i = 0; i < collLights.size(); i++ ) {
            for( int i = 0; i < getComponentCount(); i++ ) {
//				li	= (LightInfo) collLights.get( i );
                c	= getComponent( i );
                r	= c.getBounds();
                if( r.intersects( updateRect )) {
                    if( !c.isOpaque() ) {
                        g2.setPaint( pntBackground );
//						g2.fillRect( li.r.x, li.r.y, li.r.width, li.r.height );
                        g2.fillRect( r.x, r.y, r.width, r.height );
                    }
//					g2.clipRect( li.r.x, li.r.y, li.r.width, li.r.height );
                    g2.clipRect( r.x, r.y, r.width, r.height );
//					g2.translate( li.r.x, li.r.y );
                    g2.translate( r.x, r.y );
//					li.c.paint( this, g2 );
                    c.paint( g2 );
                    g2.setClip( clipOrig );
                    g2.setTransform( atOrig );
                }
            }
            updateRect.setBounds( 0, 0, 0, 0 );
            imageUpdate		= false;
        }
        g2.dispose();
    }

    public void dispose() {
        Component c;

        flushImage();
        synchronized (sync) {
            for (int i = 0; i < getComponentCount(); i++) {
                c = getComponent(i);
                if (c instanceof Disposable) {
                    ((Disposable) c).dispose();
                }
            }
            removeAll();
        }
    }

    private void flushImage() {
        if (limited != null) {
            limited.flush();
            limited = null;
        }
    }

    private void recreateImage() {
        flushImage();
        limited = createImage(recentWidth, recentHeight);
        imageUpdateC = true;
    }

    // no paintChildren() !
    public void paint(Graphics g) {
        paintComponent(g);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        final int width		= getWidth();
        final int height	= getHeight();

        if ((width != recentWidth) || (height != recentHeight)) {
            recentWidth = width;
            recentHeight = height;
            recreateImage();
        }

        if (imageUpdate || imageUpdateC) {
            redrawImage();
        }

        if (limited != null) {
            g.drawImage(limited, 0, 0, this);
        }

        // --- invoke top painters ---
        if (!collTopPainters.isEmpty()) {
            final Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            for (int i = 0; i < collTopPainters.size(); i++) {
                ((TopPainter) collTopPainters.get(i)).paintOnTop(g2);
            }
        }
    }

    /**
     *  Registers a new top painter.
     *  If the top painter wants to paint
     *  a specific portion of the surface,
     *  it must make an appropriate repaint call!
     *
     *  synchronization:	this method must be called in the event thread
     *
     *  @param  p   the painter to be added to the paint queue
     */
    public void addTopPainter(TopPainter p) {
        if (!collTopPainters.contains(p)) {
            collTopPainters.add(p);
        }
    }

    /**
     *  Removes a registered top painter.
     *
     *  synchronization:	this method must be called in the event thread
     *
     *  @param  p   the painter to be removed from the paint queue
     */
    public void removeTopPainter(TopPainter p) {
        collTopPainters.remove(p);
    }
}
