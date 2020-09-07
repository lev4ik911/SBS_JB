package by.iba.sbs.tools

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.net.Uri
import android.os.Handler
import android.os.Message
import by.iba.sbs.library.model.Guideline
import by.iba.sbs.library.model.Step
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.io.File
import java.io.FileOutputStream
import java.util.*

class DownloadManager (_context: Context){
    private val context: Context
    init {
        context = _context
    }
    fun createImageFileInInternalStorage(guidelineId: String="", stepId: String = "", remoteImageId: String=""): Uri {
        var imageFileName = ""
        if (remoteImageId.isNotEmpty())
            imageFileName = remoteImageId
        else
            imageFileName = UUID.randomUUID().toString()
        val cw = ContextWrapper(context)
        var directory = cw.getDir("imageDir", Context.MODE_PRIVATE)

        val builder = StringBuilder()
        builder.append(directory.path)

        if (guidelineId.isNotEmpty()) {
            builder.append("/")
                    .append(guidelineId)
        }
        if (stepId.isNotEmpty()) {
            builder.append("/")
                .append(stepId)
        }
        directory = File(builder.toString()).apply {
            if (!this.exists()) {
                this.mkdirs();
            }
        }
        var file = File(String.format("%s/%s.jpg", directory.path, imageFileName))
        if (!file.exists()) {
            file = File.createTempFile(
                imageFileName, ".jpg", directory
            )
        }
        return Uri.fromFile(file)
    }
    fun downloadImage(url: String,
                      guidelineId: String,
                      stepId: String = "",
                      remoteImageId: String,
                      item: Any,
                      imageHandler: Handler) {
        val glideUrl = GlideUrl(
            url,
            LazyHeaders.Builder().addHeader("Accept", "application/octet-stream;q=0.9, application/json;q=0.1").build())
        Glide.with(context)
            .asBitmap()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .load(glideUrl)
            .listener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    isFirstResource: Boolean
                ): Boolean {
                    //TODO("add handler")
                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    val destFile = File(createImageFileInInternalStorage(guidelineId, stepId, remoteImageId).path.orEmpty())
                    val fos = FileOutputStream(destFile)
                    resource?.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                    when (item) {
                        is Guideline -> item.imagePath = destFile.path
                        is Step -> item.imagePath = destFile.path
                        else -> {}
                    }
                    Message().apply {
                        this.obj = item
                        imageHandler.sendMessage(this)
                    }
                    return true
                }
            })
            .submit()
        }
}