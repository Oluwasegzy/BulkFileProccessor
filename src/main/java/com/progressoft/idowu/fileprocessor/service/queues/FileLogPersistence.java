package com.progressoft.idowu.fileprocessor.service.queues;

import java.util.List;

import javax.persistence.EntityManager;

import org.apache.log4j.Level;

import com.progressoft.idowu.fileprocessor.model.BaseRecord;
import com.progressoft.idowu.fileprocessor.model.UploadedFileLog;
import com.progressoft.idowu.fileprocessor.model.ValidRecord;
import com.progressoft.idowu.fileprocessor.service.FileService;

public class FileLogPersistence implements Runnable {

	private org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(FileLogPersistence.class.getName());

	String fileName;

	public FileLogPersistence(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public void run() {
		FileService s = new FileService(UploadedFileLog.class);
	        
			EntityManager em = s.getEntityManager(true);
			em.getTransaction().begin();
			UploadedFileLog fileLog = new UploadedFileLog();
			fileLog.setFileName(fileName);
			em.persist(fileLog);
			em.getTransaction().commit();
			em.close();
		}
	

}
