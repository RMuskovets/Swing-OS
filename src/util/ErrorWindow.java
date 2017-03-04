package util;

import javax.swing.*;
import java.awt.*;

/**
 * Created by LINKOR on 23.01.2017 in 14:47.
 * Date: 2017.01.23
 */
public class ErrorWindow extends Window {
    public ErrorWindow(String title, String err) {
        super(title);
        add(new JLabel(err));
        setLayout(new FlowLayout());
        setSize(500, 150);
        setVisible(true);
    }
}
