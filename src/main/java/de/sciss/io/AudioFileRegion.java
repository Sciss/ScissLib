/*
 *  AudioFileRegion.java
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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *	A small class that allows
 *	inter-application drag-and-drop
 *	of regions of audio files.
 *
 *  @author		Hanns Holger Rutz
 *  @version	0.15, 05-May-06
 */
public class AudioFileRegion
implements Serializable, Cloneable, Transferable
{
    public final File	file;
    public final Region	region;

    public static DataFlavor flavor = new DataFlavor( AudioFileRegion.class, "Audio File Region" );

    private static final DataFlavor[] supportedFlavors = {
        DataFlavor.javaFileListFlavor, DataFlavor.stringFlavor, flavor
    };

    public AudioFileRegion( File file, Region region )
    {
        this.file	= file;
        this.region	= region;
    }

    public AudioFileRegion( AudioFile af, Region region )
    {
        this.file	= af.getFile();
        this.region	= region;
    }

    public AudioFileRegion( File file, Span region )
    {
        this.file	= file;
        this.region	= new Region( region, file.getName() );
    }

// ------------- Cloneable interface -------------

    /**
     *	Returns a new audio file region which is
     *	equal to this one. <code>CloneNotSupportedException</code>
     *	is never thrown.
     *
     *	@return		a new audio file region identical to this one
     */
    public Object clone()
    throws CloneNotSupportedException
    {
        return super.clone();	// field by field copy
    }

// ------------- Transferable interface -------------

    public DataFlavor[] getTransferDataFlavors()
    {
        return supportedFlavors;
    }

    public boolean isDataFlavorSupported( DataFlavor aFlavor )
    {
        for( int i = 0; i < supportedFlavors.length; i++ ) {
            if( supportedFlavors[ i ].equals( aFlavor )) return true;
        }
        return false;
    }

    public Object getTransferData( DataFlavor aFlavor )
    throws UnsupportedFlavorException, IOException
    {
        if( aFlavor.equals( AudioFileRegion.flavor )) {
            return this;
        } else if( aFlavor.equals( DataFlavor.javaFileListFlavor )) {
            final List coll = new ArrayList( 1 );
            coll.add( file );
            return coll;
        } else if( aFlavor.equals( DataFlavor.stringFlavor )) {
            return region.name;
        } else {
            throw new UnsupportedFlavorException( aFlavor );
        }
    }
}