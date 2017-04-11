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
 * A PrivateMessage.
 */

@Document(collection = "private_message")
public class PrivateMessage implements Serializable {

    /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	@Id
    private String id;

    @Field("message")
    private String message;

    @Field("read")
    private ZonedDateTime read;

    @Field("delivered")
    private ZonedDateTime delivered;

    @Field("notification_id")
    private String notificationId;

    @Field("sender_id")
    private String senderId;

    @Field("receiver_id")
    private String receiverId;

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

   	public void setVersion(Integer version) {
   		this.version = version;
   	}
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ZonedDateTime getRead() {
        return read;
    }

    public void setRead(ZonedDateTime read) {
        this.read = read;
    }

    public ZonedDateTime getDelivered() {
        return delivered;
    }

    public void setDelivered(ZonedDateTime delivered) {
        this.delivered = delivered;
    }

    public String getNotificationId() {
        return notificationId;
    }

	public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PrivateMessage privateMessage = (PrivateMessage) o;
        return Objects.equals(id, privateMessage.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "PrivateMessage{" +
            "id=" + id +
            ", message='" + message + "'" +
            ", read='" + read + "'" +
            ", delivered='" + delivered + "'" +
            ", created='" + created + "'" +
            ", notificationId='" + notificationId + "'" +
            ", senderId='" + senderId + "'" +
            ", receiverId='" + receiverId + "'" +
            '}';
    }
}
