package util;

import javax.swing.*;

/**
 * Created by LINKOR on 23.01.2017 in 14:42.
 * Date: 2017.01.23
 */
public class Window extends JInternalFrame {
    public Window(String name) {
        super(name);
        setVisible(true);
        setClosable(true);
        setMaximizable(true);
        setIconifiable(true);
        setResizable(true);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
    }
}
