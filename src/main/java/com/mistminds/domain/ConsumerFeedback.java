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
 * A ConsumerFeedback.
 */

@Document(collection = "consumer_feedback")
public class ConsumerFeedback implements Serializable {

    /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	@Id
    private String id;

    @Field("comment")
    private String comment;

    @Field("like_or_dislike")
    private String likeDislike;

    @Field("notification_id")
    private String notificationId;
    
	@Field("provider_id")
    private String providerId;

    @Field("consumer_id")
    private String consumerId;
    
    @Field("report_status")
    private String reportStatus;
    
    @Field("offensive")
    private Boolean offensive;
    
    @Field("share")
    private String share;
    
    @Field("favourite")
    private String favourite;
    
    @Field("count")
    private Integer count;

    @Field("created")
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getLikeDislike() {
		return likeDislike;
	}

	public void setLikeDislike(String likeDislike) {
		this.likeDislike = likeDislike;
	}

	public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }
    public String getReportStatus() {
		return reportStatus;
	}

	public void setReportStatus(String reportStatus) {
		this.reportStatus = reportStatus;
	}
    public Boolean getOffensive() {
        return offensive;
    }

    public void setOffensive(Boolean offensive) {
        this.offensive = offensive;
    }
    public String getShare() {
		return share;
	}

	public void setShare(String share) {
		this.share = share;
	}

	public String getFavourite() {
		return favourite;
	}

	public void setFavourite(String favourite) {
		this.favourite = favourite;
	}

	public Integer getCount() {
		return count;
	}

	public String getProviderId() {
		return providerId;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConsumerFeedback consumerFeedback = (ConsumerFeedback) o;
        return Objects.equals(id, consumerFeedback.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ConsumerFeedback{" +
            "id=" + id +
            ", comment='" + comment + "'" +
            ", likeOrDislike='" + likeDislike + "'" +
            ", notificationId='" + notificationId + "'" +
            ", consumerId='" + consumerId + "'" +
            ", providerId='" + providerId + "'" +
            ", offensive='" + offensive + "'" +
            ", reportStatus='" + reportStatus + "'" +
            ", count='" + count + "'" +
            ", share='" + share + "'" +
            ", favourite='" + favourite + "'" +
            '}';
    }
}
