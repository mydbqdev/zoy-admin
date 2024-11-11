
package com.integration.zoy.utils;
 
import com.google.gson.annotations.SerializedName;
 
public class RoleModel {
	@SerializedName("id")
	int id;
	
	@SerializedName("roleName")
	String roleName;
 
	public int getId() {
		return id;
	}
 
	public void setId(int id) {
		this.id = id;
	}
 
	public String getRoleName() {
		return roleName;
	}
 
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
 
	public RoleModel(int id, String roleName) {
		super();
		this.id = id;
		this.roleName = roleName;
	}
}
	
	