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
    var stepNo: Int32 = 0
    var newStep : Step!
    var addEventVC : AddEventViewController!
    
    @IBAction func cancel_adding_step(_ sender: Any) {
        DeclineStepAlert.show(controller: self)
    }
    
    @IBAction func add_new_step(_ sender: Any) {
        //TODO: ask Aleksey
        //var result = StepCreate(name: nameText.text!, description: descriptionText.text!, weight: stepNo as! KotlinInt)
        
        let stepId = Int.random(in: 0...100000)
        let stepWeight = Int32(stepNo)
        let stepName = nameText.text!
        let stepDescription = descriptionText.text!
        let formatter = DateFormatter()
        //formatter.timeStyle = .short
        let dateString = formatter.string(from: Date())
        
        //let sc = StepCreate(name: stepName, description: stepDescription, weight: (stepNo as! KotlinInt))
        
        newStep = Step(stepId: String(stepId), name: stepName, descr: stepDescription, weight: stepWeight, imagePath: "", remoteImageId: String(stepId), updateImageDateTime: dateString)
        
        addEventVC.steps.append(newStep)
        addEventVC.tableView.reloadData()
        
        //(self.parent as! AddEventViewController).steps.append(newStep)
        //(self.parent as! AddEventViewController).tableView.reloadData()
        
        self.dismiss(animated: true, completion: nil)
    }
    
    @IBOutlet var nameText: UITextField!
    @IBOutlet var descriptionText: UITextView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.nameText.layer.borderColor = UIColor.lightGray.cgColor
        self.nameText.layer.borderWidth = 1.0
        self.nameText.layer.cornerRadius = 8
                
        self.descriptionText.layer.borderColor = UIColor.lightGray.cgColor
        self.descriptionText.layer.borderWidth = 1.0
        self.descriptionText.layer.cornerRadius = 8
        
        self.descriptionText.text = "Step description"
        
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
