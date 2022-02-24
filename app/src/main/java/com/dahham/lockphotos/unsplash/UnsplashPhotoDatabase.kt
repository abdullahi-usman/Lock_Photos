package com.dahham.lockphotos.unsplash

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.graphics.drawable.toDrawable
import androidx.room.*
import androidx.room.OnConflictStrategy.ABORT
import androidx.room.OnConflictStrategy.REPLACE
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.FilenameFilter
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Path


@Dao
interface UnsplashPhotoDaO{
    @Insert(onConflict = ABORT)
    fun put(vararg photos: UnsplashPhoto)

    @Query("SELECT * from unsplashphoto ORDER BY noOfUsed ASC LIMIT 1")
    fun getleastUnsed(): UnsplashPhoto?

    @Query("SELECT * from unsplashphoto")
    fun getAll(): Array<UnsplashPhoto>

    @Update(onConflict = REPLACE)
    fun Update(vararg photos: UnsplashPhoto)

    @Delete
    fun Delete(vararg photos: UnsplashPhoto)

    @Query("SELECT * from unsplashphoto WHERE noOfUsed >= :maxUsable")
    fun getPurgeable(maxUsable: Int): Array<UnsplashPhoto>

    @Transaction
    fun purge(maxUsable: Int){
        Delete(*getPurgeable(maxUsable))
    }
}

@Database(entities = [UnsplashPhoto::class], version = 1)
abstract class UnsplashPhotoDatabase: RoomDatabase(){

    lateinit var localFilesDir: File

    companion object{
        @Volatile
        private var INSTANCE: UnsplashPhotoDatabase? = null

        fun getInstance(context: Context) = INSTANCE ?: synchronized(this){
            INSTANCE ?: Room.databaseBuilder(context.applicationContext, UnsplashPhotoDatabase::class.java, "unsplash_photos.db").allowMainThreadQueries().build().also {
                INSTANCE = it
                INSTANCE?.localFilesDir = context.filesDir
            }
        }
    }

    protected abstract fun getDaO(): UnsplashPhotoDaO;

    private fun writeBitmap(bitmap: Bitmap, id: String): Boolean{
        /*val byteBufer = ByteBuffer.allocate(bitmap.allocationByteCount)
        bitmap.copyPixelsToBuffer(byteBufer)*/

        var fileOutputStream: FileOutputStream? = null
        try {
            val unsplashPhotoFile = File(localFilesDir, id)
            if (unsplashPhotoFile.createNewFile().not()) return false

            fileOutputStream = FileOutputStream(unsplashPhotoFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fileOutputStream)

            fileOutputStream.flush()
        }catch (ex: IOException){
            return false
        }finally {
            fileOutputStream?.close()
        }

        return true
    }


    fun getRandomPhoto(): UnsplashPhoto? {

        val np = getDaO().getAll()
        val nn = getDaO().getleastUnsed()
        return nn?.apply {
            localFilesDir.listFiles{ dir, name ->
                name == id
            }?.first()?.let { file ->
                BitmapFactory.decodeStream(file.inputStream())?.let {

                    if (bitmap != null) {
                        ++this.noOfUsed
                        getDaO().Update(this)
                    }
                    bitmap
                }
            }
        }
    }

    fun savePhoto(unsplashPhoto: UnsplashPhoto): Boolean{
        return unsplashPhoto.bitmap?.let {
            return@let if (writeBitmap(it, unsplashPhoto.id)) {
                getDaO().put(unsplashPhoto)
                true
            } else false
        } ?: false
    }

    fun Purge(maxUsable: Int){
        getDaO().purge(maxUsable)
    }
}