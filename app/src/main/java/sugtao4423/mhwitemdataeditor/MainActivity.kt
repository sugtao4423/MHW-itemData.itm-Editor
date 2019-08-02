package sugtao4423.mhwitemdataeditor

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.PermissionChecker
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

class MainActivity : AppCompatActivity() {

    companion object {
        const val CHUNK_FILE_START_POSITION = 38
    }

    private lateinit var itemListAdapter: ItemListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        itemListAdapter = ItemListAdapter(this)
        findViewById<ItemDataRecyclerView>(R.id.itemDataRecyclerView).adapter = itemListAdapter

        loadChunkFile()
    }

    private fun loadChunkFile() {
        AlertDialog.Builder(this).apply {
            setCancelable(false)
            setTitle(R.string.choose_file)
            setItems(R.array.choose_file_items) { _, which ->
                when (which) {
                    0 -> loadBuiltInChunkFile()
                    else -> loadLocalFile()
                }
            }
            show()
        }
    }

    private fun loadBuiltInChunkFile() {
        val title = "itemData.itm"
        val items = arrayOf(
                "chunk0",
                "chunk3",
                "chunk5",
                "chunk7",
                "chunk9",
                "chunk10"
        )
        AlertDialog.Builder(this).apply {
            setCancelable(false)
            setTitle(title)
            setItems(items) { _, which ->
                val selectedChunkFileName = "itemData_${items[which]}.itm"
                try {
                    val inputStream = assets.open("itemData.itm/$selectedChunkFileName")
                    fileOpen(inputStream)
                } catch (e: IOException) {
                    fileOpenFailed()
                }
            }
            show()
        }
    }

    private fun loadLocalFile() {
        if (!hasWriteExternalStoragePermission()) {
            requestWriteExternalStoragePermission()
            return
        }
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, 1919)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1919 && resultCode == RESULT_OK && data != null) {
            val dataUri = data.data
            if (dataUri == null) {
                fileOpenFailed()
                return
            }

            try {
                val inputStream = contentResolver.openInputStream(dataUri)
                fileOpen(inputStream!!)
            } catch (e: FileNotFoundException) {
                fileOpenFailed()
            }
        } else {
            fileOpenFailed()
        }
    }

    private fun fileOpen(inputStream: InputStream) {
        val chunkFile = ChunkFileUtils.file2bytes(inputStream)
        if (chunkFile.isEmpty()) {
            fileOpenFailed()
            return
        }

        val itemDataArray = arrayListOf<ItemData>()
        for (i in CHUNK_FILE_START_POSITION until chunkFile.size step 32) {
            val itemData = ItemData(chunkFile.sliceArray(i until i + 32))
            itemDataArray.add(itemData)
        }
        itemListAdapter.addAll(itemDataArray.toTypedArray())
    }

    private fun fileOpenFailed() {
        Toast.makeText(applicationContext, R.string.file_open_failed, Toast.LENGTH_LONG).show()
        loadChunkFile()
    }

    private fun hasWriteExternalStoragePermission(): Boolean {
        val writeExternalStorage = PermissionChecker.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return writeExternalStorage == PackageManager.PERMISSION_GRANTED
    }

    private fun requestWriteExternalStoragePermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 810)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != 810) {
            return
        }
        if (permissions[0] == Manifest.permission.WRITE_EXTERNAL_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadLocalFile()
        } else {
            Toast.makeText(applicationContext, R.string.permission_rejected, Toast.LENGTH_LONG).show()
            loadChunkFile()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(0, Menu.FIRST, Menu.NONE, R.string.reload)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == Menu.FIRST) {
            itemListAdapter.clear()
            loadChunkFile()
        }
        return super.onOptionsItemSelected(item)
    }

}
