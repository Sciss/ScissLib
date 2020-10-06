/*
 *  UndoManager.java
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

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import de.sciss.app.Document;

/**
 *  An subclass of Swing's <code>UndoManager</code> that
 *  provides <code>Actions</code> to attach to menu items
 *  in a standard edit menu. Besides it informs the main
 *  application's document handler about the document being
 *	modified, thus allowing the application to update the
 *	window's title bar and display confirmation dialogs when
 *	an unsaved session is about to be discarded.
 *
 *  @author		Hanns Holger Rutz
 *  @version	0.18, 02-Oct-07
 */
public class UndoManager
extends javax.swing.undo.UndoManager
{
    /*
     *  An <code>Action</code> object
     *  suitable for attaching to a
     *  <code>JMenuItem</code> in a edit
     *  menu. The action will cause the
     *  <code>UndoManager</code> to undo
     *  the last edit in its history.
     */
    private final AbstractAction  undoAction;
    /*
     *  An <code>Action</code> object
     *  suitable for attaching to a
     *  <code>JMenuItem</code> in a edit
     *  menu. The action will cause the
     *  <code>UndoManager</code> to redo
     *  the edit that was undone recently.
     */
    private final AbstractAction  redoAction;

    private AbstractAction	debugAction	= null;

    private final Document	doc;

    protected final String undoText, redoText;

    /*
     *	The concept of pendingEdits is that
     *	insignificant (visual only) edits
     *	should not destroy the redo tree
     *	(which they do in the standard undo manager).
     *	Imagine the user places a marker on the timeline,
     *	hits undo, then accidentally or by intention
     *	moves the timeline position. This will render
     *	the redo of the marker add impossible. Now the
     *	timeline position is insignificant and hence
     *	will be placed on the pendingEdits stack.
     *	This is a &quot;lazy&quot; thing because these
     *	pendingEdits will only be added to the real
     *	undo history when the next significant edit comes in.
     */
    private CompoundEdit	pendingEdits;
    private int				pendingEditCount = 0;	// fucking CompoundEdit hasn't got getter methods

    /**
     *  Instantiate a new UndoManager.
     *
     *  @param  doc		the document whose edits are monitored
     */
    public UndoManager( Document doc )
    {
        super();

        setLimit( 1000 );

        this.doc	= doc;
        undoText	= doc.getApplication().getResourceString( "menuUndo" );
        redoText	= doc.getApplication().getResourceString( "menuRedo" );
        undoAction	= createUndoAction();
        redoAction	= createRedoAction();
        pendingEdits= new CompoundEdit();
        updateStates();
    }

    protected AbstractAction createUndoAction()
    {
        return new ActionUndo();
    }

    protected AbstractAction createRedoAction()
    {
        return new ActionRedo();
    }

    /**
     *  Get an Action object that will undo the
     *	last step in the undo history.
     *
     *  @return <code>Action</code> suitable for attaching to a <code>JMenuItem</code>.
     */
    public Action getUndoAction()
    {
        return undoAction;
    }

    /**
     *  Get an Action object that will redo the
     *	next step in the undo history.
     *
     *  @return <code>Action</code> suitable for attaching to a <code>JMenuItem</code>.
     */
    public Action getRedoAction()
    {
        return redoAction;
    }

    /**
     *  Get an Action object that will dump the
     *  current undo history to the console.
     *
     *  @return <code>Action</code> suitable for attaching to a <code>JMenuItem</code>.
     */
    public Action getDebugDumpAction()
    {
        if( debugAction == null ) {
            debugAction = new ActionDebugDump();
        }

        return debugAction;
    }

    /**
     *  Add a new edit to the undo history.
     *  This behaves just like the normal
     *  UndoManager, i.e. it tries to replace
     *  the previous edit if possible. When
     *  the edits <code>isSignificant()</code>
     *  method returns true, the main application
     *  is informed about this edit by calling
     *  the <code>setModified</code> method.
     *  Also the undo and redo action's enabled
     *  / disabled states are updated.
     *	<p>
     *	Insignificant edits are saved in a pending
     *	compound edit that gets added with the
     *	next significant edit to allow redos as
     *	long as possible.
     *
     *  @see	de.sciss.app.Document#setDirty( boolean )
     *  @see	javax.swing.undo.UndoableEdit#isSignificant()
     *  @see	javax.swing.Action#setEnabled( boolean )
     */
    public boolean addEdit( UndoableEdit anEdit )
    {
        if( anEdit.isSignificant() ) {
            synchronized( pendingEdits ) {
                if( pendingEditCount > 0 ) {
                    pendingEdits.end();
                    super.addEdit( pendingEdits );
                    pendingEdits = new CompoundEdit();
                    pendingEditCount = 0;
                }
            }
            final boolean result = super.addEdit( anEdit );
            updateStates();
    //		if( anEdit.isSignificant() ) doc.setDirty( true );
            return result;
        } else {
            synchronized( pendingEdits ) {
                pendingEditCount++;
                return pendingEdits.addEdit( anEdit );
            }
        }
    }

    public void redo()
    throws CannotRedoException
    {
        try {
            undoPending();
            super.redo();
        }
        finally {
            updateStates();
        }
    }

    public void undo()
    throws CannotUndoException
    {
        try {
            undoPending();
            super.undo();
        }
        finally {
            updateStates();
        }
    }

    private void undoPending()
    {
        synchronized( pendingEdits ) {
            if( pendingEditCount > 0 ) {
                pendingEdits.end();
                pendingEdits.undo();
                pendingEdits = new CompoundEdit();
                pendingEditCount = 0;
            }
        }
    }

    /**
     *  Purge the undo history and
     *  update the undo / redo actions enabled / disabled state.
     *
     *  @see	de.sciss.app.Document#setDirty( boolean )
     */
    public void discardAllEdits()
    {
        synchronized( pendingEdits ) {
            pendingEdits.die();
            pendingEdits = new CompoundEdit();
            pendingEditCount = 0;
        }
        super.discardAllEdits();
        updateStates();
    }

    private void updateStates()
    {
        String text;

        if( undoAction.isEnabled() != canUndo() ) {
            undoAction.setEnabled( canUndo() );
            doc.setDirty( canUndo() );
        }
        if( redoAction.isEnabled() != canRedo() ) redoAction.setEnabled( canRedo() );

//		text = canUndo() ? undoText + " " + getUndoPresentationName() : undoText;
        text = getUndoPresentationName();
        if( !text.equals( undoAction.getValue( Action.NAME ))) undoAction.putValue( Action.NAME, text );

//		text = canRedo() ? redoText + " " + getRedoPresentationName() : redoText;
        text = getRedoPresentationName();
        if( !text.equals( redoAction.getValue( Action.NAME ))) redoAction.putValue( Action.NAME, text );
    }

    protected UndoableEdit editToBeRedone()
    {
        return super.editToBeRedone();
    }

    protected UndoableEdit editToBeUndone()
    {
        return super.editToBeUndone();
    }

    public Document getDocument()
    {
        return doc;
    }

    protected List getEdits()
    {
        return edits;
    }

    protected class ActionUndo
    extends AbstractAction
    {
        protected ActionUndo()
        {
            super( undoText );
            putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_Z,
                      Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() ));
        }

        public void actionPerformed( ActionEvent e )
        {
            try {
                undo();
            } catch( CannotUndoException e1 ) {
                System.err.println( e1.getLocalizedMessage() );
            }
//			updateStates();
        }
    }

    protected class ActionRedo
    extends AbstractAction
    {
//		private final String menuText = doc.getApplication().getResourceString( "menuRedo" );

        protected ActionRedo()
        {
            super( redoText );
            putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_Z,
                      Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() + InputEvent.SHIFT_MASK ));
        }

        public void actionPerformed( ActionEvent e )
        {
            try {
                redo();
            } catch( CannotRedoException e1 ) {
                System.err.println( e1.getLocalizedMessage() );
            }
//			updateStates();
        }
    }

    private class ActionDebugDump
    extends AbstractAction
    {
        protected ActionDebugDump()
        {
            super( "Dump Undo History" );
        }

        public void actionPerformed( ActionEvent e )
        {
            final int			num			= getEdits().size();
            final UndoableEdit	redoEdit	= editToBeRedone();
            final UndoableEdit	undoEdit	= editToBeUndone();

            UndoableEdit	edit;

            System.err.println( "Undo buffer contains "+num+" edits." );

            for( int i = 0; i < num; i++ ) {
                edit = (UndoableEdit) getEdits().get( i );
                if( edit == redoEdit ) System.err.print( "R" );
                else if( edit == undoEdit ) System.err.print( "U" );
                else System.err.print( " " );
                System.err.println( " edit #"+i+" = "+edit.getPresentationName() );
            }
        }
    }
}