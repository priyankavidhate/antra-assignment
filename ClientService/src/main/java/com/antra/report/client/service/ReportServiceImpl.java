package com.antra.report.client.service;

import com.amazonaws.services.s3.AmazonS3;
import com.antra.report.client.pojo.reponse.PDFResponseList;
import com.antra.report.client.entity.ExcelReportEntity;
import com.antra.report.client.entity.PDFReportEntity;
import com.antra.report.client.entity.ReportRequestEntity;
import com.antra.report.client.entity.ReportStatus;
import com.antra.report.client.exception.RequestNotFoundException;
import com.antra.report.client.pojo.EmailType;
import com.antra.report.client.pojo.FileType;
import com.antra.report.client.pojo.reponse.ExcelResponse;
import com.antra.report.client.pojo.reponse.GeneralResponse;
import com.antra.report.client.pojo.reponse.PDFResponse;
import com.antra.report.client.pojo.reponse.ReportVO;
import com.antra.report.client.pojo.reponse.ReportViewObj;
import com.antra.report.client.pojo.reponse.SqsResponse;
import com.antra.report.client.pojo.request.ReportRequest;
import com.antra.report.client.repository.ReportRequestRepo;

import org.apache.logging.log4j.util.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import com.antra.report.client.pojo.reponse.ReportViewObj;
@Service
public class ReportServiceImpl implements ReportService {
	private static final Logger log = LoggerFactory.getLogger(ReportServiceImpl.class);

	private final ReportRequestRepo reportRequestRepo;
	private final SNSService snsService;
	private final AmazonS3 s3Client;
	private final EmailService emailService;
	ExecutorService pool = Executors.newFixedThreadPool(10);

	public ReportServiceImpl(ReportRequestRepo reportRequestRepo, SNSService snsService, AmazonS3 s3Client,
			EmailService emailService) {
		this.reportRequestRepo = reportRequestRepo;
		this.snsService = snsService;
		this.s3Client = s3Client;
		this.emailService = emailService;
	}

	private ReportRequestEntity persistToLocal(ReportRequest request) {
		request.setReqId("Req-" + UUID.randomUUID().toString());

		ReportRequestEntity entity = new ReportRequestEntity();
		entity.setReqId(request.getReqId());
		entity.setSubmitter(request.getSubmitter());
		entity.setDescription(request.getDescription());
		entity.setCreatedTime(LocalDateTime.now());

		PDFReportEntity pdfReport = new PDFReportEntity();
		pdfReport.setRequest(entity);
		pdfReport.setStatus(ReportStatus.PENDING);
		pdfReport.setCreatedTime(LocalDateTime.now());
		entity.setPdfReport(pdfReport);

		ExcelReportEntity excelReport = new ExcelReportEntity();
		BeanUtils.copyProperties(pdfReport, excelReport);
		entity.setExcelReport(excelReport);

		return reportRequestRepo.save(entity);
	}

	@Override
	public ReportVO generateReportsSync(ReportRequest request) {
		persistToLocal(request);
		sendDirectRequests(request);
		return new ReportVO(reportRequestRepo.findById(request.getReqId()).orElseThrow());
	}

	// TODO:Change to parallel process using Threadpool? CompletableFuture?
	private void sendDirectRequests(ReportRequest request) {
		RestTemplate rs = new RestTemplate();
		ExcelResponse excelResponseupdate = new ExcelResponse();
		PDFResponse pdfResponseupdate = new PDFResponse();

		// PDFResponse pdfResponse = new PDFResponse();
		try {
			// ExcelResponse excelResponse = new ExcelResponse();

//        	CompletableFuture<ExcelResponse> f1 = CompletableFuture.supplyAsync(() ->             
//        	excelResponse = rs.postForEntity("http://localhost:8888/excel", request, ExcelResponse.class).getBody());
			CompletableFuture<ExcelResponse> threadexcelresponse = CompletableFuture.supplyAsync(() -> {

				try {
					log.info("in threadexcel");
					ExcelResponse excelResponse1 = rs
							.postForEntity("http://localhost:8888/excel", request, ExcelResponse.class).getBody();
					return excelResponse1;
				} catch (Exception ex) {
					log.error("error");
				}
				return null;
			});
			excelResponseupdate = threadexcelresponse.get();
			log.info("hello"+excelResponseupdate);
			CompletableFuture<PDFResponse> threadpdfresponse = CompletableFuture.supplyAsync(() -> {

				try {
					log.info("hello in pdf");
					PDFResponse pdfResponse = rs.postForEntity("http://localhost:9999/pdf", request, PDFResponse.class)
							.getBody();
					return pdfResponse;
				} catch (Exception e) {
					log.error("PDF Generation Error (Sync) : e", e);

				}
				return null;
			});
			pdfResponseupdate = threadpdfresponse.get();

			// excelResponse = rs.postForEntity("http://localhost:8888/excel", request,
			// ExcelResponse.class).getBody();
		}

//        try {
//            pdfResponse = rs.postForEntity("http://localhost:9999/pdf", request, PDFResponse.class).getBody();
		catch (Exception e) {
			log.error("PDF Generation Error (Sync) : e", e);
//            pdfResponse.setReqId(request.getReqId());
//            pdfResponse.setFailed(true);
		} finally {
			updateLocal(excelResponseupdate);
			updateLocal(pdfResponseupdate);
		}
	}

	private void updateLocal(ExcelResponse excelResponse) {
		SqsResponse response = new SqsResponse();
		BeanUtils.copyProperties(excelResponse, response);
		updateAsyncExcelReport(response);
	}

