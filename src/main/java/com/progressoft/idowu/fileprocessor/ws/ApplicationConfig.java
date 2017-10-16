/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progressoft.idowu.fileprocessor.ws;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author Oluwasegun.Idowu
 */
@javax.ws.rs.ApplicationPath("path")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
          addRestResource(resources);
         
        addRestResourceClasses(resources);
        return resources;
    }
    
    
      private void addRestResource(Set<Class<?>> resources) {
    
        resources.add(RestPath.class);
    }

    /**
     * Do not modify addRestResourceClasses() method. It is automatically
     * populated with all resources defined in the project. If required, comment
     * out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(RestPath.class);
    }

}
