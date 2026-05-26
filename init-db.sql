CREATE TABLE users (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    full_name       VARCHAR(255) NOT NULL,
    role            ENUM('STUDENT','TEACHER','ADMIN') NOT NULL DEFAULT 'STUDENT',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email)
);

CREATE TABLE exams (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    title               VARCHAR(500) NOT NULL,
    description         TEXT,
    created_by          BIGINT NOT NULL,
    duration_minutes    INT NOT NULL,
    start_time          DATETIME NOT NULL,
    end_time            DATETIME NOT NULL,
    max_tab_violations  INT DEFAULT 3,
    screen_share_required BOOLEAN DEFAULT TRUE,
    grace_period_seconds  INT DEFAULT 30,
    is_published        BOOLEAN DEFAULT FALSE,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id),
    INDEX idx_start (start_time)
);

CREATE TABLE questions (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    exam_id         BIGINT NOT NULL,
    question_text   TEXT NOT NULL,
    question_type   ENUM('MCQ','SHORT_ANSWER','ESSAY') NOT NULL,
    points          INT DEFAULT 1,
    sort_order      INT DEFAULT 0,
    FOREIGN KEY (exam_id) REFERENCES exams(id) ON DELETE CASCADE,
    INDEX idx_exam (exam_id)
);

CREATE TABLE question_options (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_id     BIGINT NOT NULL,
    option_text     TEXT NOT NULL,
    is_correct      BOOLEAN DEFAULT FALSE,
    sort_order      INT DEFAULT 0,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);

