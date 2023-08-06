package com.rai.online.aidemo.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Setter
@Getter
@NoArgsConstructor
@Entity
@ToString
@DynamicInsert
@DynamicUpdate
@Table(name = "users", schema = "rai", uniqueConstraints = {@UniqueConstraint(columnNames = "USER_ID")})
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long userId;

    @NotNull(message = "First Name should not be null")
    @Column(name = "FIRST_NM", nullable = false)
    private String firstName;

    @NotNull(message = "Last Name should not be null")
    @Column(name = "LAST_NM", nullable = false)
    private String lastName;

    @Column(name = "ACC_NR")
    private Integer accountNo = 1;

    @NotNull(message = "e-mail should not be null")
    @Column(name = "EMAIL", nullable = false)
    private String email;

    @NotNull(message = "User password should not be null")
    @Column(name = "USR_PWD", nullable = false)
    private String password;

    @NotNull
    @Column(name = "LAST_MODIFIED_BY", nullable = false)
    private String lastModifiedBy;

    @NotNull
    @Column(name = "LAST_MODIFIED_DATE", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp lastModifiedTime;
}
