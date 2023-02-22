package com.vladimir.ppm.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "log_records")
public class LogRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String user;

    @Enumerated(EnumType.STRING)
    private Acts act;

    @Enumerated(EnumType.STRING)
    private Objects object;

    private String objName;
    private Date date;
    private String comment;

    public LogRecord() {}

    public LogRecord(String user, Acts act, Objects object, String objName, Date date, String comment) {
        this.user = user;
        this.act = act;
        this.object = object;
        this.objName = objName;
        this.date = date;
        this.comment = comment;
    }

    public Long getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    public Acts getAct() {
        return act;
    }

    public Objects getObject() {
        return object;
    }

    public String getObjName() {
        return objName;
    }

    public Date getDate() {
        return date;
    }

    public String getDateStr() {
        return String.format("%1$tF %1$tT", date);
    }

    public String getComment() {
        return comment;
    }
}
