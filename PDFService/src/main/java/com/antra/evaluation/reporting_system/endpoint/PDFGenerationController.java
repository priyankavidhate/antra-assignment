package com.antra.evaluation.reporting_system.endpoint;

import com.antra.evaluation.reporting_system.pojo.api.PDFRequest;
import com.antra.evaluation.reporting_system.pojo.api.PDFResponse;
import com.antra.evaluation.reporting_system.pojo.api.PDFResponseList;
import com.antra.evaluation.reporting_system.pojo.report.PDFFil;
import com.antra.evaluation.reporting_system.pojo.report.PDFFile;
import com.antra.evaluation.reporting_system.service.PDFService;

import java.awt.List;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PDFGenerationController {

	private static final Logger log = LoggerFactory.getLogger(PDFGenerationController.class);

	private PDFService pdfService;

	@Autowired
	public PDFGenerationController(PDFService pdfService) {
		this.pdfService = pdfService;
	}

	@PostMapping("/pdf")
	public ResponseEntity<PDFResponse> createPDF(@RequestBody @Validated PDFRequest request) {
		log.info("Got request to generate PDF: {}", request);

		PDFResponse response = new PDFResponse();
		PDFFil file = null;
		response.setReqId(request.getReqId());

		try {
			file = pdfService.createPDF(request);
			response.setFileId(file.getId());
			response.setFileLocation(file.getFileLocation());
			response.setFileSize(file.getFileSize());
			log.info("Generated: {}", file);
		} catch (Exception e) {
			response.setFailed(true);
			log.error("Error in generating pdf", e);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PutMapping("/pdf/{id}")
	public ResponseEntity<Boolean> updateExcel(@PathVariable String id, @RequestBody @Validated PDFRequest request) {
		log.info("Got request to update excel: {}", request);

		PDFResponse response = new PDFResponse();
		response.setReqId(request.getReqId());
		boolean reponse = true;

		try {
			pdfService.updatePDF(id, request);
			
		} 
		catch (Exception e) {
			reponse = false;
		}
		return new ResponseEntity<>(reponse, HttpStatus.OK);
	}

	@DeleteMapping("/pdf/{id}")
	public ResponseEntity<Boolean> deletePDF(@PathVariable("id") String reqId) {
		log.info("deletePDF :" + reqId);
		boolean response = pdfService.deletePDF(reqId);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}


	@GetMapping("/pdf/{id}")
	public PDFFil getPDF(@PathVariable("id") final String id) {
		return pdfService.getPDF(id);
	}

	@GetMapping("/pdf/getAllFiles")
	public PDFResponseList getAllPDFFiles() {
		log.info("in getAllPDFFiles");
		ArrayList<PDFFil> list = new ArrayList<PDFFil>();
		list = (ArrayList<PDFFil>) pdfService.getAllFiles();
		ArrayList<PDFResponse> pdfResponseList = new ArrayList<PDFResponse>();
		for (PDFFil pdfFil : list) {
			PDFResponse pdfResponse = new PDFResponse();
			pdfResponse.setFileId(pdfFil.getId());
			pdfResponse.setFileSize(pdfFil.getFileSize());
			pdfResponse.setGeneratedTime(pdfFil.getGeneratedTime());
			pdfResponse.setSubmitter(pdfFil.getSubmitter());
			pdfResponse.setStatus(pdfFil.getStatus());
			pdfResponse.setDescription(pdfFil.getDescription());
			pdfResponse.setFileLocation(pdfFil.getFileLocation());
			pdfResponse.setFileName(pdfFil.getFileName());
			pdfResponse.setReqId(pdfFil.getReqId());
			pdfResponse.setData(pdfFil.getStudentLevelRecordTrail().getThisStudentLevelRecords());
			log.info("in  pdf getfiles status " + pdfResponse.getStatus());
			pdfResponseList.add(pdfResponse);
			log.info("getAllPDFFiles, submitter : " + pdfFil.getSubmitter());
			log.info("in getAllPDFFiles, pdfResponse :" + pdfResponse.toString());
		}
		PDFResponseList pdfResList = new PDFResponseList();
		pdfResList.setPdfResponseList(pdfResponseList);

		return pdfResList;
	}
}
