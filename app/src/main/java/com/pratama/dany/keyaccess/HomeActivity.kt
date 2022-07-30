package com.pratama.dany.keyaccess

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.ncorti.slidetoact.SlideToActView
import com.yarolegovich.discretescrollview.DSVOrientation
import com.yarolegovich.discretescrollview.DiscreteScrollView
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter
import com.yarolegovich.discretescrollview.transform.ScaleTransformer
import kotlinx.android.synthetic.main.activity_home.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class HomeActivity : AppCompatActivity(), DiscreteScrollView.OnItemChangedListener<RecyclerView.ViewHolder> {

    private val dbf : FirebaseDatabase = FirebaseDatabase.getInstance()
    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var sqliteHelper: SqliteHelper
    private lateinit var notificationManager: NotificationManager

    private lateinit var idUser : String
    private lateinit var accessUser : String
    private lateinit var idHome : String
    private lateinit var invitCode : String
    private lateinit var idDoor : String
    private lateinit var keyDoor : String
    private var status : Boolean = false

    private val dataDoor = ArrayList<DoorModel>()
    private var infiniteAdapter: InfiniteScrollAdapter<*>? = null
    private var dataPosisi : Int = 0
    private var dataName : String = "Door"
    private var currentPosition : Int = 0
    private var currentAction : String = "Scroll"

    private var mBack : Long = 0
    private val INTERVAL = 2000

    override fun onCreate(savedInstanceState: Bundle?) {

        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        init()
        onClickListener()

    }

    private fun init(){

        sqliteHelper = SqliteHelper(this)

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll();

        idUser = intent.getStringExtra("idUser")
        idHome = intent.getStringExtra("idHome")

        db.collection("USER").document(idUser).get().addOnSuccessListener { e ->

            if(e != null && e.exists()){

                accessUser = e.getString("access").toString()

                if(accessUser.equals("User")){

                    homeDetails.isEnabled = false

                }

            }

        }

        getData()

        open.onSlideCompleteListener = (object : SlideToActView.OnSlideCompleteListener {

            override fun onSlideComplete(view: SlideToActView) {

                currentAction = "Slide"
                currentPosition = dataPosisi

                val calendar = Calendar.getInstance()
                val monthYearFormat = SimpleDateFormat("MMM-yyyy")
                val timeFormat = SimpleDateFormat("HH:mm:ss")
                val monthYear = monthYearFormat.format(calendar.time)
                val timeDate = timeFormat.format(calendar.time)

                if(status){

                    val doorStatus = dbf.getReference(keyDoor + "/status")
                    doorStatus.setValue(false)

                    val updateStatus = hashMapOf<String, Any>(
                        "status" to false
                    )

                    db.collection("HOME").document(idHome).collection("DOOR")
                        .document(idDoor).update(updateStatus)

                    val history = hashMapOf(
                        "user" to idUser,
                        "time" to timeDate,
                        "door" to dataName,
                        "action" to false
                    )

                    db.collection("HOME").document(idHome)
                        .collection("HISTORY").document(monthYear).collection("ACTIVITY").add(history)

                    val closeAuto = sqliteHelper.getStatusAutomatically(21) as String
                    if(closeAuto.equals("T")){
                        updateDoorState(idDoor, keyDoor, false)
                    }

                } else {

                    val doorStatus = dbf.getReference(keyDoor + "/status")
                    doorStatus.setValue(true)

                    val updateStatus = hashMapOf<String, Any>(
                        "status" to true
                    )

                    db.collection("HOME").document(idHome).collection("DOOR")
                        .document(idDoor).update(updateStatus)

                    val history = hashMapOf(
                        "user" to idUser,
                        "time" to timeDate,
                        "door" to dataName,
                        "action" to true
                    )

                    db.collection("HOME").document(idHome)
                        .collection("HISTORY").document(monthYear).collection("ACTIVITY").add(history)

                }

            }

        })

        if(!isServiceRunning(NotificationService::class.java)){
            val intent = Intent(this, NotificationService::class.java)
            intent.putExtra("idHome", idHome)
            intent.putExtra("idUser", idUser)
            startService(intent)
        } else {
            Log.i("Service Notif", "Aktif")
        }

        if(!isServiceRunning(NotificationAlaramService::class.java)){
            val intent = Intent(this, NotificationAlaramService::class.java)
            intent.putExtra("idHome", idHome)
            intent.putExtra("idUser", idUser)
            startService(intent)
        } else {
            Log.i("Service Alarm", "Aktif")
        }

        if(!isServiceRunning(WifiService::class.java)){
            val intent = Intent(this, WifiService::class.java)
            intent.putExtra("idHome", idHome)
            intent.putExtra("idUser", idUser)
            startService(intent)
        } else {
            Log.i("Service Alarm", "Aktif")
        }

    }

    private fun Snackbar.withColor(@ColorInt colorInt: Int): Snackbar{
        this.view.setBackgroundColor(colorInt)
        return this
    }

    private fun isServiceRunning(service: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for(servicenfo in manager.getRunningServices(Int.MAX_VALUE)){
            if(service.name.equals(servicenfo.service.className)){
                return true
            }
        }
        return false;
    }


    private fun onClickListener(){

        homeDetails.setOnClickListener {

            val intent = Intent(this, HomeDetailsActivity::class.java)
            intent.putExtra("homeName", homeName.text)
            intent.putExtra("homeAddress", homeAddress.text)
            intent.putExtra("idHome", idHome)
            intent.putExtra("invitation", invitCode)
            startActivity(intent)

        }

        action.setOnClickListener {

            val popupMenu = PopupMenu(this, it)
            popupMenu.inflate(R.menu.option_menu)
            popupMenu.setOnMenuItemClickListener { item ->

                when (item.itemId){

                    R.id.setting -> {

                        val intent = Intent(this, SettingActivity::class.java)
                        intent.putExtra("idUser", idUser)
                        intent.putExtra("idHome", idHome)
                        intent.putExtra("nameHome", homeName.text)
                        intent.putExtra("homeAddress", homeAddress.text)
                        intent.putExtra("invitation", invitCode)
                        intent.putExtra("accessUser", accessUser)
                        startActivity(intent)
                        true

                    }

                    R.id.signout -> {

                        showPopupWarning()
                        true

                    }

                    R.id.history -> {
                        val intent = Intent(this, HistoryActivity::class.java)
                        intent.putExtra("idHome", idHome)
                        intent.putExtra("nameHome", homeName.text)
                        startActivity(intent)
                        true
                    }

                    R.id.users -> {
                        val intent = Intent(this, UserManagementActivity::class.java)
                        intent.putExtra("idHome", idHome)
                        startActivity(intent)
                        true
                    }

                    R.id.door -> {

                        val intent = Intent(this, SetupActivity::class.java)
                        intent.putExtra("idUser", idUser)
                        intent.putExtra("idHome", idHome)
                        startActivity(intent)
                        true

                    }

                    else -> false

                }

            }
            popupMenu.show()
            val menu = popupMenu.menu
            if(accessUser.equals("User")){
                menu.findItem(R.id.users).isVisible = false
                menu.findItem(R.id.door).isVisible = false
            }

        }

        setupNow.setOnClickListener {

            val intent = Intent(this, SetupActivity::class.java)
            intent.putExtra("idUser", idUser)
            intent.putExtra("idHome", idHome)
            startActivity(intent)

        }

    }

    fun updateDoorState(id: String, key: String, oldStatus: Boolean){

        val reference = dbf.reference
        reference.child(key).child("status").addValueEventListener(object:
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.e("CANCEL", p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {

                val status = p0.getValue(Boolean::class.java)
                if(status != oldStatus){

                    val updateStatus = hashMapOf<String, Any>(
                        "status" to true
                    )

                    db.collection("HOME").document(idHome).collection("DOOR")
                        .document(id).update(updateStatus)

                }

            }

        })
    }

    private fun getData(){


        db.collection("HOME").document(idHome).addSnapshotListener{ result, e ->

            if(e != null){
                Log.w("Select Home", "listen:error", e)
                return@addSnapshotListener
            }

            if(result != null && result.exists()){

                homeName.text = result.getString("name")
                homeAddress.text = result.getString("address")
                invitCode = result.getString("invitation").toString()

            }

        }

        db.collection("HOME").document(idHome).collection("DOOR").addSnapshotListener { result, e ->

            if(e != null){
                Log.w("Select Door", "listen:error", e)
                return@addSnapshotListener
            }

            if(result!!.isEmpty){

                val anim = AnimationUtils.loadAnimation(this, R.anim.slide_from_buttom)

                form_action.visibility = View.INVISIBLE
                empty.visibility = View.VISIBLE
                empty2.visibility = View.VISIBLE
                addNew.visibility = View.VISIBLE
                addNew.startAnimation(anim)

            } else {

                val anim = AnimationUtils.loadAnimation(this, R.anim.slide_from_buttom)

                dataDoor.clear()
                empty.visibility = View.GONE
                empty2.visibility = View.GONE
                addNew.visibility = View.INVISIBLE
                form_action.visibility = View.VISIBLE
                form_action.startAnimation(anim)

                for (doc in result){

                    dataDoor.add(DoorModel(doc.id, doc.getString("name"),
                        doc.getBoolean("status"), doc.getString("key")))

                }

                val context = listDoor.context
                val controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_recycle)
                listDoor.setOrientation(DSVOrientation.HORIZONTAL)
                listDoor.addOnItemChangedListener(this)
                infiniteAdapter = InfiniteScrollAdapter.wrap(DoorAdapter(dataDoor, this))
                listDoor.adapter = infiniteAdapter
                listDoor.setItemTransitionTimeMillis(400)
                listDoor.setItemTransformer(ScaleTransformer.Builder().setMinScale(0.8f).build())
                listDoor.layoutAnimation = controller
                listDoor.scheduleLayoutAnimation()

                Handler().postDelayed({
                    var destination = currentPosition
                    val adapter = listDoor.getAdapter()
                    if (adapter is InfiniteScrollAdapter) {
                        destination = (adapter as InfiniteScrollAdapter<*>).getClosestPosition(destination)
                    }
                    listDoor.smoothScrollToPosition(destination)

                }, 500)

            }

        }

    }

    override fun onCurrentItemChanged(doorViewHolder: RecyclerView.ViewHolder?, position: Int) {

        val positionInDataSet = infiniteAdapter?.getRealPosition(position)

        if(currentAction.equals("Scroll")){
            onItemChanged(dataDoor[positionInDataSet!!])
        } else {
            if(positionInDataSet == currentPosition){
                onItemChanged(dataDoor[positionInDataSet])
                currentAction = "Scroll"
            }
        }

    }

    private fun onItemChanged(item : DoorModel){

        idDoor = item.id.toString()
        keyDoor = item.key.toString()
        dataName = item.name.toString()
        dataPosisi = dataDoor.indexOf(item)

        if(item.status == false){

            open.isReversed = true
            open.resetSlider()
            open.text = "Slide left to lock the door"
            open.textColor = resources.getColor(R.color.Red400)
            open.outerColor = resources.getColor(R.color.Red50)
            open.innerColor = resources.getColor(R.color.Red400)
            status = false

        } else {

            open.isReversed = false
            open.resetSlider()
            open.text = "Slide right to unlock the door"
            open.textColor = resources.getColor(R.color.TweetIconEnable)
            open.outerColor = resources.getColor(R.color.Blue50)
            open.innerColor = resources.getColor(R.color.TweetIconEnable)
            status = true

        }

        db.collection("USER").document(idUser).collection("ACCESS")
            .whereEqualTo("door", idDoor).addSnapshotListener { result, e ->

                if(e != null){
                    Log.w("Select Door", "listen:error", e)
                    return@addSnapshotListener
                }

                if(result!!.isEmpty){

                    caption.text = "Oops.. something wrong!"
                    statusDoor.text = "You don't have access right now"
                    statusDoor.setTextColor(resources.getColor(R.color.Red400))

                } else {

                    if(item.status == false){

                        caption.text = "Let's lock " + item.name + " now!"
                        statusDoor.text = "is unlocked now"
                        statusDoor.setTextColor(resources.getColor(R.color.Red400))

                    } else {

                        caption.text = "Do you want to unlock " + item.name + "?"
                        statusDoor.text = "is locked now"
                        statusDoor.setTextColor(resources.getColor(R.color.TweetIconEnable))

                    }

                }

                val wifiStatus = dbf.getReference("$keyDoor/wifi")
                wifiStatus.setValue(false)

            }

        dbf.getReference("$keyDoor/wifi").addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.e("DATABASE", p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val wifi = p0.getValue(Boolean::class.java)
                    open.isLocked = !wifi!!
                }
            }

        })

    }

    private fun showPopupWarning() {

        val logout : Button
        val cancel : Button
        val mdialog : Dialog
        mdialog = Dialog(this)
        mdialog.setCancelable(false)
        mdialog.setContentView(R.layout.signout_layout)
        logout = mdialog.findViewById(R.id.logout)
        cancel = mdialog.findViewById(R.id.cancel)

        cancel.setOnClickListener {

            mdialog.dismiss()

        }

        logout.setOnClickListener {

            auth.signOut()
            mdialog.dismiss()

            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()

        }

        mdialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mdialog.getWindow()!!.getAttributes().windowAnimations = R.style.DialogAnimation
        mdialog.show()

    }

    override fun onBackPressed() {

        if (mBack + INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed()
            return
        } else {
            Toast.makeText(this, "Double tap to exit", Toast.LENGTH_SHORT).show()
        }
        mBack = System.currentTimeMillis()

    }

}
