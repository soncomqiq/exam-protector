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

-- ============================================================
-- Sample Exam: active now (starts 1 hour ago, ends in 23 hours)
-- ============================================================
INSERT INTO exams (id, title, description, created_by, duration_minutes, start_time, end_time,
                   max_tab_violations, screen_share_required, grace_period_seconds, is_published) VALUES
(1, 'Introduction to Computer Science - Midterm',
    'This exam covers chapters 1-5: algorithms, data structures, and basic programming concepts.',
    1, 60,
    DATE_SUB(NOW(), INTERVAL 1 HOUR),
    DATE_ADD(NOW(), INTERVAL 23 HOUR),
    3, TRUE, 30, TRUE);

-- A future exam (not yet started)
INSERT INTO exams (id, title, description, created_by, duration_minutes, start_time, end_time,
                   max_tab_violations, screen_share_required, grace_period_seconds, is_published) VALUES
(2, 'Database Systems - Final Exam',
    'Covers SQL, normalization, indexing, and transaction management.',
    1, 90,
    DATE_ADD(NOW(), INTERVAL 7 DAY),
    DATE_ADD(NOW(), INTERVAL 8 DAY),
    5, TRUE, 60, TRUE);

-- Unpublished draft exam
INSERT INTO exams (id, title, description, created_by, duration_minutes, start_time, end_time,
                   max_tab_violations, screen_share_required, grace_period_seconds, is_published) VALUES
(3, 'Software Engineering - Quiz 3 (Draft)',
    'Quick quiz on design patterns and SOLID principles.',
    1, 20,
    DATE_ADD(NOW(), INTERVAL 14 DAY),
    DATE_ADD(NOW(), INTERVAL 15 DAY),
    2, FALSE, 15, FALSE);

-- ============================================================
-- Questions for Exam 1 (CS Midterm)
-- ============================================================

-- Q1: Multiple Choice
INSERT INTO questions (id, exam_id, question_text, question_type, points, sort_order) VALUES
(1, 1, 'What is the time complexity of binary search on a sorted array of n elements?', 'MCQ', 5, 1);

INSERT INTO question_options (id, question_id, option_text, is_correct, sort_order) VALUES
(1, 1, 'O(1)', FALSE, 1),
(2, 1, 'O(log n)', TRUE, 2),
(3, 1, 'O(n)', FALSE, 3),
(4, 1, 'O(n log n)', FALSE, 4);

-- Q2: Multiple Choice
INSERT INTO questions (id, exam_id, question_text, question_type, points, sort_order) VALUES
(2, 1, 'Which data structure uses FIFO (First In, First Out) ordering?', 'MCQ', 5, 2);

INSERT INTO question_options (id, question_id, option_text, is_correct, sort_order) VALUES
(5, 2, 'Stack', FALSE, 1),
(6, 2, 'Queue', TRUE, 2),
(7, 2, 'Binary Tree', FALSE, 3),
(8, 2, 'Hash Map', FALSE, 4);

-- Q3: Multiple Choice
INSERT INTO questions (id, exam_id, question_text, question_type, points, sort_order) VALUES
(3, 1, 'What does the acronym "DRY" stand for in software development?', 'MCQ', 5, 3);

INSERT INTO question_options (id, question_id, option_text, is_correct, sort_order) VALUES
(9, 3, 'Do Repeat Yourself', FALSE, 1),
(10, 3, 'Don''t Repeat Yourself', TRUE, 2),
(11, 3, 'Data Replication Yield', FALSE, 3),
(12, 3, 'Direct Resource Yielding', FALSE, 4);

-- Q4: Multiple Choice
INSERT INTO questions (id, exam_id, question_text, question_type, points, sort_order) VALUES
(4, 1, 'Which sorting algorithm has the best average-case time complexity?', 'MCQ', 5, 4);

INSERT INTO question_options (id, question_id, option_text, is_correct, sort_order) VALUES
(13, 4, 'Bubble Sort - O(n²)', FALSE, 1),
(14, 4, 'Merge Sort - O(n log n)', TRUE, 2),
(15, 4, 'Selection Sort - O(n²)', FALSE, 3),
(16, 4, 'Insertion Sort - O(n²)', FALSE, 4);

