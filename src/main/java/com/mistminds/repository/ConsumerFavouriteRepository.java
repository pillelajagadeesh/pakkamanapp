package com.mistminds.repository;

import java.util.List;

import com.mistminds.domain.ConsumerFavourite;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the ConsumerFavourite entity.
 */
public interface ConsumerFavouriteRepository extends MongoRepository<ConsumerFavourite,String> {

	public List<ConsumerFavourite> findByproviderId(String providerId);
	
	public ConsumerFavourite findByConsumerId(String consumerId);
}
