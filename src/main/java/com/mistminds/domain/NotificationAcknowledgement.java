package com.mistminds.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A NotificationAcknowledgement.
 */

@Document(collection = "notification_acknowledgement")
public class NotificationAcknowledgement implements Serializable {

    /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	@Id
    private String id;

	@Field("read")
    private ZonedDateTime read;

    @Field("consumer_id")
    private String consumerId;

    @Field("notification_id")
    private String notificationId;

    @Field("delivered")
    private ZonedDateTime delivered;
    
    @Field("check_In")
    private ZonedDateTime checkIn;

    @Field("sent")
    private ZonedDateTime sent;

    @Field("visited")
    private ZonedDateTime visited;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ZonedDateTime getRead() {
        return read;
    }

    public void setRead(ZonedDateTime read) {
        this.read = read;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public ZonedDateTime getDelivered() {
        return delivered;
    }

    public void setDelivered(ZonedDateTime delivered) {
        this.delivered = delivered;
    }

    public ZonedDateTime getSent() {
		return sent;
	}

	public void setSent(ZonedDateTime sent) {
		this.sent = sent;
	}

	public ZonedDateTime getVisited() {
		return visited;
	}

	public void setVisited(ZonedDateTime visited) {
		this.visited = visited;
	}
	 public ZonedDateTime getCheckIn() {
			return checkIn;
		}

		public void setCheckIn(ZonedDateTime checkIn) {
			this.checkIn = checkIn;
		}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NotificationAcknowledgement notificationAcknowledgement = (NotificationAcknowledgement) o;
        return Objects.equals(id, notificationAcknowledgement.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "NotificationAcknowledgement{" +
            "id=" + id +
            ", read='" + read + "'" +
            ", checkIn='" + checkIn + "'" +
            ", consumerId='" + consumerId + "'" +
            ", notificationId='" + notificationId + "'" +
            ", delivered='" + delivered + "'" +
            '}';
    }
}
