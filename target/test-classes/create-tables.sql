CREATE TABLE questions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    topic VARCHAR(255) NOT NULL,
    difficulty_rank INT NOT NULL,
    content VARCHAR(255) NOT NULL
);

CREATE TABLE responses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    question_id INT,
    text VARCHAR(255) NOT NULL,
    correct BOOLEAN NOT NULL,
    FOREIGN KEY (question_id) REFERENCES questions(id)
);