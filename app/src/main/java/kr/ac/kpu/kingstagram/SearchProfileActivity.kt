package kr.ac.kpu.kingstagram

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_comments.*
import kotlinx.android.synthetic.main.activity_search_profile.*
import kotlinx.android.synthetic.main.view_search.view.*

class SearchProfileActivity : AppCompatActivity() {
    var firestore: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_profile)

        val profileUid = intent.getStringExtra("uid")

        firestore = FirebaseFirestore.getInstance()
        val user = Firebase.auth.currentUser
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document("$profileUid")
            .get()
            .addOnSuccessListener { result ->

                //Toast.makeText(this.context,"${result.data?.get("name")}",Toast.LENGTH_LONG)
                if (result.data?.get("imageUrl") == "")
                    account_iv_profile.setImageResource(R.drawable.account_iv_profile)
                else
                    Glide.with(this).load(result.data?.get("imageUrl"))
                        .into(account_iv_profile)

                account_tv_post_count.text = "${result.data?.get("postNumber")}"
                val follower: ArrayList<String> = result.data?.get("follower") as ArrayList<String>
                account_tv_follower_count.text = "${follower.size}"
                val following: ArrayList<String> = result.data?.get("following") as ArrayList<String>
                account_tv_following_count.text = "${following.size}"
                name_profile.text = "${result.data?.get("name")}"
                //Log.w("TAG", "${result}")
            }
            .addOnFailureListener { exception ->
                //Log.w(TAG, "Error getting documents.", exception)
            }

        //recycleView_comments.layoutManager = LinearLayoutManager(this)
        //recycleView_comments.adapter = CommentsViewAdapter()

        account_btn_follow.setOnClickListener {
            val data = hashMapOf(
                "following" to arrayListOf<String>(profileUid)
            )
            db.collection("users").document("${user?.uid}")
                .set(data, SetOptions.merge())
            Toast.makeText(this,"팔로우 했습니다.",Toast.LENGTH_SHORT)
        }
    }
}