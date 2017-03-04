package text;

import main.OSMain;
import util.*;
import javax.swing.*;
import java.awt.event.*;
import java.beans.*;

/**
 * Created by LINKOR on 23.01.2017 in 15:25.
 * Date: 2017.01.23
 */
public class TE extends Window implements ActionListener {
    public JPanel panel1;
    private JTextPane text;

    public TE() {
        super("Text Editor");
        setFrameIcon(new ImageIcon("images\\te.png"));
        add(panel1);
        setSize(panel1.getMinimumSize());
        setJMenuBar(getMb());
    }

    private JMenuBar getMb() {
        JMenuBar res = new JMenuBar();
        JMenu file = new JMenu("File"), ab = new JMenu("About");
        file.addActionListener(this);
        ab.addActionListener(this);
        itmms(file, ab);
        res.add(file);
        res.add(ab);
        return res;
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getActionCommand() == "Exit") try {
            setClosed(true);
            setVisible(false);
        } catch (PropertyVetoException e) {e.printStackTrace();}
        else if (evt.getActionCommand() == "Save As...") saveas(text.getText());
        else if (evt.getActionCommand() == "About") add(new ErrorWindow("About Text Editor 2.0", "\t\t\t\tText Editor 2.0\n\tIt's a text editor for\n\tSwing OS."));
    }

    private void saveas(String text) {
        new SaveAs(text);
        OSMain.root.repaint();
    }

    private void itmms(JMenu... ms) {
        ms[0].add("Save As...");
        ms[0].add("Exit");
        ms[1].add("About");
    }
}