	private void updateLocal(PDFResponse pdfResponse) {
		SqsResponse response = new SqsResponse();
		BeanUtils.copyProperties(pdfResponse, response);
		updateAsyncPDFReport(response);
	}

	@Override
	@Transactional
	public ReportVO generateReportsAsync(ReportRequest request) {
		ReportRequestEntity entity = persistToLocal(request);
		snsService.sendReportNotification(request);
		log.info("Send SNS the message: {}", request);
		return new ReportVO(entity);
	}

	@Override
//    @Transactional // why this? email could fail
	public void updateAsyncPDFReport(SqsResponse response) {
		ReportRequestEntity entity = reportRequestRepo.findById(response.getReqId())
				.orElseThrow(RequestNotFoundException::new);
		var pdfReport = entity.getPdfReport();
		pdfReport.setUpdatedTime(LocalDateTime.now());
		if (response.isFailed()) {
			pdfReport.setStatus(ReportStatus.FAILED);
		} else {
			pdfReport.setStatus(ReportStatus.COMPLETED);
			pdfReport.setFileId(response.getFileId());
			pdfReport.setFileLocation(response.getFileLocation());
			pdfReport.setFileSize(response.getFileSize());
		}
		entity.setUpdatedTime(LocalDateTime.now());
		reportRequestRepo.save(entity);
		String to = "priyanka05vidhate@gmail.com";
		emailService.sendEmail(to, EmailType.SUCCESS, entity.getSubmitter());
	}

	@Override
//    @Transactional
	public void updateAsyncExcelReport(SqsResponse response) {
		ReportRequestEntity entity = reportRequestRepo.findById(response.getReqId())
				.orElseThrow(RequestNotFoundException::new);
		var excelReport = entity.getExcelReport();
		excelReport.setUpdatedTime(LocalDateTime.now());
		if (response.isFailed()) {
			excelReport.setStatus(ReportStatus.FAILED);
		} else {
			excelReport.setStatus(ReportStatus.COMPLETED);
			excelReport.setFileId(response.getFileId());
			excelReport.setFileLocation(response.getFileLocation());
			excelReport.setFileSize(response.getFileSize());
		}
		entity.setUpdatedTime(LocalDateTime.now());
		reportRequestRepo.save(entity);
		String to = "priyanka05vidhate@gmail.com";
		emailService.sendEmail(to, EmailType.SUCCESS, entity.getSubmitter());
	}

	@Override
	@Transactional(readOnly = true)
	public List<ReportViewObj> getReportList() {
		List<ReportViewObj> list = new ArrayList<ReportViewObj>();
		RestTemplate restTemplate = new RestTemplate();
		PDFResponseList response = restTemplate.getForObject(
				  "http://localhost:9999/pdf/getAllFiles",
				  PDFResponseList.class);
		List<com.antra.report.client.pojo.reponse.PDFResponse> pdfResponseList = response.getPdfResponseList();
		for(com.antra.report.client.pojo.reponse.PDFResponse pdfResponse :pdfResponseList) {
			log.info("in report controller");
			log.info(pdfResponse.toString());
			ReportViewObj reportViewObj = new ReportViewObj();
			reportViewObj.setId(pdfResponse.getFileId());
			reportViewObj.setFileName(pdfResponse.getFileName());
		    reportViewObj.setGeneratedTime(pdfResponse.getGeneratedTime());
			reportViewObj.setStatus(pdfResponse.getStatus());
			reportViewObj.setDescription(pdfResponse.getDescription());
			reportViewObj.setSubmitter(pdfResponse.getSubmitter());
			list.add(reportViewObj);
		}
		log.info("size of list is "+list.size());
		return list;
	}

	@Override
	public InputStream getFileBodyByReqId(String reqId, FileType type) {
		log.info("req id ----" + reqId);
		// ReportRequestEntity entity =
		// reportRequestRepo.findById(reqId).orElseThrow(RequestNotFoundException::new);
		if (type == FileType.PDF) {
//            String fileLocation = entity.getPdfReport().getFileLocation(); // this location is s3 "bucket/key"
//            String bucket = fileLocation.split("/")[0];
//            String key = fileLocation.split("/")[1];
			return s3Client.getObject("reporting-generated-file-priyanka", "File-29b8babd-5a2f-487a-b686-ebe581d91453")
					.getObjectContent();
		} else if (type == FileType.EXCEL) {
			// String fileId = entity.getExcelReport().getFileId();
//            String fileLocation = entity.getExcelReport().getFileLocation();
//            try {
//                return new FileInputStream(fileLocation);// this location is in local, definitely sucks
//            } catch (FileNotFoundException e) {
//                log.error("No file found", e);
//            }
			RestTemplate restTemplate = new RestTemplate();
//            InputStream is = restTemplate.execute(, HttpMethod.GET, null, ClientHttpResponse::getBody, fileId);
			ResponseEntity<Resource> exchange = restTemplate.exchange("http://localhost:8888/excel/{id}/content",
					HttpMethod.GET, null, Resource.class, reqId);
			try {
				return exchange.getBody().getInputStream();
			} catch (IOException e) {
				log.error("Cannot download excel", e);
			}
		}
		return null;
	}

	@Override
	public ResponseEntity<GeneralResponse> deleteFile(String fileId) {
		log.info("in pdf delete");
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Boolean> exchange = restTemplate.exchange("http://localhost:9999/pdf/delete/" + fileId,
				HttpMethod.DELETE, new HttpEntity<String>("some sample body sent along the DELETE request"),
				Boolean.class);
		log.info(String.valueOf(exchange.getStatusCode()));
		return ResponseEntity.ok(new GeneralResponse());
	}
}
