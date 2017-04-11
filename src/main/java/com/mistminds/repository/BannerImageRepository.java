package com.mistminds.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mistminds.domain.BannerImage;

public interface BannerImageRepository extends MongoRepository<BannerImage, String> {

}
