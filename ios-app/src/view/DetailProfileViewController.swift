//
//  DetailProfileViewController.swift
//  ios-app
//
//  Created by ECL User on 9/7/20.
//  Copyright © 2020 IceRock Development. All rights reserved.
//

import Foundation
import UIKit
import MultiPlatformLibrary

class DetailProfileViewController : UIViewController {

    var profileId = ""
    var profileViewModel : ProfileViewModel!
    
    @IBOutlet var xibView: UIView!
    
    @IBAction func BackButtonTap(_ sender: Any) {
        self.dismiss(animated: true, completion: nil)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
    
        profileViewModel = ProfileViewModel(settings: AppleSettings(delegate: UserDefaults.standard), eventsDispatcher: EventsDispatcher(listener: self))
        
        let userView = UserView(frame: self.view.bounds)
        userView.vm = profileViewModel
        userView.profileID = self.profileId
        self.view.addSubview(userView)
        userView.prepareForPreviewProfile()
        userView.setNeedsDisplay()
    }
}

extension DetailProfileViewController : ProfileViewModelEventsListener {
    func onSaveProfileAction() {
        
    }
    
    func routeToProfileScreen() {
        
    }
    
    func loadImage(url: String, guideline: Guideline) {
        
    }
    
    func onActionButtonAction() {
        
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
