package by.iba.mvvmbase.custom.bottomnavigation

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import by.iba.mvvmbase.R
import kotlinx.android.extensions.LayoutContainer


@Suppress("unused")
class BottomNavigationCell : RelativeLayout, LayoutContainer {

    companion object {
        const val EMPTY_VALUE = "empty"
    }

    var defaultIconColor = 0
    var selectedIconColor = 0
    var circleColor = 0
    private lateinit var tvCount: TextView
    private lateinit var ivCell: CellImageView
    private lateinit var vCircle: View
    private lateinit var frame: FrameLayout
    var icon = 0
        set(value) {
            field = value
            if (allowDraw)
                ivCell.resource = value
        }

    var count: String? = EMPTY_VALUE
        set(value) {
            field = value
            if (allowDraw) {
                if (count != null && count == EMPTY_VALUE) {
                    tvCount.text = ""
                    tvCount.visibility = View.INVISIBLE
                } else {
                    if (count != null && count?.length ?: 0 >= 3) {
                        field = count?.substring(0, 1) + ".."
                    }
                    tvCount.text = count
                    tvCount.visibility = View.VISIBLE
                    val scale = if (count?.isEmpty() == true) 0.5f else 1f
                    tvCount.scaleX = scale
                    tvCount.scaleY = scale
                }
            }
        }

    private var iconSize = dip(context, 48)
        set(value) {
            field = value
            if (allowDraw) {
                ivCell.size = value
                ivCell.pivotX = iconSize / 2f
                ivCell.pivotY = iconSize / 2f
            }
        }

    var countTextColor = 0
        set(value) {
            field = value
            if (allowDraw)
                tvCount.setTextColor(field)
        }

    var countBackgroundColor = 0
        set(value) {
            field = value
            if (allowDraw) {
                val d = GradientDrawable()
                d.setColor(field)
                d.shape = GradientDrawable.OVAL
                ViewCompat.setBackground(tvCount, d)
            }
        }

    var countTypeface: Typeface? = null
        set(value) {
            field = value
            if (allowDraw && field != null)
                tvCount.typeface = field
        }

    var rippleColor = 0
        set(value) {
            field = value

        }

    var isFromLeft = false
    var duration = 0L
    private var progress = 0f
        set(value) {
            field = value
            frame.y = (1f - progress) * dip(context, 18) + dip(context, -2)

            ivCell.color = if (progress == 1f) selectedIconColor else defaultIconColor
            val scale = (1f - progress) * (-0.2f) + 1f
            ivCell.scaleX = scale
            ivCell.scaleY = scale

            val d = GradientDrawable()
            d.setColor(circleColor)
            d.shape = GradientDrawable.OVAL

            ViewCompat.setBackground(vCircle, d)

            ViewCompat.setElevation(vCircle, if (progress > 0.7f) dipf(context, progress * 4f) else 0f)

            val m = dip(context, 24)
            vCircle.x = (1f - progress) * (if (isFromLeft) -m else m) + ((measuredWidth - dip(context, 48)) / 2f)
            vCircle.y = (1f - progress) * measuredHeight + dip(context, 6)
        }

    var isEnabledCell = false
        set(value) {
            field = value
            val d = GradientDrawable()
            d.setColor(circleColor)
            d.shape = GradientDrawable.OVAL
            if (Build.VERSION.SDK_INT >= 21 && !isEnabledCell) {
                frame.background = RippleDrawable(ColorStateList.valueOf(rippleColor), null, d)
            } else {
                frame.runAfterDelay(200) {
                    frame.setBackgroundColor(Color.TRANSPARENT)
                }
            }
        }

    var onClickListener: () -> Unit = {}
        set(value) {
            field = value
            ivCell.setOnClickListener {
                onClickListener()
            }
        }

    override lateinit var containerView: View
    private var allowDraw = false

    constructor(context: Context) : super(context) {
        initializeView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setAttributeFromXml(context, attrs)
        initializeView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setAttributeFromXml(context, attrs)
        initializeView()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun setAttributeFromXml(context: Context, attrs: AttributeSet) {
    }

    private fun initializeView() {
        allowDraw = true
        containerView = LayoutInflater.from(context).inflate(R.layout.bottom_navigation_cell, this)
        tvCount = findViewById(R.id.tv_count)
        ivCell = findViewById(R.id.iv_cell)
        vCircle= findViewById(R.id.v_circle)
        frame =  findViewById(R.id.fl)
        draw()
    }

    private fun draw() {
        if (!allowDraw)
            return

        icon = icon
        count = count
        iconSize = iconSize
        countTextColor = countTextColor
        countBackgroundColor = countBackgroundColor
        countTypeface = countTypeface
        rippleColor = rippleColor
        onClickListener = onClickListener
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        progress = progress
    }

    fun disableCell() {
        if (isEnabledCell)
            animateProgress(false)
        isEnabledCell = false
    }

    fun enableCell(isAnimate: Boolean = true) {
        if (!isEnabledCell)
            animateProgress(true, isAnimate)
        isEnabledCell = true
    }

    private fun animateProgress(enableCell: Boolean, isAnimate: Boolean = true) {
        val d = if (enableCell) duration else 250
        val anim = ValueAnimator.ofFloat(0f, 1f)
        anim.apply {
            startDelay = if (enableCell) d / 4 else 0L
            duration = if (isAnimate) d else 1L
            interpolator = FastOutSlowInInterpolator()
            addUpdateListener {
                val f = it.animatedFraction
                progress = if (enableCell)
                    f
                else
                    1f - f
            }
            start()
        }
    }
}