//
//  AddStepViewController.swift
//  ios-app
//
//  Created by ECL User on 8/30/20.
//  Copyright © 2020 IceRock Development. All rights reserved.
//

import Foundation
import UIKit
import MultiPlatformLibrary

class AddStepViewController : UIViewController {
    var stepNo: Int = 0
    var newStep : Step!
    
    @IBAction func cancel_adding_step(_ sender: Any) {
        DeclineStepAlert.show(controller: self)
    }
    
    @IBAction func add_new_step(_ sender: Any) {
        //TODO: ask Aleksey
        //var result = StepCreate(name: nameText.text!, description: descriptionText.text!, weight: stepNo as! KotlinInt)
        
        let stepId = Int.random(in: 0...100000)
        newStep = Step(stepId: String(stepId), name: nameText.text!, descr: descriptionText.text!, weight: Int32(stepNo), imagePath: "", updateImageTimeSpan: 0)
        
        (self.parent as! AddEventViewController).steps.append(newStep)
        (self.parent as! AddEventViewController).tableView.reloadData()
        
        self.dismiss(animated: true, completion: nil)
    }
    
    @IBOutlet var nameText: UITextField!
    @IBOutlet var descriptionText: UITextView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        NotificationCenter.default.addObserver(self, selector: #selector(self.keyboardWillShow), name: NSNotification.Name.UIKeyboardWillShow, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(self.keyboardWillHide), name: NSNotification.Name.UIKeyboardWillHide, object: nil)
        
        self.hideKeyboardWhenTappedAround()
    }
    
    @objc func keyboardWillShow(notification: NSNotification) {
        if let keyboardSize = (notification.userInfo?[UIKeyboardFrameEndUserInfoKey] as? NSValue)?.cgRectValue {
         if self.view.frame.origin.y == 0 {
             self.view.frame.origin.y -= keyboardSize.height/2.5
            }
        }
    }

    @objc func keyboardWillHide(notification: NSNotification) {
     if self.view.frame.origin.y != 0 {
         self.view.frame.origin.y = 0
        }
    }
    
}
