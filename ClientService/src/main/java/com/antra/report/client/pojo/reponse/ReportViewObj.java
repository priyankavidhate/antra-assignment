package com.antra.report.client.pojo.reponse;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.antra.evaluation.reporting_system.service.PDFServiceImpl;

public class ReportViewObj {
    private static final Logger log = LoggerFactory.getLogger(ReportViewObj.class);

	 private String id;
	 private String fileName;
	    private String fileLocation;
	    private String submitter;
	    private Long fileSize;
	    private String description;
	    private LocalDateTime generatedTime;
	    private String status;
	    public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileLocation() {
		return fileLocation;
	}
	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}
	public String getSubmitter() {
		return submitter;
	}
	public void setSubmitter(String submitter) {
		this.submitter = submitter;
	}
	public Long getFileSize() {
		return fileSize;
	}
	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}
	public String getDescription() {
		log.info("get description: "+description);
		return description;
	}
	public void setDescription(String description) {
		
		this.description = description;
		log.info("get description: "+description);

	}
	public LocalDateTime getGeneratedTime() {
		return generatedTime;
	}
	public void setGeneratedTime(LocalDateTime generatedTime) {
		this.generatedTime = generatedTime;
	}
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
