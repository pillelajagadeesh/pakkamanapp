package com.mistminds.web.rest;

import com.mistminds.domain.Localities;
import com.mistminds.repository.LocalitiesRepository;

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
 * Test class for the LocalitiesResource REST controller.
 *
 * @see LocalitiesResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = LocalitiesResourceIntTest.class)
@WebAppConfiguration
@IntegrationTest
public class LocalitiesResourceIntTest {

    private static final String DEFAULT_LOCALITYNAME = "AAAAA";
    private static final String UPDATED_LOCALITYNAME = "BBBBB";
    private static final String DEFAULT_LOCALITYLOCATION = "AAAAA";
    private static final String UPDATED_LOCALITYLOCATION = "BBBBB";
    private static final String DEFAULT_CITYNAME = "AAAAA";
    private static final String UPDATED_CITYNAME = "BBBBB";

    @Inject
    private LocalitiesRepository localitiesRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restLocalitiesMockMvc;

    private Localities localities;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        LocalitiesResource localitiesResource = new LocalitiesResource();
        ReflectionTestUtils.setField(localitiesResource, "localitiesRepository", localitiesRepository);
        this.restLocalitiesMockMvc = MockMvcBuilders.standaloneSetup(localitiesResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        localitiesRepository.deleteAll();
        localities = new Localities();
        localities.setLocalityname(DEFAULT_LOCALITYNAME);
       // localities.setLocalitylocation(DEFAULT_LOCALITYLOCATION);
        localities.setCityname(DEFAULT_CITYNAME);
    }

    @Test
    public void createLocalities() throws Exception {
        int databaseSizeBeforeCreate = localitiesRepository.findAll().size();

        // Create the Localities

        restLocalitiesMockMvc.perform(post("/api/localities")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(localities)))
                .andExpect(status().isCreated());

        // Validate the Localities in the database
        List<Localities> localities = localitiesRepository.findAll();
        assertThat(localities).hasSize(databaseSizeBeforeCreate + 1);
        Localities testLocalities = localities.get(localities.size() - 1);
        assertThat(testLocalities.getLocalityname()).isEqualTo(DEFAULT_LOCALITYNAME);
        assertThat(testLocalities.getLocalitylocation()).isEqualTo(DEFAULT_LOCALITYLOCATION);
        assertThat(testLocalities.getCityname()).isEqualTo(DEFAULT_CITYNAME);
    }

    @Test
    public void getAllLocalities() throws Exception {
        // Initialize the database
        localitiesRepository.save(localities);

        // Get all the localities
        restLocalitiesMockMvc.perform(get("/api/localities?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(localities.getId())))
                .andExpect(jsonPath("$.[*].localityname").value(hasItem(DEFAULT_LOCALITYNAME.toString())))
                .andExpect(jsonPath("$.[*].localitylocation").value(hasItem(DEFAULT_LOCALITYLOCATION.toString())))
                .andExpect(jsonPath("$.[*].cityname").value(hasItem(DEFAULT_CITYNAME.toString())));
    }

    @Test
    public void getLocalities() throws Exception {
        // Initialize the database
        localitiesRepository.save(localities);

        // Get the localities
        restLocalitiesMockMvc.perform(get("/api/localities/{id}", localities.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(localities.getId()))
            .andExpect(jsonPath("$.localityname").value(DEFAULT_LOCALITYNAME.toString()))
            .andExpect(jsonPath("$.localitylocation").value(DEFAULT_LOCALITYLOCATION.toString()))
            .andExpect(jsonPath("$.cityname").value(DEFAULT_CITYNAME.toString()));
    }

    @Test
    public void getNonExistingLocalities() throws Exception {
        // Get the localities
        restLocalitiesMockMvc.perform(get("/api/localities/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateLocalities() throws Exception {
        // Initialize the database
        localitiesRepository.save(localities);
        int databaseSizeBeforeUpdate = localitiesRepository.findAll().size();

        // Update the localities
        Localities updatedLocalities = new Localities();
        updatedLocalities.setId(localities.getId());
        updatedLocalities.setLocalityname(UPDATED_LOCALITYNAME);
        //updatedLocalities.setLocalitylocation(UPDATED_LOCALITYLOCATION);
        updatedLocalities.setCityname(UPDATED_CITYNAME);

        restLocalitiesMockMvc.perform(put("/api/localities")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedLocalities)))
                .andExpect(status().isOk());

        // Validate the Localities in the database
        List<Localities> localities = localitiesRepository.findAll();
        assertThat(localities).hasSize(databaseSizeBeforeUpdate);
        Localities testLocalities = localities.get(localities.size() - 1);
        assertThat(testLocalities.getLocalityname()).isEqualTo(UPDATED_LOCALITYNAME);
        assertThat(testLocalities.getLocalitylocation()).isEqualTo(UPDATED_LOCALITYLOCATION);
        assertThat(testLocalities.getCityname()).isEqualTo(UPDATED_CITYNAME);
    }

    @Test
    public void deleteLocalities() throws Exception {
        // Initialize the database
        localitiesRepository.save(localities);
        int databaseSizeBeforeDelete = localitiesRepository.findAll().size();

        // Get the localities
        restLocalitiesMockMvc.perform(delete("/api/localities/{id}", localities.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Localities> localities = localitiesRepository.findAll();
        assertThat(localities).hasSize(databaseSizeBeforeDelete - 1);
    }
}
