package com.integration.zoy.entity;

import javax.persistence.*;

@Entity
@Table(name = "triggered_value", schema = "pgadmin")
public class TriggeredValue {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "trigger_value", nullable = false, length = 100)
    private String triggerValue;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTriggerValue() {
        return triggerValue;
    }

    public void setTriggerValue(String triggerValue) {
        this.triggerValue = triggerValue;
    }
}