/*
 *  Application.java
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

import java.awt.datatransfer.Clipboard;
import java.util.prefs.Preferences;

/**
 *  The <code>Application</code> interface is an attempt
 *	to create common classes and interfaces (the package
 *	<code>de.sciss.app</code>) which can be shared by
 *	different programmes, such as Meloncillo or FScape,
 *	without having to make adjustments in different places
 *	each time a modification is made. This interface
 *	describes the most prominent methods needed for
 *	a general GUI based application.
 *
 *  @author		Hanns Holger Rutz
 *  @version	0.10, 20-May-05
 */
public interface Application
{
    /**
     *	Returns the applications system wide preferences.
     *
     *	@return	the root node of the application's global
     *			preferences
     *
     *	@see	java.util.prefs.Preferences#systemNodeForPackage( Class )
     */
    public Preferences getSystemPrefs();

    /**
     *	Returns the applications user specific preferences.
     *
     *	@return	the root node of the application's preferences
     *			from the current user's local folder
     *
     *	@see	java.util.prefs.Preferences#userNodeForPackage( Class )
     */
    public Preferences getUserPrefs();

    /**
     *	Returns the application programme version.
     *
     *	@return	current application version
     */
    public double getVersion();

    /**
     *	Returns the application's name.
     *
     *	@return	the application's name
     */
    public String getName();

    /**
     *	Returns the clipboard used by the application.
     *
     *	@return	the clipboard used by the application
     *
     *	@see	java.awt.Toolkit#getSystemClipboard()
     */
    public Clipboard getClipboard();

    /**
     *  Retrieves a specific component (such as a GUI frame) of the application.
     *
     *  @param  key		agreed upon idenfier for the component,
     *					e.g. a string or class
     *  @return			the requested component or <code>null</code> if absent or unknown
     */
    public Object getComponent( Object key );

    /**
     *  Adds a newly created component (e.g. a specific frame) to the application.
     *	Adding means making it known to other components which can retrieve this
     *	object by calling the <code>getComponent</code> method.
     *
     *  @param	key			agreed upon idenfier for the component,
     *						e.g. a string or class
     *  @param  component	the component to be registered
     */
    public void addComponent( Object key, Object component );

    /**
     *  Unregisters a component, for example when a frame has been disposed.
     *	This will reomve the component from the internal dictionary.
     *
     *  @param	key			agreed upon idenfier for the component to be removed,
     *						e.g. a string or class
     */
    public void removeComponent( Object key );

    public DocumentHandler getDocumentHandler();

    public WindowHandler getWindowHandler();

    public GraphicsHandler getGraphicsHandler();

    /**
     *	Returns a localized string for a given
     *	key. If the key is not found, returns a warning
     *	text and the key name.
     *
     *	@param	key		a key into the application's main string recource file
     *	@return	the localized text
     *
     *	@see	java.util.ResourceBundle#getString( String )
     */
    public String getResourceString( String key );

    /**
     *	Returns a localized string for a given
     *	key. If the key is not found, returns the
     *	given default string.
     *
     *	@param	key				a key into the application's main string recource file
     *	@param	defaultValue	the text to return if the key is
     *							not in the dictionary
     *	@return					the localized text
     *
     *	@see	java.util.ResourceBundle#getString( String )
     */
    public String getResourceString( String key, String defaultValue );

    /**
     *	Forces to application to quit.
     *	The application will perform necessary cleanup
     *	such as flushing the preferences.
     */
    public void quit();
}