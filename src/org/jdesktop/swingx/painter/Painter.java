package org.jdesktop.swingx.painter;

import java.awt.*;

/**
 * Created by LINKOR on 03.03.2017 in 15:23.
 * Date: 2017.03.03
 */
public interface Painter<T> {
    void paint(Graphics2D var1, T var2, int var3, int var4);
}
