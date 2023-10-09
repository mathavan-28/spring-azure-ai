package com.rai.online.aidemo.utils;

import lombok.Getter;

@Getter
public enum AIDemoColorCode {

    COLOR_RED("Red"),
    COLOR_GREEN("Green");

    private final String code;

    private final String value;

    AIDemoColorCode(String value) {
        this.code = this.name();
        this.value = value;
    }
}
