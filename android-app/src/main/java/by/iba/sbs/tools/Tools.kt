package by.iba.sbs.tools

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import by.iba.sbs.R
import by.iba.sbs.library.model.MessageType
import by.iba.sbs.library.model.ToastMessage
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.shashank.sony.fancytoastlib.FancyToast

@BindingAdapter("textColorByValue")
fun TextView.textColorByValue(value: Int) {
    val color = when {
        value < 0 -> resources.getColor(R.color.colorLightRed)
        value > 0 -> resources.getColor(R.color.colorLightGreen)
        else -> resources.getColor(R.color.textColorSecondary)
    }
    setTextColor(color)
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

class Tools {

    companion object {
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