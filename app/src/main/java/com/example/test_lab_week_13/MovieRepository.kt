package com.example.test_lab_week_13

import com.example.test_lab_week_13.api.MovieService
import com.example.test_lab_week_13.database.MovieDatabase
import com.example.test_lab_week_13.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class MovieRepository(
    private val movieService: MovieService,
    private val movieDatabase: MovieDatabase
) {

    private val apiKey = "db6f6cfeb2e5b6bd4b5f8728c3f76d7b"

    fun fetchMovies(): Flow<List<Movie>> {
        return flow {
            // ACCESS DAO
            val movieDao = movieDatabase.movieDao()

            // Load movies from DB first
            val savedMovies = movieDao.getMovies()

            if (savedMovies.isEmpty()) {
                // Fetch from API
                val movies = movieService.getPopularMovies(apiKey).results

                // Save to Room DB
                movieDao.addMovies(movies)

                // Emit from API
                emit(movies)
            } else {
                // Emit cached DB movies
                emit(savedMovies)
            }

        }.flowOn(Dispatchers.IO)
    }
}
