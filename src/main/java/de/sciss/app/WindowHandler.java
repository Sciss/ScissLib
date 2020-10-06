/*
 *  WindowHandler.java
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

import java.util.Iterator;
import java.util.Map;

/**
 *  @author		Hanns Holger Rutz
 *  @version	0.17, 19-Mar-08
 */
public interface WindowHandler
{
    public static final Object OPTION_EXCLUDE_FONT		= "excludefont";	// value : (java.util.)List of components
    public static final Object OPTION_GLOBAL_MENUBAR	= "globalmenu";		// value : null

//	public void addWindow( Window w, Map options );
//	public void removeWindow( Window w, Map options );
    public void addWindow( AbstractWindow w, Map options );
    public void removeWindow( AbstractWindow w, Map options );
    public Iterator getWindows();
    public AbstractWindow createWindow( int flags );
//	public int showOptionPane( JOptionPane op );
    public boolean usesInternalFrames();
    public boolean usesScreenMenuBar();
}
