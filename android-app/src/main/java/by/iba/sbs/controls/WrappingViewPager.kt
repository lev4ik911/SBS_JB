package by.iba.sbs.controls

import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager


class WrappingViewPager(context: Context) : ViewPager(context) {
    private var mAnimStarted = false

    //constructor(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {}

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightMeasureSpec = heightMeasureSpec
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (!mAnimStarted && null != adapter) {
            var height = 0
            val child: View? = (adapter as FragmentPagerAdapter).getItem(currentItem).view
            if (child != null) {
                child.measure(
                    widthMeasureSpec,
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                )
                height = child.getMeasuredHeight()
                if (height < minimumHeight) {
                    height = minimumHeight
                }
            }

            // Not the best place to put this animation, but it works pretty good.
            val newHeight = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
            if (layoutParams.height != 0 && heightMeasureSpec != newHeight) {
                val targetHeight = height
                val currentHeight = layoutParams.height
                val heightChange = targetHeight - currentHeight
                val a: Animation = object : Animation() {
                    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                        if (interpolatedTime >= 1) {
                            layoutParams.height = targetHeight
                        } else {
                            val stepHeight = (heightChange * interpolatedTime).toInt()
                            layoutParams.height = currentHeight + stepHeight
                        }
                        requestLayout()
                    }

                    override fun willChangeBounds(): Boolean {
                        return true
                    }
                }
                a.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {
                        mAnimStarted = true
                    }

                    override fun onAnimationEnd(animation: Animation) {
                        mAnimStarted = false
                    }

                    override fun onAnimationRepeat(animation: Animation) {}
                })
                a.duration = 1000
                startAnimation(a)
                mAnimStarted = true
            } else {
                heightMeasureSpec = newHeight
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}