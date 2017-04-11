package com.mistminds.repository;

import com.mistminds.domain.PakkaApplicationSettings;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the PakkaApplicationSettings entity.
 */
public interface PakkaApplicationSettingsRepository extends MongoRepository<PakkaApplicationSettings,String> {

	public PakkaApplicationSettings findByName(String name);

}
