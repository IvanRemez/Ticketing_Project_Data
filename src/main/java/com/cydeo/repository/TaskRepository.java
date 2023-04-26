package com.cydeo.repository;

import com.cydeo.entity.Project;
import com.cydeo.entity.Task;
import com.cydeo.entity.User;
import com.cydeo.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.validation.Valid;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT COUNT(t) FROM Task t WHERE t.project.projectCode = ?1 " +
            "AND t.taskStatus <> 'COMPLETE'")
    Integer totalNonCompletedTasks(String projectCode);

    @Query(value = "SELECT COUNT(*) FROM tasks t JOIN projects p " +
            "ON t.project_id = p.id WHERE p.project_code = ?1 " +
            "AND t.task_status = 'COMPLETE'", nativeQuery = true)
    Integer totalCompletedTasks(String projectCode);

    List<Task> findAllByProject(Project project);
    // ^^ can pass Entity in parameter with Derived Query ONLY
    // for JPQL or Native Queries, it needs to be an ID

    List<Task> findAllByTaskStatusAndAssignedEmployee(Status status, User user);
    List<Task> findAllByTaskStatusIsNotAndAssignedEmployee(Status status, User user);

}
