package kr.ac.kpu.kingstagram

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_comments.*
import kotlinx.android.synthetic.main.fragment_detail.view.*
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
            var content = comments_view_detail_editView.text.toString()
            val data = hashMapOf(
                "comments" to mapOf(nickname to content)
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
            /*for (i in comments?.keys!!) {
                contentList.add(CommentView(i, comments!!.getValue(i)))
                contentUidList.add(i)
            }*/
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
                            //var item = snapshot?.toObject(CommentView::class.java)
                            //contentList.add(item!!)
                            //Log.d("MyTag", "$item")
                            //println(item.toString())
                            /*var content: String = "${snapshot.data?.get("content")}"
                        var imageUrl: String = "${snapshot.data?.get("imageUrl")}"
                        var kingcount: Int = "${snapshot.data?.get("kingcount")}".toInt()
                        var uid : String = snapshot.id
                        var userId: String = "${snapshot.data?.get("userId")}"
                        contentList.add(PostView(content, imageUrl, kingcount, uid, userId))*/
                        //contentUidList.add(snapshot.id)


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

            viewholder.nickname_comments.text = contentList!![p1].nickname

            viewholder.content_comments.text = contentList!![p1].comments


            /*
            viewholder.card_view_detail_titleView.text = contentList!![p1].userId

            Glide.with(p0.itemView.context).load(contentList!![p1].imageUrl).into(viewholder.card_view_detail_imageView)

            viewholder.card_view_detail_contentView.text = contentList!![p1].content

            viewholder.card_view_detail_kingView.text = "King  " + contentList!![p1].like + "개"
             */


        }

    }

}