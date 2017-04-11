package com.mistminds.web.rest.util;

import java.time.ZonedDateTime;
import java.util.List;

import com.mistminds.domain.Notification;
import com.mongodb.DBObject;

public class NotificationResult {

	private List<DBObject> notifications;

	private Double minRadius;

	private Double maxRadius;

	private ZonedDateTime lastRecordDate;
	
	private String markerPinUrl;

	public List<DBObject> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<DBObject> notifications) {
		this.notifications = notifications;
	}

	public Double getMinRadius() {
		return minRadius;
	}

	public void setMinRadius(Double minRadius) {
		this.minRadius = minRadius;
	}

	public Double getMaxRadius() {
		return maxRadius;
	}

	public void setMaxRadius(Double maxRadius) {
		this.maxRadius = maxRadius;
	}

	public ZonedDateTime getLastRecordDate() {
		return lastRecordDate;
	}

	public void setLastRecordDate(ZonedDateTime lastRecordDate) {
		this.lastRecordDate = lastRecordDate;
	}

	public String getMarkerPinUrl() {
		return markerPinUrl;
	}

	public void setMarkerPinUrl(String markerPinUrl) {
		this.markerPinUrl = markerPinUrl;
	}


}
