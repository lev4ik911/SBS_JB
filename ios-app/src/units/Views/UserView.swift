//
//  UserView.swift
//  ios-app
//
//  Created by ECL User on 7/8/20.
//  Copyright © 2020 IceRock Development. All rights reserved.
//

import Foundation
import UIKit
import ValueStepper
import MultiPlatformLibrary

class UserView: UIView {
    
    var nibName = "UserView"
    var profileID = ""
    
    struct Consts {
        var InstructionCellName = "InstructionsTableViewCell"
        var SubscriberCellName = "SubscriberTableViewCell"
    }
    var consts = Consts()
    
    var userInstructions = [Guideline]()
    var userSubscribers = [User]()
    
    func createGuidlinesTile(
        item: Guideline
    ) -> InstructionsTableViewCell.CellModel {
        return InstructionsTableViewCell.CellModel (
            id: item.id,
            picturePath: item.imagePath,
            isFavorite: item.isFavorite,
            title: item.name,
            author: item.author,
            positiveRating: String(item.rating.positive),
            negativeRating: String(item.rating.negative),
            description: item.descr
        )
    }
    
    func createSubscribersTile(
        item: User
    ) -> SubscriberTableViewCell.CellModel {
        return SubscriberTableViewCell.CellModel (
            id: item.id,
            userPath: item.name,
            userName: item.name,
            instructionsCount: "123",
            suubscribersCount: "48",
            rating: 3.6
        )
    }
    
    @IBOutlet var mainView: UIView!
    
    @IBOutlet var editProfileButton: UIBarButtonItem!
    @IBAction func editProfile_Tap(_ sender: Any) {
        print("on Edit profile tap")
    }
    
    @IBOutlet var logoutButton: UIBarButtonItem!
    @IBAction func logout_Tap(_ sender: Any) {
        print("on logout tap")
    }
    
    @IBOutlet var profileImageView: UIImageView!
    @IBOutlet var profileNameLabel: UILabel!
    @IBOutlet var profileMailLabel: UILabel!
    
    @IBOutlet var profileSegmentedControl: UISegmentedControl!
    @IBAction func profileSegmentedControl_ValueChanged(_ sender: Any) {
        switch profileSegmentedControl.selectedSegmentIndex {
        case 0:
            settingsView.isHidden = false
            instructionsTableView.isHidden = true
            subscribersTableView.isHidden = true
        case 1:
            settingsView.isHidden = true
            instructionsTableView.isHidden = false
            subscribersTableView.isHidden = true
        case 2:
            settingsView.isHidden = true
            instructionsTableView.isHidden = true
            subscribersTableView.isHidden = false
        default:
            settingsView.isHidden = false
            instructionsTableView.isHidden = true
            subscribersTableView.isHidden = true
        }
    }
    
    @IBOutlet var instructionsTableView: UITableView!
    @IBOutlet var subscribersTableView: UITableView!
    
    @IBOutlet var settingsView: UIView!
    @IBOutlet var showReccomendedSwitch: UISwitch!
    @IBAction func showReccomendedSwitch_ValueChanged(_ sender: Any) {
        
    }
    @IBOutlet var showFavoritedSwitch: UISwitch!
    @IBAction func showFavoritedSwitch_ValueChanged(_ sender: Any) {
        
    }
    @IBOutlet var searchResultsValueStepper: ValueStepper!
    @IBAction func searchResultsValueStepper_ValueChanged(_ sender: Any) {
        print("stepper valuechanged")
    }
    @IBOutlet var clearSearchHistoryButton: UIButton!
    @IBAction func clearSearchHistoryButton_Tap(_ sender: Any) {
        print("on clearSearchHistoryButton tap")
    }
    
    override func willMove(toSuperview newSuperview: UIView?) {
        super.willMove(toSuperview: newSuperview)
        
        settingsView.isHidden = false
        instructionsTableView.isHidden = true
        subscribersTableView.isHidden = true
    }
    
    
    // Our custom view from the XIB file
    var view: UIView!

    func xibSetup() {
        view = loadViewFromNib()

        // use bounds not frame or it'll be offset
        view.frame = bounds

        // Make the view stretch with containing view
        view.autoresizingMask = [UIViewAutoresizing.flexibleWidth, UIViewAutoresizing.flexibleHeight]
        // Adding custom subview on top of our view (over any custom drawing > see note below)
        addSubview(view)
        //mainView = view
    }

    func loadViewFromNib() -> UIView {

        let bundle = Bundle(for: type(of: self))
        let nib = UINib(nibName: nibName, bundle: bundle)
        let view = nib.instantiate(withOwner: self, options: nil)[0] as! UIView

        return view
    }
    
    override init(frame: CGRect) {
        // 1. setup any properties here

        // 2. call super.init(frame:)
        super.init(frame: frame)

        // 3. Setup view from .xib file
        xibSetup()
    }

    required init?(coder aDecoder: NSCoder) {
        // 1. setup any properties here

        // 2. call super.init(coder:)
        super.init(coder: aDecoder)

        // 3. Setup view from .xib file
        xibSetup()
    }
 
}


extension UserView : UITableViewDataSource, UITableViewDelegate {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if tableView == self.instructionsTableView
        {
            return userInstructions.count
        }
        else if tableView == self.subscribersTableView
        {
            return userSubscribers.count
        }
        return 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if tableView == instructionsTableView {
            if let cell = self.instructionsTableView.dequeueReusableCell(withIdentifier: consts.InstructionCellName) as? InstructionsTableViewCell {
                cell.fill(self.createGuidlinesTile(item: userInstructions[indexPath.row]))
                return cell
            }
        }
        else if tableView == subscribersTableView {
            if let cell = self.subscribersTableView.dequeueReusableCell(withIdentifier: consts.SubscriberCellName) as? SubscriberTableViewCell {
                cell.fill(self.createSubscribersTile(item: userSubscribers[indexPath.row]))
                return cell
            }
        }
        return UITableViewCell()
    }
}
