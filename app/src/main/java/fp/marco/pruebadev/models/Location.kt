package fp.marco.pruebadev.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import java.util.*


data class Location (
    val latitude: Double,
    val longitude: Double,
    val dateTime: Date
)

data class Movies(
    var results: List<Movie>
)

@Entity
data class Movie (
    @Expose(serialize = false) @PrimaryKey(autoGenerate = true) val id: Int,
    val title: String,
    val backdrop_path: String,
    val release_date: String
)