/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */


import UIKit
import MultiPlatformLibrary

@UIApplicationMain
class AppDelegate: NSObject, UIApplicationDelegate {
    
    var window: UIWindow?
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        
        /*
            let homeVC = HomeViewController()
            let navigationHomeVC = UINavigationController(rootViewController: homeVC)
            let homeIcon = UIImage(systemName: "house")
            let homeItem = UITabBarItem(title: nil, image: homeIcon, tag: 0)
            navigationHomeVC.tabBarItem = homeItem

            let favoritesVC = FavoritesViewController()
            let searchVC = SearchViewController()
            let profileVC = ProfileViewController()
            //let addInstructionVC = AddInstructionViewController()

            let tabBarController = MainViewController()
            tabBarController.viewControllers = [navigationHomeVC, favoritesVC, searchVC, profileVC]
            tabBarController.selectedViewController = homeVC
            
            window?.rootViewController = tabBarController
            window?.makeKeyAndVisible()
         */
        
        
      // Override point for customization after application launch.
      return true
    }
    
    /*
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil) -> Bool {
        
        // create factory of shared module - it's main DI component of application.
        // Provide ViewModels of all features.
        // Input is platform-specific:
        // * settings - settings platform storage for https://github.com/russhwolf/multiplatform-settings
        // * antilog - platform logger with println for https://github.com/AAkira/Napier
        // * baseUrl - server url from platform build configs (allows use schemes for server configs)
        // * newsUnitsFactory - platform factory of UITableView items for https://github.com/icerockdev/moko-units
        AppComponent.factory = SharedFactory(
            settings: AppleSettings(delegate: UserDefaults.standard),
            antilog: DebugAntilog(defaultTag: "MPP"),
            baseUrl: "https://newsapi.org/v2/",
            newsUnitsFactory: NewsListUnitsFactory()
        )
        return true
    }
 */
}
