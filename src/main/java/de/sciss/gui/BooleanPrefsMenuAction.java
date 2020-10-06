/*
 *  BooleanPrefsMenuAction.java
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

import java.awt.event.ActionEvent;
import javax.swing.AbstractButton;
import javax.swing.KeyStroke;

/**
 *	Adds PreferenceEntrySync functionality to the superclass
 *	note that unlike PrefCheckBox and the like, it's only
 *	valid to listen to the prefs changes, not the action events
 *
 *  @author		Hanns Holger Rutz
 *  @version	0.10, 10-Sep-06
 */
public class BooleanPrefsMenuAction
extends PrefMenuAction
{
    private MenuCheckItem mci;

    public BooleanPrefsMenuAction( String text, KeyStroke shortcut )
    {
        super( text, shortcut );
    }

    public void setCheckItem( MenuCheckItem mci )
    {
        this.mci = mci;
    }

    /**
     *  Switches button state
     *  and updates preferences.
     */
    public void actionPerformed( ActionEvent e )
    {
        boolean state   = ((AbstractButton) e.getSource()).isSelected();

        if( mci != null ) mci.setSelected( state );

        if( shouldWritePrefs() ) {
            getPreferenceNode().putBoolean( getPreferenceKey(), state );
        }
    }

    public void writePrefs()
    {
        if( canWritePrefs() && (mci != null) ) getPreferenceNode().putBoolean( getPreferenceKey(), mci.isSelected() );
    }

    protected void readPrefsFromString( String prefsValue )
    {
        if( prefsValue == null ) return;
        final boolean prefsVal	= Boolean.valueOf( prefsValue ).booleanValue();

        if( mci != null ) mci.setSelected( prefsVal );
    }
}