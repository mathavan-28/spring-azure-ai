package com.rai.online.aidemo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.File;

@Getter
@Data
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DemoMessage {

    private String appName;
    private String message;

    private String audioFile;
}
