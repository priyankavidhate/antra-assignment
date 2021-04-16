package com.antra.evaluation.reporting_system.pojo.report;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

@DynamoDBDocument
public class Student {

	String id;

	String name;

	String st_class;
	
	String score;

	public Student(String id, String name, String st_class, String sco) {
		super();
		this.id = id;
		this.name = name;
		this.st_class = st_class;
		this.score = score;
	}

	public Student() {

	}
	
	@DynamoDBAttribute(attributeName = "score")
	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
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
	
	@Override
	public String toString() {
		return "Student [id=" + id + ", name=" + name + ", st_class=" + st_class + ", score=" + score + "]";
	}

}
