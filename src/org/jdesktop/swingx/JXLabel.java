package org.jdesktop.swingx;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Window;
import java.awt.event.HierarchyBoundsAdapter;
import java.awt.event.HierarchyEvent;
import java.awt.font.TextAttribute;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.StringReader;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentEvent.ElementChange;
import javax.swing.text.AttributeSet;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.ParagraphView;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.WrappedPlainView;
import org.jdesktop.swingx.BackgroundPaintable;
import org.jdesktop.swingx.SwingXUtilities;
import org.jdesktop.swingx.painter.AbstractPainter;
import org.jdesktop.swingx.painter.Painter;

public class JXLabel extends JLabel implements BackgroundPaintable {
    public static final double NORMAL = 0.0D;
    public static final double INVERTED = 3.141592653589793D;
    public static final double VERTICAL_LEFT = 4.71238898038469D;
    public static final double VERTICAL_RIGHT = 1.5707963267948966D;
    private double textRotation = 0.0D;
    private boolean painting = false;
    private Painter foregroundPainter;
    private Painter backgroundPainter;
    private boolean multiLine;
    private int pWidth;
    private int pHeight;
    private boolean dontIgnoreRepaint = false;
    private int occupiedWidth;
    private static final String oldRendererKey = "washtml";
    private boolean paintBorderInsets = true;
    private int maxLineSpan = -1;
    public boolean painted;
    private JXLabel.TextAlignment textAlignment;

    public JXLabel() {
        this.textAlignment = JXLabel.TextAlignment.LEFT;
        this.initPainterSupport();
        this.initLineWrapSupport();
    }

    public JXLabel(Icon image) {
        super(image);
        this.textAlignment = JXLabel.TextAlignment.LEFT;
        this.initPainterSupport();
        this.initLineWrapSupport();
    }

    public JXLabel(Icon image, int horizontalAlignment) {
        super(image, horizontalAlignment);
        this.textAlignment = JXLabel.TextAlignment.LEFT;
        this.initPainterSupport();
        this.initLineWrapSupport();
    }

    public JXLabel(String text) {
        super(text);
        this.textAlignment = JXLabel.TextAlignment.LEFT;
        this.initPainterSupport();
        this.initLineWrapSupport();
    }

    public JXLabel(String text, Icon image, int horizontalAlignment) {
        super(text, image, horizontalAlignment);
        this.textAlignment = JXLabel.TextAlignment.LEFT;
        this.initPainterSupport();
        this.initLineWrapSupport();
    }

    public JXLabel(String text, int horizontalAlignment) {
        super(text, horizontalAlignment);
        this.textAlignment = JXLabel.TextAlignment.LEFT;
        this.initPainterSupport();
        this.initLineWrapSupport();
    }

    private void initPainterSupport() {
        this.foregroundPainter = new AbstractPainter<JXLabel>() {
            protected void doPaint(Graphics2D g, JXLabel label, int width, int height) {
                Insets i = JXLabel.this.getInsets();
                g = (Graphics2D)g.create(-i.left, -i.top, width, height);

                try {
                    label.paint(g);
                } finally {
                    g.dispose();
                }

            }

            protected boolean shouldUseCache() {
                return false;
            }


            public boolean equals(Object obj) {
                return obj != null && this.getClass().equals(obj.getClass());
            }
        };
        ((AbstractPainter)this.foregroundPainter).setAntialiasing(false);
    }

    private void initLineWrapSupport() {
        this.addPropertyChangeListener(new JXLabel.MultiLineSupport());
        this.addHierarchyBoundsListener(new HierarchyBoundsAdapter() {
            public void ancestorResized(HierarchyEvent e) {
                if(e.getChanged() instanceof JViewport) {
                    Rectangle viewportBounds = e.getChanged().getBounds();
                    if(viewportBounds.getWidth() < (double)JXLabel.this.getWidth()) {
                        View view = JXLabel.this.getWrappingView();
                        if(view != null) {
                            view.setSize((float)viewportBounds.width, (float)viewportBounds.height);
                        }
                    }
                }

            }
        });
    }

    public final Painter getForegroundPainter() {
        return this.foregroundPainter;
    }

