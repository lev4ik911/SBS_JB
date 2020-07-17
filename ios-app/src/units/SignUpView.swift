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

class SignUpView : UIView{
    
    let nibName = "SignUpView"
    
    @IBOutlet var mainView: UIView!
    
    var registerVM : RegisterViewModel!
    
    override func willMove(toSuperview newSuperview: UIView?) {
      super.willMove(toSuperview: newSuperview)
      //Do stuff here
        registerVM = RegisterViewModel(settings: AppleSettings(delegate: UserDefaults.standard),
        eventsDispatcher: EventsDispatcher(listener: self), systemInfo: self)
        
        //userText.bindTextTwoWay(liveData: loginVM.login)
        //passwordText.bindText(liveData: loginVM.password)
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

extension SignUpView : RegisterViewModelEventsListener, SystemInformation {
func routeToLoginScreen() {
    //TODO: route to profile
    
}

func showErrors() {
    
}

func showToast(msg: ToastMessage) {
    
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
