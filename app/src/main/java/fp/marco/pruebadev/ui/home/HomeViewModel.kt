package fp.marco.pruebadev.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fp.marco.pruebadev.models.Movies
import fp.marco.pruebadev.retrofit.APIService
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeViewModel : ViewModel() {

    val baseURL = "https://api.themoviedb.org/3/movie/"
    val apiKey = "629004a9cb04bdec673326485dd071c8"
    val movies = MutableLiveData<Movies>()

    val connectionError = MutableLiveData<Boolean>()

    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        connectionError.postValue(true)
    }

    fun getMovies()
    {
        try {
            viewModelScope.launch(exceptionHandler) {
                val call = getRetrofit().create(APIService::class.java).getAllMovies("${baseURL}now_playing?api_key=$apiKey")
                if (call.isSuccessful)
                {
                    movies.postValue(call.body())
                }
                else connectionError.postValue(true)

            }
        }catch (e: Exception){
            connectionError.postValue(true)

        }

    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

}