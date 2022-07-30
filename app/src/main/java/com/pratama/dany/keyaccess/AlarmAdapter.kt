package com.pratama.dany.keyaccess

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class AlarmAdapter(val listAlarm: ArrayList<AlarmModel>, val context: Context) : RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.alarm_layout, parent, false)
        return AlarmViewHolder(view)

    }

    override fun getItemCount(): Int {

        return listAlarm.size

    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {

        val currentItem : AlarmModel = listAlarm.get(position)

        holder.name.text = currentItem.door
        holder.time.text = currentItem.time

    }

    class AlarmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val card = itemView.findViewById(R.id.cardHistory) as CardView
        val name = itemView.findViewById(R.id.name) as TextView
        val time = itemView.findViewById(R.id.time) as TextView
    }


}
