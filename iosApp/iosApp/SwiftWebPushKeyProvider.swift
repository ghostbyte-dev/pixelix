//
//  SwiftWebPushKeyProvider.swift
//  iosApp
//
//  Created by Daniel Hiebeler on 17.10.26.
//

import Foundation
import CryptoKit
import Security

struct WebPushKeys {
    let privateKey: P256.KeyAgreement.PrivateKey
    let auth: Data   // 16 bytes
}

enum WebPushKeyStore {
    static let service = "webpush"
    static let accessGroup: String? = nil   // "<TeamID>.your.shared.group" once the NSE exists

    private static func query(_ account: String) -> [String: Any] {
        var q: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrService as String: service,
            kSecAttrAccount as String: account,
        ]
        if let g = accessGroup { q[kSecAttrAccessGroup as String] = g }
        return q
    }

    // Stored blob layout: 32 bytes raw private key + 16 bytes auth
    static func read(account: String) -> WebPushKeys? {
        var q = query(account)
        q[kSecReturnData as String] = true
        q[kSecMatchLimit as String] = kSecMatchLimitOne
        var out: CFTypeRef?
        guard SecItemCopyMatching(q as CFDictionary, &out) == errSecSuccess,
              let blob = out as? Data, blob.count == 48,
              let key = try? P256.KeyAgreement.PrivateKey(rawRepresentation: Data(blob.prefix(32)))
        else { return nil }
        return WebPushKeys(privateKey: key, auth: Data(blob.suffix(16)))
    }

    static func loadOrCreate(account: String) -> WebPushKeys? {
        if let existing = read(account: account) { return existing }

        let key = P256.KeyAgreement.PrivateKey()
        var auth = Data(count: 16)
        let status = auth.withUnsafeMutableBytes {
            SecRandomCopyBytes(kSecRandomDefault, 16, $0.baseAddress!)
        }
        guard status == errSecSuccess else { return nil }

        var q = query(account)
        q[kSecValueData as String] = key.rawRepresentation + auth
        // Lets the NSE read it while the device is locked
        q[kSecAttrAccessible as String] = kSecAttrAccessibleAfterFirstUnlock
        guard SecItemAdd(q as CFDictionary, nil) == errSecSuccess else { return nil }
        return WebPushKeys(privateKey: key, auth: auth)
    }

    static func delete(account: String) {
        SecItemDelete(query(account) as CFDictionary)
    }
}

extension Data {
    func base64URLNoPadding() -> String {
        base64EncodedString()
            .replacingOccurrences(of: "+", with: "-")
            .replacingOccurrences(of: "/", with: "_")
            .replacingOccurrences(of: "=", with: "")
    }
}
