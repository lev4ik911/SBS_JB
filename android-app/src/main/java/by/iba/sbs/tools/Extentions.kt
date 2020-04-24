package by.iba.sbs.tools

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter

@BindingAdapter("srcByBoolean", "trueRes", "falseRes")
fun ImageView.srcByBoolean(value: Boolean,  trueRes:Int, falseRes:Int) {
    this.setImageResource(if (value) trueRes else falseRes)
}

class Extentions {
}