package kr.ac.kpu.kingstagram.navigation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_comments.*
import kotlinx.android.synthetic.main.activity_edit__profile.*
import kotlinx.android.synthetic.main.activity_user.*
import kotlinx.android.synthetic.main.activity_user.view.*
import kotlinx.android.synthetic.main.activity_user.view.btnProfile
import kotlinx.android.synthetic.main.cardview_user.view.*
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kotlinx.android.synthetic.main.fragment_user.view.*
import kotlinx.android.synthetic.main.view_comments.view.*
import kotlinx.android.synthetic.main.view_search.view.*
import kr.ac.kpu.kingstagram.CommentView
import kr.ac.kpu.kingstagram.EditProfileActivity
import kr.ac.kpu.kingstagram.PostView
import kr.ac.kpu.kingstagram.R

class UserFragment : Fragment() {
    var fragmentView: View? = null
    var firestore: FirebaseFirestore? = null
    var uid: String? = null
    var auth: FirebaseAuth? = null
    var currentUserUid: String? = null
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
            .addOnFailureListener { e ->
                //Toast.makeText(this.context, "${e.message}", Toast.LENGTH_LONG).show()
            }
        val view = LayoutInflater.from(activity).inflate(R.layout.fragment_user, container, false)


        firestore = FirebaseFirestore.getInstance()
        var layoutManager = view.recycleView_profile.layoutManager
        layoutManager  = GridLayoutManager(activity,8 )
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                when (position % 8) {
                    0, 1, 2 -> return 2

                }
                return 2
            }
        }
        view.recycleView_profile.layoutManager = layoutManager
        view.recycleView_profile.adapter = ProfileViewAdapter()



                view.btnProfile.setOnClickListener {
            val intent = Intent(this.context, EditProfileActivity::class.java)
            startActivity(intent)
        }
        return view
    }

    inner class ProfileViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var contentList: java.util.ArrayList<ProfileView> = arrayListOf()
        var contentUidList: java.util.ArrayList<String> = arrayListOf()

        init {
            /*for (i in comments?.keys!!) {
                contentList.add(CommentView(i, comments!!.getValue(i)))
                contentUidList.add(i)
            }*/
            firestore?.collection("posts")?.whereEqualTo("uid", "${Firebase.auth.currentUser?.uid}")//?.document("$postUid")?.collection("comments")
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    Log.d("Querysnap", "${querySnapshot?.documents}")
                    contentList.clear()
                    contentUidList.clear()
                    //Log.d("MyTag","${querySnapshot!!.documents}")
                    //println(querySnapshot.documents.toString())
                    for (snapshot in querySnapshot!!.documents) {
                        Log.w("Comment TAG"," ${snapshot?.id}")

                            var profileImgUrl = snapshot.data?.get("imageUrl") as String

                                contentList.add(ProfileView(profileImgUrl))
                                contentUidList.add(snapshot.id)

                    }
                    notifyDataSetChanged()
                }

        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(p0.context).inflate(R.layout.cardview_user, p0, false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {
            return contentList.size
        }

        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            var viewholder = (p0 as CustomViewHolder).itemView

            Log.w("Comment TAG"," ${contentList!![p1]}")
            Glide.with(p0.itemView.context).load(contentList!![p1].imageUrl).override(1000, 1000)
                .into(viewholder.imageView)

        }

    }

    data class ProfileView(var imageUrl: String) {

    }

}







