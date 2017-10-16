/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progressoft.idowu.fileprocessor.ws;

import com.google.gson.Gson;
import com.progressoft.idowu.fileprocessor.model.DealsCount;
import com.progressoft.idowu.fileprocessor.model.InvalidRecord;
import com.progressoft.idowu.fileprocessor.model.UploadedFileLog;
import com.progressoft.idowu.fileprocessor.model.ValidRecord;
import com.progressoft.idowu.fileprocessor.service.FileService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.PathParam;

import com.sun.jersey.multipart.FormDataParam;

import java.io.InputStream;
//import org.jboss.resteasy.plugins.providers.multipart.InputPart;
//import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

/**
 *
 * @author oluwasegun.idowu
 */
@Path("/endpoint")
public class RestPath {

	private org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(RestPath.class.getName());

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/upload/{fileName}")
	public String upload(@FormDataParam("file") InputStream input, @PathParam("fileName") String fileName) {
		log.info("uploading filename " + fileName);
		log.info("file input " + input);
		Map<String, Object> map = new HashMap<String, Object>();
		Long millisec = 0l;
		FileService service = new FileService(DealsCount.class);
		try {
			log.info("calling service ");
			millisec = service.upload(input, fileName);
			log.info("upload happened in " + millisec + " milliseconds");
			map.put("responseCode", "0");
			map.put("responseMessage", "Upload happened successful; processing time in milli seconds (" + millisec + ")");
			map.put("uploadTime", millisec);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("An error occurred uploading " + fileName, e);
			map.put("responseCode", "6");
			map.put("responseMessage", e.getMessage());
		}
		return new Gson().toJson(map);
	}

	@GET
	@Path("/reviewValid/{fileName}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String reviewValid(@PathParam("fileName") String fileName) {
		log.info("searching filename valid " + fileName);
		List<ValidRecord> list = new ArrayList<>();
		FileService service = new FileService(DealsCount.class);
		try {
			log.info("finding valid ");
			list = service.retrieveValid(fileName);
			log.info("count " + list.size());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("Error retrieving valid data", e);
		}
		return new Gson().toJson(list);
	}
	
	
	@GET
	@Path("/reviewInvalid/{fileName}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String reviewInvalid(@PathParam("fileName") String fileName) {
		log.info("searching filename invalid " + fileName);
		List<InvalidRecord> list = new ArrayList<>();
		FileService service = new FileService(DealsCount.class);
		try {
			log.info("finding invalid ");
			list = service.retrieveInvalid(fileName);
			log.info("count " + list.size());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("Error retrieving valid data", e);
		}
		return new Gson().toJson(list);
	}
	
	
	@GET
	@Path("/fileLog/{fileName}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String fileLog(@PathParam("fileName") String fileName) {
		List<UploadedFileLog> list = new ArrayList<>();
		FileService service = new FileService(DealsCount.class);
		try {
			log.info("finding invalid ");
			list = service.retrieveFileLog(fileName);
			log.info("count " + list.size());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("Error retrieving valid data", e);
		}
		return new Gson().toJson(list);
	}

}
