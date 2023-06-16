package com.example.routpixal

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class Pamiatki : AppCompatActivity() {

    private lateinit var btnAddPhoto: Button
    private lateinit var cardContainer: LinearLayout
    private lateinit var existingCard: CardView
    private lateinit var existingImageView: ImageView

    companion object {
        private const val REQUEST_IMAGE_PICK = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.card)

        btnAddPhoto = findViewById(R.id.btnAddPhoto)
        cardContainer = findViewById(R.id.containero)
        existingCard = findViewById(R.id.cardview)
        existingImageView = findViewById(R.id.photo)

        existingCard.visibility = View.GONE

        btnAddPhoto.setOnClickListener {
            wybierzZdjecie()
        }
    }

    private fun wybierzZdjecie() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            val selectedImage = data.data
            val imageDrawable = getImageDrawable(selectedImage)
            addNewCard(imageDrawable)
        }
    }

    private fun getImageDrawable(imageUri: Uri?): Drawable? {
        return try {
            val inputStream = contentResolver.openInputStream(imageUri!!)
            Drawable.createFromStream(inputStream, imageUri.toString())
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun addNewCard(imageDrawable: Drawable?) {
        val cardView = CardView(this)
        cardView.layoutParams = existingCard.layoutParams
        cardView.radius = existingCard.radius
        cardView.cardElevation = existingCard.cardElevation
        cardView.setCardBackgroundColor(existingCard.cardBackgroundColor)
        cardView.setContentPadding(
            existingCard.contentPaddingLeft,
            existingCard.contentPaddingTop,
            existingCard.contentPaddingRight,
            existingCard.contentPaddingBottom
        )

        val imageView = ImageView(this)
        imageView.layoutParams = existingImageView.layoutParams
        imageView.scaleType = existingImageView.scaleType
        imageView.setImageDrawable(imageDrawable)

        cardView.addView(imageView)
        cardContainer.addView(cardView)
    }
}
