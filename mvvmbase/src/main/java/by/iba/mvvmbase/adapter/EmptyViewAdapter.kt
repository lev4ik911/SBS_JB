package by.iba.mvvmbase.adapter

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView

class EmptyViewAdapter<T>(
    @LayoutRes private val layoutId: Int,

    override val onBind: (view: View, item: T, position: Int) -> Unit,
    isItemsEquals: (oldItem: T, newItem: T) -> Boolean
) : BaseAdapter<T>(layoutId, onBind, isItemsEquals) {
    @LayoutRes
    var emptyViewId: Int = 0
    @LayoutRes
    var dragLayoutId: Int = 0
    var onItemMoved: ((oldPos: Int, newPos: Int) -> Unit)? = null
    val itemTouchHelper by lazy {
        val simpleItemTouchCallback =
            object : ItemTouchHelper.SimpleCallback(UP or DOWN or START or END, 0) {

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    if (dragLayoutId == 0) return false
                    val adapter = recyclerView.adapter as EmptyViewAdapter<T>
                    val from = viewHolder.adapterPosition
                    val to = target.adapterPosition
                    adapter.moveItem(from, to)
                    adapter.notifyItemMoved(from, to)
                    onItemMoved?.invoke(from, to)
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                }

                override fun onSelectedChanged(
                    viewHolder: RecyclerView.ViewHolder?,
                    actionState: Int
                ) {
                    super.onSelectedChanged(viewHolder, actionState)

                    if (actionState == ACTION_STATE_DRAG) {
                        viewHolder?.itemView?.alpha = 0.8f
                    }
                }

                override fun clearView(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) {
                    super.clearView(recyclerView, viewHolder)
                    viewHolder.itemView.alpha = 1.0f
                }
            }
        ItemTouchHelper(simpleItemTouchCallback)
    }

    private fun moveItem(from: Int, to: Int) {
        if (from == itemCount - 1 || to == itemCount - 1) return
        val dragItem = itemsList[from]
        itemsList.removeAt(from)
        if (to < from) {
            itemsList.add(to, dragItem)
        } else {
            itemsList.add(to - 1, dragItem)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1) 1 else 0
    }

    override fun getItemCount(): Int = if (emptyViewId == 0) itemsList.size else itemsList.size + 1

    @SuppressLint("ResourceType")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<T> {
        val holder = if (viewType == 0 || emptyViewId == 0) ViewHolder(
            parent.inflate(layoutId),
            onBind
        ) else ViewHolder(parent.inflate(emptyViewId), onBind)
        if (dragLayoutId != 0) {
            holder.itemView.findViewById<View>(dragLayoutId)?.setOnTouchListener { _, event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                    itemTouchHelper.startDrag(holder)
                }
                return@setOnTouchListener true
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder<T>, pos: Int) {
        if (pos < itemCount - 1 || emptyViewId == 0) holder.bind(
            itemsList[pos],
            pos
        ) else holder.bindEmptyView()
    }
}