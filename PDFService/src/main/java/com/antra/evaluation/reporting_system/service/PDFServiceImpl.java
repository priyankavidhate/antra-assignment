package com.antra.evaluation.reporting_system.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.antra.evaluation.reporting_system.pojo.api.PDFRequest;
import com.antra.evaluation.reporting_system.pojo.report.PDFFil;
import com.antra.evaluation.reporting_system.pojo.report.PDFFile;
import com.antra.evaluation.reporting_system.pojo.report.Student;
import com.antra.evaluation.reporting_system.pojo.report.StudentRecordTrail;
import com.antra.evaluation.reporting_system.repo.PDFRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PDFServiceImpl implements PDFService {

	private static final Logger log = LoggerFactory.getLogger(PDFServiceImpl.class);

	@Autowired
	private PDFRepository repository;

	private final PDFGenerator generator;

	private final AmazonS3 s3Client;

	@Value("${s3.bucket}")
	private String s3Bucket;

	public PDFServiceImpl(PDFGenerator generator, AmazonS3 s3Client) {
		this.generator = generator;
		this.s3Client = s3Client;
	}

	@Override
	public PDFFil createPDF(final PDFRequest request) {
		PDFFil fileSave = new PDFFil();

		try {
			System.out.println("---------------------in pdf ----------------");
			PDFFile file = new PDFFile();
			String id = UUID.randomUUID().toString();
			file.setId(id);
			file.setSubmitter(request.getSubmitter());
			file.setDescription(request.getDescription());
			file.setGeneratedTime(LocalDateTime.now());

			PDFFile generatedFile = generator.generate(request);

			File temp = new File(generatedFile.getFileLocation());
			log.debug("Upload temp file to s3 {}", generatedFile.getFileLocation());
			s3Client.putObject(s3Bucket, file.getId(), temp);
			log.debug("Uploaded");

			file.setFileLocation(String.join("/", s3Bucket, file.getId()));
			file.setFileSize(generatedFile.getFileSize());
			file.setFileName(generatedFile.getFileName());

			// my code

			// List<List<String>> datum = request.getData();
			List<Student> stuList = new ArrayList<Student>();
			for (List<String> datum : request.getData()) {
				Student s = new Student();
				s.setId(datum.get(0));
				s.setName(datum.get(1));
				s.setSt_class(datum.get(2));
				s.setScore(datum.get(3));
				stuList.add(s);
			}
			
			fileSave.setReqId(request.getReqId());
			fileSave.setId(id);
			fileSave.setFileName(request.getSubmitter() + ".pdf");
			fileSave.setFileLocation(generatedFile.getFileLocation());
			fileSave.setSubmitter(request.getSubmitter());
			fileSave.setFileSize(generatedFile.getFileSize());
			fileSave.setDescription(request.getDescription());
			fileSave.setStatus("completed");
			fileSave.setGeneratedTime(LocalDateTime.now());
			fileSave.setSubmitter(request.getSubmitter());
//        fileSave.setGeneratedTime(generatedFile.getGeneratedTime());
			StudentRecordTrail sr = new StudentRecordTrail(stuList);
			sr.getThisStudentLevelRecords();
			fileSave.setStudentLevelRecordTrail(sr);
			repository.save(fileSave);
			System.out.println("Sucess");
			for (Student str : sr.getThisStudentLevelRecords()) {
				System.out.println(str);
			}
			log.debug("clear tem file {}", file.getFileLocation());
			if (temp.delete()) {
				log.debug("cleared");
			}
		} catch (Exception e) {
			System.out.println("------------------This is exception------------------------------" + e);
		}

		return fileSave;
	}

	@Override
	public List<PDFFil> getAllFiles() {
		List<PDFFil> list = repository.getAllFiles();
		return list;
	}

	@Override
	public boolean deletePDF(String reqId) {
		log.info("reqId in delete pdf" + reqId);
		PDFFil pdf = repository.getFileById(reqId);
		repository.delete(reqId);		
		log.info("pdf s3 object id :" + pdf.getId());
		deleteFileFromBucket(pdf.getId());
		return true;
	}

	@Override
	public boolean updatePDF(String reqId, PDFRequest request) {
		log.info("updatePDF :", reqId);
		PDFFil fileSave = new PDFFil();
		PDFFil fileInfo = getPDF(reqId);
		deletePDF(reqId);

		PDFFile generatedFile = generator.generate(request);
		String id = UUID.randomUUID().toString();
		
		fileSave.setReqId(reqId);
		fileSave.setId(id);
		fileSave.setFileName(request.getSubmitter() + ".pdf");
		fileSave.setFileLocation(generatedFile.getFileLocation());
		fileSave.setSubmitter(request.getSubmitter());
		fileSave.setFileSize(generatedFile.getFileSize());
		fileSave.setDescription(request.getDescription());
		fileSave.setStatus("completed");		
		fileSave.setGeneratedTime(LocalDateTime.now());
//        fileSave.setGeneratedTime(generatedFile.getGeneratedTime());

		fileSave.setFileLocation(String.join("/", s3Bucket, id));
		List<Student> stuList = new ArrayList<Student>();
		for (List<String> datum : request.getData()) {
			Student s = new Student();
			s.setId(datum.get(0));
			s.setName(datum.get(1));
			s.setSt_class(datum.get(2));
			s.setScore(datum.get(3));
			stuList.add(s);
		}
		StudentRecordTrail sr = new StudentRecordTrail(stuList);
		sr.getThisStudentLevelRecords();
		fileSave.setStudentLevelRecordTrail(sr);
		File pdf = new File(generatedFile.getFileLocation());
		log.debug("Upload pdf file to s3 {}", generatedFile.getFileLocation());
		s3Client.putObject(s3Bucket, fileSave.getId(), pdf);
		repository.save(fileSave);
		System.out.println("Sucess");
		for (Student str : sr.getThisStudentLevelRecords()) {
			System.out.println(str);
		}
		return true;
	}
	
	public boolean deleteFileFromBucket(String keyName) {
		log.info("Deleting file with name= " + keyName);
		final DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(s3Bucket, keyName);
		s3Client.deleteObject(deleteObjectRequest);
		log.info("File deleted successfully.");
		return true;
	}

	@Override
	public PDFFil getPDF(String reqId) {
		// TODO Auto-generated method stub
		PDFFil pdfFile = repository.getFileById(reqId);
		return pdfFile;
	}

}
