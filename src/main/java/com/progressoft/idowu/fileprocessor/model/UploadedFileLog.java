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
@Table(name = "FILEPROCESSOR_UPLOADED_FILE_LOG", indexes = {
		  @Index(name = "fileNameIndex", columnList = "file_name")})
public class UploadedFileLog implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Column(name = "file_name")
	private String fileName;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created")
	private Date created = new Date();
	public Long getId() {
		return this.id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFileName() {
		return this.fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	
  
	
	
	
	

}
