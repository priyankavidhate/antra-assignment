package com.antra.evaluation.reporting_system.pojo.report;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

@DynamoDBDocument
public class Student {

	String id;

	String name;

	String st_class;

	public Student(String id, String name, String st_class) {
		super();
		this.id = id;
		this.name = name;
		this.st_class = st_class;
	}
	public Student() {
		
	}

	@DynamoDBAttribute(attributeName = "id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@DynamoDBAttribute(attributeName = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@DynamoDBAttribute(attributeName = "st_class")
	public String getSt_class() {
		return st_class;
	}

	public void setSt_class(String st_class) {
		this.st_class = st_class;
	}

}
