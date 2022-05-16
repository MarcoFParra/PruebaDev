package fp.marco.pruebadev.room

import androidx.room.Database
import androidx.room.RoomDatabase
import fp.marco.pruebadev.models.Movie

@Database(
    entities = [Movie::class],
    version = 1
)

abstract class MoviesDB : RoomDatabase() {
    abstract fun placeDao() : MovieDao
}