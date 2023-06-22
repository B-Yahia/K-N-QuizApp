package models;

public class Response {

    private int id;
    private int questionId;
    private String text;
    private boolean isCorrect;


    public Response(int id, int questionId, String text, boolean isCorrect) {
        this.id = id;
        this.questionId = questionId;
        this.text = text;
        this.isCorrect = isCorrect;
    }

    public String getText() {
        return text;
    }

    public boolean isCorrect() {
        return isCorrect;
    }
}






