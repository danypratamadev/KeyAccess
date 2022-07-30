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

class HistoryAdapter(val historyList: ArrayList<HistoryModel>, val context: Context) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.history_layout, parent, false)
        return HistoryViewHolder(view)

    }

    override fun getItemCount(): Int {

        return historyList.size

    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {

        val currentItem : HistoryModel = historyList.get(position)

        db.collection("USER").document(currentItem.user!!).addSnapshotListener{ result, e ->

            if(result != null && result.exists()){

                holder?.name?.text = result.getString("name")

            }

        }

        if(currentItem.action == false){

            holder?.action?.text = "Unlock " + currentItem.door
            holder.action.setTextColor(context.resources.getColor(R.color.Red200))

        } else {

            holder?.action?.text = "Lock " + currentItem.door
            holder.action.setTextColor(context.resources.getColor(R.color.TweetIconEnable))

        }

        holder?.time?.text = currentItem.time

    }

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val card = itemView.findViewById(R.id.cardHistory) as CardView
        val name = itemView.findViewById(R.id.name) as TextView
        val action = itemView.findViewById(R.id.action) as TextView
        val time = itemView.findViewById(R.id.time) as TextView
    }


}
