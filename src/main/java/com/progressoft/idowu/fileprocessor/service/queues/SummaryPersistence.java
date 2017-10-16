package com.progressoft.idowu.fileprocessor.service.queues;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.persistence.EntityManager;

import com.progressoft.idowu.fileprocessor.model.DealsCount;
import com.progressoft.idowu.fileprocessor.model.ValidRecord;
import com.progressoft.idowu.fileprocessor.service.FileService;

public class SummaryPersistence implements Runnable{
	
	private org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(SummaryPersistence.class.getName());
	
	List<Map<String, Boolean>> results;
	
	public SummaryPersistence(List<Map<String, Boolean>> summary) {
		this.results = summary; 
	}

	@Override
	public void run() {
		FileService s= new FileService(ValidRecord.class);
		EntityManager em = s.getEntityManager(true);
		em.getTransaction().begin();
		log.info("doing summary for " + results.size());
		if (!results.isEmpty()) {
			for (Map<String, Boolean> result : results) {
				Set<Entry<String, Boolean>> entries = result.entrySet();
				for (Entry<String, Boolean> entry : entries) {
					if (entry.getValue()) {
						DealsCount dealCount = em.find(DealsCount.class, entry.getKey());
						if (dealCount == null) {
							dealCount = new DealsCount();
							dealCount.setOrderingCurrency(entry.getKey());
							dealCount.setDealCount(1l);
						} else {
							dealCount.setDealCount(dealCount.getDealCount() + 1l);
						}
						em.merge(dealCount);
					}
				}
			}
		}
		em.getTransaction().commit();
		em.close();
		
	}

}
