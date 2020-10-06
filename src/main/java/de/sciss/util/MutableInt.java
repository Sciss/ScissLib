/*
 *  MutableInt.java
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
 *	A mutable integer
 *
 *	@version	0.11, 21-Apr-08
 *	@author		Hanns Holger Rutz
 */
public class MutableInt
{
	private int value;
	
	public MutableInt( int initialValue )
	{
		value = initialValue;
	}
	
	public int value()
	{
		return value;
	}
	
	public void set( int newValue )
	{
		value = newValue;
	}

	public void add( int x )
	{
		value += x;
	}
	
	public String toString()
	{
		return "MutableInt( " + value + " )";
	}
}