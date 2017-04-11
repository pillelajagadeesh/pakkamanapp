package com.mistminds.domain;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Location {

	@Field("type")
	private String type;
	
	@Field("coordinates")
	private List<Double> coordinates;
	
	@Field("latitude")
	private String latitude;
	
	@Field("longitute")
	private String longitude;

	@Field("created")
   	@CreatedDate
   	@JsonIgnore
   	private ZonedDateTime created;

   	@Field("deleted")
   	private ZonedDateTime deleted;

   	@Field("last_update")
   	@LastModifiedDate
   	@JsonIgnore
   	private ZonedDateTime lastUpdate;

   	@Field("version")
   	private Integer version;

   	public ZonedDateTime getCreated() {
   		return created;
   	}

   	public void setCreated(ZonedDateTime created) {
   		this.created = created;
   	}

   	public ZonedDateTime getDeleted() {
   		return deleted;
   	}

   	public void setDeleted(ZonedDateTime deleted) {
   		this.deleted = deleted;
   	}

   	public ZonedDateTime getLastUpdate() {
   		return lastUpdate;
   	}

   	public void setLastUpdate(ZonedDateTime lastUpdate) {
   		this.lastUpdate = lastUpdate;
   	}

   	public Integer getVersion() {
   		return version;
   	}

	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<Double> getCoordinates() {
		return coordinates;
	}
	public void setCoordinates(List<Double> coordinates) {
		this.coordinates = coordinates;
	}

}
