/*
 *  AbstractApplication.java
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

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 *  A rudimentary implementation of the <code>de.sciss.app.Application</code>
 *	interface, which deals with component registration, quitting,
 *	preference, clipboard and resource bundle generation.
 *
 *  @author		Hanns Holger Rutz
 *  @version	0.13, 15-Sep-05
 */
public abstract class AbstractApplication
        implements de.sciss.app.Application {

    /*
     *  This ResourceBundle contains all of the strings used in this application.
     */
    private final ResourceBundle resBundle;

    private final Map					mapComponents	= new HashMap();

    private final Class					mainClass;
    private Preferences					userPrefs		= null;
    private Preferences					systemPrefs		= null;
    private Clipboard					clipboard		= null;

    private static Application app = null;

    private String name;

    /**
     *	Creates a new instance of this class. The <code>mainClass</code>
     *	will be used to determine the application's preference root node.
     *	This <code>name</code> will be used to set the application's name
     *	and to load a <code>ResourceBundle</code> with localized text strings.
     *	The resource bundle must be stored in a file
     *	<code>&quot;<var>&lt;name&gt;</var>Strings.properties&quot;</code>,
     *	e.g. if <code>name</code> equals <code>&quot;FScape&quot;</code>, the
     *	resources are retrieved from <code>&quot;FScapeStrings.properties&quot;</code>!
     *	<p>
     *	Only one instance of this class can be created, any further attempt
     *	will result in a <code>RuntimeException</code>. The one and only instance
     *	is available through the class method <code>getApplication</code>.
     *
     *	@param	mainClass	the main class of the application whose package
     *						(e.g. <code>de.sciss.fscape</code>) is used to identify
     *						the preferences files.
     *	@param	name		application name, should not contain white spaces
     *						or special characters.
     *
     *	@see	#getApplication
     */
    protected AbstractApplication(Class mainClass, String name) {
        super();

        if (AbstractApplication.app != null) {    // only one application allowed
            throw new RuntimeException("AbstractApplication cannot be instantiated more than once");
        }
        AbstractApplication.app = this;
        setName(name);

        resBundle = ResourceBundle.getBundle(name + "Strings", Locale.getDefault());
        this.mainClass = mainClass;

    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        if(this.name == null) {
            this.name = System.getProperty("com.apple.mrj.application.apple.menu.about.name");
        }

        return this.name;
    }

    public final Preferences getSystemPrefs() {
        if (systemPrefs == null) {
            systemPrefs = Preferences.systemNodeForPackage(mainClass);
        }

        return systemPrefs;
    }

    public final Preferences getUserPrefs() {
        if (userPrefs == null) {
            userPrefs = Preferences.userNodeForPackage(mainClass);
        }

        return userPrefs;
    }

//	/**
//	 *	@sync	this method is synchronized
//	 */
//	public UndoManager getUndoManager()
//	{
//		if( undo == null ) {
//			// check again with synchronization
//			// ; the sync is placed here to speed
//			// up normal operation once the undo manager has been created!
//			synchronized( this ) {
//				if( undo == null ) {
//					undo = new de.sciss.app.UndoManager( this );
//				}
//			}
//		}
//		
//		return undo;
//	}

    /**
     *	Returns the system's clipboard, or a local
     *	clipboard if security permits to retrieve the system clipboard.
     */
    public final Clipboard getClipboard()
    {
        if( clipboard == null ) {
            try {
                clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            }
            catch( SecurityException e1 ) {
                clipboard = new Clipboard( getName() );
            }
        }

        return clipboard;
    }

    /**
     *	synchronization:	this method is synchronized
     */
    public final Object getComponent( Object key )
    {
        synchronized( mapComponents ) {
            return mapComponents.get( key );
        }
    }

    /**
     *	synchronization: this method is synchronized
     */
    public final void addComponent(Object key, Object component) {
        synchronized (mapComponents) {
            mapComponents.put(key, component);
        }
    }

    /**
     *	synchronization:	this method is synchronized
     */
    public final void removeComponent(Object key) {
        synchronized (mapComponents) {
            mapComponents.remove(key);
        }
    }

    public final String getResourceString(String key) {
        try {
            return resBundle.getString(key);
        } catch (MissingResourceException e1) {
            return ("[Missing Resource: " + key + "]");
        }
    }

    public final String getResourceString(String key, String defaultValue) {
        try {
            return resBundle.getString(key);
        } catch (MissingResourceException e1) {
            return (defaultValue);
        }
    }

    /**
     *	Flushes preferences and quits.
     */
    public synchronized void quit() {
        try {
            if (systemPrefs != null) systemPrefs.flush();
            if (userPrefs   != null) userPrefs  .flush();
            System.exit(0);
        } catch (BackingStoreException e1) {
            System.err.println("error while flushing prefs : " + e1.getLocalizedMessage());
            System.exit(1);
        }
    }

    /**
     *	Returns the current runtime's application.
     *	Since this is a class method, it can be used by
     *	static classes such IO or GUI utilities to retrieve
     *	the application and its preferences or resource bundle.
     *
     *	@return	the active <code>Application</code> or <code>null</code>
     *			if no application has been created.
     */
    public static Application getApplication() {
        return app;
    }
}