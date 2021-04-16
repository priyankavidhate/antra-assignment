package com.antra.evaluation.reporting_system.repo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.antra.evaluation.reporting_system.pojo.report.PDFFil;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;

@Repository
public class PDFRepository {
	private static final Logger log = LoggerFactory.getLogger(PDFRepository.class);
	@Autowired
	private DynamoDBMapper dynamoDBMapper;

	public PDFFil save(PDFFil fileSave) {
		dynamoDBMapper.save(fileSave);
		return fileSave;
	}

	public boolean delete(String reqId) {
		PDFFil pdfFile = dynamoDBMapper.load(PDFFil.class, reqId);
		dynamoDBMapper.delete(pdfFile);
		return true;
	}

	public boolean update(String reqId, PDFFil fileSave) {
		dynamoDBMapper.save(fileSave, new DynamoDBSaveExpression().withExpectedEntry("reqId",
				new ExpectedAttributeValue(new AttributeValue().withS(reqId))));
		return true;
	}

	public List<PDFFil> getAllFiles() {
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();

		PaginatedScanList<PDFFil> paginatedScanList = dynamoDBMapper.scan(PDFFil.class, scanExpression);
		paginatedScanList.loadAllResults();

		List<PDFFil> list = new ArrayList<PDFFil>(paginatedScanList.size());

		Iterator<PDFFil> iterator = paginatedScanList.iterator();
		while (iterator.hasNext()) {
			PDFFil element = iterator.next();
			list.add(element);
		}

		log.info("size of data in db :" + list.toString());
		return list;
	}
	
	public PDFFil getFileById(String reqId) {
		return dynamoDBMapper.load(PDFFil.class, reqId);
	}

}