/*
 *  Param.java
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

package de.sciss.util;

import java.util.prefs.Preferences;

/**
 *  @version	0.27, 25-Sep-05
 */
public class Param
{
	public final double		val;
	public final int		unit;
	
	public Param( double val, int unit )
	{
		this.val	= val;
		this.unit	= unit;
	}
	
	public int hashCode()
	{
		final long v = Double.doubleToLongBits( val );
		
		return( (int) (v ^ (v >>> 32)) ^ unit);
	}
	
	public boolean equals( Object o )
	{
		if( (o != null) && (o instanceof Param) ) {
			final Param p2 = (Param) o;
			return( (Double.doubleToLongBits( this.val ) == Double.doubleToLongBits( p2.val )) &&
					(this.unit == p2.unit) );
		} else {
			return false;
		}
	}
 	
	public static Param fromPrefs( Preferences prefs, String key, Param defaultValue )
	{
		final String str = prefs.get( key, null );
		return( str == null ? defaultValue : Param.valueOf( str ));
	}

	public static Param valueOf( String str )
	{
		final int sepIdx = str.indexOf( ' ' );
		if( sepIdx >= 0 ) {
			return new Param( Double.parseDouble( str.substring( 0, sepIdx )),
							  ParamSpace.stringToUnit( str.substring( sepIdx + 1 )));
		} else {
			return new Param( Double.parseDouble( str ), ParamSpace.NONE );
		}
	}
	
	public String toString()
	{
		return( String.valueOf( val ) + ' ' + ParamSpace.unitToString( unit ));
	}
}
