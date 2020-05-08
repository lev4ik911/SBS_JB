package by.iba.sbs.tools

import android.content.Context
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import by.iba.sbs.R

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

class Extentions {
    companion object {
        fun startAlphaAnimation(v: View, duration: Long, visibility: Int) {
            val alphaAnimation = if (visibility == View.VISIBLE)
                AlphaAnimation(0f, 1f)
            else
                AlphaAnimation(1f, 0f)

            alphaAnimation.duration = duration
            alphaAnimation.fillAfter = true
            v.startAnimation(alphaAnimation)
        }
    }
}