CREATE TABLE exam_submissions (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    exam_id         BIGINT NOT NULL,
    user_id         BIGINT NOT NULL,
    status          ENUM('IN_PROGRESS','SUBMITTED','AUTO_SUBMITTED','LOCKED') NOT NULL,
    started_at      DATETIME NOT NULL,
    submitted_at    DATETIME,
    score           DECIMAL(5,2),
    ip_address      VARCHAR(45),
    user_agent      VARCHAR(500),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (exam_id) REFERENCES exams(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE KEY uk_exam_user (exam_id, user_id)
);

CREATE TABLE student_answers (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    submission_id       BIGINT NOT NULL,
    question_id         BIGINT NOT NULL,
    selected_option_id  BIGINT,
    answer_text         TEXT,
    answered_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (submission_id) REFERENCES exam_submissions(id),
    FOREIGN KEY (question_id) REFERENCES questions(id),
    FOREIGN KEY (selected_option_id) REFERENCES question_options(id),
    UNIQUE KEY uk_sub_question (submission_id, question_id)
);

CREATE TABLE violation_logs (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    submission_id   BIGINT NOT NULL,
    user_id         BIGINT NOT NULL,
    violation_type  ENUM('TAB_SWITCH','TAB_HIDDEN','SCREEN_SHARE_STOPPED',
                         'HEARTBEAT_MISS','DEVTOOLS_OPEN','COPY_PASTE',
                         'RIGHT_CLICK','FULLSCREEN_EXIT') NOT NULL,
    severity        ENUM('LOW','MEDIUM','HIGH','CRITICAL') NOT NULL,
    details         JSON,
    client_timestamp DATETIME NOT NULL,
    server_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (submission_id) REFERENCES exam_submissions(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_sub (submission_id),
    INDEX idx_user_time (user_id, server_timestamp)
);

CREATE TABLE heartbeat_logs (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    submission_id   BIGINT NOT NULL,
    received_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    screen_sharing  BOOLEAN NOT NULL,
    tab_visible     BOOLEAN NOT NULL,
    FOREIGN KEY (submission_id) REFERENCES exam_submissions(id),
    INDEX idx_sub_time (submission_id, received_at)
);

-- ============================================================
-- SEED DATA: Test accounts + sample exam
-- Password for all accounts: password123
-- ============================================================

-- Teacher account
INSERT INTO users (id, email, password_hash, full_name, role) VALUES
(1, 'teacher@test.com', '$2a$10$DDcPoDgfqA5QBBnSz1JVuuEUfd3QEtDI6mv3NCYoN9J.NIi.T1e3q', 'John Teacher', 'TEACHER');

-- Student accounts
INSERT INTO users (id, email, password_hash, full_name, role) VALUES
(2, 'student@test.com', '$2a$10$DDcPoDgfqA5QBBnSz1JVuuEUfd3QEtDI6mv3NCYoN9J.NIi.T1e3q', 'Alice Student', 'STUDENT'),
(3, 'student2@test.com', '$2a$10$DDcPoDgfqA5QBBnSz1JVuuEUfd3QEtDI6mv3NCYoN9J.NIi.T1e3q', 'Bob Student', 'STUDENT');

-- Admin account
INSERT INTO users (id, email, password_hash, full_name, role) VALUES
(4, 'admin@test.com', '$2a$10$DDcPoDgfqA5QBBnSz1JVuuEUfd3QEtDI6mv3NCYoN9J.NIi.T1e3q', 'Admin User', 'ADMIN');

-- Sample Exam 1: active now (starts 1 hour ago, ends in 23 hours)
INSERT INTO exams (id, title, description, created_by, duration_minutes, start_time, end_time,
                   max_tab_violations, screen_share_required, grace_period_seconds, is_published) VALUES
(1, 'Introduction to Computer Science - Midterm',
    'This exam covers chapters 1-5: algorithms, data structures, and basic programming concepts.',
    1, 60,
    DATE_SUB(NOW(), INTERVAL 1 HOUR),
    DATE_ADD(NOW(), INTERVAL 23 HOUR),
    3, TRUE, 30, TRUE);

-- Sample Exam 2: future exam (not yet started)
INSERT INTO exams (id, title, description, created_by, duration_minutes, start_time, end_time,
                   max_tab_violations, screen_share_required, grace_period_seconds, is_published) VALUES
(2, 'Database Systems - Final Exam',
    'Covers SQL, normalization, indexing, and transaction management.',
    1, 90,
    DATE_ADD(NOW(), INTERVAL 7 DAY),
    DATE_ADD(NOW(), INTERVAL 8 DAY),
    5, TRUE, 60, TRUE);

-- Sample Exam 3: unpublished draft
INSERT INTO exams (id, title, description, created_by, duration_minutes, start_time, end_time,
                   max_tab_violations, screen_share_required, grace_period_seconds, is_published) VALUES
(3, 'Software Engineering - Quiz 3 (Draft)',
    'Quick quiz on design patterns and SOLID principles.',
    1, 20,
    DATE_ADD(NOW(), INTERVAL 14 DAY),
    DATE_ADD(NOW(), INTERVAL 15 DAY),
    2, FALSE, 15, FALSE);

-- Questions for Exam 1 (CS Midterm)
INSERT INTO questions (id, exam_id, question_text, question_type, points, sort_order) VALUES
(1, 1, 'What is the time complexity of binary search on a sorted array of n elements?', 'MCQ', 5, 1),
(2, 1, 'Which data structure uses FIFO (First In, First Out) ordering?', 'MCQ', 5, 2),
(3, 1, 'What does the acronym "DRY" stand for in software development?', 'MCQ', 5, 3),
(4, 1, 'Which sorting algorithm has the best average-case time complexity?', 'MCQ', 5, 4),
(5, 1, 'What is a "pointer" in programming?', 'MCQ', 5, 5),
(6, 1, 'Name one advantage of using a linked list over an array.', 'SHORT_ANSWER', 10, 6),
(7, 1, 'Explain the difference between a stack and a queue. Provide a real-world example for each.', 'ESSAY', 15, 7),
(8, 1, 'In object-oriented programming, what is "encapsulation"?', 'MCQ', 5, 8),
(9, 1, 'What is the output of: print(len("Hello World"))?', 'SHORT_ANSWER', 5, 9),
(10, 1, 'Which of the following is NOT a valid HTTP method?', 'MCQ', 5, 10);

-- Options for MCQ questions
INSERT INTO question_options (id, question_id, option_text, is_correct, sort_order) VALUES
(1, 1, 'O(1)', FALSE, 1),
(2, 1, 'O(log n)', TRUE, 2),
(3, 1, 'O(n)', FALSE, 3),
(4, 1, 'O(n log n)', FALSE, 4),
(5, 2, 'Stack', FALSE, 1),
(6, 2, 'Queue', TRUE, 2),
(7, 2, 'Binary Tree', FALSE, 3),
(8, 2, 'Hash Map', FALSE, 4),
(9, 3, 'Do Repeat Yourself', FALSE, 1),
(10, 3, 'Don''t Repeat Yourself', TRUE, 2),
(11, 3, 'Data Replication Yield', FALSE, 3),
(12, 3, 'Direct Resource Yielding', FALSE, 4),
(13, 4, 'Bubble Sort - O(n²)', FALSE, 1),
(14, 4, 'Merge Sort - O(n log n)', TRUE, 2),
(15, 4, 'Selection Sort - O(n²)', FALSE, 3),
(16, 4, 'Insertion Sort - O(n²)', FALSE, 4),
(17, 5, 'A variable that stores a memory address', TRUE, 1),
(18, 5, 'A type of loop construct', FALSE, 2),
(19, 5, 'A function that returns void', FALSE, 3),
(20, 5, 'An object-oriented design pattern', FALSE, 4),
(21, 8, 'Hiding internal state and requiring interaction through public methods', TRUE, 1),
(22, 8, 'Creating multiple instances of a class', FALSE, 2),
(23, 8, 'Inheriting properties from a parent class', FALSE, 3),
(24, 8, 'Converting one data type to another', FALSE, 4),
(25, 10, 'GET', FALSE, 1),
(26, 10, 'POST', FALSE, 2),
(27, 10, 'SUBMIT', TRUE, 3),
(28, 10, 'DELETE', FALSE, 4);

-- Questions for Exam 2 (Database Systems)
INSERT INTO questions (id, exam_id, question_text, question_type, points, sort_order) VALUES
(11, 2, 'What does ACID stand for in database transactions?', 'SHORT_ANSWER', 10, 1),
(12, 2, 'Explain the difference between 2NF and 3NF with examples.', 'ESSAY', 20, 2),
(13, 2, 'Which SQL keyword removes duplicate rows from a result set?', 'MCQ', 5, 3);

INSERT INTO question_options (id, question_id, option_text, is_correct, sort_order) VALUES
(29, 13, 'UNIQUE', FALSE, 1),
(30, 13, 'DISTINCT', TRUE, 2),
(31, 13, 'SEPARATE', FALSE, 3),
(32, 13, 'FILTER', FALSE, 4);

-- ============================================================
-- LOGIN CREDENTIALS:
--   Teacher:  teacher@test.com  / password123
--   Student:  student@test.com  / password123
--   Student2: student2@test.com / password123
--   Admin:    admin@test.com    / password123
--
-- Exam 1 (CS Midterm) is ACTIVE NOW - students can start immediately
-- Exam 2 (DB Final) starts in 7 days - visible but not startable
-- Exam 3 (SE Quiz) is a draft - only visible to teacher
-- ============================================================
