package cedit;

import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import util.Window;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.*;
import java.net.MalformedURLException;
import java.util.ArrayList;

/**
 * Created by LINKOR on 28.02.2017 in 13:54.
 * Date: 2017.02.28
 */
@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
public class CodeEditor extends Window {
    private JPanel root;
    private JTabbedPane cedt;
    private JEditorPane editorPane1;
    private JButton newf;
    private JButton sas;
    private JButton closeButton;
    private JTextField textField1;
    private JPanel rt;
    private JButton ok;
    private JTextArea terminal;
    private JScrollPane ccc;
    private JButton chn;
    private JButton run;
    private JButton cmp;
    private JComboBox<String> type;
    private JPanel r2t;
    private JTextField tnm;
    private JButton o2k;
    private JLabel error;
    private ArrayList<JEditorPane> panes = new ArrayList<>();

    public CodeEditor() {
        super("Swing CODE");
        setJMenuBar(AddMenuBar());
        setContentPane(root);
        CEListener ce = new CEListener();
        newf.addActionListener(ce);
        sas.addActionListener(ce);
        panes.add(editorPane1);
        closeButton.addActionListener(ce);
        ok.addActionListener(ce);
        chn.addActionListener(ce);
        run.addActionListener(ce);
        cmp.addActionListener(ce);
        o2k.addActionListener(ce);
        type.addItemListener(ce);
    }

    private JMenuBar AddMenuBar() {
        return new JMenuBar();
    }

    private class CEListener implements ActionListener, ItemListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == newf) action(0);
            else if (e.getSource() == sas) action(1);
            else if (e.getSource() == closeButton) action(2);
            else if (e.getSource() == ok) action(3);
            else if (e.getSource() == cmp) action(4);
            else if (e.getSource() == run) action(5);
            else if (e.getSource() == chn) action(7);
            else if (e.getSource() == o2k) action(8);
            else action(-1);
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            editorPane1.setContentType(String.valueOf(e.getItem()) != "null" ? String.valueOf(e.getItem()) : "text/plain");
        }
    }

    private void action(int btn) {
        switch (btn) {
            case 0:
                JEditorPane jep = new JEditorPane();
                panes.add(jep);
                cedt.addTab("untitled", jep);
                break;
            case 1:
                JEditorPane sel = (JEditorPane) ((JScrollPane)cedt.getSelectedComponent()).getComponent(3);
                String text= sel.getText();
                FileDialog fd = new FileDialog((Frame)null, "Save As", FileDialog.SAVE);
                fd.setFilenameFilter((dir, name) -> name.endsWith("." + getExt(sel.getContentType())));
                fd.setVisible(true);
                if (fd.getFile() != null) {
                    File f = new File(fd.getFile());
                    try {
                        BufferedWriter bw = new BufferedWriter(new FileWriter(f));
                        for (String line : text.split(System.lineSeparator())) bw.write(line + "\n");
                        bw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else return;
                break;
            case 2:
                rt.setVisible(true); break;
            case 3:
                int index = Integer.parseInt(textField1.getText());
                cedt.removeTabAt(index);
                panes.remove(index);
                rt.setVisible(false);
                break;
            case 4:
                switch ((String)type.getSelectedItem()) {
                    case "text/java": compileJava(cedt.getTitleAt(cedt.getSelectedIndex())); break;
                    case "text/cpp": compileCpp(cedt.getTitleAt(cedt.getSelectedIndex())); break;
                    case "text/c": compileCpp(cedt.getTitleAt(cedt.getSelectedIndex())); break;
                    case "text/html": error.setText("Error: IDE cannot compile HTML file."); break;
                    case "text/plain": error.setText("Error: IDE cannot compile plain text file."); break;
                    case "text/ruby": error.setText("Error: IDE cannot compile Ruby script. Use JRuby"); break;
                }
            case 7:
                r2t.setVisible(true);
                break;
            case 8:
                String title = tnm.getText();
                cedt.setTitleAt(cedt.getSelectedIndex(), title);
                r2t.setVisible(false);
                break;
            case 5:
                switch ((String)type.getSelectedItem()) {
                    case "text/java": runCmd("java " + cedt.getTitleAt(cedt.getSelectedIndex()).replace(".java", ".class")); break;
                    case "text/cpp": runCmd(cedt.getTitleAt(cedt.getSelectedIndex()).replace(".cpp", ".exe")); break;
                    case "text/c": runCmd(cedt.getTitleAt(cedt.getSelectedIndex()).replace(".c", ".exe")); break;
                    case "text/html":
                        try {
                            HtmlRun(cedt.getTitleAt(cedt.getSelectedIndex()));
                        } catch (MalformedURLException e) {
                            error.setText(e.toString());
                        }
                        break;
                    case "text/ruby": compileRuby(cedt.getTitleAt(cedt.getSelectedIndex()));
                }
                break;
            case -1: break;
        }
    }

    private void HtmlRun(String file) throws MalformedURLException {
        Window w = new Window("HTML Runner");
        JFXPanel jfx = new JFXPanel();
        WebView wv = new WebView();
        wv.getEngine().load("file:///" + file);
        jfx.setScene(new Scene(wv));
        w.add(jfx);
    }

    private void compileRuby(String file) {
    }

    private void compileCpp(String file) {
    }

    private void compileJava(String file) {
    }

    private String getExt(String ct) {
        if (ct == "text/java") {
            return "java";
        }else if (ct == "text/html") {
            return "html";
        } else return "txt";
    }

    private void runCmd(String t) {

    }
}
