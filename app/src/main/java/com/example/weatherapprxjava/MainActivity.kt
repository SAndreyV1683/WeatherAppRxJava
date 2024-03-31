package com.example.weatherapprxjava

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.weatherapprxjava.data.network.ForecaRepository

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Создали репозиторий и вызвали нужный метод
        val forecaRepository = ForecaRepository()
        forecaRepository.getCurrentWeather()
    }
}