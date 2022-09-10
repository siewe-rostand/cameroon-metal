package com.siewe.pos.service;

import com.siewe.pos.dto.RoleDTO;
import com.siewe.pos.dto.UserDto;
import com.siewe.pos.model.Role;
import com.siewe.pos.model.User;
import com.siewe.pos.repository.RoleRepository;
import com.siewe.pos.repository.UserRepository;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public UserDto save(UserDto userDto, String role){
        log.debug("Request to save a new user {}",userDto);

        User user = new User();

        user.setUserId(userDto.getUserId());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setLogin(userDto.getLogin());
        user.setPhone(userDto.getPhone());
        user.setPassword(userDto.getPassword());

        //set created date;
        String pattern = "yyyy-MM-dd";
        LocalDate date = new LocalDate(DateTimeZone.forOffsetHours(1));
        user.setCreatedDate(date.toString(pattern));

        //set new user role
        user.setRole(new HashSet<>());
        Role role1 = roleRepository.findByName(role);
        if (role != null){
            user.getRole().add(role1);
        }

        User result = userRepository.save(user);

        return new UserDto().createDTO(result);

    }
    public UserDto save(UserDto userDto){
        log.debug("Request to save a new user {}",userDto);

        User user = new User();
        if (userRepository.findByLogin(userDto.getLogin()) != null){
            throw new RuntimeException("Username already exist!");
        }
        user.setUserId(userDto.getUserId());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setLogin(userDto.getLogin());
        user.setPhone(userDto.getPhone());
        user.setPassword(userDto.getPassword());

        //set created date;
        String pattern = "yyyy-MM-dd";
        LocalDate date = new LocalDate(DateTimeZone.forOffsetHours(1));
        user.setCreatedDate(date.toString(pattern));

        HashSet<Role> roles = new HashSet<>();
        if (user.getRole() != null){
            for (String role : userDto.getRole())
                roles.add(roleRepository.findByName(role));
        }
        user.setRole(roles);
        User result = userRepository.save(user);

        return new UserDto().createDTO(result);

    }

    public UserDto update(UserDto userDto){
        log.debug("Request to update a user{}",userDto);

        User user = userRepository.findByUserId(userDto.getUserId());

        /*if (userRepository.findByLogin(userDto.getLogin()) != null){
            throw new RuntimeException("Username already exist!");
        }*/
        user.setUserId(userDto.getUserId());
        user.setLogin(userDto.getLogin());
        user.setLastName(userDto.getLastName());
        user.setFirstName(userDto.getFirstName());
        user.setPassword(userDto.getPassword());
        user.setPhone(userDto.getPhone());

        //set created date;
        String pattern = "yyyy-MM-dd";
        LocalDate date = new LocalDate(DateTimeZone.forOffsetHours(1));
        user.setUpdatedDate(date.toString(pattern));

        HashSet<Role> roles = new HashSet<>();
        if (user.getRole() != null){
            for (String role : userDto.getRole())
                roles.add(roleRepository.findByName(role));
        }
        user.setRole(roles);

        User result = userRepository.save(user);

        return new UserDto().createDTO(result);
    }

    /**
     * get all users
     * @return list of users
     */
    @Transactional(readOnly = true)
    public ResponseEntity<List<UserDto>> findAll() {
        log.debug("Request to get all Users");
        List<User> users = userRepository.findAll();
        List<UserDto> userDtos = new ArrayList<>();
        if (users.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
            // You many decide to return HttpStatus.NOT_FOUND
        }
        for (User user : users)
            userDtos.add(new UserDto().createDTO(user));
        return new ResponseEntity<List<UserDto>>(userDtos, HttpStatus.OK);
    }

    /**
     * get user by role
     * @param roleName of the entity
     * @return users by role
     */
    @Transactional(readOnly = true)
    public List<UserDto> findAllByRole(String roleName){
        log.debug("Request to get all Users by Role");
        List<User> users = userRepository.findAllByRole(roleName);
        List<UserDto> userDtos = new ArrayList<>();

        for (User user : users)
            userDtos.add(new UserDto().createDTO(user));

        //return new ResponseEntity<List<UserDto>>(userDtos, HttpStatus.OK);
        return userDtos;
    }

    public Page<UserDto> findAll(Integer page, Integer size, String sortBy, String direction, String name) {
        log.debug("Request to get all Users");

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(direction), sortBy);

        Page<User> users = userRepository.findAll("%"+name+"%", pageable);

        Page<UserDto> userDtos = users.map(user -> new UserDto().createDTO(user));
        return userDtos;
    }

    public Page<UserDto> findAll(Integer page, Integer size, String sortBy, String direction, String login, String[] roles) {
        log.debug("Request to get all Users");

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(direction), sortBy);

        Page<User> users = userRepository.findAll("%"+login+"%", roles, pageable);

        Page<UserDto> userDtos = users.map(user -> new UserDto().createDTO(user));
        return userDtos;
    }

    public List<UserDto> findByKeyword(String keyword) {
        List<User> users = userRepository.findByKeyword("%"+keyword+"%");
        List<UserDto> userDtos = new ArrayList<>();

        for (User user : users)
            userDtos.add(new UserDto().createDTO(user));

        return userDtos;
    }

    /**
     *  Get one user by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public ResponseEntity<UserDto> findOne(Long id) {
        log.debug("Request to get User : {}", id);
        User user = userRepository.findByUserId(id);

        UserDto userDto = new UserDto().createDTO(user);
        return Optional.ofNullable(userDto)
                .map(result -> new ResponseEntity<>(
                        result,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     *  Delete the  user by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete User : {}", id);
        User user = userRepository.findByUserId(id);
        if(Optional.ofNullable(user).isPresent()){
                userRepository.deleteById(id);
            }
    }

    public User findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    public User addNewUser(String login, String role) {
        User user = new User();
        user.setLogin(login);
        user.setLastName(login);
        user.setPhone( ThreadLocalRandom.current().nextInt(11111111, 99999999));
        user.setPassword("1234");
        //user.setEmail(user.getLogin()+ "@pharma.com");

        /*Role role1 = roleRepository.findByName(role);
        if(role1 != null){user.setRole(role1); }*/

        user.setRole(new HashSet<>());
        Role role1 = roleRepository.findByName(role);
        if(role != null){
            user.getRole().add(role1);
        }

        //set created date;
        String pattern = "yyyy-MM-dd";
        LocalDate date = new LocalDate();
        user.setCreatedDate(date.toString(pattern));

        User result = userRepository.save(user);
        return result;
    }

    public User addAdmin() {
        User user = new User();
        user.setLastName("Admin");
        user.setLogin("admin");
        user.setPhone(ThreadLocalRandom.current().nextInt(11111111, 99999999));
        user.setPassword("1234");
        //user.setEmail(user.getLogin()+ "@pharma.com");

        user.setRole(new HashSet<>());
        user.getRole().addAll(roleRepository.findAll());

        //set created date;
        String pattern = "yyyy-MM-dd";
        LocalDate date = new LocalDate();
        user.setCreatedDate(date.toString(pattern));

        return userRepository.save(user);
    }

}


