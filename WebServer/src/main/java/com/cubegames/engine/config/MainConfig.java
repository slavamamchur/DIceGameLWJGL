package com.cubegames.engine.config;

import com.cubegames.engine.utils.Utils;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

@Configuration
@ComponentScan("com.cubegames.engine")
public class MainConfig {

  private static final String MAIN_CONFIG_FILE = "/config/cubegames.properties";
  private static final String LOG_CONFIG_FILE = "/config/cubegames_log.properties";
  private static final Properties properties = new Properties();

  private static final Logger log = Logger.getLogger(MainConfig.class.getName());
  //TODO: create own one-line-message formatter for Logger

  public MainConfig() {
    //System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");

    String message = "\n\n\n           C U B E    G A M E S \n\n\n" +
        "\n Creating MainConfig at " + Utils.longToDateTime(System.currentTimeMillis()) +
        "\n Current folder: " + System.getProperty("user.dir") +
        "\n\n";

    System.out.println(message);
    log.info(message);

    try {
      log.info("Loading LOG config file: " + LOG_CONFIG_FILE);
      File fLog = new File(LOG_CONFIG_FILE);
      if (fLog.exists() && !fLog.isDirectory()) {
        log.info("... LOG config file is found. Loading...");
        LogManager.getLogManager().readConfiguration(new FileInputStream(fLog));
      } else {
        log.info("... LOG config file is not found!  Using default config.");
      }

      log.info("Loading MAIN config file: " + MAIN_CONFIG_FILE);
      File fMain = new File(MAIN_CONFIG_FILE);
      if (fMain.exists() && !fMain.isDirectory()) {
        log.info("... MAIN config file is found. Loading...");
        properties.load(new FileInputStream(fMain));
      } else {
        log.info("... MAIN config file is not found!  Using default config.");
      }
    } catch (FileNotFoundException e) {
      log.log(Level.SEVERE, "No such file. Using default config.", e);
    } catch (IOException e) {
      log.log(Level.SEVERE, "Cannot load properties from file.", e);
    }
  }


  public static Properties getMainProperties() {
    return properties;
  }

}
