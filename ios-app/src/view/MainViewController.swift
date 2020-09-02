//
//  MainViewController.swift
//  ios-app
//
//  Created by ECL User on 6/5/20.
//  Copyright © 2020 IceRock Development. All rights reserved.
//

import UIKit
import Foundation
import MultiPlatformLibrary

class MainViewController : UITabBarController {
    
    var settings : LocalSettings!
    
    @IBOutlet var mainTabBar: UITabBar!

    override func viewDidLoad() {
        super.viewDidLoad()
        
        settings = LocalSettings(settings: AppleSettings(delegate: UserDefaults.standard))
        /*
        if (settings.accessToken.isEmpty){
            print("hide")
            hideTabBarItems()
        }
        else{
            print("show")
            showTabBarItems()
        }
        */
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        print(settings.accessToken)
        
    }
    
    override func tabBar(_ tabBar: UITabBar, didSelect item: UITabBarItem) {
        print(settings.accessToken)
        switch item.tag {
        case 1:
            //Home view
            print("Home pressed")
        case 2:
            // Favorites view
            print("Favorites pressed")
            if settings.accessToken.isEmpty {
                self.tabBarController?.selectedIndex = 1
                LoginAlert.show(controller: self, mainTabBarcontroller: self)
            }
        case 3:
            //Add new
            print("Add new pressed")
            //TODO: route to another storyboard
            if settings.accessToken.isEmpty{
                self.tabBarController?.selectedIndex = 1
                LoginAlert.show(controller: self, mainTabBarcontroller: self)
            }
            let storyboard = UIStoryboard(name: "AddEvent", bundle: nil)
            let vc = storyboard.instantiateViewController(withIdentifier: "AddEventID") as UIViewController
            present(vc, animated: true, completion: nil)
        case 4:
            print("Search pressed")
        case 5:
            print("User pressed")
        default:
            print("default")
        }
    }
    
    func showTabBarItems(){
        
    }
    
    func hideTabBarItems(){
        var items = tabBarController?.toolbarItems
        items?.remove(at: 2)
    }
}
