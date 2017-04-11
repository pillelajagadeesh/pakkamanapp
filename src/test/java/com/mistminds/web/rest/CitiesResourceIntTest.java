package com.mistminds.web.rest;

import com.mistminds.domain.Cities;
import com.mistminds.repository.CitiesRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the CitiesResource REST controller.
 *
 * @see CitiesResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CitiesResourceIntTest.class)
@WebAppConfiguration
@IntegrationTest
public class CitiesResourceIntTest {

    private static final String DEFAULT_CITYNAME = "AAAAA";
    private static final String UPDATED_CITYNAME = "BBBBB";
    private static final String DEFAULT_CITYLOCATION = "AAAAA";
    //private static final List<Double> UPDATED_CITYLOCATION = "BBBBB";

    @Inject
    private CitiesRepository citiesRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restCitiesMockMvc;

    private Cities cities;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        CitiesResource citiesResource = new CitiesResource();
        ReflectionTestUtils.setField(citiesResource, "citiesRepository", citiesRepository);
        this.restCitiesMockMvc = MockMvcBuilders.standaloneSetup(citiesResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        citiesRepository.deleteAll();
        cities = new Cities();
        cities.setCityname(DEFAULT_CITYNAME);
        //cities.setCitylocation(DEFAULT_CITYLOCATION);
    }

    @Test
    public void createCities() throws Exception {
        int databaseSizeBeforeCreate = citiesRepository.findAll().size();

        // Create the Cities

        restCitiesMockMvc.perform(post("/api/cities")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(cities)))
                .andExpect(status().isCreated());

        // Validate the Cities in the database
        List<Cities> cities = citiesRepository.findAll();
        assertThat(cities).hasSize(databaseSizeBeforeCreate + 1);
        Cities testCities = cities.get(cities.size() - 1);
        assertThat(testCities.getCityname()).isEqualTo(DEFAULT_CITYNAME);
        assertThat(testCities.getCitylocation()).isEqualTo(DEFAULT_CITYLOCATION);
    }

    @Test
    public void getAllCities() throws Exception {
        // Initialize the database
        citiesRepository.save(cities);

        // Get all the cities
        restCitiesMockMvc.perform(get("/api/cities?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(cities.getId())))
                .andExpect(jsonPath("$.[*].cityname").value(hasItem(DEFAULT_CITYNAME.toString())))
                .andExpect(jsonPath("$.[*].citylocation").value(hasItem(DEFAULT_CITYLOCATION.toString())));
    }

    @Test
    public void getCities() throws Exception {
        // Initialize the database
        citiesRepository.save(cities);

        // Get the cities
        restCitiesMockMvc.perform(get("/api/cities/{id}", cities.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(cities.getId()))
            .andExpect(jsonPath("$.cityname").value(DEFAULT_CITYNAME.toString()))
            .andExpect(jsonPath("$.citylocation").value(DEFAULT_CITYLOCATION.toString()));
    }

    @Test
    public void getNonExistingCities() throws Exception {
        // Get the cities
        restCitiesMockMvc.perform(get("/api/cities/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateCities() throws Exception {
        // Initialize the database
        citiesRepository.save(cities);
        int databaseSizeBeforeUpdate = citiesRepository.findAll().size();

        // Update the cities
        Cities updatedCities = new Cities();
        updatedCities.setId(cities.getId());
        updatedCities.setCityname(UPDATED_CITYNAME);
        //updatedCities.setCitylocation(UPDATED_CITYLOCATION);

        restCitiesMockMvc.perform(put("/api/cities")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedCities)))
                .andExpect(status().isOk());

        // Validate the Cities in the database
        List<Cities> cities = citiesRepository.findAll();
        assertThat(cities).hasSize(databaseSizeBeforeUpdate);
        Cities testCities = cities.get(cities.size() - 1);
        assertThat(testCities.getCityname()).isEqualTo(UPDATED_CITYNAME);
       // assertThat(testCities.getCitylocation()).isEqualTo(UPDATED_CITYLOCATION);
    }

    @Test
    public void deleteCities() throws Exception {
        // Initialize the database
        citiesRepository.save(cities);
        int databaseSizeBeforeDelete = citiesRepository.findAll().size();

        // Get the cities
        restCitiesMockMvc.perform(delete("/api/cities/{id}", cities.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Cities> cities = citiesRepository.findAll();
        assertThat(cities).hasSize(databaseSizeBeforeDelete - 1);
    }
}
