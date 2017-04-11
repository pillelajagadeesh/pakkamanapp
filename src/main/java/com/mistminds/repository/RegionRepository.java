package com.mistminds.repository;

import java.util.List;

import com.mistminds.domain.Region;

import org.springframework.data.geo.Circle;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the Region entity.
 */
public interface RegionRepository extends MongoRepository<Region,String> {

	public List<Region> findByLocationWithin(Circle c);
}
