package com.messagehub.samples.env;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateTopicConfig {

    @JsonProperty("retentionMs")
    private long retentionMs;

    @JsonCreator
    public CreateTopicConfig(long retentionMs) {
        this.retentionMs = retentionMs;
    }
    
    public long getRetentionMs() {
        return retentionMs;
    }
}
