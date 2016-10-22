package usecasecontrol;

import java.awt.Window;
import java.util.logging.Level;

import javax.swing.JInternalFrame;

import java.util.logging.Logger;

public class ApplicationUtil {

    private static final Logger LOG = Logger.getLogger(ApplicationUtil.class.getName());

    public static void cleanup(Window[] windows) {
        for (Window w : windows) {
            if (w != null) {
                LOG.log(Level.INFO, "Disposing of window {0}", w.getClass().getName());
                w.dispose();
            }
        }
    }

    public static void cleanup(JInternalFrame[] internalFrames) {
        for (JInternalFrame w : internalFrames) {
            if (w != null) {
                LOG.log(Level.INFO, "Disposing of window {0}", w.getClass().getName());
                w.dispose();
            }
        }
    }
}
