/*
 *  LogTextArea.java
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

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 *  A <code>JTextArea</code> encompassing a <code>PrintWriter</code> that
 *  can be used as an alternative to the standard <code>System.out</code>
 *  or <code>System.err</code> objects. Writing to this <code>PrintWriter</code>
 *  will append the text to the text area.
 *  <p>
 *  This code is based on an idea by Real Gagnon published at:<br>
 *  <A HREF="http://tanksoftware.com/juk/developer/src/com/tanksoftware/util/RedirectedFrame.java">
 *  tanksoftware.com/juk/developer/src/com/tanksoftware/util/RedirectedFrame.java</A>
 *
 *  @author		Hanns Holger Rutz
 *  @version	0.16, 05-May-06
 *
 *  @see	java.io.PrintStream
 *  @see	java.lang.System#setOut( PrintStream )
 *  @see	java.lang.System#setErr( PrintStream )
 */
public class LogTextArea
        extends JTextArea {

    protected final boolean		useLogFile;
    protected final File		logFile;
    private final PrintStream	outStream;
    protected FileWriter		logFileWriter   = null;
    private int					totalLength		= 0;
    private MenuAction			actionClear		= null;

    /**
     *  Constructs a new text area for logging messages.
     *  The area is readonly and wraps lines as they exceed
     *  the right margin. Alternatively messages can
     *  be logged in a text file.
     *
     *  @param rows			same as in JTextArea()
     *  @param columns		same as in JTextArea()
     *  @param useLogFile	<code>true</code> to have a copy of the output logged into a file
     *  @param logFile		if <code>useLogFile</code> is <code>true</code>, this is the file
     *						into which the log is written. if <code>useLogFile</code> is
     *						<code>false</code>, you can pass <code>null</code> here.
     */
    public LogTextArea(int rows, int columns, boolean useLogFile, File logFile) {
        super(rows, columns);

        this.useLogFile = useLogFile;
        this.logFile    = logFile;
        outStream       = new PrintStream(new RedirectedStream());

        setEditable(false);
        setLineWrap(true);
    }

    public LogTextArea() {
        this(6, 40, false, null);
    }

    /*
     *  Returns the stream used by this
     *  gadget to write data to.
     *
     *  Warning:	Theoretically if you use this stream for
     *				<code>System.setErr</code>, you will create
     *				a recursion deadlock if an exception is thrown
     *				within the <code>write</code> method of the
     *				stream. This case has never been experienced however.
     *
     *  @return the <code>PrintStream</code>, useful
     *			for redirecting system output to.
     *
     *  @see	java.lang.System#setOut( PrintStream )
     *  @see	java.lang.System#setErr( PrintStream )
     */
    public PrintStream getLogStream() {
        return outStream;
    }

    /**
     *  This method is public because
     *  of the superclass method. Appending
     *  text using this method directly
     *  will not use the internal print
     *  stream and thus not appear in
     *  a log file.
     */
    public void append(String str) {
        super.append(str);
        totalLength += str.length();
        updateCaret();
    }

    private void updateCaret() {
        try {
            setCaretPosition(Math.max(0, totalLength - 1));
        } catch (IllegalArgumentException e1) { /* ignore */ }
    }

    /**
     *  Replaces the gadget's text.
     *  This is useful for clearing
     *  the gadget. This doesn't
     *  affect the <code>PrintStream</code>
     *  or the log file.
     *
     *  @param  str		the new text to replace
     *					the gadgets content or <code>null</code>
     *					to clear the gadget.
     */
    public void setText(String str) {
        super.setText(str);
        totalLength = str == null ? 0 : str.length();
    }

    public MenuAction getClearAction() {
        if (actionClear == null) {
            actionClear = new ActionClear();
        }
        return actionClear;
    }

    public JScrollPane placeMeInAPane() {
        final JScrollPane res = new JScrollPane(this,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        res.putClientProperty("styleId", "undecorated");
        return res;
    }

    public void makeSystemOutput() {
        System.setOut(getLogStream());
        System.setErr(getLogStream());
    }

// ---------------- internal classes ----------------

    private class RedirectedStream
            extends OutputStream {
        private byte[] cheesy = new byte[1];

        protected RedirectedStream() {
            super();
        }

        public void write(byte b[])
                throws IOException {
            this.write(b, 0, b.length);
        }

        public void write(byte b[], int off, int len)
                throws IOException {
            String str = new String(b, off, len);
            append( str );

            if (useLogFile) {
                if (logFileWriter == null) {
                    logFileWriter = new FileWriter(logFile);
                }
                logFileWriter.write(str);
            }
        }

        public void flush()
                throws IOException {
            if (logFileWriter != null) {
                logFileWriter.flush();
            }
            super.flush();
        }

        public void close()
                throws IOException {
            if (logFileWriter != null) {
                logFileWriter.close();
                logFileWriter = null;
            }
            super.close();
        }

        public void write(int b)
                throws IOException {
            cheesy[0] = (byte) b;
            this.write(cheesy);
        }
    }

    private class ActionClear
            extends MenuAction {
        protected ActionClear() { /* empty */ }

        public void actionPerformed(ActionEvent e) {
            setText(null);
        }
    }
 }