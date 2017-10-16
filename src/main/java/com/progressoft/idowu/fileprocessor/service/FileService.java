/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progressoft.idowu.fileprocessor.service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;

import com.progressoft.idowu.fileprocessor.dao.DaoImpl;
import com.progressoft.idowu.fileprocessor.model.BaseRecord;
import com.progressoft.idowu.fileprocessor.model.DealsCount;
import com.progressoft.idowu.fileprocessor.model.InvalidRecord;
import com.progressoft.idowu.fileprocessor.model.UploadedFileLog;
import com.progressoft.idowu.fileprocessor.model.ValidRecord;
import com.progressoft.idowu.fileprocessor.service.queues.FileLinePersistence;
import com.progressoft.idowu.fileprocessor.service.queues.FileLogPersistence;
import com.progressoft.idowu.fileprocessor.service.queues.SummaryPersistence;
import com.progressoft.idowu.fileprocessor.ws.RestPath;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

/**
 *
 * @author Oluwasegun Idowu
 */
public class FileService extends DaoImpl<DealsCount> {

	private static final char DEFAULT_SEPARATOR = ',';
	private static final char DEFAULT_QUOTE = '"';
	private org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(FileService.class.getName());

	public FileService(Class claszz) {
		super(claszz);
	}

	public long upload(InputStream stream, String fileName) throws Exception {
		Calendar cal = Calendar.getInstance();
		long startMilliSec = cal.getTimeInMillis();
		log.info("entity manager " + em);
		em = this.getEntityManager(true);
		List<UploadedFileLog> list = em.createQuery("select e from UploadedFileLog e where e.fileName=:fileName")
				.setParameter("fileName", fileName).getResultList();
		if (!list.isEmpty()) {
			em.close();
			throw new Exception("File '" + fileName + "' already uploaded");

		}
		log.info("here...");

		Reader in = new InputStreamReader(stream);
		CsvParserSettings settings = new CsvParserSettings();
		settings.getFormat().setLineSeparator("\n");
		CsvParser parser = new CsvParser(settings);
		List<String[]> records = parser.parseAll(in);
		// for (CSVRecord record : records) {
		ExecutorService executor = Executors.newCachedThreadPool();
		CompletionService<List<Map<String, Boolean>>> completionService = new ExecutorCompletionService<>(executor);
		completionService.submit(new FileProcessCallable(records, fileName));
		
		
		log.info("starting other stuff ...");
		Future<List<Map<String, Boolean>>> f = completionService.take();
		List<Map<String, Boolean>> results = f.get();
	 	new Thread(new SummaryPersistence(results)).start();
		log.info("ending other stuff ...");
		new Thread(new FileLogPersistence(fileName)).start();
		
		in.close();
		stream.close();
		return Calendar.getInstance().getTimeInMillis() - startMilliSec;
	}


	public List<ValidRecord> retrieveValid(String fileName) throws Exception {
		em = getEntityManager(true);
		return em.createQuery("select v from ValidRecord v where v.sourceFile=:sourceFile")
				.setParameter("sourceFile", fileName).getResultList();
	}

	public List<InvalidRecord> retrieveInvalid(String fileName) throws Exception {
		em = getEntityManager(true);
		return em.createQuery("select v from InvalidRecord v where v.sourceFile=:sourceFile")
				.setParameter("sourceFile", fileName).getResultList();
	}
	
	public List<UploadedFileLog> retrieveFileLog(String fileName) throws Exception {
		em = getEntityManager(true);
		return em.createQuery("select v from UploadedFileLog v where v.fileName=:sourceFile")
				.setParameter("sourceFile", fileName).getResultList();
	}

	public static List<String> parseLine(String cvsLine) {
		return parseLine(cvsLine, DEFAULT_SEPARATOR, DEFAULT_QUOTE);
	}

