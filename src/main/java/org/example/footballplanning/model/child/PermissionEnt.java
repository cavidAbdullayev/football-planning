//package org.example.footballplanning.model.child;
//
//
//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
//import jakarta.persistence.ManyToMany;
//import lombok.Builder;
//import lombok.Data;
//import org.springframework.security.core.GrantedAuthority;
//
//import java.util.List;
//
//@Data
//@Builder
//@Entity
//public class PermissionEnt implements GrantedAuthority {
//    @Id
//    private String id;
//    private String name;
//    @ManyToMany(mappedBy = "permissions")
//    private List<Role>roles;
//
//
//
//    public Permission() {}
//    public Permission(String id, String name, List<Role> roles) {
//        this.id = id;
//        this.name = name;
//        this.roles = roles;
//    }
//    public Permission(String name){
//        this.name=name;
//    }
//
//    @Override
//    public String getAuthority() {
//        return name;
//    }
//}
//
