package com.mistminds.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Document(collection = "provider")
public class Provider implements Serializable{
	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	@Id
	private String id;
	@Field("name")
	private String name;
	 @Field("active")
	 private boolean active;
	@Field("mobile")
	private String mobile;
	@Field("email")
	private String email;
	@Field("address")
	private String address;
	@Field("location")
    private List<Double> location;
	@Field("image_url")
	private String imageUrl;
	@Field("image")
	private String image;
   @Field("consumer_id")
	private String consumerId;
	@Field("promo_createdDate")
	 private String promo_createdDate;
   @Field("monthly_free_credits")
	private double monthly_free_credits;
     @Field("wallet_credits")
   private double wallet_credits;
   @Field("eleigible_for_promo_credit")
  private boolean eleigible_for_promo_credit;
   
   private List<Notification> notification;

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

  	public void setVersion(Integer version) {
  		this.version = version;
  	}

   public List<Notification> getNotification() {
		return notification;
	}

	public void setNotification(List<Notification> notification) {
		this.notification = notification;
	}
	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	 public boolean isActive() {
			return active;
		}

		public void setActive(boolean active) {
			this.active = active;
		}
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	 public String getConsumerId() {
	        return consumerId;
	    }

	    public void setConsumerId(String consumerId) {
	        this.consumerId = consumerId;
	    }
	public List<Double> getLocation() {
		return location;
	}

	public void setLocation(List<Double> location) {
		this.location = location;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPromo_createdDate() {
		return promo_createdDate;
	}

	public void setPromo_createdDate(String promo_createdDate) {
		this.promo_createdDate = promo_createdDate;
	}
    public double getMonthly_free_credits() {
        return monthly_free_credits;
    }

    public void setMonthly_free_credits(double monthly_free_credits) {
        this.monthly_free_credits = monthly_free_credits;
    }

    public double getWallet_credits() {
        return wallet_credits;
    }

    public void setWallet_credits(double wallet_credits) {
        this.wallet_credits = wallet_credits;
    }
    public boolean getEleigible_for_promo_credit() {
        return eleigible_for_promo_credit;
    }

    public void setEleigible_for_promo_credit(boolean eleigible_for_promo_credit) {
        this.eleigible_for_promo_credit = eleigible_for_promo_credit;
    }

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Provider provider = (Provider) o;
        return Objects.equals(id, provider.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Provider{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", mobile='" + mobile + "'" +
            ", email='" + email + "'" +
            ", active='" + active + "'" +
            ", address='" + address + "'" +
            ", url='" + imageUrl + "'" +
            ", consumerId='" + consumerId + "'" +
            ", image='" + image + "'" +
            ", location='" + location + "'" +
            ", monthly_free_credits='" + monthly_free_credits + "'" +
            ", promo_createdDate='" + promo_createdDate + "'" +
            ", wallet_credits='" + wallet_credits + "'" +
            ", eleigible_for_promo_credit='" + eleigible_for_promo_credit + "'" +
            '}';
    }

}
	