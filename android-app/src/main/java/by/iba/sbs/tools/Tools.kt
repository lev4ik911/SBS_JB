package by.iba.sbs.tools

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import by.iba.sbs.R
import by.iba.sbs.library.model.MessageType
import by.iba.sbs.library.model.ToastMessage
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.shashank.sony.fancytoastlib.FancyToast
import java.io.File

@BindingAdapter("imageFromPath")
fun ImageView.imageFromPath(imagePath: String) {
    if (imagePath.isNotEmpty()) {
        val imageUri = Uri.fromFile(File(imagePath))
        this.setImageURI(imageUri)
    } else {
        this.setImageResource(R.drawable.clouds)
    }
}

fun ImageView.loadImageFromResources(context: Context, aImageUrl: Int) {
    Glide.with(context)
        .load(aImageUrl)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(this)
}

@BindingAdapter("refreshing")
fun SwipeRefreshLayout.refreshing(value: Boolean) {
    this.isRefreshing = value
}

@BindingAdapter("visibleOrGone")
fun View.visibleOrGone(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

@BindingAdapter("visibleOrNot")
fun View.visibleOrNot(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.INVISIBLE
}

@BindingAdapter("childrenEnabled")
fun ViewGroup.childrenEnabled(enabled: Boolean) {
    for (i in 0 until this.childCount) {
        val child = this.getChildAt(i)
        child.isEnabled = enabled
    }
}

class Tools {

    companion object {
        /**
         * This method converts dp unit to equivalent pixels, depending on device density.
         *
         * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
         * @param context Context to get resources and device specific display metrics
         * @return A float value to represent px equivalent to dp depending on device density
         */
        fun convertDpToPx(context: Context, dp: Float): Float {
            return dp * context.resources.displayMetrics.density
        }

        /**
         * This method converts device specific pixels to density independent pixels.
         *
         * @param px      A value in px (pixels) unit. Which we need to convert into db
         * @param context Context to get resources and device specific display metrics
         * @return A float value to represent dp equivalent to px value
         */
        fun convertPxToDp(context: Context, px: Float): Float {
            return px / context.resources.displayMetrics.density
        }

        fun View.waitForLayout(f: (v: View) -> Unit) = with(viewTreeObserver) {
            addOnGlobalLayoutListener {
                f(this@waitForLayout)
            }
        }

        fun colorFromHex(colorHex: String) = Color.parseColor(colorHex)

        fun startAlphaAnimation(v: View, duration: Long, visibility: Int) {
            val alphaAnimation = if (visibility == View.VISIBLE)
                AlphaAnimation(0f, 1f)
            else
                AlphaAnimation(1f, 0f)

            alphaAnimation.duration = duration
            alphaAnimation.fillAfter = true
            v.startAnimation(alphaAnimation)
        }

        fun showToast(context: Context, tag: String, msg: ToastMessage) {
            when (msg.type) {
                MessageType.ERROR ->
                    Log.e(tag, msg.getLogMessage())
                MessageType.WARNING ->
                    Log.w(tag, msg.getLogMessage())
                MessageType.INFO ->
                    Log.i(tag, msg.getLogMessage())
                else ->
                    Log.v(tag, msg.getLogMessage())
            }
            FancyToast.makeText(
                context,
                msg.message,
                FancyToast.LENGTH_LONG,
                msg.type.index,
                false
            ).show()
        }
    }

}