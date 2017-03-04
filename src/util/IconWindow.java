package util;

import org.jdesktop.swingx.JXLabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by LINKOR on 03.03.2017 in 7:24.
 * Date: 2017.03.03
 */
public abstract class IconWindow extends Window implements MouseListener, ActionListener {
    public IconWindow(String name, Image i, String appName) {
        super(name);
        setLayout(new BorderLayout());
        add(new IconPanel(i), "Center");
        add(new JLabel(appName), "South");
        addMouseListener(this);
        setResizable(false);
        setClosable(false);
        setMaximizable(false);
        setVisible(true);
    }
    @Override
    public void mouseClicked(MouseEvent e) {
        actionPerformed(new ActionEvent(e.getSource(), ActionEvent.ACTION_PERFORMED, "action"));
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseClicked(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    public abstract void actionPerformed(ActionEvent e);
}
