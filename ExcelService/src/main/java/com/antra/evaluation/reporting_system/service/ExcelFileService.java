package com.antra.evaluation.reporting_system.service;

import java.io.IOException;
import java.util.List;

import com.antra.evaluation.reporting_system.pojo.api.ExcelRequest;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFil;

public interface ExcelFileService {
	ExcelFil createExcel(ExcelRequest request,boolean multisheet);
	ExcelFil deleteExcel(String fileId);
	boolean updateExcel(String fileId, ExcelRequest request) throws IOException;
    ExcelFil getExcel(String fileId);  
    boolean deleteFileFromBucket(final String keyName);
	List<ExcelFil> getAllFiles();
        
}
