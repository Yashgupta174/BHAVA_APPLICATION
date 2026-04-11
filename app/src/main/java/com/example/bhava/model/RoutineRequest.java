package com.example.bhava.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RoutineRequest {
    @SerializedName("days")
    private List<String> days;

    @SerializedName("reminderTime")
    private String reminderTime;

    public RoutineRequest(List<String> days) {
        this.days = days;
    }

    public List<String> getDays() { return days; }
    public String getReminderTime() { return reminderTime; }
    public void setReminderTime(String reminderTime) { this.reminderTime = reminderTime; }
}
