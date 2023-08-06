package com.rai.online.aidemo.exceptions;

import lombok.Getter;

@Getter
public enum SpringAIDemoErrorCode {

    E2001("Field Validation Failed"),
    E2002("Constraint Violation"),
    E2003("Cert Parse Error"),
    E2004("Server Error"),
    E2006("Invalid User Id"),
    E2010("Invalid input"),
    E2012("User Already Exists"),
    E2013("User Not Exists"),
    //        E2014("Invalid IBAN"),
    E2015("Invalid User Id"),
    //        E2016("Debtor Id mismatch"),
    E2017("User Id mismatch"),
    //        E2018("Category Id mismatch with edoId"),
//        E2019("Amount is out of range"),
//        E2020("Certificate not trusted"),
    E2022("JSON Parse Error"),
    //        E2023("Cannot add more categories for a edoId"),
//        E2024("Invalid Edo Id"),
//        E2025("Record already exists"),
//        E2026("Duplicate records exists"),
//        E2027("Empty MandateReference"),
//        E2028("Invalid Current Batch ID"),
//        E2029("Invalid Access of Batch"),
//        E2030("Invalid Current Batch Txn ID"),
//        E2031("There is no debtors to add batch transaction list."),
//        E2032("Invalid Debtors Ids are available in the batch."),
//        E2033("Invalid Sort Value for Batch Transactions sorting."),
    E2034("No Values to Update.");

    private final String code;

    private final String defaultMessage;

    SpringAIDemoErrorCode(String defaultMessage) {
        this.code = this.name();
        this.defaultMessage = defaultMessage;
    }
}