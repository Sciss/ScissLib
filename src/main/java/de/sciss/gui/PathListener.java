/*
 *  PathListener.java
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

import java.util.EventListener;

/**
 *  Interface for listening
 *  the changes of the contents
 *  of a <code>PathField</code> or
 *  <code>PathButton</code> gadget
 *
 *  @author		Hanns Holger Rutz
 *  @version	0.10, 20-May-05
 *
 *  @see	PathField#addPathListener( PathListener )
 */
public interface PathListener
extends EventListener
{
    /**
     *  Notifies the listener that
     *  a path changed occured.
     *
     *  @param  e   the event describing
     *				the path change
     */
    public void pathChanged( PathEvent e );
}