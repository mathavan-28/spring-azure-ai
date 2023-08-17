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
import java.sql.Date;
import java.sql.Timestamp;

@Setter
@Getter
@NoArgsConstructor
@Entity
@ToString
@DynamicInsert
@DynamicUpdate
@Table(name = "transactions", schema = "rai", uniqueConstraints = {@UniqueConstraint(columnNames = "ID")})
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long txnId;

    @ManyToOne(targetEntity = AccountEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "ACC_ID", referencedColumnName = "ACNT_ID")
    @ToString.Exclude
    private AccountEntity accountEntity;

    @Digits(integer = 18, fraction = 2)
    @Column(name = "AMNT", nullable = false)
    private Double amount;

    @Column(name = "DSCR")
    private String description;

    @Column(name = "PAYMENT_TYPE", nullable = false)
    private Integer paymentType;

    @NotNull(message = "Account payee should not be null")
    @Column(name = "ACC_PAYEE")
    private Integer accountPayee;

    @NotNull(message = "Transaction date should not be null")
    @Column(name = "TXN_DATE", nullable = false)
    private Date txnDate;

    @NotNull
    @Column(name = "LAST_MODIFIED_BY", nullable = false)
    private String lastModifiedBy;

    @NotNull
    @Column(name = "LAST_MODIFIED_DATE", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp lastModifiedTime;
}
