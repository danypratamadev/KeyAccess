package com.pratama.dany.keyaccess

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserAdapter(val userList: ArrayList<UserModel>, val contex: Context) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.UserViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_layout, parent, false)
        return UserViewHolder(view)

    }

    override fun getItemCount(): Int {

        return userList.size

    }

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(holder: UserAdapter.UserViewHolder, position: Int) {

        val currentItem : UserModel = userList.get(position)

        holder.name.text = currentItem.name
        holder.access.text = currentItem.access
        holder.phone.text = currentItem.phone

        holder.phone.setOnClickListener{

            val intent = Intent(Intent.ACTION_CALL)
            intent.setData(Uri.parse("tel: "+currentItem.phone))
            contex.startActivity(intent)

        }

    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name = itemView.findViewById(R.id.name) as TextView
        val access = itemView.findViewById(R.id.access) as TextView
        val phone = itemView.findViewById(R.id.phone) as TextView

    }
}