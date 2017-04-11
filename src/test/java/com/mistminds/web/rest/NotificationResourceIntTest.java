package com.mistminds.web.rest;

import com.mistminds.Application;
import com.mistminds.domain.Notification;
import com.mistminds.repository.NotificationRepository;

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
 * Test class for the NotificationResource REST controller.
 *
 * @see NotificationResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class NotificationResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.of("Z"));

    private static final String DEFAULT_CATEGERY = "AAAAA";
    private static final String UPDATED_CATEGERY = "BBBBB";
    private static final String DEFAULT_TITLE = "AAAAA";
    private static final String UPDATED_TITLE = "BBBBB";
    private static final String DEFAULT_DESCRIPTION = "AAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBB";

    private static final ZonedDateTime DEFAULT_VALID_FROM = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_VALID_FROM = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_VALID_FROM_STR = dateTimeFormatter.format(DEFAULT_VALID_FROM);

    private static final ZonedDateTime DEFAULT_VALID_TO = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_VALID_TO = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_VALID_TO_STR = dateTimeFormatter.format(DEFAULT_VALID_TO);
    private static final String DEFAULT_SECURE_URL = "AAAAA";

    private static final ZonedDateTime DEFAULT_EXPIERY_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final String DEFAULT_EXPIERY_DATE_STR = dateTimeFormatter.format(DEFAULT_EXPIERY_DATE);
    private static final String DEFAULT_DELIVERED = "AAAAA";

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final ZonedDateTime DEFAULT_CREATED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_CREATED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_CREATED_STR = dateTimeFormatter.format(DEFAULT_CREATED);

    private static final ZonedDateTime DEFAULT_UPDATED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_UPDATED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_UPDATED_STR = dateTimeFormatter.format(DEFAULT_UPDATED);
    private static final Boolean DEFAULT_OFFENSIVE = true;
    private static final Boolean UPDATED_OFFENSIVE = false;
    private static final String DEFAULT_PUBLIC_ID = "AAAAA";
    private static final String DEFAULT_URL = "AAAAA";
    private static final String DEFAULT_CONSUMER_ID = "AAAAA";
    private static final String UPDATED_CONSUMER_ID = "BBBBB";

    @Inject
    private NotificationRepository notificationRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restNotificationMockMvc;

    private Notification notification;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        NotificationResource notificationResource = new NotificationResource();
        ReflectionTestUtils.setField(notificationResource, "notificationRepository", notificationRepository);
        this.restNotificationMockMvc = MockMvcBuilders.standaloneSetup(notificationResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        notificationRepository.deleteAll();
        notification = new Notification();
        notification.setCategory(DEFAULT_CATEGERY);
        notification.setTitle(DEFAULT_TITLE);
        notification.setDescription(DEFAULT_DESCRIPTION);
        notification.setValidFrom(DEFAULT_VALID_FROM);
        notification.setValidTo(DEFAULT_VALID_TO);
        notification.setActive(DEFAULT_ACTIVE);
        notification.setCreated(DEFAULT_CREATED);
        notification.setLastUpdate(DEFAULT_UPDATED);
        notification.setOffensive(DEFAULT_OFFENSIVE);
        notification.setConsumerId(DEFAULT_CONSUMER_ID);
    }

    @Test
    public void createNotification() throws Exception {
        int databaseSizeBeforeCreate = notificationRepository.findAll().size();

        // Create the Notification

        restNotificationMockMvc.perform(post("/api/notifications")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(notification)))
                .andExpect(status().isCreated());

        // Validate the Notification in the database
        List<Notification> notifications = notificationRepository.findAll();
        assertThat(notifications).hasSize(databaseSizeBeforeCreate + 1);
        Notification testNotification = notifications.get(notifications.size() - 1);
        assertThat(testNotification.getCategoryId()).isEqualTo(DEFAULT_CATEGERY);
        assertThat(testNotification.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testNotification.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testNotification.getValidFrom()).isEqualTo(DEFAULT_VALID_FROM);
        assertThat(testNotification.getValidTo()).isEqualTo(DEFAULT_VALID_TO);
        assertThat(testNotification.getActive()).isEqualTo(DEFAULT_ACTIVE);
        assertThat(testNotification.getCreated()).isEqualTo(DEFAULT_CREATED);
        assertThat(testNotification.getOffensive()).isEqualTo(DEFAULT_OFFENSIVE);
        assertThat(testNotification.getConsumerId()).isEqualTo(DEFAULT_CONSUMER_ID);
    }

    @Test
    public void getAllNotifications() throws Exception {
        // Initialize the database
        notificationRepository.save(notification);

        // Get all the notifications
        restNotificationMockMvc.perform(get("/api/notifications?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(notification.getId())))
                .andExpect(jsonPath("$.[*].categery").value(hasItem(DEFAULT_CATEGERY.toString())))
                .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].validFrom").value(hasItem(DEFAULT_VALID_FROM_STR)))
                .andExpect(jsonPath("$.[*].validTo").value(hasItem(DEFAULT_VALID_TO_STR)))
                .andExpect(jsonPath("$.[*].secureUrl").value(hasItem(DEFAULT_SECURE_URL.toString())))
                .andExpect(jsonPath("$.[*].expieryDate").value(hasItem(DEFAULT_EXPIERY_DATE_STR)))
                .andExpect(jsonPath("$.[*].delivered").value(hasItem(DEFAULT_DELIVERED.toString())))
                .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())))
                .andExpect(jsonPath("$.[*].created").value(hasItem(DEFAULT_CREATED_STR)))
                .andExpect(jsonPath("$.[*].updated").value(hasItem(DEFAULT_UPDATED_STR)))
                .andExpect(jsonPath("$.[*].offensive").value(hasItem(DEFAULT_OFFENSIVE.toString())))
                .andExpect(jsonPath("$.[*].publicId").value(hasItem(DEFAULT_PUBLIC_ID.toString())))
                .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL.toString())))
                .andExpect(jsonPath("$.[*].consumerId").value(hasItem(DEFAULT_CONSUMER_ID.toString())));
    }

    @Test
    public void getNotification() throws Exception {
        // Initialize the database
        notificationRepository.save(notification);

        // Get the notification
        restNotificationMockMvc.perform(get("/api/notifications/{id}", notification.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(notification.getId()))
            .andExpect(jsonPath("$.categery").value(DEFAULT_CATEGERY.toString()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.validFrom").value(DEFAULT_VALID_FROM_STR))
            .andExpect(jsonPath("$.validTo").value(DEFAULT_VALID_TO_STR))
            .andExpect(jsonPath("$.secureUrl").value(DEFAULT_SECURE_URL.toString()))
            .andExpect(jsonPath("$.expieryDate").value(DEFAULT_EXPIERY_DATE_STR))
            .andExpect(jsonPath("$.delivered").value(DEFAULT_DELIVERED.toString()))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()))
            .andExpect(jsonPath("$.created").value(DEFAULT_CREATED_STR))
            .andExpect(jsonPath("$.updated").value(DEFAULT_UPDATED_STR))
            .andExpect(jsonPath("$.offensive").value(DEFAULT_OFFENSIVE.toString()))
            .andExpect(jsonPath("$.publicId").value(DEFAULT_PUBLIC_ID.toString()))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL.toString()))
            .andExpect(jsonPath("$.consumerId").value(DEFAULT_CONSUMER_ID.toString()));
    }

    @Test
    public void getNonExistingNotification() throws Exception {
        // Get the notification
        restNotificationMockMvc.perform(get("/api/notifications/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateNotification() throws Exception {
        // Initialize the database
        notificationRepository.save(notification);

		int databaseSizeBeforeUpdate = notificationRepository.findAll().size();

        // Update the notification
        notification.setCategory(UPDATED_CATEGERY);
        notification.setTitle(UPDATED_TITLE);
        notification.setDescription(UPDATED_DESCRIPTION);
        notification.setValidFrom(UPDATED_VALID_FROM);
        notification.setValidTo(UPDATED_VALID_TO);
        notification.setActive(UPDATED_ACTIVE);
        notification.setCreated(UPDATED_CREATED);
        notification.setLastUpdate(UPDATED_UPDATED);
        notification.setOffensive(UPDATED_OFFENSIVE);
        notification.setConsumerId(UPDATED_CONSUMER_ID);

        restNotificationMockMvc.perform(put("/api/notifications")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(notification)))
                .andExpect(status().isOk());

        // Validate the Notification in the database
        List<Notification> notifications = notificationRepository.findAll();
        assertThat(notifications).hasSize(databaseSizeBeforeUpdate);
        Notification testNotification = notifications.get(notifications.size() - 1);
        assertThat(testNotification.getCategoryId()).isEqualTo(UPDATED_CATEGERY);
        assertThat(testNotification.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testNotification.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testNotification.getValidFrom()).isEqualTo(UPDATED_VALID_FROM);
        assertThat(testNotification.getValidTo()).isEqualTo(UPDATED_VALID_TO);
        assertThat(testNotification.getActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testNotification.getCreated()).isEqualTo(UPDATED_CREATED);
        assertThat(testNotification.getOffensive()).isEqualTo(UPDATED_OFFENSIVE);
        assertThat(testNotification.getConsumerId()).isEqualTo(UPDATED_CONSUMER_ID);
    }

    @Test
    public void deleteNotification() throws Exception {
        // Initialize the database
        notificationRepository.save(notification);

		int databaseSizeBeforeDelete = notificationRepository.findAll().size();

        // Get the notification
        restNotificationMockMvc.perform(delete("/api/notifications/{id}", notification.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Notification> notifications = notificationRepository.findAll();
        assertThat(notifications).hasSize(databaseSizeBeforeDelete - 1);
    }
}
