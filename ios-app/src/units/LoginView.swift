//
//  LoginView.swift
//  ios-app
//
//  Created by ECL User on 6/22/20.
//  Copyright © 2020 IceRock Development. All rights reserved.
//

import Foundation
import UIKit
import MultiPlatformLibrary
import MultiPlatformLibraryMvvm
import SkyFloatingLabelTextField

class LoginView : UIView {
    
    let nibName = "LoginView"
    
    @IBOutlet var userText: SkyFloatingLabelTextField!
    @IBOutlet var passwordText: SkyFloatingLabelTextField!
    
    @IBOutlet var forgorPasswordButton: UIButton!
    
    @IBAction func forgotPasswordClick(_ sender: Any) {
        print("Repair password - load SignUpView.xib")
        //TODO: need meeting about this
    }
    
    @IBOutlet var signUpButton: UIButton!
    
    @IBAction func signUpButtonClick(_ sender: Any) {
        print("SignUp pressed - load SignUpView.xib")
        self.parentViewController?.performSegue(withIdentifier: "show_sign_up", sender: self.parentViewController)
        //let signUpView = SignUpView()
        //addSubview(signUpView)
    }
    
    @IBOutlet var mainView: UIView!
    @IBOutlet var signInButton: UIButton!
    
    @IBAction func signInButtonClick(_ sender: Any) {
        print("SignIn pressed - load UserView.xib")

        loginVM.onLoginButtonClick()
    }

    var loginVM: LoginViewModel!
    
    override func willMove(toSuperview newSuperview: UIView?) {
      super.willMove(toSuperview: newSuperview)
      //Do stuff here
        loginVM = LoginViewModel(settings: AppleSettings(delegate: UserDefaults.standard),
        eventsDispatcher: EventsDispatcher(listener: self), systemInfo: self)
        
        userText.bindTextTwoWay(liveData: loginVM.login)
        passwordText.bindTextTwoWay(liveData: loginVM.password)
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)

        guard let view = loadViewFromNib() else { return }
        view.frame = self.bounds
        self.addSubview(view)
        mainView = view
    }

    func loadViewFromNib() -> UIView? {
        let bundle = Bundle(for: type(of: self))
        let nib = UINib(nibName: nibName, bundle: bundle)
        return nib.instantiate(withOwner: self, options: nil).first as? UIView
    }
    
}

extension UIView {
    var parentViewController: UIViewController? {
        var parentResponder: UIResponder? = self
        while parentResponder != nil {
            parentResponder = parentResponder!.next
            if let viewController = parentResponder as? UIViewController {
                return viewController
            }
        }
        return nil
    }
}

extension LoginView :LoginViewModelEventsListener, SystemInformation {
    func flipToLogin() {
        
    }
    
    func flipToPassword() {
        
    }
    
    func onRegister() {
        
    }
    
    func onResetPassword() {
        
    }
    
    func routeToProfile(userId: String) {
        print(userId)
    }
    
    func showToast(msg: ToastMessage) {
        print(msg.message)
    }
    
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
