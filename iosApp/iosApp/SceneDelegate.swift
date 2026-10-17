//
//  SceneDelegate.swift
//  iosApp
//
//  Created by Daniel Hiebeler on 17.10.26.
//

import UIKit
import ComposeApp

class SceneDelegate: UIResponder, UIWindowSceneDelegate {
    var window: UIWindow?

    private var urlCallback: IosUrlCallback {
        (UIApplication.shared.delegate as! AppDelegate).urlCallback
    }

    func scene(
        _ scene: UIScene,
        willConnectTo session: UISceneSession,
        options connectionOptions: UIScene.ConnectionOptions
    ) {
        guard let windowScene = scene as? UIWindowScene else { return }

        let window = UIWindow(windowScene: windowScene)
        window.rootViewController = AppViewControllerKt.AppViewController(urlCallback: urlCallback)
        window.makeKeyAndVisible()
        self.window = window

        // Cold launch via URL (e.g. OAuth redirect): it arrives here, not in openURLContexts
        if let url = connectionOptions.urlContexts.first?.url {
            urlCallback.onRedirect(url.absoluteString)
        }
    }

    // App already running / backgrounded
    func scene(_ scene: UIScene, openURLContexts URLContexts: Set<UIOpenURLContext>) {
        for context in URLContexts {
            urlCallback.onRedirect(context.url.absoluteString)
        }
    }
}