    public void reshape(int x, int y, int w, int h) {
        int oldH = this.getHeight();
        super.reshape(x, y, w, h);
        if(this.isLineWrap()) {
            if(oldH != 0) {
                if(w > this.getVisibleRect().width) {
                    w = this.getVisibleRect().width;
                }

                View view = (View)this.getClientProperty("html");
                if(view != null && view instanceof JXLabel.Renderer) {
                    view.setSize((float)(w - this.occupiedWidth), (float)h);
                }

            }
        }
    }

    public void setBackground(Color bg) {
        super.setBackground(bg);
        SwingXUtilities.installBackground(this, bg);
    }

    public void setForegroundPainter(Painter painter) {
        Painter old = this.getForegroundPainter();
        if(painter == null) {
            this.initPainterSupport();
        } else {
            this.foregroundPainter = painter;
        }

        this.firePropertyChange("foregroundPainter", old, this.getForegroundPainter());
        this.repaint();
    }

    public void setBackgroundPainter(Painter p) {
        Painter old = this.getBackgroundPainter();
        this.backgroundPainter = p;
        this.firePropertyChange("backgroundPainter", old, this.getBackgroundPainter());
        this.repaint();
    }

    public final Painter getBackgroundPainter() {
        return this.backgroundPainter;
    }

