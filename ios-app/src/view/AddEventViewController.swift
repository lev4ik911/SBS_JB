//
//  AddEventViewController.swift
//  ios-app
//
//  Created by ECL User on 8/30/20.
//  Copyright © 2020 IceRock Development. All rights reserved.
//

import Foundation
import UIKit
import MultiPlatformLibrary

class AddEventViewController :  UIViewController, UITabBarDelegate {

    struct Consts {
        var StepsCell = "StepTableViewCell"
        var NavigationIdentifier = "add-step-action"
    }
    var consts = Consts()
    
    var steps = [Step]()
    
    @IBAction func cancel_creating_instruction(_ sender: Any) {
        DeclineInstructionAlert.show(controller: self)
    }
    
    @IBAction func save_new_instruction(_ sender: Any) {
        var result = GuidelineCreate(name: instructionName.text!, description: instructionDescription.text!)
        
    }
    
    @IBOutlet var instructionName: UITextField!
    @IBOutlet var instructionDescription: UITextView!
    @IBOutlet var tableView: UITableView!
    
    @IBOutlet var addStepButton: UITabBarItem!
    
    @IBOutlet var addTabBar: UITabBar!
    func createStepTile(
        item: Step
    ) -> StepTableViewCell.CellModel {
        return StepTableViewCell.CellModel (
            id: item.stepId,
            stepPath: item.imagePath,
            title: item.name,
            description: item.descr
        )
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        addTabBar.delegate = self
        
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
    
    func tabBar(_ tabBar: UITabBar, didSelect item: UITabBarItem) {
        //TODO: add navigation to Instruction detail View
        print("add pressed")
        print("item")
        performSegue(withIdentifier: consts.NavigationIdentifier, sender: self)
        self.tableView.reloadData()
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let destination = segue.destination as? AddStepViewController{
            destination.stepNo = self.steps.count
        }
    }
}
/*
extension AddEventViewController : UITabBarDelegate {
    
}
*/
extension AddEventViewController : UITableViewDataSource, UITableViewDelegate {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return steps.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if let cell = tableView.dequeueReusableCell(withIdentifier: consts.StepsCell) as? StepTableViewCell {
            cell.fill(self.createStepTile(item: steps[indexPath.row]))
            return cell
        }
        return UITableViewCell()
    }
}
