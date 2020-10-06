/*
 *  AncestorAdapter.java
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

import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

/**
 *  In analogy to <code>ComponentAdapter</code>
 *  provides a stub class that implements
 *  the <code>AncestorListener</code> interface,
 *  so subclasses need to override only those
 *  methods they're interested in.
 *
 *  @author		Hanns Holger Rutz
 *  @version	0.11, 25-Feb-08
 *
 *  @see	java.awt.event.ComponentAdapter
 */
public class AncestorAdapter
implements AncestorListener
{
    public void ancestorAdded( AncestorEvent e ) { /* empty */ }
    public void ancestorRemoved( AncestorEvent e ) { /* empty */ }
    public void ancestorMoved( AncestorEvent e ) { /* empty */ }
}