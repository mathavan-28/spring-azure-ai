package com.rai.online.aidemo.utils;

import lombok.Getter;
import lombok.Setter;
import net.jcip.annotations.GuardedBy;

@Getter
@Setter
public abstract class AzureAIClientProperties extends ClientProperties{

    @GuardedBy("this")
    private String endpoint;

    @GuardedBy("this")
    private String subscriptionKey;

    @GuardedBy("this")
    private String region;
}

