package by.iba.mvvmbase.adapter

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


open class BaseBindingAdapter<T, DB : androidx.databinding.ViewDataBinding>(
    @LayoutRes private val layoutId: Int,
    @LayoutRes val viewModelVariableId: Int,
    open val isItemsEquals: (oldItem: T, newItem: T) -> Boolean
) : RecyclerView.Adapter<BaseBindingAdapter<T, DB>.BindingHolder>(), Filterable {

    @LayoutRes
    var emptyViewId: Int = 0

    @LayoutRes
    var dragLayoutId: Int = 0
    var charSearch: String = ""
    private val itemsSource = ArrayList<T>()
    var itemsList = ArrayList<T>()

    var onItemClick: ((pos: Int, view: View, item: T) -> Unit)? = null
    var onEmptyViewItemClick: (() -> Unit)? = null

    var filterCriteria: ((item: T, textToSearch: String) -> Boolean)? = null
    private var mRecyclerView: RecyclerView? = null

    init {
        itemsList.addAll(itemsSource)
    }

    val itemTouchHelper by lazy {
        val simpleItemTouchCallback =
            object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END,
                0
            ) {

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    if (dragLayoutId == 0) return false
                    val adapter = recyclerView.adapter as BaseBindingAdapter<*, *>
                    val from = viewHolder.adapterPosition
                    val to = target.adapterPosition
                    adapter.moveItem(from, to)
                    adapter.notifyItemMoved(from, to)
                    // onItemMoved?.invoke(from, to)
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                }

                override fun onSelectedChanged(
                    viewHolder: RecyclerView.ViewHolder?,
                    actionState: Int
                ) {
                    super.onSelectedChanged(viewHolder, actionState)

                    if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
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

    infix fun ViewGroup.inflate(layoutRes: Int): View =
        LayoutInflater.from(context).inflate(layoutRes, this, false)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: DB = DataBindingUtil.inflate(
            inflater,
            layoutId,
            parent,
            false
        )
        val view = binding.root
        val holder = BindingHolder(binding, view)
        if (dragLayoutId != 0) {
            view.findViewById<View>(dragLayoutId)?.setOnTouchListener { _, event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                    itemTouchHelper.startDrag(holder)
                }
                return@setOnTouchListener true
            }
        }
        return holder
    }


    override fun onBindViewHolder(holder: BindingHolder, pos: Int) {
        if (pos < itemCount - 1 || emptyViewId == 0)
            holder.bind(itemsList[pos])
        else
            holder.bindEmptyView()
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

    inner class BindingHolder(private var dataBinding: DB, private val view: View) :
        RecyclerView.ViewHolder(dataBinding.root), View.OnClickListener {
        fun bind(employee: T) {
            dataBinding.setVariable(viewModelVariableId, employee)
            dataBinding.executePendingBindings()
        }

        override fun onClick(p0: View?) {
            if (adapterPosition < itemCount - 1)
                onItemClick?.invoke(adapterPosition, p0!!, itemsList[adapterPosition])
            else
                onEmptyViewItemClick?.invoke()
        }

        fun bindEmptyView() {
            view.setOnClickListener(this)
        }
    }
}



