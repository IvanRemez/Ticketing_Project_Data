package com.cydeo.entity;

import com.cydeo.enums.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "projects")
@Where(clause = "is_deleted=false")
// ^^ adds this WHERE clause to each User entity method in your App
// (including JPA standard methods)
// ** when soft deleting, your data remains in DB but this clause ignores the deleted
// Entities when marked true (prevents queries from bringing these objects to the UI)

public class Project extends BaseEntity {

    @Column(unique = true)  // prevents creation of new Projects with the same code
                    // needed validation since we are using projectCode as our id
    private String projectCode;
    private String projectName;

    @Column(columnDefinition = "DATE")
    private LocalDate startDate;
    @Column(columnDefinition = "DATE")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private Status projectStatus;
    private String projectDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private User assignedManager;
}
