//
//  WebPushKeyStore.swift
//  iosApp
//
//  Created by Daniel Hiebeler on 17.10.26.
//

import Foundation
import ComposeApp

final class SwiftWebPushKeyProvider: NSObject, WebPushKeyProvider {
    func loadOrCreate(account: String) -> WebPushKeyPair? {
        guard let keys = WebPushKeyStore.loadOrCreate(account: account) else { return nil }
        return WebPushKeyPair(
            publicKey: keys.privateKey.publicKey.x963Representation.base64URLNoPadding(),
            auth: keys.auth.base64URLNoPadding()
        )
    }
    func delete(account: String) {
        WebPushKeyStore.delete(account: account)
    }
}