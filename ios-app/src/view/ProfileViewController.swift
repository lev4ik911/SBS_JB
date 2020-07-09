//
//  ProfileViewController.swift
//  ios-app
//
//  Created by ECL User on 6/12/20.
//  Copyright © 2020 IceRock Development. All rights reserved.
//

import Foundation
import UIKit
import MultiPlatformLibrary


class ProfileViewController : UIViewController  {
    
    var loginView = LoginView()
    var userView = UserView()
    
    override func viewDidLoad() {
        //TODO: check if user logged
        self.view.addSubview(loginView)
        //self.addSubView(loginView)
    }
    
}
