package com.mistminds.service;

import java.io.IOException;
import java.util.Map;

import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cloudinary.Cloudinary;
import com.mistminds.config.Constants;

@Service
public class ContentMetadataService {
	
private final Logger log = LoggerFactory.getLogger(ContentMetadataService.class);
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> cloudanaryUploadImage(String image){
		log.debug("Inside cloudanaryUploadImage method of ContentMetadataService for upload image");
		byte[] b = Base64.decodeBase64(image);
		Cloudinary cloudinary = new Cloudinary(Constants.CLOUDINARY);
		try {
			return cloudinary.uploader().upload(b, null);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> cloudanaryUploadCategoryImage(byte[] b){
		log.debug("Inside cloudanaryUploadCategoryImage method of ContentMetadataService for upload image");
		Cloudinary cloudinary = new Cloudinary(Constants.CLOUDINARY);
		try {
			return cloudinary.uploader().upload(b, null);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}