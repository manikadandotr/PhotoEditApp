package com.example.imageapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.NavController
import androidx.navigation.Navigation
import java.io.File
import java.net.URI


class ImageSelectionFragment : Fragment(), View.OnClickListener {

    var navController: NavController? = null
    private lateinit var imageView: ImageView
    private lateinit var file: File
    private lateinit var uri: URI
    private lateinit var cameraIntent: Intent
    private lateinit var galleryIntent: Intent
    private lateinit var cropIntent: Intent

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
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
    }

    private fun openGallery() {
        galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultLauncher.launch(galleryIntent)
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it ->
            it.resultCode
            it.data
            if (it.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = it.data
//            doSomeOperations()
            }
        }

    private fun enableRuntimePermission() {
        TODO("Not yet implemented")
    }
}