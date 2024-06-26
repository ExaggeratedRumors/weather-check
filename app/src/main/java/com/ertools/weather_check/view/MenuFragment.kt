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
import com.ertools.weather_check.R
import com.ertools.weather_check.dto.History
import com.ertools.weather_check.dto.Location
import com.ertools.weather_check.model.DataManager
import com.ertools.weather_check.utils.Locations
import com.ertools.weather_check.utils.Utils
import com.ertools.weather_check.interfaces.DataFetchListener
import com.google.android.gms.location.LocationServices

class MenuFragment: Fragment() {
    private lateinit var view: View
    private lateinit var history: History
    private var selectedLocation: Location? = null
    private var savedInstance: Bundle? = null
    var listener: DataFetchListener? = null

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
        val currentBtn = view.findViewById<Button>(R.id.menu_current_location)
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
                                null,
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
            builder.setTitle(getString(R.string.input_window_title))

            val latitudeInput = EditText(requireContext())
            val longitudeInput = EditText(requireContext())
            val nameInput = EditText(requireContext())

            latitudeInput.hint = getString(R.string.input_window_latitude)
            longitudeInput.hint = getString(R.string.input_window_longitude)
            nameInput.hint = getString(R.string.input_window_name)

            val layout = LinearLayout(requireContext())
            layout.orientation = LinearLayout.VERTICAL
            layout.addView(latitudeInput)
            layout.addView(longitudeInput)
            layout.addView(nameInput)
            builder.setView(layout)

            builder.setPositiveButton(getString(R.string.input_window_ok)) { _, _ ->
                val latitude = latitudeInput.text.toString().toDoubleOrNull()
                val longitude = longitudeInput.text.toString().toDoubleOrNull()
                val name = nameInput.text.toString()

                if(latitude == null || longitude == null || name.isEmpty()
                    || latitude !in -90.0..90.0 || longitude !in -180.0..180.0) {
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

            builder.setNegativeButton(getString(R.string.input_window_cancel)) { dialog, _ ->
                dialog.cancel()
            }

            builder.show()
        }
    }

    private fun serviceInputLocationByCity() {
        val inputBtn = view.findViewById<Button>(R.id.menu_input_location_by_name)
        inputBtn.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(getString(R.string.input_window_title))

            val cityInput = EditText(requireContext())
            cityInput.hint = getString(R.string.input_window_city)

            val layout = LinearLayout(requireContext())
            layout.orientation = LinearLayout.VERTICAL
            layout.addView(cityInput)
            builder.setView(layout)

            builder.setPositiveButton( getString(R.string.input_window_ok)) { _, _ ->
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

            builder.setNegativeButton( getString(R.string.input_window_cancel)) { dialog, _ ->
                dialog.cancel()
            }

            builder.show()
        }
    }

    private fun serviceLocationsSpinner() {
        val spinner = view.findViewById<Spinner>(R.id.menu_default_locations)
        val cities = mutableListOf(getString(R.string.menu_select_default))
        cities.addAll(Locations.cities.map { it.name })
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.text_spinner,
            cities
        )
        adapter.setDropDownViewResource(R.layout.item_spinner)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(position == 0) return
                selectedLocation = Locations.cities[position - 1]
                onSelectedLocation()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedLocation = null
            }
        }
    }

    private fun serviceHistoryLocationsSpinner() {
        val spinner = view.findViewById<Spinner>(R.id.menu_locations_history)
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.text_spinner,
            mutableListOf(getString(R.string.menu_select_history)) + history.locations.map { l -> l.name }
        )
        adapter.setDropDownViewResource(R.layout.item_spinner)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(position == 0) return
                spinner.setSelection(0)
                askSpinnerItem(position - 1, adapter)
                adapter.notifyDataSetChanged()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedLocation = null
            }
        }
    }

    private fun askSpinnerItem(position: Int, adapter: ArrayAdapter<String>){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.input_window_manage, history.locations[position].name))

        builder.setPositiveButton(getString(R.string.input_window_use)) { _, _ ->
            selectedLocation = history.locations[position]
            onSelectedLocation()
        }

        builder.setNeutralButton(getString(R.string.input_window_remove)) { _, _ ->
            adapter.remove(adapter.getItem(position + 1) as String)
            history.modify { this.removeAt(position) }
            DataManager.writeObject(Utils.HISTORY_PATH, history, requireContext())
            DataManager.removeFile("${ history.locations[position].name}${Utils.WEATHER_DATA_PATH}" , requireContext())
            DataManager.removeFile("${ history.locations[position].name}${Utils.FORECAST_DATA_PATH}" , requireContext())
        }

        builder.setNegativeButton(getString(R.string.input_window_cancel)) { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    fun onSelectedLocation() {
        val info = StringBuilder("Location:")
        if(selectedLocation?.city != null)
            info.append(" ${selectedLocation?.city}")
        if(selectedLocation?.lat != null && selectedLocation?.lon != null)
            info.append(" (${selectedLocation?.lat} , ${selectedLocation?.lon})")
        Toast.makeText(
            requireContext(),
            if(listener == null) "No listener" else info,
            Toast.LENGTH_SHORT
        ).show()
        listener?.notifyLocationChanged(selectedLocation)
    }
}