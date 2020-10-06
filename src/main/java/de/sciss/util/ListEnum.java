/*
 *  ListEnum.java
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

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

/**
 *	@author		Hanns Holger Rutz
 *	@version	0.10, 25-Feb-06
 */
public class ListEnum
implements Enumeration
{
	private final Iterator iter;

	public ListEnum( List coll )
	{
		iter	= coll.iterator();
	}

	public ListEnum( Iterator iter )
	{
		this.iter	= iter;
	}

	public boolean hasMoreElements()
	{
		return iter.hasNext();
	}
	
	public Object nextElement()
	{
		return iter.next();
	}
}
