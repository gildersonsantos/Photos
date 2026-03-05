package br.edu.ifsp.scl.sdm.photos.ui

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import br.edu.ifsp.scl.sdm.photos.R
import br.edu.ifsp.scl.sdm.photos.databinding.ActivityMainBinding
import br.edu.ifsp.scl.sdm.photos.model.Photo
import br.edu.ifsp.scl.sdm.photos.model.PhotosJSONAPI
import com.android.volley.toolbox.ImageRequest

class MainActivity : AppCompatActivity() {
    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var photoList: List<Photo> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)

        setSupportActionBar(amb.mainTb.apply {
            title = getString(R.string.app_name)
        })

        amb.photosSp.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                retrieveImages(photoList[position])
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
        retrievePhotos()
    }

    private fun retrievePhotos() {
        val request = PhotosJSONAPI.PhotoListRequest({ response ->
            photoList = response

            val titles = photoList.map { it.title }
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, titles)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            amb.photosSp.adapter = adapter

        }, {
            Toast.makeText(this, "Erro ao buscar fotos", Toast.LENGTH_SHORT).show()
        })

        PhotosJSONAPI.getInstance(this).addToRequestQueue(request)
    }

    private fun retrieveImages(photo: Photo) {
        val fixedUrl = "${photo.url.replace("via.placeholder.com", "placehold.co")}/FFF.png"
        val fixedThumbUrl = "${photo.thumbnailUrl.replace("via.placeholder.com", "placehold.co")}/FFF.png"

        val mainImageRequest = ImageRequest(fixedUrl, { response ->
            amb.photoIv.setImageBitmap(response)
        }, 0, 0, ImageView.ScaleType.CENTER, Bitmap.Config.ARGB_8888, {
            Toast.makeText(this, getString(R.string.request_problem), Toast.LENGTH_SHORT).show()
        })

        val thumbImageRequest = ImageRequest(fixedThumbUrl, { response ->
            amb.thumbnailIv.setImageBitmap(response)
        }, 0, 0, ImageView.ScaleType.CENTER, Bitmap.Config.ARGB_8888, {
            Toast.makeText(this, getString(R.string.request_problem), Toast.LENGTH_SHORT).show()
        })

        val queue = PhotosJSONAPI.getInstance(this)

        queue.addToRequestQueue(mainImageRequest)
        queue.addToRequestQueue(thumbImageRequest)
    }
}