package com.antra.evaluation.reporting_system.endpoint;

import com.antra.evaluation.reporting_system.exception.FileGenerationException;
import com.antra.evaluation.reporting_system.pojo.api.ErrorResponse;
import com.antra.evaluation.reporting_system.pojo.api.ExcelRequest;
import com.antra.evaluation.reporting_system.pojo.api.ExcelResponse;
import com.antra.evaluation.reporting_system.pojo.api.MultiSheetExcelRequest;
import com.antra.evaluation.reporting_system.pojo.api.ExcelResponseList;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFil;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;
import com.antra.evaluation.reporting_system.repo.ExcelFileRepository;
import com.antra.evaluation.reporting_system.service.ExcelFileService;
import com.antra.evaluation.reporting_system.service.ExcelService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ExcelGenerationController {

	private static final Logger log = LoggerFactory.getLogger(ExcelGenerationController.class);
	private static final String DOWNLOAD_API_URI = "/excel/{id}/content";

	ExcelService excelService;
	ExcelFileService excelfileservice;

	@Autowired
	public ExcelGenerationController(ExcelService excelService, ExcelFileService excelFileService) {
		this.excelService = excelService;
		this.excelfileservice = excelFileService;
	}

	@PostMapping("/excel")
	@ApiOperation("Generate Excel")
	public ResponseEntity<ExcelResponse> createExcel(@RequestBody @Validated ExcelRequest request) {
		log.info("Got request to generate excel: {}", request);

		ExcelResponse response = new ExcelResponse();
		ExcelFil file = null;
		response.setReqId(request.getReqId());

		try {
			file = excelfileservice.createExcel(request, false);
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
	
	
	@PutMapping("/excel/{id}")
	public ResponseEntity<Boolean> updateExcel(@PathVariable String id, @RequestBody @Validated ExcelRequest request) {
		log.info("Got request to update excel: {}", request);

		ExcelResponse response = new ExcelResponse();
		response.setReqId(request.getReqId());
		boolean reponse = true;

		try {
			excelfileservice.updateExcel(id, request);
			
		} 
		catch (Exception e) {
			reponse = false;
		}
		return new ResponseEntity<>(reponse, HttpStatus.OK);
	}

//	@PutMapping("/excel/{id}")
//	public boolean update(@PathVariable("id") final String reqId, @RequestBody ExcelRequest request) throws IOException {
//		return excelfileservice.updateExcel(reqId,request);
//	}

	private String generateFileDownloadLink(String fileId) {
		UriComponents uriComponents = UriComponentsBuilder.newInstance().scheme("http").host("localhost:8080")
				.path(DOWNLOAD_API_URI) // localhost:8080 need to be externalized as parameter
				.buildAndExpand(fileId);
		return uriComponents.toUriString();
	}

	@PostMapping("/excel/auto")
	@ApiOperation("Generate Multi-Sheet Excel Using Split field")
	public ResponseEntity<ExcelResponse> createMultiSheetExcel(@RequestBody @Validated MultiSheetExcelRequest request) {
		log.debug("Got Request to Create Multi-Sheet Excel:{}", request);
		// Double check if the header has splitBy field.
		if (!request.getHeaders().contains(request.getSplitBy())) {
			throw new InvalidParameterException("No such header for splitting the sheets");
		}
		ExcelFile fileInfo = excelService.generateFile(request, true);
		ExcelResponse response = new ExcelResponse();
		BeanUtils.copyProperties(fileInfo, response);
		response.setFileLocation(this.generateFileDownloadLink(fileInfo.getFileId()));
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/excel/getAllFiles")
	public ExcelResponseList getAllExcelFiles() {
		ArrayList<ExcelFil> list = new ArrayList<ExcelFil>();
		list = (ArrayList<ExcelFil>) excelfileservice.getAllFiles();
		ArrayList<ExcelResponse> excelResponseList = new ArrayList<ExcelResponse>();
		for (ExcelFil excelFil : list) {
			ExcelResponse excelResponse = new ExcelResponse();
			excelResponse.setFileId(excelFil.getId());
			excelResponse.setFileSize(excelFil.getFileSize());
			excelResponse.setGeneratedTime(excelFil.getGeneratedTime());
			excelResponse.setSubmitter(excelFil.getSubmitter());
			excelResponse.setStatus(excelFil.getStatus());
			excelResponse.setDescription(excelFil.getDescription());
			excelResponse.setFileLocation(excelFil.getFileLocation());
			excelResponse.setFileName(excelFil.getFileName());
			excelResponse.setReqId(excelFil.getReqId());
			log.info("in  excel getfiles status " + excelResponse.getStatus());
			excelResponseList.add(excelResponse);
		}
		ExcelResponseList excelResList = new ExcelResponseList();
		excelResList.setExcelfileResponseList(excelResponseList);

		return excelResList;
	}

	@GetMapping(DOWNLOAD_API_URI)
	public void downloadExcel(@PathVariable String id, HttpServletResponse response) throws IOException {
		log.debug("Got Request to Download File:{}", id);
		InputStream fis = excelService.getExcelBodyById(id);
		response.setHeader("Content-Type", "application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment; filename=\"name_of_excel_file.xls\""); // TODO: File name
																										// cannot be
																										// hardcoded
																										// here
		FileCopyUtils.copy(fis, response.getOutputStream());
		log.debug("Downloaded File:{}", id);
	}

	@DeleteMapping("/excel/{id}")
	public ResponseEntity<Boolean> deleteExcel(@PathVariable String id) throws FileNotFoundException {
		log.debug("Got Request to Delete excel File:{}", id);
		boolean response = excelfileservice.deleteExcel(id);
//		BeanUtils.copyProperties(fileDeleted, response);
//		response.setFileLocation(this.generateFileDownloadLink(fileDeleted.getId()));
//		log.debug("File Deleted:{}", fileDeleted);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@ExceptionHandler(FileNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleFileNotFound(Exception e) {
		log.error("The file doesn't exist", e);
		return new ResponseEntity<>(new ErrorResponse("The file doesn't exist", HttpStatus.NOT_FOUND),
				HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(FileGenerationException.class)
	public ResponseEntity<ErrorResponse> handleFileGenerationExceptions(Exception e) {
		log.error("Cannot Generate Excel File", e);
		return new ResponseEntity<>(new ErrorResponse("Cannot Generate Excel File", HttpStatus.INTERNAL_SERVER_ERROR),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleUnknownExceptions(Exception e) {
		log.error("Something is wrong", e);
		return new ResponseEntity<>(new ErrorResponse("Something is wrong", HttpStatus.INTERNAL_SERVER_ERROR),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
