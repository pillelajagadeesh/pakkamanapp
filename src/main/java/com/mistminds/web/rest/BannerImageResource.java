package com.mistminds.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.mistminds.domain.BannerImage;
import com.mistminds.repository.BannerImageRepository;
import com.mistminds.service.BannerImageService;
import com.mistminds.web.rest.util.HeaderUtil;
	
	/**
	 * 
	 * @author Srinivas
	 * Rest controller for managing BannerImage
	 *
	 */
	@RestController
	@RequestMapping("/api")
	public class BannerImageResource {

		private final Logger log = LoggerFactory.getLogger(BannerImageResource.class);
		
		@Inject
		private BannerImageRepository bannerImageRepository;
		
		@Inject
		private BannerImageService bannerImageService;
	    
	    
	    /**
		 * POST BannerImages -> Create a new Banner Image records
		 */
	    @RequestMapping(value = "/bannerImage", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
		@Timed
		public ResponseEntity<BannerImage> createBannerImages(@RequestBody BannerImage bannerimage) throws URISyntaxException{
	    	log.debug("REST request for save BannerImage : {}", bannerimage);
	    	if (bannerimage.getId() != null) {
	    		return ResponseEntity
						.badRequest()
						.headers(
								HeaderUtil.createFailureAlert("Banner",
										"idexists",
										"A new Banner cannot already have an ID"))
						.body(null);
	    	}
	    	BannerImage result = bannerImageRepository.save(bannerimage);
	    		return ResponseEntity
	    				.created(new URI("/api/bannerImage/" + result.getId()))
	    				.headers(
	    						HeaderUtil.createEntityCreationAlert("bannerimage", result
	    								.getId().toString())).body(result);
	    	
	    }
	    
	    
	    /**
		 * GET /Banner Image/:id -> get the id banner image
		 */
		
		@RequestMapping(value = "/bannerImage/{id}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
		@Timed
		public ResponseEntity<BannerImage> getBannerImage(@PathVariable String id){
			log.debug("REST api for get BannerImage : {}", id);
			BannerImage banner = bannerImageRepository.findOne(id);
			return Optional.ofNullable(banner)
		            .map(result -> new ResponseEntity<>(
		                result,
		                HttpStatus.OK))
		            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
			
		}
	}

