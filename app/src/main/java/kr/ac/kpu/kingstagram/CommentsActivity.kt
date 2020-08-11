package kr.ac.kpu.kingstagram

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_comments.*
import kotlinx.android.synthetic.main.activity_search_profile.*
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kotlinx.android.synthetic.main.view_comments.*
import kotlinx.android.synthetic.main.view_comments.view.*
import java.util.*
import kotlin.collections.HashMap

class CommentsActivity : AppCompatActivity() {
    var firestore: FirebaseFirestore? = null
    var postUid: String? = null
    var comments: HashMap<String, String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        postUid = intent.getStringExtra("uid")
        var userId = intent.getStringExtra("userId")
        var content = intent.getStringExtra("content")
        //comments = intent.getSerializableExtra("comments") as HashMap<String, String>?

        firestore = FirebaseFirestore.getInstance()
        val user = Firebase.auth.currentUser
        val db = FirebaseFirestore.getInstance()

        comments_view_detail_titleView.text = "$userId"
        comments_view_detail_contentView.text = "$content"

        var nickname = ""
        db.collection("users").document("${user?.uid}")
            .get()
            .addOnSuccessListener { result ->

                //Toast.makeText(this.context,"${result.data?.get("name")}",Toast.LENGTH_LONG)
                nickname = "${result.data?.get("nickname")}"
                //Log.w("TAG", "${result}")
            }
            .addOnFailureListener { exception ->
                //Log.w(TAG, "Error getting documents.", exception)
            }

        recycleView_comments.layoutManager = LinearLayoutManager(this)
        recycleView_comments.adapter = CommentsViewAdapter()

        comments_view_detail_editBtn.setOnClickListener {
            val uid = user?.uid
            var content = comments_view_detail_editView.text.toString()
            val data = hashMapOf(
                "comments" to mapOf(uid to content)
            )
            db.collection("posts").document("$postUid")
                .set(data, SetOptions.merge())
        }

        //PostView(null, "123", "123", 2, null, null, Timestamp(Date()), "123", "123")
    }

    inner class CommentsViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var contentList: ArrayList<CommentView> = arrayListOf()
        var contentUidList: ArrayList<String> = arrayListOf()

        init {
            firestore?.collection("posts")//?.document("$postUid")?.collection("comments")
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    contentList.clear()
                    contentUidList.clear()
                    //Log.d("MyTag","${querySnapshot!!.documents}")
                    //println(querySnapshot.documents.toString())
                    for (snapshot in querySnapshot!!.documents) {
                        Log.w("Comment TAG"," ${snapshot?.id}")
                        if(snapshot?.id == postUid){
                            var commentsMap: HashMap<String, String> = snapshot.data?.get("comments") as HashMap<String, String>
                            for (i in commentsMap.keys){
                                contentList.add(CommentView(i, commentsMap.getValue(i)))
                                contentUidList.add(i)
                            }
                        }

                    }
                    notifyDataSetChanged()
                }

        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(p0.context).inflate(R.layout.view_comments, p0, false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {
            return contentList.size
        }

        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            var viewholder = (p0 as CustomViewHolder).itemView
            val db = FirebaseFirestore.getInstance()


            viewholder.content_comments.text = contentList!![p1].comments

            db.collection("users").document("${contentUidList!![p1]}")
                .get()
                .addOnSuccessListener { result ->
                    viewholder.nickname_comments.text = result.data?.get("nickName") as String

                        //Toast.makeText(this.context,"${result.data?.get("name")}",Toast.LENGTH_LONG)
                        if (result.data?.get("imageUrl") != "") {
                            Glide.with(p0.itemView.context).load(result.data?.get("imageUrl") as String)
                                .into(viewholder.img_comments)
                        }


                    //Log.w("TAG", "${result}")
                }
                .addOnFailureListener { exception ->
                    //Log.w(TAG, "Error getting documents.", exception)
                }

        }

    }

}