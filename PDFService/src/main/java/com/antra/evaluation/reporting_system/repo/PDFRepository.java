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
import com.antra.evaluation.reporting_system.pojo.report.PDFFil;
import com.antra.evaluation.reporting_system.pojo.report.PDFFile;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;


@Repository
public class PDFRepository {
	@Autowired
	private DynamoDBMapper dynamoDBMapper;

	public PDFFil save(PDFFil fileSave) {
		dynamoDBMapper.save(fileSave);
		return fileSave;
	}
	public String delete(String fileId) {
        PDFFil pdfFile = dynamoDBMapper.load(PDFFil.class,fileId);
        dynamoDBMapper.delete(pdfFile);
        return "Employee Deleted!";
    }
	
	public PDFFil getFileById(String fileId) {
        return dynamoDBMapper.load(PDFFil.class, fileId);
    }
	
	public boolean update(String fileId,PDFFil fileSave) {
		 dynamoDBMapper.save(fileSave,
	                new DynamoDBSaveExpression()
	        .withExpectedEntry("fileId",
	                new ExpectedAttributeValue(
	                        new AttributeValue().withS(fileId)
	                )));
	        return true;
	    }
	
	public List<PDFFil> getAllFiles(){
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();

        PaginatedScanList<PDFFil> paginatedScanList = dynamoDBMapper.scan(PDFFil.class, scanExpression);
        paginatedScanList.loadAllResults();

        List<PDFFil>list = new ArrayList<PDFFil>(paginatedScanList.size());

        Iterator<PDFFil> iterator = paginatedScanList.iterator();
        while (iterator.hasNext()) {
        	PDFFil element = iterator.next();
            list.add(element);
        }

        
        System.out.println(list.toString());
        return list;
	}
	
	
} 