package sugtao4423.mhwitemdataeditor

import java.io.ByteArrayOutputStream
import java.io.InputStream

class ChunkFileUtils {

    companion object {

        @JvmStatic
        fun chunkFileByteFormat2Int(uByteArray: UByteArray): Int {
            var i = 0
            uByteArray.reversedArray().mapIndexed { index: Int, uByte: UByte ->
                i = i or uByte.toInt()
                if (index != (uByteArray.size - 1)) {
                    i = i shl UByte.SIZE_BITS
                }
            }
            return i
        }

        @JvmStatic
        fun int2ChunkFileByteFormat(num: Int, byteSize: Int): UByteArray {
            val result = UByteArray(byteSize)
            for (i in 0 until byteSize) {
                result[i] = (num ushr i * UByte.SIZE_BITS).toUByte()
            }
            return result
        }

        @JvmStatic
        fun file2bytes(inputStream: InputStream): UByteArray {
            return try {
                val bout = ByteArrayOutputStream()
                val buffer = ByteArray(1024)
                var len = inputStream.read(buffer)
                while (len > 0) {
                    bout.write(buffer, 0, len)
                    len = inputStream.read(buffer)
                }
                inputStream.close()
                val uByteArray = UByteArray(bout.size())
                bout.toByteArray().mapIndexed { index, byte ->
                    uByteArray[index] = byte.toUByte()
                }
                uByteArray
            } catch (e: Exception) {
                UByteArray(0)
            }
        }

    }

}