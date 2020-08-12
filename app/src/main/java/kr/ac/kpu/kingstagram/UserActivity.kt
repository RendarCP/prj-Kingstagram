package kr.ac.kpu.kingstagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.analytics.connector.AnalyticsConnectorImpl.getInstance
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.core.view.View
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_edit__profile.*
import kotlinx.android.synthetic.main.activity_user.*


class UserActivity : AppCompatActivity() {
var fragmentView : View? = null
    var firestore : FirebaseFirestore? = null
    var uid : String? = null
    var auth : FirebaseAuth? = null
    private var storageReference: StorageReference? = null
    private val user = Firebase.auth.currentUser
    var db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_user)
        storageReference = FirebaseStorage.getInstance().reference

        db.collection("users").document("${user?.uid}")
            .get()
            .addOnSuccessListener { result ->
                Toast.makeText(this, "데이터성공", Toast.LENGTH_SHORT).show()
                Glide.with(this).load("${result.data?.get("imageUrl")}").into(btnProfile)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "데이터실패", Toast.LENGTH_SHORT).show()
                //Log.w(TAG, "Error getting documents.", exception)
            }

        btnProfile.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)

        }
        account_btn_follow_signout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            FirebaseAuth.getInstance().signOut()
                }
        }
    }



