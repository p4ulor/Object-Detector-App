package p4ulor.obj.detector.data.sources.local.utils

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

object Crypto {
    private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
    private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
    private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
    private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"

    private val cipher = Cipher.getInstance(TRANSFORMATION)
    private val keyStore = KeyStore
        .getInstance("AndroidKeyStore")
        .apply {
            load(null)
        }

    private fun getKey(keyId: String): SecretKey {
        val existingKey = keyStore.getEntry(keyId, null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createKey(keyId)
    }

    private fun createKey(keyId: String): SecretKey {
        return KeyGenerator
            .getInstance(ALGORITHM)
            .apply {
                init(
                    KeyGenParameterSpec.Builder(
                        keyId,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                    ).setBlockModes(BLOCK_MODE)
                        .setEncryptionPaddings(PADDING)
                        .setRandomizedEncryptionRequired(true)
                        .build()
                )
            }
            .generateKey()
    }

    fun encrypt(keyId: String,bytes: ByteArray): ByteArray {
        cipher.init(Cipher.ENCRYPT_MODE, getKey(keyId))
        val initializationVector = cipher.iv
        val encrypted = cipher.doFinal(bytes)
        return initializationVector + encrypted
    }

    fun decrypt(keyId: String, bytes: ByteArray): ByteArray {
        val initializationVector = bytes.copyOfRange(0, cipher.blockSize)
        cipher.init(Cipher.DECRYPT_MODE, getKey(keyId), IvParameterSpec(initializationVector))
        val data = bytes.copyOfRange(cipher.blockSize, bytes.size)
        return cipher.doFinal(data)
    }
}