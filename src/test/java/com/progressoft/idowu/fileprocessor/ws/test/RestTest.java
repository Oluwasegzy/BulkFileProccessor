package com.progressoft.idowu.fileprocessor.ws.test;

import org.hamcrest.core.IsEqual;
import org.junit.BeforeClass;
import org.junit.Test;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.post;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.MatcherAssert.assertThat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.progressoft.idowu.fileprocessor.model.InvalidRecord;
import com.progressoft.idowu.fileprocessor.model.ValidRecord;
import com.progressoft.idowu.fileprocessor.service.FileService;

import java.lang.reflect.Type;
import java.util.*;
import java.io.*;

public class RestTest{
	 //test file to be uploaded already in the classpath
	 private static final String FILE_NAME = "test_file1.csv";
	 

	@BeforeClass
	public static void init() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 8086;
	}


	@Test
	public void testEndpoint() {
		//assert that the endpoint is up and running
		post("/fileprocessor/path/endpoint/upload/file.csv").then()
		.assertThat().statusCode(415);
	}


	
	@Test
	public void testUpload() {
		InputStream  stream = getClass().getResourceAsStream("/" + FILE_NAME);
		
		String response = given().multiPart("file", FILE_NAME, stream).
		post("/fileprocessor/path/endpoint/upload/" + FILE_NAME).thenReturn().asString();
		try {
		stream.close();
		}catch(IOException e) {
			
		}
		Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
       Map<String, Object> data = new Gson().fromJson(response,type);
       //assert that upload was successful
       assertThat((String)data.get("responseCode"),is(equalTo("0")));
       //assert that upload happened in less than 5 seconds
       assertThat((Double)data.get("uploadTime"),is(lessThan(new Double(5000))));
	}
	
	

	
	

}
