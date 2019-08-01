package sugtao4423.mhwitemdataeditor

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View

class ItemDataRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : RecyclerView(context, attrs, defStyle) {

    init {
        isVerticalScrollBarEnabled = true
        addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        addItemDecoration(AlternatelyColor())
        layoutManager = LinearLayoutManager(context)
    }

    inner class AlternatelyColor : ItemDecoration() {

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: State) {
            super.getItemOffsets(outRect, view, parent, state)
            val pos = parent.getChildAdapterPosition(view)
            setAlternately(pos, view)
        }

        private fun setAlternately(pos: Int, view: View) {
            view.setBackgroundResource(if (pos % 2 == 0) R.drawable.position0 else R.drawable.position1)
        }

    }

}