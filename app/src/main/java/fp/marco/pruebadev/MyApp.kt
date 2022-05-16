package fp.marco.pruebadev

import android.app.Application
import androidx.room.Room
import com.google.firebase.firestore.FirebaseFirestore
import fp.marco.pruebadev.room.MoviesDB


class MyApp : Application() {

    public lateinit var room: MoviesDB

    companion object {
        lateinit var instance: MyApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        room = Room
            .databaseBuilder(instance,MoviesDB::class.java,"movies")
            .build()
    }



}