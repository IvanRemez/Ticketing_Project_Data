package com.cydeo.service.impl;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.entity.Project;
import com.cydeo.entity.User;
import com.cydeo.enums.Status;
import com.cydeo.mapper.ProjectMapper;
import com.cydeo.mapper.UserMapper;
import com.cydeo.repository.ProjectRepository;
import com.cydeo.service.ProjectService;
import com.cydeo.service.TaskService;
import com.cydeo.service.UserService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final UserService userService;
    private final UserMapper userMapper;
    private final TaskService taskService;

    public ProjectServiceImpl(ProjectRepository projectRepository, ProjectMapper projectMapper, UserService userService, UserMapper userMapper, TaskService taskService) {
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
        this.userService = userService;
        this.userMapper = userMapper;
        this.taskService = taskService;
    }

    @Override
    public ProjectDTO getByProjectCode(String code) {

        Project project = projectRepository.findByProjectCode(code);
        return projectMapper.convertToDto(project);
    }

    @Override
    public List<ProjectDTO> listAllProjects() {

        List<Project> projects = projectRepository
                .findAll(Sort.by("projectCode"));

        return projects.stream()
                .map(projectMapper::convertToDto).collect(Collectors.toList());
    }

    @Override
    public void save(ProjectDTO dto) {

        if (dto.getProjectStatus() == null) {
            dto.setProjectStatus(Status.OPEN);
        }
        Project project = projectMapper.convertToEntity(dto);

        projectRepository.save(project);
    }

    @Override
    public void update(ProjectDTO dto) {

        Project project = projectRepository.findByProjectCode(dto.getProjectCode());
    // ^^ retrieved to get ID
        Project convertedProject = projectMapper.convertToEntity(dto);

        convertedProject.setId(project.getId());
        convertedProject.setProjectStatus(project.getProjectStatus());
    // ^^ both NOT in Form so need to set prior to saving

        projectRepository.save(convertedProject);
    }

    @Override
    public void delete(String code) {

        Project project = projectRepository.findByProjectCode(code);
        project.setIsDeleted(true);

        project.setProjectCode(project.getProjectCode() + "-" + project.getId());
    // ^^ keeps this altered Project code in DB for future reference while allowing for
    // creation of the same Project code

        projectRepository.save(project);

        taskService.deleteByProject(projectMapper.convertToDto(project));
    // ^^ deletes tasks associated with Project (DTO b/c we want it as a service which can be
    // implemented in other areas of app)
    }

    @Override
    public void complete(String code) {

        Project project = projectRepository.findByProjectCode(code);
        project.setProjectStatus(Status.COMPLETE);
        projectRepository.save(project);

        taskService.completeByProject(projectMapper.convertToDto(project));
    }

    @Override
    public List<ProjectDTO> listAllProjectDetails() {
// End Goal: list all projects assigned to the manager currently logged in to system

// Capture which user is logged in to the system (hardcoded for now)
        UserDTO currentUserDTO = userService.findByUserName("harold@manager.com");
                                // ^^ will be replaced by Security login info

// Check projects assigned to this manager in DB
    // Need to convert to Entity
        User user = userMapper.convertToEntity(currentUserDTO);

// find all projects assigned to this manager (user)
        List<Project> projectList = projectRepository.findAllByAssignedManager(user);

// run through stream to convert to DTO list (for UI)
        return projectList.stream().map(project -> {

            ProjectDTO dto = projectMapper.convertToDto(project);
// set DTO fields which are not included in the Entity
            dto.setUnfinishedTaskCounts(taskService.totalNonCompletedTasks(project.getProjectCode()));
            dto.setCompleteTaskCounts(taskService.totalCompletedTasks(project.getProjectCode()));

            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ProjectDTO> listAllNonCompletedByAssignedManager(UserDTO assignedManager) {

        List<Project> projects = projectRepository.findAllByProjectStatusIsNotAndAssignedManager(
                Status.COMPLETE, userMapper.convertToEntity(assignedManager));

        return projects.stream().map(projectMapper::convertToDto).collect(Collectors.toList());
    }
}
