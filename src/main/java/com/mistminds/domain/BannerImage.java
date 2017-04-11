package com.mistminds.domain;

import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

public class BannerImage {

	
	private static final long serialVersionUID = 1L;

	@Id
	private String id;
	
	@Field("title")
	private String title;
	
	@Field("description")
    private String description;
	
	@Field("home_banner_images")
    private String[] homeBannerImages;
	
	@Field("food_category_banner_images")
    private String[] foodCategoryBannerImages;
	
	@Field("fashion_category_banner_images")
    private String[] fashionCategoryBannerImages;
	
	@Field("entertainment_category_banner_images")
    private String[] entertainmentCategoryBannerImages;
	
	@Field("electronics_category_banner_images")
    private String[] electronicsCategoryBannerImages;
	
	@Field("education_category_banner_images")
    private String[] educationCategoryBannerImages;
	
	@Field("buy_category_banner_images")
    private String[] buyCategoryBannerImages;
	
	@Field("government_category_banner_images")
    private String[] governmentCategoryBannerImages;
	
	@Field("job_category_banner_images")
    private String[] jobCategoryBannerImages;
	
	@Field("miscellaneous_banner_images")
    private String[] miscellaneousBannerImages;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String[] getHomeBannerImages() {
		return homeBannerImages;
	}

	public void setHomeBannerImages(String[] homeBannerImages) {
		this.homeBannerImages = homeBannerImages;
	}
	
	public String[] getFoodCategoryBannerImages() {
		return foodCategoryBannerImages;
	}

	public void setFoodCategoryBannerImages(String[] foodCategoryBannerImages) {
		this.foodCategoryBannerImages = foodCategoryBannerImages;
	}

	public String[] getFashionCategoryBannerImages() {
		return fashionCategoryBannerImages;
	}

	public void setFashionCategoryBannerImages(String[] fashionCategoryBannerImages) {
		this.fashionCategoryBannerImages = fashionCategoryBannerImages;
	}

	public String[] getEntertainmentCategoryBannerImages() {
		return entertainmentCategoryBannerImages;
	}

	public void setEntertainmentCategoryBannerImages(String[] entertainmentCategoryBannerImages) {
		this.entertainmentCategoryBannerImages = entertainmentCategoryBannerImages;
	}

	public String[] getElectronicsCategoryBannerImages() {
		return electronicsCategoryBannerImages;
	}

	public void setElectronicsCategoryBannerImages(String[] electronicsCategoryBannerImages) {
		this.electronicsCategoryBannerImages = electronicsCategoryBannerImages;
	}

	public String[] getEducationCategoryBannerImages() {
		return educationCategoryBannerImages;
	}

	public void setEducationCategoryBannerImages(String[] educationCategoryBannerImages) {
		this.educationCategoryBannerImages = educationCategoryBannerImages;
	}

	public String[] getBuyCategoryBannerImages() {
		return buyCategoryBannerImages;
	}

	public void setBuyCategoryBannerImages(String[] buyCategoryBannerImages) {
		this.buyCategoryBannerImages = buyCategoryBannerImages;
	}

	public String[] getGovernmentCategoryBannerImages() {
		return governmentCategoryBannerImages;
	}

	public void setGovernmentCategoryBannerImages(String[] governmentCategoryBannerImages) {
		this.governmentCategoryBannerImages = governmentCategoryBannerImages;
	}

	public String[] getJobCategoryBannerImages() {
		return jobCategoryBannerImages;
	}

	public void setJobCategoryBannerImages(String[] jobCategoryBannerImages) {
		this.jobCategoryBannerImages = jobCategoryBannerImages;
	}
	
	
	public String[] getMiscellaneousBannerImages() {
		return miscellaneousBannerImages;
	}

	public void setMiscellaneousBannerImages(String[] miscellaneousBannerImages) {
		this.miscellaneousBannerImages = miscellaneousBannerImages;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BannerImage bannerimage = (BannerImage) o;
        return Objects.equals(id, bannerimage.id);
    }


	@Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "BannerImage{" +
            "id=" + id +
            ", title='" + title + "'" +
            ", description='" + description + "'" +
            ", homeBannerImages='" + homeBannerImages + "'" +
            ", foodCategoryBannerImages='" + foodCategoryBannerImages + "'" +
            ", fashionCategoryBannerImages='" + fashionCategoryBannerImages + "'" +
            ", entertainmentCategoryBannerImages='" + entertainmentCategoryBannerImages + "'" +
            ", electronicsCategoryBannerImages='" + electronicsCategoryBannerImages + "'" +
            ", educationCategoryBannerImages='" + educationCategoryBannerImages + "'" +
            ", buyCategoryBannerImages='" + buyCategoryBannerImages + "'" +
            ", governmentCategoryBannerImages='" + governmentCategoryBannerImages + "'" +
            ", jobCategoryBannerImages='" + jobCategoryBannerImages + "'" +
            ", miscellaneousBannerImages='" + miscellaneousBannerImages + "'" +
            '}';
    }

	
}
