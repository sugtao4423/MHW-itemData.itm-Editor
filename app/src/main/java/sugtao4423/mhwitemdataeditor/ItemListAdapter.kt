package sugtao4423.mhwitemdataeditor

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import java.text.NumberFormat
import java.util.*

class ItemListAdapter(private val context: Context) : RecyclerView.Adapter<ItemListAdapter.ViewHolder>() {

    private val data = arrayListOf<ItemData>()
    private val numberFormat = NumberFormat.getNumberInstance(Locale.US)

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_mhw_item, viewGroup, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (data.size <= position) {
            return
        }
        val item = data[position]

        holder.itemIcon.setImageResource(ItemDataUtils.getItemIconRes(item))
        holder.itemName.text = ItemDataUtils.getItemName(context, item)
        holder.itemDetail.text = context.getString(R.string.item_detail, item.id, item.carry, numberFormat.format(item.sell), numberFormat.format(item.buy))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun clear() {
        val size = data.size
        data.clear()
        notifyItemRangeRemoved(0, size)
    }

    fun addAll(items: Array<out ItemData>) {
        data.addAll(items)
        notifyItemRangeInserted(0, items.size)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemIcon: ImageView = itemView.findViewById(R.id.itemIcon)
        val itemName: TextView = itemView.findViewById(R.id.itemName)
        val itemDetail: TextView = itemView.findViewById(R.id.itemDetail)
    }

}