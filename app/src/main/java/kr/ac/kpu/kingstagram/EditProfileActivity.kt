package kr.ac.kpu.kingstagram

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_edit__profile.*

class EditProfileActivity : AppCompatActivity() {

    private val PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 1001
    var image_uri: Uri? = null
    val REQUEST_CODE_GET_IMAGE = 101
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit__profile)

        button.setOnClickListener {
            showDialog()
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
                        account_iv_profile.setImageResource(0)

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
            account_iv_profile.setImageURI(image_uri)
        }
        if(requestCode == REQUEST_CODE_GET_IMAGE){
            account_iv_profile.setImageURI(data?.data);
        }
    }

}

