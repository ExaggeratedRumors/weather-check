package com.ertools.weather_check.fragments

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
import com.ertools.weather_check.R
import com.ertools.weather_check.dto.History
import com.ertools.weather_check.dto.Location
import com.ertools.weather_check.model.DataManager
import com.ertools.weather_check.utils.InputFilterRange
import com.ertools.weather_check.utils.Locations
import com.ertools.weather_check.utils.Utils
import com.ertools.weather_check.activities.LocationListener
import com.ertools.weather_check.widgets.HistorySpinnerAdapter
import com.google.android.gms.location.LocationServices

class MenuFragment(private val listener: LocationListener): Fragment() {
    private lateinit var view: View
    private lateinit var history: History
    private var selectedLocation: Location? = null
    private var savedInstance: Bundle? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        this.savedInstance = savedInstanceState
        view = inflater.inflate(R.layout.fragment_menu, container, false)
        history = DataManager.readObject(
            Utils.HISTORY_PATH,
            History::class.java,
            requireContext()
        ) ?: History()

        serviceCurrentLocation()
        serviceInputLocationByCoordinates()
        serviceInputLocationByCity()
        serviceLocationsSpinner()
        serviceHistoryLocationsSpinner()
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
                                "Current city",
                                location.latitude,
                                location.longitude
                            )
                            onSelectedLocation()
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

    private fun serviceInputLocationByCoordinates() {
        val inputBtn = view.findViewById<Button>(R.id.menu_input_location_by_coords)
        inputBtn.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Input new location")

            val latitudeInput = EditText(requireContext())
            val longitudeInput = EditText(requireContext())
            val nameInput = EditText(requireContext())

            latitudeInput.hint = "Latitude"
            longitudeInput.hint = "Longitude"
            nameInput.hint = "Name your location"

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
                selectedLocation = Location(name, null, latitude, longitude)

                if(history.locations.find { it.name == name } == null) {
                    selectedLocation?.let { history.modify { this.add(selectedLocation!!) } }
                    DataManager.writeObject(Utils.HISTORY_PATH, history, requireContext())
                }

                onSelectedLocation()
            }

            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }

            builder.show()
        }
    }

    private fun serviceInputLocationByCity() {
        val inputBtn = view.findViewById<Button>(R.id.menu_input_location_by_name)
        inputBtn.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Input city name")

            val cityInput = EditText(requireContext())
            cityInput.hint = "City"

            val layout = LinearLayout(requireContext())
            layout.orientation = LinearLayout.VERTICAL
            layout.addView(cityInput)
            builder.setView(layout)

            builder.setPositiveButton("OK") { _, _ ->
                val name = cityInput.text.toString()
                if(name.isEmpty()) {
                    Toast.makeText(requireContext(), "Invalid input", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                selectedLocation = Location(name, name, null, null)
                if(history.locations.find { it.name == name} == null) {
                    selectedLocation?.let { history.modify { this.add(selectedLocation!!) } }
                    DataManager.writeObject(Utils.HISTORY_PATH, history, requireContext())
                }
                onSelectedLocation()
            }

            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }

            builder.show()
        }
    }

    private fun serviceLocationsSpinner() {
        val spinner = view.findViewById<Spinner>(R.id.menu_default_locations)
        val cities = mutableListOf("Select location")
        cities.addAll(Locations.cities.map { it.name })
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            cities
        )
        adapter.setDropDownViewResource(R.layout.item_spinner)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(position == 0) return
                selectedLocation = Locations.cities[position]
                onSelectedLocation()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedLocation = null
            }
        }
    }

    private fun serviceHistoryLocationsSpinner() {
        val spinner = view.findViewById<Spinner>(R.id.menu_locations_history)
        val adapter = HistorySpinnerAdapter(
            history,
            requireContext()
        )
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(position == 0) return
                selectedLocation = history.locations[position]
                onSelectedLocation()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedLocation = null
            }
        }
    }

    fun onSelectedLocation() {
        val info = StringBuilder("Location:")
        if(selectedLocation?.city != null)
            info.append(" ${selectedLocation?.city}")
        if(selectedLocation?.lat != null && selectedLocation?.lon != null)
            info.append(" (${selectedLocation?.lat} , ${selectedLocation?.lon})")
        Toast.makeText(
            requireContext(),
            info,
            Toast.LENGTH_SHORT
        ).show()
        listener.notifyLocationChanged(selectedLocation)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(Utils.STORE_FAVORITE_LOCATION, selectedLocation)
    }
}