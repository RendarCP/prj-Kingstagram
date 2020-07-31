package kr.ac.kpu.kingstagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {
    private lateinit var inputDataField: Array<EditText>
    private lateinit var textInputLayoutArray: Array<TextInputLayout>
    private val RC_SIGN_IN = 9001 // Activity의 requestCode지정
    private val TAG = MainActivity::class.java.simpleName

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        firebaseAuth = Firebase.auth

        Enjoy.setOnClickListener {
            if(Password.text.toString() == CheckPassword.text.toString()){
                Toast.makeText(this, "비밀번호가 맞습니다", Toast.LENGTH_SHORT).show()

                firebaseAuth.createUserWithEmailAndPassword(EnterEmail.text.toString(), Password.text.toString())
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "createUserWithEmail:success")
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)

                        }
                    }

                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "비밀번호가 틀립니다", Toast.LENGTH_SHORT).show()
            }
        }
    }
}