/*
 *  BasicApplication.java
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

import de.sciss.app.AbstractApplication;
import de.sciss.app.DocumentHandler;
import de.sciss.app.GraphicsHandler;
import de.sciss.app.WindowHandler;
import de.sciss.gui.MenuAction;
import de.sciss.gui.MenuRoot;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public abstract class BasicApplication
        extends AbstractApplication {
    private final ActionQuit actionQuit;
    private BasicWindowHandler wh;
    private DocumentHandler dh;
    private GraphicsHandler gh;
    private BasicMenuFactory mf;

    protected BasicApplication(Class c, String name) {
        super(c, name);

        actionQuit = new ActionQuit(getResourceString("menuQuit"),
                KeyStroke.getKeyStroke(KeyEvent.VK_Q,
                        BasicMenuFactory.MENU_SHORTCUT));
    }

    protected void init() {
        gh = new BasicGraphicsHandler();
        dh = createDocumentHandler();
        mf = createMenuFactory();
        wh = createWindowHandler();

        mf.init();
        wh.init();
    }

    public GraphicsHandler getGraphicsHandler() {
        return gh;
    }

    protected abstract BasicMenuFactory createMenuFactory();

    protected abstract BasicWindowHandler createWindowHandler();

    protected abstract DocumentHandler createDocumentHandler();

    public WindowHandler getWindowHandler() {
        return wh;
    }

    public DocumentHandler getDocumentHandler() {
        return dh;
    }

    public MenuRoot getMenuBarRoot() {
        return mf;
    }

    public BasicMenuFactory getMenuFactory() {
        return mf;
    }

    public Action getQuitAction() {
        return actionQuit;
    }

// ---------------- internal classes ---------------- 

    // action for Application-Quit menu item
    private class ActionQuit
            extends MenuAction {
//		private String text;

        protected ActionQuit(String text, KeyStroke shortcut) {
            super(text, shortcut);

//			this.text = text;
        }

        public void actionPerformed(ActionEvent e) {
            quit();
        }
    }
}