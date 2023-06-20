package com.example.nonreldataproject.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
public class OpcDataDTO {

    private String tagName;
    private Date serverTime;
    private Date sourceTime;
    private Date readTime;
    private Object value;

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public Date getServerTime() {
        return serverTime;
    }

    public void setServerTime(Date serverTime) {
        this.serverTime = serverTime;
    }

    public Date getSourceTime() {
        return sourceTime;
    }

    public void setSourceTime(Date sourceTime) {
        this.sourceTime = sourceTime;
    }

    public Date getReadTime() {
        return readTime;
    }

    public void setReadTime(Date readTime) {
        this.readTime = readTime;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "OpcDataDTO{" +
                "tagName='" + tagName + '\'' +
                ", serverTime=" + serverTime +
                ", sourceTime=" + sourceTime +
                ", readTime=" + readTime +
                ", value=" + value +
                '}';
    }
}
