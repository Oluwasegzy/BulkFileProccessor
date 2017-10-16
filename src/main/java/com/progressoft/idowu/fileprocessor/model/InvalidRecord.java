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
import javax.persistence.Index;
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
@Table(name = "FILEPROCESSOR_INVALID", indexes = {
		  @Index(name = "fileNameIndex", columnList = "source_file")})
public class InvalidRecord extends BaseRecord implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Column(name = "deal_unique_id")
	private String dealUniqueId;
	@Column(name = "from_iso_code")
	private String fromIsoCode;
	@Column(name = "to_iso_code")
	private String toIsoCode;
	@Column(name = "source_file")
	private String sourceFile;
	@Column(name = "deal_amount")
	private String dealAmount;
	@Column(name = "deal_time")
	private String dealTime;
	
	public Long getId() {
		return this.id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDealUniqueId() {
		return this.dealUniqueId;
	}
	public void setDealUniqueId(String dealUniqueId) {
		this.dealUniqueId = dealUniqueId;
	}
	public String getFromIsoCode() {
		return this.fromIsoCode;
	}
	public void setFromIsoCode(String fromIsoCode) {
		this.fromIsoCode = fromIsoCode;
	}
	public String getToIsoCode() {
		return this.toIsoCode;
	}
	public void setToIsoCode(String toIsoCode) {
		this.toIsoCode = toIsoCode;
	}
	public String getDealAmount() {
		return this.dealAmount;
	}
	public void setDealAmount(String dealAmount) {
		this.dealAmount = dealAmount;
	}
	public String getDealTime() {
		return this.dealTime;
	}
	public void setDealTime(String dealTime) {
		this.dealTime = dealTime;
	}
	public String getSourceFile() {
		return this.sourceFile;
	}
	public void setSourceFile(String sourceFile) {
		this.sourceFile = sourceFile;
	}
	
	
	

}
