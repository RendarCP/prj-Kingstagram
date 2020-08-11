package kr.ac.kpu.kingstagram

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Camera
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.activity_edit__profile.*
import kotlinx.android.synthetic.main.activity_edit__profile.view.*
import java.util.*


class EditProfileActivity : AppCompatActivity() {
    private val PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 1001
    var image_uri: Uri? = null
    val REQUEST_CODE_GET_IMAGE = 101
    private lateinit var database: DatabaseReference
    var db = FirebaseFirestore.getInstance()
    private val TAG = "Firestore"
    private var storageReference: StorageReference? = null
    private var firebaseStore: FirebaseStorage? = null
    private var filePath: Uri? = null
    private var CAMERA_FACING = Camera.CameraInfo.CAMERA_FACING_BACK
    private val mCamera: Camera? = null
    private var myCameraPreview: MyCameraPreview? = null
    private val PERMISSIONS_REQUEST_CODE = 100
    private val IMAGE_PICK_CODE = 1000;
    private val filepath = null;
    private val REQUIRED_PERMISSIONS =
        arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val user = Firebase.auth.currentUser


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit__profile)
        storageReference = FirebaseStorage.getInstance().reference
        //Toast.makeText(this, "${user?.uid}", Toast.LENGTH_SHORT).show()

        //val user = Firebase.auth.currentUser
        db.collection("users").document("${user?.uid}")
            .get()
            .addOnSuccessListener { result ->
                edittextPersonName.setText("${result.data?.get("name")}")
                editTextNickName.setText("${result.data?.get("nickName")}")
                Glide.with(this).load("${result.data?.get("imageUrl")}").into(btnProfile1)

            }
            .addOnFailureListener { exception ->
                //Log.w(TAG, "Error getting documents.", exception)
            }
        button.setOnClickListener {
            showDialog()
        }
        profile_button.setOnClickListener {
            Toast.makeText(this, "클릭", Toast.LENGTH_SHORT).show()
            if (image_uri != null){
                updateData(image_uri)
            }
            if(filePath != null){
                updateData(filePath)
            }
            else{
                updateData(null)
            }

            //updateData()
            //Toast.makeText(this, "${edittextPersonName}", Toast.LENGTH_SHORT).show()
        }
    }

    fun testData(filepath:Uri?) {
        Toast.makeText(this, "함수안에 도착", Toast.LENGTH_SHORT).show()
        if (filepath != null) {
            Toast.makeText(this, "업로드시작", Toast.LENGTH_SHORT).show()
            val ref = storageReference?.child("profile/" + UUID.randomUUID().toString())
            val uploadTask = ref?.putFile(filepath!!)
            val urlTask =
                uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    return@Continuation ref.downloadUrl
                })?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        Toast.makeText(this, "${downloadUri}", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(this, "업로드 실패", Toast.LENGTH_SHORT).show()
                    }
                }?.addOnFailureListener { exception ->
                    Toast.makeText(this, "${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }


    private fun updateData(filepath: Uri?) {
        val db = FirebaseFirestore.getInstance()
        if (filepath != null) {
            val imageUrl = filepath
            val ref = storageReference?.child("profile/" + UUID.randomUUID().toString())
            val uploadTask = ref?.putFile(filepath!!)
            val urlTask =
                uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    return@Continuation ref.downloadUrl
                })?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        if(downloadUri != null){
                            Toast.makeText(this, "${downloadUri}", Toast.LENGTH_SHORT).show()
                            db.collection("users").document("${user?.uid}")
                                .update("imageUrl", downloadUri.toString())
                                .addOnSuccessListener {result ->
                                    Toast.makeText(this, "업로드 성공", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    //Log.w(TAG, "Error getting documents.", exception)
                                    Toast.makeText(this, "업로드 실패", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                }
        }
        else{
            db.collection("users").document("${user?.uid}")
                .update("name", edittextPersonName.text.toString() ,
                    "nickName", editTextNickName.text.toString())
                .addOnSuccessListener {result ->
                    Toast.makeText(this, "사용자 정보 성공", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    //Log.w(TAG, "Error getting documents.", exception)
                    Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }













    private fun showDialog() {
        val arrayOf = arrayOf("엘범에서 선택", "사진촬영", "프로필 삭제")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("사진 편집")
            .setItems(arrayOf, DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    1 -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (checkSelfPermission(Manifest.permission.CAMERA)
                                == PackageManager.PERMISSION_DENIED ||
                                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_DENIED
                            ) {

                                val permission = arrayOf(
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                )

                                requestPermissions(permission, PERMISSION_CODE)
                            } else {

                                openCamera()
                            }
                        } else {

                            openCamera()
                        }

                    }


                    0 -> {

                        val pickImageFileIntent = Intent()
                        pickImageFileIntent.type = "image/*"
                        pickImageFileIntent.action = Intent.ACTION_GET_CONTENT

                        val pickGalleryImageIntent = Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        )

                        val pickTitle =
                            "Capture from camera or Select from gallery the Profile photo"
                        val chooserIntent = Intent.createChooser(pickImageFileIntent, pickTitle)
                        chooserIntent.putExtra(
                            Intent.EXTRA_INITIAL_INTENTS,
                            arrayOf(pickGalleryImageIntent)
                        )
                        startActivityForResult(chooserIntent, REQUEST_CODE_GET_IMAGE)
                    }

                    2 -> {
                        btnProfile1.setImageResource(0)

                    }
                }
            }
            )

        val dialog = builder.create()

        dialog.show()

    }


    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        image_uri =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        //camera intent
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        //called when user presses ALLOW or DENY from Permission Request Popup
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    //permission from popup was granted
                    openCamera()
                } else {
                    //permission from popup was denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //called when image was captured from camera intent
        if (resultCode == Activity.RESULT_OK) {
            //set image captured to image view
            btnProfile1.setImageURI(image_uri)
        }
        if (requestCode == REQUEST_CODE_GET_IMAGE) {
            filePath = data?.data;
            btnProfile1.setImageURI(filePath);
        }
    }
}


