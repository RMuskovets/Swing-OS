package org.jdesktop.swingx;

import org.jdesktop.swingx.painter.Painter;

interface BackgroundPaintable {
    Painter getBackgroundPainter();

    void setBackgroundPainter(Painter var1);

    boolean isPaintBorderInsets();

    void setPaintBorderInsets(boolean var1);
}
