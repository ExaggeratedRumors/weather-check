package com.ertools.weather_check.view

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.ertools.weather_check.R
import com.ertools.weather_check.dto.Favorites
import com.ertools.weather_check.dto.Location
import com.ertools.weather_check.model.DataManager
import com.ertools.weather_check.utils.InputFilterRange
import com.ertools.weather_check.utils.Locations
import com.ertools.weather_check.utils.Utils
import com.google.android.gms.location.LocationServices

class DecisionFragment: Fragment() {
    private lateinit var view: View
    private var selectedLocation: Location? = null
    private var savedInstance: Bundle? = null
    private var favorites: Favorites = DataManager.readObject(Utils.FAVOURITE_PATH, Favorites::class.java) ?: Favorites()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        this.savedInstance = savedInstanceState
        view = inflater.inflate(R.layout.fragment_decision, container, false)
        serviceCurrentLocation()
        serviceInputLocation()
        serviceLocationsSpinner()
        serviceFavoritesLocationsSpinner()
        return view
    }

    private fun serviceCurrentLocation() {
        val currentBtn = view.findViewById<Button>(R.id.decision_current_location)
        currentBtn.setOnClickListener {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
            if(requireActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1
                )
                Toast.makeText(requireContext(), "You must to grant location permission.", Toast.LENGTH_SHORT).show()
            } else {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: android.location.Location? ->
                        if(location != null) {
                            selectedLocation = Location(
                                "Current location",
                                location.latitude,
                                location.longitude
                            )
                            storeLocationAndChangeFragment()
                        }  else {
                            Toast.makeText(
                                requireContext(),
                                "There is problem with location fetch.",
                                Toast.LENGTH_SHORT
                            ).show()
                            selectedLocation = null
                        }
                    }
                    .addOnFailureListener {
                        selectedLocation = null
                        Toast.makeText(
                            requireContext(),
                            "Location fetch failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                if(selectedLocation == null) {
                    return@setOnClickListener
                }
            }
        }
    }

    private fun serviceInputLocation() {
        val inputBtn = view.findViewById<Button>(R.id.decision_input_location)
        inputBtn.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Input location")

            val latitudeInput = EditText(requireContext())
            val longitudeInput = EditText(requireContext())
            val nameInput = EditText(requireContext())

            latitudeInput.hint = "Latitude"
            longitudeInput.hint = "Longitude"
            nameInput.hint = "Name"

            latitudeInput.filters = arrayOf(InputFilterRange(-90.0, 90.0))
            longitudeInput.filters = arrayOf(InputFilterRange(-180.0, 180.0))

            val layout = LinearLayout(requireContext())
            layout.orientation = LinearLayout.VERTICAL
            layout.addView(latitudeInput)
            layout.addView(longitudeInput)
            layout.addView(nameInput)
            builder.setView(layout)

            builder.setPositiveButton("OK") { _, _ ->
                val latitude = latitudeInput.text.toString().toDoubleOrNull()
                val longitude = longitudeInput.text.toString().toDoubleOrNull()
                val name = nameInput.text.toString()
                if(latitude == null || longitude == null || name.isEmpty()) {
                    Toast.makeText(requireContext(), "Invalid input", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                selectedLocation = Location(name, latitude, longitude)
                selectedLocation?.let { favorites.modify { this.add(selectedLocation!!) } }
                DataManager.writeObject(Utils.FAVOURITE_PATH, favorites)
                storeLocationAndChangeFragment()
            }

            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }

            builder.show()
        }
    }

    private fun serviceLocationsSpinner() {
        val spinner = view.findViewById<Spinner>(R.id.decision_locations)
        val cities = mutableListOf("Select location")
        cities.addAll(Locations.cities.map { it.name })
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            cities
        )
        adapter.setDropDownViewResource(R.layout.view_spinner)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(position == 0) return
                selectedLocation = Locations.cities[position]
                storeLocationAndChangeFragment()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedLocation = null
            }
        }
    }

    private fun serviceFavoritesLocationsSpinner() {
        val spinner = view.findViewById<Spinner>(R.id.decision_favorites_locations)
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            favorites.locations.map { it.name }
        )
        adapter.setDropDownViewResource(R.layout.view_spinner)
        spinner.adapter = adapter
        spinner.setSelection(-1)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedLocation = favorites.locations[position]
                storeLocationAndChangeFragment()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedLocation = null
            }
        }
    }

    fun storeLocationAndChangeFragment() {
        Toast.makeText(
            requireContext(),
            "Location: ${selectedLocation?.name} ${selectedLocation?.lat} ${selectedLocation?.lon}",
            Toast.LENGTH_SHORT
        ).show()
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.hide(this@DecisionFragment)
        savedInstance?.putSerializable(Utils.STORE_FAVORITE_LOCATION, selectedLocation)
        transaction.show(MainDataFragment())
        transaction.show(SecondDataFragment())
        transaction.show(ForecastFragment())
        transaction.commit()
    }
}