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
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_activity.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ActivityFragment(val idHome: String, val nameHome: String) : Fragment() {

    private val historyList = ArrayList<HistoryModel>()
    private val userCount = ArrayList<HistoryUserModel>()

    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()
    private val calendar = Calendar.getInstance()

    private var count = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_activity, container, false)

        val dateFormat = SimpleDateFormat("dd MMM yyyy")
        val monthYearFormat = SimpleDateFormat("MMM-yyyy")

        val dateNow = dateFormat.format(calendar.time)
        val monthYear = monthYearFormat.format(calendar.time)

        view.date.text = "Today, "+dateNow.toString()
        view.homeName.text = "All the history of activities at " + nameHome

        view.historyRecycler.setHasFixedSize(true)
        view.historyRecycler.layoutManager = LinearLayoutManager(context)

        db.collection("HOME").document(idHome)
            .collection("HISTORY").document(monthYear).collection("ACTIVITY")
            .orderBy("time", Query.Direction.DESCENDING).limit(10)
            .addSnapshotListener{ result, e ->

                if (e != null) {
                    Log.w("Select History", "listen:error", e)
                    return@addSnapshotListener
                }

                if(result!!.isEmpty){

                    Toast.makeText(context, "Data Kosong", Toast.LENGTH_LONG).show()

                } else {

                    val historyTampung = ArrayList<HistoryModel>()

                    historyList.clear()
                    for(doc in result!!){
                        historyList.add(HistoryModel(doc.id, doc.getString("user"), doc.getString("time"),
                            doc.getString("door"), doc.getBoolean("action")))
                    }

                    val context = view.historyRecycler.context
                    val controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_recycle2)
                    val adapter = HistoryAdapter(historyList, context)
                    adapter.notifyDataSetChanged()
                    view.historyRecycler.adapter = adapter
                    view.historyRecycler.isNestedScrollingEnabled = false
                    view.historyRecycler.layoutAnimation = controller
                    view.historyRecycler.scheduleLayoutAnimation()

                    historyTampung.addAll(historyList)
                    db.collection("USER").whereEqualTo("home", idHome).get().addOnCompleteListener { task ->

                        if(task.isSuccessful){

                            val listTampung = ArrayList<HistoryUserModel>()

                            for(doc in task.result!!){

                                for(i in historyTampung.indices){

                                    if(historyTampung[i].user.equals(doc.id)){

                                        count++

                                    }

                                }

                                val model = HistoryUserModel(doc.getString("name")!!, count)
                                listTampung.add(model)
                                count = 0

                            }

                            userCount.addAll(listTampung)
                            val pie = AnyChart.pie()
                            pie.animation(true)

                            val data = ArrayList<DataEntry>()
                            for(i in userCount.indices){

                                data.add(ValueDataEntry(userCount[i].nama, userCount[i].jumlah))

                            }
                            pie.data(data)

                            view.chartHistory.setChart(pie)

                        }

                    }

                }

            }

        return view
    }

}
