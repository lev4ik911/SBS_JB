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


class ProfileViewController : UIViewController {

    @IBOutlet var parentView: UIView!
    
    var loginViewModel: LoginViewModel!
    var profileViewModel: ProfileViewModel!
    var signUpViewModel: RegisterViewModel!
    
    var cnt = 0
    
    override func viewDidLoad() {
        /*
        let view = parentView as! LoginView
        let user = view.userText.text
        let pass = view.passwordText.text
        Toast.show(message: "login", controller: self)
        loginViewModel = LoginViewModel(settings: AppleSettings(delegate: UserDefaults.standard),
                                        eventsDispatcher: EventsDispatcher(listener: self), systemInfo: self)
        */
        /*
        loginViewModel.login.setValue(value: user, async: false)
        loginViewModel.password.setValue(value: pass, async: false)
        loginViewModel.onLoginButtonClick()
        */
        //TODO: check if user logged
        //self.profileView.addSubview(loginView)
        //parentView = LoginView.loadViewFromNib(
        //parentView.layoutSubviews()
        //loginView = LoginView().self
        //cnt = parentView.subviews.count
    }
    
    

}

extension ProfileViewController : SystemInformation {
    func getAppVersion() -> String {
        return ""
    }
    
    func getDeviceID() -> String {
        return ""
    }
    
    func getDeviceName() -> String {
        return ""
    }
}

extension ProfileViewController : LoginViewModelEventsListener  {
    func flipToLogin() {
        
    }
    
    func flipToPassword() {
        
    }
    
    func onRegister() {
        
    }
    
    func onResetPassword() {
        
    }
    
    func routeToProfile(userId: String) {
        
    }
    
    func showToast(msg: ToastMessage) {
        Toast.show(message: msg.message, controller: self)
    }
}
