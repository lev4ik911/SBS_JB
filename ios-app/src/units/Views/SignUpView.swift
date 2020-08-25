//
//  SignUpView.swift
//  ios-app
//
//  Created by ECL User on 7/8/20.
//  Copyright © 2020 IceRock Development. All rights reserved.
//

import Foundation
import UIKit
import MultiPlatformLibrary
import MultiPlatformLibraryMvvm

class SignUpView : UIView {
    
    let nibName = "SignUpView"
    
    @IBOutlet var mainView: UIView!
    
    @IBOutlet var nameTextField: UITextField!
    @IBOutlet var emailTextField: UITextField!
    @IBOutlet var passwordTextField: UITextField!
    @IBOutlet var repeatTextField: UITextField!
    
    @IBOutlet var signUpButton: UIButton!
    
    @IBAction func onSignUpButton_TouchUp(_ sender: Any) {
        
        registerVM.onRegisterButtonClick()
    }
    
    @IBAction func onBackToLogin_Tap(_ sender: Any) {
        self.parentViewController?.navigationController?.popViewController(animated: true)
    }
    
    var registerVM : RegisterViewModel!
    
    override func willMove(toSuperview newSuperview: UIView?) {
      super.willMove(toSuperview: newSuperview)
        
        NotificationCenter.default.addObserver(self, selector: #selector(self.keyboardWillShow), name: NSNotification.Name.UIKeyboardWillShow, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(self.keyboardWillHide), name: NSNotification.Name.UIKeyboardWillHide, object: nil)
        
        self.parentViewController!.hideKeyboardWhenTappedAround()
        
      //Do stuff here
        registerVM = RegisterViewModel(settings: AppleSettings(delegate: UserDefaults.standard),
        eventsDispatcher: EventsDispatcher(listener: self), systemInfo: self)
        
        nameTextField.bindTextTwoWay(liveData: registerVM.login)
        emailTextField.bindTextTwoWay(liveData: registerVM.email)
        passwordTextField.bindTextTwoWay(liveData: registerVM.password)
        repeatTextField.bindTextTwoWay(liveData: registerVM.passwordConfirm)
        
        signUpButton.bindEnabled(liveData: registerVM.isRegisterEnabled)
    }
    
    @objc func keyboardWillShow(notification: NSNotification) {
        if let keyboardSize = (notification.userInfo?[UIKeyboardFrameEndUserInfoKey] as? NSValue)?.cgRectValue {
            if self.frame.origin.y == 0 {
                self.frame.origin.y -= keyboardSize.height/2.5
            }
        }
    }

    @objc func keyboardWillHide(notification: NSNotification) {
        if self.frame.origin.y != 0 {
            self.frame.origin.y = 0
        }
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

extension SignUpView : SystemInformation {
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

extension SignUpView : RegisterViewModelEventsListener {
    func showErrors(errorList: [ValidationErrors]) {
        //TODO: add validation for text fields
        Toast.show(message: "Smth went wrong", controller: self.parentViewController!)
    }
    
    func routeToLoginScreen() {
        //TODO: route to profile
        self.parentViewController?.tabBarController?.selectedIndex = 4
    }

    func showToast(msg: ToastMessage) {
        
    }
}
