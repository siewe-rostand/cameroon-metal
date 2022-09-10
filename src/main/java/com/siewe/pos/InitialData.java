package com.siewe.pos;

import com.siewe.pos.model.Role;
import com.siewe.pos.repository.RoleRepository;
import com.siewe.pos.service.UserService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class InitialData {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserService userService;

    public InitializingBean load(){
        createRole();
        createAdmin();
        return null;
    }

    private void createRole(){
        List<String> roles = Arrays.asList("ADMIN","USER");

        for (String role : roles){
            if (roleRepository.findByName(role) == null){
                roleRepository.save(new Role(role));
            }
        }
    }

    private void createAdmin(){
        if (userService.findByLogin("admin") == null){
            userService.addNewUser("admin","user");
        }
    }
}
