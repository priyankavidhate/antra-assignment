package com.antra.evaluation.reporting_system.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.antra.evaluation.reporting_system.exception.FileGenerationException;
import com.antra.evaluation.reporting_system.pojo.api.ErrorResponse;
import com.antra.evaluation.reporting_system.pojo.api.ExcelRequest;
import com.antra.evaluation.reporting_system.pojo.api.MultiSheetExcelRequest;
import com.antra.evaluation.reporting_system.pojo.report.ExcelData;
import com.antra.evaluation.reporting_system.pojo.report.ExcelDataHeader;
import com.antra.evaluation.reporting_system.pojo.report.ExcelDataSheet;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFil;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;
import com.antra.evaluation.reporting_system.pojo.report.LocalTimeConverter;
import com.antra.evaluation.reporting_system.pojo.report.Student;
import com.antra.evaluation.reporting_system.pojo.report.StudentRecordTrail;
import com.antra.evaluation.reporting_system.repo.ExcelFileRepository;
import com.antra.evaluation.reporting_system.repo.ExcelRepository;

@Service
public class ExcelFileServiceImpl implements ExcelFileService {

	private static final Logger log = LoggerFactory.getLogger(ExcelFileServiceImpl.class);
	@Autowired
	private ExcelFileRepository repository;
	private ExcelGenerationService excelGenerationService;
	private final AmazonS3 s3Client;
	@Value("${s3.bucket}")
	private String s3Bucket;

	@Autowired
	public ExcelFileServiceImpl(ExcelGenerationService excelGenerationService, AmazonS3 s3Client) {

		this.excelGenerationService = excelGenerationService;
		this.s3Client = s3Client;
	}

