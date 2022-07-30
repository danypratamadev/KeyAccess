package com.pratama.dany.keyaccess

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class DeviceAdapter(val listDeviceInfo: ArrayList<DeviceModel>, val context: Context) : RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.device_layout, parent, false)
        return DeviceViewHolder(view)

    }

    override fun getItemCount(): Int {

        return listDeviceInfo.size

    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {

        val currentItem : DeviceModel = listDeviceInfo.get(position)

        holder.name.text = currentItem.door
        if(currentItem.startlost.equals("null")){

            holder.startLost.text = "Active on ${currentItem.endlost}"
            holder.endLost.visibility = View.GONE
            holder.line.visibility = View.GONE

        } else {

            holder.startLost.text = currentItem.startlost
            holder.endLost.text = currentItem.endlost
            holder.line.visibility = View.VISIBLE

        }

    }

    class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val card = itemView.findViewById(R.id.cardHistory) as CardView
        val name = itemView.findViewById(R.id.name) as TextView
        val startLost = itemView.findViewById(R.id.startLost) as TextView
        val endLost = itemView.findViewById(R.id.endLost) as TextView
        val line = itemView.findViewById(R.id.line) as RelativeLayout
    }


}
