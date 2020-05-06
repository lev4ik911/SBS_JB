package by.iba.mvvmbase.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView


open class BaseAdapter<T>(
    @LayoutRes private val layoutId: Int,
    open val onBind: (view: View, item: T, position: Int) -> Unit,
    open val isItemsEquals: (oldItem: T, newItem: T) -> Boolean
) : RecyclerView.Adapter<BaseAdapter<T>.ViewHolder<T>>(), Filterable {

    var charSearch: String = ""
    private val itemsSource = ArrayList<T>()
    var itemsList = ArrayList<T>()

    var onItemClick: ((pos: Int, view: View, item: T) -> Unit)? = null
    var onEmptyViewItemClick: (() -> Unit)? = null

    var filterCriteria: ((item: T, textToSearch: String) -> Boolean)? = null

    init {
        itemsList.addAll(itemsSource)
    }

    var mRecyclerView: RecyclerView? = null
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                charSearch = constraint.toString()
                itemsList = if (filterCriteria == null || charSearch.isEmpty()) {
                    itemsSource
                } else {
                    val resultList = ArrayList<T>()
                    for (row in itemsSource) {
                        if (filterCriteria!!(row, charSearch)) {
                            resultList.add(row)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = itemsList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                itemsList = results?.values as ArrayList<T>
                notifyDataSetChanged()
            }

        }
    }

    fun addItem(item: T) {
        this.itemsSource.add(item)
        notifyItemInserted(itemsSource.size - 1)
        filter.filter(charSearch)
    }

    fun addItems(items: List<T>) {
        if (this.itemsSource.isEmpty())
            this.itemsSource.addAll(items)
        else
            smartUpdate(items)
        notifyDataSetChanged()
        filter.filter(charSearch)
    }

    fun clearItems() {
        itemsSource.clear()
        itemsList.clear()
        notifyDataSetChanged()
    }

    inner class ViewHolder<T>(
        private val view: View,
        val onBind: (view: View, item: T, position: Int) -> Unit
    ) : RecyclerView.ViewHolder(view), View.OnClickListener {

        override fun onClick(p0: View?) {
            if (adapterPosition < itemCount - 1)
                onItemClick?.invoke(adapterPosition, p0!!, itemsList[adapterPosition])
            else
                onEmptyViewItemClick?.invoke()
        }

        fun bind(item: T, position: Int) {
            view.setOnClickListener(this)
                onBind(view, item, position)
        }
        fun bindEmptyView() {
            view.setOnClickListener(this)
        }
    }

    infix fun ViewGroup.inflate(layoutRes: Int): View =
        LayoutInflater.from(context).inflate(layoutRes, this, false)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<T> {
        return ViewHolder(parent.inflate(layoutId), onBind)
    }

    override fun getItemCount(): Int = itemsList.size

    open override fun onBindViewHolder(holder: ViewHolder<T>, pos: Int) {
        holder.bind(itemsList[pos], pos)
    }

    private fun smartUpdate(newItems: List<T>) {
        val diffCallback = ItemDiffCallback(itemsSource, newItems, isItemsEquals)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.itemsSource.clear()
        this.itemsSource.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }

    class ItemDiffCallback<T>(
        private val oldList: List<T>,
        private val newList: List<T>,
        var isContentTheSame: (oldItem: T, newItem: T) -> Boolean
    ) : DiffUtil.Callback() {

        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition] == newList[newItemPosition]

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return isContentTheSame(oldList[oldItemPosition], newList[newItemPosition])

        }
    }
}



