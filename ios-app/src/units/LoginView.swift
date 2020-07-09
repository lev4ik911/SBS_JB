//
//  LoginView.swift
//  ios-app
//
//  Created by ECL User on 6/22/20.
//  Copyright © 2020 IceRock Development. All rights reserved.
//

import Foundation
import UIKit


class LoginView : UIView {
    
    @IBOutlet var userText: UITextField!
    @IBOutlet var passwordText: UITextField!
    
    @IBOutlet var forgorPasswordButton: UIButton!
    
    @IBAction func forgotPasswordClick(_ sender: Any) {
    }
    
    @IBOutlet var signUpButton: UIButton!
    
    @IBAction func signUpButtonClick(_ sender: Any) {
        print("SignUp pressed - load ")
        //let signUpView = SignUpView()
        //self.addSubView(signUpView)
    }
    
    @IBOutlet var signInButton: UIButton!
    
    @IBAction func signInButtonClick(_ sender: Any) {
        print("SignIn pressed - load User.xib")
        
        //self.addSubView(customInfoView)
    
    }
    
    
}
