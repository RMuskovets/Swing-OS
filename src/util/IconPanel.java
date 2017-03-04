package util;

import javax.swing.*;
import java.awt.*;

/**
 * Created by LINKOR on 03.03.2017 in 7:25.
 * Date: 2017.03.03
 */
public class IconPanel extends JPanel {
    private Image ii;
    public IconPanel(Image i) {
        ii = i;
    }

    public void paint(Graphics g) {
        g.drawImage(ii, 0, 0, this);
    }
}
