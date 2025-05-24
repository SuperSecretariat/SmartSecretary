package com.example.demo.model.enums;

import com.example.demo.exceptions.InvalidFormRequestStatusException;

import java.util.Arrays;

public enum FormRequestStatus {
    PENDING,
    APPROVED,
    MODIFICATIONS_REQUIRED,
    IN_REVIEW;

    public static FormRequestStatus getStatusFromString (String status) throws InvalidFormRequestStatusException {
        return Arrays.stream(FormRequestStatus.values())
                .filter(e -> e.name().equalsIgnoreCase(status))
                .findFirst()
                .orElseThrow(() -> new InvalidFormRequestStatusException("Invalid status: " + status));
    }
}
