package sugtao4423.mhwitemdataeditor

import android.content.Context
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AlertDialog
import android.view.View

class ItemClicked : ItemListAdapter.OnItemClickListener {

    override fun onItemClicked(context: Context, itemListAdapter: ItemListAdapter, position: Int) {
        val item = itemListAdapter.data[position]

        val editorView = View.inflate(context, R.layout.item_edit, null)
        val editCarry = editorView.findViewById<TextInputEditText>(R.id.carry)
        val editSell = editorView.findViewById<TextInputEditText>(R.id.sell)
        val editBuy = editorView.findViewById<TextInputEditText>(R.id.buy)

        editCarry.setText(item.carry.toString())
        editSell.setText(item.sell.toString())
        editBuy.setText(item.buy.toString())

        AlertDialog.Builder(context).apply {
            setTitle(ItemDataUtils.getItemName(context, item))
            setView(editorView)
            setNegativeButton(R.string.cancel, null)
            setPositiveButton(R.string.ok) { _, _ ->
                item.carry = editCarry.text.toString().toInt()
                item.sell = editSell.text.toString().toInt()
                item.buy = editBuy.text.toString().toInt()

                itemListAdapter.notifyItemChanged(position)
            }
            show()
        }
    }

}