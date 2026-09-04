package com.daniebeler.pfpixelix.domain.service.pushNotifications

import org.unifiedpush.android.embedded_fcm_distributor.EmbeddedDistributorReceiver
import org.unifiedpush.android.embedded_fcm_distributor.Gateway

class EmbeddedDistributor : EmbeddedDistributorReceiver() {
    override val gateway = object : Gateway {
        override val vapid = "BOtNWTDFdl1_u_O9WNXMW9FbjTXMPB7u0ib0HYsLF2wEr3hZYczf6hnOaas6F-CbxRNQwH39GaXVrnxzkAGQpmg"

        override fun getEndpoint(token: String): String {
            return "https://fcm.proxy.pixelix.social/wpfcm?t=$token"
        }
    }
}