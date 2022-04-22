package yzh.lifediary.view.main.weather


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import yzh.lifediary.R
import yzh.lifediary.view.main.weather.ui.place.PlaceFragment
import yzh.lifediary.view.main.weather.ui.place.PlaceViewModel
import yzh.lifediary.view.main.weather.ui.weather.WeatherDetailFragment


class WeatherFragment : Fragment() {
    private val viewModel by lazy { ViewModelProviders.of(this).get(PlaceViewModel::class.java) }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return layoutInflater.inflate(R.layout.fragment_mainview,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (viewModel.isPlaceSaved()) {
            val place = viewModel.getSavedPlace()
            val intent = Intent().apply {
                putExtra("location_lng", place.location.lng)
                putExtra("location_lat", place.location.lat)
                putExtra("place_name", place.name)
            }
            childFragmentManager.beginTransaction().replace(R.id.fragment, WeatherDetailFragment(intent))
                .commit()
        } else {
            childFragmentManager.beginTransaction().replace(R.id.fragment, PlaceFragment()).commit()
        }

    }
}
