package com.example.mygallery

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ProgressBar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mygallery.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var recyclerView : RecyclerView
    private lateinit var progressBar : ProgressBar
    private lateinit var galleryImage : ArrayList<Image>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = binding.rv
        progressBar = binding.pb

        recyclerView.layoutManager = GridLayoutManager(this, 3)

        if(ContextCompat.checkSelfPermission(
                this@MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE
            )!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                101
            )
        }

        galleryImage = ArrayList()

        if(galleryImage!!.isEmpty()) {
            progressBar.visibility = View.VISIBLE
            // image is loading, show progress bar
            galleryImage = getImageFromGallary()
            println("Image Fetch Start")
            for (image in galleryImage) {
                println("Image Name: ${image.imageName}, Image Path: ${image.imagePath}")
            }
            println("Image Fetch End")

            recyclerView.adapter = ImageAdapter(this, galleryImage)
            // progress bar gone
            progressBar.visibility = View.GONE
        }
    }

    private fun getImageFromGallary(): ArrayList<Image> {
        val imageList = ArrayList<Image>()

        val projection = arrayOf(
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA
        )

        val queryURI : Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val cursor : Cursor? = contentResolver.query(queryURI, projection, null, null, sortOrder)

        cursor?.use {
            val nameColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

            while(it.moveToNext()) {
                val name = it.getString(nameColumn)
                val data = it.getString(dataColumn)

                val image = Image(data, name)
                imageList.add(image)
            }
        }

        return imageList
    }
}