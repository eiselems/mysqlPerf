package com.example.demo.dto;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "vehicle_event")
public class Event {

    @Id
    private String vin;

    private String fieldOne;
    private String fieldTwo;

    public Event(String vin) {
        this.vin = vin;
    }

    public Event() {

    }
}
