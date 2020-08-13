package kr.ac.kpu.kingstagram

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.util.Linkify
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var mContext: Context


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth
        mContext = this

        login_btn.setOnClickListener {
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Kingstagram")
            progressDialog.setMessage("Loading...")
            progressDialog.show()

            et_id.setBackgroundResource(R.drawable.main_edittext)
            et_pw.setBackgroundResource(R.drawable.main_edittext)
            tv_error_id.text = ""
            val email = et_id.text.toString()
            val password = et_pw.text.toString()

            if (email == "" || password == "") {
                et_id.setBackgroundResource(R.drawable.red_edittext)
                et_pw.setBackgroundResource(R.drawable.red_edittext)
                progressDialog.dismiss()
                tv_error_id.text = "아이디 및 비밀번호를 입력해주세요"
            } else {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("login", "signInWithEmail:success")
                            //Toast.makeText(baseContext, "Authentication success.", Toast.LENGTH_SHORT).show()
                            val user = auth.currentUser
                            updateUI(user)
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("login", "signInWithEmail:failure", task.exception)
                            //Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                            progressDialog.dismiss()
                            updateFail(null)
                            // ...
                        }

                        // ...
                    }

            }
        }
        var intent = Intent(this, SignUpActivity::class.java)
        content_signUp.setOnClickListener {
            //content_signUp.paintFlags
            startActivity(intent)
        }


    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        //updateUI(currentUser)
    }

    fun updateUI(user: FirebaseUser?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("user", user)
        startActivity(intent)
    }

    fun updateFail(nothing: Nothing?) {
        et_id.setBackgroundResource(R.drawable.red_edittext)
        et_pw.setBackgroundResource(R.drawable.red_edittext)
        tv_error_id.text = "아이디 및 비밀번호를 확인해주세요"
    }

}