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
import kotlinx.android.synthetic.main.fragment_alarm.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AlarmFragment(val idHome: String) : Fragment() {

    private val listAlarm = ArrayList<AlarmModel>()
    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()
    private val calendar = Calendar.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_alarm, container, false)

        val monthYearFormat = SimpleDateFormat("MMM-yyyy")
        val monthYear = monthYearFormat.format(calendar.time)

        view.rvAlarm.setHasFixedSize(true)
        view.rvAlarm.layoutManager = LinearLayoutManager(context)

        db.collection("HOME").document(idHome)
            .collection("HISTORY").document(monthYear).collection("ALARM")
            .orderBy("time", Query.Direction.DESCENDING).limit(10)
            .addSnapshotListener{ result, e ->

                if (e != null) {
                    Log.w("Select History", "listen:error", e)
                    return@addSnapshotListener
                }

                if(result!!.isEmpty){

                    Toast.makeText(context, "Data Kosong", Toast.LENGTH_LONG).show()

                } else {

                    listAlarm.clear()
                    for(doc in result!!){
                        listAlarm.add(AlarmModel(doc.id, doc.getString("door"), doc.getString("time")))
                    }

                    val context = view.rvAlarm.context
                    val controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_recycle2)
                    val adapter = AlarmAdapter(listAlarm, context)
                    adapter.notifyDataSetChanged()
                    view.rvAlarm.adapter = adapter
                    view.rvAlarm.isNestedScrollingEnabled = false
                    view.rvAlarm.layoutAnimation = controller
                    view.rvAlarm.scheduleLayoutAnimation()

                }

            }

        return view

    }

}
