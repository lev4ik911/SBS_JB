package by.iba.sbs.tools

import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import androidx.databinding.BindingAdapter

@BindingAdapter("srcByBoolean", "trueRes", "falseRes")
fun ImageView.srcByBoolean(value: Boolean,  trueRes:Int, falseRes:Int) {
    this.setImageResource(if (value) trueRes else falseRes)
}
@BindingAdapter("visibleOrGone")
fun View.visibleOrGone(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
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