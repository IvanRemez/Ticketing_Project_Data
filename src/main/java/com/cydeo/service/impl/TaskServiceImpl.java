package com.cydeo.service.impl;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.TaskDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.entity.Project;
import com.cydeo.entity.Task;
import com.cydeo.enums.Status;
import com.cydeo.mapper.ProjectMapper;
import com.cydeo.mapper.TaskMapper;
import com.cydeo.mapper.UserMapper;
import com.cydeo.repository.TaskRepository;
import com.cydeo.service.TaskService;
import com.cydeo.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final ProjectMapper projectMapper;
    private final UserService userService;
    private final UserMapper userMapper;

    public TaskServiceImpl(TaskRepository taskRepository, TaskMapper taskMapper, ProjectMapper projectMapper, UserService userService, UserMapper userMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.projectMapper = projectMapper;
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @Override
    public List<TaskDTO> listAllTasks() {

//        List<Task> taskList = taskRepository.findAll(Sort.by("taskId"));

        return taskRepository.findAll()
                .stream().map(taskMapper::convertToDto).collect(Collectors.toList());
    }

    @Override
    public TaskDTO findById(Long id) {

        Optional<Task> task = taskRepository.findById(id);

        if (task.isPresent()) {

            return taskMapper.convertToDto(task.get());
        }
        return null;
    }

    @Override
    public void save(TaskDTO dto) {

        if (dto.getTaskStatus() == null) {
            dto.setTaskStatus(Status.OPEN);
        }
        if (dto.getAssignedDate() == null) {
            dto.setAssignedDate(LocalDate.now());
        }
        taskRepository.save(taskMapper.convertToEntity(dto));
    }

    @Override
    public void update(TaskDTO dto) {

        Optional<Task> task = taskRepository.findById(dto.getId());

        Task convertedTask = taskMapper.convertToEntity(dto);

        if (task.isPresent()) {
            convertedTask.setTaskStatus(
                    dto.getTaskStatus() == null ? task.get().getTaskStatus()
                            : dto.getTaskStatus());
            // Ternary op^^: IF task Status is null (as in Manager update -
            // no Status field in form) -> get Status from DB
            // ELSE get task Status from DTO (as in Employee update OR
            // completeByProject() method update)
            convertedTask.setAssignedDate(task.get().getAssignedDate());
        // ^^ these 2 not present in UI(dto) form so need to set prior to saving update
            taskRepository.save(convertedTask);
        }
    }

    @Override
    public void delete(Long id) {

        Optional<Task> task = taskRepository.findById(id);

        if (task.isPresent()) {
            task.get().setIsDeleted(true);
            taskRepository.save(task.get());
        }
    }

    @Override
    public int totalNonCompletedTasks(String projectCode) {
        return taskRepository.totalNonCompletedTasks(projectCode);
    }

    @Override
    public int totalCompletedTasks(String projectCode) {
        return taskRepository.totalCompletedTasks(projectCode);
    }

    @Override
    public void deleteByProject(ProjectDTO projectDTO) {

        Project project = projectMapper.convertToEntity(projectDTO);
        List<Task> tasks = taskRepository.findAllByProject(project);

        tasks.forEach(task -> delete(task.getId()));
    // ^^ use forEach to soft-delete tasks 1 by 1 using their ID
    // sets isDeleted field to true but keeps task in DB for future reference
    }

    @Override
    public void completeByProject(ProjectDTO projectDTO) {

        Project project = projectMapper.convertToEntity(projectDTO);
        List<Task> tasks = taskRepository.findAllByProject(project);

        tasks.stream().map(taskMapper::convertToDto)
                .forEach(taskDTO -> {
                    taskDTO.setTaskStatus(Status.COMPLETE);
                // need to set task Status to COMPLETE prior to updating
                    update(taskDTO);
                // no need to convert back to Entity for DB since update()
                // method already does this conversion
                });
    }

    @Override
    public List<TaskDTO> listAllTasksByStatus(Status status) {

        UserDTO loggedInUser = userService.findByUserName("john@employee.com");
                                        // ^^ hardcoded for now (need Security)
        List<Task> tasks = taskRepository.
                findAllByTaskStatusAndAssignedEmployee(status,
                        userMapper.convertToEntity(loggedInUser));
                        // ^^ need to convert loggedInUser to Entity since it's DTO,
                        // and we are using Repository layer for tasks

        return tasks.stream()
                .map(taskMapper::convertToDto).collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> listAllTasksByStatusIsNot(Status status) {

        UserDTO loggedInUser = userService.findByUserName("john@employee.com");
        // ^^ hardcoded for now (need Security)
        List<Task> tasks = taskRepository.
                findAllByTaskStatusIsNotAndAssignedEmployee(status,
                        userMapper.convertToEntity(loggedInUser));
        // ^^ need to convert loggedInUser to Entity since it's DTO,
        // and we are using Repository layer for tasks

        return tasks.stream()
                .map(taskMapper::convertToDto).collect(Collectors.toList());
    }
}
