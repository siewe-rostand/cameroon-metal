package com.siewe.pos.dto;

import com.siewe.pos.model.Role;

public class RoleDTO {

    private Long roleId;
    private String name;

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RoleDTO createDTO(Role role){
        RoleDTO roleDTO = new RoleDTO();

        if (role != null){
            roleDTO.setRoleId(role.getRoleId());
            roleDTO.setName(role.getName());
        }
        return roleDTO;
    }
}
