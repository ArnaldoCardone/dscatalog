package com.cardone.dscatalog.entities;

import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_role")
public class Role implements GrantedAuthority {
    
    @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String authority;

    public Role(){
    }

    public Role( Long id, String authority) {
        this.authority = authority;
        this.id = id;
    }

    public Role(Role entity) {
        this.id = entity.getId();
        this.authority = entity.getAuthority();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Role other = (Role) obj;
        return Objects.equals(this.id, other.id);
    }



}   
