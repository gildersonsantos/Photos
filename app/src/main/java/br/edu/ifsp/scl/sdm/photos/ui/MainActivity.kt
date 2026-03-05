package br.edu.ifsp.scl.sdm.photos.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.ifsp.scl.sdm.photos.R
import br.edu.ifsp.scl.sdm.photos.adapter.ProductAdapter
import br.edu.ifsp.scl.sdm.photos.adapter.ProductImageAdapter
import br.edu.ifsp.scl.sdm.photos.databinding.ActivityMainBinding
import br.edu.ifsp.scl.sdm.photos.model.PhotosJSONAPI
import br.edu.ifsp.scl.sdm.photos.model.Product
import br.edu.ifsp.scl.sdm.photos.model.ProductList
import com.android.volley.Request
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.HttpURLConnection.HTTP_OK
import java.net.URL
import java.nio.Buffer

class MainActivity : AppCompatActivity() {
    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val productList: MutableList<Product> = mutableListOf()
    private val  productAdapter: ProductAdapter by lazy {
        ProductAdapter(this, productList)
    }

    private val productImageList: MutableList<Bitmap> = mutableListOf()
    private val productImageAdapter: ProductImageAdapter by lazy {
        ProductImageAdapter(this, productImageList)
    }

    companion object {
        const val PRODUCTS_ENDPOINT = "https://dummyjson.com/products/"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)

        setSupportActionBar(amb.mainTb.apply {
            title = getString(R.string.app_name)
        })

        amb.productsSp.apply {
            adapter = productAdapter
            onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val size = productImageList.size
                    productImageList.clear()
                    productImageAdapter.notifyItemRangeRemoved(0, size)
                    retrieveProductImages(productList[position])
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }
        }

        amb.productImagesRv.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = productImageAdapter
        }
        retrieveProducts()
    }


    private fun retrieveProducts() {
        StringRequest(Request.Method.GET, PRODUCTS_ENDPOINT, { response ->
            Gson().fromJson(response, ProductList::class.java).products.also {
                productAdapter.addAll(it)
            }
        }, {
            Toast.makeText(this, getString(R.string.request_problem), Toast.LENGTH_SHORT).show()
        }).also {
            PhotosJSONAPI.getInstance(this).addToRequestQueue(it)
        }
    }

    private fun retrieveProductImages(product: Product) {
        product.images.forEach { imageUrl ->
            ImageRequest(imageUrl, { response ->
                productImageList.add(response)
                productImageAdapter.notifyItemInserted(productImageList.lastIndex)
            }, 0, 0, ImageView.ScaleType.CENTER, Bitmap.Config.ARGB_8888, {
                Toast.makeText(this, getString(R.string.request_problem), Toast.LENGTH_SHORT).show()
            }).also {
                PhotosJSONAPI.getInstance(this).addToRequestQueue(it)
            }
        }
    }
}