
package com.integration.zoy.utils;
 
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
 
public class RoleModel {
	@SerializedName("id")
	int id;
	
	@SerializedName("roleName")
	String roleName;
	
	@SerializedName("approveStatus")
	String approveStatus;
 
	@SerializedName("approvedPrivilege")
    List<String> approvedPrivileges= new ArrayList<>();;

	@SerializedName("unapprovedPrivilege")
	List<String> unapprovedPrivileges= new ArrayList<>();;
	
	
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
 
	public String getApproveStatus() {
		return approveStatus;
	}

	public void setApproveStatus(String approveStatus) {
		this.approveStatus = approveStatus;
	}
	
	

	public List<String> getApprovedPrivileges() {
		return approvedPrivileges;
	}

	public void setApprovedPrivileges(List<String> approvedPrivileges) {
		this.approvedPrivileges = approvedPrivileges;
	}

	public List<String> getUnapprovedPrivileges() {
		return unapprovedPrivileges;
	}

	public void setUnapprovedPrivileges(List<String> unapprovedPrivileges) {
		this.unapprovedPrivileges = unapprovedPrivileges;
	}
	
	 public void addApprovedPrivilege(String privilege) {
	        approvedPrivileges.add(privilege);
	    }

	 
	    public void addUnapprovedPrivilege(String privilege) {
	        unapprovedPrivileges.add(privilege);
	    }

	    
	public RoleModel(int id, String roleName, String approveStatus) {
		super();
		this.id = id;
		this.roleName = roleName;
		this.approveStatus=approveStatus;
	}
}
	
	