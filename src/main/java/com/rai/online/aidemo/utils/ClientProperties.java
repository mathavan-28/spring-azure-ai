package com.rai.online.aidemo.utils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ClientProperties {

    private String baseUrl;

    private boolean isSslEnabled;

    private int connectTimeoutMilliSeconds;

    private int readTimeoutMilliSeconds;

    private int writeTimeoutMilliSeconds;

    private int maxConnections;
}

