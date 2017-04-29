package com.example.admin.footballer.Models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FieldSchedule {
    public int type;
    public String orderId;
    public String fieldId;
    public String userName;
    public static int NOT_AVAILABLE = 0x01;
    public static int AVAILABLE = 0x02;
    public FieldSchedule() {

    }

    public FieldSchedule(int type, String orderId, String fieldId, String userName) {
        this.type = type;
        this.orderId = orderId;
        this.fieldId = fieldId;
        this.userName = userName;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("type", type);
        result.put("orderId", orderId);
        result.put("fieldId", fieldId);
        result.put("userName", userName);
        return result;
    }
}