	public static List<String> parseLine(String cvsLine, char separators, char customQuote) {

		List<String> result = new ArrayList<>();

		// if empty, return!
		if (cvsLine == null && cvsLine.isEmpty()) {
			return result;
		}

		if (customQuote == ' ') {
			customQuote = DEFAULT_QUOTE;
		}

		if (separators == ' ') {
			separators = DEFAULT_SEPARATOR;
		}

		StringBuffer curVal = new StringBuffer();
		boolean inQuotes = false;
		boolean startCollectChar = false;
		boolean doubleQuotesInColumn = false;

		char[] chars = cvsLine.toCharArray();

		for (char ch : chars) {

			if (inQuotes) {
				startCollectChar = true;
				if (ch == customQuote) {
					inQuotes = false;
					doubleQuotesInColumn = false;
				} else // Fixed : allow "" in custom quote enclosed
				{
					if (ch == '\"') {
						if (!doubleQuotesInColumn) {
							curVal.append(ch);
							doubleQuotesInColumn = true;
						}
					} else {
						curVal.append(ch);
					}
				}
			} else if (ch == customQuote) {

				inQuotes = true;

				// Fixed : allow "" in empty quote enclosed
				if (chars[0] != '"' && customQuote == '\"') {
					curVal.append('"');
				}

				// double quotes in column will hit this!
				if (startCollectChar) {
					curVal.append('"');
				}

			} else if (ch == separators) {

				result.add(curVal.toString());

				curVal = new StringBuffer();
				startCollectChar = false;

			} else if (ch == '\r') {
				// ignore LF characters
				continue;
			} else if (ch == '\n') {
				// the end, break!
				break;
			} else {
				curVal.append(ch);
			}

		}

		result.add(curVal.toString());

		return result;
	}


	class FileProcessCallable implements Callable<List<Map<String, Boolean>>> {

		private String fileName;
		private List<String[]> allLines;
		private final int BATCH_INSERT_RECORD = 30000;

		FileProcessCallable(List<String[]> allLines, String file) {
			this.allLines = allLines;
			this.fileName = file;
		}

		@Override
		public List<Map<String, Boolean>> call() throws Exception {
			BaseRecord baseRecord = null;
			List<Map<String, Boolean>> results = new ArrayList<>();
			Calendar cal = Calendar.getInstance();
			List<BaseRecord> recordstoPersist = new ArrayList<>();
			log.info("records ...." + allLines.size());
			int count = 0;
			for (String[] line : allLines) {
				if(count<5) {
					count++;
					continue;
				}
				
				Date timeStamp=null;
				try {
				cal.setTimeInMillis(new Long(line[3]));
			
				timeStamp = cal.getTime();
				}catch(Exception e) {
					
				}
				BigDecimal amount=null;
				try {
					amount = new BigDecimal(line[4]);
				}catch(Exception e) {
					
				}
				if (timeStamp==null || amount==null) {
					
					baseRecord = new InvalidRecord();
					((InvalidRecord)baseRecord).setSourceFile(fileName);
					try {
						((InvalidRecord)baseRecord).setDealAmount(line[4]);
					} catch (Exception e) {
					}
					try {
		
						((InvalidRecord)baseRecord).setDealTime(line[3]);
					} catch (Exception e) {
					}
					try {
						((InvalidRecord)baseRecord).setDealUniqueId(line[0]);
					} catch (Exception e) {

					}
					try {
						((InvalidRecord)baseRecord).setFromIsoCode(line[1]);
					} catch (Exception e) {

					}
					try {
						((InvalidRecord)baseRecord).setToIsoCode(line[2]);
					} catch (Exception e) {

					}

				} else {
					log.info("valid record ");
					baseRecord = new ValidRecord();

					((ValidRecord)baseRecord).setSourceFile(fileName);
					((ValidRecord)baseRecord).setDealAmount(amount);
					((ValidRecord)baseRecord).setDealTime(timeStamp);
					((ValidRecord)baseRecord).setDealUniqueId(line[0]);
					((ValidRecord)baseRecord).setFromIsoCode(line[1]);
					((ValidRecord)baseRecord).setToIsoCode(line[2]);
					Map<String, Boolean> result = new HashMap<>();
					result.put(line[1], true);
					results.add(result);

				}
			    	recordstoPersist.add(baseRecord);
			    
			}
			int start = 0;
			double benCount = Math.ceil(Double.parseDouble(String.valueOf(recordstoPersist.size()))
					/ Double.parseDouble(String.valueOf(BATCH_INSERT_RECORD)));
			for (int i = 1; i <= benCount; i++) {
				int end = BATCH_INSERT_RECORD * i;
				if (end > recordstoPersist.size()) {
					end = recordstoPersist.size();
				}
				new Thread(new FileLinePersistence(recordstoPersist.subList(start, end))).start();
				start = (BATCH_INSERT_RECORD * i);
			}
			log.info(benCount + " persisters started...");
			return results;

		}

	}

}
