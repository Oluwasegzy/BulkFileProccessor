/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progressoft.idowu.fileprocessor.listener;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.progressoft.idowu.fileprocessor.dao.DBConfig;
import com.progressoft.idowu.fileprocessor.model.DealsCount;
import com.progressoft.idowu.fileprocessor.model.ValidRecord;
import com.progressoft.idowu.fileprocessor.service.FileService;

/**
 *
 * @author oluwasegun.idowu
 */
@WebListener
public class DBLoader implements ServletContextListener {

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(DBLoader.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            log.info("Initializing Application persistence context.......");
            log.info("Initializing Application persistence context.......");
            InputStream  stream = getClass().getResourceAsStream("/persistence.properties");
            DBConfig.loadConfig(stream);
            DBConfig.createEntityManagerFactory();
            log.info("This helps to update/create database table at startup");
            new FileService(DealsCount.class).getEntityManager(true).find(DealsCount.class, "NGN");
           
        } catch (Exception ex) {
            log.error("Startup error", ex);
            System.exit(1);
        } finally {
  
   
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
   
    }

  
}
