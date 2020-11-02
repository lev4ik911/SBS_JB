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
    var settings : LocalSettings!
    
    var userView : UserView!
    var loginView : LoginView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.navigationController?.setNavigationBarHidden(true, animated: false)
        settings = LocalSettings(settings: AppleSettings(delegate: UserDefaults.standard))
        
        profileViewModel = ProfileViewModel(settings: AppleSettings(delegate: UserDefaults.standard), eventsDispatcher: EventsDispatcher(listener: self))
        
        userView = UserView(frame: parentView.bounds)
        userView.vm = profileViewModel
        parentView.addSubview(userView)
        
        loginView = LoginView(frame: parentView.bounds)
        parentView.addSubview(loginView)
        loginView.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint(item: loginView, attribute: .width, relatedBy: .equal, toItem: parentView, attribute:.width, multiplier: 1.0, constant:0.0).isActive = true
        NSLayoutConstraint(item: loginView, attribute: .height, relatedBy: .equal, toItem: parentView, attribute:.height, multiplier: 1.0, constant:0.0).isActive = true
        NSLayoutConstraint(item: loginView, attribute: .top, relatedBy: .equal, toItem: view, attribute: .top, multiplier: 1.0, constant: 0.0).isActive = true
        
        
        if settings.accessToken.count > 0
        {
            showProfileScreen(user: settings.userId)
            //parentView.bringSubview(toFront: userView)
            //parentView.reloadInputViews()
            //self.performSegue(withIdentifier: "show_profile", sender: self)
        }
        else
        {
            showLoginScreen()
            //parentView.bringSubview(toFront: loginView)
            //self.view = loginView
        }
        parentView.reloadInputViews()

        NotificationCenter.default.addObserver(self, selector: #selector(self.keyboardWillShow), name: NSNotification.Name.UIKeyboardWillShow, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(self.keyboardWillHide), name: NSNotification.Name.UIKeyboardWillHide, object: nil)

        //Looks for single or multiple taps.
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: "dismissKeyboard")

        //Uncomment the line below if you want the tap not not interfere and cancel other interactions.
        //tap.cancelsTouchesInView = false
        view.addGestureRecognizer(tap)
        self.hideKeyboardWhenTappedAround()
    }
    
    func showLoginScreen(){
        //parentView.bringSubview(toFront: loginView)
        loginView.setNeedsDisplay()
        UIView.transition(from: userView,
        to: loginView,
        duration: 0.8,
        options: [.showHideTransitionViews, .transitionCrossDissolve],
        completion: nil)
    }
    
    func showProfileScreen(user : String) {
        profileViewModel.loadUser(userId: user, forceRefresh: false)
        userView.profileID = user
        //parentView.bringSubview(toFront: userView)
        userView.reloadView()
        userView.setNeedsDisplay()
        
        UIView.transition(from: loginView,
        to: userView,
        duration: 0.8,
        options: [.showHideTransitionViews, .transitionCrossDissolve],
        completion: nil)
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
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let destination = segue.destination as? InstructionDetailViewController{
            destination.instructionID = self.userView.userInstructions[(self.userView.instructionsTableView.indexPathForSelectedRow?.row)!].id
        }
    }
}

extension ProfileViewController : ProfileViewModelEventsListener {
    func onSaveProfileAction() {
        
    }
    
    func routeToProfileScreen() {
        
    }
    
    func loadImage(url: String, guideline: Guideline) {
        
    }
    
    func onActionButtonAction() {
        print("onActionButtonAction")
    }
    
    func onLogoutAction() {
        
    }
    
    func onOpenGuidelineAction(guideline: Guideline) {
        
    }
    
    func requireAccept() {
        
    }
    
    func routeToLoginScreen() {
        
    }
    
    func showToast(msg: ToastMessage) {
        Toast.show(message: msg.message, controller: self)
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
