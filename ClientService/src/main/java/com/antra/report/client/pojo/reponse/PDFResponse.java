package com.antra.report.client.pojo.reponse;

import java.time.LocalDateTime;

public class PDFResponse {
    private String fileId;
  
	private String reqId;
    private String fileLocation;
    private long fileSize;
    private boolean failed;
    private String submitter;
    private String status;
    private LocalDateTime generatedTime;
	private String fileName;
    private String description;
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
    public String getSubmitter() {
		return submitter;
	}
	public void setSubmitter(String submitter) {
		this.submitter = submitter;
	}
	public void setGeneratedTime(LocalDateTime generatedTime) {
		this.generatedTime = generatedTime;
	}
	public String getStatus() {
		return status;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public LocalDateTime getGeneratedTime() {
		return generatedTime;
	}
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	@Override
	public String toString() {
		return "PDFResponse [fileId=" + fileId + ", reqId=" + reqId + ", fileLocation=" + fileLocation + ", fileSize="
				+ fileSize + ", failed=" + failed + ", submitter=" + submitter + ", status=" + status
				+ ", generatedTime=" + generatedTime + ", fileName=" + fileName + ", description=" + description + "]";
	}
}
