/*
 *  ColouredTextField.java
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
import java.awt.*;

/**
 * Subclass of <code>JTextField</code> that
 * adds <code>setPaint</code> and <code>getPaint</code>
 * methods and overrides the
 * <code>paintComponent</code> method. In
 * <code>paintComponent</code> it simply
 * fills the components area with the
 * specified paint. Usually this paint
 * is translucent allowing a textfield
 * to get for example a 'blueish texture'
 * which is what <code>PathField</code> does.
 *
 * @author Hanns Holger Rutz
 * @version 0.16, 05-May-06
 * @see    PathField
 */
public class ColouredTextField
        extends JTextField {

    private Paint p = null;

    /**
     * Constructs a new ColouredTextField
     */
    public ColouredTextField() {
        super();
        init();
    }

    /**
     * Constructs a new ColouredTextField
     *
     * @param columns number of columns for the textfield
     */
    public ColouredTextField(int columns) {
        super(columns);
        init();
    }

    private void init() {
        // nothing
    }

    /**
     * Changes the colour of the text field.
     *
     * @param p the new colour. Use translucent
     *          colour (RGBA) for the normal
     *          text component to shine through.
     */
    public void setPaint(Paint p) {
        this.p = p;
        repaint();
    }

    /**
     * Returns the current attached colour.
     *
     * @return the current colour or <code>null</code>
     * if no colour was set
     */
    public Paint getPaint() {
        return p;
    }

    /**
     * Paints the text field component and
     * fill its area with the specified
     * paint afterwards. If no paint was
     * specified, the text field will just
     * look like a normal <code>JTextField</code>.
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (p == null) return;

        final Dimension d     = getSize();
        final Graphics2D g2   = (Graphics2D) g;
        final Paint op        = g2.getPaint();

        final String    lafId       = UIManager.getLookAndFeel().getID();
        final boolean   isWebLaF    = lafId.equals("submin") || lafId.equals("weblaf");
        final int       insets      = isWebLaF ? 1 : 0;

        g2.setPaint(p);
        g2.fillRect(insets, insets, d.width - (insets + insets), d.height - (insets + insets));
        g2.setPaint(op);
    }
}