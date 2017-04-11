package com.mistminds.web.rest.dto;

public class UserTypeDTO {
	
	
	
	private int id;
	
	
	private String typeName;
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	private String description;
	
	@Override
	public String toString() {
		
		return "[id:"+this.id+" TypeName:"+this.typeName+" description:"+this.description+"]";
		
	}
	
	@Override
	public boolean equals(Object obj) 
	{
	     if(! (obj instanceof UserTypeDTO )){
	    	 return false;
	     }
	     else{
	    	UserTypeDTO userTypeDto = (UserTypeDTO) obj;
	    	return userTypeDto.id == this.id;
	    	 
	     }
	}
	
	
	
	
	

}