    public double getTextRotation() {
        return this.textRotation;
    }

    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        if(this.isPreferredSizeSet()) {
            return size;
        } else if(this.textRotation != 0.0D) {
            double view1 = this.getTextRotation();
            size.setSize(rotateWidth(size, view1), rotateHeight(size, view1));
            return size;
        } else {
            View view = this.getWrappingView();
            if(view == null) {
                if(!this.isLineWrap() || JXLabel.MultiLineSupport.isHTML(this.getText())) {
                    return size;
                }

                this.getMultiLineSupport();
                this.putClientProperty("html", JXLabel.MultiLineSupport.createView(this));
                view = (View)this.getClientProperty("html");
            }

            Insets insets = this.getInsets();
            int dx = insets.left + insets.right;
            int dy = insets.top + insets.bottom;
            Rectangle textR = new Rectangle();
            Rectangle viewR = new Rectangle();
            textR.x = textR.y = textR.width = textR.height = 0;
            viewR.x = dx;
            viewR.y = dy;
            viewR.width = viewR.height = 32767;
            Rectangle iconR = this.calculateIconRect();
            boolean textIsEmpty = this.getText() == null || this.getText().equals("");
            byte lsb = 0;
            int gap;
            int labelR_x;
            if(textIsEmpty) {
                textR.width = textR.height = 0;
                gap = 0;
            } else {
                gap = iconR.width == 0?0:this.getIconTextGap();
                this.occupiedWidth = dx + iconR.width + gap;
                Container labelR_width = this.getParent();
                if(labelR_width != null && labelR_width instanceof JPanel) {
                    JPanel labelR_y = (JPanel)labelR_width;
                    Border labelR_height = labelR_y.getBorder();
                    if(labelR_height != null) {
                        Insets dax = labelR_height.getBorderInsets(labelR_y);
                        this.occupiedWidth += dax.left + dax.right;
                    }
                }

                if(this.getHorizontalTextPosition() == 0) {
                    labelR_x = viewR.width;
                } else {
                    labelR_x = viewR.width - (iconR.width + gap);
                }

                float labelR_y1 = view.getPreferredSpan(0);
                textR.width = Math.min(labelR_x, (int)labelR_y1);
                if(this.maxLineSpan > 0) {
                    textR.width = Math.min(textR.width, this.maxLineSpan);
                    if(labelR_y1 > (float)this.maxLineSpan) {
                        view.setSize((float)this.maxLineSpan, (float)textR.height);
                    }
                }

                textR.height = (int)view.getPreferredSpan(1);
                if(textR.height == 0) {
                    textR.height = this.getFont().getSize();
                }
            }

            if(this.getVerticalTextPosition() == 1) {
                if(this.getHorizontalTextPosition() != 0) {
                    textR.y = 0;
                } else {
                    textR.y = -(textR.height + gap);
                }
            } else if(this.getVerticalTextPosition() == 0) {
                textR.y = iconR.height / 2 - textR.height / 2;
            } else if(this.getVerticalTextPosition() != 0) {
                textR.y = iconR.height - textR.height;
            } else {
                textR.y = iconR.height + gap;
            }

            if(this.getHorizontalTextPosition() == 2) {
                textR.x = -(textR.width + gap);
            } else if(this.getHorizontalTextPosition() == 0) {
                textR.x = iconR.width / 2 - textR.width / 2;
            } else {
                textR.x = iconR.width + gap;
            }

            labelR_x = Math.min(iconR.x, textR.x);
            int labelR_width1 = Math.max(iconR.x + iconR.width, textR.x + textR.width) - labelR_x;
            int labelR_y2 = Math.min(iconR.y, textR.y);
            int labelR_height1 = Math.max(iconR.y + iconR.height, textR.y + textR.height) - labelR_y2;
            int day;
            if(this.getVerticalAlignment() == 1) {
                day = viewR.y - labelR_y2;
            } else if(this.getVerticalAlignment() == 0) {
                day = viewR.y + viewR.height / 2 - (labelR_y2 + labelR_height1 / 2);
            } else {
                day = viewR.y + viewR.height - (labelR_y2 + labelR_height1);
            }

            int dax1;
            if(this.getHorizontalAlignment() == 2) {
                dax1 = viewR.x - labelR_x;
            } else if(this.getHorizontalAlignment() == 4) {
                dax1 = viewR.x + viewR.width - (labelR_x + labelR_width1);
            } else {
                dax1 = viewR.x + viewR.width / 2 - (labelR_x + labelR_width1 / 2);
            }

            textR.x += dax1;
            textR.y += day;
            iconR.x += dax1;
            iconR.y += day;
            if(lsb < 0) {
                textR.x -= lsb;
            }

            int x1 = Math.min(iconR.x, textR.x);
            int x2 = Math.max(iconR.x + iconR.width, textR.x + textR.width);
            int y1 = Math.min(iconR.y, textR.y);
            int y2 = Math.max(iconR.y + iconR.height, textR.y + textR.height);
            Dimension rv = new Dimension(x2 - x1, y2 - y1);
            rv.width += dx;
            rv.height += dy;
            return rv;
        }
    }

    private View getWrappingView() {
        if(super.getTopLevelAncestor() == null) {
            return null;
        } else {
            View view = (View)this.getClientProperty("html");
            return !(view instanceof JXLabel.Renderer)?null:view;
        }
    }

    private Container getViewport() {
        for(Object p = this; p != null; p = ((Container)p).getParent()) {
            if(p instanceof Window || p instanceof Applet || p instanceof JViewport) {
                return (Container)p;
            }
        }

        return null;
    }

    private Rectangle calculateIconRect() {
        Rectangle iconR = new Rectangle();
        Icon icon = this.isEnabled()?this.getIcon():this.getDisabledIcon();
        iconR.x = iconR.y = iconR.width = iconR.height = 0;
        if(icon != null) {
            iconR.width = icon.getIconWidth();
            iconR.height = icon.getIconHeight();
        } else {
            iconR.width = iconR.height = 0;
        }

        return iconR;
    }

    public int getMaxLineSpan() {
        return this.maxLineSpan;
    }

    public void setMaxLineSpan(int maxLineSpan) {
        int old = this.getMaxLineSpan();
        this.maxLineSpan = maxLineSpan;
        this.firePropertyChange("maxLineSpan", old, this.getMaxLineSpan());
    }

    private static int rotateWidth(Dimension size, double theta) {
        return (int)Math.round((double)size.width * Math.abs(Math.cos(theta)) + (double)size.height * Math.abs(Math.sin(theta)));
    }

    private static int rotateHeight(Dimension size, double theta) {
        return (int)Math.round((double)size.width * Math.abs(Math.sin(theta)) + (double)size.height * Math.abs(Math.cos(theta)));
    }

    public void setTextRotation(double textOrientation) {
        double old = this.getTextRotation();
        this.textRotation = textOrientation;
        if(old != this.getTextRotation()) {
            this.firePropertyChange("textRotation", old, this.getTextRotation());
        }

        this.repaint();
    }

    public void setLineWrap(boolean b) {
        boolean old = this.isLineWrap();
        this.multiLine = b;
        if(this.isLineWrap() != old) {
            this.firePropertyChange("lineWrap", old, this.isLineWrap());
            if(this.getForegroundPainter() != null) {
                ((AbstractPainter)this.getForegroundPainter()).setCacheable(!b);
            }
        }

    }

    public boolean isLineWrap() {
        return this.multiLine;
    }

    public JXLabel.TextAlignment getTextAlignment() {
        return this.textAlignment;
    }

    public void setTextAlignment(JXLabel.TextAlignment alignment) {
        JXLabel.TextAlignment old = this.getTextAlignment();
        this.textAlignment = alignment;
        this.firePropertyChange("textAlignment", old, this.getTextAlignment());
    }

    public boolean isPaintBorderInsets() {
        return this.paintBorderInsets;
    }

    public boolean isOpaque() {
        return this.painting?false:super.isOpaque();
    }

    public void setPaintBorderInsets(boolean paintBorderInsets) {
        boolean old = this.isPaintBorderInsets();
        this.paintBorderInsets = paintBorderInsets;
        this.firePropertyChange("paintBorderInsets", old, this.isPaintBorderInsets());
    }

    protected void paintComponent(Graphics g) {
        this.painted = true;
        if(this.painting || this.backgroundPainter == null && this.foregroundPainter == null) {
            super.paintComponent(g);
        } else {
            this.pWidth = this.getWidth();
            this.pHeight = this.getHeight();
            if(this.backgroundPainter != null) {
                Graphics2D i = (Graphics2D)g.create();

                try {
                    SwingXUtilities.paintBackground(this, i);
                } finally {
                    i.dispose();
                }
            }

            if(this.foregroundPainter != null) {
                Insets i1 = this.getInsets();
                this.pWidth = this.getWidth() - i1.left - i1.right;
                this.pHeight = this.getHeight() - i1.top - i1.bottom;
                Point2D tPoint = this.calculateT();
                double wx = Math.sin(this.textRotation) * tPoint.getY() + Math.cos(this.textRotation) * tPoint.getX();
                double wy = Math.sin(this.textRotation) * tPoint.getX() + Math.cos(this.textRotation) * tPoint.getY();
                double x = ((double)this.getWidth() - wx) / 2.0D + Math.sin(this.textRotation) * tPoint.getY();
                double y = ((double)this.getHeight() - wy) / 2.0D;
                Graphics2D tmp = (Graphics2D)g.create();
                if(i1 != null) {
                    tmp.translate((double)i1.left + x, (double)i1.top + y);
                } else {
                    tmp.translate(x, y);
                }

                tmp.rotate(this.textRotation);
                this.painting = true;
                this.foregroundPainter.paint(tmp, this, this.pWidth, this.pHeight);
                tmp.dispose();
                this.painting = false;
                this.pWidth = 0;
                this.pHeight = 0;
            }
        }

    }

    private Point2D calculateT() {
        double tx = (double)this.getWidth();
        double ty = (double)this.getHeight();
        if((this.textRotation <= 4.697D || this.textRotation >= 4.727D) && (this.textRotation <= 1.555D || this.textRotation >= 1.585D)) {
            if(this.textRotation > -0.015D && this.textRotation < 0.015D || this.textRotation > 3.14D && this.textRotation < 3.143D) {
                this.pHeight = this.getHeight();
                this.pWidth = this.getWidth();
            } else {
                this.dontIgnoreRepaint = false;
                double var19 = (double)Math.min(this.getHeight(), this.getWidth()) * Math.cos(0.7853981633974483D);
                View v = (View)this.getClientProperty("html");
                double c;
                if(v == null) {
                    ty = (double)this.getFontMetrics(this.getFont()).getHeight();
                    double var20 = ((double)this.getWidth() - Math.abs(ty * Math.sin(this.textRotation))) / Math.abs(Math.cos(this.textRotation));
                    c = ((double)this.getHeight() - Math.abs(ty * Math.cos(this.textRotation))) / Math.abs(Math.sin(this.textRotation));
                    tx = var20 < 0.0D?c:(c > 0.0D?Math.min(var20, c):var20);
                } else {
                    float w = v.getPreferredSpan(0);
                    float h = v.getPreferredSpan(1);
                    c = (double)w;
                    double alpha = this.textRotation;
                    boolean ready = false;

                    while(!ready) {
                        while(h == v.getPreferredSpan(1)) {
                            w -= 10.0F;
                            v.setSize(w, h);
                        }

                        if((double)w < var19 || (double)h > var19) {
                            w = h = (float)var19;
                            v.setSize(w, 100000.0F);
                            break;
                        }

                        h = v.getPreferredSpan(1);
                        double cw = ((double)this.getWidth() - Math.abs((double)h * Math.sin(alpha))) / Math.abs(Math.cos(alpha));
                        double ch = ((double)this.getHeight() - Math.abs((double)h * Math.cos(alpha))) / Math.abs(Math.sin(alpha));
                        c = cw < 0.0D?ch:(ch > 0.0D?Math.min(cw, ch):cw);
                        --c;
                        if(c > (double)w) {
                            v.setSize((float)c, 10.0F * h);
                            ready = true;
                        } else {
                            v.setSize((float)c, 10.0F * h);
                            if(v.getPreferredSpan(1) > h) {
                                v.setSize(w, 10.0F * h);
                            } else {
                                w = (float)c;
                                ready = true;
                            }
                        }
                    }

                    tx = Math.floor((double)w);
                    ty = (double)h;
                }

                this.pWidth = (int)tx;
                this.pHeight = (int)ty;
                this.dontIgnoreRepaint = true;
            }
        } else {
            int square = this.pHeight;
            this.pHeight = this.pWidth;
            this.pWidth = square;
            tx = (double)this.pWidth;
            ty = (double)this.pHeight;
        }

        return new Double(tx, ty);
    }

    public void repaint() {
        if(this.dontIgnoreRepaint) {
            super.repaint();
        }
    }

    public void repaint(int x, int y, int width, int height) {
        if(this.dontIgnoreRepaint) {
            super.repaint(x, y, width, height);
        }
    }

    public void repaint(long tm) {
        if(this.dontIgnoreRepaint) {
            super.repaint(tm);
        }
    }

    public void repaint(long tm, int x, int y, int width, int height) {
        if(this.dontIgnoreRepaint) {
            super.repaint(tm, x, y, width, height);
        }
    }

    public int getHeight() {
        int retValue = super.getHeight();
        if(this.painting) {
            retValue = this.pHeight;
        }

        return retValue;
    }

    public int getWidth() {
        int retValue = super.getWidth();
        if(this.painting) {
            retValue = this.pWidth;
        }

        return retValue;
    }

    protected JXLabel.MultiLineSupport getMultiLineSupport() {
        return new JXLabel.MultiLineSupport();
    }

    protected int getOccupiedWidth() {
        return this.occupiedWidth;
    }

    static class Renderer extends WrappedPlainView {
        JXLabel host;
        boolean invalidated = false;
        private float width;
        private float height;
        private View view;
        private ViewFactory factory;

        Renderer(JXLabel c, ViewFactory f, View v, boolean wordWrap) {
            super((Element)null, wordWrap);
            this.factory = f;
            this.view = v;
            this.view.setParent(this);
            this.host = c;
            if(this.host.getVisibleRect().width == 0) {
                this.invalidated = true;
            } else {
                int w = this.host.getVisibleRect().width;
                this.setSize(c.getMaxLineSpan() > -1?(float)c.getMaxLineSpan():(float)w, (float)this.host.getVisibleRect().height);
            }
        }

        protected void updateLayout(ElementChange ec, DocumentEvent e, Shape a) {
            if(a != null) {
                this.preferenceChanged((View)null, true, true);
                Container host = this.getContainer();
                if(host != null) {
                    host.repaint();
                }
            }

        }

        public void preferenceChanged(View child, boolean width, boolean height) {
            if(this.host != null && this.host.painted) {
                this.host.revalidate();
                this.host.repaint();
            }

        }

        public AttributeSet getAttributes() {
            return null;
        }

        public void paint(Graphics g, Shape allocation) {
            Rectangle alloc = allocation.getBounds();
            if(g.getClipBounds() == null) {
                g.setClip(alloc);
                this.view.paint(g, allocation);
                g.setClip((Shape)null);
            } else {
                this.view.paint(g, allocation);
            }

        }

        public void setParent(View parent) {
            throw new Error("Can\'t set parent on root view");
        }

        public int getViewCount() {
            return 1;
        }

        public View getView(int n) {
            return this.view;
        }

        public Document getDocument() {
            return this.view == null?null:this.view.getDocument();
        }

        public void setSize(float width, float height) {
            if(this.host.maxLineSpan > 0) {
                width = Math.min(width, (float)this.host.maxLineSpan);
            }

            if(width != this.width || height != this.height) {
                this.width = (float)((int)width);
                this.height = (float)((int)height);
                this.view.setSize(width, height == 0.0F?32767.0F:height);
                if(this.height == 0.0F) {
                    this.height = this.view.getPreferredSpan(1);
                }

            }
        }

        public float getPreferredSpan(int axis) {
            if(axis == 0) {
                if(this.invalidated) {
                    int w = this.host.getVisibleRect().width;
                    if(w != 0) {
                        this.invalidated = false;
                        this.setSize((float)(w - this.host.getOccupiedWidth()), (float)this.host.getVisibleRect().height);
                    }
                }

                return this.width > 0.0F?this.width:this.view.getPreferredSpan(axis);
            } else {
                return this.view.getPreferredSpan(axis);
            }
        }

        public Container getContainer() {
            return this.host;
        }

        public ViewFactory getViewFactory() {
            return this.factory;
        }

        public int getWidth() {
            return (int)this.width;
        }

        public int getHeight() {
            return (int)this.height;
        }
    }

    static class BasicDocument extends DefaultStyledDocument {
        BasicDocument(Font defaultFont, Color foreground, JXLabel.TextAlignment textAlignment, float rightIndent) {
            this.setFontAndColor(defaultFont, foreground);
            SimpleAttributeSet attr = new SimpleAttributeSet();
            StyleConstants.setAlignment(attr, textAlignment.getValue());
            this.getStyle("default").addAttributes(attr);
            attr = new SimpleAttributeSet();
            StyleConstants.setRightIndent(attr, rightIndent);
            this.getStyle("default").addAttributes(attr);
        }

        private void setFontAndColor(Font font, Color fg) {
            SimpleAttributeSet attr;
            if(fg != null) {
                attr = new SimpleAttributeSet();
                StyleConstants.setForeground(attr, fg);
                this.getStyle("default").addAttributes(attr);
            }

            if(font != null) {
                attr = new SimpleAttributeSet();
                StyleConstants.setFontFamily(attr, font.getFamily());
                this.getStyle("default").addAttributes(attr);
                attr = new SimpleAttributeSet();
                StyleConstants.setFontSize(attr, font.getSize());
                this.getStyle("default").addAttributes(attr);
                attr = new SimpleAttributeSet();
                StyleConstants.setBold(attr, font.isBold());
                this.getStyle("default").addAttributes(attr);
                attr = new SimpleAttributeSet();
                StyleConstants.setItalic(attr, font.isItalic());
                this.getStyle("default").addAttributes(attr);
                attr = new SimpleAttributeSet();
                Object underline = font.getAttributes().get(TextAttribute.UNDERLINE);
                boolean canUnderline = underline instanceof Integer && ((Integer)underline).intValue() != -1;
                StyleConstants.setUnderline(attr, canUnderline);
                this.getStyle("default").addAttributes(attr);
            }

            attr = new SimpleAttributeSet();
            StyleConstants.setSpaceAbove(attr, 0.0F);
            this.getStyle("default").addAttributes(attr);
        }
    }

    private static class BasicViewFactory implements ViewFactory {
        private BasicViewFactory() {
        }

        public View create(Element elem) {
            String kind = elem.getName();
            Object view = null;
            if(kind == null) {
                view = new LabelView(elem);
            } else if(kind.equals("content")) {
                view = new LabelView(elem);
            } else if(kind.equals("paragraph")) {
                view = new ParagraphView(elem);
            } else if(kind.equals("section")) {
                view = new BoxView(elem, 1);
            } else if(kind.equals("component")) {
                view = new ComponentView(elem);
            } else if(kind.equals("icon")) {
                view = new IconView(elem);
            }

            return (View)view;
        }
    }

    public static class MultiLineSupport implements PropertyChangeListener {
        private static final String HTML = "<html>";
        private static ViewFactory basicViewFactory;
        private static JXLabel.MultiLineSupport.BasicEditorKit basicFactory;

        public MultiLineSupport() {
        }

        public void propertyChange(PropertyChangeEvent evt) {
            String name = evt.getPropertyName();
            JXLabel src = (JXLabel)evt.getSource();
            if("ancestor".equals(name)) {
                src.dontIgnoreRepaint = true;
            }

            if(src.isLineWrap()) {
                if(!"font".equals(name) && !"foreground".equals(name) && !"maxLineSpan".equals(name) && !"textAlignment".equals(name) && !"icon".equals(name) && !"iconTextGap".equals(name)) {
                    if("text".equals(name)) {
                        if(isHTML((String)evt.getOldValue()) && evt.getNewValue() != null && !isHTML((String)evt.getNewValue())) {
                            if(src.getClientProperty("washtml") == null && src.getClientProperty("html") != null) {
                                src.putClientProperty("washtml", src.getClientProperty("html"));
                            }

                            src.putClientProperty("html", createView(src));
                        } else if(!isHTML((String)evt.getOldValue()) && evt.getNewValue() != null && !isHTML((String)evt.getNewValue())) {
                            updateRenderer(src);
                        } else {
                            restoreHtmlRenderer(src);
                        }
                    } else if("lineWrap".equals(name) && !isHTML(src.getText())) {
                        src.putClientProperty("html", createView(src));
                    }
                } else if(evt.getOldValue() != null && !isHTML(src.getText())) {
                    updateRenderer(src);
                }
            } else if("lineWrap".equals(name) && !((Boolean)evt.getNewValue()).booleanValue()) {
                restoreHtmlRenderer(src);
            }

        }

        private static void restoreHtmlRenderer(JXLabel src) {
            Object current = src.getClientProperty("html");
            if(current == null || current instanceof JXLabel.Renderer) {
                src.putClientProperty("html", src.getClientProperty("washtml"));
            }

        }

        private static boolean isHTML(String s) {
            return s != null && s.toLowerCase().startsWith("<html>");
        }

        public static View createView(JXLabel c) {
            JXLabel.MultiLineSupport.BasicEditorKit kit = getFactory();
            float rightIndent = 0.0F;
            if(c.getIcon() != null && c.getHorizontalTextPosition() != 0) {
                rightIndent = (float)(c.getIcon().getIconWidth() + c.getIconTextGap());
            }

            Document doc = kit.createDefaultDocument(c.getFont(), c.getForeground(), c.getTextAlignment(), rightIndent);
            StringReader r = new StringReader(c.getText() == null?"":c.getText());

            try {
                kit.read(r, doc, 0);
            } catch (Throwable var8) {
                ;
            }

            ViewFactory f = kit.getViewFactory();
            View hview = f.create(doc.getDefaultRootElement());
            JXLabel.Renderer v = new JXLabel.Renderer(c, f, hview, true);
            return v;
        }

        public static void updateRenderer(JXLabel c) {
            View value = null;
            View oldValue = (View)c.getClientProperty("html");
            if(oldValue == null || oldValue instanceof JXLabel.Renderer) {
                value = createView(c);
            }

            if(value != oldValue && oldValue != null) {
                for(int i = 0; i < oldValue.getViewCount(); ++i) {
                    oldValue.getView(i).setParent((View)null);
                }
            }

            c.putClientProperty("html", value);
        }

        private static JXLabel.MultiLineSupport.BasicEditorKit getFactory() {
            if(basicFactory == null) {
                basicViewFactory = new JXLabel.BasicViewFactory();
                basicFactory = new JXLabel.MultiLineSupport.BasicEditorKit();
            }

            return basicFactory;
        }

        private static class BasicEditorKit extends StyledEditorKit {
            private BasicEditorKit() {
            }

            public Document createDefaultDocument(Font defaultFont, Color foreground, JXLabel.TextAlignment textAlignment, float rightIndent) {
                JXLabel.BasicDocument doc = new JXLabel.BasicDocument(defaultFont, foreground, textAlignment, rightIndent);
                doc.setAsynchronousLoadPriority(2147483647);
                return doc;
            }

            public ViewFactory getViewFactory() {
                return JXLabel.MultiLineSupport.basicViewFactory;
            }
        }
    }

    protected interface IValue {
        int getValue();
    }

    public static enum TextAlignment implements JXLabel.IValue {
        LEFT(0),
        CENTER(1),
        RIGHT(2),
        JUSTIFY(3);

        private int value;

        private TextAlignment(int val) {
            this.value = val;
        }

        public int getValue() {
            return this.value;
        }
    }
}
