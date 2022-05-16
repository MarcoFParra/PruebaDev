package fp.marco.pruebadev.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import fp.marco.pruebadev.MainActivity
import fp.marco.pruebadev.R
import fp.marco.pruebadev.databinding.FragmentMapsBinding

class DashboardFragment : Fragment(), GoogleMap.OnMarkerClickListener {

    private val callback = OnMapReadyCallback { googleMap ->
        val markers: MutableList<Marker> = ArrayList()
        (activity as MainActivity).dbFirebaseFirestore.collection("locations")
            .get()
            .addOnCompleteListener{
                if (it.isSuccessful()) {
                    for (document in it.getResult()) {

                        Log.d("FireBase", "${document.id} => ${document.data}|||||| ${document.data.get("latitue")}" +
                                "${document.data.get("longitud")}")
                        googleMap.addMarker(
                            MarkerOptions()
                                .position(LatLng(document.data.get("latitue").toString().toDouble() ,document.data.get("longitud").toString().toDouble()  ))
                                .title("Posicion del dia: ${document.data.get("date").toString()}")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        )?.let { it1 -> markers.add(it1) }
                    }
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markers.last().position, 20.0f))
                    showMarkerInfo(markers.last())
                    markers.last().showInfoWindow()


                } else {
                    Toast.makeText(requireActivity(), "error al obtener las localizaciones", Toast.LENGTH_SHORT).show();
                }
            }
    }

    private var _binding: FragmentMapsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        showMarkerInfo(p0)
        return true
    }

    private fun showMarkerInfo(p0: Marker)
    {
        binding.tvLatitud.setText("Latitud: ${p0.position.latitude.toString()}")
        binding.tvLongitud.setText("Longitud: ${p0.position.latitude.toString()}")
        binding.tvDate.setText(p0.title)

    }
}