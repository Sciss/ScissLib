/*
 *  PrefMenuAction.java
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

import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;
import javax.swing.KeyStroke;

import de.sciss.app.EventManager;
import de.sciss.app.LaterInvocationManager;
import de.sciss.app.PreferenceEntrySync;
import de.sciss.gui.MenuAction;

/**
 *	Adds PreferenceEntrySync functionality to the superclass.
 *	note that unlike PrefCheckBox and the like, it's only
 *	valid to listen to the prefs changes, not the action events.
 *
 *  @author		Hanns Holger Rutz
 *  @version	0.70, 17-Apr-07
 */
public abstract class PrefMenuAction
extends MenuAction
implements PreferenceEntrySync, PreferenceChangeListener, LaterInvocationManager.Listener
{
    private Preferences				prefs		= null;
    private String					key			= null;
    private	LaterInvocationManager	lim			= null;

    private boolean					readPrefs	= true;
    private boolean					writePrefs	= true;

    public PrefMenuAction( String text, KeyStroke shortcut )
    {
        super( text, shortcut );
    }

    public PrefMenuAction( String text, KeyStroke shortcut, boolean readPrefs, boolean writePrefs )
    {
        this( text, shortcut );
        setReadPrefs( readPrefs );
        setWritePrefs( writePrefs );
    }

    public void setReadPrefs( boolean b )
    {
        if( b != readPrefs ) {
            readPrefs	= b;
            if( prefs != null ) {
                if( readPrefs ) prefs.addPreferenceChangeListener( this );
                else prefs.removePreferenceChangeListener( this );
            }
        }
    }

    public boolean getReadPrefs()
    {
        return readPrefs;
    }

    public void setWritePrefs( boolean b )
    {
        writePrefs	= b;
    }

    public boolean getWritePrefs()
    {
        return writePrefs;
    }

    protected boolean canWritePrefs()
    {
        return( (prefs != null) && (key != null) );
    }

    protected boolean shouldWritePrefs()
    {
        return( (prefs != null) && (key != null) && writePrefs );
    }

    public void setPreferenceKey( String key )
    {
        this.key = key;
        readPrefs();
    }

    public void setPreferenceNode( Preferences prefs )
    {
        if( (this.prefs != null) && readPrefs ) {
            this.prefs.removePreferenceChangeListener( this );
        }
        this.prefs  = prefs;
        if( prefs != null ) {
            if( readPrefs ) prefs.addPreferenceChangeListener( this );
            readPrefs();
        }
    }

    public void setPreferences( Preferences prefs, String key )
    {
        this.key = key;
        setPreferenceNode( prefs );
    }

    public Preferences getPreferenceNode() { return prefs; }
    public String getPreferenceKey() { return key; }

    public void preferenceChange( PreferenceChangeEvent e )
    {
        if( e.getKey().equals( key )) {
            if( EventManager.DEBUG_EVENTS ) System.err.println( "@menu preferenceChange : "+key+" --> "+e.getNewValue() );
            if( lim == null ) lim = new LaterInvocationManager( this );
            lim.queue( e );
        }
    }

    public void readPrefs()
    {
        if( (prefs != null) && (key != null) ) readPrefsFromString( prefs.get( key, null ));
    }

    // o instanceof PreferenceChangeEvent
    public final void laterInvocation( Object o )
    {
        final String prefsValue	= ((PreferenceChangeEvent) o).getNewValue();
        readPrefsFromString( prefsValue );
    }

    protected abstract void readPrefsFromString( String prefsValue );
}