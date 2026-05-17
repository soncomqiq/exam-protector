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
