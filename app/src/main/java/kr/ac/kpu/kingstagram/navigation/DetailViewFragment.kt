package kr.ac.kpu.kingstagram.navigation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.cardview_detail.view.*
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kr.ac.kpu.kingstagram.PostView
import kr.ac.kpu.kingstagram.R

class DetailViewFragment : Fragment() {
    var firestore: FirebaseFirestore? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = LayoutInflater.from(activity).inflate(R.layout.fragment_detail, container, false)
        firestore = FirebaseFirestore.getInstance()

        view.recycleView_detail.layoutManager = LinearLayoutManager(activity)
        view.recycleView_detail.adapter = PostViewAdapter()

        /*
        val user = Firebase.auth.currentUser
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document("${user?.uid}")
            .get()
            .addOnSuccessListener { result ->
                //if (result.id == "name") {
                    Toast.makeText(this.context,"${result.data?.get("name")}",Toast.LENGTH_LONG)
                    view.textView01.text = "${result.data?.get("name")}"
                //}
                //if (result.id == "nickname") {
                    view.textView02.text = "${result.data?.get("nickname")}"


                //Log.w("TAG", "${result}")
            }
            .addOnFailureListener { exception ->
                //Log.w(TAG, "Error getting documents.", exception)
            }*/
        return view
    }
    inner class PostViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var contentList : ArrayList<PostView> = arrayListOf()
        var contentUidList : ArrayList<String> = arrayListOf()

        init {
            firestore?.collection("posts")
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    contentList.clear()
                    contentUidList.clear()
                    for(snapshot in querySnapshot!!.documents){
                        var item = snapshot.toObject(PostView::class.java)
                        contentList.add(item!!)
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
            var view = LayoutInflater.from(p0.context).inflate(R.layout.cardview_detail, p0, false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {
            return contentList.size
        }

        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            var viewholder = (p0 as CustomViewHolder).itemView

            viewholder.card_view_detail_titleView.text = contentList!![p1].userId

            Glide.with(p0.itemView.context).load(contentList!![p1].imageUrl).into(viewholder.card_view_detail_imageView)

            viewholder.card_view_detail_contentView.text = contentList!![p1].content

            viewholder.card_view_detail_kingView.text = "King  " + contentList!![p1].like + "개"


        }

    }
}
