package com.example.bhava.model;

import com.google.gson.annotations.SerializedName;

public class RoutineStatusResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("inRoutine")
    private boolean inRoutine;

    public boolean isSuccess() {
        return success;
    }

    public boolean isInRoutine() {
        return inRoutine;
    }
}
