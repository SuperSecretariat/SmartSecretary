// response/UploadResponse.java
package com.example.demo.dto.calendar;

import java.util.Set;

public class UploadResponse {
    private String message;
    private Set<String> groups;

    public UploadResponse(String message) {
        this.message = message;
    }

    public UploadResponse(String message, Set<String> groups) {
        this.message = message;
        this.groups = groups;
    }

    public String getMessage() {
        return message;
    }

    public Set<String> getGroups() {
        return groups;
    }
}
