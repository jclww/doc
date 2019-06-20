package com.xxx.log.entity;

import com.xxx.log.LogError;
import lombok.Data;

@Data
public class LogDataDetail {

    private LogError error;
    private Object extra;
    private String logStatisticsField;

}
