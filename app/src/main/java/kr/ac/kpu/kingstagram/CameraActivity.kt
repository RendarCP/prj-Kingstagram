package kr.ac.kpu.kingstagram

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Camera
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_camera.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class CameraActivity : AppCompatActivity() {
    companion object {
        var imageUrl: String? = null
    }

    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private var filePath: Uri? = null
    private var CAMERA_FACING = Camera.CameraInfo.CAMERA_FACING_BACK
    private val mCamera: Camera? = null
    private var myCameraPreview: MyCameraPreview? = null
    private val TAG = "MyTag"
    private val PERMISSIONS_REQUEST_CODE = 100
    private val IMAGE_CAPTURE_CODE = 1001
    private val IMAGE_PICK_CODE = 1000;

    //Permission code
    private val PERMISSION_CODE = 1001;
    private val filepath = null;
    var image_uri: Uri? = null;
    private val REQUIRED_PERMISSIONS =
        arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        // 사진촬영
        take_photo.setOnClickListener {
            val values = ContentValues()
            values.put(MediaStore.Images.Media.TITLE, "New Picture")
            values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
            image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

            //camera intent
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
            startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
        }
        // 갤러리 사진 선택
        take_gallery.setOnClickListener {
            //pickImageFromGallery()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED
                ) {
                    //permission denied
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE);
                    //show popup to request runtime permission
                    requestPermissions(permissions, PERMISSION_CODE);
                } else {
                    //permission already granted
                    pickImageFromGallery();
                }
            } else {
                //system OS is < Marshmallow
                pickImageFromGallery();
            }
        }

        btnSave.setOnClickListener {
            //Toast.makeText(this, "${image_uri}", Toast.LENGTH_SHORT).show()
            if (image_uri != null){
                savePost(image_uri)
            }
            if(filePath != null){
                savePost(filePath)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // OS가 Marshmallow 이상일 경우 권한체크를 해야 합니다.

            val permissionCheckCamera =
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            val permissionCheckStorage =
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (permissionCheckCamera == PackageManager.PERMISSION_GRANTED
                && permissionCheckStorage == PackageManager.PERMISSION_GRANTED
            ) {
                // 권한 있음
                Log.d(TAG, "권한 이미 있음")
                startCamera()
            } else {
                // 권한 없음
                Log.d(TAG, "권한 없음")
                ActivityCompat.requestPermissions(
                    this,
                    REQUIRED_PERMISSIONS,
                    PERMISSIONS_REQUEST_CODE
                )
            }

        } else {
            // OS가 Marshmallow 이전일 경우 권한체크를 하지 않는다.
            Log.d("MyTag", "마시멜로 버전 이하로 권한 이미 있음")
            startCamera()

        }
    }
    data class PostSchema(
        val contents: String? = null,
        val tag: ArrayList<String> = arrayListOf(),
        val like: Int = 0,
        val userId: String? = null,
        //val kings: Map<String, Boolean> = HashMap(),
        val kings: String? = null,
        val imageUrl: String? = null,
        val date: String? = null,
        //val commnets: Map<String, String> = HashMap()
        val comments: String? = null
    )

    fun savePost(filepath:Uri?){
        Toast.makeText(this, "버튼 클릭됨", Toast.LENGTH_SHORT).show()
        val db = FirebaseFirestore.getInstance()
        if(filepath != null){
            loading()
            val imageUrl = filepath
            val ref = storageReference?.child("uploads/" + UUID.randomUUID().toString())
            val uploadTask = ref?.putFile(filePath!!)
            val urlTask = uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation ref.downloadUrl
            })?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    //Toast.makeText(this, "${downloadUri}", Toast.LENGTH_SHORT).show()
                    if(downloadUri != null){
                        val post = PostSchema(editContents.text.toString(), arrayListOf(editTag.text.toString()), 0,
                            "${Firebase.auth?.currentUser}", null ,"${downloadUri}", "${LocalDate.now()}",null)
                        db.collection("posts")
                            .add(post)
                            .addOnSuccessListener {
                                Toast.makeText(this, "db성공", Toast.LENGTH_SHORT).show()
                                val intent= Intent(this,MainActivity::class.java)
                                startActivity(intent);
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "db실패", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toast.makeText(this, "업로드 실패", Toast.LENGTH_SHORT).show()
                }
            }?.addOnFailureListener{

            }
        }else{
            Toast.makeText(this, "이미지를 업로드 해주세요!", Toast.LENGTH_SHORT).show()
        }

    }
    fun loading() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Kingstagram")
        progressDialog.setMessage("업로드중입니다..")
        progressDialog.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // grantResults[0] 거부 -> -1
        // grantResults[0] 허용 -> 0 (PackageManager.PERMISSION_GRANTED)

        Log.d(TAG, "requestCode : $requestCode, grantResults size : ${grantResults.size}")

        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            var check_result = true
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false
                    break
                }
            }
            if (check_result) {
                startCamera()
            } else {
                Log.e(TAG, "권한 거부")
            }

        }

    }

    private fun startCamera() {
        Log.e(TAG, "startCamera")
        // Create our Preview view and set it as the content of our activity.
//        myCameraPreview = MyCameraPreview(this, CAMERA_FACING)
//////        cameraPreview.addView(myCameraPreview)
//////        previewTest.bringToFront()
    }


    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
        //cameraPreview.setBackgroundColor(Color.parseColor("#000000"))
        previewTest.bringToFront()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            previewTest.setImageURI(image_uri)
        }
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            //previewTest.setImageURI(data?.data);
            filePath = data?.data;
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
            previewTest.setImageBitmap(bitmap)
        }
    }
}



