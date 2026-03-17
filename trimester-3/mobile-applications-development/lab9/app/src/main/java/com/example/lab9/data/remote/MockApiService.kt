package com.example.lab9.data.remote

import com.example.lab9.data.local.entity.AnnouncementEntity
import com.example.lab9.data.local.entity.CourseEntity
import kotlinx.coroutines.delay

/**
 * Simulates a remote API by returning realistic university data after a network-like delay.
 */
object MockApiService {

    private val mockCourses = listOf(
        CourseEntity(
            id = "CS301",
            code = "CS-301",
            title = "Data Structures & Algorithms",
            instructor = "Dr. Sarah Ahmed",
            dayOfWeek = "Monday",
            startTime = "09:00",
            endTime = "10:30",
            room = "CS Lab 2 (B-204)",
            credits = 3,
            color = 0xFF5C6BC0
        ),
        CourseEntity(
            id = "CS405",
            code = "CS-405",
            title = "Operating Systems",
            instructor = "Prof. Khalid Rehman",
            dayOfWeek = "Monday",
            startTime = "11:00",
            endTime = "12:30",
            room = "Main Hall (A-101)",
            credits = 3,
            color = 0xFF42A5F5
        ),
        CourseEntity(
            id = "MATH201",
            code = "MTH-201",
            title = "Linear Algebra & Calculus",
            instructor = "Dr. Nadia Hussain",
            dayOfWeek = "Tuesday",
            startTime = "08:00",
            endTime = "09:30",
            room = "Math Block (M-105)",
            credits = 4,
            color = 0xFF66BB6A
        ),
        CourseEntity(
            id = "CS310",
            code = "CS-310",
            title = "Database Management Systems",
            instructor = "Dr. Farhan Iqbal",
            dayOfWeek = "Tuesday",
            startTime = "10:00",
            endTime = "11:30",
            room = "DB Lab (B-301)",
            credits = 3,
            color = 0xFFEF5350
        ),
        CourseEntity(
            id = "SE401",
            code = "SE-401",
            title = "Software Engineering",
            instructor = "Prof. Amina Malik",
            dayOfWeek = "Wednesday",
            startTime = "13:00",
            endTime = "14:30",
            room = "Seminar Hall (S-02)",
            credits = 3,
            color = 0xFFFF7043
        ),
        CourseEntity(
            id = "CS450",
            code = "CS-450",
            title = "Artificial Intelligence",
            instructor = "Dr. Usman Tariq",
            dayOfWeek = "Thursday",
            startTime = "09:00",
            endTime = "10:30",
            room = "AI Lab (B-205)",
            credits = 3,
            color = 0xFFAB47BC
        ),
        CourseEntity(
            id = "CS490",
            code = "CS-490",
            title = "Final Year Project",
            instructor = "Dr. Zara Khan",
            dayOfWeek = "Friday",
            startTime = "14:00",
            endTime = "16:00",
            room = "Project Lab (FYP-01)",
            credits = 6,
            color = 0xFF26A69A
        )
    )

    private val mockAnnouncements = listOf(
        AnnouncementEntity(
            id = "ann1",
            courseCode = "CS-301",
            courseTitle = "Data Structures & Algorithms",
            title = "Assignment 3 Deadline Extended",
            body = "The deadline for Assignment 3 (Balanced BSTs) has been extended to next Friday. Please submit via the LMS portal. Late submissions will not be accepted after the new deadline.",
            postedAt = System.currentTimeMillis() - 2 * 60 * 60 * 1000,
            priority = "HIGH"
        ),
        AnnouncementEntity(
            id = "ann2",
            courseCode = "SE-401",
            courseTitle = "Software Engineering",
            title = "Guest Lecture: Industry Expert Visit",
            body = "We're pleased to announce a guest lecture by Mr. Bilal Sheikh, Senior Engineer at Systems Limited, on Wednesday at 2 PM. Attendance is mandatory. Topic: Agile in Practice.",
            postedAt = System.currentTimeMillis() - 5 * 60 * 60 * 1000,
            priority = "HIGH"
        ),
        AnnouncementEntity(
            id = "ann3",
            courseCode = "MTH-201",
            courseTitle = "Linear Algebra & Calculus",
            title = "Mid-term Exam Schedule",
            body = "Midterm exams for MTH-201 are scheduled for next Tuesday, 8:00–10:00 AM. The exam will cover Chapters 1–5: Matrices, Determinants, Eigenvalues, and Integration techniques. Bring your student ID.",
            postedAt = System.currentTimeMillis() - 24 * 60 * 60 * 1000,
            priority = "HIGH"
        ),
        AnnouncementEntity(
            id = "ann4",
            courseCode = "CS-405",
            courseTitle = "Operating Systems",
            title = "Lab Session Rescheduled",
            body = "Tuesday's lab session has been moved to Thursday, 3:00 PM in CS Lab 1 (B-203) due to the faculty seminar. Please update your schedules accordingly.",
            postedAt = System.currentTimeMillis() - 30 * 60 * 60 * 1000,
            priority = "NORMAL"
        ),
        AnnouncementEntity(
            id = "ann5",
            courseCode = "CS-450",
            courseTitle = "Artificial Intelligence",
            title = "Project Phase 1 Presentations",
            body = "Phase 1 presentations for AI projects will be held next Thursday. Each group has 10 minutes. Upload your slides 24 hours before to the shared drive link on LMS.",
            postedAt = System.currentTimeMillis() - 48 * 60 * 60 * 1000,
            priority = "NORMAL"
        ),
        AnnouncementEntity(
            id = "ann6",
            courseCode = "CS-490",
            courseTitle = "Final Year Project",
            title = "FYP Supervisor Meeting Reminder",
            body = "Weekly supervisor meetings resume this Friday. Please prepare a progress report covering the last two weeks. Those who haven't submitted their titles, please do so immediately.",
            postedAt = System.currentTimeMillis() - 72 * 60 * 60 * 1000,
            priority = "NORMAL"
        ),
        AnnouncementEntity(
            id = "ann7",
            courseCode = "CS-310",
            courseTitle = "Database Management Systems",
            title = "SQL Quiz Tomorrow",
            body = "Quick reminder: There's a 20-minute quiz on SQL Joins and Transactions tomorrow at the start of class. Open book—but closed laptop. Be on time.",
            postedAt = System.currentTimeMillis() - 3 * 60 * 60 * 1000,
            priority = "HIGH"
        )
    )

    suspend fun fetchCourses(): List<CourseEntity> {
        delay(1500) // Simulate network latency
        return mockCourses
    }

    suspend fun fetchAnnouncements(): List<AnnouncementEntity> {
        delay(1200) // Simulate network latency
        // Return fresh copies with new timestamps to simulate "updated" data
        return mockAnnouncements.mapIndexed { index, a ->
            if (index < 3) a.copy(isRead = false) else a
        }
    }
}
