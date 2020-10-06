/*
 *  PerformableEdit.java
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

import javax.swing.undo.UndoableEdit;

public interface PerformableEdit
extends UndoableEdit
{
    public PerformableEdit perform();

    public void debugDump( int nest );
}