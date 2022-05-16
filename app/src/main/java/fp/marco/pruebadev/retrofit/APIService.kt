package fp.marco.pruebadev.retrofit

import fp.marco.pruebadev.models.Movie
import fp.marco.pruebadev.models.Movies
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface APIService {
    @GET()
    suspend fun getAllMovies(@Url url:String): Response<Movies>

}