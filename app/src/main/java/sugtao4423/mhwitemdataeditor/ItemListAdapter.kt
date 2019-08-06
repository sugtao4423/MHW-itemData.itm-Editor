package sugtao4423.mhwitemdataeditor

import android.content.Context
import android.graphics.*
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import java.text.NumberFormat
import java.util.*

class ItemListAdapter(val context: Context) : RecyclerView.Adapter<ItemListAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClicked(itemListAdapter: ItemListAdapter, clickedItemData: ItemData, notifyItemChangedPosition: Int)
    }

    var onItemClickListener: OnItemClickListener? = null

    val data = arrayListOf<ItemData>()
    private val filteredData = arrayListOf<ItemData>()
    private val numberFormat = NumberFormat.getNumberInstance(Locale.US)

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_mhw_item, viewGroup, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (filteredData.size <= position) {
            return
        }
        val item = filteredData[position]

        holder.itemIcon.setImageBitmap(getMaskedImage(item))
        holder.itemName.text = ItemDataUtils.getItemName(context, item)
        holder.itemDetail.text = context.getString(R.string.item_detail, item.id, item.carry, numberFormat.format(item.sell), numberFormat.format(item.buy))

        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClicked(this, item, position)
        }
    }

    private fun getMaskedImage(itemData: ItemData): Bitmap {
        val colorImage = BitmapFactory.decodeResource(context.resources, ItemDataUtils.getItemIconColorRes(itemData))
        val itemImage = BitmapFactory.decodeResource(context.resources, ItemDataUtils.getItemIconRes(itemData))

        val masked = Bitmap.createBitmap(itemImage.width, itemImage.height, Bitmap.Config.ARGB_8888)
        Canvas(masked).apply {
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)

            drawBitmap(colorImage, 0f, 0f, null)
            drawBitmap(itemImage, 0f, 0f, paint)
        }
        return masked
    }

    override fun getItemCount(): Int {
        return filteredData.size
    }

    fun clear() {
        val size = filteredData.size
        data.clear()
        filteredData.clear()
        notifyItemRangeRemoved(0, size)
    }

    fun addAll(items: Array<out ItemData>) {
        data.addAll(items)
        filteredData.addAll(items)
        notifyItemRangeInserted(0, items.size)
    }

    fun filter(itemName: String?) {
        filteredData.let {
            val size = it.size
            it.clear()
            notifyItemRangeRemoved(0, size)
        }
        if (itemName.isNullOrBlank()) {
            filteredData.addAll(data)
            notifyItemRangeInserted(0, filteredData.size)
            return
        }

        filteredData.addAll(data.filter {
            val thisName = ItemDataUtils.getItemName(context, it)
            thisName.contains(itemName, true)
        })
        notifyItemRangeInserted(0, filteredData.size)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemIcon: ImageView = itemView.findViewById(R.id.itemIcon)
        val itemName: TextView = itemView.findViewById(R.id.itemName)
        val itemDetail: TextView = itemView.findViewById(R.id.itemDetail)
    }

}