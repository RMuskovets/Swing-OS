package main;

import cedit.CodeEditor;
import fm.Explorer;
import fm.FileTree2;
import paint.Paint;
import text.TE;
import util.*;
import util.Window;
import web.browser.BrowserMain;
import zip.JavaZIP;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by LINKOR on 23.01.2017 in 14:42.
 * Date: 2017.01.23
 */
public class OSMain extends JFrame implements ActionListener {
    public static JDesktopPane root;
    private OSMain() {
        root = new JDesktopPane();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(900, 700);
        add(root);
        try {
            root.add(new IconWindow("", ImageIO.read(new File("computer.gif")), "Explorer") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    OSMain.show(new Explorer());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        setVisible(true);
        setMenuBar(mb());
    }

    private MenuBar mb() {
        MenuBar res = new MenuBar();
        Menu start = new Menu("Start");
        start.add(new MenuItem("Paint"));
        start.add(new MenuItem("Archiver"));
        start.add(new MenuItem("Text Editor"));
        start.add(new MenuItem("File Explorer"));
        start.add(new MenuItem("Browser"));
        Menu games = new Menu("Games");
        games.add("Tetris");
        games.add("Snake");
        start.add(games);
        games.addActionListener(this);
        Menu stand = new Menu("Standart");
        stand.add("Shell");
        start.add("Files Tree");
        start.add(stand);
        Menu code = new Menu("Dev Tools");
        code.add("Code Editor");
        code.addActionListener(this);
        start.addActionListener(this);
        stand.addActionListener(this);
        start.add(code);
        res.add(start);
        return res;
    }


    public static void main(String[] args) {
        new OSMain();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand() == "Paint") root.add(new Paint());
        else if (e.getActionCommand() == "Archiver") root.add(new JavaZIP().showFrame());
        else if (e.getActionCommand() == "Text Editor") root.add(new TE());
        else if (e.getActionCommand() == "File Explorer") root.add(new Explorer());
        else if (e.getActionCommand() == "Files Tree") root.add(new FileTree2());
        else if (e.getActionCommand() == "Shell") root.add(new ShellWindow());
        else if (e.getActionCommand() == "Tetris") return;
        else if (e.getActionCommand() == "Snake") return;
        else if (e.getActionCommand() == "Code Editor") root.add(new CodeEditor());
        else if (e.getActionCommand() == "Browser") root.add(new BrowserMain());
    }

    public static String showInputDialog(Object text) {
        Window w = new Window("Input");
        w.setLayout(new FlowLayout());
        w.add(new JLabel(text.toString()));
        JTextField txt = new JTextField(10);
        w.add(txt);
        final String[] res = {""};
        class Input implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                res[0] = txt.getText();
            }
        }
        JButton jb = new JButton("OK");
        jb.addActionListener(new Input());
        w.add(jb);
        while (res[0] == "") try {
            TimeUnit.MILLISECONDS.sleep(20);
        } catch (InterruptedException ie) {ie.printStackTrace(); }

        return res[0];
    }

    public static void show(Window w) {
        root.add(w);
    }
}
