package com.mistminds.service;

/*
 * Copyright 2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.google.common.base.Preconditions;
import com.mistminds.config.PakkaApplicationSettingsConfiguration;
import com.mistminds.domain.PakkaApplicationSettings;
import com.mistminds.repository.PakkaApplicationSettingsRepository;
import com.mistminds.service.SampleMessageGenerator.Platform;

@Service
public class SNSMobilePush {
	
	@Inject
	private PakkaApplicationSettingsRepository pakkaApplicationSettingsRepository;
	
	private final static Logger log = LoggerFactory.getLogger(SNSMobilePush.class);

	private static AmazonSNSClientWrapper snsClientWrapper;

    
	public static final Map<Platform, Map<String, MessageAttributeValue>> attributesMap = new HashMap<Platform, Map<String, MessageAttributeValue>>();
	

	public static AWSCredentials credentials = null;
	public static AmazonSNSClient sns = null;
	
	static {
		attributesMap.put(Platform.GCM, null);
		InputStream credentialsAsStream=Thread.currentThread().getContextClassLoader().getResourceAsStream("AwsCredentials.properties");
		Preconditions.checkNotNull(credentialsAsStream, "File 'AwsCredentials.properties' NOT found in the classpath");
		try {
			credentials = new PropertiesCredentials(credentialsAsStream);
		} catch (IOException e) {
			log.error("Error while getting credentials for push notification " + e.getMessage(), e);
		} 
		sns = new AmazonSNSClient(credentials); 
		sns.setEndpoint("https://sns.us-west-2.amazonaws.com");
		snsClientWrapper = new AmazonSNSClientWrapper(sns);

	}
	
	public void pushNotification(Map<String, String> pushData) throws IOException {
		
	
		/*
		 * TODO: Be sure to fill in your AWS access credentials in the
		 * AwsCredentials.properties file before you try to run this sample.
		 * http://aws.amazon.com/security-credentials
		 */
		try {
			

		log.info("=========================Getting Started with Amazon SNS=================================");
		
		demoAndroidAppNotification(pushData);
		
		log.info("Pushed the notification successfully");
		
		} catch(Exception e){
			log.error("Exception occurs while push notification"+ e.getMessage(),e);
		}
	}

	public void demoAndroidAppNotification(Map<String, String> pushData) {
	    PakkaApplicationSettings apiKey = pakkaApplicationSettingsRepository.findByName(PakkaApplicationSettingsConfiguration.KEY_NAME_SERVERAPI_KEY);
	    PakkaApplicationSettings appName = pakkaApplicationSettingsRepository.findByName(PakkaApplicationSettingsConfiguration.KEY_NAME_APPLICATION_NAME);
	    String serverApiKey=apiKey.getValue().toString();
	    String applicationName=appName.getValue().toString();
		// TODO: Please fill in following values for your application. You can
		// also change the notification payload as per your preferences using
		// the method
		// com.amazonaws.sns.samples.tools.SampleMessageGenerator.getSampleAndroidMessage()
				String registrationId = pushData.get("installationId");
				log.info("notification push to the id: "+registrationId);
		snsClientWrapper.demoNotification(Platform.GCM, "", serverApiKey,
				registrationId, applicationName, attributesMap, pushData);
	}
}
