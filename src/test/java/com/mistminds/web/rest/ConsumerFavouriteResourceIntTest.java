package com.mistminds.web.rest;

import com.mistminds.Application;
import com.mistminds.domain.ConsumerFavourite;
import com.mistminds.repository.ConsumerFavouriteRepository;

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

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the ConsumerFavouriteResource REST controller.
 *
 * @see ConsumerFavouriteResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class ConsumerFavouriteResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.of("Z"));

    private static final String DEFAULT_CONSUMER_ID = "AAAAA";
    private static final String UPDATED_CONSUMER_ID = "BBBBB";
    private static final String DEFAULT_PROVIDER_ID = "AAAAA";
    private static final Set<String> UPDATED_PROVIDER_ID = new HashSet<String>(Arrays.asList("BBBBB"));

    private static final ZonedDateTime DEFAULT_CREATED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_CREATED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_CREATED_STR = dateTimeFormatter.format(DEFAULT_CREATED);

    private static final ZonedDateTime DEFAULT_UPDATED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_UPDATED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_UPDATED_STR = dateTimeFormatter.format(DEFAULT_UPDATED);

    @Inject
    private ConsumerFavouriteRepository consumerFavouriteRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restConsumerFavouriteMockMvc;

    private ConsumerFavourite consumerFavourite;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ConsumerFavouriteResource consumerFavouriteResource = new ConsumerFavouriteResource();
        ReflectionTestUtils.setField(consumerFavouriteResource, "consumerFavouriteRepository", consumerFavouriteRepository);
        this.restConsumerFavouriteMockMvc = MockMvcBuilders.standaloneSetup(consumerFavouriteResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        consumerFavouriteRepository.deleteAll();
        consumerFavourite = new ConsumerFavourite();
        consumerFavourite.setConsumerId(DEFAULT_CONSUMER_ID);
        consumerFavourite.setProviderId(UPDATED_PROVIDER_ID);
        consumerFavourite.setCreated(DEFAULT_CREATED);
        consumerFavourite.setLastUpdate(DEFAULT_UPDATED);
    }

    @Test
    public void createConsumerFavourite() throws Exception {
        int databaseSizeBeforeCreate = consumerFavouriteRepository.findAll().size();

        // Create the ConsumerFavourite

        restConsumerFavouriteMockMvc.perform(post("/api/consumerFavourites")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(consumerFavourite)))
                .andExpect(status().isCreated());

        // Validate the ConsumerFavourite in the database
        List<ConsumerFavourite> consumerFavourites = consumerFavouriteRepository.findAll();
        assertThat(consumerFavourites).hasSize(databaseSizeBeforeCreate + 1);
        ConsumerFavourite testConsumerFavourite = consumerFavourites.get(consumerFavourites.size() - 1);
        assertThat(testConsumerFavourite.getConsumerId()).isEqualTo(DEFAULT_CONSUMER_ID);
        assertThat(testConsumerFavourite.getProviderId()).isEqualTo(DEFAULT_PROVIDER_ID);
        assertThat(testConsumerFavourite.getCreated()).isEqualTo(DEFAULT_CREATED);
        assertThat(testConsumerFavourite.getLastUpdate()).isEqualTo(DEFAULT_UPDATED);
    }

    @Test
    public void getAllConsumerFavourites() throws Exception {
        // Initialize the database
        consumerFavouriteRepository.save(consumerFavourite);

        // Get all the consumerFavourites
        restConsumerFavouriteMockMvc.perform(get("/api/consumerFavourites?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(consumerFavourite.getId())))
                .andExpect(jsonPath("$.[*].consumerId").value(hasItem(DEFAULT_CONSUMER_ID.toString())))
                .andExpect(jsonPath("$.[*].providerId").value(hasItem(DEFAULT_PROVIDER_ID.toString())))
                .andExpect(jsonPath("$.[*].created").value(hasItem(DEFAULT_CREATED_STR)))
                .andExpect(jsonPath("$.[*].updated").value(hasItem(DEFAULT_UPDATED_STR)));
    }

    @Test
    public void getConsumerFavourite() throws Exception {
        // Initialize the database
        consumerFavouriteRepository.save(consumerFavourite);

        // Get the consumerFavourite
        restConsumerFavouriteMockMvc.perform(get("/api/consumerFavourites/{id}", consumerFavourite.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(consumerFavourite.getId()))
            .andExpect(jsonPath("$.consumerId").value(DEFAULT_CONSUMER_ID.toString()))
            .andExpect(jsonPath("$.providerId").value(DEFAULT_PROVIDER_ID.toString()))
            .andExpect(jsonPath("$.created").value(DEFAULT_CREATED_STR))
            .andExpect(jsonPath("$.updated").value(DEFAULT_UPDATED_STR));
    }

    @Test
    public void getNonExistingConsumerFavourite() throws Exception {
        // Get the consumerFavourite
        restConsumerFavouriteMockMvc.perform(get("/api/consumerFavourites/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateConsumerFavourite() throws Exception {
        // Initialize the database
        consumerFavouriteRepository.save(consumerFavourite);

		int databaseSizeBeforeUpdate = consumerFavouriteRepository.findAll().size();

        // Update the consumerFavourite
        consumerFavourite.setConsumerId(UPDATED_CONSUMER_ID);
        consumerFavourite.setProviderId(UPDATED_PROVIDER_ID);
        consumerFavourite.setCreated(UPDATED_CREATED);
        consumerFavourite.setLastUpdate(UPDATED_UPDATED);

        restConsumerFavouriteMockMvc.perform(put("/api/consumerFavourites")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(consumerFavourite)))
                .andExpect(status().isOk());

        // Validate the ConsumerFavourite in the database
        List<ConsumerFavourite> consumerFavourites = consumerFavouriteRepository.findAll();
        assertThat(consumerFavourites).hasSize(databaseSizeBeforeUpdate);
        ConsumerFavourite testConsumerFavourite = consumerFavourites.get(consumerFavourites.size() - 1);
        assertThat(testConsumerFavourite.getConsumerId()).isEqualTo(UPDATED_CONSUMER_ID);
        assertThat(testConsumerFavourite.getProviderId()).isEqualTo(UPDATED_PROVIDER_ID);
        assertThat(testConsumerFavourite.getCreated()).isEqualTo(UPDATED_CREATED);
        assertThat(testConsumerFavourite.getLastUpdate()).isEqualTo(UPDATED_UPDATED);
    }

    @Test
    public void deleteConsumerFavourite() throws Exception {
        // Initialize the database
        consumerFavouriteRepository.save(consumerFavourite);

		int databaseSizeBeforeDelete = consumerFavouriteRepository.findAll().size();

        // Get the consumerFavourite
        restConsumerFavouriteMockMvc.perform(delete("/api/consumerFavourites/{id}", consumerFavourite.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<ConsumerFavourite> consumerFavourites = consumerFavouriteRepository.findAll();
        assertThat(consumerFavourites).hasSize(databaseSizeBeforeDelete - 1);
    }
}
