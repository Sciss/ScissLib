/*
 *  BasicDocument.java
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

package de.sciss.common;

import de.sciss.app.Document;
import de.sciss.util.Flag;

/**
 *  @author		Hanns Holger Rutz
 *  @version	0.70, 02-Oct-07
 */
public abstract class BasicDocument
implements Document
{
    public abstract ProcessingThread closeDocument( boolean force, Flag wasClosed );
    public abstract void start( ProcessingThread pt );
}
