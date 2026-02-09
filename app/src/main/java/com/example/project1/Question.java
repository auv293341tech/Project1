package com.example.project1;

import java.util.ArrayList;
import java.util.List;

public class Question {
    private String questionId;
    private String text;
    private String type;
    private List<String> options;

    public Question(String questionId, String text, String type, List<String> options) {
        this.questionId = questionId;
        this.text = text;
        this.type = type;
        if (options == null) {
            this.options = new ArrayList<>();
        } else {
            this.options = options;
        }
    }

    public String getQuestionId() {
        return questionId;
    }

    public String getText() {
        return text;
    }

    public String getType() {
        return type;
    }

    public List<String> getOptions() {
        return options;
    }
}
