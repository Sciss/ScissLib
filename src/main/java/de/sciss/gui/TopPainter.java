/*
 *  TopPainter.java
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

import java.awt.Graphics2D;

/**
 *  Simple as that: paint something
 *  arbitrary on top of a hosting component
 *  See the implementing classes for examples.
 *
 *  @author		Hanns Holger Rutz
 *  @version	0.67, 02-Sep-04
 */
public interface TopPainter
{
    /**
     *	Paints something on top of a component's
     *	graphics context. Components offering
     *	adding and removal of top painters should
     *	state which flags and transforms are initially
     *	set for the context, e.g. if coordinates are
     *	already normalized or not. The top painter
     *	should undo any temporary changes to the graphics
     *	context's affine transform, paint and stroke.
     *
     *	@param	g	the graphics context to paint onto.
     */
    public void paintOnTop( Graphics2D g );
}