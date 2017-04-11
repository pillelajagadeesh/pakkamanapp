package com.mistminds.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.mistminds.config.PakkaApplicationSettingsConfiguration;
import com.mistminds.domain.Localities;
import com.mistminds.repository.LocalitiesRepository;
import com.mistminds.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Localities.
 */
@RestController
@RequestMapping("/api")
public class LocalitiesResource {

    private final Logger log = LoggerFactory.getLogger(LocalitiesResource.class);
        
    @Inject
    private LocalitiesRepository localitiesRepository;
    
    /**
     * POST  /localities : Create a new localities.
     *
     * @param localities the localities to create
     * @return the ResponseEntity with status 201 (Created) and with body the new localities, or with status 400 (Bad Request) if the localities has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/localities",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Localities> createLocalities(@RequestBody Localities localities) throws URISyntaxException {
        log.debug("REST request to save Localities : {}", localities);
        if (localities.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("localities", "idexists", "A new localities cannot already have an ID")).body(null);
        }
        Localities result = localitiesRepository.save(localities);
        return ResponseEntity.created(new URI("/api/localities/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("localities", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /localities : Updates an existing localities.
     *
     * @param localities the localities to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated localities,
     * or with status 400 (Bad Request) if the localities is not valid,
     * or with status 500 (Internal Server Error) if the localities couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/localities",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Localities> updateLocalities(@RequestBody Localities localities) throws URISyntaxException {
        log.debug("REST request to update Localities : {}", localities);
        if (localities.getId() == null) {
            return createLocalities(localities);
        }
        Localities result = localitiesRepository.save(localities);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("localities", localities.getId().toString()))
            .body(result);
    }

    /**
     * GET  /localities : get all the localities.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of localities in body
     */
    @RequestMapping(value = "/localities",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Localities> getAllLocalities() {
        log.debug("REST request to get all Localities");
        List<Localities> localities = localitiesRepository.findAll();
        return localities;
    }

    /**
     * GET  /localities/:id : get the "id" localities.
     *
     * @param id the id of the localities to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the localities, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/localities/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Localities> getLocalities(@PathVariable String id) {
        log.debug("REST request to get Localities : {}", id);
        Localities localities = localitiesRepository.findOne(id);
        return Optional.ofNullable(localities)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    
    /**
     * GET  /localities/:id : get the "id" localities.
     *
     * @param id the id of the localities to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the localities, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/localitiesname/{cityname}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Localities> getLocalitiesName(@PathVariable String cityname) {
        log.debug("REST request to get Localities : {}", cityname);
        List<Localities> localities = localitiesRepository.findBycityname(cityname);
        return localities;
       /* return Optional.ofNullable(localities)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));*/
    }

    /**
     * DELETE  /localities/:id : delete the "id" localities.
     *
     * @param id the id of the localities to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/localities/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteLocalities(@PathVariable String id) {
        log.debug("REST request to delete Localities : {}", id);
        localitiesRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("localities", id.toString())).build();
    }

}
