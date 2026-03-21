package com.example.endsem.repository

import com.example.endsem.data.local.RentalDao
import com.example.endsem.data.local.WishlistDao
import com.example.endsem.data.model.Movie
import com.example.endsem.data.model.Rental
import com.example.endsem.data.model.WishlistItem
import com.example.endsem.data.remote.MockMovieData
import com.example.endsem.data.remote.MovieApiService
import kotlinx.coroutines.flow.Flow

/**
 * Repository that handles both API and database interactions
 * Following MVVM architecture pattern
 */
class MovieRepository(
    private val movieApiService: MovieApiService,
    private val rentalDao: RentalDao,
    private val wishlistDao: WishlistDao
) {

    // ==================== API Operations ====================

    suspend fun getMovies(): List<Movie> {
        return try {
            val response = movieApiService.getMovies()
            response.movies ?: MockMovieData.movies
        } catch (e: Exception) {
            MockMovieData.movies
        }
    }

    // ==================== Rental Operations ====================

    suspend fun rentMovie(movie: Movie, days: Int = 1) {
        val rental = Rental(
            movieId = movie.id,
            title = movie.title,
            posterUrl = movie.posterUrl,
            rating = movie.rating,
            days = days,
            pricePerDay = movie.rentalPricePerDay,
            overview = movie.overview,
            rentalStartTime = System.currentTimeMillis()
        )
        rentalDao.insertRental(rental)
    }

    fun getAllRentals(): Flow<List<Rental>> = rentalDao.getAllRentals()

    suspend fun getAllRentalsList(): List<Rental> = rentalDao.getAllRentalsList()

    suspend fun isMovieRented(movieId: Int): Boolean = rentalDao.isMovieRented(movieId)

    suspend fun increaseRentalDays(rental: Rental) {
        rentalDao.updateRentalDays(rental.id, rental.days + 1)
    }

    suspend fun decreaseRentalDays(rental: Rental) {
        if (rental.days > 1) {
            rentalDao.updateRentalDays(rental.id, rental.days - 1)
        }
    }

    suspend fun deleteRental(rental: Rental) = rentalDao.deleteRental(rental)

    suspend fun getRentalCount(): Int = rentalDao.getRentalCount()

    // ==================== Wishlist Operations ====================

    suspend fun addToWishlist(movie: Movie) {
        val item = WishlistItem(
            movieId = movie.id,
            title = movie.title,
            posterUrl = movie.posterUrl,
            overview = movie.overview,
            rating = movie.rating,
            genre = movie.genre,
            trailerUrl = movie.trailerUrl,
            pricePerDay = movie.rentalPricePerDay
        )
        wishlistDao.addToWishlist(item)
    }

    fun getAllWishlistItems(): Flow<List<WishlistItem>> = wishlistDao.getAllWishlistItems()

    suspend fun removeFromWishlist(movieId: Int) = wishlistDao.removeByMovieId(movieId)

    suspend fun isInWishlist(movieId: Int): Boolean = wishlistDao.isInWishlist(movieId)
}
