package com.lahroubasmae.logservicefinal.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "logs")
public class Log {

    @Id
    private String id;
    private String userEmail;
    private String action;
    private Date timestamp;

    public Log() {}

    public Log(String id, String userEmail, String action, Date timestamp) {
        this.id = id;
        this.userEmail = userEmail;
        this.action = action;
        this.timestamp = timestamp;
    }

    // Getters & Setters

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}
