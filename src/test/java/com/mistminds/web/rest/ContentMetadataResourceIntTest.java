package com.mistminds.web.rest;

import com.mistminds.Application;
import com.mistminds.domain.ContentMetadata;
import com.mistminds.repository.ContentMetadataRepository;

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
 * Test class for the ContentMetadataResource REST controller.
 *
 * @see ContentMetadataResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class ContentMetadataResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.of("Z"));

    private static final String DEFAULT_CONSUMER_ID = "AAAAA";
    private static final String DEFAULT_SIGNATURE = "AAAAA";
    private static final String UPDATED_SIGNATURE = "BBBBB";
    private static final String DEFAULT_FORMAT = "AAAAA";
    private static final String UPDATED_FORMAT = "BBBBB";
    private static final String DEFAULT_RESOURCE_TYPE = "AAAAA";
    private static final String DEFAULT_SECURE_URL = "AAAAA";

    private static final ZonedDateTime DEFAULT_CREATED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_CREATED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_CREATED_STR = dateTimeFormatter.format(DEFAULT_CREATED);
    private static final String DEFAULT_TYPE = "AAAAA";
    private static final String UPDATED_TYPE = "BBBBB";
    private static final String DEFAULT_VERSION = "AAAAA";
    private static final String UPDATED_VERSION = "BBBBB";
    private static final String DEFAULT_URL = "AAAAA";
    private static final String UPDATED_URL = "BBBBB";
    private static final String DEFAULT_PUBLIC_ID = "AAAAA";
    private static final String DEFAULT_TAGS = "AAAAA";
    private static final String UPDATED_TAGS = "BBBBB";
    private static final String DEFAULT_ORGINAL_FILE_NAME = "AAAAA";

    private static final Integer DEFAULT_BYTES = 1;
    private static final Integer UPDATED_BYTES = 2;

    private static final Integer DEFAULT_WIDTH = 1;
    private static final Integer UPDATED_WIDTH = 2;
    private static final String DEFAULT_E_TAG = "AAAAA";
    private static final String UPDATED_E_TAG = "BBBBB";

    private static final Integer DEFAULT_HEIGHT = 1;
    private static final Integer UPDATED_HEIGHT = 2;

    @Inject
    private ContentMetadataRepository contentMetadataRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restContentMetadataMockMvc;

    private ContentMetadata contentMetadata;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ContentMetadataResource contentMetadataResource = new ContentMetadataResource();
        ReflectionTestUtils.setField(contentMetadataResource, "contentMetadataRepository", contentMetadataRepository);
        this.restContentMetadataMockMvc = MockMvcBuilders.standaloneSetup(contentMetadataResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        contentMetadataRepository.deleteAll();
        contentMetadata = new ContentMetadata();
        contentMetadata.setSignature(DEFAULT_SIGNATURE);
        contentMetadata.setFormat(DEFAULT_FORMAT);
        contentMetadata.setCreated(DEFAULT_CREATED);
        contentMetadata.setType(DEFAULT_TYPE);
        contentMetadata.setUrl(DEFAULT_URL);
        contentMetadata.setTags(DEFAULT_TAGS);
        contentMetadata.setBytes(DEFAULT_BYTES);
        contentMetadata.setWidth(DEFAULT_WIDTH);
        contentMetadata.seteTag(DEFAULT_E_TAG);
        contentMetadata.setHeight(DEFAULT_HEIGHT);
    }

    @Test
    public void createContentMetadata() throws Exception {
        int databaseSizeBeforeCreate = contentMetadataRepository.findAll().size();

        // Create the ContentMetadata

        restContentMetadataMockMvc.perform(post("/api/contentMetadatas")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(contentMetadata)))
                .andExpect(status().isCreated());

        // Validate the ContentMetadata in the database
        List<ContentMetadata> contentMetadatas = contentMetadataRepository.findAll();
        assertThat(contentMetadatas).hasSize(databaseSizeBeforeCreate + 1);
        ContentMetadata testContentMetadata = contentMetadatas.get(contentMetadatas.size() - 1);
        assertThat(testContentMetadata.getSignature()).isEqualTo(DEFAULT_SIGNATURE);
        assertThat(testContentMetadata.getFormat()).isEqualTo(DEFAULT_FORMAT);
        assertThat(testContentMetadata.getCreated()).isEqualTo(DEFAULT_CREATED);
        assertThat(testContentMetadata.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testContentMetadata.getVersion()).isEqualTo(DEFAULT_VERSION);
        assertThat(testContentMetadata.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testContentMetadata.getTags()).isEqualTo(DEFAULT_TAGS);
        assertThat(testContentMetadata.getBytes()).isEqualTo(DEFAULT_BYTES);
        assertThat(testContentMetadata.getWidth()).isEqualTo(DEFAULT_WIDTH);
        assertThat(testContentMetadata.geteTag()).isEqualTo(DEFAULT_E_TAG);
        assertThat(testContentMetadata.getHeight()).isEqualTo(DEFAULT_HEIGHT);
    }

    @Test
    public void getAllContentMetadatas() throws Exception {
        // Initialize the database
        contentMetadataRepository.save(contentMetadata);

        // Get all the contentMetadatas
        restContentMetadataMockMvc.perform(get("/api/contentMetadatas?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(contentMetadata.getId())))
                .andExpect(jsonPath("$.[*].consumerId").value(hasItem(DEFAULT_CONSUMER_ID.toString())))
                .andExpect(jsonPath("$.[*].signature").value(hasItem(DEFAULT_SIGNATURE.toString())))
                .andExpect(jsonPath("$.[*].format").value(hasItem(DEFAULT_FORMAT.toString())))
                .andExpect(jsonPath("$.[*].resourceType").value(hasItem(DEFAULT_RESOURCE_TYPE.toString())))
                .andExpect(jsonPath("$.[*].secureUrl").value(hasItem(DEFAULT_SECURE_URL.toString())))
                .andExpect(jsonPath("$.[*].created").value(hasItem(DEFAULT_CREATED_STR)))
                .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
                .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION.toString())))
                .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL.toString())))
                .andExpect(jsonPath("$.[*].publicId").value(hasItem(DEFAULT_PUBLIC_ID.toString())))
                .andExpect(jsonPath("$.[*].tags").value(hasItem(DEFAULT_TAGS.toString())))
                .andExpect(jsonPath("$.[*].orginalFileName").value(hasItem(DEFAULT_ORGINAL_FILE_NAME.toString())))
                .andExpect(jsonPath("$.[*].bytes").value(hasItem(DEFAULT_BYTES)))
                .andExpect(jsonPath("$.[*].width").value(hasItem(DEFAULT_WIDTH)))
                .andExpect(jsonPath("$.[*].eTag").value(hasItem(DEFAULT_E_TAG.toString())))
                .andExpect(jsonPath("$.[*].height").value(hasItem(DEFAULT_HEIGHT)));
    }

    @Test
    public void getContentMetadata() throws Exception {
        // Initialize the database
        contentMetadataRepository.save(contentMetadata);

        // Get the contentMetadata
        restContentMetadataMockMvc.perform(get("/api/contentMetadatas/{id}", contentMetadata.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(contentMetadata.getId()))
            .andExpect(jsonPath("$.consumerId").value(DEFAULT_CONSUMER_ID.toString()))
            .andExpect(jsonPath("$.signature").value(DEFAULT_SIGNATURE.toString()))
            .andExpect(jsonPath("$.format").value(DEFAULT_FORMAT.toString()))
            .andExpect(jsonPath("$.resourceType").value(DEFAULT_RESOURCE_TYPE.toString()))
            .andExpect(jsonPath("$.secureUrl").value(DEFAULT_SECURE_URL.toString()))
            .andExpect(jsonPath("$.created").value(DEFAULT_CREATED_STR))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION.toString()))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL.toString()))
            .andExpect(jsonPath("$.publicId").value(DEFAULT_PUBLIC_ID.toString()))
            .andExpect(jsonPath("$.tags").value(DEFAULT_TAGS.toString()))
            .andExpect(jsonPath("$.orginalFileName").value(DEFAULT_ORGINAL_FILE_NAME.toString()))
            .andExpect(jsonPath("$.bytes").value(DEFAULT_BYTES))
            .andExpect(jsonPath("$.width").value(DEFAULT_WIDTH))
            .andExpect(jsonPath("$.eTag").value(DEFAULT_E_TAG.toString()))
            .andExpect(jsonPath("$.height").value(DEFAULT_HEIGHT));
    }

    @Test
    public void getNonExistingContentMetadata() throws Exception {
        // Get the contentMetadata
        restContentMetadataMockMvc.perform(get("/api/contentMetadatas/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateContentMetadata() throws Exception {
        // Initialize the database
        contentMetadataRepository.save(contentMetadata);

		int databaseSizeBeforeUpdate = contentMetadataRepository.findAll().size();

        // Update the contentMetadata
        contentMetadata.setSignature(UPDATED_SIGNATURE);
        contentMetadata.setFormat(UPDATED_FORMAT);
        contentMetadata.setCreated(UPDATED_CREATED);
        contentMetadata.setType(UPDATED_TYPE);
        contentMetadata.setUrl(UPDATED_URL);
        contentMetadata.setTags(UPDATED_TAGS);
        contentMetadata.setBytes(UPDATED_BYTES);
        contentMetadata.setWidth(UPDATED_WIDTH);
        contentMetadata.seteTag(UPDATED_E_TAG);
        contentMetadata.setHeight(UPDATED_HEIGHT);

        restContentMetadataMockMvc.perform(put("/api/contentMetadatas")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(contentMetadata)))
                .andExpect(status().isOk());

        // Validate the ContentMetadata in the database
        List<ContentMetadata> contentMetadatas = contentMetadataRepository.findAll();
        assertThat(contentMetadatas).hasSize(databaseSizeBeforeUpdate);
        ContentMetadata testContentMetadata = contentMetadatas.get(contentMetadatas.size() - 1);
        assertThat(testContentMetadata.getSignature()).isEqualTo(UPDATED_SIGNATURE);
        assertThat(testContentMetadata.getFormat()).isEqualTo(UPDATED_FORMAT);
        assertThat(testContentMetadata.getCreated()).isEqualTo(UPDATED_CREATED);
        assertThat(testContentMetadata.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testContentMetadata.getVersion()).isEqualTo(UPDATED_VERSION);
        assertThat(testContentMetadata.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testContentMetadata.getTags()).isEqualTo(UPDATED_TAGS);
        assertThat(testContentMetadata.getBytes()).isEqualTo(UPDATED_BYTES);
        assertThat(testContentMetadata.getWidth()).isEqualTo(UPDATED_WIDTH);
        assertThat(testContentMetadata.geteTag()).isEqualTo(UPDATED_E_TAG);
        assertThat(testContentMetadata.getHeight()).isEqualTo(UPDATED_HEIGHT);
    }

    @Test
    public void deleteContentMetadata() throws Exception {
        // Initialize the database
        contentMetadataRepository.save(contentMetadata);

		int databaseSizeBeforeDelete = contentMetadataRepository.findAll().size();

        // Get the contentMetadata
        restContentMetadataMockMvc.perform(delete("/api/contentMetadata/{id}", contentMetadata.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<ContentMetadata> contentMetadatas = contentMetadataRepository.findAll();
        assertThat(contentMetadatas).hasSize(databaseSizeBeforeDelete - 1);
    }
}
