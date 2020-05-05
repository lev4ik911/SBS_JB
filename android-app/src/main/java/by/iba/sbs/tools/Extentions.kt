package by.iba.sbs.tools

import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.TextView
import androidx.databinding.BindingAdapter
import by.iba.sbs.R


@BindingAdapter("visibleOrGone")
fun View.visibleOrGone(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

@BindingAdapter("textColorByValue")
fun TextView.textColorByValue(value: Int) {
    val color = when {
        value < 0 -> resources.getColor(R.color.colorLightRed)
        value > 0 -> resources.getColor(R.color.colorLightGreen)
        else -> resources.getColor(R.color.textColorSecondary)
    }
    setTextColor(color)
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