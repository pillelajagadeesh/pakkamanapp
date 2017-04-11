package com.mistminds.web.rest;

import com.mistminds.Application;
import com.mistminds.domain.Consumer;
import com.mistminds.repository.ConsumerRepository;

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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the ConsumerResource REST controller.
 *
 * @see ConsumerResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class ConsumerResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.of("Z"));

    private static final String DEFAULT_MOBILE = "AAAAA";
    private static final String UPDATED_MOBILE = "BBBBB";
    private static final String DEFAULT_EMAIL = "AAAAA";
    private static final String UPDATED_EMAIL = "BBBBB";
    private static final String DEFAULT_STATUS = "AAAAA";
    private static final String UPDATED_STATUS = "BBBBB";
    private static final String DEFAULT_OTP = "AAAAA";
    private static final String UPDATED_OTP = "BBBBB";

    private static final Integer DEFAULT_OTP_COUNT = 1;
    private static final Integer UPDATED_OTP_COUNT = 2;

    private static final ZonedDateTime DEFAULT_CREATED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_CREATED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_CREATED_STR = dateTimeFormatter.format(DEFAULT_CREATED);

    private static final ZonedDateTime DEFAULT_UPDATED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_UPDATED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_UPDATED_STR = dateTimeFormatter.format(DEFAULT_UPDATED);
    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    @Inject
    private ConsumerRepository consumerRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restConsumerMockMvc;

    private Consumer consumer;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ConsumerResource consumerResource = new ConsumerResource();
        ReflectionTestUtils.setField(consumerResource, "consumerRepository", consumerRepository);
        this.restConsumerMockMvc = MockMvcBuilders.standaloneSetup(consumerResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        consumerRepository.deleteAll();
        consumer = new Consumer();
        consumer.setMobile(DEFAULT_MOBILE);
        consumer.setEmail(DEFAULT_EMAIL);
        consumer.setStatus(DEFAULT_STATUS);
        consumer.setOtp(DEFAULT_OTP);
        consumer.setOtpCount(DEFAULT_OTP_COUNT);
        consumer.setCreated(DEFAULT_CREATED);
        consumer.setLastUpdate(DEFAULT_UPDATED);
        consumer.setName(DEFAULT_NAME);
    }

    @Test
    public void createConsumer() throws Exception {
        int databaseSizeBeforeCreate = consumerRepository.findAll().size();

        // Create the Consumer

        restConsumerMockMvc.perform(post("/api/consumers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(consumer)))
                .andExpect(status().isCreated());

        // Validate the Consumer in the database
        List<Consumer> consumers = consumerRepository.findAll();
        assertThat(consumers).hasSize(databaseSizeBeforeCreate + 1);
        Consumer testConsumer = consumers.get(consumers.size() - 1);
        assertThat(testConsumer.getMobile()).isEqualTo(DEFAULT_MOBILE);
        assertThat(testConsumer.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testConsumer.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testConsumer.getOtp()).isEqualTo(DEFAULT_OTP);
        assertThat(testConsumer.getOtpCount()).isEqualTo(DEFAULT_OTP_COUNT);
        assertThat(testConsumer.getCreated()).isEqualTo(DEFAULT_CREATED);
        assertThat(testConsumer.getLastUpdate()).isEqualTo(DEFAULT_UPDATED);
        assertThat(testConsumer.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    public void getAllConsumers() throws Exception {
        // Initialize the database
        consumerRepository.save(consumer);

        // Get all the consumers
        restConsumerMockMvc.perform(get("/api/consumers?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(consumer.getId())))
                .andExpect(jsonPath("$.[*].mobile").value(hasItem(DEFAULT_MOBILE.toString())))
                .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())))
                .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
                .andExpect(jsonPath("$.[*].otp").value(hasItem(DEFAULT_OTP.toString())))
                .andExpect(jsonPath("$.[*].otpCount").value(hasItem(DEFAULT_OTP_COUNT)))
                .andExpect(jsonPath("$.[*].created").value(hasItem(DEFAULT_CREATED_STR)))
                .andExpect(jsonPath("$.[*].updated").value(hasItem(DEFAULT_UPDATED_STR)))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    public void getConsumer() throws Exception {
        // Initialize the database
        consumerRepository.save(consumer);

        // Get the consumer
        restConsumerMockMvc.perform(get("/api/consumers/{id}", consumer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(consumer.getId()))
            .andExpect(jsonPath("$.mobile").value(DEFAULT_MOBILE.toString()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.otp").value(DEFAULT_OTP.toString()))
            .andExpect(jsonPath("$.otpCount").value(DEFAULT_OTP_COUNT))
            .andExpect(jsonPath("$.created").value(DEFAULT_CREATED_STR))
            .andExpect(jsonPath("$.updated").value(DEFAULT_UPDATED_STR))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    public void getNonExistingConsumer() throws Exception {
        // Get the consumer
        restConsumerMockMvc.perform(get("/api/consumers/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateConsumer() throws Exception {
        // Initialize the database
        consumerRepository.save(consumer);

		int databaseSizeBeforeUpdate = consumerRepository.findAll().size();

        // Update the consumer
        consumer.setMobile(UPDATED_MOBILE);
        consumer.setEmail(UPDATED_EMAIL);
        consumer.setStatus(UPDATED_STATUS);
        consumer.setOtp(UPDATED_OTP);
        consumer.setOtpCount(UPDATED_OTP_COUNT);
        consumer.setCreated(UPDATED_CREATED);
        consumer.setLastUpdate(UPDATED_UPDATED);
        consumer.setName(UPDATED_NAME);

        restConsumerMockMvc.perform(put("/api/consumers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(consumer)))
                .andExpect(status().isOk());

        // Validate the Consumer in the database
        List<Consumer> consumers = consumerRepository.findAll();
        assertThat(consumers).hasSize(databaseSizeBeforeUpdate);
        Consumer testConsumer = consumers.get(consumers.size() - 1);
        assertThat(testConsumer.getMobile()).isEqualTo(UPDATED_MOBILE);
        assertThat(testConsumer.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testConsumer.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testConsumer.getOtp()).isEqualTo(UPDATED_OTP);
        assertThat(testConsumer.getOtpCount()).isEqualTo(UPDATED_OTP_COUNT);
        assertThat(testConsumer.getCreated()).isEqualTo(UPDATED_CREATED);
        assertThat(testConsumer.getLastUpdate()).isEqualTo(UPDATED_UPDATED);
        assertThat(testConsumer.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    public void deleteConsumer() throws Exception {
        // Initialize the database
        consumerRepository.save(consumer);

		int databaseSizeBeforeDelete = consumerRepository.findAll().size();

        // Get the consumer
        restConsumerMockMvc.perform(delete("/api/consumers/{id}", consumer.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Consumer> consumers = consumerRepository.findAll();
        assertThat(consumers).hasSize(databaseSizeBeforeDelete - 1);
    }
}
