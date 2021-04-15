package com.antra.evaluation.reporting_system.service;

import java.util.List;

import com.antra.evaluation.reporting_system.pojo.api.PDFRequest;
import com.antra.evaluation.reporting_system.pojo.report.PDFFil;
import com.antra.evaluation.reporting_system.pojo.report.PDFFile;

public interface PDFService {
    PDFFil createPDF(PDFRequest request);
    boolean deletePDF(String fileId);
	boolean updatePDF(String fileId, PDFRequest request);
    PDFFil getPDF(String fileId);
    List<PDFFil> getAllFiles();  
}
