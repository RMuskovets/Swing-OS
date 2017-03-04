package main;
import devtools.PropsRead;
import util.ErrorWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by LINKOR on 10.02.2017 in 14:59.
 * Date: 2017.02.10
 */
public class ShellWindow extends util.Window {
    public JTextField sh;
    public JTextArea cmd;
    public JTextArea in = new JTextArea();

    private void showError(Throwable e) {
        cmd.append("<ERROR> " + e.toString() + "\n");
    }


    public ShellWindow() {
        super("cmd");
        cmd = new JTextArea();
        sh = new JTextField();
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setSize(600, 400);
        sh.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                keyPressed(KeyEvent.VK_ENTER);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                mouseClicked(e);
            }
        });
        setLayout(new BorderLayout());
        add(sh, "North");
        cmd.setLineWrap(true);
        cmd.setEditable(false);
        JScrollPane jsp = new JScrollPane(cmd);
        JPanel jp = new JPanel(new BorderLayout());
        jp.add(new JLabel("Input: \\/"), "North");
        jp.add(in, "Center");
        cmd.setText("Java CMD version 1.0, help: \n for execute a command - write it upper &\n  click where you wrote the command\nHelp - run '$ help'\n");
        add(jp, "South");
        add(jsp, "Center");
        in.setRows(5);
        setVisible(true);

    }

    private void keyPressed(int code) {
        if (code == KeyEvent.VK_ENTER && sh.getText().length() != 0 && sh.getText() != null) runCommand(sh.getText());
    }

    public void runCommand(String cmd) {
        if (cmd.startsWith("os")) {
            try {
                Process osp = Runtime.getRuntime().exec(cmd.substring(3));
                new Thread(() -> {
                    BufferedReader br = new BufferedReader(new InputStreamReader(osp.getErrorStream()));
                    String line;
                    try {
                        while ((line = br.readLine()) != null) ShellWindow.this.cmd.append(line + "\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
                new Thread(() -> {
                    BufferedReader br = new BufferedReader(new InputStreamReader(osp.getInputStream()));
                    try {
                        String line;
                        while ((line = br.readLine()) != null) ShellWindow.this.cmd.append(line + "\n");
                    } catch (IOException e) {
                        showError(e);
                    }
                }).start();
                osp.getOutputStream().write(in.getText().getBytes());
            } catch (IOException e) {
                showError(e);
            }
        } else {
//            switch (cmd.substring(2)) {
//                case "clean": ShellWindow.this.cmd.setText(""); break;
//                case "run": runCommand("os java " + cmd.substring(6)); break;
//                default: showError(new RuntimeException("Unknown command: " + cmd.substring(2)));
//            }
            String sh = cmd.substring(2);
            if (sh.equals("clean")) ShellWindow.this.cmd.setText("");
            else if (sh.startsWith("run")) runCommand("os java " + cmd.substring(6));
            else if (sh.equals("help")) this.cmd.append(usage());
            else if (sh.equals("sh")) runCommand("os cmd");
            else if (sh.startsWith("props")) {
                new Thread(() -> {
                    try {
                        new PropsRead(sh.split(" ")[1]);
                    } catch (IOException e) {
                        showError(e);
                    }
                }).start();
            }
            else showError(new RuntimeException("Unknown command: " + sh));
        }
    }
    private static String usage() {
        return "Commands: \n" +
                "$ clean - cleans the shell\n" +
                "$ help - prints this message\n" +
                "os <cmd> - runs <cmd>\n" +
                "$ run <args> - short version of \"os java <args>\"\n" +
                "$ sh - other version of \"os cmd\" (only in Windows)\n";
    }


}
