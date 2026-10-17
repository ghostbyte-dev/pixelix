package com.daniebeler.pfpixelix.utils

class WebPushKeyPair(val publicKey: String, val auth: String)

interface WebPushKeyProvider {
    fun loadOrCreate(account: String): WebPushKeyPair?
    fun delete(account: String)
}

object WebPushKeyProviderHolder {
    lateinit var provider: WebPushKeyProvider
}