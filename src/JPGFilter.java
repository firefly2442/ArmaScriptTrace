import java.io.File;
import javax.swing.filechooser.*;


public class JPGFilter extends FileFilter {


    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = getExtension(f);

        if (extension != null) {
            if (extension.equals("jpg")) {
                    return true;
            } else {
                return false;
            }
        }

        return false;
    }

    public String getDescription() {
        return "JPG Files";
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
