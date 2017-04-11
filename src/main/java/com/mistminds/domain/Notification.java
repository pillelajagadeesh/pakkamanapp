package com.mistminds.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A Notification.
 */

@Document(collection = "notification")
public class Notification implements Serializable {

    /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	@Id
    private String id;
	
	@Field("track_id")
    private String trackId;

    @Field("category_id")
    private String categoryId;

    @Field("main_category_id")
    private String mainCategoryId;
    
    @Field("title")
    private String title;

    @Field("description")
    private String description;

    @Field("valid_from")
    private ZonedDateTime validFrom;

    @Field("valid_to")
    private ZonedDateTime validTo;
    
    @Field("approved_by")
    private String approvedBy;
    
    @Field("approved_time")
    private ZonedDateTime approvedTime;

    @Field("active")
    private boolean active;
    
    @Field("location")
    private List<Double> location;
    
    @Field("provider_name")
    private String providerName;
    
    @Field("provider_id")
    private String providerId;
    
    @Field("notification_date")
    private ZonedDateTime notificationDate;

    @Field("offensive")
    private boolean offensive;

	@Field("image_urls")
    private String[] imageUrls;

    @Field("consumer_id")
    private String consumerId;
    
    @Field("radius")
    private String radius;

    @Field("callStatus")
	private boolean callStatus;

    @Field("images")
    private String[] images;

    @Field("image")
    private String image;
    
	@Field("showLocation")
	private boolean showLocation;
	
	@Field("homeBannerStatus")
	private boolean homeBannerStatus;

	@Field("categoryBannerStatus")
	private boolean categoryBannerStatus;
	
	@Field("mrp_Price")
	private double mrpPrice;
	
	@Field("offer_Price")
	private double offerPrice;
	
	@Field("hot_link")
	private String hotLink;
	
	@Field("spam_count")
	private Integer spamCount;

	@Field("free_credits_used")
	private double freeCreditsUsed;

	@Field("wallet_credits_used")
	private double walletCreditsUsed;

	@Field("created")
	@CreatedDate
	@JsonIgnore
	private ZonedDateTime created;

	@Field("deleted")
	private ZonedDateTime deleted;

	@Field("last_update")
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
    
    public String getTrackId() {
		return trackId;
	}

	public void setTrackId(String uuid) {
		this.trackId = uuid;
	}

	public String getCategoryId() {
    	return categoryId;
	}

	public void setCategory(String categoryId) {
		this.categoryId = categoryId;
	}
    
	public String getMainCategoryId() {
		return mainCategoryId;
	}

	public void setMainCategoryId(String mainCategoryId) {
		this.mainCategoryId = mainCategoryId;
	}

	public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<Double> getLocation() {
		return location;
	}

	public void setLocation(List<Double> location) {
		this.location = location;
	}

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	public String getProviderId() {
		return providerId;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	public ZonedDateTime getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(ZonedDateTime validFrom) {
		this.validFrom = validFrom;
	}

	public ZonedDateTime getNotificationDate() {
		return notificationDate;
	}

	public void setNotificationDate(ZonedDateTime notificationDate) {
		this.notificationDate = notificationDate;
	}

	public ZonedDateTime getValidTo() {
		return validTo;
	}

	public void setValidTo(ZonedDateTime validTo) {
		this.validTo = validTo;
	}
	
	public String getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(String approvedBy) {
		this.approvedBy = approvedBy;
	}
    
	
	public ZonedDateTime getApprovedTime() {
		return approvedTime;
	}

	public void setApprovedTime(ZonedDateTime now) {
		this.approvedTime = now;
	}

	public boolean getOffensive() {
        return offensive;
    }

    public void setOffensive(boolean offensive) {
        this.offensive = offensive;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    public String getRadius() {
		return radius;
	}

	public void setRadius(String radius) {
		this.radius = radius;
	}

	public boolean getCallStatus() {
		return callStatus;
	}

	public void setCallStatus(boolean callStatus) {
		this.callStatus = callStatus;
	}

	public boolean getShowLocation() {
		return showLocation;
	}

	public void setShowLocation(boolean showLocation) {
		this.showLocation = showLocation;
	}
	public Integer getSpamCount() {
		return spamCount;
	}

	public void setSpamCount(Integer spamCount) {
		this.spamCount = spamCount;
	}

	public String[] getImageUrls() {
		return imageUrls;
	}

	public void setImageUrls(String[] imageUrls) {
		this.imageUrls = imageUrls;
	}
	public boolean isHomeBannerStatus() {
		return homeBannerStatus;
	}

	public void setHomeBannerStatus(boolean homeBannerStatus) {
		this.homeBannerStatus = homeBannerStatus;
	}

	public boolean isCategoryBannerStatus() {
		return categoryBannerStatus;
	}

	public void setCategoryBannerStatus(boolean categoryBannerStatus) {
		this.categoryBannerStatus = categoryBannerStatus;
	}
	public double getMrpPrice() {
		return mrpPrice;
	}

	public void setMrpPrice(double mrpPrice) {
		this.mrpPrice = mrpPrice;
	}

	public double getOfferPrice() {
		return offerPrice;
	}

	public String getHotLink() {
		return hotLink;
	}

	public void setHotLink(String hotLink) {
		this.hotLink = hotLink;
	}

	public void setOfferPrice(double offerPrice) {
		this.offerPrice = offerPrice;
	}
	

	public double getFreeCreditsUsed() {
		return freeCreditsUsed;
	}

	public void setFreeCreditsUsed(double freeCreditsUsed) {
		this.freeCreditsUsed = freeCreditsUsed;
	}

	public double getWalletCreditsUsed() {
		return walletCreditsUsed;
	}

	public void setWalletCreditsUsed(double walletCreditsUsed) {
		this.walletCreditsUsed = walletCreditsUsed;
	}

	public String[] getImages() {
		return images;
	}

	public void setImages(String[] images) {
		this.images = images;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Notification other = (Notification) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Notification [id=" + id +
				", trackId=" + trackId +
				", categoryId=" + categoryId +
				", mainCategoryId=" + mainCategoryId +
				", title=" + title + 
				", description=" + description + 
				", validFrom=" + validFrom + 
				", validTo=" + validTo + 
				", approvedBy=" + approvedBy +
				", approvedTime=" + approvedTime +
				", active=" + active + 
				", location=" + location + 
				", providerName=" + providerName +
				", providerId=" + providerId +
				", notificationDate=" + notificationDate + 
				", offensive=" + offensive + 
				", imageUrls=" + imageUrls +
				", consumerId=" + consumerId + 
				", radius=" + radius + 
				", callStatus=" + callStatus +
				", images=" + images +
				", showLocation=" + showLocation + 
				", homeBannerStatus=" + homeBannerStatus + 
				", categoryBannerStatus=" + categoryBannerStatus + 
				", mrpPrice=" + mrpPrice + 
				", offerPrice=" + offerPrice +
				", hotLink=" + hotLink +
				", spamCount=" + spamCount +
				", image=" + image +
				", freeCreditsUsed=" + freeCreditsUsed + 
				", walletCreditsUsed=" + walletCreditsUsed + 
				", created=" + created + 
				", deleted=" + deleted + 
				", lastUpdate=" + lastUpdate + 
				", version=" + version + "]";
	}

}
