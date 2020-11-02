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
        //var result = GuidelineCreate(name: instructionName.text!, description: instructionDescription.text!)
        tableView.reloadData()
    }
    
    @IBOutlet var instructionName: UITextField!
    @IBOutlet var instructionDescription: UITextView!
    @IBOutlet var tableView: UITableView!
    
    @IBOutlet var addStepButton: UITabBarItem!
    
    @IBOutlet var addTabBar: UITabBar!
    func createStepTile(
        item: Step
    ) -> StepTableViewCell.CellModel {
        let tmpTitle = String(item.weight) + ". " + item.name
        return StepTableViewCell.CellModel (
            id: item.stepId,
            stepPath: item.imagePath,
            title: tmpTitle,
            description: item.descr
        )
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        addTabBar.delegate = self
        
        self.tableView.dataSource = self
        self.tableView.delegate = self
        
        let tempCell = UINib(nibName: consts.StepsCell, bundle: nil)
        self.tableView.register(tempCell, forCellReuseIdentifier: consts.StepsCell)
        self.tableView.rowHeight = UITableViewAutomaticDimension
        self.tableView.estimatedRowHeight = 88
        
        self.instructionName.layer.borderColor = UIColor.lightGray.cgColor
        self.instructionName.layer.borderWidth = 1.0
        self.instructionName.layer.cornerRadius = 8
        
        self.instructionDescription.layer.borderColor = UIColor.lightGray.cgColor
        self.instructionDescription.layer.borderWidth = 1.0
        self.instructionDescription.layer.cornerRadius = 8
        
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
        
        //let vc = AddStepViewController()
        //vc.addEventVC = self
        //vc.stepNo = steps.count
        //vc.modalPresentationStyle = .fullScreen
        //present(vc, animated: true, completion: nil)
        performSegue(withIdentifier: consts.NavigationIdentifier, sender: self)
        self.tableView.reloadData()
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let destination = segue.destination as? AddStepViewController{
            destination.stepNo = Int32(self.steps.count + 1)
            destination.addEventVC = self
            
        }
    }
}

extension AddEventViewController : UITableViewDataSource, UITableViewDelegate {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return steps.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if let cell = tableView.dequeueReusableCell(withIdentifier: consts.StepsCell) as? StepTableViewCell {
            cell.layoutIfNeeded()
            cell.fill(self.createStepTile(item: steps[indexPath.row]))
            let cgrect = CGRect(x: 0, y: 0, width: cell.pictureImage.frame.width, height: cell.pictureImage.frame.height * 0.61)
            let imageFrame = UIBezierPath(rect: cgrect)
            cell.textView.textContainer.exclusionPaths = [imageFrame]
            return cell
        }
        return UITableViewCell()
    }
}
