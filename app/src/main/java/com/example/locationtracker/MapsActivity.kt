package com.example.locationtracker

import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.locationtracker.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.PolylineOptions


import com.google.android.gms.maps.GoogleMap.OnPolygonClickListener
import com.google.android.gms.maps.GoogleMap.OnPolylineClickListener
import com.google.android.gms.maps.model.Polyline

import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, OnPolylineClickListener {

    lateinit var mapFragment : SupportMapFragment
    lateinit var googleMap: GoogleMap

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private lateinit var currentLocation : Location
    private lateinit var fusedLocationProviderClient : FusedLocationProviderClient
    private val permissionCode = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        getCurrentLocationUser()

        val searchButton : Button = findViewById<Button>(R.id.editButton)
        searchButton.setOnClickListener{
            val locationSearch: EditText = findViewById(R.id.editText)
            var location: String = locationSearch.text.toString().trim()


            var addressList: List<Address>? = null

            if (location == null || location == ""){
                Toast.makeText(this, "provide location", Toast.LENGTH_SHORT).show()
            }else{

                val geoCoder = Geocoder(this)
                try {
                    addressList = geoCoder.getFromLocationName(location, 1)
                }catch (e: IOException){
                    e.printStackTrace()
                }

                val address = addressList!![0]
                val latLng = LatLng(address.latitude, address.longitude)

                val markerOptions = MarkerOptions().position(latLng).title("Destination")

                Toast.makeText(applicationContext, address.latitude.toString() + " "
                        + address.longitude.toString(), Toast.LENGTH_LONG).show()

                googleMap?.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))



                //googleMap?.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                //googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
                //googleMap?.addMarker(markerOptions)
            }
        }
    }


    private fun getCurrentLocationUser() {
        if (ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, android.Manifest.permission.ACCESS_COARSE_LOCATION)!=
            PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                permissionCode)

            return
        }

        val getLocation = fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                location ->

            if(location != null) {
                currentLocation = location
                Toast.makeText(applicationContext, currentLocation.latitude.toString() + " " + currentLocation.longitude.toString(), Toast.LENGTH_LONG).show()


                val mapFragment = supportFragmentManager
                    .findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync(this)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            permissionCode -> if (grantResults.isNotEmpty() && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED) {

                getCurrentLocationUser()
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    override fun onMapReady(googleMap: GoogleMap) {
        val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        val markerOptions = MarkerOptions().position(latLng).title("Current Location")

        googleMap?.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
        googleMap?.addMarker(markerOptions)

        // Add polylines to the map.
        val polyline1 = googleMap.addPolyline(PolylineOptions()
            .clickable(true)
            .add(
                LatLng(currentLocation.latitude.toString().toDouble(), currentLocation.longitude.toString().toDouble()),
                LatLng(21.024502040540398, 105.77361306386827),
                LatLng(21.02454278914481, 105.77390811036241),
                LatLng(21.02694973469387, 105.77310512810007),
                LatLng(21.027579596771833, 105.77478658934024),
                LatLng(21.02830961391011, 105.77566714404466),
                LatLng(21.027928601174455, 105.77609082802222),
                LatLng(21.029184184191923, 105.77631317915169),
                LatLng(21.028716439454954, 105.77941945754954),
                LatLng(21.0351833137893, 105.78050811299335),
                LatLng(21.039666501958997, 105.78107369140807),
                LatLng(21.039643405546755, 105.78218479519593)))
        // Store a data object with the polyline, used here to indicate an arbitrary type.

        googleMap.setOnPolylineClickListener(this)
    }

    override fun onPolylineClick(polyline: Polyline) {
    }
    private val PATTERN_DASH_LENGTH_PX = 20



    fun searchLocation(position: Location){
        val locationSearch: EditText = findViewById(R.id.editText)
        var location: String
        location = locationSearch.text.toString().trim()
        var addressList = ArrayList<Address>()

        if (location == null || location == ""){
            Toast.makeText(this, "provide location", Toast.LENGTH_SHORT).show()
        }else{
            val geoCoder = Geocoder(this, Locale.getDefault())
            addressList = geoCoder.getFromLocation(position.latitude, position.longitude, 1) as ArrayList<Address>


            val address = addressList!![0]
            val latLng = LatLng(address.latitude, address.longitude)

            //googleMap?.animateCamera(CameraUpdateFactory.newLatLng(latLng))
            //googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
            //googleMap?.addMarker(markerOptions)
        }
    }
}