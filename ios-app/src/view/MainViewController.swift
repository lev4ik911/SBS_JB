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
    var lastSelectedIdx = 0

    override func viewDidLoad() {
        super.viewDidLoad()
        
        settings = LocalSettings(settings: AppleSettings(delegate: UserDefaults.standard))
        /*
        if (settings.accessToken.isEmpty){
            print("hide")
            //hideTabBarItems()
        }
        else{
            print("show")
            showTabBarItems()
        }
        */
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        //hideTabBarItems()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    override func tabBar(_ tabBar: UITabBar, didSelect item: UITabBarItem) {
        LoginAlert.navigate_index = -1
        switch item.tag {
        case 1:
            //Home view
            print("Home pressed")
            lastSelectedIdx = 0
        case 2:
            // Favorites view
            print("Favorites pressed")
            if (settings.accessToken.isEmpty) {
                LoginAlert.navigate_index = lastSelectedIdx
                LoginAlert.show(controller: self, mainTabBarcontroller: self)
            } else {
                lastSelectedIdx = 1
            }
        case 3:
            //Add new
            print("Add new pressed")
            //TODO: route to another storyboard
            if (settings.accessToken.isEmpty) {
                LoginAlert.navigate_index = lastSelectedIdx
                LoginAlert.show(controller: self, mainTabBarcontroller: self)
            } else {
                let storyboard = UIStoryboard(name: "AddEvent", bundle: nil)
                let vc = storyboard.instantiateViewController(withIdentifier: "AddEventID") as UIViewController
                vc.modalPresentationStyle = .fullScreen
                present(vc, animated: true, completion: nil)
            }
        case 4:
            print("Search pressed")
            lastSelectedIdx = 3
        case 5:
            print("User pressed")
            lastSelectedIdx = 4
        default:
            print("default")
            lastSelectedIdx = 0
        }
    }
    
    func showTabBarItems(){
        
    }
    
    func hideTabBarItems(){
        //mainTabBar.items?.remove(at: 1)
        
        tabBarController?.viewControllers?.remove(at: 1)
        //var items = tabBarController?.toolbarItems
        //items?.remove(at: 2)
    }
}
