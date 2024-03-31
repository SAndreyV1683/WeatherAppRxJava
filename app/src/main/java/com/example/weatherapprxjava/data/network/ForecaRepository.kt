package com.example.weatherapprxjava.data.network

import android.annotation.SuppressLint
import android.util.Log
import com.example.weatherapprxjava.data.api.ForecaApi
import com.example.weatherapprxjava.data.dto.ForecaAuthRequest
import com.example.weatherapprxjava.data.dto.ForecaAuthResponse
import com.example.weatherapprxjava.data.dto.LocationsResponse
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ForecaRepository {

    private companion object {
        const val BASE_URL = "https://fnw-us.foreca.com"

        const val USER = "sav1683-as"
        const val PASSWORD = "p6hm84prnikj"
        const val HARDCODED_LOCATION = "Barcelona"
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()

    private val forecaService = retrofit.create(ForecaApi::class.java)

    private var token = ""

    @SuppressLint("CheckResult")
    fun getCurrentWeather() {
        forecaService.authenticate(ForecaAuthRequest(USER, PASSWORD))
            .flatMap { tokenResponse ->
                // Конвертируем полученный accessToken в новый запрос
                token = tokenResponse.token

                // Переключаемся на следующий сетевой запрос
                val bearerToken = "Bearer ${tokenResponse.token}"
                forecaService.getLocations(bearerToken, HARDCODED_LOCATION)
                    // Добавляем конвертацию результата в Pair,
                    // чтобы пробросить и результат, и access token
                    // дальше по цепочке
                    .map { Pair(it.locations, bearerToken) }
            }
            .flatMap { pairLocationsAndToken ->
                // Получаем данные из Pair
                val (locations, bearerToken) = pairLocationsAndToken
                // Опускаем обработку кейса с отсутствием локаций
                val firstLocation = locations.first()

                // Делаем запрос на текущую погоду
                forecaService.getForecast(bearerToken, firstLocation.id)
            }
            .retry { count, throwable ->
                count < 3 && throwable is HttpException && throwable.code() == 401
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { forecastResponse ->
                    // В итоговый subscribe теперь приходит прогноз
                    Log.d("RxJava", "Current forecast: ${forecastResponse.current}")
                },
                { error -> Log.e("RxJava", "Got error with auth or locations, or forecast", error) }
            )
    }



}