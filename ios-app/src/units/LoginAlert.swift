//
//  LoginAlert.swift
//  ios-app
//
//  Created by ECL User on 7/10/20.
//  Copyright © 2020 IceRock Development. All rights reserved.
//

import Foundation
import UIKit

class LoginAlert {
    static func show(controller: UIViewController, mainTabBarcontroller: UITabBarController?){
        let msg = "Authorization is required to perform this action"
        let alert = UIAlertController(title: "Authorization required", message: msg, preferredStyle: UIAlertController.Style.alert )

        let save = UIAlertAction(title: "Login", style: .default) { (alertAction) in
            //route to login
            mainTabBarcontroller?.selectedIndex = 4
        }
        let cancel = UIAlertAction(title: "Cancel", style: .default) { (alertAction) in }

        alert.addAction(save)
        alert.addAction(cancel)

        controller.present(alert, animated:true, completion: nil)
    }
}
