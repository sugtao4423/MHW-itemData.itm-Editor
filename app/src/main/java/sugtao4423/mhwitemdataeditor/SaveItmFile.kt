package sugtao4423.mhwitemdataeditor

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Environment
import android.support.v7.app.AlertDialog
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import java.io.File

class SaveItmFile(context: Context, itmFileHeader: UByteArray, itemDataList: ArrayList<ItemData>) {

    init {
        val filePathView = FrameLayout(context)
        val filePathEdit = PrefixEditText(context)
        val margin = ((24 * context.resources.displayMetrics.density) + 0.5).toInt()
        val params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            leftMargin = margin
            rightMargin = margin
        }
        filePathEdit.layoutParams = params
        filePathView.addView(filePathEdit)

        AlertDialog.Builder(context).apply {
            setTitle(R.string.save_as)
            setView(filePathView)
            setNegativeButton(R.string.cancel, null)
            setPositiveButton(R.string.ok) { _, _ ->
                val filePath = filePathEdit.prefix + filePathEdit.text.toString()
                if (File(filePath).exists()) {
                    Toast.makeText(context, R.string.file_already_exists, Toast.LENGTH_LONG).show()
                    return@setPositiveButton
                }

                var fileBinary = itmFileHeader
                itemDataList.map {
                    fileBinary += it.uByte32Array
                }
                val writeResult = ItmFileUtils.bytes2file(fileBinary, filePath)
                if (writeResult) {
                    Toast.makeText(context, R.string.file_saved, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, R.string.file_save_failed, Toast.LENGTH_SHORT).show()
                }
            }
            show()
        }
    }

    inner class PrefixEditText(context: Context) : EditText(context) {

        val prefix = Environment.getExternalStorageDirectory().absolutePath + "/"
        private val prefixRect = Rect()

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            paint.getTextBounds(prefix, 0, prefix.length, prefixRect)
            prefixRect.right += paint.measureText("").toInt()
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }

        override fun onDraw(canvas: Canvas?) {
            super.onDraw(canvas)
            canvas?.drawText(prefix, super.getCompoundPaddingLeft().toFloat(), baseline.toFloat(), paint)
        }

        override fun getCompoundPaddingLeft(): Int {
            return super.getCompoundPaddingLeft() + prefixRect.width()
        }

    }

}