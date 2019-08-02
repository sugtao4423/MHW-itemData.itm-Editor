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
        const val ITM_FILE_START_POSITION = 38
        const val ITM_FILE_SIZE = 32006
        val ITM_FILE_HEADER = ubyteArrayOf(
                0xAEu, 0x00u, 0xE8u, 0x03u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x01u, 0x00u, 0x00u, 0x00u, 0x00u,
                0x01u, 0x01u, 0x01u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0xFFu, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,
                0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u
        )

        private const val REQUEST_CODE_SELECT_LOCAL_FILE = 1919
        private const val PERMISSION_REQUEST_CODE_LOAD_FILE = 810
        private const val PERMISSION_REQUEST_CODE_SAVE_FILE = 931
    }

    private lateinit var itemListAdapter: ItemListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        itemListAdapter = ItemListAdapter(this)
        itemListAdapter.onItemClickListener = ItemClicked()
        findViewById<ItemDataRecyclerView>(R.id.itemDataRecyclerView).adapter = itemListAdapter

        loadItmFile()
    }

    private fun loadItmFile() {
        AlertDialog.Builder(this).apply {
            setCancelable(false)
            setTitle(R.string.choose_file)
            setItems(R.array.choose_file_items) { _, which ->
                when (which) {
                    0 -> loadBuiltInItmFile()
                    else -> loadLocalFile()
                }
            }
            show()
        }
    }

    private fun loadBuiltInItmFile() {
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
                val selectedItmFileName = "itemData_${items[which]}.itm"
                try {
                    val inputStream = assets.open("itemData.itm/$selectedItmFileName")
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
            requestWriteExternalStoragePermission(PERMISSION_REQUEST_CODE_LOAD_FILE)
            return
        }
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, REQUEST_CODE_SELECT_LOCAL_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_LOCAL_FILE && resultCode == RESULT_OK && data != null) {
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
        val itmFile = ItmFileUtils.file2bytes(inputStream)
        if (itmFile.isEmpty()) {
            fileOpenFailed()
            return
        }
        var isValid = (itmFile.size == ITM_FILE_SIZE)
        ITM_FILE_HEADER.mapIndexed { index: Int, uByte: UByte ->
            if (uByte != itmFile[index]) {
                isValid = false
            }
        }
        if (!isValid) {
            fileOpenFailed(true)
            return
        }

        val itemDataArray = arrayListOf<ItemData>()
        for (i in ITM_FILE_START_POSITION until itmFile.size step 32) {
            val itemData = ItemData(itmFile.sliceArray(i until i + 32))
            itemDataArray.add(itemData)
        }
        itemListAdapter.addAll(itemDataArray.toTypedArray())
    }

    private fun fileOpenFailed(isInvalidFile: Boolean = false) {
        if (isInvalidFile) {
            Toast.makeText(applicationContext, R.string.is_not_itm_file, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(applicationContext, R.string.file_open_failed, Toast.LENGTH_LONG).show()
        }
        loadItmFile()
    }

    private fun saveItmFile() {
        if (!hasWriteExternalStoragePermission()) {
            requestWriteExternalStoragePermission(PERMISSION_REQUEST_CODE_SAVE_FILE)
            return
        }
        SaveItmFile(this, ITM_FILE_HEADER, itemListAdapter.data)
    }

    private fun hasWriteExternalStoragePermission(): Boolean {
        val writeExternalStorage = PermissionChecker.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return writeExternalStorage == PackageManager.PERMISSION_GRANTED
    }

    private fun requestWriteExternalStoragePermission(permissionRequestCode: Int) {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), permissionRequestCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != PERMISSION_REQUEST_CODE_LOAD_FILE && requestCode != PERMISSION_REQUEST_CODE_SAVE_FILE) {
            return
        }
        if (permissions[0] == Manifest.permission.WRITE_EXTERNAL_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            when (requestCode) {
                PERMISSION_REQUEST_CODE_LOAD_FILE -> loadLocalFile()
                PERMISSION_REQUEST_CODE_SAVE_FILE -> saveItmFile()
            }
        } else {
            Toast.makeText(applicationContext, R.string.permission_rejected, Toast.LENGTH_LONG).show()
            if (requestCode == PERMISSION_REQUEST_CODE_LOAD_FILE) {
                loadItmFile()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(0, Menu.FIRST, Menu.NONE, R.string.reload)
        menu?.add(0, Menu.FIRST + 1, Menu.NONE, R.string.save_as)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            Menu.FIRST -> {
                itemListAdapter.clear()
                loadItmFile()
            }
            Menu.FIRST + 1 -> saveItmFile()
        }
        return super.onOptionsItemSelected(item)
    }

}
