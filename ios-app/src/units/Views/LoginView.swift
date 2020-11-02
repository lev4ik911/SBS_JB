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
        //TODO: implement password repairing!!!
    }
    
    @IBOutlet var signUpButton: UIButton!
    @IBAction func signUpButtonClick(_ sender: Any) {
        userText.text = ""
        passwordText.text = ""
        self.parentViewController?.performSegue(withIdentifier: "show_sign_up", sender: self.parentViewController)
    }
    
    @IBOutlet var mainView: UIView!
    
    @IBOutlet var signInButton: UIButton!
    @IBAction func signInButtonClick(_ sender: Any) {
        loginVM.onLoginButtonClick()
    }

    var loginVM: LoginViewModel!
    var settings : LocalSettings!
    
    override func willMove(toSuperview newSuperview: UIView?) {
        super.willMove(toSuperview: newSuperview)
        signInButton.isEnabled = false
        forgorPasswordButton.isEnabled = false
        
        //Do stuff here
        settings = LocalSettings(settings: AppleSettings(delegate: UserDefaults.standard))
        loginVM = LoginViewModel(settings: AppleSettings(delegate: UserDefaults.standard),
        eventsDispatcher: EventsDispatcher(listener: self), systemInfo: self)
        
        userText.addTarget(self, action: #selector(self.textDidChange), for: .editingChanged)
        userText.addTarget(self, action: #selector(self.resetTextDidChange), for: .editingChanged)
        passwordText.addTarget(self, action: #selector(self.textDidChange), for: .editingChanged)
        
        userText.bindTextTwoWay(liveData: loginVM.login)
        passwordText.bindTextTwoWay(liveData: loginVM.password)
    }
    
    @objc func textDidChange(_ textField: UITextField) {
        signInButton.isEnabled = canLogin()
    }
    
    @objc func resetTextDidChange(_ textField: UITextField) {
        forgorPasswordButton.isEnabled = canResetPassword()
    }
    
    func canLogin() -> Bool {
        let isUserNotEmpty = userText.text!.count > 0
        let isPassNotEmpty = passwordText.text!.count > 0
        return isUserNotEmpty && isPassNotEmpty
    }
    
    func canResetPassword() -> Bool {
        return userText.text!.count > 0
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
    
    override init(frame: CGRect) {
        // 1. setup any properties here

        // 2. call super.init(frame:)
        super.init(frame: frame)

        // 3. Setup view from .xib file
        guard let view = loadViewFromNib() else { return }
        view.frame = self.bounds
        self.addSubview(view)
        mainView = view
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

extension LoginView : LoginViewModelEventsListener, SystemInformation {
    func flipToLogin() {
        print("flipToLogin")
    }
    
    func flipToPassword() {
        print("flipToPassword")
    }
    
    func onRegister() {
        print("onRegister")
    }
    
    func onResetPassword() {
        print("onResetPassword")
    }
    
    func routeToProfile(userId: String) {
        //TODO: !!! load profile!
        settings.userId = userId
        (self.parentViewController as! ProfileViewController).showProfileScreen(user: userId)
        print(userId)
    }
    
    func showToast(msg: ToastMessage) {
        Toast.show(message: msg.message, controller: self.parentViewController!)
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
