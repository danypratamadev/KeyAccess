package com.pratama.dany.keyaccess

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class DoorAdapter2(val listDoor : ArrayList<DoorModel>, val context: Context, val idUser : String) : RecyclerView.Adapter<DoorAdapter2.DoorViewHolder>() {

    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoorAdapter2.DoorViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.door_layout2, parent, false)
        return DoorAdapter2.DoorViewHolder(view)

    }

    override fun getItemCount(): Int {

        return listDoor.size

    }

    override fun onBindViewHolder(holder: DoorAdapter2.DoorViewHolder, position: Int) {

        val currentItem = listDoor.get(position)

        holder.name.text = currentItem.name
        holder.key.text = currentItem.key

        db.collection("USER").document(idUser).collection("ACCESS").whereEqualTo("door", currentItem.id).addSnapshotListener { result, e ->

            if(e != null){
                Log.w("Select Door", "listen:error", e)
                return@addSnapshotListener
            }

            if(result!!.isEmpty){

                holder.access.text = "Grant access"
                holder.access.backgroundTintList = context.resources.getColorStateList(R.color.TweetIconEnable)

            } else {

                holder.access.text = "Remove access"
                holder.access.backgroundTintList = context.resources.getColorStateList(R.color.Red400)

            }

        }

        holder.access.setOnClickListener {

            if(holder.access.text.equals("Grant access")){

                val newDoor = hashMapOf(
                    "door" to currentItem.id
                )

                db.collection("USER").document(idUser).collection("ACCESS").add(newDoor)

            } else if(holder.access.text.equals("Remove access")){

                db.collection("USER").document(idUser).collection("ACCESS").whereEqualTo("door", currentItem.id).addSnapshotListener { result, e ->

                    if(e != null){
                        Log.w("Select Door", "listen:error", e)
                        return@addSnapshotListener
                    }

                    if(!result!!.isEmpty){

                        for(doc in result){

                            db.collection("USER").document(idUser).collection("ACCESS").document(doc.id).delete()

                        }

                    }

                }

            }

        }

    }

    class DoorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val key = itemView.findViewById(R.id.key) as TextView
        val name = itemView.findViewById(R.id.name) as TextView
        val access = itemView.findViewById(R.id.access) as Button

    }

}