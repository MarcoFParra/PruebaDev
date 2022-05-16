package fp.marco.pruebadev.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import fp.marco.pruebadev.R
import fp.marco.pruebadev.databinding.LayoutItemMovieBinding
import fp.marco.pruebadev.models.Movie
import java.util.*


class MoviesAdapter(private val movies: List<Movie>): RecyclerView.Adapter<MovieViewHolder>(),
    Filterable {

    private var movieFilterList: List<Movie>
    init {
        movieFilterList = movies
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return MovieViewHolder(parent.context,layoutInflater.inflate(R.layout.layout_item_movie,parent,false))
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val item = movieFilterList[position]
        holder.bind(item);
    }

    override fun getItemCount(): Int = movieFilterList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    movieFilterList = movies
                } else {
                    val resultList = ArrayList<Movie>()
                    for (row in movies) {
                        if (row.title?.lowercase(Locale.ROOT)?.contains(charSearch.lowercase(Locale.ROOT)) == true) {
                            resultList.add(row)
                        }
                    }
                    movieFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = movieFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                movieFilterList = results?.values as ArrayList<Movie>
                notifyDataSetChanged()
            }

        }
    }

}

class MovieViewHolder(private var context: Context, view: View):RecyclerView.ViewHolder(view){

    private var binding =  LayoutItemMovieBinding.bind(view)
    fun bind (movie: Movie)
   {
       binding.tvTitle.setText(movie.title)
       binding.tvDate.setText(movie.release_date)
       val picasso = Picasso.get()
       picasso.load("https://image.tmdb.org/t/p/w500/${movie.backdrop_path}")
           .placeholder(R.drawable.ic_baseline_image_not)
           .into(binding.imageView)

   }
}