package lol.dutchu.myweather.data.remote

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface LocationApi {

    //https://api.openweathermap.com/data/2.5/find?&q=san%20jose&type=like&sort=population&cnt=30&appid=439d4b804bc8187953eb36d2a8c26a02&_=1740760268845
//https://api.openweathermap.com/geo/1.0/direct?q=Lodz&limit=1&appid=a012be0ced4257708497fd0115e8c694
    @GET("data/2.5/find")
    suspend fun fetchLocation(
        @Query("q") city: String,
        @Query("type") type: String = "like",
        @Query("sort") sort: String = "population",
        @Query("cnt") cnt: Int = 30,
        @Query("limit") limit: Int = 5,
        @Query("appid") apiKey: String = "a012be0ced4257708497fd0115e8c694"
    ) : LocationsResponseDto
}
//https://api.openweathermap.com/data/2.5/find?q=Lodz&type=like&sort=population&cnt=30&appid=a012be0ced4257708497fd0115e8c694