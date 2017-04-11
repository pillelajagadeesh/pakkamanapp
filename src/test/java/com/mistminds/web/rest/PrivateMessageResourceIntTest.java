package com.mistminds.web.rest;

import com.mistminds.Application;
import com.mistminds.domain.PrivateMessage;
import com.mistminds.repository.PrivateMessageRepository;

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
 * Test class for the PrivateMessageResource REST controller.
 *
 * @see PrivateMessageResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class PrivateMessageResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.of("Z"));

    private static final String DEFAULT_MESSAGE = "AAAAA";
    private static final String UPDATED_MESSAGE = "BBBBB";

    private static final ZonedDateTime DEFAULT_READ = ZonedDateTime.now();
    private static final ZonedDateTime UPDATED_READ = ZonedDateTime.now();
    private static final ZonedDateTime DEFAULT_DELIVERED = ZonedDateTime.now();
    private static final ZonedDateTime UPDATED_DELIVERED = ZonedDateTime.now();

    private static final ZonedDateTime DEFAULT_CREATED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_CREATED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_CREATED_STR = dateTimeFormatter.format(DEFAULT_CREATED);
    private static final String DEFAULT_NOTIFICATION_ID = "AAAAA";
    private static final String UPDATED_NOTIFICATION_ID = "BBBBB";
    private static final String DEFAULT_SENDER_ID = "AAAAA";
    private static final String UPDATED_SENDER_ID = "BBBBB";
    private static final String DEFAULT_RECEIVER_ID = "AAAAA";
    private static final String UPDATED_RECEIVER_ID = "BBBBB";

    @Inject
    private PrivateMessageRepository privateMessageRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restPrivateMessageMockMvc;

    private PrivateMessage privateMessage;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PrivateMessageResource privateMessageResource = new PrivateMessageResource();
        ReflectionTestUtils.setField(privateMessageResource, "privateMessageRepository", privateMessageRepository);
        this.restPrivateMessageMockMvc = MockMvcBuilders.standaloneSetup(privateMessageResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        privateMessageRepository.deleteAll();
        privateMessage = new PrivateMessage();
        privateMessage.setMessage(DEFAULT_MESSAGE);
        privateMessage.setRead(DEFAULT_READ);
        privateMessage.setDelivered(DEFAULT_DELIVERED);
        privateMessage.setCreated(DEFAULT_CREATED);
        privateMessage.setNotificationId(DEFAULT_NOTIFICATION_ID);
        privateMessage.setSenderId(DEFAULT_SENDER_ID);
        privateMessage.setReceiverId(DEFAULT_RECEIVER_ID);
    }

    @Test
    public void createPrivateMessage() throws Exception {
        int databaseSizeBeforeCreate = privateMessageRepository.findAll().size();

        // Create the PrivateMessage

        restPrivateMessageMockMvc.perform(post("/api/privateMessages")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(privateMessage)))
                .andExpect(status().isCreated());

        // Validate the PrivateMessage in the database
        List<PrivateMessage> privateMessages = privateMessageRepository.findAll();
        assertThat(privateMessages).hasSize(databaseSizeBeforeCreate + 1);
        PrivateMessage testPrivateMessage = privateMessages.get(privateMessages.size() - 1);
        assertThat(testPrivateMessage.getMessage()).isEqualTo(DEFAULT_MESSAGE);
        assertThat(testPrivateMessage.getRead()).isEqualTo(DEFAULT_READ);
        assertThat(testPrivateMessage.getDelivered()).isEqualTo(DEFAULT_DELIVERED);
        assertThat(testPrivateMessage.getCreated()).isEqualTo(DEFAULT_CREATED);
        assertThat(testPrivateMessage.getNotificationId()).isEqualTo(DEFAULT_NOTIFICATION_ID);
        assertThat(testPrivateMessage.getSenderId()).isEqualTo(DEFAULT_SENDER_ID);
        assertThat(testPrivateMessage.getReceiverId()).isEqualTo(DEFAULT_RECEIVER_ID);
    }

    @Test
    public void getAllPrivateMessages() throws Exception {
        // Initialize the database
        privateMessageRepository.save(privateMessage);

        // Get all the privateMessages
        restPrivateMessageMockMvc.perform(get("/api/privateMessages?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(privateMessage.getId())))
                .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE.toString())))
                .andExpect(jsonPath("$.[*].read").value(hasItem(DEFAULT_READ)))
                .andExpect(jsonPath("$.[*].delivered").value(hasItem(DEFAULT_DELIVERED.toString())))
                .andExpect(jsonPath("$.[*].created").value(hasItem(DEFAULT_CREATED_STR)))
                .andExpect(jsonPath("$.[*].notificationId").value(hasItem(DEFAULT_NOTIFICATION_ID.toString())))
                .andExpect(jsonPath("$.[*].senderId").value(hasItem(DEFAULT_SENDER_ID.toString())))
                .andExpect(jsonPath("$.[*].receiverId").value(hasItem(DEFAULT_RECEIVER_ID.toString())));
    }

    @Test
    public void getPrivateMessage() throws Exception {
        // Initialize the database
        privateMessageRepository.save(privateMessage);

        // Get the privateMessage
        restPrivateMessageMockMvc.perform(get("/api/privateMessages/{id}", privateMessage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(privateMessage.getId()))
            .andExpect(jsonPath("$.message").value(DEFAULT_MESSAGE.toString()))
            .andExpect(jsonPath("$.read").value(DEFAULT_READ))
            .andExpect(jsonPath("$.delivered").value(DEFAULT_DELIVERED.toString()))
            .andExpect(jsonPath("$.created").value(DEFAULT_CREATED_STR))
            .andExpect(jsonPath("$.notificationId").value(DEFAULT_NOTIFICATION_ID.toString()))
            .andExpect(jsonPath("$.senderId").value(DEFAULT_SENDER_ID.toString()))
            .andExpect(jsonPath("$.receiverId").value(DEFAULT_RECEIVER_ID.toString()));
    }

    @Test
    public void getNonExistingPrivateMessage() throws Exception {
        // Get the privateMessage
        restPrivateMessageMockMvc.perform(get("/api/privateMessages/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updatePrivateMessage() throws Exception {
        // Initialize the database
        privateMessageRepository.save(privateMessage);

		int databaseSizeBeforeUpdate = privateMessageRepository.findAll().size();

        // Update the privateMessage
        privateMessage.setMessage(UPDATED_MESSAGE);
        privateMessage.setRead(UPDATED_READ);
        privateMessage.setDelivered(UPDATED_DELIVERED);
        privateMessage.setCreated(UPDATED_CREATED);
        privateMessage.setNotificationId(UPDATED_NOTIFICATION_ID);
        privateMessage.setSenderId(UPDATED_SENDER_ID);
        privateMessage.setReceiverId(UPDATED_RECEIVER_ID);

        restPrivateMessageMockMvc.perform(put("/api/privateMessages")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(privateMessage)))
                .andExpect(status().isOk());

        // Validate the PrivateMessage in the database
        List<PrivateMessage> privateMessages = privateMessageRepository.findAll();
        assertThat(privateMessages).hasSize(databaseSizeBeforeUpdate);
        PrivateMessage testPrivateMessage = privateMessages.get(privateMessages.size() - 1);
        assertThat(testPrivateMessage.getMessage()).isEqualTo(UPDATED_MESSAGE);
        assertThat(testPrivateMessage.getRead()).isEqualTo(UPDATED_READ);
        assertThat(testPrivateMessage.getDelivered()).isEqualTo(UPDATED_DELIVERED);
        assertThat(testPrivateMessage.getCreated()).isEqualTo(UPDATED_CREATED);
        assertThat(testPrivateMessage.getNotificationId()).isEqualTo(UPDATED_NOTIFICATION_ID);
        assertThat(testPrivateMessage.getSenderId()).isEqualTo(UPDATED_SENDER_ID);
        assertThat(testPrivateMessage.getReceiverId()).isEqualTo(UPDATED_RECEIVER_ID);
    }

    @Test
    public void deletePrivateMessage() throws Exception {
        // Initialize the database
        privateMessageRepository.save(privateMessage);

		int databaseSizeBeforeDelete = privateMessageRepository.findAll().size();

        // Get the privateMessage
        restPrivateMessageMockMvc.perform(delete("/api/privateMessages/{id}", privateMessage.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<PrivateMessage> privateMessages = privateMessageRepository.findAll();
        assertThat(privateMessages).hasSize(databaseSizeBeforeDelete - 1);
    }
}
