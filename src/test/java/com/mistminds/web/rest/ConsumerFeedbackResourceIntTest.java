package com.mistminds.web.rest;

import com.mistminds.Application;
import com.mistminds.domain.ConsumerFeedback;
import com.mistminds.repository.ConsumerFeedbackRepository;

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
 * Test class for the ConsumerFeedbackResource REST controller.
 *
 * @see ConsumerFeedbackResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class ConsumerFeedbackResourceIntTest {

    private static final String DEFAULT_COMMENT = "AAAAA";
    private static final String UPDATED_COMMENT = "BBBBB";
    private static final String DEFAULT_LIKE_OR_DISLIKE = "AAAAA";
    private static final String UPDATED_LIKE_OR_DISLIKE = "BBBBB";
    private static final String DEFAULT_NOTIFICATION_ID = "AAAAA";
    private static final String UPDATED_NOTIFICATION_ID = "BBBBB";
    private static final String DEFAULT_CONSUMER_ID = "AAAAA";
    private static final String UPDATED_CONSUMER_ID = "BBBBB";

    @Inject
    private ConsumerFeedbackRepository consumerFeedbackRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restConsumerFeedbackMockMvc;

    private ConsumerFeedback consumerFeedback;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ConsumerFeedbackResource consumerFeedbackResource = new ConsumerFeedbackResource();
        ReflectionTestUtils.setField(consumerFeedbackResource, "consumerFeedbackRepository", consumerFeedbackRepository);
        this.restConsumerFeedbackMockMvc = MockMvcBuilders.standaloneSetup(consumerFeedbackResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        consumerFeedbackRepository.deleteAll();
        consumerFeedback = new ConsumerFeedback();
        consumerFeedback.setComment(DEFAULT_COMMENT);
        consumerFeedback.setLikeDislike(DEFAULT_LIKE_OR_DISLIKE);
        consumerFeedback.setNotificationId(DEFAULT_NOTIFICATION_ID);
        consumerFeedback.setConsumerId(DEFAULT_CONSUMER_ID);
    }

    @Test
    public void createConsumerFeedback() throws Exception {
        int databaseSizeBeforeCreate = consumerFeedbackRepository.findAll().size();

        // Create the ConsumerFeedback

        restConsumerFeedbackMockMvc.perform(post("/api/consumerFeedbacks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(consumerFeedback)))
                .andExpect(status().isCreated());

        // Validate the ConsumerFeedback in the database
        List<ConsumerFeedback> consumerFeedbacks = consumerFeedbackRepository.findAll();
        assertThat(consumerFeedbacks).hasSize(databaseSizeBeforeCreate + 1);
        ConsumerFeedback testConsumerFeedback = consumerFeedbacks.get(consumerFeedbacks.size() - 1);
        assertThat(testConsumerFeedback.getComment()).isEqualTo(DEFAULT_COMMENT);
        assertThat(testConsumerFeedback.getLikeDislike()).isEqualTo(DEFAULT_LIKE_OR_DISLIKE);
        assertThat(testConsumerFeedback.getNotificationId()).isEqualTo(DEFAULT_NOTIFICATION_ID);
        assertThat(testConsumerFeedback.getConsumerId()).isEqualTo(DEFAULT_CONSUMER_ID);
    }

    @Test
    public void getAllConsumerFeedbacks() throws Exception {
        // Initialize the database
        consumerFeedbackRepository.save(consumerFeedback);

        // Get all the consumerFeedbacks
        restConsumerFeedbackMockMvc.perform(get("/api/consumerFeedbacks?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(consumerFeedback.getId())))
                .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
                .andExpect(jsonPath("$.[*].likeOrDislike").value(hasItem(DEFAULT_LIKE_OR_DISLIKE.toString())))
                .andExpect(jsonPath("$.[*].notificationId").value(hasItem(DEFAULT_NOTIFICATION_ID.toString())))
                .andExpect(jsonPath("$.[*].consumerId").value(hasItem(DEFAULT_CONSUMER_ID.toString())));
    }

    @Test
    public void getConsumerFeedback() throws Exception {
        // Initialize the database
        consumerFeedbackRepository.save(consumerFeedback);

        // Get the consumerFeedback
        restConsumerFeedbackMockMvc.perform(get("/api/consumerFeedbacks/{id}", consumerFeedback.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(consumerFeedback.getId()))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT.toString()))
            .andExpect(jsonPath("$.likeOrDislike").value(DEFAULT_LIKE_OR_DISLIKE.toString()))
            .andExpect(jsonPath("$.notificationId").value(DEFAULT_NOTIFICATION_ID.toString()))
            .andExpect(jsonPath("$.consumerId").value(DEFAULT_CONSUMER_ID.toString()));
    }

    @Test
    public void getNonExistingConsumerFeedback() throws Exception {
        // Get the consumerFeedback
        restConsumerFeedbackMockMvc.perform(get("/api/consumerFeedbacks/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateConsumerFeedback() throws Exception {
        // Initialize the database
        consumerFeedbackRepository.save(consumerFeedback);

		int databaseSizeBeforeUpdate = consumerFeedbackRepository.findAll().size();

        // Update the consumerFeedback
        consumerFeedback.setComment(UPDATED_COMMENT);
        consumerFeedback.setLikeDislike(UPDATED_LIKE_OR_DISLIKE);
        consumerFeedback.setNotificationId(UPDATED_NOTIFICATION_ID);
        consumerFeedback.setConsumerId(UPDATED_CONSUMER_ID);

        restConsumerFeedbackMockMvc.perform(put("/api/consumerFeedbacks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(consumerFeedback)))
                .andExpect(status().isOk());

        // Validate the ConsumerFeedback in the database
        List<ConsumerFeedback> consumerFeedbacks = consumerFeedbackRepository.findAll();
        assertThat(consumerFeedbacks).hasSize(databaseSizeBeforeUpdate);
        ConsumerFeedback testConsumerFeedback = consumerFeedbacks.get(consumerFeedbacks.size() - 1);
        assertThat(testConsumerFeedback.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testConsumerFeedback.getLikeDislike()).isEqualTo(UPDATED_LIKE_OR_DISLIKE);
        assertThat(testConsumerFeedback.getNotificationId()).isEqualTo(UPDATED_NOTIFICATION_ID);
        assertThat(testConsumerFeedback.getConsumerId()).isEqualTo(UPDATED_CONSUMER_ID);
    }

    @Test
    public void deleteConsumerFeedback() throws Exception {
        // Initialize the database
        consumerFeedbackRepository.save(consumerFeedback);

		int databaseSizeBeforeDelete = consumerFeedbackRepository.findAll().size();

        // Get the consumerFeedback
        restConsumerFeedbackMockMvc.perform(delete("/api/consumerFeedbacks/{id}", consumerFeedback.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<ConsumerFeedback> consumerFeedbacks = consumerFeedbackRepository.findAll();
        assertThat(consumerFeedbacks).hasSize(databaseSizeBeforeDelete - 1);
    }
}
