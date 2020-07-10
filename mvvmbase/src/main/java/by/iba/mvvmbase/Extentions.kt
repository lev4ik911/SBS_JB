package by.iba.mvvmbase

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.databinding.BindingAdapter


@BindingAdapter("visibleOrGone")
fun View.visibleOrGone(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

@BindingAdapter("visibleOrNot")
fun View.visibleOrNot(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.INVISIBLE
}

@BindingAdapter("childrenEnabled")
fun ViewGroup.childrenEnabled(enabled:Boolean){
    for (i in 0 until this.childCount) {
        val child = this.getChildAt(i)
        child.isEnabled = enabled
    }
}

class Extentions{
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

    }
}