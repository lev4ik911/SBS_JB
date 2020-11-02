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
    
    struct Texts {
        var EmailLabelErrorText = "Invalid email"
        var IncorrectSymbolsErrorText = "Password has incorrect symbols"
        var SmallPasswordErrorText = "Password is too small"
        var MismatchPasswordErrorText = "Password mismatch"
        var SuccessRegistrationText = "Registration successfully completed"
    }
    var texts = Texts()
    
    let errorColor : UIColor = UIColor.red
    let normalColor : UIColor = UIColor.lightGray
    
    @IBOutlet var mainView: UIView!
    
    @IBOutlet var nameTextField: UITextField!
    @IBOutlet var emailTextField: UITextField!
    @IBOutlet var passwordTextField: UITextField!
    @IBOutlet var repeatTextField: UITextField!
    
    @IBOutlet var emailLabel: UILabel!
    @IBOutlet var passwordLabel: UILabel!
    @IBOutlet var confirmLabel: UILabel!
    
    
    @IBOutlet var signUpButton: UIButton!
    @IBAction func onSignUpButton_TouchUp(_ sender: Any) {
        registerVM.onRegisterButtonClick()
    }
    
    @IBAction func onBackButtonTap(_ sender: Any) {
        self.parentViewController?.navigationController?.popViewController(animated: true)
    }
    
    var registerVM : RegisterViewModel!
    
    override func willMove(toSuperview newSuperview: UIView?) {
      super.willMove(toSuperview: newSuperview)
        
        NotificationCenter.default.addObserver(self, selector: #selector(self.keyboardWillShow), name: NSNotification.Name.UIKeyboardWillShow, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(self.keyboardWillHide), name: NSNotification.Name.UIKeyboardWillHide, object: nil)
        
        self.parentViewController!.hideKeyboardWhenTappedAround()
        
        signUpButton.isEnabled = false
        
        nameTextField.addTarget(self, action: #selector(self.textDidChange), for: .editingChanged)
        emailTextField.addTarget(self, action: #selector(self.textDidChange), for: .editingChanged)
        passwordTextField.addTarget(self, action: #selector(self.textDidChange), for: .editingChanged)
        repeatTextField.addTarget(self, action: #selector(self.textDidChange), for: .editingChanged)
        
        emailTextField.addTarget(self, action: #selector(self.colorChange), for: .editingDidBegin)
        passwordTextField.addTarget(self, action: #selector(self.colorChange), for: .editingDidBegin)
        repeatTextField.addTarget(self, action: #selector(self.colorChange), for: .editingDidBegin)
        
        emailLabel.isHidden = true
        passwordLabel.isHidden = true
        confirmLabel.isHidden = true
        //Do stuff here
        registerVM = RegisterViewModel(settings: AppleSettings(delegate: UserDefaults.standard),
        eventsDispatcher: EventsDispatcher(listener: self), systemInfo: self)
        
        nameTextField.bindTextTwoWay(liveData: registerVM.login)
        emailTextField.bindTextTwoWay(liveData: registerVM.email)
        passwordTextField.bindTextTwoWay(liveData: registerVM.password)
        repeatTextField.bindTextTwoWay(liveData: registerVM.passwordConfirm)
        
        //signUpButton.bindEnabled(liveData: registerVM.isRegisterEnabled)
    }
    
    @objc func colorChange(_ textField: UITextField) {
        textField.layer.borderWidth = 0
        textField.layer.borderColor = normalColor.cgColor
        if textField == emailTextField
        {
            emailLabel.isHidden = true
        }
        if textField == passwordTextField
        {
            passwordLabel.isHidden = true
        }
        if textField == repeatTextField
        {
            confirmLabel.isHidden = true
        }
    }
    
    @objc func textDidChange(_ textField: UITextField) {
        signUpButton.isEnabled = canRegister()
    }
    
    func canRegister() -> Bool{
        let isUserNotEmpty = nameTextField.text!.count > 0
        let isEmailNotEmpty = emailTextField.text!.count > 0
        let isPasswordNotEmpty = passwordTextField.text!.count > 0
        let isRepeatPassNotEmpty = repeatTextField.text!.count > 0
        return isUserNotEmpty && isEmailNotEmpty && isPasswordNotEmpty && isRepeatPassNotEmpty
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
    func showToast(msg: ToastMessage) {
        Toast.show(message: msg.message, controller: self.parentViewController!)
    }
    
    func showErrors(errorList: [ValidationErrors]) {
        //TODO: add validation for text fields
        print(errorList)
        for error in errorList
        {
            switch error {
            case ValidationErrors.invalidEmail:
                emailTextField.layer.borderColor = errorColor.cgColor
                emailTextField.layer.borderWidth = 1
                emailLabel.isHidden = false
                emailLabel.text = texts.EmailLabelErrorText
            case ValidationErrors.passwordHasIncorrectSymbols:
                passwordTextField.layer.borderColor = errorColor.cgColor
                passwordTextField.layer.borderWidth = 1
                passwordLabel.isHidden = false
                passwordLabel.text = texts.IncorrectSymbolsErrorText
            case ValidationErrors.passwordIsTooSmall:
                passwordTextField.layer.borderColor = errorColor.cgColor
                passwordTextField.layer.borderWidth = 1
                passwordLabel.isHidden = false
                passwordLabel.text = texts.SmallPasswordErrorText
                repeatTextField.layer.borderColor = errorColor.cgColor
                repeatTextField.layer.borderWidth = 1
                confirmLabel.isHidden = false
                confirmLabel.text = texts.SmallPasswordErrorText
            case ValidationErrors.passwordMismatch:
                passwordTextField.layer.borderColor = errorColor.cgColor
                passwordTextField.layer.borderWidth = 1
                passwordLabel.isHidden = false
                passwordLabel.text = texts.MismatchPasswordErrorText
                repeatTextField.layer.borderColor = errorColor.cgColor
                repeatTextField.layer.borderWidth = 1
                confirmLabel.isHidden = false
                confirmLabel.text = texts.MismatchPasswordErrorText
            default:
                print(error)
            }
        }
    }
    
    func routeToLoginScreen() {
        //TODO: route to profile
        Toast.show(message: texts.SuccessRegistrationText, controller: self.parentViewController!)
        self.parentViewController?.navigationController?.popViewController(animated: true)
    }
}
