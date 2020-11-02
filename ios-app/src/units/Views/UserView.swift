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

class UserView : UIView {
    
    var nibName = "UserView"
    var profileID = ""
    
    struct Consts {
        var InstructionCellName = "InstructionsTableViewCell"
        var SubscriberCellName = "SubscriberTableViewCell"
        var NavigationIdentifier = "showinstructiondetailProfile"
    }
    var consts = Consts()
    
    var userInstructions = [Guideline]()
    var userSubscribers = [Author]()
    
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
        item: Author
    ) -> SubscriberTableViewCell.CellModel {
        return SubscriberTableViewCell.CellModel (
            userPath: item.name,
            userName: item.name,
            instructionsCount: String(item.instructionsCount),
            suubscribersCount: String(item.subscribersCount),
            rating: Double(item.rating)
        )
    }
    
    @IBOutlet var mainView: UIView!
    
    var userInfo : User!
    
    @IBOutlet var editButton: UIButton!
    @IBAction func editProfile_Tap(_ sender: Any) {
        print("on Edit profile tap")
        if (profileSegmentedControl.numberOfSegments == 3) {
        let ttl = "Edit user"
        let msg = "Coming soon in version 2 :)"
        let alert = UIAlertController(title: ttl, message: msg, preferredStyle: UIAlertController.Style.alert )
        alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.default, handler: nil))
        self.parentViewController!.present(alert, animated:true, completion: nil)
        }
        else{
            self.parentViewController?.dismiss(animated: true, completion: nil)
        }
    }
    
    @IBOutlet var logoutButton: UIButton!
    @IBAction func logout_Tap(_ sender: Any) {
        let ttl = "Logout"
        let msg = "Are you sure you want to logout?"
        let alert = UIAlertController(title: ttl, message: msg, preferredStyle: UIAlertController.Style.alert )

        let save = UIAlertAction(title: "Logout", style: .default) { (alertAction) in
            self.vm.onLogoutButtonClick()
            self.settings.accessToken = ""
            (self.parentViewController as! ProfileViewController).showLoginScreen()
        }
        let cancel = UIAlertAction(title: "Cancel", style: .default) { (alertAction) in }

        alert.addAction(save)
        alert.addAction(cancel)

        self.parentViewController!.present(alert, animated:true, completion: nil)
    }
    
    @IBOutlet var profileImageView: UIImageView!
    @IBOutlet var profileNameLabel: UILabel!
    @IBOutlet var profileMailLabel: UILabel!
    
    @IBOutlet var profileSegmentedControl: UISegmentedControl!
    @IBAction func profileSegmentedControl_ValueChanged(_ sender: Any) {
        switch profileSegmentedControl.selectedSegmentIndex {
        case 0:
            if (profileSegmentedControl.numberOfSegments == 3)
            {settingsView.isHidden = false
            instructionsTableView.isHidden = true
            subscribersTableView.isHidden = true
            }
            else{
                settingsView.isHidden = true
                instructionsTableView.isHidden = false
                subscribersTableView.isHidden = true
            }
            
        case 1:
            if (profileSegmentedControl.numberOfSegments == 3) {
            settingsView.isHidden = true
            instructionsTableView.isHidden = false
            subscribersTableView.isHidden = true
            }
            else {
                settingsView.isHidden = true
                instructionsTableView.isHidden = true
                subscribersTableView.isHidden = false
            }
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
        vm.onClearHistoryClick()
    }
    
    var vm : ProfileViewModel!
    var settings : LocalSettings!
    
    override func willMove(toSuperview newSuperview: UIView?) {
        super.willMove(toSuperview: newSuperview)
        
        //Do staff here
        profileImageView.layer.borderWidth = 3
        profileImageView.layer.borderColor = UIColor.gray.cgColor
        profileImageView.layer.cornerRadius = 90
        profileImageView.clipsToBounds = true
        
        settings = LocalSettings(settings: AppleSettings(delegate: UserDefaults.standard))
        
        self.instructionsTableView.dataSource = self
        self.instructionsTableView.delegate = self
        
        let instructionCell = UINib(nibName: consts.InstructionCellName,
                                  bundle: nil)
        self.instructionsTableView.register(instructionCell,
                                forCellReuseIdentifier: consts.InstructionCellName)
        
        self.subscribersTableView.dataSource = self
        self.subscribersTableView.delegate = self
        
        let subscribersCell = UINib(nibName: consts.SubscriberCellName,
                                  bundle: nil)
        self.subscribersTableView.register(subscribersCell,
                                forCellReuseIdentifier: consts.SubscriberCellName)
        /*
        vm = (self.parentViewController as! ProfileViewController).profileViewModel
        
        vm = ProfileViewModel(settings: AppleSettings(delegate: UserDefaults.standard), eventsDispatcher: EventsDispatcher(listener: self.parentViewController as! ProfileViewModelEventsListener))
        */
        print("userID")
        print(settings.userId)
        vm.loadUser(userId: self.profileID, forceRefresh: false)
        
        profileNameLabel.bindText(liveData: vm.fullName)
        profileMailLabel.bindText(liveData: vm.email)
        
        showFavoritedSwitch.bindValueTwoWay(liveData: vm.showFavorites)
        showReccomendedSwitch.bindValueTwoWay(liveData: vm.showRecommended)
        searchResultsValueStepper.value = 5
                
        vm.guidelines.addObserver{[weak self] itemsObject in
        guard let items = itemsObject as? [Guideline] else { return }
            self?.userInstructions = items
            self?.instructionsTableView.reloadData()
        }
        
        vm.subscribers.addObserver{[weak self] itemsObject in
        guard let items = itemsObject as? [Author] else { return }
            self?.userSubscribers = items
            self?.subscribersTableView.reloadData()
            print(self!.userSubscribers.count)
        }
        
        settingsView.isHidden = false
        instructionsTableView.isHidden = true
        subscribersTableView.isHidden = true
    }
    
    func prepareForPreviewProfile(){
        editButton.setImage(UIImage(), for: .normal)
        editButton.setTitle("< Back", for: .normal)
        logoutButton.isHidden = true
        profileSegmentedControl.removeSegment(at: 0, animated: false)
        profileSegmentedControl.selectedSegmentIndex = 0
        profileSegmentedControl.setNeedsDisplay()
        settingsView.isHidden = true
        instructionsTableView.isHidden = false
        self.setNeedsDisplay()
    }
    
    
    func reloadView(){
        vm.loadUser(userId: profileID, forceRefresh: false)
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
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        //TODO: add navigation to Instruction detail View
        if tableView == self.instructionsTableView{
            self.parentViewController!.performSegue(withIdentifier: consts.NavigationIdentifier, sender: self.parentViewController?.tabBarController)
        }
    }
}
