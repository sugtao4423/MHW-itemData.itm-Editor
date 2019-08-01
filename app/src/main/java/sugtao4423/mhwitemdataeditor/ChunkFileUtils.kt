package sugtao4423.mhwitemdataeditor

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

    }

}