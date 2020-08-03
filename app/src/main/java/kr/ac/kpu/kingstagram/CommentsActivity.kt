package kr.ac.kpu.kingstagram

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.cardview_detail.view.*
import kotlinx.android.synthetic.main.view_comments.view.*

class CommentsActivity : AppCompatActivity() {
    var firestore: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)
    }

  /*  inner class CommentsViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var contentList : ArrayList<PostView> = arrayListOf()
        var contentUidList : ArrayList<String> = arrayListOf()

        init {
            firestore?.collection("posts")
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    contentList.clear()
                    contentUidList.clear()
                    Log.d("MyTag","${querySnapshot!!.documents}")
                    println(querySnapshot.documents.toString())
                    for(snapshot in querySnapshot!!.documents){
                        Log.d("MyTag","${snapshot.data}")
                        println(snapshot.data.toString())
                        var item = snapshot.toObject(PostView::class.java)
                        contentList.add(item!!)
                        Log.d("MyTag","$item")
                        println(item.toString())
                        /*var content: String = "${snapshot.data?.get("content")}"
                        var imageUrl: String = "${snapshot.data?.get("imageUrl")}"
                        var kingcount: Int = "${snapshot.data?.get("kingcount")}".toInt()
                        var uid : String = snapshot.id
                        var userId: String = "${snapshot.data?.get("userId")}"
                        contentList.add(PostView(content, imageUrl, kingcount, uid, userId))*/
                        contentUidList.add(snapshot.id)
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

            viewholder.nickname_comments.text


            /*
            viewholder.card_view_detail_titleView.text = contentList!![p1].userId

            Glide.with(p0.itemView.context).load(contentList!![p1].imageUrl).into(viewholder.card_view_detail_imageView)

            viewholder.card_view_detail_contentView.text = contentList!![p1].content

            viewholder.card_view_detail_kingView.text = "King  " + contentList!![p1].like + "개"
             */


        }

    }*/

}