	@Override
	public ExcelFil createExcel(ExcelRequest request, boolean multisheet) {

		log.info("in create excel s");

		ExcelFile fileInfo = new ExcelFile();
		ExcelFil fileSave = new ExcelFil();
		try {
			String id = UUID.randomUUID().toString();
			fileInfo.setFileId(id);
			ExcelData data = new ExcelData();
			data.setTitle(request.getDescription());
			data.setFileId(id);
			data.setSubmitter(request.getSubmitter());

			if (multisheet) {
				data.setSheets(generateMultiSheet(request));
			} else {
				data.setSheets(generateSheet(request));
			}

			File generatedFile = excelGenerationService.generateExcelReport(data);
			// File temp = File.createTempFile(request.getSubmitter(),"_tmp.pdf");
			File currDir = new File(".");
			String path = currDir.getAbsolutePath();

			String fileLocation = path.substring(0, path.length() - 1) + data.getFileId() + ".xlsx";
			XSSFWorkbook excelWorkBook = new XSSFWorkbook();

//            FileOutputStream fileOut = new FileOutputStream(new File(fileLocation));
//            excelWorkBook.write(fileOut);
//            fileOut.flush();
//            fileOut.close();
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            try {
//                excelWorkBook.write(byteArrayOutputStream);
//                byteArrayOutputStream.close();
//            } catch (IOException e) {
//
//            	log.error(e.getMessage());
//            }
//            ByteArrayInputStream bi = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
//
//            ObjectMetadata objectMetaData = new ObjectMetadata();
//            objectMetaData.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");
//            objectMetaData.setContentLength(byteArrayOutputStream.toByteArray().length);
//            
//            log.info("in impl, s3Bucket :" + fileLocation + ", id :" + id);
//            log.info("output streamlength : " + byteArrayOutputStream.toByteArray().length);
//
//            s3Client.putObject(new PutObjectRequest(s3Bucket, id, bi, objectMetaData) );

//            File f = new File(fileLocation);   
//            log.info(f.getAbsolutePath());
//            s3Client.putObject(s3Bucket,id,f);
			// log.debug("Uploaded");

			File file = new File(fileLocation);
			InputStream dataStream = new FileInputStream(file);

			ObjectMetadata metadata = new ObjectMetadata();
			//metadata.setContentType("aapplication/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");
			metadata.setContentType("application/vnd.ms-excel");
			s3Client.putObject(new PutObjectRequest(s3Bucket, id, dataStream, metadata));
			fileInfo.setFileLocation(String.join("/", s3Bucket, id));

			fileInfo.setFileName(generatedFile.getName());
			fileInfo.setGeneratedTime(LocalDateTime.now());
			fileInfo.setSubmitter(request.getSubmitter());
			fileInfo.setFileSize(generatedFile.length());
			fileInfo.setDescription(request.getDescription());

			List<Student> stuList = new ArrayList<Student>();
			for (List<String> datum : request.getData()) {
				Student s = new Student();
				s.setId(datum.get(0));
				s.setName(datum.get(1));
				s.setSt_class(datum.get(2));
				stuList.add(s);
			}
			LocalTimeConverter timeConObj = new LocalTimeConverter();
			String time = timeConObj.convert(LocalDateTime.now());
			fileSave.setId(id);
			fileSave.setFileLocation(String.join("/", s3Bucket, id));
			fileSave.setFileName(data.getFileId() + ".xlsx");

			fileSave.setGeneratedTime(LocalDateTime.now());
			fileSave.setSubmitter(request.getSubmitter());
			fileSave.setFileSize(file.length());
			fileSave.setDescription(request.getDescription());
			StudentRecordTrail sr = new StudentRecordTrail(stuList);
			sr.getThisStudentLevelRecords();
			fileSave.setStudentLevelRecordTrail(sr);
			repository.save(fileSave);
		} catch (Exception e) {
//            log.error("Error in generateFile()", e);
			System.out.println("this is error " + e);
			// throw new FileGenerationException(e);
		}
		// excelRepository.saveFile(fileInfo);

		return fileSave;
	}

	@Override
	public ExcelFil deleteExcel(String fileId) {

		ExcelFil excelFil = repository.delete(fileId);
		if (excelFil != null) {
			deleteFileFromBucket(fileId);
		}
		return excelFil;
	}

	@Override
	public boolean updateExcel(String fileId, ExcelRequest request) throws IOException {
		log.info("in UPDATEEXCEL excel s");

		ExcelFile fileInfo = new ExcelFile();
		ExcelFil fileSave = new ExcelFil();

		ExcelData data = new ExcelData();
		data.setTitle(request.getDescription());
		data.setFileId(fileId);
		data.setSubmitter(request.getSubmitter());
		data.setSheets(generateSheet(request));

		File generatedFile = excelGenerationService.generateExcelReport(data);
		// File temp = File.createTempFile(request.getSubmitter(),"_tmp.pdf");
		File currDir = new File(".");
		String path = currDir.getAbsolutePath();

		String fileLocation = path.substring(0, path.length() - 1) + data.getFileId() + ".xlsx";
		XSSFWorkbook excelWorkBook = new XSSFWorkbook();
		File file = new File(fileLocation);
		InputStream dataStream = new FileInputStream(file);

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType("aapplication/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");
		s3Client.putObject(new PutObjectRequest(s3Bucket, fileId, dataStream, metadata));
		fileInfo.setFileLocation(String.join("/", s3Bucket, fileId));

		fileInfo.setFileName(generatedFile.getName());
		fileInfo.setGeneratedTime(LocalDateTime.now());
		fileInfo.setSubmitter(request.getSubmitter());
		fileInfo.setFileSize(generatedFile.length());
		fileInfo.setDescription(request.getDescription());

		List<Student> stuList = new ArrayList<Student>();
		for (List<String> datum : request.getData()) {
			Student s = new Student();
			s.setId(datum.get(0));
			s.setName(datum.get(1));
			s.setSt_class(datum.get(2));
			stuList.add(s);
		}
		LocalTimeConverter timeConObj = new LocalTimeConverter();
		String time = timeConObj.convert(LocalDateTime.now());
		fileSave.setId(fileId);
		fileSave.setFileLocation(String.join("/", s3Bucket, fileId));
		fileSave.setFileName(data.getFileId() + ".xlsx");

		fileSave.setGeneratedTime(LocalDateTime.now());
		fileSave.setSubmitter(request.getSubmitter());
		fileSave.setFileSize(file.length());
		fileSave.setDescription(request.getDescription());
		StudentRecordTrail sr = new StudentRecordTrail(stuList);
		sr.getThisStudentLevelRecords();
		fileSave.setStudentLevelRecordTrail(sr);
		deleteFileFromBucket(fileId);
		repository.save(fileSave);

		return true;
	}

	@Override
	public ExcelFil getExcel(String fileId) {
		// TODO Auto-generated method stub
		return null;
	}

	private List<ExcelDataSheet> generateSheet(ExcelRequest request) {
		List<ExcelDataSheet> sheets = new ArrayList<>();
		ExcelDataSheet sheet = new ExcelDataSheet();
		sheet.setHeaders(request.getHeaders().stream().map(ExcelDataHeader::new).collect(Collectors.toList()));
		sheet.setDataRows(request.getData().stream()
				.map(listOfString -> (List<Object>) new ArrayList<Object>(listOfString)).collect(Collectors.toList()));
		sheet.setTitle("sheet-1");
		sheets.add(sheet);
		return sheets;
	}

	private List<ExcelDataSheet> generateMultiSheet(ExcelRequest request) {
		List<ExcelDataSheet> sheets = new ArrayList<>();
		int index = request.getHeaders().indexOf(((MultiSheetExcelRequest) request).getSplitBy());
		Map<String, List<List<String>>> splittedData = request.getData().stream()
				.collect(Collectors.groupingBy(row -> (String) row.get(index)));
		List<ExcelDataHeader> headers = request.getHeaders().stream().map(ExcelDataHeader::new)
				.collect(Collectors.toList());
		splittedData.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
			ExcelDataSheet sheet = new ExcelDataSheet();
			sheet.setHeaders(headers);
			sheet.setDataRows(entry.getValue().stream().map(listOfString -> {
				List<Object> listOfObject = new ArrayList<>();
				listOfString.forEach(listOfObject::add);
				return listOfObject;
			}).collect(Collectors.toList()));
			sheet.setTitle(entry.getKey());
			sheets.add(sheet);
		});
		return sheets;
	}

	@Override
	public boolean deleteFileFromBucket(String keyName) {
		log.info("Deleting file with name= " + keyName);
		final DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(s3Bucket, keyName);
		s3Client.deleteObject(deleteObjectRequest);
		log.info("File deleted successfully.");
		// TODO Auto-generated method stub
		return true;
	}
	@Override
    public List<ExcelFil> getAllFiles() {
	   List<ExcelFil> list = repository.getAllFiles();
	   return list;
    }

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleUnknownExceptions(Exception e) {
		log.error("Something is wrong", e);
		return new ResponseEntity<>(new ErrorResponse("Something is wrong", HttpStatus.INTERNAL_SERVER_ERROR),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	
}
