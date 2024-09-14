/*Critical Thinking Exercise 5
    Create Xml Main
    Add all permissions to Manifest
    Add implementations to gradle

        Main Activity
            onCreate
                initialize recyclerview
                create grid layout
            checkPermissions
                check for permissions
                    if no, request permission
            onRequestPermissionResult
                if yes, load image
                if no, display error message
            LoadPhotos
                Load and sort photos
       Create ImageAdapter
       Create Main Layout
           ConstraintLayout
           Top banner
           Title
           recyclerview
       Create Picture Layout


* */
package com.example.cte5

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cte5.ImageAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val REQUEST_PERMISSIONS = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkPermission(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    // check if permission is granted, if not request permission
    private fun checkPermission(permission: String) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), REQUEST_PERMISSIONS)
        } else {
            loadGalleryPhotos()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSIONS && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadGalleryPhotos()
        } else {
            Toast.makeText(this, "Unable to Access Photos", Toast.LENGTH_SHORT).show()
        }
    }

    //get images and prep images
    private fun loadGalleryPhotos() {
        val imageUris = mutableListOf<String>()
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            //show by date added
            MediaStore.Images.Media.DATE_ADDED + " DESC")


        cursor?.use {
            val columnIndexId = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (it.moveToNext()) {
                val id = it.getLong(columnIndexId)
                val contentUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toString())
                imageUris.add(contentUri.toString())
            }
        }

        if (imageUris.isNotEmpty()) {
            val adapter = ImageAdapter(imageUris)
            recyclerView.adapter = adapter
        } else {
            Toast.makeText(this, "No images found", Toast.LENGTH_SHORT).show()
        }
    }
}
