/*
 *  IntPrefsMenuAction.java
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
import javax.swing.KeyStroke;

/**
 *	Adds PreferenceEntrySync functionality to the superclass
 *	note that unlike PrefCheckBox and the like, it's only
 *	valid to listen to the prefs changes, not the action events
 *
 *  @author		Hanns Holger Rutz
 *  @version	0.10, 10-Sep-06
 */
public class IntPrefsMenuAction
extends PrefMenuAction
{
    private final int		id;
    private MenuRadioGroup	g;

    public IntPrefsMenuAction( String text, KeyStroke shortcut, int id )
    {
        super( text, shortcut );
        this.id = id;
    }

    public void setRadioGroup( MenuRadioGroup g )
    {
        this.g = g;
    }

    /**
     *  Fired when radio button is checked
     */
    public void actionPerformed( ActionEvent e )
    {
        if( shouldWritePrefs() ) {
            if( getPreferenceNode().getInt( getPreferenceKey(), -1 ) != id ) {
                getPreferenceNode().putInt( getPreferenceKey(), id );
            }
        }
    }

    protected void readPrefsFromString( String prefsValue )
    {
        if( prefsValue == null ) return;
        final int prefsVal	= Integer.parseInt( prefsValue );

        if( (prefsVal == id) && (g != null) ) g.setSelected( id );
    }

    public void writePrefs()
    {
        if( canWritePrefs() ) {
            getPreferenceNode().putInt( getPreferenceKey(), id );
        }
    }
}

