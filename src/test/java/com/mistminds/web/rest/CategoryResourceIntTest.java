package com.mistminds.web.rest;

import com.mistminds.Application;
import com.mistminds.domain.Category;
import com.mistminds.repository.CategoryRepository;

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
 * Test class for the CategoryResource REST controller.
 *
 * @see CategoryResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class CategoryResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";
    private static final String DEFAULT_IMAGE_URL = "AAAAA";
    private static final String UPDATED_IMAGE_URL = "BBBBB";
    private static final String DEFAULT_MARKER_PINS = "AAAAA";
    private static final String UPDATED_MARKER_PINS = "BBBBB";
    private static final String DEFAULT_DESCRIPTION = "AAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBB";
    private static final String DEFAULT_PARENT_ID = "AAAAA";
    private static final String UPDATED_PARENT_ID = "BBBBB";

    private static final ZonedDateTime DEFAULT_CREATED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_CREATED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_CREATED_STR = dateTimeFormatter.format(DEFAULT_CREATED);

    private static final ZonedDateTime DEFAULT_DELETED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_DELETED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_DELETED_STR = dateTimeFormatter.format(DEFAULT_DELETED);

    private static final ZonedDateTime DEFAULT_LAST_UPDATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_LAST_UPDATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_LAST_UPDATE_STR = dateTimeFormatter.format(DEFAULT_LAST_UPDATE);

    private static final Integer DEFAULT_VERSION = 1;
    private static final Integer UPDATED_VERSION = 2;

    @Inject
    private CategoryRepository categoryRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restCategoryMockMvc;

    private Category category;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        CategoryResource categoryResource = new CategoryResource();
        ReflectionTestUtils.setField(categoryResource, "categoryRepository", categoryRepository);
        this.restCategoryMockMvc = MockMvcBuilders.standaloneSetup(categoryResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        categoryRepository.deleteAll();
        category = new Category();
        category.setName(DEFAULT_NAME);
        category.setImageUrl(DEFAULT_IMAGE_URL);
        category.setMarkerPins(DEFAULT_MARKER_PINS);
        category.setDescription(DEFAULT_DESCRIPTION);
        category.setParentId(DEFAULT_PARENT_ID);
        category.setCreated(DEFAULT_CREATED);
        category.setDeleted(DEFAULT_DELETED);
        category.setLastUpdate(DEFAULT_LAST_UPDATE);
        category.setVersion(DEFAULT_VERSION);
    }

    @Test
    public void createCategory() throws Exception {
        int databaseSizeBeforeCreate = categoryRepository.findAll().size();

        // Create the Category

        restCategoryMockMvc.perform(post("/api/categories")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(category)))
                .andExpect(status().isCreated());

        // Validate the Category in the database
        List<Category> categories = categoryRepository.findAll();
        assertThat(categories).hasSize(databaseSizeBeforeCreate + 1);
        Category testCategory = categories.get(categories.size() - 1);
        assertThat(testCategory.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCategory.getImageUrl()).isEqualTo(DEFAULT_IMAGE_URL);
        assertThat(testCategory.getMarkerPins()).isEqualTo(DEFAULT_MARKER_PINS);
        assertThat(testCategory.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testCategory.getParentId()).isEqualTo(DEFAULT_PARENT_ID);
        assertThat(testCategory.getCreated()).isEqualTo(DEFAULT_CREATED);
        assertThat(testCategory.getDeleted()).isEqualTo(DEFAULT_DELETED);
        assertThat(testCategory.getLastUpdate()).isEqualTo(DEFAULT_LAST_UPDATE);
        assertThat(testCategory.getVersion()).isEqualTo(DEFAULT_VERSION);
    }

    @Test
    public void getAllCategories() throws Exception {
        // Initialize the database
        categoryRepository.save(category);

        // Get all the categories
        restCategoryMockMvc.perform(get("/api/categories?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(category.getId())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGE_URL.toString())))
                .andExpect(jsonPath("$.[*].markerPins").value(hasItem(DEFAULT_MARKER_PINS.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].parentId").value(hasItem(DEFAULT_PARENT_ID.toString())))
                .andExpect(jsonPath("$.[*].created").value(hasItem(DEFAULT_CREATED_STR)))
                .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED_STR)))
                .andExpect(jsonPath("$.[*].lastUpdate").value(hasItem(DEFAULT_LAST_UPDATE_STR)))
                .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)));
    }

    @Test
    public void getCategory() throws Exception {
        // Initialize the database
        categoryRepository.save(category);

        // Get the category
        restCategoryMockMvc.perform(get("/api/categories/{id}", category.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(category.getId()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.imageUrl").value(DEFAULT_IMAGE_URL.toString()))
            .andExpect(jsonPath("$.markerPins").value(DEFAULT_MARKER_PINS.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.parentId").value(DEFAULT_PARENT_ID.toString()))
            .andExpect(jsonPath("$.created").value(DEFAULT_CREATED_STR))
            .andExpect(jsonPath("$.deleted").value(DEFAULT_DELETED_STR))
            .andExpect(jsonPath("$.lastUpdate").value(DEFAULT_LAST_UPDATE_STR))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION));
    }

    @Test
    public void getNonExistingCategory() throws Exception {
        // Get the category
        restCategoryMockMvc.perform(get("/api/categories/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateCategory() throws Exception {
        // Initialize the database
        categoryRepository.save(category);
        int databaseSizeBeforeUpdate = categoryRepository.findAll().size();

        // Update the category
        Category updatedCategory = new Category();
        updatedCategory.setId(category.getId());
        updatedCategory.setName(UPDATED_NAME);
        updatedCategory.setImageUrl(UPDATED_IMAGE_URL);
        updatedCategory.setMarkerPins(UPDATED_MARKER_PINS);
        updatedCategory.setDescription(UPDATED_DESCRIPTION);
        updatedCategory.setParentId(UPDATED_PARENT_ID);
        updatedCategory.setCreated(UPDATED_CREATED);
        updatedCategory.setDeleted(UPDATED_DELETED);
        updatedCategory.setLastUpdate(UPDATED_LAST_UPDATE);
        updatedCategory.setVersion(UPDATED_VERSION);

        restCategoryMockMvc.perform(put("/api/categories")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedCategory)))
                .andExpect(status().isOk());

        // Validate the Category in the database
        List<Category> categories = categoryRepository.findAll();
        assertThat(categories).hasSize(databaseSizeBeforeUpdate);
        Category testCategory = categories.get(categories.size() - 1);
        assertThat(testCategory.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCategory.getImageUrl()).isEqualTo(UPDATED_IMAGE_URL);
        assertThat(testCategory.getMarkerPins()).isEqualTo(UPDATED_MARKER_PINS);
        assertThat(testCategory.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testCategory.getParentId()).isEqualTo(UPDATED_PARENT_ID);
        assertThat(testCategory.getCreated()).isEqualTo(UPDATED_CREATED);
        assertThat(testCategory.getDeleted()).isEqualTo(UPDATED_DELETED);
        assertThat(testCategory.getLastUpdate()).isEqualTo(UPDATED_LAST_UPDATE);
        assertThat(testCategory.getVersion()).isEqualTo(UPDATED_VERSION);
    }

    @Test
    public void deleteCategory() throws Exception {
        // Initialize the database
        categoryRepository.save(category);
        int databaseSizeBeforeDelete = categoryRepository.findAll().size();

        // Get the category
        restCategoryMockMvc.perform(delete("/api/categories/{id}", category.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Category> categories = categoryRepository.findAll();
        assertThat(categories).hasSize(databaseSizeBeforeDelete - 1);
    }
}
