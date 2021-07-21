package com.vladimir.ppm.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "log_records")
public class LogRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String user;
    private Acts act;
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
