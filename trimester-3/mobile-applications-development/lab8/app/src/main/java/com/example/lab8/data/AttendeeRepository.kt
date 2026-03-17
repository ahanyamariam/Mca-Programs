package com.example.lab8.data

import kotlinx.coroutines.flow.Flow

class AttendeeRepository(private val attendeeDao: AttendeeDao) {
    val allAttendees: Flow<List<Attendee>> = attendeeDao.getAllAttendees()

    suspend fun insert(attendee: Attendee) {
        attendeeDao.insertAttendee(attendee)
    }

    suspend fun update(attendee: Attendee) {
        attendeeDao.updateAttendee(attendee)
    }

    suspend fun delete(attendee: Attendee) {
        attendeeDao.deleteAttendee(attendee)
    }
}
