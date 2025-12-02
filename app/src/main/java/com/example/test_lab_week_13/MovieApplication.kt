package com.example.test_lab_week_13

import android.app.Application
import androidx.work.*
import com.example.test_lab_week_13.api.MovieService
import com.example.test_lab_week_13.database.MovieDatabase
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class MovieApplication : Application() {

    lateinit var movieRepository: MovieRepository

    override fun onCreate() {
        super.onCreate()

        // create a Retrofit instance
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        // create a MovieService instance
        val movieService = retrofit.create(MovieService::class.java)

        // create Room database
        val movieDatabase = MovieDatabase.getInstance(applicationContext)

        // create repository
        movieRepository = MovieRepository(movieService, movieDatabase)

        // ================= WORK MANAGER SETUP =================

        val constraints = Constraints.Builder()
            // only run when connected to internet
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = PeriodicWorkRequest.Builder(
            MovieWorker::class.java,
            1,
            TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .addTag("movie-work")
            .build()

        WorkManager.getInstance(applicationContext)
            .enqueueUniquePeriodicWork(
                "movie-work",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
    }
}
