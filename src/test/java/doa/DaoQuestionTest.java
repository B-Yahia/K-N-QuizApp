    package doa;

    import models.Question;
    import models.Response;
    import org.junit.jupiter.api.AfterEach;
    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.Test;

    import java.sql.Connection;
    import java.sql.DriverManager;
    import java.sql.SQLException;
    import java.util.Arrays;
    import java.util.List;
    import static org.junit.jupiter.api.Assertions.*;

    class DaoQuestionTest {
        private DaoQuestion daoQuestion;
        private Connection connection;

        @BeforeEach
        void setUp() throws SQLException {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/kn_2", "root", "Dieuest1.");
            // Initialize the DaoQuestion object
            daoQuestion = new DaoQuestion(connection);
        }


        @Test
        void testSaveQuestion() throws SQLException {
            // Prepare a question
            List<Response> responses = Arrays.asList(new Response(0, 0, "4", true), new Response(1, 0, "5", false));
            Question questionToSave = new Question(0, "Math", 3, "What is 2 + 2?", responses);

            // Save a question using daoQuestion
            daoQuestion.saveQuestion(questionToSave);

            // Retrieve the question from the database
            var savedQuestions = daoQuestion.searchQuestionByTopic("Math");
            assertFalse(savedQuestions.isEmpty(), "No questions retrieved from database");

            var retrievedQuestion = savedQuestions.get(0);

            // Assert that the saved and retrieved questions are equal
            assertEquals(questionToSave.getTopic(), retrievedQuestion.getTopic(), "Topics do not match");
            assertEquals(questionToSave.getDifficultyRank(), retrievedQuestion.getDifficultyRank(), "Difficulty ranks do not match");
            assertEquals(questionToSave.getContent(), retrievedQuestion.getContent(), "Contents do not match");

            //Remove test data from DB
            daoQuestion.deleteQuestion(retrievedQuestion.getId());
        }

        @Test
        void testUpdateQuestion() throws SQLException {
            // Prepare a question
            List<Response> responses = Arrays.asList(new Response(0, 0, "4", true), new Response(1, 0, "5", false));
            Question questionToSave = new Question(0, "Math", 3, "What is 2 + 2?", responses);

            // Save a question using daoQuestion
            daoQuestion.saveQuestion(questionToSave);

            // Retrieve the question from the database
            List<Question> savedQuestions = daoQuestion.searchQuestionByTopic("Math");
            assertFalse(savedQuestions.isEmpty());

            var retrievedQuestion = savedQuestions.get(0);

            // Update the question
            retrievedQuestion.setTopic("New Math");
            retrievedQuestion.setDifficultyRank(5);
            retrievedQuestion.setContent("What is 3 + 3?");
            retrievedQuestion.setResponses(Arrays.asList(new Response(0, 0, "6", true), new Response(1, 0, "7", false)));

            // Update the question using daoQuestion
            daoQuestion.updateQuestion(retrievedQuestion);

            // Retrieve the updated question from the database
            var updatedQuestions = daoQuestion.searchQuestionByTopic("New Math");
            assertFalse(updatedQuestions.isEmpty());
            Question updatedQuestion = updatedQuestions.get(0);

            // Assert that the updated and retrieved questions are equal
            assertEquals(retrievedQuestion.getTopic(), updatedQuestion.getTopic());
            assertEquals(retrievedQuestion.getDifficultyRank(), updatedQuestion.getDifficultyRank());
            assertEquals(retrievedQuestion.getContent(), updatedQuestion.getContent());

            //Remove test data from DB
            daoQuestion.deleteQuestion(retrievedQuestion.getId());
        }

        @Test
        void testDeleteQuestion() throws SQLException {
            List<Response> responses = Arrays.asList(new Response(0, 0, "4", true), new Response(1, 0, "5", false));
            Question questionToSave = new Question(0, "Math", 3, "What is 2 + 2?", responses);

            // Save a question using daoQuestion
            daoQuestion.saveQuestion(questionToSave);

            // Retrieve the question from the database
            var savedQuestions = daoQuestion.searchQuestionByTopic("Math");
            assertFalse(savedQuestions.isEmpty());

            var retrievedQuestion = savedQuestions.get(0);

            // Delete the question using daoQuestion
            daoQuestion.deleteQuestion(retrievedQuestion.getId());

            // Attempt to retrieve the deleted question from the database
            List<Question> questionsAfterDelete = daoQuestion.searchQuestionByTopic("Math");


        }

        @Test
        void testSearchQuestionByTopic() throws SQLException {
            List<Response> responses = Arrays.asList(new Response(0, 0, "4", true), new Response(1, 0, "5", false));
            Question mathQuestion = new Question(0, "Math", 3, "What is 2 + 2?", responses);
            Question math2Question = new Question(0, "Math", 4, "What is 2 + 2?", responses);

            daoQuestion.saveQuestion(mathQuestion);
            daoQuestion.saveQuestion(math2Question);

            // Search questions by topic using daoQuestion
            List<Question> mathQuestions = daoQuestion.searchQuestionByTopic("Math");

            // Assert that only questions with the correct topic are returned
            for (Question question : mathQuestions) {
                assertEquals("Math", question.getTopic());
            }

            //Remove test data from DB
            for (Question question : mathQuestions) {
                daoQuestion.deleteQuestion(question.getId());
            }

        }
    }