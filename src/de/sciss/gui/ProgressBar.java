/*
 *  ProgressBar.java
 *  de.sciss.gui package
 *
 *  Copyright (c) 2004-2008 Hanns Holger Rutz. All rights reserved.
 *
 *	This software is free software; you can redistribute it and/or
 *	modify it under the terms of the GNU General Public License
 *	as published by the Free Software Foundation; either
 *	version 2, june 1991 of the License, or (at your option) any later version.
 *
 *	This software is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *	General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public
 *	License (gpl.txt) along with this software; if not, write to the Free Software
 *	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *
 *	For further information, please contact Hanns Holger Rutz at
 *	contact@sciss.de
 *
 *
 *  Changelog:
 *		31-Jul-04   commented
 *		26-May-05	moved from de.sciss.meloncillo.gui.ProgressBar
 */

package de.sciss.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

/**
 *  A bit more sophisticated progression bar
 *  than the default one provided by Swing,
 *  a slim version of the one used in FScape.
 *  This class provides methods for changing
 *  the bar's colour and text. If no text is
 *  set, the bar will try to estimate the
 *  remaining progression time.
 *
 *  @author		Hanns Holger Rutz
 *  @version	0.17, 12-Oct-06
 */
public class ProgressBar
extends JProgressBar
{
// -------- private Variablen --------

	protected float		p			= 0.0f;
//	private Color		progColor;

	private long		startTime;				// Zeitpunkt, an dem reset() aufgerufen wurde
	private long		pauseTime;
	private int			remain;
	private byte		timeStr[];
	protected int		maximum		= 0;

	private String altString = null;

	private static final Color  COLOR_SUCCESS   = new Color( 0x00, 0xFF, 0x00, 0x2F );
	private static final Color  COLOR_FAILURE	= new Color( 0xFF, 0x00, 0x00, 0x2F );

	private Paint pnt   = null;

// -------- public Methoden --------

	/**
	 *  Constructs a new horizontal
	 *  progression bar
	 */
	public ProgressBar()
	{
		super( SwingConstants.HORIZONTAL, 0, 0x1000 );
		
		timeStr			= "00:00:00".getBytes();

		reset();

		this.addComponentListener( new ComponentAdapter() {
			public void componentResized( ComponentEvent e1 )
			{
				Dimension d = getSize();
				maximum		= d.width;
				setMaximum( d.width );
				setValue( (int) (p * maximum) );
			}
		});
	}

	/**
	 *  Sets or clears the text that is displayed on top
	 *  of thes bar.
	 *
	 *  @param  altString   the text to display on the
	 *						bar or <code>null</code> to clear
	 *						the text. in this case the bar will
	 *						display a remaing-time string
	 */
	public void setText( String altString )
	{
		this.altString = altString;
		setString( altString );
		setStringPainted( altString != null );
	}
	
	/**
	 *	Sets the progression amount in percent.
	 *
	 *	@param	p	progression, between 0 and 1 (i.e. 0 and 100%)
	 *  @warning	use only this method to change the progression,
	 *				don't use the superclass's setValue method
	 */
	public synchronized void setProgression( float p )
	{
		int			oldProgWidth	= (int) (this.p * maximum);
		this.p						= Math.max( 0.0f, Math.min( 1.0f, p ));
		int			progWidth		= (int) (this.p * maximum);

		int	lastRemain				= remain;
		int	elapsed, hours, mins, secs;

		if( progWidth != oldProgWidth ) {

			setValue( progWidth );

			if( p > 0f ) {
				elapsed = (int) (System.currentTimeMillis() - startTime) / 1000;
				remain	= (int) (elapsed / p) - elapsed;
				if( (((elapsed >= 10) & (remain >= 10)) | isStringPainted()) & (altString == null) & !isIndeterminate() ) {
			
					// only update if changes are reasonable
					if( (remain < lastRemain) || ((remain - lastRemain) >= 10) ) {

						secs	= remain % 60;
						timeStr[ 6 ] = (byte) ((secs / 10) + 48);
						timeStr[ 7 ] = (byte) ((secs % 10) + 48);
						mins	= (remain / 60) % 60;
						timeStr[ 3 ] = (byte) ((mins / 10) + 48);
						timeStr[ 4 ] = (byte) ((mins % 10) + 48);
						hours	= (remain / 3600) % 100;
						timeStr[ 0 ] = (byte) ((hours / 10) + 48);
						timeStr[ 1 ] = (byte) ((hours % 10) + 48);
						setString( new String( timeStr ));
						setStringPainted( true );

					} else {
						remain	= lastRemain;				// vvv avoid flickering
					}
				}
			}				
		}
	}

	/**
	 *	Queries the progresion
	 *
	 *	@return	the progression value between 0 and 1 (i.e. 0 and 100%)
	 */
	public float getProgression()
	{
		return p;
	}

	/**
	 *	Reset progression to zero. Remove any
	 *  custom text strings and custom bar colour.
	 *  Initializes the remaining-time estimation 
	 *  with the current system time.
	 */
	public void reset()
	{
		setIndeterminate( false );
		setProgression( 0f );
		setPaint( null );
		setStringPainted( false );
		setText( null );
		remain		= 1 << 29;
		startTime	= System.currentTimeMillis();
	}
	
	/**
	 *	Pauses progression. You have to call
	 *  this if the user pauses a progress
	 *  because in this case the calculation
	 *  of the remaining time should be paused
	 *  as well.
	 */
	public void pause()
	{
		pauseTime	= System.currentTimeMillis();
	}
	
	/**
	 *	Resumes progression and calculation of
	 *  remaining time.
	 */
	public void resume()
	{
		startTime  += System.currentTimeMillis() - pauseTime;
	}

	/**
	 *	Finish the progression. This clears
	 *  custom text strings, clears indeterminate
	 *  state and sets the bars colour depending
	 *  on the success.
	 *
	 *	@param	success	<code>true</code> for success,
	 *					or <code>false</code> upon failure; the
	 *					corresponding bar colours are green and red.
	 */
	public void finish( int result )
	{
		setIndeterminate( false );
//		setString( success ? "done." : "aborted." );
setValue( maximum );
		setStringPainted( false );
		
		if( result == ProgressComponent.DONE ) {
			setPaint( COLOR_SUCCESS );
		} else if( result == ProgressComponent.FAILED ) {
			setPaint( COLOR_FAILURE );
		}
	}

	/**
	 *  Sets a custom bar colour
	 *
	 *  @param  pnt the new colour or null
	 *			to remove custom colour.
	 */
	public void setPaint( Paint pnt )
	{
		this.pnt = pnt;
		repaint();
	}
	
	/**
	 *  Queries the custom bar colour.
	 *
	 *  @return the bar colour or null if
	 *			no custom colour was set
	 */
	public Paint getPaint()
	{
		return pnt;
	}
	
	public void paintComponent( Graphics g )
	{
		super.paintComponent( g );
		if( pnt == null ) return;
		
		Dimension   d   = getSize();
		Graphics2D  g2  = (Graphics2D) g;
		Paint		op  = g2.getPaint();
		
		g2.setPaint( pnt );
//		g2.fillRect( 0, 0, d.width, d.height );
//		g2.fillRect( 2, 4, d.width - 4, d.height - 12 );		// XXX MacOS X aqua, font dependant ;-(
		g2.fillRect( 2, 1, d.width - 4, d.height - 4 );		// XXX MacOS X aqua, font dependant ;-(
		g2.setPaint( op );
	}
}