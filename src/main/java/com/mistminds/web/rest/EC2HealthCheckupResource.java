package com.mistminds.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class EC2HealthCheckupResource {
	
	private final Logger log = LoggerFactory.getLogger(EC2HealthCheckupResource.class);
	
	@RequestMapping(value = "/healthcheck",
	        method = RequestMethod.GET,
	        produces = MediaType.APPLICATION_JSON_VALUE)
	  @Timed
	    public ResponseEntity<?> checkStatus() {
            log.info("Health check called from load balancer");
	        return new ResponseEntity<>(HttpStatus.OK);
	    }
}
