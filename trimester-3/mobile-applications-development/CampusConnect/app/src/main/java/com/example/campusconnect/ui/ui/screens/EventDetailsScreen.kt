package com.example.campusconnect.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class Event(
    val title: String,
    val date: String,
    val time: String,
    val venue: String,
    val description: String,
    val organizer: String,
    val capacity: Int,
    val registered: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsScreen(
    departmentName: String,
    onBackClick: () -> Unit
) {
    // Sample events based on department
    val event = when (departmentName) {
        "Computer Science" -> Event(
            title = "Revelations",
            date = "February 30th, 2026",
            time = "9:00 AM - 12:00 PM",
            venue = "Audi Block, Auditorium",
            description = "Join us for the annual Tech Fest by MCA REVELATIONS featuring coding competitions, " +
                    "hackathons, tech talks by industry experts, project exhibitions, and " +
                    "networking opportunities. This year's theme is 'AI for Social Good'. " +
                    "Prizes worth \$10,000 to be won!",
            organizer = "CS Department",
            capacity = 500,
            registered = 342
        )
        "Electronics" -> Event(
            title = "Robotics Workshop",
            date = "February 20, 2026",
            time = "2:00 PM - 5:00 PM",
            venue = "Electronics Lab, Block B",
            description = "Hands-on workshop on building autonomous robots using Arduino and " +
                    "Raspberry Pi. Learn about sensors, actuators, and programming robots. " +
                    "All materials will be provided. Perfect for beginners and intermediate learners.",
            organizer = "Electronics Club",
            capacity = 80,
            registered = 65
        )
        "Mechanical" -> Event(
            title = "Industry Visit - Tesla Factory",
            date = "March 1, 2026",
            time = "8:00 AM - 6:00 PM",
            venue = "Tesla Factory, Fremont",
            description = "Exclusive guided tour of Tesla's manufacturing facility. Witness " +
                    "state-of-the-art manufacturing processes, meet engineers, and learn about " +
                    "sustainable automotive technology. Transportation provided.",
            organizer = "Mechanical Department",
            capacity = 40,
            registered = 38
        )
        "Civil" -> Event(
            title = "Sustainable Architecture Seminar",
            date = "February 25, 2026",
            time = "10:00 AM - 1:00 PM",
            venue = "Civil Engineering Block",
            description = "Learn about green building technologies, sustainable design principles, " +
                    "and eco-friendly construction materials. Guest speakers from leading architecture " +
                    "firms will share their experiences.",
            organizer = "Civil Engineering Society",
            capacity = 150,
            registered = 98
        )
        "Mathematics" -> Event(
            title = "Math Olympiad 2026",
            date = "March 10, 2026",
            time = "9:00 AM - 12:00 PM",
            venue = "Mathematics Department",
            description = "Annual mathematics competition featuring challenging problems in " +
                    "algebra, geometry, number theory, and combinatorics. Open to all students. " +
                    "Prizes for top performers.",
            organizer = "Math Club",
            capacity = 200,
            registered = 156
        )
        "Physics" -> Event(
            title = "Quantum Computing Lecture",
            date = "February 28, 2026",
            time = "3:00 PM - 5:00 PM",
            venue = "Physics Auditorium",
            description = "Distinguished lecture by Dr. Sarah Chen on the fundamentals of " +
                    "quantum computing and its future applications. Q&A session included.",
            organizer = "Physics Department",
            capacity = 250,
            registered = 189
        )
        "Chemistry" -> Event(
            title = "Chemistry Lab Safety Workshop",
            date = "February 22, 2026",
            time = "11:00 AM - 2:00 PM",
            venue = "Chemistry Lab Complex",
            description = "Mandatory workshop for all chemistry students covering laboratory " +
                    "safety protocols, handling hazardous materials, and emergency procedures.",
            organizer = "Chemistry Department",
            capacity = 120,
            registered = 112
        )
        "Business" -> Event(
            title = "Entrepreneurship Summit",
            date = "March 5, 2026",
            time = "9:00 AM - 5:00 PM",
            venue = "Business School Auditorium",
            description = "Meet successful entrepreneurs, participate in pitch competitions, " +
                    "attend workshops on startup funding, and network with investors. " +
                    "Best business idea wins \$5,000 seed funding.",
            organizer = "Business School",
            capacity = 300,
            registered = 267
        )
        else -> Event(
            title = "General Campus Event",
            date = "TBA",
            time = "TBA",
            venue = "Main Campus",
            description = "Event details will be announced soon. Stay tuned!",
            organizer = departmentName,
            capacity = 100,
            registered = 0
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Event Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Event Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    // Department Badge
                    AssistChip(
                        onClick = { },
                        label = { Text(departmentName) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.AccountBalance,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Organized by ${event.organizer}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }

            // Event Details
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Date & Time Card
                EventInfoCard(
                    icon = Icons.Default.CalendarToday,
                    title = "Date & Time",
                    value = "${event.date}\n${event.time}"
                )

                // Venue Card
                EventInfoCard(
                    icon = Icons.Default.LocationOn,
                    title = "Venue",
                    value = event.venue
                )

                // Capacity Card
                EventInfoCard(
                    icon = Icons.Default.Group,
                    title = "Registration",
                    value = "${event.registered} / ${event.capacity} registered"
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Registration Progress
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Registration Progress",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${(event.registered.toFloat() / event.capacity * 100).toInt()}%",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = event.registered.toFloat() / event.capacity,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Description Section
                Text(
                    text = "About this Event",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = event.description,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp),
                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = event.registered < event.capacity
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (event.registered < event.capacity) "Register Now" else "Event Full"
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Share, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share Event")
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add to Calendar")
                }
            }
        }
    }
}

@Composable
fun EventInfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}