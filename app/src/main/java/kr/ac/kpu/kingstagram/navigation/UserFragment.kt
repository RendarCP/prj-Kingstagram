package kr.ac.kpu.kingstagram.navigation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_comments.*
import kotlinx.android.synthetic.main.activity_edit__profile.*
import kotlinx.android.synthetic.main.activity_user.*
import kotlinx.android.synthetic.main.activity_user.view.*
import kr.ac.kpu.kingstagram.EditProfileActivity
import kr.ac.kpu.kingstagram.PostView
import kr.ac.kpu.kingstagram.R

class UserFragment : Fragment() {
    var fragmentView : View? = null
    var firestore : FirebaseFirestore? = null
    var uid : String? = null
    var auth : FirebaseAuth? = null
    var currentUserUid : String? = null
    private var storageReference: StorageReference? = null
    private val user = Firebase.auth.currentUser
    var db = FirebaseFirestore.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        storageReference = FirebaseStorage.getInstance().reference
        db.collection("users").document("${user?.uid}")
            .get()
            .addOnSuccessListener { result ->
                var follower = result.data?.get("follower") as ArrayList<String>
                var following = result.data?.get("following") as ArrayList<String>
                account_tv_follower_count.text = "${follower.size}"
                account_tv_following_count.text = "${following.size}"
                Glide.with(this).load("${result.data?.get("imageUrl")}").into(btnProfile)
            }
            .addOnFailureListener { exception ->
                //Toast.makeText(this.context, "데이터실패", Toast.LENGTH_SHORT).show()
                //Log.w(TAG, "Error getting documents.", exception)
            }
        db.collection("posts").whereEqualTo("uid", "${user?.uid}")
            .get()
            .addOnSuccessListener { result ->
                account_tv_post_count.setText("${result.documents.size}")
                //Toast.makeText(this.context, "${result.documents.size}", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener{e ->
                //Toast.makeText(this.context, "${e.message}", Toast.LENGTH_LONG).show()
            }
        val view = LayoutInflater.from(activity).inflate(R.layout.fragment_user,container,false)
        view.btnProfile.setOnClickListener {
            val intent = Intent(this.context,EditProfileActivity::class.java)
            startActivity(intent)
        }
        return view
    }

}

