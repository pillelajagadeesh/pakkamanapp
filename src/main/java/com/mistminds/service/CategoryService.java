package com.mistminds.service;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.mistminds.domain.Category;
import com.mistminds.domain.Consumer;
import com.mistminds.repository.CategoryRepository;
import com.mistminds.repository.ConsumerRepository;

@Service
public class CategoryService {

	
	@Inject
	private CategoryRepository categoryRepository;
	
	@Inject
	private ConsumerRepository consumerRepository;
	
	public boolean addCategory(Category category){
		
		boolean flag = true;
		List<Category> categories = categoryRepository.findAll();
		
		for(Category dbCategory : categories){
			if(dbCategory.getName() != null && dbCategory.getName().equalsIgnoreCase(category.getName())){
				flag = false;
				break;
			}
		}
		return flag;
	}
	
	public List<Category> getConsumerCategory(String consumerId){
		System.out.println("consumer Id"+ consumerId);
		List<Category> categories = categoryRepository.findAll();
		List<Category> consumerCategories = new ArrayList<Category>();
		List<Category> mainList = new ArrayList<Category>();
		String categoryName;
		boolean flag = true;
		Consumer consumer = consumerRepository.findOneById(consumerId);
		List<Category> listConsumerCategory = (List<Category>) consumer.getUnsubscribeCategory();
		
		for(Category category : categories){
			if(listConsumerCategory!=null){
			for(Category consumerCategory : listConsumerCategory){
				if(category.getName() != null && category.getName().equals(consumerCategory.getName())){
					categoryName = category.getName() + "-----" + "true";
					category.setName(categoryName);
					consumerCategories.add(category);
					flag = false;
					break;
				}
			}
			}
			if(flag){
				categoryName = category.getName() + "-----" + "false";
				category.setName(categoryName);
				mainList.add(category);
			}
			flag = true;
		}
		mainList.addAll(consumerCategories);
		return mainList;
	}
}
	