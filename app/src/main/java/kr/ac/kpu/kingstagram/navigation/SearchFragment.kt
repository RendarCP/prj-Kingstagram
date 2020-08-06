package kr.ac.kpu.kingstagram.navigation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.cardview_detail.view.*
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.view.*
import kotlinx.android.synthetic.main.view_search.view.*
import kotlinx.android.synthetic.main.view_search.view.img_search
import kr.ac.kpu.kingstagram.*

class SearchFragment : Fragment() {
    var firestore: FirebaseFirestore? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = LayoutInflater.from(activity).inflate(R.layout.fragment_search,container,false)
        firestore = FirebaseFirestore.getInstance()

        view.recycleView_search.layoutManager = LinearLayoutManager(activity)
        view.recycleView_search.addItemDecoration(
            DividerItemDecoration(this.context, LinearLayoutManager.VERTICAL))
        view.recycleView_search.adapter = SearchViewAdapter()

        view.edit_search.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus)
                view.img_search.setImageResource(R.drawable.ic_baseline_keyboard_backspace_24)
            else
                view.img_search.setImageResource(R.drawable.ic_baseline_search_24)
        }
        view.img_search.setOnClickListener {

        }


        return view
    }

    inner class SearchViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var contentList: ArrayList<SearchView> = arrayListOf()
        var contentUidList: ArrayList<String> = arrayListOf()

        init {
            firestore?.collection("users")
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    contentList.clear()
                    contentUidList.clear()
                    for (snapshot in querySnapshot!!.documents) {
                        val imgUrl: String = snapshot.data?.get("imageUrl") as String
                        val name: String = snapshot.data?.get("name") as String
                        val nickname: String = snapshot.data?.get("nickName") as String
                        contentList.add(SearchView(imgUrl,nickname,name))
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
            var view = LayoutInflater.from(p0.context).inflate(R.layout.view_search, p0, false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {
            return contentList.size
        }

        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            var viewholder = (p0 as CustomViewHolder).itemView
            val user = Firebase.auth.currentUser
            val db = FirebaseFirestore.getInstance()

            viewholder.nickname_search.text = contentList!![p1].nickname

            if(contentList!![p1].profile_url == ""){
                viewholder.img_search.setImageResource(R.drawable.account_iv_profile)

            }else {
                Glide.with(p0.itemView.context).load(contentList!![p1].profile_url)
                    .into(viewholder.img_search)
            }

            viewholder.name_search.text = contentList!![p1].name


            /*viewholder.setOnClickListener {
                println("${contentUidList!![p1]}")
                var intent = Intent(context, SearchProfileActivity::class.java)
                intent.putExtra("uid", contentUidList!![p1])
                //intent.putExtra("comments", contentList!![p1].comments as HashMap<String, String>)
                startActivity(intent)

            }*/

        }

    }
}