package com.project22.myapplication.frangments

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.maps.android.SphericalUtil
import com.project22.myapplication.R
import com.project22.myapplication.databinding.ActivityMapsBinding
import java.lang.Math.*
import kotlin.math.pow


class TravelFragment : Fragment(), OnMapReadyCallback {
    // TODO: Rename and change types of parameters

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    val REQUEST_LOCATION_PERMISSION = 1
    private var locationManager : LocationManager? = null
    var auth: FirebaseAuth = Firebase.auth
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun  bitmapDescriptorFromVector(vectorResId:Int): BitmapDescriptor {
        var vectorDrawable = ContextCompat.getDrawable(requireContext(), vectorResId);
        vectorDrawable!!.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        var bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        var canvas =  Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var rootView =  inflater.inflate(R.layout.fragment_travel, container, false)

        // Inflate the layout for this fragment
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return rootView
    }

    fun distanceInMeter(firstLocation: Location, secondLocation: Location): Double {
        val earthRadius = 6371000.0
        val deltaLatitudeDegree = (firstLocation.latitude - secondLocation.latitude) * Math.PI / 180f
        val deltaLongitudeDegree = (firstLocation.longitude - secondLocation.longitude) * Math.PI / 180f
        val a = sin(deltaLatitudeDegree / 2).pow(2) +
                cos(firstLocation.latitude * Math.PI / 180f) * cos(secondLocation.latitude * Math.PI / 180f) *
                sin(deltaLongitudeDegree / 2).pow(2)
        val c = 2f * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }

    data class Location(val latitude: Double, val longitude: Double)

    fun drawCurveOnMap(googleMap: GoogleMap, latLng1: LatLng, latLng2: LatLng, k: Double) {

        //Adding marker is optional here, you can move out from here.
        googleMap.addMarker(
            MarkerOptions()
                .position(latLng1)
                .icon(bitmapDescriptorFromVector(R.drawable.travel)))
        googleMap.addMarker(
                    MarkerOptions()
                        .position(latLng2)
                        .icon(bitmapDescriptorFromVector(R.drawable.travel)))


        var h = SphericalUtil.computeHeading(latLng1, latLng2)
        var d = 0.0
        val p: LatLng?

        //The if..else block is for swapping the heading, offset and distance
        //to draw curve always in the upward direction
        if (h < 0) {
            d = SphericalUtil.computeDistanceBetween(latLng2, latLng1)
            h = SphericalUtil.computeHeading(latLng2, latLng1)
            //Midpoint position
            p = SphericalUtil.computeOffset(latLng2, d * 0.5, h)
        } else {
            d = SphericalUtil.computeDistanceBetween(latLng1, latLng2)

            //Midpoint position
            p = SphericalUtil.computeOffset(latLng1, d * 0.5, h)
        }

        //Apply some mathematics to calculate position of the circle center
        val x = (1 - k * k) * d * 0.5 / (2 * k)
        val r = (1 + k * k) * d * 0.5 / (2 * k)

        val c = SphericalUtil.computeOffset(p, x,  h + 90.0 )

        //Calculate heading between circle center and two points
        val h1 = SphericalUtil.computeHeading(c, latLng1)
        val h2 = SphericalUtil.computeHeading(c, latLng2)

        //Calculate positions of points on circle border and add them to polyline options
        val numberOfPoints = 500 //more numberOfPoints more smooth curve you will get
        val step = (h2 - h1) / numberOfPoints

        //Create PolygonOptions object to draw on map
        val polygon = PolygonOptions()

        //Create a temporary list of LatLng to store the points that's being drawn on map for curve
        val temp = arrayListOf<LatLng>()

        //iterate the numberOfPoints and add the LatLng to PolygonOptions to draw curve
        //and save in temp list to add again reversely in PolygonOptions
        for (i in 0 until numberOfPoints) {
            val latlng = SphericalUtil.computeOffset(c, r, h1 + i * step)
            polygon.add(latlng) //Adding in PolygonOptions
            temp.add(latlng)    //Storing in temp list to add again in reverse order
        }

        //iterate the temp list in reverse order and add in PolygonOptions
        for (i in (temp.size - 1) downTo 1) {
            polygon.add(temp[i])
        }

        polygon.strokeColor(Color.rgb(91,14,45))
        polygon.strokeWidth(12f)

        googleMap.addPolygon(polygon)

        temp.clear() //clear the temp list
    }

    override fun onMapReady(googleMap: GoogleMap?) {



        if (googleMap != null) {
            map = googleMap
            val latitude = 37.422160
            val longitude = -122.084270
            val zoomLevel = 1f

            db.collection("users").document(auth.uid.toString()).collection("location")
                .get().addOnSuccessListener { documents ->
                    for (document in documents) {
                        if (document != null) {

                            val d = distanceInMeter(Location(document.data?.get("originLatitude").toString().toDouble(),
                                document.data?.get("originLongitude").toString().toDouble()),
                                Location(document.data?.get("destinationLatitude").toString().toDouble(),
                                    document.data?.get("destinationLongitude").toString().toDouble())
                            )/1000


                            val origin = LatLng(document.data?.get("originLatitude").toString().toDouble(),
                                document.data?.get("originLongitude").toString().toDouble())
                            val destination = LatLng(document.data?.get("destinationLatitude").toString().toDouble(),
                                document.data?.get("destinationLongitude").toString().toDouble())


                            Log.d("DATA",d.toString())
                            if (googleMap != null) {
                                if (d.toDouble()>=1300) {
                                    drawCurveOnMap(googleMap,origin,destination,1.0)
                                } else {
                                    drawCurveOnMap(googleMap,origin,destination,0.5)
                                }

                            }

                        }

                    }}


//            val homeLatLng = LatLng(latitude, longitude)
//            map.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng, zoomLevel))
//


            enableMyLocation()
        }






    }



    private fun isPermissionGranted() : Boolean {
        return ContextCompat.checkSelfPermission(
            this.context as Activity,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            if (ActivityCompat.checkSelfPermission(
                    this.context as Activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this.context as Activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                return
            }
            map.isMyLocationEnabled = true

        }
        else {
            ActivityCompat.requestPermissions(
                this.context as Activity,
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            }
        }
    }


}