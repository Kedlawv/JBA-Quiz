package engine.model;

public class QuizGuessResponseObject {

    private boolean success;
    private String feedback;

    public QuizGuessResponseObject() {
    }

    public QuizGuessResponseObject(boolean success, String feedback) {
        this.success = success;
        this.feedback = feedback;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getFeedback() {
        return feedback;
    }
}
