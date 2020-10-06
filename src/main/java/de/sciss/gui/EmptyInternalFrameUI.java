/*
 *  EmptyInternalFrameUI.java
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

import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.plaf.basic.BasicInternalFrameUI;

/**
 * 	A rather quick and dirty hack to be able
 * 	to produce borderless JInternalFrames.
 * 	This extends EmptyInternalFrameUI by
 * 	not creating the title bar and not
 * 	installing the mouse listeners that will
 * 	allow you to resize the window from its
 * 	borders.
 * 
 *	@author		Hanns Holger Rutz
 *	@version	0.10, 01-Aug-08
 */
public class EmptyInternalFrameUI
extends BasicInternalFrameUI	// BasicInternalFrameUI
{
    public EmptyInternalFrameUI( JInternalFrame b )
    {
        super( b );
    }

    protected JComponent createNorthPane( JInternalFrame w )
    {
        return null;
    }

// this causes problems!
// instead make sure resizable is kept false
//    protected void installMouseHandlers( JComponent c )
//    {
//    	// nada
//    }
}
