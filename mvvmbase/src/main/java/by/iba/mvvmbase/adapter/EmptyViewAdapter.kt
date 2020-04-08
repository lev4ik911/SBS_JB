package by.iba.mvvmbase.adapter

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

class EmptyViewAdapter<T>(
    @LayoutRes private val layoutId: Int,
    @LayoutRes private val emptyViewId: Int = 0,
    override val onBind: (view: View, item: T, position: Int) -> Unit,
    isItemsEquals: (oldItem: T, newItem: T) -> Boolean
) : BaseAdapter<T>(layoutId, onBind, isItemsEquals) {
    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1) 1 else 0
    }
    override fun getItemCount(): Int = if(emptyViewId != 0) itemsList.size +1 else itemsList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<T> {
        return if(viewType == 0) ViewHolder(parent.inflate(layoutId), onBind) else ViewHolder(parent.inflate(emptyViewId), onBind)
    }

    override fun onBindViewHolder(holder: ViewHolder<T>, pos: Int) {
        if (pos < itemCount - 1)  holder.bind(itemsList[pos], pos)
    }
}