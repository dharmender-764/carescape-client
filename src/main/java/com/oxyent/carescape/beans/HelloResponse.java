package com.oxyent.carescape.beans;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class HelloResponse {
	private int id;
	private String questionname;

	public HelloResponse() {
	}

	public HelloResponse(int id, String questionname) {
		super();
		this.id = id;
		this.questionname = questionname;
	}

	@XmlAttribute
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@XmlElement
	public String getQuestionname() {
		return questionname;
	}

	public void setQuestionname(String questionname) {
		this.questionname = questionname;
	}

}