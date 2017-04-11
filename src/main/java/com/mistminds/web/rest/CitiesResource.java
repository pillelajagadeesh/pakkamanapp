package com.mistminds.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.mistminds.domain.Cities;
import com.mistminds.domain.Consumer;
import com.mistminds.domain.Localities;
import com.mistminds.repository.CitiesRepository;
import com.mistminds.repository.LocalitiesRepository;
import com.mistminds.service.CitiesService;
import com.mistminds.web.rest.util.CitiesResult;
import com.mistminds.web.rest.util.HeaderUtil;

/**
 * REST controller for managing Cities.
 */
@RestController
@RequestMapping("/api")
public class CitiesResource {

    private final Logger log = LoggerFactory.getLogger(CitiesResource.class);
        
    @Inject
    private CitiesRepository citiesRepository;

    @Inject
    private LocalitiesRepository localitiesRepository;
    
    @Inject
    private CitiesService citiesService;

    
    /**
     * POST  /cities : Create a new cities.
     *
     * @param cities the cities to create
     * @return the ResponseEntity with status 201 (Created) and with body the new cities, or with status 400 (Bad Request) if the cities has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/cities",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Cities> createCities(@RequestBody Cities cities) throws URISyntaxException {
        log.debug("REST request to save Cities : {}", cities);
        if (cities.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("cities", "idexists", "A new cities cannot already have an ID")).body(null);
        }
        Cities result = citiesRepository.save(cities);
        return ResponseEntity.created(new URI("/api/cities/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("cities", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /cities : Updates an existing cities.
     *
     * @param cities the cities to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated cities,
     * or with status 400 (Bad Request) if the cities is not valid,
     * or with status 500 (Internal Server Error) if the cities couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/cities",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Cities> updateCities(@RequestBody Cities cities) throws URISyntaxException {
        log.debug("REST request to update Cities : {}", cities);
        if (cities.getId() == null) {
            return createCities(cities);
        }
        Cities result = citiesRepository.save(cities);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("cities", cities.getId().toString()))
            .body(result);
    }

    /**
     * GET  /cities : get all the cities.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of cities in body
     */
    @RequestMapping(value = "/cities",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Cities> getAllCities() {
        log.debug("REST request to get all Cities");
        List<Cities> cities = citiesRepository.findAll();
        return cities;
    }
    
    
    /**
     * GET  /cities : get all the localities of the range of 30km along with their city names.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of cities in body
     */
    @RequestMapping(value = "/filterCities",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Cities> getAllCitiesOfRange(@RequestBody Consumer consumer) {
    	List<Cities> totalCities = new ArrayList<Cities>();
        log.debug("REST request to get all Cities"+consumer.getLocation());
        Point center = new Point(consumer.getLocation().get(0), consumer.getLocation().get(1));
		Double radius = 500000.0/1000.0;
		Distance distance = new Distance(Math.toDegrees(radius / 6378.137));
		Circle circle = new Circle(center, distance);
		
		List<Cities> cities = citiesRepository.findByCitylocationWithin(circle);
		for(Cities city:cities){
			totalCities.add(city);
		}
		System.out.println(cities);
		
        return totalCities;
    }
    
    
    
    /**
     * GET  /cities : get all the cities with in range 500 km and all localities related to each city with in the range of 30km.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of cities in body
     */
    @RequestMapping(value = "/getAllfilteredCitiesWithLocalities",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public CitiesResult getAllCitiesWithLocalities(@RequestBody Consumer consumer) {
    	log.debug("REST request to get all localities with respect to their  particular cities"+consumer.getLocation());
    	if(consumer.getLocation().isEmpty() || consumer.getLocation()==null){
    		log.info("Consumer Location is null or empty: "+consumer.getLocation());
    		return null;
    		
    	}
		return citiesService.listCitiesAndLocalities(consumer);
    }
    

    /**
     * GET  /cities/:id : get the "id" cities.
     *
     * @param id the id of the cities to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the cities, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/cities/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Cities> getCities(@PathVariable String id) {
        log.debug("REST request to get Cities : {}", id);
        Cities cities = citiesRepository.findOne(id);
        return Optional.ofNullable(cities)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /cities/:id : delete the "id" cities.
     *
     * @param id the id of the cities to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/cities/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteCities(@PathVariable String id) {
        log.debug("REST request to delete Cities : {}", id);
        citiesRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("cities", id.toString())).build();
    }

}
