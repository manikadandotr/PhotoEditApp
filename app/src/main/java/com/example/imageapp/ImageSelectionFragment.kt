@file:Suppress("DEPRECATION")

package com.example.imageapp

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.gesture.GestureLibraries.fromFile
import android.graphics.Bitmap
import android.net.Uri
import android.net.Uri.fromFile
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.contentValuesOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import java.io.File
import java.util.jar.Manifest


class ImageSelectionFragment : Fragment(), View.OnClickListener {


    val REQUEST_CODE = 200

    private val TAG = "ImageSelectionFragment"
    var navController: NavController? = null
    private lateinit var iv_selectedimg: ImageView
    private lateinit var file: File
    private var iuri: Uri? =null
    private lateinit var cameraIntent: Intent
    private lateinit var galleryIntent: Intent
    private lateinit var cropIntent: Intent

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        // Inflate the layout for this fragment
        if (ContextCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) !==
            PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                    android.Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(android.Manifest.permission.CAMERA), 1)
            } else {
                ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(android.Manifest.permission.CAMERA), 1)
            }
        }
        return inflater.inflate(R.layout.fragment_image_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        view.findViewById<Button>(R.id.selectedBtn).setOnClickListener(this)
        view.findViewById<Button>(R.id.uploadBtn).setOnClickListener(this)
        view.findViewById<ImageView>(R.id.iv_selectedimg)
//        enableRuntimePermission()


    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.selectedBtn -> {
                navController!!.navigate(R.id.action_imageSelectionFragment_to_imageEditingFragment)

            }
            R.id.uploadBtn -> {
                openDialog()
            }


        }
    }

    private fun openDialog() {
        val openDialog = AlertDialog.Builder(requireContext())
        openDialog.setTitle("Choose the option")
        openDialog.setPositiveButton("Camera") { dialog, _ ->

            openCamera()
        }
        openDialog.setNegativeButton("Gallery") { dialog, _ ->
            openGallery()
        }
        openDialog.setNeutralButton("Cancel") { dialog, _ -> }
        openDialog.create().show()

    }

    private fun openCamera() {

//        val values= ContentValues()
//        values.put(MediaStore.Images.Media.TITLE,"New Image")
//        values.put(MediaStore.Images.Media.DESCRIPTION,"Image taken by camera")

        cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        file = File(Environment.getExternalStorageDirectory(),
//        "file"+System.currentTimeMillis().toString()+".jpg")
//        uri= Uri.fromFile(file)
//        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,uri)
        cameraIntent.putExtra("return-data",true)
//        resultLauncher.launch(cameraIntent)
        startActivityForResult(cameraIntent, REQUEST_CODE)

    }
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE && data != null){
            iuri=data.data
//            val imageView = findViewById(R.id.iv_selectedimg) as ImageView
            Log.d(TAG, "onActivityResult: $iuri")
            iv_selectedimg.setImageBitmap(data.extras?.get("data") as Bitmap)
//                iv_selectedimg.setImageURI(iuri)
        }
    }

    fun hasCameraPermission() = ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.CAMERA)
    fun hasExternalStoragePermission() = ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.WRITE_EXTERNAL_STORAGE)


    private fun openGallery() {
        galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultLauncher.launch(galleryIntent)
    }


    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it ->
//            it.resultCode
//            it.data
            if (it.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = it.data
                Log.d(TAG, data.toString())
                Log.d("api",it.data?.type.toString())
//                cropImage()
//                iv_selectedimg.setImageURI(data)
//            doSomeOperations()
            }
        }

    private fun enableRuntimePermission() {

        if(ActivityCompat.shouldShowRequestPermissionRationale(
                requireContext() as Activity,android.Manifest.permission.CAMERA)){
            Toast.makeText(requireContext(),"Allowed",Toast.LENGTH_LONG)
        }
        else{
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.CAMERA),RequestPermissionCode)

        }

    }

    private fun cropImage(){
        try {
            cropIntent=Intent("com.android.camera.action.CROP")
            cropIntent.setDataAndType(iuri,"image/*")
            cropIntent.putExtra("crop",true)
            cropIntent.putExtra("outputX",1024)
            cropIntent.putExtra("outputY",1024)
            cropIntent.putExtra("aspectX",1)
            cropIntent.putExtra("aspectY",1)
            cropIntent.putExtra("scaleUpIfneed",true)
            cropIntent.putExtra("return-data",true)
            cropLauncher.launch(cropIntent)
        }catch (e:ActivityNotFoundException){
            e.printStackTrace()
        }
    }

    var cropLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it ->

            if (it.resultCode == Activity.RESULT_OK) {

//                val data: Intent? = it.data

            cropImage()
                Log.d(TAG, "dd:  ${it.resultCode}")
            }
        }


    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openCamera()
            Toast.makeText(requireContext(),"Granted",Toast.LENGTH_LONG)
        } else {
            Toast.makeText(requireContext(),"Not Granted",Toast.LENGTH_LONG)
        }
    }

    companion object{
        const val RequestPermissionCode =111
    }
}