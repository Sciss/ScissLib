/*
 *  Flag.java
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

/**
 *	A mutable Boolean essentially.
 *
 *	@version	0.11, 21-Apr-08
 *	@author		Hanns Holger Rutz
 */
public class Flag
{
	private boolean	value;
	
	public Flag( boolean onOff )
	{
		value = onOff;
	}
	
	public boolean isSet()
	{
		return value;
	}
	
	public void set( boolean onOff )
	{
		value = onOff;
	}

	public String toString()
	{
		return "Flag( " + value + " )";
	}
}