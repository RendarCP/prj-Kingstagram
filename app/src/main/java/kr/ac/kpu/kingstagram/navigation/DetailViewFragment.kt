package kr.ac.kpu.kingstagram.navigation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kr.ac.kpu.kingstagram.R

class DetailViewFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = LayoutInflater.from(activity).inflate(R.layout.fragment_detail, container, false)
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
            }
        return view
    }
}