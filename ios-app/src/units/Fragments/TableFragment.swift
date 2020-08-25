//
//  TableFragment.swift
//  ios-app
//
//  Created by ECL User on 7/29/20.
//  Copyright © 2020 IceRock Development. All rights reserved.
//

import Foundation
import UIKit
import MultiPlatformLibrary
import MultiPlatformLibraryMvvm

protocol CellTapped {
    /// Method
    func cellGotTapped()
    func buttonGotTapped()
}

class TableFragment : UIView {
    
    
    var nibName = "TableFragment"
    
    var delegate : CellTapped!
    var data = [Guideline]()
    var vm : GuidelineListViewModelShared!
    
    
    struct Consts {
        var InstructionCellName = "InstructionsTableViewCell"
        var InstructionNavigationIdentifier = "showinstructiondetail"
        var DetailNavigationIdentifier = "show-full-list-from-home"
    }
    var consts = Consts()
    
    @IBOutlet var navigationBar: UINavigationBar!
    @IBOutlet var showAllButton: UIBarButtonItem!
    @IBOutlet var activityIndicator: UIActivityIndicatorView!
    @IBOutlet var tableView: UITableView!
    
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
    
    func loadFavorites() {
        navigationBar.topItem?.title = "Favorites"
        vm = GuidelineListViewModelShared(settings: AppleSettings(delegate: UserDefaults.standard),
           eventsDispatcher: EventsDispatcher(listener: self))
        activityIndicator.bindVisibility(liveData: vm.isLoading)
        tableView.bindVisibility(liveData: vm.isLoading, inverted: true)
        vm.loadInstructions(forceRefresh: true)
        vm.instructions.addObserver{[weak self] itemsObject in
        guard let items = itemsObject as? [Guideline] else { return }
            self?.data = items
            self?.tableView.reloadData()
        }
    }
    
    func loadRecommended() {
        navigationBar.topItem?.title = "Recommended"
        vm = GuidelineListViewModelShared(settings: AppleSettings(delegate: UserDefaults.standard),
           eventsDispatcher: EventsDispatcher(listener: self))
        activityIndicator.bindVisibility(liveData: vm.isLoading)
        tableView.bindVisibility(liveData: vm.isLoading, inverted: true)
        vm.loadInstructions(forceRefresh: true)
        vm.instructions.addObserver{[weak self] itemsObject in
        guard let items = itemsObject as? [Guideline] else { return }
            self?.data = items
            self?.tableView.reloadData()
        }
    }
    
    func loadPopular() {
        navigationBar.topItem?.title = "Popular"
        vm = GuidelineListViewModelShared(settings: AppleSettings(delegate: UserDefaults.standard),
           eventsDispatcher: EventsDispatcher(listener: self))
        activityIndicator.bindVisibility(liveData: vm.isLoading)
        tableView.bindVisibility(liveData: vm.isLoading, inverted: true)
        vm.loadInstructions(forceRefresh: true)
        vm.instructions.addObserver{[weak self] itemsObject in
        guard let items = itemsObject as? [Guideline] else { return }
            self?.data = items
            self?.tableView.reloadData()
        }
    }
    
    override func willMove(toSuperview newSuperview: UIView?) {
        super.willMove(toSuperview: newSuperview)
        
        data = [Guideline]()
        activityIndicator.isHidden = false
        tableView.isHidden = true
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

extension TableFragment : GuidelineListViewModelSharedEventsListener {
    func showToast(msg: ToastMessage) {
    
    }
}

extension TableFragment : CellTapped {
    func buttonGotTapped() {
        //performSegue(withIdentifier: consts.DetailNavigationIdentifier, sender: self)
    }
    
    func cellGotTapped() {
        //performSegue(withIdentifier: consts.InstructionNavigationIdentifier, sender: self)
    }
}

extension TableFragment : UITableViewDataSource, UITableViewDelegate {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
            return data.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
            if let cell = tableView.dequeueReusableCell(withIdentifier: consts.InstructionCellName) as? InstructionsTableViewCell {
                cell.fill(self.createGuidlinesTile(item: data[indexPath.row]))
                return cell
            }
        
        return UITableViewCell()
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        //TODO: add navigation to Instruction detail View
        delegate.cellGotTapped()
        //performSegue(withIdentifier: consts.InstructionNavigationIdentifier, sender: self)
    }
    /*
    //TODO: Ask ROMAN!!!
    func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let destination = segue.destination as? InstructionDetailViewController{
            destination.instructionID = self.data[(tableView.indexPathForSelectedRow?.row)!].id
        }
    }
    */
}
