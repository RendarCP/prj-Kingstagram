package kr.ac.kpu.kingstagram

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_up.*



class SignUpActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName
    private lateinit var auth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = Firebase.auth


        Enjoy.setOnClickListener {
            var password = edit_password.text.toString()
            var checkPassword = edit_checkPassword.text.toString()
            var nickName = edit_nickName.text.toString()
            var name = edit_name.text.toString()
            var email = edit_email.text.toString()
            var nickNameTrigger = true
            edit_password.setBackgroundResource(R.drawable.main_edittext)
            edit_checkPassword.setBackgroundResource(R.drawable.main_edittext)
            edit_nickName.setBackgroundResource(R.drawable.main_edittext)
            edit_email.setBackgroundResource(R.drawable.main_edittext)
            edit_name.setBackgroundResource(R.drawable.main_edittext)

            val db = FirebaseFirestore.getInstance()

            if (TextUtils.isEmpty(password) || TextUtils.isEmpty(checkPassword) ||
                TextUtils.isEmpty(nickName) || TextUtils.isEmpty(email)
                || TextUtils.isEmpty(name)
            ) {
                if(TextUtils.isEmpty(password))
                    edit_password.setBackgroundResource(R.drawable.red_edittext)
                if(TextUtils.isEmpty(checkPassword))
                    edit_checkPassword.setBackgroundResource(R.drawable.red_edittext)
                if(TextUtils.isEmpty(nickName))
                    edit_nickName.setBackgroundResource(R.drawable.red_edittext)
                if(TextUtils.isEmpty(email))
                    edit_email.setBackgroundResource(R.drawable.red_edittext)
                if(TextUtils.isEmpty(name))
                    edit_name.setBackgroundResource(R.drawable.red_edittext)
                errorText.text = "정보를 올바르게 입력해주세요."
                //Toast.makeText(this, "정보를 올바르게 입력해주세요. ", Toast.LENGTH_SHORT).show()

            }

            else if (password == checkPassword) {
                db.collection("users")
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            if (document.data["nickName"] == nickName){
                                errorText.text = "이미 존재하는 닉네임입니다."
                                edit_nickName.setBackgroundResource(R.drawable.red_edittext)
                                nickNameTrigger = false
                            }
                           // Toast.makeText(this, "${document.data["nickName"]}", Toast.LENGTH_LONG).show()
                           Log.d(TAG, "${document.id} => ${document.data}")
                        }
                        if (nickNameTrigger) {
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener() { task ->
                                    if (task.isSuccessful) {
                                        val userUid = auth.currentUser?.uid.toString()
                                        saveData(userUid)
                                        Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this, LoginActivity::class.java)
                                        startActivity(intent)

                                    }

                                }
                                .addOnFailureListener { exception ->
                                    errorText.text = "이미 존재하는 이메일입니다."
                                    edit_email.setBackgroundResource(R.drawable.red_edittext)
                                    //Toast.makeText(this, "이미 존재하는 이메일입니다.", Toast.LENGTH_LONG).show()
                                }
                        }
                    }
                    .addOnFailureListener { exception ->
                       // Log.d(TAG, "Error getting documents: ", exception)
                    }
            } else {
                errorText.text = "비밀번호가 틀립니다."
                edit_checkPassword.setBackgroundResource(R.drawable.red_edittext)
                //Toast.makeText(this, "비밀번호가 틀립니다", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun saveData(uid: String) {
        var username = edit_name.text.toString()
        var useremail = edit_email.text.toString()
        var userNickname = edit_nickName.text.toString()
        var imageUrl = ""
        var postnumber = 0
        var follower: ArrayList<String> = arrayListOf()
        var following: ArrayList<String> = arrayListOf()


        val user = user("${uid}", useremail, username, userNickname, postnumber, imageUrl,
            follower, following)
        db.collection("users").document("${uid}").set(user)
            .addOnSuccessListener {
                Toast.makeText(this, "db성공", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "db실패", Toast.LENGTH_SHORT).show()
            }
    }

    data class user(
        val userId: String? = null,
        val email: String? = null,
        val name: String? = null,
        val nickName: String? = null,
        val postNumber: Int = 0,
        val imageUrl: String? = null,
        val follower: ArrayList<String>,
        val following: ArrayList<String>
    )
}