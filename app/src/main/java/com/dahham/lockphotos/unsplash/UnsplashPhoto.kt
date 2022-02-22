package com.dahham.lockphotos.unsplash

import android.graphics.Bitmap
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey


@Entity
data class UnsplashPhoto(@PrimaryKey(autoGenerate = false)val id: String, val description: String? = null, val width: Int, val height: Int, @Embedded val links: UnsplashPhotoLinks, @Embedded val urls: UnsplashPhotoUrls) {

    @Ignore var bitmap: Bitmap? = null
    var noOfUsed = 0

    data class UnsplashPhotoLinks (val self: String, val html: String, val download: String, val download_location: String)

    data class UnsplashPhotoUrls(val raw: String, val full: String, val regular: String, val small: String, val small_s3: String)

}