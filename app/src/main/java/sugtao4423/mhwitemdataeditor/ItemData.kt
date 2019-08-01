package sugtao4423.mhwitemdataeditor

data class ItemData(val uByte32Array: UByteArray) {

    var id: Int
        get() {
            return ubyteArrayOf(uByte32Array[0], uByte32Array[1]).let {
                ChunkFileUtils.chunkFileByteFormat2Int(it)
            }
        }
        set(value) {
            ChunkFileUtils.int2ChunkFileByteFormat(value, 2).mapIndexed { index: Int, uByte: UByte ->
                uByte32Array[index] = uByte
            }
        }

    var type: Int
        get() {
            return uByte32Array[5].toInt()
        }
        set(value) {
            uByte32Array[5] = value.toUByte()
        }

    var rare: Int
        get() {
            return uByte32Array[9].toInt()
        }
        set(value) {
            uByte32Array[9] = value.toUByte()
        }

    var carry: Int
        get() {
            return uByte32Array[10].toInt()
        }
        set(value) {
            uByte32Array[10] = value.toUByte()
        }

    var icon: Int
        get() {
            return uByte32Array[18].toInt()
        }
        set(value) {
            uByte32Array[18] = value.toUByte()
        }

    var iconColor: Int
        get() {
            return uByte32Array[22].toInt()
        }
        set(value) {
            uByte32Array[22] = value.toUByte()
        }

    var sell: Int
        get() {
            return ubyteArrayOf(uByte32Array[24], uByte32Array[25], uByte32Array[26], uByte32Array[27]).let {
                ChunkFileUtils.chunkFileByteFormat2Int(it)
            }
        }
        set(value) {
            ChunkFileUtils.int2ChunkFileByteFormat(value, 4).mapIndexed { index: Int, uByte: UByte ->
                uByte32Array[24 + index] = uByte
            }
        }

    var buy: Int
        get() {
            return ubyteArrayOf(uByte32Array[28], uByte32Array[29], uByte32Array[30], uByte32Array[31]).let {
                ChunkFileUtils.chunkFileByteFormat2Int(it)
            }
        }
        set(value) {
            ChunkFileUtils.int2ChunkFileByteFormat(value, 4).mapIndexed { index: Int, uByte: UByte ->
                uByte32Array[28 + index] = uByte
            }
        }

}
