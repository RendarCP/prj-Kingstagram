package kr.ac.kpu.kingstagram

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Color.parseColor
import android.graphics.Color.toArgb
import android.hardware.Camera
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_camer.*
import java.io.File
import java.lang.Exception
import java.util.*


class CamerActivity : AppCompatActivity() {
    lateinit var storage: FirebaseStorage
    private var CAMERA_FACING = Camera.CameraInfo.CAMERA_FACING_BACK
    private val mCamera: Camera? = null
    private var myCameraPreview: MyCameraPreview? = null
    private val TAG = "MyTag"
    private val PERMISSIONS_REQUEST_CODE = 100
    private val IMAGE_CAPTURE_CODE = 1001
    private val IMAGE_PICK_CODE = 1000;
    //Permission code
    private val PERMISSION_CODE = 1001;
    var image_uri: Uri? = null
    private val REQUIRED_PERMISSIONS =
        arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camer)

        //storage = Firebase.storage
        //storageReference = FirebaseStorage.getInstance().getReference("image")
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED){
                    //permission denied
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE);
                    //show popup to request runtime permission
                    requestPermissions(permissions, PERMISSION_CODE);
                }
                else{
                    //permission already granted
                    pickImageFromGallery();
                }
            }
            else{
                //system OS is < Marshmallow
                pickImageFromGallery();
            }
        }

        btnSave.setOnClickListener {

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // OS가 Marshmallow 이상일 경우 권한체크를 해야 합니다.

            val permissionCheckCamera
                    = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            val permissionCheckStorage
                    = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (permissionCheckCamera == PackageManager.PERMISSION_GRANTED
                && permissionCheckStorage == PackageManager.PERMISSION_GRANTED) {
                // 권한 있음
                Log.d(TAG, "권한 이미 있음")
                startCamera()
            } else {
                // 권한 없음
                Log.d(TAG, "권한 없음")
                ActivityCompat.requestPermissions(this,
                    REQUIRED_PERMISSIONS,
                    PERMISSIONS_REQUEST_CODE)
            }

        } else {
            // OS가 Marshmallow 이전일 경우 권한체크를 하지 않는다.
            Log.d("MyTag", "마시멜로 버전 이하로 권한 이미 있음")
            startCamera()

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // grantResults[0] 거부 -> -1
        // grantResults[0] 허용 -> 0 (PackageManager.PERMISSION_GRANTED)

        Log.d(TAG, "requestCode : $requestCode, grantResults size : ${grantResults.size}")

        if(requestCode == PERMISSIONS_REQUEST_CODE) {
            var check_result = true
            for(result in grantResults) {
                if(result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false
                    break
                }
            }
            if(check_result) {
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
        myCameraPreview = MyCameraPreview(this, -1)
        cameraPreview.addView(myCameraPreview)
        previewTest.bringToFront()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            previewTest.setImageURI(image_uri)
        }
        if(resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            previewTest.setImageURI(data?.data);
            Log.d(TAG,"${data?.data}")
            val file = Uri.fromFile(File("${data?.data}"))
            Log.d(TAG, "${file}")
            val riverRef = storage.reference.child("images/${file.lastPathSegment}")
            var uploadTask = riverRef.putFile(file)

            uploadTask.addOnFailureListener { Exception ->
                Toast.makeText(this, "업로드 실패 ${Exception}", Toast.LENGTH_LONG).show()
            }.addOnSuccessListener {
                Toast.makeText(this, "업로드 성공", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadPicture() {
        val randomKey = UUID.randomUUID().toString()
        val storageRef = storage.reference
        storageRef.child("images/"+ randomKey);

        //storageRef.putFile()
    }
}


