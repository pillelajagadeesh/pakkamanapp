package com.mistminds.repository;

import java.util.List;

import org.springframework.data.geo.Circle;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.mistminds.domain.Cities;

/**
 * Spring Data MongoDB repository for the Cities entity.
 */
@SuppressWarnings("unused")
public interface CitiesRepository extends MongoRepository<Cities,String> {

	List<Cities> findByCitylocationWithin(Circle circle);

}
