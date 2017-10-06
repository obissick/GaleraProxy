package com.orenbissick.GaleraProxy;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Statistic {
	@Id
	private String name;
	private int totalConnections;
	private int currConnections;
	private String status;
	
	public Statistic() {
		super();
	}
	public Statistic(String name, int totalConnections, int currConnections, String status) {
		super();
		this.name = name;
		this.totalConnections = totalConnections;
		this.currConnections = currConnections;
		this.status = status;
	}
	public String getName() {
		return name;
	}
	public int getTotalConnections() {
		return totalConnections;
	}
	public int getCurrConnections() {
		return currConnections;
	}
	public String getStatus() {
		return status;	
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setTotalConnections(int totalConnections) {
		this.totalConnections = totalConnections;
	}
	public void setCurrConnections(int currConnections) {
		this.currConnections = currConnections;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
