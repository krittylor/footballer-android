package com.example.admin.footballer.Models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Order {
    public int amount;
    public String cardHolderName;
    public String cardNumber;
    public String cardType;
    public String cvc;
    public String datetime;
    public int durationType;
    public String expirationDate;
    public String fieldId;
    public String userId;

    public Order() {

    }

    public Order(String userId, String fieldId, int amount, String cardHolderName, String cardNumber, String cardType,
                 String cvc, String datetime, int durationType, String expirationDate) {
        this.userId = userId;
        this.fieldId = fieldId;
        this.amount = amount;
        this.cardHolderName = cardHolderName;
        this.cardNumber = cardNumber;
        this.cardType = cardType;
        this.cvc = cvc;
        this.datetime = datetime;
        this.durationType = durationType;
        this.expirationDate = expirationDate;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("fieldId", fieldId);
        result.put("amount", amount);
        result.put("cardHolderName", cardHolderName);
        result.put("cardType", cardType);
        result.put("cardNumber", cardNumber);
        result.put("expirationDate", expirationDate);
        result.put("cvc", cvc);
        result.put("durationType", durationType);
        result.put("dateTime", datetime);

        return result;
    }
}
