/*
 *  AbstractDocumentHandler.java
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

import java.util.ArrayList;
import java.util.List;

/**
 *  A basic implementation of the <code>DocumentHandler</code> interface.
 *
 *  @author		Hanns Holger Rutz
 *  @version	0.15, 02-Feb-06
 */
public abstract class AbstractDocumentHandler
implements DocumentHandler, EventManager.Processor
{
    private final boolean			isMDA;
    private final List				collDocs	= new ArrayList();
    private final EventManager		elm			= new EventManager( this );
    private Document				activeDoc	= null;
    // subclasses can make a synchronized( sync ) block
    // to iterate safely over all documents
    protected final Object			sync		= new Object();

    protected AbstractDocumentHandler( boolean isMDA )
    {
        this.isMDA	= isMDA;
    }

    public boolean isMultiDocumentApplication()
    {
        return isMDA;
    }

    public int getDocumentCount()
    {
        synchronized( sync ) {
            return( collDocs.size() );
        }
    }

    public Document getDocument( int idx )
    {
        synchronized( sync ) {
            if( idx < collDocs.size() ) {
                return( (Document) collDocs.get( idx ));
            } else {
                return null;
            }
        }
    }

    public void addDocument( Object source, Document doc )
    {
        synchronized( sync ) {
            if( !isMultiDocumentApplication() && !collDocs.isEmpty() ) {
                throw new UnsupportedOperationException( "Cannot add more than one doc to a SDA" );
            }
            if( collDocs.contains( doc )) {
                throw new IllegalArgumentException( "Duplicate document registration" );
            }
            collDocs.add( doc );
            if( source != null ) {
                elm.dispatchEvent( new DocumentEvent( source, DocumentEvent.ADDED, System.currentTimeMillis(), doc ));
            }
        }
    }

    public void removeDocument( Object source, Document doc )
    {
        synchronized( sync ) {
            if( doc == activeDoc ) {
                setActiveDocument( source, null );
            }
            if( !collDocs.remove( doc )) {
                throw new IllegalArgumentException( "Tried to remove unknown document" );
            }
            if( source != null ) {
                elm.dispatchEvent( new DocumentEvent( source, DocumentEvent.REMOVED, System.currentTimeMillis(), doc ));
            }
            doc.dispose();
        }
    }

    public void setActiveDocument( Object source, Document doc )
    {
        synchronized( sync ) {
            if( (doc != null) && !collDocs.contains( doc )) {
                throw new IllegalArgumentException( "Tried to make unknown document active" );
            }
            if( doc != activeDoc ) {
                activeDoc	= doc;
                if( source != null ) {
                    elm.dispatchEvent( new DocumentEvent( source, DocumentEvent.FOCUSSED, System.currentTimeMillis(), doc ));
                }
            }
        }
    }

    public Document getActiveDocument()
    {
        synchronized( sync ) {
            return activeDoc;
        }
    }

    public void addDocumentListener( DocumentListener l )
    {
        elm.addListener( l );
    }

    public void removeDocumentListener( DocumentListener l )
    {
        elm.removeListener( l );
    }

// ------------ EventManager.Processor interface ------------

    /**
     *  This is called by the EventManager
     *  if new events are to be processed.
     */
    public void processEvent( BasicEvent e )
    {
        DocumentListener listener;

        for( int i = 0; i < elm.countListeners(); i++ ) {
            listener = (DocumentListener) elm.getListener( i );
            switch( e.getID() ) {
            case DocumentEvent.FOCUSSED:
                listener.documentFocussed( (DocumentEvent) e );
                break;
            case DocumentEvent.ADDED:
                listener.documentAdded( (DocumentEvent) e );
                break;
            case DocumentEvent.REMOVED:
                listener.documentRemoved( (DocumentEvent) e );
                break;
            default:
                assert false : e.getID();
            }
        } // for( i = 0; i < elm.countListeners(); i++ )
    }
}