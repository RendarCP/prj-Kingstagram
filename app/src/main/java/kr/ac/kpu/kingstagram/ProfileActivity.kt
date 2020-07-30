package kr.ac.kpu.kingstagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_edit__profile.*

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile2)

        account_iv_profile.setOnClickListener {
            val nextIntent = Intent(this, EditProfileActivity::class.java)
            startActivity(nextIntent)
        }

    }
}