-- Q5: Multiple Choice
INSERT INTO questions (id, exam_id, question_text, question_type, points, sort_order) VALUES
(5, 1, 'What is a "pointer" in programming?', 'MCQ', 5, 5);

INSERT INTO question_options (id, question_id, option_text, is_correct, sort_order) VALUES
(17, 5, 'A variable that stores a memory address', TRUE, 1),
(18, 5, 'A type of loop construct', FALSE, 2),
(19, 5, 'A function that returns void', FALSE, 3),
(20, 5, 'An object-oriented design pattern', FALSE, 4);

-- Q6: Short Answer
INSERT INTO questions (id, exam_id, question_text, question_type, points, sort_order) VALUES
(6, 1, 'Name one advantage of using a linked list over an array.', 'SHORT_ANSWER', 10, 6);

-- Q7: Essay
INSERT INTO questions (id, exam_id, question_text, question_type, points, sort_order) VALUES
(7, 1, 'Explain the difference between a stack and a queue. Provide a real-world example for each data structure and describe when you would choose one over the other.', 'ESSAY', 15, 7);

-- Q8: Multiple Choice
INSERT INTO questions (id, exam_id, question_text, question_type, points, sort_order) VALUES
(8, 1, 'In object-oriented programming, what is "encapsulation"?', 'MCQ', 5, 8);

INSERT INTO question_options (id, question_id, option_text, is_correct, sort_order) VALUES
(21, 8, 'Hiding internal state and requiring interaction through public methods', TRUE, 1),
(22, 8, 'Creating multiple instances of a class', FALSE, 2),
(23, 8, 'Inheriting properties from a parent class', FALSE, 3),
(24, 8, 'Converting one data type to another', FALSE, 4);

-- Q9: Short Answer
INSERT INTO questions (id, exam_id, question_text, question_type, points, sort_order) VALUES
(9, 1, 'What is the output of: print(len("Hello World"))?', 'SHORT_ANSWER', 5, 9);

-- Q10: Multiple Choice
INSERT INTO questions (id, exam_id, question_text, question_type, points, sort_order) VALUES
(10, 1, 'Which of the following is NOT a valid HTTP method?', 'MCQ', 5, 10);

INSERT INTO question_options (id, question_id, option_text, is_correct, sort_order) VALUES
(25, 10, 'GET', FALSE, 1),
(26, 10, 'POST', FALSE, 2),
(27, 10, 'SUBMIT', TRUE, 3),
(28, 10, 'DELETE', FALSE, 4);

-- ============================================================
-- Questions for Exam 2 (Database Systems)
-- ============================================================

INSERT INTO questions (id, exam_id, question_text, question_type, points, sort_order) VALUES
(11, 2, 'What does ACID stand for in database transactions?', 'SHORT_ANSWER', 10, 1),
(12, 2, 'Explain the difference between 2NF and 3NF with examples.', 'ESSAY', 20, 2);

INSERT INTO questions (id, exam_id, question_text, question_type, points, sort_order) VALUES
(13, 2, 'Which SQL keyword is used to remove duplicate rows from a result set?', 'MCQ', 5, 3);

INSERT INTO question_options (id, question_id, option_text, is_correct, sort_order) VALUES
(29, 13, 'UNIQUE', FALSE, 1),
(30, 13, 'DISTINCT', TRUE, 2),
(31, 13, 'SEPARATE', FALSE, 3),
(32, 13, 'FILTER', FALSE, 4);

-- ============================================================
-- USAGE NOTES:
-- ============================================================
-- Login credentials:
--   Teacher:  teacher@test.com  / password123
--   Student:  student@test.com  / password123
--   Student2: student2@test.com / password123
--   Admin:    admin@test.com    / password123
--
-- Exam 1 (CS Midterm) is ACTIVE NOW - students can start immediately
-- Exam 2 (DB Final) starts in 7 days - visible but not startable yet
-- Exam 3 (SE Quiz) is a draft - only visible to teacher
-- ============================================================
