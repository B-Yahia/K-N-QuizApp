package models;

import java.util.List;

public class Quiz {
    private int id;
    private List<Question> questions;

    public Quiz() {
    }

    public Quiz(int id, List<Question> questions) {
        this.id = id;
        this.questions = questions;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}
