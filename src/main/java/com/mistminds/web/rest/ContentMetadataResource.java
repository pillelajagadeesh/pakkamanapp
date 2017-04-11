package com.mistminds.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.mistminds.config.Constants;
import com.mistminds.domain.ContentMetadata;
import com.mistminds.domain.Consumer;
import com.mistminds.domain.Welcome;
import com.mistminds.repository.ContentMetadataRepository;
import com.mistminds.repository.ConsumerRepository;
import com.mistminds.service.ContentMetadataService;
import com.mistminds.service.ConsumerService;
import com.mistminds.web.rest.util.HeaderUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST controller for managing ContentMetadata.
 */
@RestController
@RequestMapping("/api")
public class ContentMetadataResource {

    private final Logger log = LoggerFactory.getLogger(ContentMetadataResource.class);
        
    @Inject
    private ContentMetadataRepository contentMetadataRepository;
    
    @Inject 
    private ConsumerRepository consumerRepository;

    @Inject 
    private ConsumerService consumerService;
    
    @Inject 
    private ContentMetadataService contentMetadataService;
    
    Welcome welcome;
    /**
     * POST  /contentMetadatas -> Create a new contentMetadata.
     */
    @RequestMapping(value = "/contentMetadata",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ContentMetadata> createContentMetadata(@RequestBody ContentMetadata contentMetadata) throws URISyntaxException {
        log.debug("REST request to save ContentMetadata : {}", contentMetadata);
        if (contentMetadata.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("contentMetadata", "idexists", "A new contentMetadata cannot already have an ID")).body(null);
        }
        ContentMetadata result = contentMetadataRepository.save(contentMetadata);
        return ResponseEntity.created(new URI("/api/contentMetadatas/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("contentMetadata", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /contentMetadatas -> Updates an existing contentMetadata.
     */
    @RequestMapping(value = "/contentMetadata",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ContentMetadata> updateContentMetadata(@RequestBody ContentMetadata contentMetadata) throws URISyntaxException {
        log.debug("REST request to update ContentMetadata : {}", contentMetadata);
        if (contentMetadata.getId() == null) {
            return createContentMetadata(contentMetadata);
        }
        ContentMetadata result = contentMetadataRepository.save(contentMetadata);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("contentMetadata", contentMetadata.getId().toString()))
            .body(result);
    }

    /**
     * GET  /contentMetadatas -> get all the contentMetadatas.
     */
    @RequestMapping(value = "/contentMetadata",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ContentMetadata> getAllContentMetadatas() {
        log.debug("REST request to get all ContentMetadatas");
        return contentMetadataRepository.findAll();
            }

    /**
     * GET  /contentMetadata/:id -> get the "id" contentMetadata.
     */
    @RequestMapping(value = "/contentMetadata/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ContentMetadata> getContentMetadata(@PathVariable String id) {
        log.debug("REST request to get ContentMetadata : {}", id);
        ContentMetadata contentMetadata = contentMetadataRepository.findOne(id);
        return Optional.ofNullable(contentMetadata)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /contentMetadata/:id -> delete the "id" contentMetadata.
     */
    @RequestMapping(value = "/contentMetadata/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteContentMetadata(@PathVariable String id) {
        log.debug("REST request to delete ContentMetadata : {}", id);
        contentMetadataRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("contentMetadata", id.toString())).build();
    }
    
    @RequestMapping(value = "uploadImage", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	@Timed
	public ResponseEntity<Welcome> uploadImage(@PathVariable("image") String image, @PathVariable("consumer_id") String id) throws URISyntaxException{
    	log.debug("REST request to uploadImage");
		welcome = new Welcome();
		ResponseEntity<ContentMetadata> result = null;
		Consumer dbConsumer = consumerRepository.findOne(id);
		if(dbConsumer != null && image != null && !image.isEmpty()){
			Map<String, Object> uploadResult = contentMetadataService.cloudanaryUploadImage(image);
			ContentMetadata cloudinay = consumerService.uploadPhoto(uploadResult, id);
			if(cloudinay != null){
				result = createContentMetadata(cloudinay);
				if(result != null && result.getStatusCode().equals("200")){
					dbConsumer.setUrl(cloudinay.getUrl());
					dbConsumer.setLastUpdate(ZonedDateTime.now());
					consumerRepository.save(dbConsumer);
				}
			}
		}
		if(result != null && result.getStatusCode().equals("200")){
			welcome.setMessage(Constants.SUCCESS_RESULT);
		}else{
			welcome.setMessage(Constants.FAILURE_RESULT);
		}
		return ResponseEntity.ok()
		        .headers(HeaderUtil.createEntityCreationAlert("cloudinay", "d"))
		        .body(welcome);
	}
}