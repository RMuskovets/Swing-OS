package web.browser;

import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import util.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by LINKOR on 04.03.2017 in 7:43.
 * Date: 2017.03.04
 */
public class BrowserMain extends Window {
    private BrowserView bw = new BrowserView();
    private JTextField url = new JTextField(20);
    private JButton reload = new JButton(new ImageIcon("images/browser/reload.gif"));
    public BrowserMain() {
        super("Browser");
        setLayout(new BorderLayout());
        JToolBar jtb = new JToolBar();
        jtb.setLayout(new FlowLayout());
        jtb.add(reload);
        jtb.add(url);
        add(jtb, "North");
        add(bw, "Center");
        BMListener bml = new BMListener();
        bw.addMouseListener(bml);
        reload.addActionListener(bml);

    }

    private class BMListener extends MouseAdapter implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            fireAction();
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            fireMouseClick();
        }
    }

    private void fireAction() {

    }

    private void fireMouseClick() {

    }
}
