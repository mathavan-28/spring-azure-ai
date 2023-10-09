package com.rai.online.aidemo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Data
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AiProcessedImageResponse {

    private String creditorName;

    private String creditorIdentifier;

    private String creditorAddress;

    private String creditorPostalCode;

    private String creditorCity;

    private String creditorCountry;

    private String mandateReference;

    private String creditorNameColor;

    private String creditorIdentifierColor;

    private String creditorAddressColor;

    private String creditorPostalCodeColor;

    private String creditorCityColor;

    private String creditorCountryColor;

    private String mandateReferenceColor;
}
