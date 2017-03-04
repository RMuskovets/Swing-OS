package zip;

import util.Window;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.util.*;
import java.util.zip.*;

/**
 * Created by LINKOR on 15.12.2016 in 16:20.
 * Date: 2016.12.15
 */
public class JavaZIP {
    private JPanel frameContent;
    private JButton czip;
    private JButton cgz;
    private JButton ogz;
    private JButton ozip;
    private JTable content;
    private JButton ezip;

    public Window showFrame() {
//        JFrame jfr = new JFrame(title);
//        jfr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        jfr.setSize(frameContent.getMinimumSize());
//        jfr.setVisible(true);
//        jfr.add(frameContent);
//        actions();
        Window w = new Window("JavaZIP 1.0");
        w.setSize(frameContent.getMinimumSize());
        w.add(frameContent);
        actions();
        return w;
    }

    private void actions() {
        czip.addActionListener(evt -> createZIP());
        cgz.addActionListener(evt -> createGZ());
        ogz.addActionListener(evt -> openGZ());
        ozip.addActionListener(evt -> openZIP());
        ezip.addActionListener(evt -> extrZIP());
    }

    private void extrZIP() {
        try {
            ZipInputStream zis = new ZipInputStream(new FileInputStream(JOptionPane.showInputDialog("Enter path to ZIP archive")));
            BufferedInputStream bis = new BufferedInputStream(zis);
            ZipEntry ze;
            File dir = new File(JOptionPane.showInputDialog("Enter path to folder, where will be saved\nfiles from archive"));
            while ((ze = zis.getNextEntry()) != null) {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dir.getAbsoluteFile().getAbsolutePath() + "\\" + ze.getName()));
                int c;
                while ((c = bis.read()) != -1) bos.write(c);
                bos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openZIP() {
        Vector<String> cnames = new Vector<>(Arrays.asList("Name", "Size", "Last modified", "CRC"));
        ArrayList<String[]> data = new ArrayList<>();
        try {
            ZipFile zf = new ZipFile(JOptionPane.showInputDialog("Enter path to ZIP archive"));
            Enumeration<? extends ZipEntry> e = zf.entries();
            while (e.hasMoreElements()) {
                ZipEntry ze = e.nextElement();
                data.add(new String[]{ze.getName(), ze.getSize() + " B", ze.getLastModifiedTime() + " ms", String.valueOf(ze.getCrc())});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        content.setModel(new DefaultTableModel(data.toArray(new String[data.size()][]), cnames.toArray(new String[cnames.size()])));
    }
    private void openGZ() {
        try {
            GZIPInputStream gzis = new GZIPInputStream(new FileInputStream(JOptionPane.showInputDialog("Enter path to GZIP archive")));
            File f = new File(JOptionPane.showInputDialog("Enter path to folder, where will be saved\nfile from archive"));
            if (!f.isDirectory()) {
                BufferedInputStream gzbis = new BufferedInputStream(gzis);
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
                int c;
                while ((c = gzbis.read()) != -1) {
                    bos.write(c);
                }
                bos.close();
                gzbis.close();
                gzis.close();
            } else JOptionPane.showMessageDialog(null, "Your file isn't directory.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void createGZ() {
        try {
            String name = JOptionPane.showInputDialog("Enter path to new GZIP archive");
            GZIPOutputStream gz = new GZIPOutputStream(new FileOutputStream(name));
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(JOptionPane.showInputDialog("Enter path to 1-st file in archive " + name)));
            int c;
            while ((c = bis.read()) != -1) {
                gz.write(c);
            }
            gz.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void createZIP() {
        try {
            String s = JOptionPane.showInputDialog("Enter path to new ZIP archive");
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(s));
            zos.putNextEntry(new ZipEntry(JOptionPane.showInputDialog("Enter path to 1-st file in archive " + s)));
            zos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
