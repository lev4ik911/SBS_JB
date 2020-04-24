package by.iba.mvvmbase.adapter

import android.graphics.Canvas
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider
import kotlin.math.max


class SectionsAdapter<T>(
    @LayoutRes private val layoutId: Int,
    override val onBind: (view: View, item: T, position: Int) -> Unit,
    override val isItemsEquals: (oldItem: T, newItem: T) -> Boolean,
    val getSectionTitle: (item: T) -> String
) : BaseAdapter<T>(layoutId, onBind, isItemsEquals), SectionTitleProvider {

    override fun getSectionTitle(position: Int): String {
        return getSectionTitle(itemsList[position])
    }

    inner class StickySectionItemDecoration(
        private val headerOffset: Int,
        @LayoutRes private val headerLayoutId: Int,
        val defineSectionTitleView: (parent: View) -> TextView,
        val isSection: (items: ArrayList<T>, position: Int) -> Boolean
    ) : RecyclerView.ItemDecoration() {

        private val headerView: View by lazy {
            LayoutInflater.from(mRecyclerView!!.context)
                .inflate(headerLayoutId, mRecyclerView, false)
        }

        private var headerTextView: TextView? = null

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)

            val pos = parent.getChildAdapterPosition(view)
            if (pos in 0 until itemsList.size-1 && isSection(itemsList, pos+1)) {
                outRect.bottom = headerOffset
            }
            if (isSection(itemsList, pos)) {
                outRect.top = headerView.height
            }
        }

        private fun fixLayoutSize(view: View, parent: ViewGroup) {
            val widthSpec = View.MeasureSpec.makeMeasureSpec(
                parent.width,
                View.MeasureSpec.EXACTLY
            )
            val heightSpec = View.MeasureSpec.makeMeasureSpec(
                parent.height,
                View.MeasureSpec.UNSPECIFIED
            )

            val childWidth = ViewGroup.getChildMeasureSpec(
                widthSpec,
                parent.paddingLeft + parent.paddingRight,
                view.layoutParams.width
            )
            val childHeight = ViewGroup.getChildMeasureSpec(
                heightSpec,
                parent.paddingTop + parent.paddingBottom,
                view.layoutParams.height
            )
            view.measure(childWidth, childHeight)
            view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        }

        override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            super.onDrawOver(c, parent, state)
            if(itemsList.isEmpty()) return
            headerTextView = defineSectionTitleView(headerView)
            fixLayoutSize(headerView, parent)

            var previousHeader: CharSequence = ""

            for (i in 0 until parent.childCount) {
                val child = parent.getChildAt(i)
                val position = parent.getChildAdapterPosition(child)
                val title = getSectionTitle(itemsList[position])
                headerTextView!!.text = title
                if (previousHeader != title || isSection(itemsList, position)) {

                    drawHeader(c, child, headerView)
                    previousHeader = title
                }
            }
        }

        private fun drawHeader(c: Canvas, child: View, headerView: View) {
            c.save()
            //  if (sticky) {
            c.translate(0f, max(0, child.top - headerView.height- headerOffset/2).toFloat())
            //   } else {
            //      c.translate(0f, (child.top - headerView.height).toFloat())
            //   }
            headerView.draw(c)
            c.restore()
        }


    }
}