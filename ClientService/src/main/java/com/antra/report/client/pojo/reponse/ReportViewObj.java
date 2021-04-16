package com.antra.report.client.pojo.reponse;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.antra.evaluation.reporting_system.service.PDFServiceImpl;

public class ReportViewObj {
	private static final Logger log = LoggerFactory.getLogger(ReportViewObj.class);

	private String reqId;
	private String pdfFileId;
	private String pdfFileName;
	private String pdfFileLocation;
	private String submitter;
	private Long pdfFileSize;
	private String description;
	private LocalDateTime generatedTime;
	private String pdfStatus;
	
	private String excelFileId;
	private String excelFileName;
	private String excelFileLocation;
	private Long excelFileSize;
	private String excelStatus;
	
	private List<Student> fileData;
	
	
	public String getPdfFileId() {
		return pdfFileId;
	}
	public void setPdfFileId(String pdfFileId) {
		this.pdfFileId = pdfFileId;
	}
	public String getExcelFileId() {
		return excelFileId;
	}
	public void setExcelFileId(String excelFileId) {
		this.excelFileId = excelFileId;
	}
	
	public String getReqId() {
		return reqId;
	}
	public void setReqId(String reqId) {
		this.reqId = reqId;
	}
	public String getPdfFileName() {
		return pdfFileName;
	}
	public void setPdfFileName(String pdfFileName) {
		this.pdfFileName = pdfFileName;
	}
	public String getPdfFileLocation() {
		return pdfFileLocation;
	}
	public void setPdfFileLocation(String pdfFileLocation) {
		this.pdfFileLocation = pdfFileLocation;
	}
	public String getSubmitter() {
		return submitter;
	}
	public void setSubmitter(String submitter) {
		this.submitter = submitter;
	}
	public Long getPdfFileSize() {
		return pdfFileSize;
	}
	public void setPdfFileSize(Long pdfFileSize) {
		this.pdfFileSize = pdfFileSize;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public LocalDateTime getGeneratedTime() {
		return generatedTime;
	}
	public void setGeneratedTime(LocalDateTime generatedTime) {
		this.generatedTime = generatedTime;
	}
	public String getPdfStatus() {
		return pdfStatus;
	}
	public void setPdfStatus(String pdfStatus) {
		this.pdfStatus = pdfStatus;
	}
	public String getExcelFileName() {
		return excelFileName;
	}
	public void setExcelFileName(String excelFileName) {
		this.excelFileName = excelFileName;
	}
	public String getExcelFileLocation() {
		return excelFileLocation;
	}
	public void setExcelFileLocation(String excelFileLocation) {
		this.excelFileLocation = excelFileLocation;
	}
	public Long getExcelFileSize() {
		return excelFileSize;
	}
	public void setExcelFileSize(Long excelFileSize) {
		this.excelFileSize = excelFileSize;
	}
	public String getExcelStatus() {
		return excelStatus;
	}
	public void setExcelStatus(String excelStatus) {
		this.excelStatus = excelStatus;
	}
	public static Logger getLog() {
		return log;
	}
	public List<Student> getFileData() {
		return fileData;
	}
	public void setFileData(List<Student> fileData) {
		this.fileData = fileData;
	}
}
