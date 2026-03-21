package com.example.endsem.data.remote

import com.example.endsem.data.model.Movie

/**
 * Mock movie data for testing/demo purposes
 * Used when the API is unavailable
 */
object MockMovieData {

    val movies = listOf(
        Movie(
            id = 1,
            title = "The Shawshank Redemption",
            posterUrl = "https://image.tmdb.org/t/p/w500/9cqNxx0GxF0bflZmeSMuL5tnGzr.jpg",
            overview = "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.",
            rating = 9.3,
            releaseDate = "1994-09-23",
            genre = "Drama",
            trailerUrl = "https://www.youtube.com/watch?v=PLl99DlL6b4"
        ),
        Movie(
            id = 2,
            title = "The Godfather",
            posterUrl = "https://image.tmdb.org/t/p/w500/3bhkrj58Vtu7enYsRolD1fZdja1.jpg",
            overview = "The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant youngest son.",
            rating = 9.2,
            releaseDate = "1972-03-14",
            genre = "Crime",
            trailerUrl = "https://www.youtube.com/watch?v=UaVTIH8mujA"
        ),
        Movie(
            id = 3,
            title = "The Dark Knight",
            posterUrl = "https://image.tmdb.org/t/p/w500/qJ2tW6WMUDux911r6m7haRef0WH.jpg",
            overview = "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.",
            rating = 9.0,
            releaseDate = "2008-07-18",
            genre = "Action",
            trailerUrl = "https://www.youtube.com/watch?v=EXeTwQWrcwY"
        ),
        Movie(
            id = 4,
            title = "Pulp Fiction",
            posterUrl = "https://image.tmdb.org/t/p/w500/d5iIlFn5s0ImszYzBPb8JPIfbXD.jpg",
            overview = "The lives of two mob hitmen, a boxer, a gangster and his wife, and a pair of diner bandits intertwine in four tales of violence and redemption.",
            rating = 8.9,
            releaseDate = "1994-10-14",
            genre = "Crime",
            trailerUrl = "https://www.youtube.com/watch?v=s7EdQ4FqbhY"
        ),
        Movie(
            id = 5,
            title = "Forrest Gump",
            posterUrl = "https://image.tmdb.org/t/p/w500/arw2vcBveWOVZr6pxd9XTd1TdQa.jpg",
            overview = "The presidencies of Kennedy and Johnson, the Vietnam War, the Watergate scandal and other historical events unfold from the perspective of an Alabama man with an IQ of 75.",
            rating = 8.8,
            releaseDate = "1994-07-06",
            genre = "Drama",
            trailerUrl = "https://www.youtube.com/watch?v=bLvqoHBptjg"
        ),
        Movie(
            id = 6,
            title = "Inception",
            posterUrl = "https://image.tmdb.org/t/p/w500/ljsZTbVsrQSqZgWeep2B1QiDKuh.jpg",
            overview = "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O.",
            rating = 8.8,
            releaseDate = "2010-07-16",
            genre = "Sci-Fi",
            trailerUrl = "https://www.youtube.com/watch?v=YoHD9XEInc0"
        ),
        Movie(
            id = 7,
            title = "The Matrix",
            posterUrl = "https://image.tmdb.org/t/p/w500/f89U3ADr1oiB1s9GkdPOEpXUk5H.jpg",
            overview = "A computer programmer discovers that reality as he knows it is a simulation created by machines, and joins a rebellion to break free.",
            rating = 8.7,
            releaseDate = "1999-03-31",
            genre = "Sci-Fi",
            trailerUrl = "https://www.youtube.com/watch?v=vKQi3bBA1y8"
        ),
        Movie(
            id = 8,
            title = "Interstellar",
            posterUrl = "https://image.tmdb.org/t/p/w500/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg",
            overview = "A team of explorers travel through a wormhole in space in an attempt to ensure humanity's survival.",
            rating = 8.6,
            releaseDate = "2014-11-07",
            genre = "Sci-Fi",
            trailerUrl = "https://www.youtube.com/watch?v=zSWdZVtXT7E"
        ),
        Movie(
            id = 9,
            title = "Parasite",
            posterUrl = "https://image.tmdb.org/t/p/w500/7IiTTgloJzvGI1TAYymCfbfl3vT.jpg",
            overview = "Greed and class discrimination threaten the newly formed symbiotic relationship between the wealthy Park family and the destitute Kim clan.",
            rating = 8.5,
            releaseDate = "2019-05-30",
            genre = "Thriller",
            trailerUrl = "https://www.youtube.com/watch?v=5xH0HfJHsaY"
        ),
        Movie(
            id = 10,
            title = "The Avengers",
            posterUrl = "https://image.tmdb.org/t/p/w500/RYMX2wcKCBAr24UyPD7xwmjaTn.jpg",
            overview = "Earth's mightiest heroes must come together and learn to fight as a team to stop Loki and his alien army from enslaving humanity.",
            rating = 8.0,
            releaseDate = "2012-05-04",
            genre = "Action",
            trailerUrl = "https://www.youtube.com/watch?v=eOrNdBpGMv8"
        ),
        Movie(
            id = 11,
            title = "Joker",
            posterUrl = "https://image.tmdb.org/t/p/w500/udDclJoHjfjb8Ekgsd4FDteOkCU.jpg",
            overview = "A mentally troubled stand-up comedian embarks on a downward spiral that leads to the creation of an iconic villain.",
            rating = 8.4,
            releaseDate = "2019-10-04",
            genre = "Thriller",
            trailerUrl = "https://www.youtube.com/watch?v=zAGVQLHvwOY"
        ),
        Movie(
            id = 12,
            title = "Spider-Man: No Way Home",
            posterUrl = "https://image.tmdb.org/t/p/w500/1g0dhYtq4irTY1GPXvft6k4YLjm.jpg",
            overview = "Peter Parker's secret identity is revealed to the entire world. Desperate for help, Peter turns to Doctor Strange to make the world forget.",
            rating = 8.3,
            releaseDate = "2021-12-17",
            genre = "Action",
            trailerUrl = "https://www.youtube.com/watch?v=JfVOs4VSpmA"
        )
    )
}
