package com.xxx.log.entity;

import lombok.Data;

@Data
public class LogData {
    private String type;
    private String level;
    private String platform;
    private String tag;
    private String app;
    private String module;
    private LogDataDetail detail;

}
