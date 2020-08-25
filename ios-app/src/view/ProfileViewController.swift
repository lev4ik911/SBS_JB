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

    var profileViewModel: ProfileViewModel!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        profileViewModel = ProfileViewModel.init(settings: AppleSettings(delegate: UserDefaults.standard),
        eventsDispatcher: EventsDispatcher(listener: self))

        
        let userView = UserView(frame: parentView.bounds)
        parentView.addSubview(userView)
        userView.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint(item: userView, attribute: .width, relatedBy: .equal, toItem: parentView, attribute:.width, multiplier: 1.0, constant:0.0).isActive = true
        NSLayoutConstraint(item: userView, attribute: .height, relatedBy: .equal, toItem: parentView, attribute:.height, multiplier: 1.0, constant:0.0).isActive = true
        NSLayoutConstraint(item: userView, attribute: .top, relatedBy: .equal, toItem: view, attribute: .top, multiplier: 1.0, constant: 220.0).isActive = true

        
        let loginView = LoginView(frame: parentView.bounds)
        parentView.addSubview(loginView)
        loginView.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint(item: loginView, attribute: .width, relatedBy: .equal, toItem: parentView, attribute:.width, multiplier: 1.0, constant:0.0).isActive = true
        NSLayoutConstraint(item: loginView, attribute: .height, relatedBy: .equal, toItem: parentView, attribute:.height, multiplier: 1.0, constant:0.0).isActive = true
        NSLayoutConstraint(item: loginView, attribute: .top, relatedBy: .equal, toItem: view, attribute: .top, multiplier: 1.0, constant: 0.0).isActive = true
        
        

        NotificationCenter.default.addObserver(self, selector: #selector(self.keyboardWillShow), name: NSNotification.Name.UIKeyboardWillShow, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(self.keyboardWillHide), name: NSNotification.Name.UIKeyboardWillHide, object: nil)

        //Looks for single or multiple taps.
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: "dismissKeyboard")

        //Uncomment the line below if you want the tap not not interfere and cancel other interactions.
        //tap.cancelsTouchesInView = false
        view.addGestureRecognizer(tap)
        self.hideKeyboardWhenTappedAround()
    }
    
    
    @objc func keyboardWillShow(notification: NSNotification) {
        if let keyboardSize = (notification.userInfo?[UIKeyboardFrameEndUserInfoKey] as? NSValue)?.cgRectValue {
            if self.view.frame.origin.y == 0 {
                self.view.frame.origin.y -= keyboardSize.height/2
            }
        }
    }

    @objc func keyboardWillHide(notification: NSNotification) {
        if self.view.frame.origin.y != 0 {
            self.view.frame.origin.y = 0
        }
    }

}

extension UIViewController {
    func hideKeyboardWhenTappedAround() {
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(UIViewController.dismissKeyboard))
        tap.cancelsTouchesInView = false
        view.addGestureRecognizer(tap)
    }

    @objc func dismissKeyboard() {
        view.endEditing(true)
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

extension ProfileViewController : ProfileViewModelEventsListener {
    func onOpenGuidelineAction(guideline: Guideline) {
        
    }
    
    func showToast(msg: ToastMessage) {
        
    }
    
    func onActionButtonAction() {
        
    }
    
    func onLogoutAction() {
        
    }
    
    func requireAccept() {
        
    }
    
    func routeToLoginScreen() {
        
    }
}
