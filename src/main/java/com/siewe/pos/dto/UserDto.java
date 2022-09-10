package com.siewe.pos.dto;

import com.siewe.pos.model.Role;
import com.siewe.pos.model.User;
import lombok.Data;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Data
public class UserDto {

    private Long userId;
    private String firstName;
    private String lastName;
    private String login;
    private String createdDate;
    private String updatedDate;
    private int phone;
    private String password;
    private String badge;
    private Set<String> role;

    public UserDto createDTO(User user){
        UserDto userDto = new UserDto();

        if (user != null){
            userDto.setUserId(user.getUserId());
            userDto.setFirstName(user.getFirstName());
            userDto.setLastName(user.getLastName());
            userDto.setLogin(user.getLogin());
            userDto.setPassword(user.getPassword());
            userDto.setPhone(user.getPhone());
            userDto.setCreatedDate(user.getCreatedDate());
            userDto.setUpdatedDate(user.getUpdatedDate());

            if (user.getRole() != null){
                HashSet<String> roles = new HashSet<>();
                if (user.getRole() != null) {
                    for (Role role : user.getRole())
                        roles.add(role.getName());
                }
                userDto.setRole(roles);
                if (userDto.getRole().equals("ADMIN"))
                    userDto.setBadge("success");
                if (userDto.getRole().equals("User"))
                    userDto.setBadge("Employee");
            }
        }

        return userDto;
    }
}
