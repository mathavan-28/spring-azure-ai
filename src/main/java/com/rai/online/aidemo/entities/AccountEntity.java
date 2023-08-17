package com.rai.online.aidemo.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "accounts", schema = "rai", uniqueConstraints = {@UniqueConstraint(columnNames = "ACNT_ID")})
public class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ACNT_ID")
    private Long accountId;

    @ManyToOne(targetEntity = UserEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID")
    @ToString.Exclude
    private UserEntity userEntity;

    @NotNull(message = "Account number should not be null")
    @Column(name = "ACC_NR")
    private Integer accountNo = 1;

    @NotNull(message = "Branch should not be null")
    @Column(name = "BRNCH")
    private String branchName;

    @NotNull(message = "monthly limit should not be null")
    @Column(name = "LIMIT_MONTHLY", nullable = false)
    private Double monthlyLimit;

    @Column(name = "LIMIT_USED")
    private Double usedLimit;

    @NotNull(message = "Current Balance should not be null")
    @Column(name = "CRNT_BAL", nullable = false)
    private Double currentBal;

    @NotNull
    @Column(name = "LAST_MODIFIED_BY", nullable = false)
    private String lastModifiedBy;

    @NotNull
    @Column(name = "LAST_MODIFIED_DATE", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp lastModifiedTime;
}
