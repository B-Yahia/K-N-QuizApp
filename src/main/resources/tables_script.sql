CREATE TABLE quizzes (
    quiz_id INT AUTO_INCREMENT PRIMARY KEY
);

CREATE TABLE questions (
    question_id INT AUTO_INCREMENT PRIMARY KEY,
    quiz_id INT,
    topic VARCHAR(255),
    difficulty_rank INT,
    content TEXT,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(quiz_id)
);

CREATE TABLE responses (
    response_id INT AUTO_INCREMENT PRIMARY KEY,
    question_id INT,
    text TEXT,
    is_correct BOOLEAN,
    FOREIGN KEY (question_id) REFERENCES questions(question_id)
);