package com.mistminds.domain;

import java.time.ZonedDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Objects;

/**
 * A Cloudinary.
 */

@Document(collection = "content_metadata")
public class ContentMetadata implements Serializable {

    /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	@Id
    private String id;

    @Field("signature")
    private String signature;

    @Field("format")
    private String format;

    @Field("type")
    private String type;

    @Field("url")
    private String url;

    @Field("image_version")
    private String imageVersion;
  
    @Field("tags")
    private String tags;

    @Field("orginal_file_name")
    private String orginalFileName;

    @Field("bytes")
    private Integer bytes;

    @Field("width")
    private Integer width;

    @Field("e_tag")
    private String eTag;

    @Field("height")
    private Integer height;

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
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Integer getBytes() {
        return bytes;
    }

    public void setBytes(Integer bytes) {
        this.bytes = bytes;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public String geteTag() {
        return eTag;
    }

    public void seteTag(String eTag) {
        this.eTag = eTag;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getImageVersion() {
		return imageVersion;
	}

	public void setImageVersion(String imageVersion) {
		this.imageVersion = imageVersion;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ContentMetadata contentMetadata = (ContentMetadata) o;
        return Objects.equals(id, contentMetadata.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

	@Override
	public String toString() {
		return "ContentMetadata [id=" + id + ", signature=" + signature
				+ ", format=" + format + ", type=" + type + ", url=" + url
				+ ", imageVersion=" + imageVersion + ", tags=" + tags
				+ ", orginalFileName=" + orginalFileName + ", bytes=" + bytes
				+ ", width=" + width + ", eTag=" + eTag + ", height=" + height
				+ ", created=" + created + ", deleted=" + deleted
				+ ", lastUpdate=" + lastUpdate + ", version=" + version + "]";
	}
}
