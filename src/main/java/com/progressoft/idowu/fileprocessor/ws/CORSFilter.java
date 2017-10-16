/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progressoft.idowu.fileprocessor.ws;

import java.io.IOException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author Oluwasegun.Idowu
 */
@Provider
public class CORSFilter implements ContainerResponseFilter {
    
   private org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(CORSFilter.class.getName());
    
   @Override
   public void filter(final ContainerRequestContext requestContext,
                      final ContainerResponseContext cres) throws IOException {
      log.info("djdjdjdturueueue8988 " + requestContext);
      
      cres.getHeaders().add("Access-Control-Allow-Origin", "*");
      cres.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
      cres.getHeaders().add("Access-Control-Allow-Credentials", "true");
      cres.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
      cres.getHeaders().add("Access-Control-Max-Age", "1209600");
   }
}
