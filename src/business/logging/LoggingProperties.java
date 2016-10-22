/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business.logging;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 *  Hak
 */
public class LoggingProperties {

    public static void load(Logger logger) {
        try {
            InputStream configFile = LoggingProperties.class.getClassLoader().getResourceAsStream("properties/logging.properties");
            LogManager.getLogManager().readConfiguration(configFile);
            logger.addHandler(new java.util.logging.ConsoleHandler());
            logger.setUseParentHandlers(false);
            configFile.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(LoggingProperties.class.getName()).log(Level.SEVERE, null, ex);

        } catch (IOException ex) {
            Logger.getLogger(LoggingProperties.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
