package fp.marco.pruebadev

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import fp.marco.pruebadev.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), LocationListener {

    var dbFirebaseFirestore = FirebaseFirestore.getInstance()

    companion object
    {
        var fireLocation : HashMap<String, String>
                = HashMap<String, String> ()
    }

    private lateinit var binding: ActivityMainBinding
    private var CHANNEL_ID: String = ""

    var locationManager: LocationManager? = null
    private val GPS_TIME_INTERVAL: Long = 1000 * 60 * 5
    private val GPS_DISTANCE = 1000f
    private val HANDLER_DELAY: Long = 1000 * 60 * 5
    private val START_HANDLER_DELAY : Long = 0


    val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )


    val PERMISSION_ALL = 1

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        navView.setupWithNavController(navController)

        dbFirebaseFirestore = FirebaseFirestore.getInstance()



        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(PERMISSIONS, PERMISSION_ALL);
        }


        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                requestLocation()
                handler.postDelayed(this, HANDLER_DELAY)
            }
        }, START_HANDLER_DELAY)

    }


    private fun requestLocation() {
        if (locationManager == null) locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_TIME_INTERVAL, GPS_DISTANCE, this)
            return
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(PERMISSIONS, PERMISSION_ALL)
        }

    }

    override fun onLocationChanged(location: Location) {
        Log.d("mylog", "Got Location: " + location.getLatitude() + ", " + location.getLongitude());
        Toast.makeText(this, "Got Coordinates: " + location.getLatitude() + ", " + location.getLongitude(), Toast.LENGTH_SHORT).show();
        val b = NotificationCompat.Builder(this,CHANNEL_ID)

        b.setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setTicker("Hearty365")
            .setContentTitle("Guardando ubicacion")
            .setContentText("Almacenandola la ubicacion ${location.getLatitude()} ${location.getLongitude()} en firebase")
            .setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND)
            .setContentInfo("Info")

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        notificationManager!!.notify(1, b.build())

        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())

        fireLocation.put("latitue", location.latitude.toString())
        fireLocation.put("longitud", location.longitude.toString())
        fireLocation.put("date", currentDate)

        dbFirebaseFirestore.collection("locations")
            .add(fireLocation)
            .addOnSuccessListener {
                Toast.makeText(this, "DocumentSnapshot added to fireStore with ID: " + it.getId(), Toast.LENGTH_SHORT).show();
            }
            .addOnFailureListener{
                Toast.makeText(this, "DocumentSnapshot fail to added to fireStore", Toast.LENGTH_SHORT).show();
            }

        locationManager?.removeUpdates(this);
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions!!, grantResults)
        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val handler = Handler()
            handler.postDelayed(object : Runnable {
                override fun run() {
                    requestLocation()
                    handler.postDelayed(this, HANDLER_DELAY)
                }
            }, START_HANDLER_DELAY)
        } else {
            Toast.makeText(this, "La Localizacion es requerida", Toast.LENGTH_SHORT).show();


        }
    }
}