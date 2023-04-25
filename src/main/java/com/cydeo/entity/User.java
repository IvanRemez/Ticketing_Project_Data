package com.cydeo.entity;

import com.cydeo.enums.Gender;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
@Where(clause = "is_deleted=false")
// ^^ adds this WHERE clause to each User entity method in your App
// (including JPA standard methods)
// ** when soft deleting, your data remains in DB but this clause ignores the deleted
// Entities when marked true (prevents queries from bringing these objects to the UI)
public class User extends BaseEntity {

    private String firstName;
    private String lastName;
    private String userName;
    private String passWord;
    private boolean enabled;
    private String phone;

    @ManyToOne  // many Users can have the same Role
    private Role role;

    @Enumerated(EnumType.STRING)
    private Gender gender;

}
