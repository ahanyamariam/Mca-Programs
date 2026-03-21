package com.example.endsem.viewmodel

import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.endsem.MainActivity
import com.example.endsem.MovieExplorerApp
import com.example.endsem.R
import com.example.endsem.data.local.UserPreferences
import com.example.endsem.data.model.Movie
import com.example.endsem.data.model.Rental
import com.example.endsem.data.model.WishlistItem
import com.example.endsem.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * UI State for Movies screen
 */
data class MoviesUiState(
    val movies: List<Movie> = emptyList(),
    val filteredMovies: List<Movie> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val cartMovieIds: Set<Int> = emptySet(),
    val rentedMovieIds: Set<Int> = emptySet(),
    val wishlistMovieIds: Set<Int> = emptySet(),
    val searchQuery: String = "",
    val selectedGenre: String? = null,
    val minRating: Double = 0.0,
    val availableGenres: List<String> = emptyList()
)

/**
 * UI State for Cart screen
 */
data class CartUiState(
    val cartItems: List<Movie> = emptyList(),
    val totalPrice: Double = 0.0
)

/**
 * UI State for Rentals screen
 */
data class RentalsUiState(
    val rentals: List<Rental> = emptyList(),
    val totalPrice: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * UI State for Wishlist screen
 */
data class WishlistUiState(
    val items: List<WishlistItem> = emptyList()
)

/**
 * UI State for Profile screen
 */
data class ProfileUiState(
    val name: String = "",
    val email: String = "",
    val isDarkMode: Boolean = false
)

/**
 * ViewModel for Movie Explorer app
 */
class MovieViewModel(
    private val repository: MovieRepository,
    private val application: Application
) : ViewModel() {

    private val userPreferences = UserPreferences(application)

    private val _moviesUiState = MutableStateFlow(MoviesUiState())
    val moviesUiState: StateFlow<MoviesUiState> = _moviesUiState.asStateFlow()

    private val _cartUiState = MutableStateFlow(CartUiState())
    val cartUiState: StateFlow<CartUiState> = _cartUiState.asStateFlow()

    private val _rentalsUiState = MutableStateFlow(RentalsUiState())
    val rentalsUiState: StateFlow<RentalsUiState> = _rentalsUiState.asStateFlow()

    private val _wishlistUiState = MutableStateFlow(WishlistUiState())
    val wishlistUiState: StateFlow<WishlistUiState> = _wishlistUiState.asStateFlow()

    private val _profileUiState = MutableStateFlow(ProfileUiState())
    val profileUiState: StateFlow<ProfileUiState> = _profileUiState.asStateFlow()

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    init {
        loadMovies()
        observeRentals()
        observeWishlist()
        observePreferences()
    }

    // ==================== Preferences ====================

    private fun observePreferences() {
        viewModelScope.launch {
            userPreferences.isDarkMode.collect { dark ->
                _isDarkMode.value = dark
                _profileUiState.value = _profileUiState.value.copy(isDarkMode = dark)
            }
        }
        viewModelScope.launch {
            userPreferences.userName.collect { name ->
                _profileUiState.value = _profileUiState.value.copy(name = name)
            }
        }
        viewModelScope.launch {
            userPreferences.userEmail.collect { email ->
                _profileUiState.value = _profileUiState.value.copy(email = email)
            }
        }
    }

    fun updateUserName(name: String) {
        viewModelScope.launch { userPreferences.setUserName(name) }
    }

    fun updateUserEmail(email: String) {
        viewModelScope.launch { userPreferences.setUserEmail(email) }
    }

    fun toggleDarkMode() {
        viewModelScope.launch {
            userPreferences.setDarkMode(!_isDarkMode.value)
        }
    }

    // ==================== Movies & Search/Filter ====================

    fun loadMovies() {
        viewModelScope.launch {
            _moviesUiState.value = _moviesUiState.value.copy(isLoading = true, error = null)
            try {
                val movies = repository.getMovies()
                val genres = movies.map { it.genre }.distinct().sorted()
                _moviesUiState.value = _moviesUiState.value.copy(
                    movies = movies,
                    isLoading = false,
                    availableGenres = genres
                )
                applyFilters()
                updateRentedMovieIds()
            } catch (e: Exception) {
                _moviesUiState.value = _moviesUiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load movies"
                )
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _moviesUiState.value = _moviesUiState.value.copy(searchQuery = query)
        applyFilters()
    }

    fun selectGenre(genre: String?) {
        _moviesUiState.value = _moviesUiState.value.copy(
            selectedGenre = if (_moviesUiState.value.selectedGenre == genre) null else genre
        )
        applyFilters()
    }

    fun setMinRating(rating: Double) {
        _moviesUiState.value = _moviesUiState.value.copy(minRating = rating)
        applyFilters()
    }

    private fun applyFilters() {
        val state = _moviesUiState.value
        var filtered = state.movies

        if (state.searchQuery.isNotBlank()) {
            filtered = filtered.filter {
                it.title.contains(state.searchQuery, ignoreCase = true)
            }
        }
        if (state.selectedGenre != null) {
            filtered = filtered.filter { it.genre == state.selectedGenre }
        }
        if (state.minRating > 0) {
            filtered = filtered.filter { it.rating >= state.minRating }
        }

        _moviesUiState.value = state.copy(filteredMovies = filtered)
    }

    private fun observeRentals() {
        viewModelScope.launch {
            repository.getAllRentals()
                .catch { e ->
                    _rentalsUiState.value = _rentalsUiState.value.copy(
                        error = e.message ?: "Failed to load rentals"
                    )
                }
                .collect { rentals ->
                    _rentalsUiState.value = RentalsUiState(
                        rentals = rentals,
                        totalPrice = rentals.sumOf { it.totalPrice },
                        isLoading = false
                    )
                    _moviesUiState.value = _moviesUiState.value.copy(
                        rentedMovieIds = rentals.map { it.movieId }.toSet()
                    )
                    applyFilters()
                }
        }
    }

    private fun updateRentedMovieIds() {
        viewModelScope.launch {
            val rentals = repository.getAllRentalsList()
            _moviesUiState.value = _moviesUiState.value.copy(
                rentedMovieIds = rentals.map { it.movieId }.toSet()
            )
            applyFilters()
        }
    }

    // ==================== Wishlist ====================

    private fun observeWishlist() {
        viewModelScope.launch {
            repository.getAllWishlistItems()
                .catch { /* ignore */ }
                .collect { items ->
                    _wishlistUiState.value = WishlistUiState(items = items)
                    _moviesUiState.value = _moviesUiState.value.copy(
                        wishlistMovieIds = items.map { it.movieId }.toSet()
                    )
                    applyFilters()
                }
        }
    }

    fun toggleWishlist(movie: Movie) {
        viewModelScope.launch {
            val isInWishlist = repository.isInWishlist(movie.id)
            if (isInWishlist) {
                repository.removeFromWishlist(movie.id)
                _snackbarMessage.value = "${movie.title} removed from wishlist"
            } else {
                repository.addToWishlist(movie)
                _snackbarMessage.value = "${movie.title} added to wishlist"
            }
        }
    }

    fun removeFromWishlist(movieId: Int) {
        viewModelScope.launch {
            repository.removeFromWishlist(movieId)
            _snackbarMessage.value = "Removed from wishlist"
        }
    }

    // ==================== Cart ====================

    fun addToCart(movie: Movie) {
        val currentCart = _cartUiState.value.cartItems.toMutableList()
        if (currentCart.any { it.id == movie.id }) {
            _snackbarMessage.value = "${movie.title} is already in the cart"
            return
        }
        if (_moviesUiState.value.rentedMovieIds.contains(movie.id)) {
            _snackbarMessage.value = "${movie.title} is already rented"
            return
        }
        currentCart.add(movie)
        _cartUiState.value = CartUiState(
            cartItems = currentCart,
            totalPrice = currentCart.sumOf { it.rentalPricePerDay }
        )
        _moviesUiState.value = _moviesUiState.value.copy(
            cartMovieIds = currentCart.map { it.id }.toSet()
        )
        applyFilters()
        _snackbarMessage.value = "${movie.title} added to cart"
    }

    fun removeFromCart(movie: Movie) {
        val currentCart = _cartUiState.value.cartItems.toMutableList()
        currentCart.removeAll { it.id == movie.id }
        _cartUiState.value = CartUiState(
            cartItems = currentCart,
            totalPrice = currentCart.sumOf { it.rentalPricePerDay }
        )
        _moviesUiState.value = _moviesUiState.value.copy(
            cartMovieIds = currentCart.map { it.id }.toSet()
        )
        applyFilters()
        _snackbarMessage.value = "${movie.title} removed from cart"
    }

    fun purchaseCart() {
        val cartItems = _cartUiState.value.cartItems
        if (cartItems.isEmpty()) {
            _snackbarMessage.value = "Cart is empty"
            return
        }

        viewModelScope.launch {
            try {
                for (movie in cartItems) {
                    repository.rentMovie(movie)
                    showPurchaseNotification(movie)
                    showTimeLeftNotification(movie)
                }
                _cartUiState.value = CartUiState()
                _moviesUiState.value = _moviesUiState.value.copy(cartMovieIds = emptySet())
                applyFilters()
                updateRentedMovieIds()
                _snackbarMessage.value = if (cartItems.size == 1)
                    "${cartItems[0].title} purchased!"
                else
                    "${cartItems.size} movies purchased!"
            } catch (e: Exception) {
                _snackbarMessage.value = "Purchase failed: ${e.message}"
            }
        }
    }

    // ==================== Notifications ====================

    private fun showPurchaseNotification(movie: Movie) {
        if (!hasNotificationPermission()) return

        val notification = NotificationCompat.Builder(application, MovieExplorerApp.PURCHASE_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_movie_notification)
            .setContentTitle("Purchase Successful!")
            .setContentText("Your purchase for \"${movie.title}\" was successful")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(createAppPendingIntent(movie.id))
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(application).notify(movie.id + 2000, notification)
    }

    private fun showTimeLeftNotification(movie: Movie) {
        if (!hasNotificationPermission()) return

        val notification = NotificationCompat.Builder(application, MovieExplorerApp.RENTAL_REMINDER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_movie_notification)
            .setContentTitle("Rental Timer Started")
            .setContentText("You have 24 hours left for \"${movie.title}\"")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(createAppPendingIntent(movie.id + 100))
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(application).notify(movie.id + 3000, notification)
    }

    private fun hasNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(
                application, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        }
        return true
    }

    private fun createAppPendingIntent(requestCode: Int): PendingIntent {
        val intent = Intent(application, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent.getActivity(
            application, requestCode, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    // ==================== Rental Operations ====================

    fun increaseRentalDays(rental: Rental) {
        viewModelScope.launch {
            try { repository.increaseRentalDays(rental) }
            catch (e: Exception) { _snackbarMessage.value = "Failed: ${e.message}" }
        }
    }

    fun decreaseRentalDays(rental: Rental) {
        viewModelScope.launch {
            try {
                if (rental.days > 1) repository.decreaseRentalDays(rental)
                else _snackbarMessage.value = "Minimum rental period is 1 day"
            } catch (e: Exception) { _snackbarMessage.value = "Failed: ${e.message}" }
        }
    }

    fun removeRental(rental: Rental) {
        viewModelScope.launch {
            try {
                repository.deleteRental(rental)
                _snackbarMessage.value = "${rental.title} removed from rentals"
                updateRentedMovieIds()
            } catch (e: Exception) { _snackbarMessage.value = "Failed: ${e.message}" }
        }
    }

    fun clearSnackbarMessage() { _snackbarMessage.value = null }
}

class MovieViewModelFactory(
    private val repository: MovieRepository,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieViewModel::class.java)) {
            return MovieViewModel(repository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
