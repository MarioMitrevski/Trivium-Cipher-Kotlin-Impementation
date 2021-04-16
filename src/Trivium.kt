import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.math.BigInteger

object Trivium {
    private var internalState: BooleanArray
    private const val KEY_STREAM_LENGTH = 512
    private const val KEY_LENGTH = 80
    private const val IV_LENGTH = 80

    init {
        internalState = BooleanArray(288)
    }

    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val bufferedReader = BufferedReader(InputStreamReader(System.`in`))
        val keyBooleanArray = initKey(bufferedReader)
        val ivBooleanArray = initIV(bufferedReader)

        internalState = keyBooleanArray.copyInto(internalState)
        internalState.fill(false, 80, 92)
        internalState = ivBooleanArray.copyInto(internalState, 93)
        internalState.fill(false, 173, 284)
        internalState.fill(true, 285, 287)

        setUpInternalState()
        val keyStream = generateKeyStream()

        println(binaryToHex(keyStream))
    }

    private fun generateKeyStream(): BooleanArray {
        val keyStream = BooleanArray(KEY_STREAM_LENGTH)
        for (i in 0 until KEY_STREAM_LENGTH) {
            val t1 = internalState[65].xor(internalState[92])
            val t2 = internalState[161].xor(internalState[176])
            val t3 = internalState[242].xor(internalState[287])
            val z = t1.xor(t2).xor(t3)
            keyStream[i] = z

            rotateInternalState()
        }
        return keyStream
    }

    private fun setUpInternalState() {
        for (i in 0..1151) {
            rotateInternalState()
        }
    }

    private fun rotateInternalState() {
        val t1 = internalState[65].xor((internalState[90].and(internalState[91]))).xor(internalState[92]).xor(internalState[170])
        val t2 = internalState[161].xor((internalState[174].and(internalState[175]))).xor(internalState[176]).xor(internalState[263])
        val t3 = internalState[242].xor((internalState[285].and(internalState[286]))).xor(internalState[287]).xor(internalState[68])

        for (j in 92 downTo 1) {
            internalState[j] = internalState[j - 1]
        }
        internalState[0] = t3
        for (j in 176 downTo 94) {
            internalState[j] = internalState[j - 1]
        }
        internalState[93] = t1
        for (j in 287 downTo 178) {
            internalState[j] = internalState[j - 1]
        }
        internalState[177] = t2
    }

    private fun initIV(bufferedReader: BufferedReader): BooleanArray {
        println("Внесете го $IV_LENGTH битниот иницијализирачки вектор во хексадецимален запис - (0-F) 20 карактери")
        val iv = bufferedReader.readLine()
        return hexToBinary(iv)
    }

    private fun initKey(bufferedReader: BufferedReader): BooleanArray {
        println("Внесете го $KEY_LENGTH битниот клуч во хексадецимален запис - (0-F) 20 карактери")
        val key = bufferedReader.readLine()
        return hexToBinary(key)
    }

    private fun binaryToHex(keyStream: BooleanArray): String {
        val stringBuilder = StringBuilder()
        for (bit in keyStream) {
            stringBuilder.append(if (bit) "1" else "0")
        }
        return BigInteger(stringBuilder.toString(), 2).toString(16)
    }

    private fun hexToBinary(hex: String): BooleanArray {
        val booleanArray = BooleanArray(hex.length * 4)

        hex.forEachIndexed { hexIndex, hexCharacter ->
            val decimal = hexCharacter.toString().toInt(16)
            val binaryString = Integer.toBinaryString(decimal)

            binaryString.forEachIndexed { index, c ->
                booleanArray[hexIndex * 4 + index] = c == '1'
            }
        }
        return booleanArray
    }
}