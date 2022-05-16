package fp.marco.pruebadev.room

import androidx.lifecycle.LiveData
import androidx.room.*
import fp.marco.pruebadev.models.Movie

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(places: List<Movie>)

    @Query("DELETE from Movie")
    suspend fun removeAll()

    @Query("SELECT * from Movie")
    fun getAll(): LiveData<List<Movie>>

}