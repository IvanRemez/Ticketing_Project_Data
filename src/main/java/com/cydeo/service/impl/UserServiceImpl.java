package com.cydeo.service.impl;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.TaskDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.entity.User;
import com.cydeo.mapper.UserMapper;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.ProjectService;
import com.cydeo.service.TaskService;
import com.cydeo.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ProjectService projectService;
    private final TaskService taskService;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper,
                           @Lazy ProjectService projectService, @Lazy TaskService taskService) {
                        // ^^ fixes circular dependency error due to injections in both classes
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.projectService = projectService;
        this.taskService = taskService;
    }

    @Override
    public List<UserDTO> listAllUsers() {

        List<User> userList = userRepository
                .findAllByIsDeletedOrderByFirstNameDesc(false);

        return userList.stream()
                .map(userMapper::convertToDto).collect(Collectors.toList());
    }

    @Override
    public UserDTO findByUserName(String username) {

        User user = userRepository.findByUserNameAndIsDeleted(
                username, false);

        return userMapper.convertToDto(user);
    }

    @Override
    public void save(UserDTO user) {
        userRepository.save(userMapper.convertToEntity(user));
    }

//    @Override
//    public void deleteByUserName(String username) {     // HARD delete - NOT used
//
//        userRepository.deleteByUserName(username);
//    }

    @Override
    public UserDTO update(UserDTO user) {
// find current user
        User user1 = userRepository
                .findByUserNameAndIsDeleted(user.getUserName(), false);
        // ^^ has ID
// Map updated UserDTO to Entity object
        User convertedUser = userMapper.convertToEntity(user);
        // NO ID
// set ID to converted Object
        convertedUser.setId(user1.getId());
// save the updated User in DB
        userRepository.save(convertedUser);

        return findByUserName(user1.getUserName());
    }

    @Override
    public void delete(String username) {
    // ^^ used delete from UI while still keeping User in DB:
// go to DB and find User by username
        User user = userRepository
                .findByUserNameAndIsDeleted(username, false);
// check if User can be deleted:
        if (checkIfUserCanBeDeleted(user)) {
        // change isDeleted field to true
            user.setIsDeleted(true);
            user.setUserName(user.getUserName() + "-" + user.getId());
        // ^^ keeps this altered username in DB for future reference while
            // allowing for creation of the same username in the future

        // save the Object in DB
            userRepository.save(user);
        }
        // no else, just won't be able to delete User
        // can throw an exception in the future
    }

    @Override
    public List<UserDTO> listAllByRole(String role) {

        List<User> users = userRepository
                .findByRoleDescriptionIgnoreCaseAndIsDeleted(role, false);

        return users.stream()
                .map(userMapper::convertToDto).collect(Collectors.toList());
    }

    private boolean checkIfUserCanBeDeleted(User user) {

        switch (user.getRole().getDescription()) {
            case "Manager":
                List<ProjectDTO> projectDTOList = projectService
                        .listAllNonCompletedByAssignedManager(userMapper.convertToDto(user));
                return projectDTOList.size() == 0;
            // ^^ if Manager has no uncompleted projects (true) we are able to delete Manager
            case "Employee":
                List<TaskDTO> taskDTOList = taskService
                        .listAllNonCompletedByAssignedEmployee(userMapper.convertToDto(user));
                return taskDTOList.size() == 0;
            // ^^ if Employee has no uncompleted tasks (true) we are able to delete Employee
            default:
                return true;
        }
    }
}
