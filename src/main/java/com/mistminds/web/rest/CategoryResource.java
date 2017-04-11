package com.mistminds.web.rest;


import com.codahale.metrics.annotation.Timed;
import com.hazelcast.com.eclipsesource.json.JsonObject;
import com.mistminds.config.PakkaApplicationSettingsConfiguration;
import com.mistminds.domain.Category;
import com.mistminds.domain.ContentMetadata;
import com.mistminds.domain.PakkaApplicationSettings;
import com.mistminds.domain.Welcome;
import com.mistminds.repository.CategoryRepository;
import com.mistminds.repository.ContentMetadataRepository;
import com.mistminds.repository.PakkaApplicationSettingsRepository;
import com.mistminds.service.CategoryService;
import com.mistminds.service.ConsumerService;
import com.mistminds.service.ContentMetadataService;
import com.mistminds.web.rest.util.HeaderUtil;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST controller for managing Category.
 */
@RestController
@RequestMapping("/api")
public class CategoryResource {

    private final Logger log = LoggerFactory.getLogger(CategoryResource.class);
        
    @Inject
    private CategoryRepository categoryRepository;
    
    @Inject
	private CategoryService categoryService;
	
    @Inject
    private ContentMetadataService contentMetadataService;
    
	@Inject
	private ConsumerService consumerService;
	
	@Inject
	private ContentMetadataRepository contentMetadataRepository;
	
	@Inject
	private PakkaApplicationSettingsRepository pakkaApplicationSettingsRepository;
	
	Welcome welcome;
    
    /**
     * POST  /categories : Create a new category.
     *
     * @param category the category to create
     * @return the ResponseEntity with status 201 (Created) and with body the new category, or with status 400 (Bad Request) if the category has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
	
	@RequestMapping(value = "/categories", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Category> createCategory(@RequestBody Category category) throws URISyntaxException{
		log.debug("REST request for save Category : {}", category);
		ZonedDateTime now = ZonedDateTime.now();
		welcome = new Welcome();
		if (category.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("category", "idexists", "A new category cannot already have an ID")).body(null);
        }
		boolean flag = categoryService.addCategory(category);
		if(flag){
			category.setCreated(now);
			Category result = categoryRepository.save(category);
		return ResponseEntity.created(new URI("/api/categories/" + result.getId()))
	            .headers(HeaderUtil.createEntityCreationAlert("Category", ""))
	            .body(result);
		}else{
			return ResponseEntity.created(new URI("/api/categories/" + ""))
		            .headers(HeaderUtil.createEntityCreationAlert("Category",""))
		            .body(null);
		}
		
	}
	
	
	
    /*@RequestMapping(value = "/categories",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Category> createCategory(@RequestBody Category category) throws URISyntaxException {
        log.debug("REST request to save Category : {}", category);
        if (category.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("category", "idexists", "A new category cannot already have an ID")).body(null);
        }
        Category result = categoryRepository.save(category);
        return ResponseEntity.created(new URI("/api/categories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("category", result.getId().toString()))
            .body(result);
    }*/

    /**
     * PUT  /categories : Updates an existing category.
     *
     * @param category the category to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated category,
     * or with status 400 (Bad Request) if the category is not valid,
     * or with status 500 (Internal Server Error) if the category couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
	
    @RequestMapping(value = "/categories",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Category> updateNotification(@RequestBody Category category) throws URISyntaxException {
        log.debug("REST request to update Category : {}", category);
        if (category.getId() == null) {
            return createCategory(category);
        }
        Category result = categoryRepository.save(category);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("notification", category.getId().toString()))
            .body(result);
    }

    /**
     * GET  /categories : get all the categories.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of categories in body
     */
    /**
	 * GET  /categories -> get all category
	 * 
	 * @return list of category
	 */
	
