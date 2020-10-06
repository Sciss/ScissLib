/*
 *  DocumentEvent.java
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

import de.sciss.app.BasicEvent;

/**
 *  This kind of event is fired
 *  from a <code>DocumentHandler</code> when
 *  a document as been created, destroyed
 *	or switched.
 *
 *  @author		Hanns Holger Rutz
 *  @version	0.17, 18-Mar-08
 *
 *  @see		DocumentHandler#addDocumentListener( DocumentListener )
 *  @see		DocumentHandler
 *  @see		Document
 */
public class DocumentEvent
extends BasicEvent
{
// --- ID values ---
    /**
     *  returned by getID() : the document has been added
     */
    public static final int ADDED		= 0;

    /**
     *  returned by getID() : the document has been removed
     */
    public static final int REMOVED		= 1;

    /**
     *  returned by getID() : the document has become the active document
     */
    public static final int FOCUSSED	= 2;

    private final Document	doc;

    /**
     *  Constructs a new <code>DocumentEvent</code>
     *
     *  @param  source  who originated the action
     *  @param  ID		<code>ADDED</code>, <code>REMOVED</code>, or <code>FOCUSSED</code>
     *  @param  when	system time when the event occured
     *  @param  doc		the related document
     */
    public DocumentEvent( Object source, int ID, long when, Document doc )
    {
        super( source, ID, when );

        this.doc		= doc;
    }

    /**
     *  Queries the related document
     *
     *  @return the document which was added, removed or focussed.
     */
    public Document getDocument()
    {
        return doc;
    }

    public boolean incorporate( BasicEvent oldEvent )
    {
        if( (oldEvent instanceof DocumentEvent) &&
            (getSource() == oldEvent.getSource()) &&
            (getID() == oldEvent.getID()) &&
            (getDocument() == ((DocumentEvent) oldEvent).getDocument()) ) {

            return true;

        } else return false;
    }
}
