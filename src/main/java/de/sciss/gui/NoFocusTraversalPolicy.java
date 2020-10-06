/*
 *  NoFocusTraversalPolicy.java
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

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.Window;

/**
 *  This is 'no' focus policy because
 *  all requests for focus are
 *  blocked; this can be used
 *  as a Frame's or Dialog's
 *  policy during processing
 *  when the user shouldn't be
 *  able to cycle through virtually
 *  inactive gadgets.
 *
 *  @author		Hanns Holger Rutz
 *  @version	0.10, 20-May-05
 */
public class NoFocusTraversalPolicy
extends FocusTraversalPolicy
{
    /**
     *  @return <code>null</code>, in order to block traversal
     */
    public Component getComponentAfter( Container focusCycleRoot, Component aComponent )
    {
        return null;
    }

    /**
     *  @return <code>null</code>, in order to block traversal
     */
    public Component getComponentBefore( Container focusCycleRoot, Component aComponent )
    {
        return null;
    }

    /**
     *  @return <code>null</code>, in order to block traversal
     */
    public Component getFirstComponent( Container focusCycleRoot )
    {
        return null;
    }

    /**
     *  @return <code>null</code>, in order to block traversal
     */
    public Component getLastComponent( Container focusCycleRoot )
    {
        return null;
    }

    /**
     *  @return <code>null</code>, in order to block traversal
     */
    public Component getDefaultComponent( Container focusCycleRoot )
    {
        return null;
    }

    /**
     *  @return <code>null</code>, in order to block traversal
     */
    public Component getInitialComponent( Window window )
    {
        return null;
    }
}