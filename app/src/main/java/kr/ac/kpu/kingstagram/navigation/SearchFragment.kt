package kr.ac.kpu.kingstagram.navigation

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_search_profile.*
import kotlinx.android.synthetic.main.fragment_search.view.*
import kotlinx.android.synthetic.main.view_search.view.*
import kr.ac.kpu.kingstagram.R
import kr.ac.kpu.kingstagram.SearchProfileActivity
import kr.ac.kpu.kingstagram.SearchView


class SearchFragment : Fragment() {
    var firestore: FirebaseFirestore? = null
    var textChanged: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = LayoutInflater.from(activity).inflate(R.layout.fragment_search, container, false)
        firestore = FirebaseFirestore.getInstance()

        view.recycleView_search.layoutManager = LinearLayoutManager(activity)
        view.recycleView_search.addItemDecoration(
            DividerItemDecoration(this.context, LinearLayoutManager.VERTICAL)
        )


        view.edit_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                textChanged = view.edit_search.text.toString()
                view.recycleView_search.adapter = SearchViewAdapter(textChanged)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })


        view.edit_search.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
                view.img_search.setImageResource(R.drawable.ic_baseline_keyboard_backspace_24)
            else
                view.img_search.setImageResource(R.drawable.ic_baseline_search_24)
        }

        view.img_search.setOnClickListener {
            if (view.edit_search.isFocused) {
                view.edit_search.clearFocus()
                view.img_search.setImageResource(R.drawable.ic_baseline_search_24)
                val imm =
                    this.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.edit_search.windowToken, 0)
            } else {
                view.edit_search.requestFocus()
                view.img_search.setImageResource(R.drawable.ic_baseline_keyboard_backspace_24)
                val imm = this.context?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.toggleSoftInput(
                    InputMethodManager.SHOW_FORCED,
                    InputMethodManager.HIDE_IMPLICIT_ONLY
                )
            }
            //view.edit_search.isFocusable = view.edit_search.isFocusable != true

        }


        return view
    }

    inner class SearchViewAdapter(text: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var contentList: ArrayList<SearchView> = arrayListOf()
        var contentUidList: ArrayList<String> = arrayListOf()

        init {
            val user = Firebase.auth.currentUser
            firestore?.collection("users")
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    contentList.clear()
                    contentUidList.clear()
                    for (snapshot in querySnapshot!!.documents) {
                        if(snapshot.id != user?.uid) {
                            val imgUrl: String = snapshot.data?.get("imageUrl") as String
                            val name: String = snapshot.data?.get("name") as String
                            val nickname: String = snapshot.data?.get("nickName") as String
                            if (text != "") {
                                if (nickname.contains(text)) {
                                    contentList.add(SearchView(imgUrl, nickname, name))
                                    contentUidList.add(snapshot.id)
                                }
                            }
                        }
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

            if (contentList!![p1].profileUrl == "") {
                viewholder.img_search_profile.setImageResource(R.drawable.account_iv_profile)

            } else {
                Glide.with(p0.itemView.context).load(contentList!![p1].profileUrl)
                    //.apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
                    .into(viewholder.img_search_profile)
                //account_iv_profile.background = ShapeDrawable(OvalShape())
                //account_iv_profile.clipToOutline = true
            }

            viewholder.name_search.text = contentList!![p1].name


            viewholder.setOnClickListener {
                var intent = Intent(context, SearchProfileActivity::class.java)
                intent.putExtra("uid", contentUidList!![p1])
                //intent.putExtra("comments", contentList!![p1].comments as HashMap<String, String>)
                startActivity(intent)
            }

        }

    }
}