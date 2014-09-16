package com.app.lomo;

public class Task {
	private String taskId;
	private String taskName;
	private String description;
	private String location;
	private String longitude;
	private String latitude;
	private String date;
	private String time;
	private String priority;
	private String distance;

	public Task() {
	}
	
	public Task(String taskId, String taskName, String description,
			String location, String longitude, String latitude, String date,
			String time, String priority) {
		this.taskId = taskId;
		this.taskName = taskName;
		this.description = description;
		this.location = location;
		this.longitude = longitude;
		this.latitude = latitude;
		this.date = date;
		this.time = time;
		this.priority = priority;
		distance="";
	}


	public Task(String taskId, String taskName, String description,
			String location, String longitude, String latitude, String date,
			String time, String priority,String distance) {
		this.taskId = taskId;
		this.taskName = taskName;
		this.description = description;
		this.location = location;
		this.longitude = longitude;
		this.latitude = latitude;
		this.date = date;
		this.time = time;
		this.priority = priority;
		this.distance=distance;
	}

	/**
	 * @return the taskId
	 */
	public String getTaskId() {
		return taskId;
	}

	/**
	 * @param taskId
	 *            the taskId to set
	 */
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	/**
	 * @return the taskName
	 */
	public String getTaskName() {
		return taskName;
	}

	/**
	 * @param taskName
	 *            the taskName to set
	 */
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @param location
	 *            the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @return the longitude
	 */
	public String getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude
	 *            the longitude to set
	 */
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return the latitude
	 */
	public String getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude
	 *            the latitude to set
	 */
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * @return the time
	 */
	public String getTime() {
		return time;
	}

	/**
	 * @param time
	 *            the time to set
	 */
	public void setTime(String time) {
		this.time = time;
	}

	/**
	 * @return the priority
	 */
	public String getPriority() {
		return priority;
	}

	/**
	 * @param priority
	 *            the priority to set
	 */
	public void setPriority(String priority) {
		this.priority = priority;
	}
	
	public String getDistance() {
		return distance;
	}

	/**
	 * @param priority
	 *            the priority to set
	 */
	public void setDistance(String distance) {
		this.distance = distance;
	}

}
