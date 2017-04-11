package com.mistminds.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A Consumer.
 */

@Document(collection = "consumer")
public class Consumer implements Serializable {

    /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	@Id
    private String id;

    @Field("mobile")
    private String mobile;

    @Field("email")
    private String email;

    @Field("status")
    private String status;
    
    /*@Field("category")
    private String category;*/
    
    @Transient
    private String categoryId;
    
    @Field("active")
    private boolean active;
    
    @Field("gender")
    private String gender;

    @Field("otp")
    private String otp;
    
    @Field("image")
    private String image;
    
    @Field("public_id")
    private String public_id;
    
    @Field("address")
	private String address;

	@Field("url")
    private String url;

    @Field("otp_count")
    private Integer otpCount;
    
    @Field("device_info")
    private DeviceInfo device_info;
    @Field("location")
    private List<Double> location;

    @Field("name")
    private String name;
    
    @Field("unsubscribe_category")
   	private List<Category> unsubscribeCategory;
    
    @Field("subscribe_category")
   	private List<String> subscribeCategory;

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
   	
   	@Transient
    private Double minRadius;
    
    @Transient
    private Double maxRadius;
    
    @Transient
    private Integer pageCount;
    
    @Transient
   	private ZonedDateTime lastRecordDate;
    
    @Transient
    private String search;

	public ZonedDateTime getLastRecordDate() {
		return lastRecordDate;
	}

	public void setLastRecordDate(ZonedDateTime lastRecordDate) {
		this.lastRecordDate = lastRecordDate;
	}

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

	/*public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}*/

	public String getAddress() {
		return address;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public void setAddress(String address) {
		this.address = address;
	}
    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public Integer getOtpCount() {
        return otpCount;
    }

    public void setOtpCount(Integer otpCount) {
        this.otpCount = otpCount;
    }

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

   public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
    
	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public DeviceInfo getDevice_info() {
		return device_info;
	}

	public void setDevice_info(DeviceInfo device_info) {
		this.device_info = device_info;
	}

	 public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getPublic_id() {
		return public_id;
	}

	public void setPublic_id(String public_id) {
		this.public_id = public_id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<Double> getLocation() {
		return location;
	}

	public void setLocation(List<Double> location) {
		this.location = location;
	}

	public List<Category> getUnsubscribeCategory() {
		return unsubscribeCategory;
	}

	public void setUnsubscribeCategory(List<Category> unsubscribeCategory) {
		this.unsubscribeCategory = unsubscribeCategory;
	}
 	public List<String> getSubscribeCategory() {
		return subscribeCategory;
	}

	public void setSubscribeCategory(List<String> subscribeCategory) {
		this.subscribeCategory = subscribeCategory;
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

	public Integer getPageCount() {
		return pageCount;
	}

	public void setPageCount(Integer pageCount) {
		this.pageCount = pageCount;
	}
	
	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Consumer consumer = (Consumer) o;
        return Objects.equals(id, consumer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Consumer{" +
            "id=" + id +
            ", mobile='" + mobile + "'" +
            ", email='" + email + "'" +
            ", status='" + status + "'" +
            /*", category='" + category + "'" +*/
            ", categoryId='" + categoryId + "'" +
            ", image='" + image + "'" +
            ", active='" + active + "'" +
            ", gender='" + gender + "'" +
            ", public_id='" + public_id + "'" +
            ", url='" + url + "'" +
            ", otp='" + otp + "'" +
            ", otpCount='" + otpCount + "'" +
            ", name='" + name + "'" +
            ", address='" + address + "'" +
            ", device_info='" + device_info + "'" +
            ", location='" + location + "'" +
            ", unsubscribe_category='" + unsubscribeCategory + "'" +
            ", subscribe_category='" + subscribeCategory + "'" +
            ", min_radius='" + minRadius + "'" +
            ", max_radius='" + maxRadius + "'" +
            ", page_count='" + pageCount + "'" +
            ", search='" + search + "'" +
            '}';
    }

}