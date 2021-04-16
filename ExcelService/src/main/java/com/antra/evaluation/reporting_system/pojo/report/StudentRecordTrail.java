package com.antra.evaluation.reporting_system.pojo.report;

import java.util.List;

import org.springframework.lang.NonNull;

public class StudentRecordTrail {
	private List<Student> thisStudentLevelRecords;

	public StudentRecordTrail(List<Student> thisStudentLevelRecords) {
		super();
		this.thisStudentLevelRecords = thisStudentLevelRecords;
	}

	public List<Student> getThisStudentLevelRecords() {
		return thisStudentLevelRecords;
	}

	public void setThisStudentLevelRecords(List<Student> thisStudentLevelRecords) {
		this.thisStudentLevelRecords = thisStudentLevelRecords;
	}

	public void appendStudentLevelRecord(@NonNull Student studentLevelRecord) {

		thisStudentLevelRecords.add(studentLevelRecord);

	}
}
