package com.smartschedule.common;

import java.time.LocalDate;

public class HolidayInfo {
    private LocalDate date;
    private String name;
    private String type;

    public HolidayInfo() {}

    public HolidayInfo(LocalDate date, String name, String type) {
        this.date = date;
        this.name = name;
        this.type = type;
    }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}