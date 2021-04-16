package com.antra.evaluation.reporting_system.pojo.report;

import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;

public class StudentLevelRecordTrailConverter implements DynamoDBTypeConverter<List<Student>, StudentRecordTrail> {

	@Override
	public List<Student> convert(StudentRecordTrail object) {
		// TODO Auto-generated method stub
		return object.getThisStudentLevelRecords();

	}

	@Override
	public StudentRecordTrail unconvert(List<Student> object) {
		// TODO Auto-generated method stub
		return new StudentRecordTrail(object);
	}
}
