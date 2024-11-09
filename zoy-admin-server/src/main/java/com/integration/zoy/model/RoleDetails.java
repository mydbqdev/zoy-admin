package com.integration.zoy.model;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

   
public class RoleDetails {

   @JsonProperty("role_name")
   String roleName;

   @JsonProperty("desc")
   String desc;

   @JsonProperty("role_screen")
   List<RoleScreen> roleScreen;


    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
    public String getRoleName() {
        return roleName;
    }
    
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getDesc() {
        return desc;
    }
    
    public void setRoleScreen(List<RoleScreen> roleScreen) {
        this.roleScreen = roleScreen;
    }
    public List<RoleScreen> getRoleScreen() {
        return roleScreen;
    }
    
}