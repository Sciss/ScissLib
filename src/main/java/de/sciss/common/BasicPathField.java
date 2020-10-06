/*
 *  BasicPathField.java
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

import java.awt.Dialog;

import de.sciss.gui.PathButton;
import de.sciss.gui.PrefPathField;

public class BasicPathField
extends PrefPathField
{
    public BasicPathField( int type, String dlgTxt )
    {
        super( type, dlgTxt );
    }

    protected PathButton createPathButton( int buttonType )
    {
        return new Button( buttonType );
    }

    private static class Button
    extends PathButton
    {
        protected Button( int type )
        {
            super( type );
        }

        protected void showDialog( Dialog dlg )
        {
//			dlg.setVisible( true );
            BasicWindowHandler.showDialog( dlg );
        }
    }
}
