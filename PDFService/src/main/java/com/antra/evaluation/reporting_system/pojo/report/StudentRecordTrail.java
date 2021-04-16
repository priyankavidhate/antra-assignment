package com.antra.evaluation.reporting_system.pojo.report;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

public class StudentRecordTrail {
	
	private static final Logger log = LoggerFactory.getLogger(StudentRecordTrail.class);
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

	@Override
	public String toString() {
		String holder = "";
		for(Student s: thisStudentLevelRecords) {
			
			holder += s.toString() + ",";
		}
		return holder.substring(0, holder.length() - 1);
	}
	
	
}
