package com.integration.zoy.entity;

import javax.persistence.*;

@Entity
@Table(name = "triggered_on", schema = "pgadmin")
public class TriggeredOn {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "trigger_on", nullable = false, length = 100)
    private String triggerOn;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTriggerOn() {
        return triggerOn;
    }

    public void setTriggerOn(String triggerOn) {
        this.triggerOn = triggerOn;
    }
}