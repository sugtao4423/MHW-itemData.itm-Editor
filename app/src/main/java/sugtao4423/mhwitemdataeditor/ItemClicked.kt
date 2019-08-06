package sugtao4423.mhwitemdataeditor

import android.support.design.widget.TextInputEditText
import android.support.v7.app.AlertDialog
import android.view.View

class ItemClicked : ItemListAdapter.OnItemClickListener {

    override fun onItemClicked(itemListAdapter: ItemListAdapter, clickedItemData: ItemData, notifyItemChangedPosition: Int) {
        val context = itemListAdapter.context

        val editorView = View.inflate(context, R.layout.item_edit, null)
        val editCarry = editorView.findViewById<TextInputEditText>(R.id.carry)
        val editSell = editorView.findViewById<TextInputEditText>(R.id.sell)
        val editBuy = editorView.findViewById<TextInputEditText>(R.id.buy)

        editCarry.setText(clickedItemData.carry.toString())
        editSell.setText(clickedItemData.sell.toString())
        editBuy.setText(clickedItemData.buy.toString())

        AlertDialog.Builder(context).apply {
            setTitle(ItemDataUtils.getItemName(context, clickedItemData))
            setView(editorView)
            setNegativeButton(R.string.cancel, null)
            setPositiveButton(R.string.ok) { _, _ ->
                clickedItemData.carry = editCarry.text.toString().toInt()
                clickedItemData.sell = editSell.text.toString().toInt()
                clickedItemData.buy = editBuy.text.toString().toInt()

                itemListAdapter.notifyItemChanged(notifyItemChangedPosition)
            }
            show()
        }
    }

}