package com.pratama.dany.keyaccess

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.yarolegovich.discretescrollview.DSVOrientation
import com.yarolegovich.discretescrollview.DiscreteScrollView
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter
import com.yarolegovich.discretescrollview.transform.ScaleTransformer
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager


class UserManagementActivity : AppCompatActivity(), DiscreteScrollView.OnItemChangedListener<RecyclerView.ViewHolder> {

    private lateinit var back : ImageButton
    private lateinit var userRecycler : DiscreteScrollView
    private lateinit var doorRecycler : RecyclerView
    private lateinit var empty : LinearLayout

    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()

    private var listUser = ArrayList<UserModel>()
    private var listDoor = ArrayList<DoorModel>()
    private var infiniteAdapter: InfiniteScrollAdapter<*>? = null

    private lateinit var idUser : String
    private lateinit var idHome : String
    private lateinit var idDoor : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_management)

        init()
        onClickListener()

    }

    private fun init(){

        idHome = intent.getStringExtra("idHome")

        back = findViewById(R.id.back)
        userRecycler = findViewById(R.id.userRecycler)
        doorRecycler = findViewById(R.id.doorRecycler)
        empty = findViewById(R.id.empty_result)

        empty.visibility = View.GONE
        doorRecycler.setHasFixedSize(true)
        doorRecycler.layoutManager = LinearLayoutManager(this)

        db.collection("USER").whereEqualTo("home", idHome).addSnapshotListener { result, e ->

            if(e != null){
                Log.w("Select User", "listen:error", e)
                return@addSnapshotListener
            }

            if(result!!.isEmpty){

                empty.visibility = View.VISIBLE

            } else {

                listUser.clear()
                empty.visibility = View.GONE

                for (doc in result){

                    listUser.add(UserModel(doc.id, doc.getString("name").toString(),
                        doc.getString("phone").toString(), doc.getString("access").toString()))

                }

                val context = userRecycler.context
                val controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_recycle)
                userRecycler.setOrientation(DSVOrientation.HORIZONTAL)
                userRecycler.addOnItemChangedListener(this)
                infiniteAdapter = InfiniteScrollAdapter.wrap(UserAdapter(listUser, this))
                userRecycler.adapter = infiniteAdapter
                userRecycler.setItemTransitionTimeMillis(400)
                userRecycler.setItemTransformer(ScaleTransformer.Builder().setMinScale(0.8f).build())
                userRecycler.layoutAnimation = controller
                userRecycler.scheduleLayoutAnimation()

            }

        }

    }

    private fun onClickListener(){

        back.setOnClickListener {

            onBackPressed()

        }

    }

    override fun onCurrentItemChanged(p0: RecyclerView.ViewHolder?, p1: Int) {

        val positionInDataSet = infiniteAdapter?.getRealPosition(p1)

        onItemChanged(listUser.get(positionInDataSet!!))

    }

    private fun onItemChanged(item : UserModel){

        idUser = item.id

        db.collection("HOME").document(idHome).collection("DOOR").addSnapshotListener { result, e ->

            if(e != null){
                Log.w("Select Door", "listen:error", e)
                return@addSnapshotListener
            }

            if(!result!!.isEmpty){

                listDoor.clear()

                for(doc in result){

                    listDoor.add(DoorModel(doc.id, doc.getString("name"), doc.getBoolean("status"), doc.getString("key")))

                }

                val context = doorRecycler.context
                val controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_recycle2)
                val adapter = DoorAdapter2(listDoor, this, idUser)
                adapter.notifyDataSetChanged()
                doorRecycler.adapter = adapter
                doorRecycler.isNestedScrollingEnabled = false
                doorRecycler.layoutAnimation = controller
                doorRecycler.scheduleLayoutAnimation()

            }

        }

    }

}
