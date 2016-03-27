/*
 *  BasicCompoundEdit.java
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

package de.sciss.common;

import de.sciss.app.AbstractCompoundEdit;

/**
 *  This subclass of <code>SyncCompoundEdit</code> is 
 *  the most basic extension of the abstract class
 *  which simply puts empty bodies for the abstract methods.
 *
 *  @author			Hanns Holger Rutz
 *  @version		0.70, 01-May-06
 */
public class BasicCompoundEdit
extends AbstractCompoundEdit
{
    private boolean	significant	= true;

    /**
     *  Creates a <code>CompountEdit</code> object
     */
    public BasicCompoundEdit()
    {
        super();
    }

    /**
     *  Creates a <code>CompountEdit</code> object with a given name
     *
     *	@param	presentationName	text describing the compound edit
     */
    public BasicCompoundEdit( String presentationName )
    {
        super( presentationName );
    }

    public boolean isSignificant()
    {
        if( significant ) return super.isSignificant();
        else return false;
    }

    public void setSignificant( boolean b )
    {
        significant = b;
    }

    /**
     *  Does nothing
     */
    protected void undoDone() { /* empty */ }
    /**
     *  Does nothing
     */
    protected void redoDone() { /* empty */ }
    /**
     *  Does nothing
     */
    protected void cancelDone() { /* empty */ }
}