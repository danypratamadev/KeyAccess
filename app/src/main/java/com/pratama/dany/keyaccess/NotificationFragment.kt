package com.pratama.dany.keyaccess


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_notification.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NotificationFragment(val idHome: String) : Fragment() {

    private val listDeviceInfo = ArrayList<DeviceModel>()
    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()
    private val calendar = Calendar.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_notification, container, false)

        val monthYearFormat = SimpleDateFormat("MMM-yyyy")
        val monthYear = monthYearFormat.format(calendar.time)

        view.rvDevice.setHasFixedSize(true)
        view.rvDevice.layoutManager = LinearLayoutManager(context)

        db.collection("HOME").document(idHome)
            .collection("HISTORY").document(monthYear).collection("NOTIF")
            .orderBy("endlost", Query.Direction.DESCENDING).limit(10)
            .addSnapshotListener{ result, e ->

                if (e != null) {
                    Log.w("Select History", "listen:error", e)
                    return@addSnapshotListener
                }

                if(result!!.isEmpty){

                    Toast.makeText(context, "Data Kosong", Toast.LENGTH_LONG).show()

                } else {

                    listDeviceInfo.clear()
                    for(doc in result!!){
                        listDeviceInfo.add(DeviceModel(doc.id, doc.getString("door"),
                            doc.getString("startlost"), doc.getString("endlost")))
                    }

                    val context = view.rvDevice.context
                    val controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_recycle2)
                    val adapter = DeviceAdapter(listDeviceInfo, context)
                    adapter.notifyDataSetChanged()
                    view.rvDevice.adapter = adapter
                    view.rvDevice.isNestedScrollingEnabled = false
                    view.rvDevice.layoutAnimation = controller
                    view.rvDevice.scheduleLayoutAnimation()

                }

            }

        return view
    }


}
