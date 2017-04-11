package com.mistminds.web.rest;

import com.mistminds.Application;
import com.mistminds.domain.NotificationAcknowledgement;
import com.mistminds.repository.NotificationAcknowledgementRepository;

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

import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the NotificationAcknowledgementResource REST controller.
 *
 * @see NotificationAcknowledgementResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class NotificationAcknowledgementResourceIntTest {

    private static final ZonedDateTime DEFAULT_READ = ZonedDateTime.now();
    private static final ZonedDateTime UPDATED_READ = ZonedDateTime.now();
    private static final String DEFAULT_CONSUMER_ID = "AAAAA";
    private static final String UPDATED_CONSUMER_ID = "BBBBB";
    private static final String DEFAULT_NOTIFICATION_ID = "AAAAA";
    private static final String UPDATED_NOTIFICATION_ID = "BBBBB";
    private static final ZonedDateTime DEFAULT_DELIVERED = ZonedDateTime.now();
    private static final ZonedDateTime UPDATED_DELIVERED = ZonedDateTime.now();

    @Inject
    private NotificationAcknowledgementRepository notificationAcknowledgementRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restNotificationAcknowledgementMockMvc;

    private NotificationAcknowledgement notificationAcknowledgement;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        NotificationAcknowledgementResource notificationAcknowledgementResource = new NotificationAcknowledgementResource();
        ReflectionTestUtils.setField(notificationAcknowledgementResource, "notificationAcknowledgementRepository", notificationAcknowledgementRepository);
        this.restNotificationAcknowledgementMockMvc = MockMvcBuilders.standaloneSetup(notificationAcknowledgementResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        notificationAcknowledgementRepository.deleteAll();
        notificationAcknowledgement = new NotificationAcknowledgement();
        notificationAcknowledgement.setRead(DEFAULT_READ);
        notificationAcknowledgement.setConsumerId(DEFAULT_CONSUMER_ID);
        notificationAcknowledgement.setNotificationId(DEFAULT_NOTIFICATION_ID);
        notificationAcknowledgement.setDelivered(DEFAULT_DELIVERED);
    }

    @Test
    public void createNotificationAcknowledgement() throws Exception {
        int databaseSizeBeforeCreate = notificationAcknowledgementRepository.findAll().size();

        // Create the NotificationAcknowledgement

        restNotificationAcknowledgementMockMvc.perform(post("/api/notificationAcknowledgements")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(notificationAcknowledgement)))
                .andExpect(status().isCreated());

        // Validate the NotificationAcknowledgement in the database
        List<NotificationAcknowledgement> notificationAcknowledgements = notificationAcknowledgementRepository.findAll();
        assertThat(notificationAcknowledgements).hasSize(databaseSizeBeforeCreate + 1);
        NotificationAcknowledgement testNotificationAcknowledgement = notificationAcknowledgements.get(notificationAcknowledgements.size() - 1);
        assertThat(testNotificationAcknowledgement.getRead()).isEqualTo(DEFAULT_READ);
        assertThat(testNotificationAcknowledgement.getConsumerId()).isEqualTo(DEFAULT_CONSUMER_ID);
        assertThat(testNotificationAcknowledgement.getNotificationId()).isEqualTo(DEFAULT_NOTIFICATION_ID);
        assertThat(testNotificationAcknowledgement.getDelivered()).isEqualTo(DEFAULT_DELIVERED);
    }

    @Test
    public void getAllNotificationAcknowledgements() throws Exception {
        // Initialize the database
        notificationAcknowledgementRepository.save(notificationAcknowledgement);

        // Get all the notificationAcknowledgements
        restNotificationAcknowledgementMockMvc.perform(get("/api/notificationAcknowledgements?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(notificationAcknowledgement.getId())))
                .andExpect(jsonPath("$.[*].read").value(hasItem(DEFAULT_READ.toString())))
                .andExpect(jsonPath("$.[*].consumerId").value(hasItem(DEFAULT_CONSUMER_ID.toString())))
                .andExpect(jsonPath("$.[*].notificationId").value(hasItem(DEFAULT_NOTIFICATION_ID.toString())))
                .andExpect(jsonPath("$.[*].delivered").value(hasItem(DEFAULT_DELIVERED.toString())));
    }

    @Test
    public void getNotificationAcknowledgement() throws Exception {
        // Initialize the database
        notificationAcknowledgementRepository.save(notificationAcknowledgement);

        // Get the notificationAcknowledgement
        restNotificationAcknowledgementMockMvc.perform(get("/api/notificationAcknowledgements/{id}", notificationAcknowledgement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(notificationAcknowledgement.getId()))
            .andExpect(jsonPath("$.read").value(DEFAULT_READ.toString()))
            .andExpect(jsonPath("$.consumerId").value(DEFAULT_CONSUMER_ID.toString()))
            .andExpect(jsonPath("$.notificationId").value(DEFAULT_NOTIFICATION_ID.toString()))
            .andExpect(jsonPath("$.delivered").value(DEFAULT_DELIVERED.toString()));
    }

    @Test
    public void getNonExistingNotificationAcknowledgement() throws Exception {
        // Get the notificationAcknowledgement
        restNotificationAcknowledgementMockMvc.perform(get("/api/notificationAcknowledgements/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateNotificationAcknowledgement() throws Exception {
        // Initialize the database
        notificationAcknowledgementRepository.save(notificationAcknowledgement);

		int databaseSizeBeforeUpdate = notificationAcknowledgementRepository.findAll().size();

        // Update the notificationAcknowledgement
        notificationAcknowledgement.setRead(UPDATED_READ);
        notificationAcknowledgement.setConsumerId(UPDATED_CONSUMER_ID);
        notificationAcknowledgement.setNotificationId(UPDATED_NOTIFICATION_ID);
        notificationAcknowledgement.setDelivered(UPDATED_DELIVERED);

        restNotificationAcknowledgementMockMvc.perform(put("/api/notificationAcknowledgements")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(notificationAcknowledgement)))
                .andExpect(status().isOk());

        // Validate the NotificationAcknowledgement in the database
        List<NotificationAcknowledgement> notificationAcknowledgements = notificationAcknowledgementRepository.findAll();
        assertThat(notificationAcknowledgements).hasSize(databaseSizeBeforeUpdate);
        NotificationAcknowledgement testNotificationAcknowledgement = notificationAcknowledgements.get(notificationAcknowledgements.size() - 1);
        assertThat(testNotificationAcknowledgement.getRead()).isEqualTo(UPDATED_READ);
        assertThat(testNotificationAcknowledgement.getConsumerId()).isEqualTo(UPDATED_CONSUMER_ID);
        assertThat(testNotificationAcknowledgement.getNotificationId()).isEqualTo(UPDATED_NOTIFICATION_ID);
        assertThat(testNotificationAcknowledgement.getDelivered()).isEqualTo(UPDATED_DELIVERED);
    }

    @Test
    public void deleteNotificationAcknowledgement() throws Exception {
        // Initialize the database
        notificationAcknowledgementRepository.save(notificationAcknowledgement);

		int databaseSizeBeforeDelete = notificationAcknowledgementRepository.findAll().size();

        // Get the notificationAcknowledgement
        restNotificationAcknowledgementMockMvc.perform(delete("/api/notificationAcknowledgements/{id}", notificationAcknowledgement.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<NotificationAcknowledgement> notificationAcknowledgements = notificationAcknowledgementRepository.findAll();
        assertThat(notificationAcknowledgements).hasSize(databaseSizeBeforeDelete - 1);
    }
}
