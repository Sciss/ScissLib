/*
 *  NumberListener.java
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
 *  of a <code>NumberField</code> gadget
 *
 *  @author		Hanns Holger Rutz
 *  @version	0.25, 17-Sep-05
 *
 *  @see	NumberField#addListener( NumberListener )
 */
public interface NumberListener
extends EventListener
{
    /**
     *  Notifies the listener that
     *  a number changed occured.
     *
     *  @param  e   the event describing
     *				the number change
     */
    public void numberChanged( NumberEvent e );
}