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
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Setter
@Getter
@NoArgsConstructor
@Entity
@ToString
@DynamicInsert
@DynamicUpdate
@Table(name = "loan", schema = "rai", uniqueConstraints = {@UniqueConstraint(columnNames = "LOAN_ID")})
public class LoanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LOAN_ID")
    private Long loanId;

    @ManyToOne(targetEntity = UserEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID")
    @ToString.Exclude
    private UserEntity userEntity;

    @NotNull(message = "Account number should not be null")
    @Column(name = "LOAN_ACC_NR")
    private Integer loanAccountNo = 1;

    @NotNull(message = "Plan Name should not be null")
    @Column(name = "PLAN_NM", nullable = false)
    private String planName;

    @NotNull(message = "Min sanctioned amount should not be null")
    @Digits(integer = 18, fraction = 2)
    @Column(name = "MIN_AMNT", nullable = false)
    private Double minSanctionedAmount;

    @Digits(integer = 18, fraction = 2)
    @Column(name = "ROI", nullable = false)
    private Double interestRate;

    @NotNull(message = "Max Tenure should not be null")
    @Column(name = "MAX_TENURE")
    private Integer maxTenure;

    @NotNull
    @Column(name = "LAST_MODIFIED_BY", nullable = false)
    private String lastModifiedBy;

    @NotNull
    @Column(name = "LAST_MODIFIED_DATE", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp lastModifiedTime;
}
