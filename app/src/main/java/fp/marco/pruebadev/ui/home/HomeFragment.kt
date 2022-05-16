package fp.marco.pruebadev.ui.home

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import fp.marco.pruebadev.MyApp
import fp.marco.pruebadev.databinding.FragmentHomeBinding
import fp.marco.pruebadev.models.Movie
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var adapter: MoviesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initObservables(homeViewModel)
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    fun populateRecycler(movies: List<Movie>)
    {
        binding.etSearch.isEnabled = true
        if (binding.viewSwitch.displayedChild == 1)
            binding.viewSwitch.showPrevious()
        adapter = MoviesAdapter(movies)
        binding.rvPlaces.layoutManager = LinearLayoutManager(context)
        binding.rvPlaces.adapter = adapter
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun afterTextChanged(p0: Editable?) {
                adapter.filter.filter(p0.toString())

            }

        })
    }
    fun initObservables(homeViewModel: HomeViewModel)
    {
        lifecycleScope.launch {

            MyApp.instance.room.placeDao().getAll().observe(viewLifecycleOwner) {
                if (it != null) {
                    if (it.isEmpty()) {
                        homeViewModel.getMovies()
                    }
                    else populateRecycler(it)
                }
            }
        }
        homeViewModel.movies.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it.results.isNotEmpty())
                {
                    populateRecycler(it.results)
                    lifecycleScope.launch {
                        MyApp.instance.room.placeDao().insert(it.results)
                    }
                }

            }
        }
        homeViewModel.connectionError.observe(viewLifecycleOwner) {
            if (it)
            {
                binding.viewSwitch.showNext()
                binding.vwNoInternet.root.setOnClickListener {
                    initObservables(homeViewModel)
                }
                /*
                AlertDialog.Builder(requireContext())
                    .setTitle("Error de conexion")
                    .setMessage("Ocurrio un error en la conexión, verifica tu conexion e intentalo mas tarde") // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.ok,
                        DialogInterface.OnClickListener { dialog, which ->
                            dialog.dismiss()
                        }) // A null listener allows the button to dismiss the dialog and take no further action.
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()

                 */

            }
        }
    }
}