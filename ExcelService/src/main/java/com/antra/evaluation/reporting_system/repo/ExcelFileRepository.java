package com.antra.evaluation.reporting_system.repo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFil;

@Repository
public class ExcelFileRepository {
	@Autowired
	private DynamoDBMapper dynamoDBMapper;

	public ExcelFil save(ExcelFil fileSave) {
		dynamoDBMapper.save(fileSave);
		return fileSave;
	}

	public ExcelFil delete(String reqId) {
		ExcelFil excelFile = dynamoDBMapper.load(ExcelFil.class, reqId);
		dynamoDBMapper.delete(excelFile);
		return excelFile;
	}

//	public String delete(String fileId) {
//		ExcelFil pdfFile = dynamoDBMapper.load(ExcelFil.class, fileId);
//        dynamoDBMapper.delete(pdfFile);
//        return "Employee Deleted!";
//    }
//	
	public ExcelFil getFileById(String reqId) {
		return dynamoDBMapper.load(ExcelFil.class, reqId);
	}

	public boolean update(String reqId, ExcelFil fileSave) {
		dynamoDBMapper.save(fileSave, new DynamoDBSaveExpression().withExpectedEntry("reqId",
				new ExpectedAttributeValue(new AttributeValue().withS(reqId))));
		return true;
	}

	public List<ExcelFil> getAllFiles() {
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();

		PaginatedScanList<ExcelFil> paginatedScanList = dynamoDBMapper.scan(ExcelFil.class, scanExpression);
		paginatedScanList.loadAllResults();

		List<ExcelFil> list = new ArrayList<ExcelFil>(paginatedScanList.size());

		Iterator<ExcelFil> iterator = paginatedScanList.iterator();
		while (iterator.hasNext()) {
			ExcelFil element = iterator.next();
			list.add(element);
		}

		System.out.println(list.toString());
		return list;
	}
//	
//	public boolean update(String fileId,ExcelFil fileSave) {
//		 dynamoDBMapper.save(fileSave,
//	                new DynamoDBSaveExpression()
//	        .withExpectedEntry("fileId",
//	                new ExpectedAttributeValue(
//	                        new AttributeValue().withS(fileId)
//	                )));
//	        return true;
//	    }
}