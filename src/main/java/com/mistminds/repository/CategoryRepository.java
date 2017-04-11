package com.mistminds.repository;

import com.mistminds.domain.Category;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the Category entity.
 */

@SuppressWarnings("unused")
public interface CategoryRepository extends MongoRepository<Category,String> {
	public Category findByName(String name);

	public ArrayList<Category> findByParentIdIsNull();

	public List<Category> findByParentId(String parentId);

	public Category findOneByCategoryId(String categoryId);
	
	public Category findById(String subCategoryId);
}
