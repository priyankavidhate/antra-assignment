package com.antra.evaluation.reporting_system.service;

import java.io.IOException;
import java.util.List;

import com.antra.evaluation.reporting_system.pojo.api.ExcelRequest;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFil;

public interface ExcelFileService {
	ExcelFil createExcel(ExcelRequest request, boolean multisheet);

	boolean deleteExcel(String reqId);

	boolean updateExcel(String reqId,ExcelRequest request) throws IOException;

	ExcelFil getExcel(String reqId);

	boolean deleteFileFromBucket(final String keyName);

	List<ExcelFil> getAllFiles();

}
