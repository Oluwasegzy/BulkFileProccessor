/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.progressoft.idowu.fileprocessor.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Oluwasegun.Idowu
 */
@Entity
@Table(name = "FILEPROCESSOR_DEALS_COUNT")
public class DealsCount implements Serializable {
	@Id
	@Column(name = "ordering_currency", length = 30)
	private String orderingCurrency;
	@Column(name = "deal_count")
	private Long dealCount;
	

	public String getOrderingCurrency() {
		return this.orderingCurrency;
	}
	public void setOrderingCurrency(String orderingCurrency) {
		this.orderingCurrency = orderingCurrency;
	}
	public Long getDealCount() {
		return this.dealCount;
	}
	public void setDealCount(Long dealCount) {
		this.dealCount = dealCount;
	}
	
	
	

}
