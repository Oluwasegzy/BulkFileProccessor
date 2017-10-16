/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package  com.progressoft.idowu.fileprocessor.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.apache.log4j.Logger;

/**
 *
 * @author Oluwasegun.Idowu
 */
public class DBConfig {

    protected static Map DB_PROPERTIES;
    private static EntityManagerFactory EMF;
      private static  Logger  log = Logger.getLogger(DBConfig.class.getName());

    public static void loadConfig(File configFile) throws IOException  {
         
        if (configFile == null || !configFile.exists()) {
                return;
            }
            Properties prop = new Properties();
            prop.load(new FileInputStream(configFile));
            DB_PROPERTIES = prop;
                   log.info(DB_PROPERTIES);
    }
    
    public static void loadConfig(InputStream stream) throws IOException  {
        
        if (stream == null) {
                return;
            }
            Properties prop = new Properties();
            prop.load(stream);
            DB_PROPERTIES = prop;
                   log.info(DB_PROPERTIES);
    }
    
    public static void createEntityManagerFactory() {
        if (EMF ==null) {
        	  if (DB_PROPERTIES != null) {
                  EMF = Persistence.createEntityManagerFactory("FILEPROCESSORPU", DBConfig.DB_PROPERTIES);
              } else {
                  EMF = Persistence.createEntityManagerFactory("FILEPROCESSORPU");
              }
        }
    }

    public static void closeConfig() {
        if (EMF != null && EMF.isOpen()) {
            EMF.close();
        }
    }

    static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (EMF == null) {
            if (DB_PROPERTIES != null) {
                EMF = Persistence.createEntityManagerFactory("FILEPROCESSORPU", DBConfig.DB_PROPERTIES);
            } else {
                EMF = Persistence.createEntityManagerFactory("FILEPROCESSORPU");
            }

        }
        return EMF;
    }
    
}
