package com.progressoft.idowu.fileprocessor.service.queues;

import java.util.List;

import javax.persistence.EntityManager;

import org.apache.log4j.Level;

import com.progressoft.idowu.fileprocessor.model.BaseRecord;
import com.progressoft.idowu.fileprocessor.model.ValidRecord;
import com.progressoft.idowu.fileprocessor.service.FileService;

public class FileLinePersistence implements Runnable {

	private org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(FileLinePersistence.class.getName());

	List<BaseRecord> baseRecords;
	static final int MAX_RECORDS = 500;

	public FileLinePersistence(List<BaseRecord> records) {
		this.baseRecords = records;
	}

	@Override
	public void run() {
		FileService s = new FileService(ValidRecord.class);
		

		int start = 0;
		double benCount = Math.ceil(Double.parseDouble(String.valueOf(baseRecords.size()))
				/ Double.parseDouble(String.valueOf(MAX_RECORDS)));
		for (int i = 1; i <= benCount; i++) {
			EntityManager em = s.getEntityManager(true);
			int end = MAX_RECORDS * i;
			if (end > baseRecords.size()) {
				end = baseRecords.size();
			}
			log.info(" Start " + start + "   end " + end);
			em.getTransaction().begin();
			for (BaseRecord record : this.baseRecords.subList(start, end)) {
				em.persist(record);
			}
			em.getTransaction().commit();
			em.close();
			start = (MAX_RECORDS * i);
		}
	}

}
