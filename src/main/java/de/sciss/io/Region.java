/*
 *  Region.java
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

package de.sciss.io;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *  A struct class: region in
 *  an audio file. (copied from FScape).
 *
 *  @author		Hanns Holger Rutz
 *  @version	0.22, 05-May-06
 *
 *  @see	de.sciss.io.AudioFileDescr#KEY_REGIONS
 */
public class Region
implements Serializable, Cloneable
{
// -------- public Variablen --------
    /**
     *  A region's time span in sample frames
     */
    public final Span	span;
    /**
     *  A region's name
     */
    public final String	name;

// -------- public Methoden --------

    /**
     *  Constructs a new immutable region
     *
     *  @param  span	time span in sample frames
     *  @param  name	region's name
     */
    public Region( Span span, String name )
    {
        this.name	= name;
        this.span	= span;
    }

    /**
     *  Constructs a new immutable region
     *  identical to a given region.
     *
     *  @param  orig	the region to copy
     */
    public Region( Region orig )
    {
        this.name	= orig.name;
        this.span	= new Span( orig.span );
    }

    /**
     *	Returns a new region which is
     *	equal to this one. <code>CloneNotSupportedException</code>
     *	is never thrown.
     *
     *	@return		a new region with the same name and span
     *				as this region
     */
    public Object clone()
    throws CloneNotSupportedException
    {
        return super.clone();	// field by field copy
    }

    /**
     *	Sorts regions chronologically
     *
     *  @param  regions a vector whose elements are
     *					instanceof Region.
     *	@param	byBegin	<code>true</code> to sort by region begin points;
     *					<code>false</code> to sort by region end points
     *	@return	sorted region list. Each region
     *			is guaranteed to have a time span's begin (byBegin==true)
     *			or end (byBegin==false) less or equal its successor
     */
    public static List sort( List regions, boolean byBegin )
    {
        List sorted = new ArrayList( regions.size() );
        for( int i = 0; i < regions.size(); i++ ) {
            add( sorted, (Region) regions.get( i ), byBegin );
        }
        return sorted;
    }

    /**
     *	Adds region chronologically to
     *  a pre-sorted list.
     *
     *  @param  regions a vector whose elements are
     *					instanceof Region and which are chronologically
     *					sorted according to the <code>byBegin</code> flag.
     *  @param  region	the region to insert such that
     *					its predecessor has a time span's start (byBegin==true)
     *					or stop (byBegin==false) less or equal this region's
     *					time span start or stop
     *					and the regions's successor has a time span's start or stop
     *					greater than this regions's time span start or stop.
     *	@param	byBegin	<code>true</code> to sort by region begin points;
     *					<code>false</code> to sort by region end points
     *	@return	region index in vector at which it was inserted
     */
    public static int add( List regions, Region region, boolean byBegin )
    {
        int i;
        if( byBegin ) {
            for( i = 0; i < regions.size(); i++ ) {
                if( ((Region) regions.get( i )).span.start > region.span.start ) break;
            }
        } else {
            for( i = 0; i < regions.size(); i++ ) {
                if( ((Region) regions.get( i )).span.stop > region.span.stop ) break;
            }
        }
        regions.add( i, region );
        return i;
    }

    /**
     *	Gets the index for specific region in a list.
     *
     *  @param  regions		a vector whose elements are
     *						instanceof Region.
     *	@param	name		region name to find
     *	@param	startIndex	where to begin
     *	@return	The list index of the first occurance (beginning
     *			at <code>startIndex</code>) of a region whose name equals
     *			the given name.
     */
    public static int find( List regions, String name, int startIndex )
    {
        for( int i = startIndex; i < regions.size(); i++ ) {
            if( ((Region) regions.get( i )).name.equals( name )) return i;
        }
        return -1;
    }
}
// class Region