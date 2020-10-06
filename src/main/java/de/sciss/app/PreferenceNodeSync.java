/*
 *  PreferenceNodeSync.java
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

import java.util.prefs.Preferences;

/**
 *  Objects implementing this interface
 *  state that they will store their (and their children's)
 *  serialized representation in privately named
 *  entries in a given preference node
 *
 *  @author		Hanns Holger Rutz
 *  @version	0.10, 20-May-05
 */
public interface PreferenceNodeSync
{
    /**
     *  Enables Preferences synchronization.
     *  This method is not thread safe and
     *  must be called from the event thread.
     *  When a preference change is received,
     *  the GUI is updated and dispatches an event
     *  to registered listeners.
     *  Likewise, if the user adjusts the GUI
     *  value, the preference will be
     *  updated. The same is true, if you
     *  call one of the value changing methods.
     *
     *  @param  prefs   the preferences node in which
     *					the values are stored, or null
     *					to disable prefs sync.
     */
    public void setPreferences( Preferences prefs );
}