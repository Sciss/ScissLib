/*
 *  AbstractAboutFrame.java
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
import javax.swing.border.Border;
import javax.swing.event.HyperlinkListener;
import java.awt.*;

/** Variant of MRJ About Frame. */
public abstract class AbstractAboutFrame extends JFrame {
    private final JLabel        lbApp;
    private final JTextArea     ggVersion;
    private final JEditorPane   ggCredits;
    private final JScrollPane   scrollCredits;
    private final JTextArea     ggCopyright;
    private final String        versionString;

    private String buildVersion;
    private HyperlinkListener hyperlinkListener;

    public AbstractAboutFrame(String applicationName, String versionString) {
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        final JPanel c = (JPanel) getContentPane();
        c.setLayout(new GridBagLayout());
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx         = 100.0;
        gbc.insets.top      = 5;
        gbc.insets.bottom   = 5;
        gbc.gridy = 0;
        lbApp = new JLabel();
        c.add(lbApp, gbc);
        gbc.gridy = 1;
        final JTextArea ggAppName = new JTextArea("java");
        ggAppName.setEditable(false);
        ggAppName.setOpaque(false);
        ggAppName.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        c.add(ggAppName, gbc);
        gbc.gridy = 2;
        ggVersion = new JTextArea("Version x.x");
        ggVersion.setEditable(false);
        ggVersion.setOpaque(false);
        final Font f = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
        ggVersion.setFont(f);
        c.add(ggVersion, gbc);
        gbc.gridy = 3;
        gbc.fill = 2;
        ggCredits = new JEditorPane();
        ggCredits.setMargin(new Insets(2, 4, 2, 4));
        ggCredits.setEditable(false);
        scrollCredits = new JScrollPane(ggCredits);
        final Border bo = scrollCredits.getBorder();
        Insets i;
        if(bo != null) {
            i = bo.getBorderInsets(scrollCredits);
        } else {
            i = new Insets(0, 0, 0, 0);
        }

        scrollCredits.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, -i.left + 1, 0, -i.right + 1), bo));
        scrollCredits.setPreferredSize(new Dimension(100, 150));
        c.add(scrollCredits, gbc);
        gbc.gridy = 4;
        gbc.insets.bottom = 32;
        gbc.fill = 0;
        ggCopyright = new JTextArea(" ");
        ggCopyright.setEditable(false);
        ggCopyright.setOpaque(false);
        ggCopyright.setFont(f);
        c.add(ggCopyright, gbc);
        lbApp.setVisible(false);
        scrollCredits.setVisible(false);
        if (applicationName != null) {
            ggAppName.setText(applicationName);
        }

        this.versionString = versionString;
        if (versionString != null) {
            ggVersion.setText(versionString);
        }

        packAndCenter();
    }

    public void setApplicationIcon(Icon applicationIcon) {
        lbApp.setIcon(applicationIcon);
        lbApp.setVisible(applicationIcon != null);
        packAndCenter();
    }

    public void setBuildVersion(String buildVersion) {
        this.buildVersion = buildVersion;
        applyVersion();
    }

    private void applyVersion() {
        final StringBuilder b = new StringBuilder();
        if(versionString != null) {
            b.append(versionString);
        } else {
            b.append("Version x.x");
        }

        if(buildVersion != null) {
            b.append(" (v");
            b.append(buildVersion);
            b.append(")");
        }

        ggVersion.setText(b.toString());
    }

    public void setCredits(String credits, String contentType) {
        if(credits != null) {
            ggCredits.setContentType(contentType);
        }

        ggCredits.setText(credits != null?credits:"");
        ggCredits.setCaretPosition(0);
        scrollCredits.setVisible(credits != null);
        packAndCenter();
    }

    public void setCreditsPreferredSize(Dimension preferredSize) {
        scrollCredits.setPreferredSize(preferredSize);
        packAndCenter();
    }

    public void setHyperlinkListener(HyperlinkListener l) {
        if(this.hyperlinkListener != null) {
            ggCredits.removeHyperlinkListener(this.hyperlinkListener);
        }

        this.hyperlinkListener = l;
        if(l != null) {
            ggCredits.addHyperlinkListener(l);
        }
    }

    public void setCopyright(String copyright) {
        ggCopyright.setText(copyright != null ? copyright : " ");
        packAndCenter();
    }

    private void packAndCenter() {
        pack();
        setSize(285, getSize().height);

        final Dimension ss = this.getToolkit().getScreenSize();
        final Dimension fs = this.getSize();
        setLocation((ss.width - fs.width) / 2, (ss.height - fs.height) / 4);
    }
}