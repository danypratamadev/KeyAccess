package com.pratama.dany.keyaccess

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import android.location.LocationManager
import android.location.Address
import android.location.Geocoder
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.location.LocationManagerCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_add_home.*
import java.io.IOException


class AddHomeActivity : AppCompatActivity(), Bottom_Sheet_Location.BottomSheetLocationListener {

    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var idUser : String
    private lateinit var idHome : String
    private lateinit var name_txt : String
    private var address_txt = ""
    private lateinit var invitationCode : String

    private var mBack : Long = 0
    private val INTERVAL = 2000
    private var enable = 0

    private var position = LatLng(-7.7828, 110.3608)
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private lateinit var dialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_home)

        idUser = intent.getStringExtra("idUser")

        with(maps){
            onCreate(null)
            getMapAsync{
                MapsInitializer.initialize(applicationContext)
                mMap = it
                setMapLocation()
            }
        }

        Handler().postDelayed({
            startFindLocation()
        }, 300)

        home_name.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {

                if(!s.toString().equals("")){

                    ti_home_name.isErrorEnabled = false

                } else {

                    ti_home_name.isErrorEnabled = true
                    ti_home_name.error = "Enter your home name!"

                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //To change body of created functions use File | Settings | File Templates.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //To change body of created functions use File | Settings | File Templates.
            }

        })

        back.setOnClickListener {

            super.onBackPressed()

        }

        finish.setOnClickListener {

            if(!home_name.text.toString().equals("") && !TextUtils.isEmpty(address_txt)){

                dialog = ProgressDialog(this)
                dialog.setMessage("Setting up your home")
                dialog.setCancelable(false)
                dialog.show()
                name_txt = home_name.text.toString()

                val chars1 = "ABCDEF012GHIJKL345MNOPQR678STUVWXYZ9".toCharArray()
                val sb1 = StringBuilder()
                val random1 = Random()
                for (i in 0..5) {
                    val c1 = chars1[random1.nextInt(chars1.size)]
                    sb1.append(c1)
                }

                invitationCode = sb1.toString()

                home_name.alpha = 0.8F
                home_name.clearFocus()
                home_name.isEnabled = false
                finish.alpha = 0.8F
                finish.isEnabled = false

                val home = hashMapOf(
                    "name" to name_txt,
                    "address" to address_txt,
                    "invitation" to invitationCode,
                    "auto" to false
                )

                db.collection("HOME").add(home).addOnSuccessListener { documentReference ->

                    idHome = documentReference.id

                    val update = hashMapOf<String, Any>(

                        "home" to idHome

                    )

                    db.collection("USER").document(idUser).update(update).addOnSuccessListener { e ->

                        val intent = Intent(this, HomeActivity::class.java)
                        intent.putExtra("idUser", idUser)
                        intent.putExtra("idHome", idHome)
                        startActivity(intent)
                        finish()

                    }

                }

            } else {

                if(home_name.text.toString().equals("")){
                    ti_home_name.isErrorEnabled = true
                    ti_home_name.error = "Enter your home name!"
                }

                if(TextUtils.isEmpty(address_txt)){
                    Toast.makeText(this, "Please check your home address!", Toast.LENGTH_LONG).show()
                }

            }

        }

        parentView.setOnTouchListener { view, motionEvent ->

            hideKeyboard(home_name)
            home_name.clearFocus()

            return@setOnTouchListener true

        }

    }

    override fun onLocationClik() {

        enable = 1
        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))

    }

    private fun setMapLocation() {
        with(mMap) {
            isMyLocationEnabled = true
            animateCamera(CameraUpdateFactory.newLatLngZoom(position, 16f))
            mapType = GoogleMap.MAP_TYPE_NORMAL
        }
    }

    private fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationManagerCompat.isLocationEnabled(locationManager)
    }

    private fun startFindLocation(){

        if (!isLocationEnabled(this@AddHomeActivity)) {

            val bottomSheetLocation = Bottom_Sheet_Location()
            bottomSheetLocation.show(this@AddHomeActivity.supportFragmentManager, "Show")

        } else {

            getLocationUpdates()
            startLocationUpdates()

        }

    }

    private fun getLocationUpdates() {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest()
        locationRequest.interval = 50000
        locationRequest.fastestInterval = 50000
        locationRequest.smallestDisplacement = 170f
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationCallback = object : LocationCallback() {

            override fun onLocationResult(locationResult: LocationResult?) {

                locationResult ?: return

                if (locationResult.locations.isNotEmpty()) {

                    val location = locationResult.lastLocation
                    position = LatLng(location.latitude, location.longitude)
                    with(mMap) {
                        isMyLocationEnabled = true
                        animateCamera(CameraUpdateFactory.newLatLngZoom(position, 16f))
                        mapType = GoogleMap.MAP_TYPE_NORMAL
                    }

                    val geocoder = Geocoder(this@AddHomeActivity, Locale.getDefault())
                    var addresses: List<Address>? = null

                    try {
                        addresses = geocoder.getFromLocation(
                            location.latitude,
                            location.longitude,
                            1)
                    } catch (ioException: IOException) {
                        Log.e("MAPS", ioException.toString())
                    } catch (illegalArgumentException: IllegalArgumentException) {
                        Log.e("MAPS", illegalArgumentException.toString())
                    }

                    if (addresses == null || addresses.isEmpty()) {
                        address_txt = "no address found"
                    } else {
                        if(addresses.get(0).getAddressLine(0) != null){
                            address_txt = addresses.get(0).getAddressLine(0)
                            address_dis.text = "Address: $address_txt"
                        }
                    }

                }

            }

        }

    }

    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onBackPressed() {

        if (mBack + INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed()
            return
        } else {
            Toast.makeText(this, "Double tap to exit", Toast.LENGTH_SHORT).show()
        }
        mBack = System.currentTimeMillis()

    }

    private fun showKeyboard(){

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

    }

    private fun hideKeyboard(view: View){

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)

    }

    override fun onResume() {
        super.onResume()
        if(enable == 1){
            startFindLocation()
            enable = 0
        }
        maps.onResume()
    }

    override fun onPause() {
        super.onPause()
        maps.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        maps.onDestroy()
        stopLocationUpdates()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        maps.onLowMemory()
    }

}
