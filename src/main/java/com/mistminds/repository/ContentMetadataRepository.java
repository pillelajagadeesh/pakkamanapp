package com.mistminds.repository;

import com.mistminds.domain.ContentMetadata;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the ContentMetadata entity.
 */
public interface ContentMetadataRepository extends MongoRepository<ContentMetadata,String> {

}
