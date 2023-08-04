package com.rabo.online.aidemo.model;

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
public class DemoMessage {

    private String appName;
    private String message;
}
