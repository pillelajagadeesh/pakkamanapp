package com.mistminds.web.rest;

import com.mistminds.Application;
import com.mistminds.domain.DeviceInfo;
import com.mistminds.repository.DeviceInfoRepository;

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
 * Test class for the DeviceInfoResource REST controller.
 *
 * @see DeviceInfoResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class DeviceInfoResourceIntTest {

    private static final String DEFAULT_DEVICE = "AAAAA";
    private static final String UPDATED_DEVICE = "BBBBB";
    private static final String DEFAULT_SDK = "AAAAA";
    private static final String UPDATED_SDK = "BBBBB";
    private static final String DEFAULT_MODEL = "AAAAA";
    private static final String UPDATED_MODEL = "BBBBB";
    private static final String DEFAULT_PRODUCT = "AAAAA";
    private static final String UPDATED_PRODUCT = "BBBBB";
    private static final String DEFAULT_CONSUMER_ID = "AAAAA";
    private static final String UPDATED_CONSUMER_ID = "BBBBB";

    @Inject
    private DeviceInfoRepository deviceInfoRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restDeviceInfoMockMvc;

    private DeviceInfo deviceInfo;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        DeviceInfoResource deviceInfoResource = new DeviceInfoResource();
        ReflectionTestUtils.setField(deviceInfoResource, "deviceInfoRepository", deviceInfoRepository);
        this.restDeviceInfoMockMvc = MockMvcBuilders.standaloneSetup(deviceInfoResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        deviceInfoRepository.deleteAll();
        deviceInfo = new DeviceInfo();
        deviceInfo.setDevice(DEFAULT_DEVICE);
        deviceInfo.setSdk(DEFAULT_SDK);
        deviceInfo.setModel(DEFAULT_MODEL);
        deviceInfo.setProduct(DEFAULT_PRODUCT);
        deviceInfo.setConsumerId(DEFAULT_CONSUMER_ID);
    }

    @Test
    public void createDeviceInfo() throws Exception {
        int databaseSizeBeforeCreate = deviceInfoRepository.findAll().size();

        // Create the DeviceInfo

        restDeviceInfoMockMvc.perform(post("/api/deviceInfos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(deviceInfo)))
                .andExpect(status().isCreated());

        // Validate the DeviceInfo in the database
        List<DeviceInfo> deviceInfos = deviceInfoRepository.findAll();
        assertThat(deviceInfos).hasSize(databaseSizeBeforeCreate + 1);
        DeviceInfo testDeviceInfo = deviceInfos.get(deviceInfos.size() - 1);
        assertThat(testDeviceInfo.getDevice()).isEqualTo(DEFAULT_DEVICE);
        assertThat(testDeviceInfo.getSdk()).isEqualTo(DEFAULT_SDK);
        assertThat(testDeviceInfo.getModel()).isEqualTo(DEFAULT_MODEL);
        assertThat(testDeviceInfo.getProduct()).isEqualTo(DEFAULT_PRODUCT);
        assertThat(testDeviceInfo.getConsumerId()).isEqualTo(DEFAULT_CONSUMER_ID);
    }

    @Test
    public void getAllDeviceInfos() throws Exception {
        // Initialize the database
        deviceInfoRepository.save(deviceInfo);

        // Get all the deviceInfos
        restDeviceInfoMockMvc.perform(get("/api/deviceInfos?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(deviceInfo.getId())))
                .andExpect(jsonPath("$.[*].device").value(hasItem(DEFAULT_DEVICE.toString())))
                .andExpect(jsonPath("$.[*].sdk").value(hasItem(DEFAULT_SDK.toString())))
                .andExpect(jsonPath("$.[*].model").value(hasItem(DEFAULT_MODEL.toString())))
                .andExpect(jsonPath("$.[*].product").value(hasItem(DEFAULT_PRODUCT.toString())))
                .andExpect(jsonPath("$.[*].consumerId").value(hasItem(DEFAULT_CONSUMER_ID.toString())));
    }

    @Test
    public void getDeviceInfo() throws Exception {
        // Initialize the database
        deviceInfoRepository.save(deviceInfo);

        // Get the deviceInfo
        restDeviceInfoMockMvc.perform(get("/api/deviceInfos/{id}", deviceInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(deviceInfo.getId()))
            .andExpect(jsonPath("$.device").value(DEFAULT_DEVICE.toString()))
            .andExpect(jsonPath("$.sdk").value(DEFAULT_SDK.toString()))
            .andExpect(jsonPath("$.model").value(DEFAULT_MODEL.toString()))
            .andExpect(jsonPath("$.product").value(DEFAULT_PRODUCT.toString()))
            .andExpect(jsonPath("$.consumerId").value(DEFAULT_CONSUMER_ID.toString()));
    }

    @Test
    public void getNonExistingDeviceInfo() throws Exception {
        // Get the deviceInfo
        restDeviceInfoMockMvc.perform(get("/api/deviceInfos/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateDeviceInfo() throws Exception {
        // Initialize the database
        deviceInfoRepository.save(deviceInfo);

		int databaseSizeBeforeUpdate = deviceInfoRepository.findAll().size();

        // Update the deviceInfo
        deviceInfo.setDevice(UPDATED_DEVICE);
        deviceInfo.setSdk(UPDATED_SDK);
        deviceInfo.setModel(UPDATED_MODEL);
        deviceInfo.setProduct(UPDATED_PRODUCT);
        deviceInfo.setConsumerId(UPDATED_CONSUMER_ID);

        restDeviceInfoMockMvc.perform(put("/api/deviceInfos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(deviceInfo)))
                .andExpect(status().isOk());

        // Validate the DeviceInfo in the database
        List<DeviceInfo> deviceInfos = deviceInfoRepository.findAll();
        assertThat(deviceInfos).hasSize(databaseSizeBeforeUpdate);
        DeviceInfo testDeviceInfo = deviceInfos.get(deviceInfos.size() - 1);
        assertThat(testDeviceInfo.getDevice()).isEqualTo(UPDATED_DEVICE);
        assertThat(testDeviceInfo.getSdk()).isEqualTo(UPDATED_SDK);
        assertThat(testDeviceInfo.getModel()).isEqualTo(UPDATED_MODEL);
        assertThat(testDeviceInfo.getProduct()).isEqualTo(UPDATED_PRODUCT);
        assertThat(testDeviceInfo.getConsumerId()).isEqualTo(UPDATED_CONSUMER_ID);
    }

    @Test
    public void deleteDeviceInfo() throws Exception {
        // Initialize the database
        deviceInfoRepository.save(deviceInfo);

		int databaseSizeBeforeDelete = deviceInfoRepository.findAll().size();

        // Get the deviceInfo
        restDeviceInfoMockMvc.perform(delete("/api/deviceInfos/{id}", deviceInfo.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<DeviceInfo> deviceInfos = deviceInfoRepository.findAll();
        assertThat(deviceInfos).hasSize(databaseSizeBeforeDelete - 1);
    }
}
