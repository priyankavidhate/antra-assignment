package com.antra.evaluation.reporting_system.service;

import java.util.List;

import com.antra.evaluation.reporting_system.pojo.api.PDFRequest;
import com.antra.evaluation.reporting_system.pojo.report.PDFFil;
import com.antra.evaluation.reporting_system.pojo.report.PDFFile;

public interface PDFService {
	PDFFil createPDF(PDFRequest request);

	boolean deletePDF(String reqId);

	boolean updatePDF(String reqId, PDFRequest request);
	
	PDFFil getPDF(String reqId);

	List<PDFFil> getAllFiles();
}
