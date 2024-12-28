package com.otus.securehomework.data.encryption

import javax.crypto.SecretKey

interface SecretKeyManager {
    suspend fun getSecretKey(keyName: String): SecretKey
}