package com.mistminds.web.rest;

import com.mistminds.Application;
import com.mistminds.domain.ConsumerRegions;
import com.mistminds.domain.Region;
import com.mistminds.repository.ConsumerRegionsRepository;

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

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the ConsumerRegionsResource REST controller.
 *
 * @see ConsumerRegionsResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class ConsumerRegionsResourceIntTest {

    private static final String DEFAULT_CONSUMER_ID = "AAAAA";
    private static final String UPDATED_CONSUMER_ID = "BBBBB";
    private static final List<Region> DEFAULT_REGION = Arrays.asList(new Region());
    private static final List<Region> UPDATED_REGION = Arrays.asList(new Region());

    @Inject
    private ConsumerRegionsRepository consumerRegionsRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restConsumerRegionsMockMvc;

    private ConsumerRegions consumerRegions;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ConsumerRegionsResource consumerRegionsResource = new ConsumerRegionsResource();
        ReflectionTestUtils.setField(consumerRegionsResource, "consumerRegionsRepository", consumerRegionsRepository);
        this.restConsumerRegionsMockMvc = MockMvcBuilders.standaloneSetup(consumerRegionsResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        consumerRegionsRepository.deleteAll();
        consumerRegions = new ConsumerRegions();
        consumerRegions.setConsumerId(DEFAULT_CONSUMER_ID);
        consumerRegions.setRegion(DEFAULT_REGION);
    }

    @Test
    public void createConsumerRegions() throws Exception {
        int databaseSizeBeforeCreate = consumerRegionsRepository.findAll().size();

        // Create the ConsumerRegions

        restConsumerRegionsMockMvc.perform(post("/api/consumerRegionss")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(consumerRegions)))
                .andExpect(status().isCreated());

        // Validate the ConsumerRegions in the database
        List<ConsumerRegions> consumerRegionss = consumerRegionsRepository.findAll();
        assertThat(consumerRegionss).hasSize(databaseSizeBeforeCreate + 1);
        ConsumerRegions testConsumerRegions = consumerRegionss.get(consumerRegionss.size() - 1);
        assertThat(testConsumerRegions.getConsumerId()).isEqualTo(DEFAULT_CONSUMER_ID);
        assertThat(testConsumerRegions.getRegion()).isEqualTo(DEFAULT_REGION);
    }

    @Test
    public void getAllConsumerRegionss() throws Exception {
        // Initialize the database
        consumerRegionsRepository.save(consumerRegions);

        // Get all the consumerRegionss
        restConsumerRegionsMockMvc.perform(get("/api/consumerRegionss?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(consumerRegions.getId())))
                .andExpect(jsonPath("$.[*].consumerId").value(hasItem(DEFAULT_CONSUMER_ID.toString())))
                .andExpect(jsonPath("$.[*].region").value(hasItem(DEFAULT_REGION.toString())));
    }

    @Test
    public void getConsumerRegions() throws Exception {
        // Initialize the database
        consumerRegionsRepository.save(consumerRegions);

        // Get the consumerRegions
        restConsumerRegionsMockMvc.perform(get("/api/consumerRegionss/{id}", consumerRegions.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(consumerRegions.getId()))
            .andExpect(jsonPath("$.consumerId").value(DEFAULT_CONSUMER_ID.toString()))
            .andExpect(jsonPath("$.region").value(DEFAULT_REGION.toString()));
    }

    @Test
    public void getNonExistingConsumerRegions() throws Exception {
        // Get the consumerRegions
        restConsumerRegionsMockMvc.perform(get("/api/consumerRegionss/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateConsumerRegions() throws Exception {
        // Initialize the database
        consumerRegionsRepository.save(consumerRegions);

		int databaseSizeBeforeUpdate = consumerRegionsRepository.findAll().size();

        // Update the consumerRegions
        consumerRegions.setConsumerId(UPDATED_CONSUMER_ID);
        consumerRegions.setRegion(UPDATED_REGION);

        restConsumerRegionsMockMvc.perform(put("/api/consumerRegionss")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(consumerRegions)))
                .andExpect(status().isOk());

        // Validate the ConsumerRegions in the database
        List<ConsumerRegions> consumerRegionss = consumerRegionsRepository.findAll();
        assertThat(consumerRegionss).hasSize(databaseSizeBeforeUpdate);
        ConsumerRegions testConsumerRegions = consumerRegionss.get(consumerRegionss.size() - 1);
        assertThat(testConsumerRegions.getConsumerId()).isEqualTo(UPDATED_CONSUMER_ID);
        assertThat(testConsumerRegions.getRegion()).isEqualTo(UPDATED_REGION);
    }

    @Test
    public void deleteConsumerRegions() throws Exception {
        // Initialize the database
        consumerRegionsRepository.save(consumerRegions);

		int databaseSizeBeforeDelete = consumerRegionsRepository.findAll().size();

        // Get the consumerRegions
        restConsumerRegionsMockMvc.perform(delete("/api/consumerRegionss/{id}", consumerRegions.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<ConsumerRegions> consumerRegionss = consumerRegionsRepository.findAll();
        assertThat(consumerRegionss).hasSize(databaseSizeBeforeDelete - 1);
    }
}
