//
//  TabViewController.swift
//  ios-app
//
//  Created by ECL User on 6/5/20.
//  Copyright © 2020 IceRock Development. All rights reserved.
//
/*
import UIKit
import MaterialComponents
//import MaterialComponents.MaterialBottomNavigation_Theming

class TabViewController: UITabBarController, MDCBottomNavigationBarDelegate {
    //var colorScheme = MDCSemantic.ColorScheme()
    let bottomNavBar = MDCBottomNavigationBar()
    
    override func viewDidLoad() {
        //colorScheme.backgroundColor = .white
        //view.backgroundColor = colorScheme.backgroundColor
        let tabBarItem1 = UITabBarItem(title: "Home", image: UIImage(named: "Home"), tag: 0)
        let tabBarItem2 = UITabBarItem(title: "Messages", image: UIImage(named: "Favorites"), tag: 1)
        let tabBarItem3 = UITabBarItem(title: "Favorites", image: UIImage(named: "Search"), tag: 2)
        let tabBarItem4 = UITabBarItem(title: "User", image: UIImage(named: "User"), tag: 3)
        tabBarItem2.selectedImage = UIImage(named: "Favorite")
        bottomNavBar.items = [ tabBarItem1, tabBarItem2, tabBarItem3, tabBarItem4 ]

        bottomNavBar.selectedItem = tabBarItem1
        view.addSubview(bottomNavBar)
        bottomNavBar.delegate = self
        //MDCBottomNavigationBarColorThemer.applySemanticColorScheme(colorScheme, toBottomNavigation: bottomNavBar)
    }
    
    
    func bottomNavigationBar(_ bottomNavigationBar: MDCBottomNavigationBar, didSelect item: UITabBarItem){
        guard let fromView = selectedViewController?.view, let toView = customizableViewControllers?[item.tag].view else {
            return
        }

        if fromView != toView {
            UIView.transition(from: fromView, to: toView, duration: 0.3, options: [.transitionCrossDissolve], completion: nil)
        }
        self.selectedIndex = item.tag
    }

    func layoutBottomNavBar() {
        let size = bottomNavBar.sizeThatFits(view.bounds.size)
        let bottomNavBarFrame = CGRect(x: 0,
                                       y: view.bounds.height - size.height,
                                       width: size.width,
                                       height: size.height)
        bottomNavBar.frame = bottomNavBarFrame
    }
    
    override func viewWillLayoutSubviews() {
        super.viewWillLayoutSubviews()
        layoutBottomNavBar()
    }

}

 
 */
