/*
 *  DynamicListening.java
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

package de.sciss.app;

/**
 *  This interface is used by Components that
 *  need to register and unregister listeners
 *  whenever they are shown or hidden in order
 *  to improve performance and garbage collection.
 *  <code>ObserverPalette</code> is a good example
 *  where listeners are dynamically added and removed.
 *  Usually you don't call the interface methods
 *  directly but let <code>DynamicAncestorAdapter</code>
 *  do the work.
 *
 *  @author		Hanns Holger Rutz
 *  @version	0.13, 15-Sep-05
 *
 *  @see	DynamicAncestorAdapter
 */
public interface DynamicListening
{
    /**
     *  will be called when the component
     *  becomes visible
     */
    public void startListening();
    /**
     *  will be called when the component
     *  is hidden
     */
    public void stopListening();
}