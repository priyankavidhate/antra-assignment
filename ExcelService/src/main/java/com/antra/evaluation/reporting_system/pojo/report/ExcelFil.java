package com.antra.evaluation.reporting_system.pojo.report;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;

//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;

@DynamoDBTable(tableName = "ExcelRecord")

public class ExcelFil {
	private String reqId;
	private String id;
	private String fileName;
	private String fileLocation;
	private String submitter;
	private Long fileSize;
	private String description;
	private LocalDateTime generatedTime;
	private String status;
	
	public ExcelFil() {
	}

	@DynamoDBAttribute(attributeName = "generatedTime")
	@DynamoDBTypeConverted(converter = LocalTimeConverter.class)

	public LocalDateTime getGeneratedTime() {
		return generatedTime;
	}

	public void setGeneratedTime(LocalDateTime generatedTime) {
		this.generatedTime = generatedTime;
	}

	@DynamoDBAttribute(attributeName = "filestatus")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@DynamoDBTypeConverted(converter = StudentLevelRecordTrailConverter.class)
	@DynamoDBAttribute(attributeName = "data")
	private StudentRecordTrail studentLevelRecordTrail;

	public StudentRecordTrail getStudentLevelRecordTrail() {
		return studentLevelRecordTrail;
	}

	public void setStudentLevelRecordTrail(StudentRecordTrail studentLevelRecordTrail) {
		this.studentLevelRecordTrail = studentLevelRecordTrail;
	}


	@DynamoDBHashKey(attributeName = "reqId")
	public String getReqId() {
		return reqId;
	}

	public void setReqId(String reqId) {
		this.reqId = reqId;
	}
	
	@DynamoDBAttribute(attributeName = "fileId")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@DynamoDBAttribute(attributeName = "fileName")
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@DynamoDBAttribute(attributeName = "fileLocation")
	public String getFileLocation() {
		return fileLocation;
	}

	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}

	@DynamoDBAttribute(attributeName = "submitter")
	public String getSubmitter() {
		return submitter;
	}

	public void setSubmitter(String submitter) {
		this.submitter = submitter;
	}

	@DynamoDBAttribute(attributeName = "fileSize")
	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	@DynamoDBAttribute(attributeName = "description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	

	@Override
	public String toString() {
		return "ExcelFil [id=" + id + ", fileName=" + fileName + ", fileLocation=" + fileLocation + ", submitter="
				+ submitter + ", fileSize=" + fileSize + ", description=" + description + ", generatedTime="
				+ generatedTime + ", status=" + status + ", studentLevelRecordTrail=" + studentLevelRecordTrail + "]";
	}

}