import java.io.File;
import javax.swing.filechooser.*;


public class ScriptFilter extends FileFilter {


    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = getExtension(f);

        if (extension != null) {
            if (extension.equals("sqf") ||
                extension.equals("sqs") ||
                extension.equals("ext") ||
                extension.equals("sqm") ||
                extension.equals("hpp") ||
                extension.equals("fsm") ||
                extension.equals("h") ||
                extension.equals("cpp")) {
                    return true;
            } else {
                return false;
            }
        }

        return false;
    }

    public String getDescription() {
        return "Arma2 Files";
    }
    
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
}
