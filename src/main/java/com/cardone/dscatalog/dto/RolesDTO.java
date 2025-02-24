package com.cardone.dscatalog.dto;

import com.cardone.dscatalog.entities.Roles;

public class RolesDTO {
    private Long id;
    private String authority;

    public RolesDTO() {
    }

    public RolesDTO(Long id, String authority) {
        this.id = id;
        this.authority = authority;
    }

    public RolesDTO(Roles entity) {
        id = entity.getId();
        authority = entity.getAuthority();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
    
}
