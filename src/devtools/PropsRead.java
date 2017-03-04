package devtools;

import main.OSMain;
import util.ErrorWindow;
import util.Window;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * Created by LINKOR on 03.03.2017 in 15:03.
 * Date: 2017.03.03
 */
public class PropsRead {
    public PropsRead(String file) throws IOException {
        Properties p = new Properties();
        InputStream is = getClass().getClassLoader().getResourceAsStream(file);
        if (is != null) p.load(is);
        else OSMain.show(new ErrorWindow("Properties Tool error", "property file '" + file + "' not found in the classpath"));
        StringBuilder rt = new StringBuilder("    Properties    \nKey     Value\n");
        for (Map.Entry me : p.entrySet()) rt.append("Key: ").append(me.getKey()).append("    Value: ").append(me.getValue()).append("\n");
        OSMain.show(new ErrorWindow("Properties", rt.toString()));
    }
}
