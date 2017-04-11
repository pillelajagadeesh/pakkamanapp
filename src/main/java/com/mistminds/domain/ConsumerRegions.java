package com.mistminds.domain;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

/**
 * A ConsumerRegions.
 */

@Document(collection = "consumer_regions")
public class ConsumerRegions implements Serializable {

    /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	@Id
    private String id;

    @Field("consumer_id")
    private String consumerId;

    @Field("region")
    private List<Region> region;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    public List<Region> getRegion() {
        return region;
    }

    public void setRegion(List<Region> region) {
        this.region = region;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConsumerRegions consumerRegions = (ConsumerRegions) o;
        return Objects.equals(id, consumerRegions.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ConsumerRegions{" +
            "id=" + id +
            ", consumerId='" + consumerId + "'" +
            ", region='" + region + "'" +
            '}';
    }
}
