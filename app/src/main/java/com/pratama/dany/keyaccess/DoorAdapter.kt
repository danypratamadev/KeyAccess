package com.pratama.dany.keyaccess

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.wang.avi.AVLoadingIndicatorView
import java.util.*
import kotlin.collections.ArrayList

class DoorAdapter(val doorList: ArrayList<DoorModel>, val contex: Context) : RecyclerView.Adapter<DoorAdapter.DoorViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoorViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.door_layout, parent, false)
        return DoorViewHolder(view)

    }

    override fun getItemCount(): Int {

        return doorList.size

    }

    override fun onBindViewHolder(holder: DoorViewHolder, position: Int) {

        val currentItem : DoorModel = doorList.get(position)

        holder?.name?.text = currentItem.name
        holder.key.text = currentItem.key

        if(currentItem.status == false){

            holder.load.setIndicatorColor(contex.resources.getColor(R.color.Red500))
            holder.image.setImageDrawable(contex.resources.getDrawable(R.drawable.ic_lock_open_black_24dp))
            holder.image.imageTintList = contex.resources.getColorStateList(R.color.Red500)
            holder.image.background = contex.resources.getDrawable(R.drawable.back_key_round_red)
            holder.status.text = "is unlocked"
            holder.status.setTextColor(contex.resources.getColor(R.color.Red400))
            holder.icon.setImageDrawable(contex.resources.getDrawable(R.drawable.ic_warning_black_24dp))
            holder.icon.imageTintList = contex.resources.getColorStateList(R.color.Red400)

        } else {

            holder.load.setIndicatorColor(contex.resources.getColor(R.color.TweetIconEnable))
            holder.image.setImageDrawable(contex.resources.getDrawable(R.drawable.ic_lock_outline_black_24dp))
            holder.image.imageTintList = contex.resources.getColorStateList(R.color.TweetIconEnable)
            holder.image.background = contex.resources.getDrawable(R.drawable.back_key_round_blue)
            holder.status.text = "is locked"
            holder.status.setTextColor(contex.resources.getColor(R.color.TweetIconEnable))
            holder.icon.setImageDrawable(contex.resources.getDrawable(R.drawable.ic_done_black_24dp))
            holder.icon.imageTintList = contex.resources.getColorStateList(R.color.TweetIconEnable)

        }

    }

    class DoorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val load = itemView.findViewById(R.id.load) as AVLoadingIndicatorView
        val image = itemView.findViewById(R.id.image) as ImageButton
        val icon = itemView.findViewById(R.id.icon) as ImageView
        val key = itemView.findViewById(R.id.key) as TextView
        val name = itemView.findViewById(R.id.name) as TextView
        val status = itemView.findViewById(R.id.status) as TextView

    }


}
