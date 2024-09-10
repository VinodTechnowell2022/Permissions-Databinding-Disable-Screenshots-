package com.example.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.myapplication.databinding.PermissionScreenBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.DexterError
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.IOException
import java.util.Date

class PermissionScreen : AppCompatActivity() {

    lateinit var mBinding: PermissionScreenBinding
    val TAG :String = "PermissionScreen"

    private val REQUEST_IMAGE = 60
    var currentImagePath: String? = ""
    var base64StringImage: String? = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = PermissionScreenBinding.inflate(layoutInflater)
        setContentView(mBinding.root)


        mBinding.flCamera.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                //This is for Android 33 API Level bcz external storage permisssion is removed in this API level
                myPermissions33Above(REQUEST_IMAGE)
            } else {
                //ths will run upto android 32
                myPermissions(REQUEST_IMAGE)
            }

        }

    }

    private fun myPermissions33Above(type: Int) {
        Dexter.withContext(applicationContext)
            .withPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES,)

            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(multiplePermissionsReport: MultiplePermissionsReport) {
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                        Log.e(TAG, "permissions Granted")
                        Toast.makeText(this@PermissionScreen, "Permissions Granted", Toast.LENGTH_LONG).show()

                        //once permissions granted then you can take picture from camera gallery or other thing
                        if (type == REQUEST_IMAGE){
                            //In this block
                            dispatchTakePictureIntent(REQUEST_IMAGE)
                        }
                    }
                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied) {
                        Log.e(TAG,"permissions Denied---> ${multiplePermissionsReport.deniedPermissionResponses}")
                        Toast.makeText(this@PermissionScreen, "Permissions Denied", Toast.LENGTH_LONG).show()
                        showSettingsDialogAll(multiplePermissionsReport.deniedPermissionResponses)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(list: List<PermissionRequest>, permissionToken: PermissionToken) {
                    permissionToken.continuePermissionRequest()
                }
            }).withErrorListener { dexterError: DexterError ->
                Log.e(TAG,"permissions dexterError :" + dexterError.name)
            }.onSameThread()
            .check()
    }

    //this function is almost same as above function
    private fun myPermissions(type: Int) {
        Dexter.withContext(applicationContext)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(multiplePermissionsReport: MultiplePermissionsReport) {
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                        Log.e(TAG, "permissions Granted")
                        Toast.makeText(this@PermissionScreen, "Permissions Granted", Toast.LENGTH_LONG).show()

                        //once permissions granted then you can take picture from camera gallery or other thing
                        if (type == REQUEST_IMAGE){
                            //In this block
                            dispatchTakePictureIntent(REQUEST_IMAGE)
                        }

                    }
                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied) {
                        Log.e(TAG,"permissions Denied")
                        Toast.makeText(this@PermissionScreen, "Permissions Denied", Toast.LENGTH_LONG).show()
                        showSettingsDialogAll(multiplePermissionsReport.deniedPermissionResponses)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    list: List<PermissionRequest>,
                    permissionToken: PermissionToken
                ) {
                    permissionToken.continuePermissionRequest()
                }
            }).withErrorListener { dexterError: DexterError ->
                Log.e(TAG, "permissions dexterError :" + dexterError.name)
            }
            .onSameThread()
            .check()
    }

    //if permission are denied then permission settings of this app will open
    fun showSettingsDialogAll(deniedPermissionResponses: MutableList<PermissionDeniedResponse>) {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Need Permissions")
        builder.setMessage(deniedPermissionResponses[0].permissionName)
        builder.setPositiveButton("GOTO SETTINGS") { dialog, _ ->
            dialog.cancel()
            openSettings()
        }
        builder.show()
    }

    private fun openSettings() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", "com.example.myapplication", null)
        intent.data = uri
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }


    private fun dispatchTakePictureIntent(type : Int) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(applicationContext.packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFile(type)
            } catch (ex: IOException) {
                Log.e(TAG, "dispatchTakePictureIntent: ${ex.toString()}")
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                if (type == REQUEST_IMAGE){
                    val photoURI = FileProvider.getUriForFile(applicationContext, "com.example.myapplication" + ".provider", photoFile)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, this.REQUEST_IMAGE)
                }
            }
        }
    }

    private fun createImageFile(type: Int): File? {

        // Create an image file name
        @SuppressLint("SimpleDateFormat")
        val timeStamp = java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir: File? = applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
        if (type == REQUEST_IMAGE){
            currentImagePath = ""
            currentImagePath = image.absolutePath
        }

        return image
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {

            currentImagePath = PathUtil.compressImage(this, currentImagePath)!!
            if (!currentImagePath!!.isEmpty()) {
                val photoURI = FileProvider.getUriForFile(this, "com.example.myapplication" + ".provider", File( currentImagePath!!) )

                Log.i(TAG, "onActivityResult photoURI : $photoURI")

                base64StringImage = ""
                Log.e(TAG, "currentImagePath --> $currentImagePath")

                if (!currentImagePath?.isEmpty()!!) {
                    base64StringImage = PathUtil.getBase64StringFile(File(currentImagePath!!))
                }
                val bitmap : Bitmap = PathUtil.base64ToBitmap( base64StringImage!! )

                //setting image here to ImageView and we can send base64 image to our api
                mBinding.ivPhoto.setImageBitmap(bitmap)
            }
        }  else {
            Log.e(TAG,"onActivityResult Not OK")
        }
    }

}