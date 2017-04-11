package com.mistminds.domain;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A DeviceInfo.
 */

@Document(collection = "device_info")
public class DeviceInfo implements Serializable {

    /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	@Id
    private String id;

    @Field("device")
    private String device;

    @Field("sdk")
    private String sdk;

    @Field("model")
    private String model;

    @Field("product")
    private String product;
    
    @Field("device_id")
    private String deviceId;

    @Field("consumer_id")
    private String consumerId;

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

    public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getSdk() {
        return sdk;
    }

    public void setSdk(String sdk) {
        this.sdk = sdk;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DeviceInfo deviceInfo = (DeviceInfo) o;
        return Objects.equals(id, deviceInfo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "DeviceInfo{" +
            "id=" + id +
            ", device='" + device + "'" +
            ", sdk='" + sdk + "'" +
            ", model='" + model + "'" +
            ", product='" + product + "'" +
            ", deviceId='" + deviceId + "'" +
            ", consumerId='" + consumerId + "'" +
            '}';
    }
}
