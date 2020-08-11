package kr.ac.kpu.kingstagram

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_search_profile.*


class SearchProfileActivity : AppCompatActivity() {
    var firestore: FirebaseFirestore? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_profile)

        val profileUid = intent.getStringExtra("uid")

        firestore = FirebaseFirestore.getInstance()
        val user = Firebase.auth.currentUser
        val db = FirebaseFirestore.getInstance()
        var userFollowing: ArrayList<String> = arrayListOf()
        var profileFollower: ArrayList<String> = arrayListOf()
        db.collection("users").document("$profileUid")
            .get()
            .addOnSuccessListener { result ->

                //Toast.makeText(this.context,"${result.data?.get("name")}",Toast.LENGTH_LONG)
                if (result.data?.get("imageUrl") == "")
                    account_iv_profile.setImageResource(R.drawable.account_iv_profile)
                else {
                    Glide.with(this).load(result.data?.get("imageUrl"))
                        .into(account_iv_profile)
                }

                account_tv_post_count.text = "${result.data?.get("postNumber")}"
                val follower: ArrayList<String> = result.data?.get("follower") as ArrayList<String>
                profileFollower = follower
                account_tv_follower_count.text = "${follower.size}"
                val following: ArrayList<String> =
                    result.data?.get("following") as ArrayList<String>
                account_tv_following_count.text = "${following.size}"
                name_profile.text = "${result.data?.get("name")}"

                for (i in follower) {
                    if (i == user?.uid)
                        account_btn_follow.text = "Following"
                    else
                        account_btn_follow.text = "Follow"
                }

                //Log.w("TAG", "${result}")
            }
            .addOnFailureListener { exception ->
                //Log.w(TAG, "Error getting documents.", exception)
            }

        //recycleView_comments.layoutManager = LinearLayoutManager(this)
        //recycleView_comments.adapter = CommentsViewAdapter()

        account_btn_follow.setOnClickListener {
            if (account_btn_follow.text == "Follow") {
                val followingData = hashMapOf(
                    "following" to arrayListOf<String>(profileUid)
                )
                val followerData = hashMapOf(
                    "follower" to arrayListOf<String>("${user?.uid}")
                )
                db.collection("users").document("${user?.uid}")
                    .set(followingData, SetOptions.merge())
                db.collection("users").document("$profileUid")
                    .set(followerData, SetOptions.merge())
                db.collection("users").document("$profileUid")
                    .get()
                    .addOnSuccessListener { result ->
                        val follower: ArrayList<String> = result.data?.get("follower") as ArrayList<String>
                        account_tv_follower_count.text = "${follower.size}"
                    }
                    .addOnFailureListener { exception ->
                        //Log.w(TAG, "Error getting documents.", exception)
                    }
                Toast.makeText(this, "${name_profile.text} 님을 팔로우 했습니다.", Toast.LENGTH_SHORT).show()
                account_btn_follow.text = "Following"
            } else {
                db.collection("users").document("${user?.uid}")
                    .get()
                    .addOnSuccessListener { result ->
                        userFollowing = result.data?.get("following") as ArrayList<String>
                        //Log.w("TAG", "${result}")
                    }
                    .addOnFailureListener { exception ->
                        //Log.w(TAG, "Error getting documents.", exception)
                    }
                userFollowing.remove(profileUid)
                profileFollower.remove(user?.uid)
                val followingData = hashMapOf(
                    "following" to userFollowing
                )
                val followerData = hashMapOf(
                    "follower" to profileFollower
                )
                db.collection("users").document("${user?.uid}")
                    .set(followingData, SetOptions.merge())
                db.collection("users").document("$profileUid")
                    .set(followerData, SetOptions.merge())
                db.collection("users").document("$profileUid")
                    .get()
                    .addOnSuccessListener { result ->
                        val follower: ArrayList<String> = result.data?.get("follower") as ArrayList<String>
                        account_tv_follower_count.text = "${follower.size}"
                    }
                    .addOnFailureListener { exception ->
                        //Log.w(TAG, "Error getting documents.", exception)
                    }
                Toast.makeText(this, "${name_profile.text} 님을 팔로우 취소 했습니다.", Toast.LENGTH_SHORT)
                    .show()
                account_btn_follow.text = "Follow"

            }
        }
    }
}