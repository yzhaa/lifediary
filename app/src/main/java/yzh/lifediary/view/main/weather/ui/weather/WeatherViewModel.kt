package yzh.lifediary.view.main.weather.ui.weather

import androidx.lifecycle.*
import yzh.lifediary.view.main.weather.logic.Repository
import yzh.lifediary.view.main.weather.logic.model.Location


class WeatherViewModel : ViewModel() {

    private val locationLiveData = MutableLiveData<Location>()

    var locationLng = ""

    var locationLat = ""

    var placeName = ""

    val weatherLiveData = Transformations.switchMap(locationLiveData) { location ->
        Repository.refreshWeather(location.lng, location.lat, placeName)
    }

    fun refreshWeather(lng: String, lat: String) {
        locationLiveData.value = Location(lng, lat)
    }

}