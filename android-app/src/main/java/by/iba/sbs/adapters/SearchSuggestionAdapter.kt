package by.iba.sbs.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView


class SearchSuggestionAdapter(context: Context, objects: List<String>, _suggestionIcon: Drawable, _ellipsize:Boolean):
    ArrayAdapter<String>(context, by.iba.sbs.R.layout.suggest_item, objects) {

    private var suggestionIcon: Drawable
    private val inflater: LayoutInflater
    private val ellipsize: Boolean
    private var data: List<String>

    init{
        inflater = LayoutInflater.from(context)
        suggestionIcon = _suggestionIcon
        ellipsize = _ellipsize
        data = objects
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults? {
                val filterResults = FilterResults()
                filterResults.values = data
                filterResults.count = data.size
                return filterResults
            }
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            }
        }
    }
    override fun getCount(): Int {
        return data.size
    }
    override fun getItem(position: Int): String? {
        return data[position]
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getView(position: Int, _convertView: View?, parent: ViewGroup): View? {
        var convertView = _convertView
        val viewHolder: SuggestionsViewHolder
        if (convertView == null) {
            convertView = inflater.inflate(by.iba.sbs.R.layout.suggest_item, parent, false)
            viewHolder = SuggestionsViewHolder(convertView)
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as SuggestionsViewHolder
        }
        val currentListData = getItem(position) as String?
        viewHolder.textView.text = currentListData
        viewHolder.imageView?.setImageDrawable(suggestionIcon)
        if (ellipsize) {
            viewHolder.textView.setSingleLine()
            viewHolder.textView.ellipsize = TextUtils.TruncateAt.END
        }
        return convertView
    }

    fun setItems(items: List<String>){
        data = items
        notifyDataSetChanged()
    }
    fun setSuggestionIcon(icon: Drawable){
        suggestionIcon = icon
    }

    private class SuggestionsViewHolder(convertView: View?) {
        var textView: TextView
        var imageView: ImageView? = null

        init {
            textView = convertView!!.findViewById(by.iba.sbs.R.id.suggestion_text) as TextView
            imageView = convertView.findViewById(by.iba.sbs.R.id.suggestion_icon) as ImageView?
        }
    }
}