	@RequestMapping(value = "/categories", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public List<Category> getAllCategories(){
		log.debug("REST request to get all Category");
		List<Category> categorylist = new ArrayList<Category>();
		List<Category> addcategory=null;
	    ArrayList<Category> category= categoryRepository.findByParentIdIsNull();
		for(Category  subcategory:category){
			addcategory =categoryRepository.findByParentId(subcategory.getId());	
			subcategory.setCategory(addcategory);
			categorylist.add(subcategory);
				
			}
		return categorylist;
	}
	
	/**
     * GET  /categories : get all the subcategories.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of subcategories in body
     */
    /**
	 * GET  /categories -> get all subcategory
	 * 
	 * @return list of category
	 */
	@RequestMapping(value = "/subCategories", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public List<Category> getAllSubCategories(){
		log.debug("REST request to get all Category");
		List<Category> categorylist = new ArrayList<Category>();
		List<Category> addcategory=null;
	    ArrayList<Category> category= categoryRepository.findByParentIdIsNull();
		for(Category  subcategory:category){
			addcategory =categoryRepository.findByParentId(subcategory.getId());	
			subcategory.setCategory(addcategory);
			categorylist.addAll(addcategory);
				
			}
		return categorylist;
	}
	
	
	 /**
     * GET  /cities : get all the categories for Admin Panel.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of cities in body
     */
    @RequestMapping(value = "/adminCategories",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Category> getAllCategorys() {
        log.debug("REST request to get all Admin Categories");
        List<Category> categorylist = categoryRepository.findAll();
        return categorylist;
    }
	
	
	
	/**
	 * GET  /Version -> get version and priority
	 * 
	 * @return Object
	 * @throws com.amazonaws.util.json.JSONException 
	 */
	
	@RequestMapping(value = "/pakkaVersion", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public JSONObject getPriorityAndVersion() throws JSONException, com.amazonaws.util.json.JSONException{
		log.debug("REST request to get all Priority & Version");
		PakkaApplicationSettings version=pakkaApplicationSettingsRepository.findByName(PakkaApplicationSettingsConfiguration.KEY_NAME_PAKKA_VERSION);
	    PakkaApplicationSettings priority=pakkaApplicationSettingsRepository.findByName(PakkaApplicationSettingsConfiguration.KEY_NAME_PRIORITY);
	    String pakkaVersion =version.getValue().toString();
        String pakkaPriority=priority.getValue().toString();
        JSONObject json = new JSONObject();
        json.put("pakkaVersion", pakkaVersion);
        json.put("pakkaPriority", pakkaPriority);
		return json;
	}

    /**
     * GET  /categories/:id : get the "id" category.
     *
     * @param id the id of the category to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the category, or with status 404 (Not Found)
     * @throws JSONException 
     */
    @RequestMapping(value = "/categories/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<JSONObject> getCategory(@PathVariable String id) throws JSONException {
        log.debug("REST request to get Category : {}", id);
        JSONObject json = new JSONObject();
        Category category = categoryRepository.findOne(id);
        List<Category> subCategories=categoryRepository.findByParentId(category.getId());
        if(subCategories!=null){
        category.setCategory(subCategories);
        }
        if(category.getParentId() != null){
        	Category categoryName=	categoryRepository.findById(category.getParentId());
        	json.put("categoryName", categoryName.getName());
        }
        json.put("categoryDetails", category);
        
        return Optional.ofNullable(json)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    
    /**
	 * GET /consumerCategory/:id -> get the id category
	 */
	
	@RequestMapping(value = "/getCategory/{consumerId}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<Category>> getConsumerCategory(@PathVariable String consumerId){
		log.debug("REST api for get Category : {}", consumerId);
		
		List<Category> category = categoryService.getConsumerCategory(consumerId);
		return Optional.ofNullable(category)
	            .map(result -> new ResponseEntity<>(
	                result,
	                HttpStatus.OK))
	            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
		
	}
	
	
    /**
     * DELETE  /categories/:id : delete the "id" category.
     *
     * @param id the id of the category to delete
     * @return the ResponseEntity with status 200 (OK)
     */
	@RequestMapping(value = "/categories/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteCategory(@PathVariable String id){
		log.debug("REST api to delete Category : {}", id);
		categoryRepository.delete(id);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("category", id.toString())).build();
	}
	
	
	@RequestMapping(value = "/addCategories/{categoryName}", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Category> addCategory(@PathVariable String categoryName, @RequestParam("file") MultipartFile file) throws URISyntaxException{
		Category category = new Category();
		category.setName(categoryName);
		welcome = new Welcome();
		Map<String, Object> uploadResult = null;
		try {
			byte[] base64 = file.getBytes();
			StringBuffer sf = new StringBuffer();
			for(int i = 0; i < base64.length; i++){
				sf.append(base64[i]);
			}
			uploadResult = contentMetadataService.cloudanaryUploadImage(Base64.encodeBase64URLSafeString(base64));
			ContentMetadata cloudinay = consumerService.uploadPhoto(uploadResult, "1");
			if(cloudinay != null){
				ContentMetadata result = contentMetadataRepository.save(cloudinay);
				if(result.getId() != null){
					category.setImageUrl(cloudinay.getUrl());
				}
			}
			
		} catch (IOException e) {
			log.error("Exceptoion occred adding category name: "+categoryName, e);
		}
		
		boolean flag = categoryService.addCategory(category);
		if(flag){
			Category result = categoryRepository.save(category);
		return ResponseEntity.created(new URI("/api/categories/" + result.getId()))
	            .headers(HeaderUtil.createEntityCreationAlert("Category", ""))
	            .body(result);
		}else{
			return ResponseEntity.created(new URI("/api/categories/" + ""))
		            .headers(HeaderUtil.createEntityCreationAlert("Category",""))
		            .body(null);
		}
		
	}

}
