# Exam Protector: Setup and Usage Guide

This document explains both:

1. How to set up and run the system locally.
2. How to use the system as a teacher and as a student.

The platform has three parts:

- MySQL database (Docker)
- Spring Boot backend API (port 8080)
- React frontend app (port 5173)

## 1. Prerequisites

Install these first:

- Node.js 20+
- npm 10+
- Java 25
- Docker Desktop (or Docker Engine)

## 2. Start the System (Local Development)

From the repository root, run each part in this order.

### Step 1: Start MySQL

```bash
docker compose up -d
```

Expected DB settings:

- Host: localhost
- Port: 3308
- Database: exam_protector
- User: exam_user
- Password: exam_user_password

### Step 2: Start Backend

Open a second terminal:

```bash
cd backend
./mvnw spring-boot:run
```

Backend URL: http://localhost:8080

### Step 3: Start Frontend

Open a third terminal:

```bash
cd frontend
npm install
npm run dev
```

Frontend URL: http://localhost:5173

## 3. First-Time Login and Roles

When you open the frontend:

1. Register a new account from the Login/Register screen.
2. By default, new accounts are created as STUDENT.
3. Teacher-only features require TEACHER or ADMIN role.

Note: In local development, you can change a user role directly in the users table if needed.

## 4. How to Use as a Teacher

Use this flow to create and manage exams.

### 4.1 Create an Exam

1. Go to Dashboard.
2. Click Create Exam.
3. Fill required fields:
   - Title
   - Description
   - Duration
   - Start and End time
   - Max tab violations
   - Grace period for screen-share interruption
4. Toggle Published when ready.
5. Save.

### 4.2 Add Questions

1. Open the exam.
2. Go to Manage Questions.
3. Add questions using supported types:
   - MCQ
   - SHORT_ANSWER
   - ESSAY
4. For MCQ, add options and mark correct choices.

### 4.3 Monitor Results

1. Open exam results/submissions.
2. Review status of each submission:
   - IN_PROGRESS
   - SUBMITTED
   - AUTO_SUBMITTED
   - LOCKED
3. Review score and timing.

## 5. How to Use as a Student

Use this flow to take an exam safely.

### 5.1 Start an Exam

1. Go to Available Exams / Student Dashboard.
2. Choose an exam that is currently active.
3. Click Start Exam.

### 5.2 Pass the Screen-Share Gate

1. Grant browser permission for screen sharing.
2. Share the full screen (recommended).
3. The exam session starts only after screen sharing is active.

### 5.3 Answer Questions

1. Navigate with Previous and Next.
2. Answers are autosaved during the session.
3. A countdown timer tracks remaining time.

### 5.4 Submit

1. Click Submit Exam on the final question.
2. Confirm submission.
3. If time expires, exam may auto-submit.

## 6. Anti-Cheat Behavior (Important)

The platform monitors exam integrity events.

### Tracked events

- Tab hidden / tab switch
- Screen-share stopped
- Missed heartbeat
- DevTools detection signal
- Copy/paste and right-click blocking in exam flow

### What happens during violations

1. Violations are logged with client and server timestamps.
2. Warning/lock action is based on server-side threshold.
3. If threshold is exceeded, submission can be locked.
4. If screen share stops, grace countdown starts.
5. If grace expires without resuming share, exam can auto-submit.

## 7. Build and Validation Commands

Frontend checks:

```bash
cd frontend
npm run lint
npm run build
```

Backend checks:

```bash
cd backend
./mvnw test
```

## 8. Common Issues and Fixes

### Docker pull fails with 403 MediaTypeBlocked

If you see an error like MediaTypeBlocked while pulling mysql:8.0, this is usually a network/proxy/content-filter issue on the Docker blob CDN path.

Try:

1. Switch network (for example, hotspot) and retry docker pull mysql:8.0.
2. Disable VPN/proxy temporarily, or configure Docker proxy settings correctly.
3. Docker logout/login:

```bash
docker logout
docker login
```

4. Retry compose:

```bash
docker compose pull --no-parallel
docker compose up -d
```

### Port conflicts

If 3308 or 8080 or 5173 is in use, change the port mapping and corresponding config.

### Backend cannot connect to DB

Confirm:

- MySQL container is running and healthy.
- DB credentials match backend properties.

### Auth issues in browser

Clear session storage and login again.

## 9. Stop the System

Stop frontend and backend with Ctrl+C in their terminals.

Stop MySQL:

```bash
docker compose down
```
