package doa;

import models.Question;
import models.Response;
import repo.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class DaoQuestion {

    // Dependencies injection
    private final Connection connection;
    public DaoQuestion(Connection connection) {
        this.connection = connection;
    }

    // Method to save a Question
    public void saveQuestion(Question question) throws SQLException {

        try (var pstmtQuestion = connection.prepareStatement("INSERT INTO questions (topic, difficulty_rank , content) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);){
            pstmtQuestion.setString(1, question.getTopic());
            pstmtQuestion.setInt(2, question.getDifficultyRank());
            pstmtQuestion.setString(3, question.getContent());
            pstmtQuestion.executeUpdate();
            // Get the ID of the inserted question
            var generatedKeys = pstmtQuestion.getGeneratedKeys();
            if (generatedKeys.next()) {
                int questionId = generatedKeys.getInt(1);
                saveResponses(question.getResponses(), questionId, connection);
            }
        }
    }

    // Method to update a Question
    public void updateQuestion(Question question) throws SQLException {
        try (var pstmtQuestion  = connection.prepareStatement("UPDATE questions SET topic = ?, difficulty_rank = ?, content = ? WHERE question_id = ?");){
            pstmtQuestion.setString(1, question.getTopic());
            pstmtQuestion.setInt(2, question.getDifficultyRank());
            pstmtQuestion.setString(3, question.getContent());
            pstmtQuestion.setInt(4, question.getId());
            pstmtQuestion.executeUpdate();
            // Delete responses that are linked to this question
            deleteResponses(question.getId(), connection);
            // Insert new responses to the question
            saveResponses(question.getResponses(), question.getId(), connection);
        }
    }

    // Method to delete a Question from the database
    public void deleteQuestion(int questionId) throws SQLException {
        // Delete the responses associated with the question
        deleteResponses(questionId, connection);
            try (var pstmtDeleteQuestion = connection.prepareStatement("DELETE FROM questions WHERE question_id = ?")) {
                pstmtDeleteQuestion.setInt(1, questionId);
                pstmtDeleteQuestion.executeUpdate();
            }
    }

    // Private method to save the Responses associated with a Question
    private void saveResponses(List<Response> responses, int questionId, Connection connection) throws SQLException {
        try (var pstmtSaveResponses = connection.prepareStatement("INSERT INTO responses (question_id, text, correct) VALUES (?, ?, ?)")){
            for (Response response : responses) {
                pstmtSaveResponses.setInt(1, questionId);
                pstmtSaveResponses.setString(2, response.getText());
                pstmtSaveResponses.setBoolean(3, response.isCorrect());
                pstmtSaveResponses.addBatch();
            }
            pstmtSaveResponses.executeBatch();

        }
    }

    // Private method to delete the Responses associated with a Question
    private void deleteResponses(int questionId, Connection connection) throws SQLException {
        try (var pstmtDeleteResponses = connection.prepareStatement("DELETE FROM responses WHERE question_id = ?")){
            pstmtDeleteResponses.setInt(1, questionId);
            pstmtDeleteResponses.executeUpdate();
        }
    }

    //Search questions ids by topic
    private List<Integer> findQuestionIdsByTopic(String topic) throws SQLException {
        List<Integer> questionIds = new ArrayList<>();
        try (var pstmt = connection.prepareStatement("SELECT question_id FROM questions WHERE topic = ?")) {
            pstmt.setString(1, topic);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    questionIds.add(rs.getInt("question_id"));
                }
            }
        }
        return questionIds;
    }

    private Question findQuestionById(int questionId) throws SQLException {
        Question question = null;
        try (var pstmt = connection.prepareStatement("SELECT topic, difficulty_rank , content FROM questions WHERE question_id = ?")) {
            pstmt.setInt(1, questionId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next())  question= new Question(questionId,rs.getString("topic"),rs.getInt("difficulty_rank"),rs.getString("content"),new ArrayList<>());
            }
        }
        return question;
    }

    private List<Response> findResponsesByQuestionId(int questionId) throws SQLException {
        List<Response> responses = new ArrayList<>();
        String sql = "SELECT response_id, text, correct FROM responses WHERE question_id = ?";
        try (var pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, questionId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    responses.add(new Response(rs.getInt("response_id"),questionId,rs.getString("text"),rs.getBoolean("correct")));
                }
            }
        }
        return responses;
    }

    // Method to search Question by topic
    public List<Question> searchQuestionByTopic(String topic) throws SQLException {
        List<Question> questions = new ArrayList<>();
        List<Integer> questionIds = findQuestionIdsByTopic(topic);
        for (Integer questionId : questionIds) {
            Question question = findQuestionById(questionId);
            if (question != null) {
                question.setResponses(findResponsesByQuestionId(questionId));
                questions.add(question);
            }
        }
        return questions;
    }
}
