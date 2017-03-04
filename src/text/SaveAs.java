package text;

import util.Window;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by LINKOR on 25.01.2017 in 14:17.
 * Date: 2017.01.25
 */
public class SaveAs extends Window implements ActionListener {
    JPanel root;
    private JFileChooser jfc;
    private JButton saveButton;
    private String text;

    SaveAs(String text) {
        super("Save as");
        this.text = text;
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == saveButton) {
            File f = jfc.getSelectedFile();
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(f));
                bw.write(text);
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
