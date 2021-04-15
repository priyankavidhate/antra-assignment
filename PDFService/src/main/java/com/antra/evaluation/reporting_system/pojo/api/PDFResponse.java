package com.antra.evaluation.reporting_system.pojo.api;

import java.time.LocalDateTime;

//import org.springframework.data.mongodb.core.mapping.Document;

//@Document
public class PDFResponse {

    private String fileId;
	private String reqId;
    private String fileName;
    private String fileLocation;
    private long fileSize;
    private boolean failed;
    private LocalDateTime generatedTime;
    private String submitter;
    private String description;
    private String status;
    
    public PDFResponse() {}
    
    public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
    
	public String getSubmitter() {
		return submitter;
	}

	public void setSubmitter(String submitter) {
		this.submitter = submitter;
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

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getReqId() {
        return reqId;
    }

    public void setReqId(String reqId) {
        this.reqId = reqId;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public boolean isFailed() {
        return failed;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }
    
    @Override
	public String toString() {
		return "PDFResponse [fileId=" + fileId + ", reqId=" + reqId + ", fileName=" + fileName + ", fileLocation="
				+ fileLocation + ", fileSize=" + fileSize + ", failed=" + failed + ", generatedTime=" + generatedTime
				+ ", submitter=" + submitter + ", description=" + description + ", status=" + status + "]";
	}

}
