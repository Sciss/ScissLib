/*
 *  CoverGrowBox.java
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

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.Box;

import de.sciss.app.AbstractApplication;

public class CoverGrowBox
{
    /**
     *  Value: Boolean stating whether frame size (grow) boxes
     *  intrude into the frame's pane. Has default value: yes!<br>
     *  Node: root
     */
    public static final String KEY_INTRUDINGSIZE = "intrudingsize";

    public static Component create()
    {
        return create( 0, 0 );
    }

    public static Component create( int padx, int pady )
    {
        final boolean intruding = AbstractApplication.getApplication().getUserPrefs().getBoolean( KEY_INTRUDINGSIZE, false );
        return Box.createRigidArea( new Dimension( intruding ? 16 + padx : padx, intruding ? 16 + pady : pady ));
    }
}