package fm;

import jdk.nashorn.tools.Shell;
import main.OSMain;
import main.ShellWindow;
import util.ErrorWindow;
import util.Window;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by LINKOR on 24.01.2017 in 14:26.
 * Date: 2017.01.24
 */
public class Explorer extends Window implements ActionListener {
    private JPanel panel1;
    private JFileChooser jfc;
    private JButton newf;
    private JButton run;

    public Explorer() {
        super("File Explorer");
        add(panel1);
        setFrameIcon(new ImageIcon("images\\fexp.png"));
        jfc.addChoosableFileFilter(new SoundFilter());
        jfc.addChoosableFileFilter(new VideoFilter());
        jfc.addChoosableFileFilter(new TextFilter());
        jfc.addChoosableFileFilter(new JarFilter());
        jfc.addChoosableFileFilter(new DirFilter());
        newf.addActionListener(this);
        run.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == newf) create(jfc.getSelectedFile());
        else if (e.getSource() == run) {
            ShellWindow sw = new ShellWindow();
            sw.runCommand("  " + jfc.getSelectedFile().getName());
            OSMain.show(sw);
        }
    }

    private static boolean create(File f) {
        try {
            return f.createNewFile();
        } catch (Exception e) {
            return false;
        }
    }

    //Filters.
    private static class SoundFilter extends FileFilter {
        @Override
        public boolean accept(File f) {
            return f.getName().endsWith(".mp3") | f.getName().endsWith(".wma") | f.isDirectory() | f.getName().endsWith(".wav");
        }

        @Override
        public String getDescription() {
            return "Sound files";
        }
    }
    private static class VideoFilter extends FileFilter {
        @Override
        public boolean accept(File f) {
            return f.isDirectory() | f.getName().endsWith(".mp4") | f.getName().endsWith(".m2ts") | f.isDirectory() | f.getName().endsWith(".avi");
        }

        @Override
        public String getDescription() {
            return "Video files";
        }
    }
    private static class TextFilter extends FileFilter {
        @Override
        public boolean accept(File f) {
            return f.getName().endsWith(".txt") | f.getName().endsWith(".doc") | f.getName().endsWith(".docx") | f.isDirectory();
        }

        @Override
        public String getDescription() {
            return "Text files";
        }
    }
    private class JarFilter extends FileFilter {

        @Override
        public boolean accept(File f) {
            return f.isDirectory() | f.getName().toLowerCase().endsWith(".jar");
        }

        @Override
        public String getDescription() {
            return "Java ARchives (*.jar)";
        }
    }
    private static class DirFilter extends FileFilter {

        @Override
        public boolean accept(File f) {
            return f.isDirectory();
        }

        @Override
        public String getDescription() {
            return "Directories";
        }
    }
}
