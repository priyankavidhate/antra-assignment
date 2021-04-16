package com.antra.report.client.pojo.reponse;

public class Student {

	String id;

	String name;

	String st_class;
	
	String score;

	public Student(String id, String name, String st_class, String score) {
		super();
		this.id = id;
		this.name = name;
		this.st_class = st_class;
		this.score = score;
	}

	public Student() {

	}
	
	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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
