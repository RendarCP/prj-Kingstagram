package kr.ac.kpu.kingstagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kr.ac.kpu.kingstagram.navigation.DetailViewFragment
import kr.ac.kpu.kingstagram.navigation.SearchFragment
import kr.ac.kpu.kingstagram.navigation.UserFragment

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    var firestore: FirebaseFirestore? = null
    var toCameraUid: String = ""
    var toCameraNickName: String = ""
    var toCameraEmail: String = ""
    var toCamerapostNumber: Int = 0
    var toProfileImageUrl: String = ""
    var toProfilePostNumber = 0
    var toProfileFollower: ArrayList<String> = arrayListOf()
    var toProfileFollowing: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        firestore = FirebaseFirestore.getInstance()

        bottom_navigation.setOnNavigationItemSelectedListener(this)
        bottom_navigation.selectedItemId = R.id.action_home

        val user = Firebase.auth.currentUser
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document("${user?.uid}")
            .get()
            .addOnSuccessListener { result ->
                toCameraUid = "${user?.uid}"
                toCameraNickName = "${result.data?.get("nickName")}"
                toCameraEmail = "${result.data?.get("email")}"

                //Log.w("TAG", "${result}")
            }
            .addOnFailureListener { exception ->
                //Log.w(TAG, "Error getting documents.", exception)
            }


    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when(p0.itemId){
            R.id.action_home -> {
                val detailViewFragment = DetailViewFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content,detailViewFragment).commit()
                return true
            }
            R.id.action_search -> {
                val searchFragment = SearchFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content,searchFragment).commit()
                return true
            }
            R.id.action_post -> {
                var intent = Intent(this,CameraActivity::class.java)
                intent.putExtra("uid", toCameraUid)
                intent.putExtra("nickName",toCameraNickName)
                intent.putExtra("email", toCameraEmail)
                startActivity(intent)
            }
            R.id.action_profile -> {
                val userFragment = UserFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content,userFragment).commit()
                return true
            }
        }
        return false
    }
}