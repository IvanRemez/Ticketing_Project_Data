package com.cydeo.service;

import com.cydeo.dto.TaskDTO;

import java.util.List;

public interface TaskService {

    List<TaskDTO> listAllTasks();
    TaskDTO findById(Long id);
    void save(TaskDTO dto);
    void update(TaskDTO dto);
    void delete(Long id);   // no unique fields for Task -> use DTO id
    int totalNonCompletedTasks(String projectCode);
    int totalCompletedTasks(String projectCode);

}
