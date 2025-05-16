package com.example.demo.response;

import java.util.List;

public class ChatResponse {
    private String answer;
    private List<String> sources;
    private List<String> chunks;

    public ChatResponse(String answer, List<String> sources, List<String> chunks) {
        this.answer = answer;
        this.sources = sources;
        this.chunks = chunks;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<String> getSources() {
        return sources;
    }

    public void setSources(List<String> sources) {
        this.sources = sources;
    }

    public List<String> getChunks() {
        return chunks;
    }

    public void setChunks(List<String> chunks) {
        this.chunks = chunks;
    }
}
