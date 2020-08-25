//
//  DetailFromHomeView.swift
//  ios-app
//
//  Created by ECL User on 7/29/20.
//  Copyright © 2020 IceRock Development. All rights reserved.
//

import Foundation
import UIKit
import MultiPlatformLibrary

class DetailFromHomeView: UIView {
    
    var viewName = ""
    let nibName = "SignUpView"
    struct Consts {
        var InstructionCellName = "InstructionsTableViewCell"
        var NavigationIdentifier = "showinstructiondetail"
    }
    var consts = Consts()
    
    var vm : GuidelineListViewModelShared!
    var data = [Guideline]()
    
    @IBOutlet var viewNameLabel: UILabel!
    
    @IBOutlet var mainTableView: UITableView!
    
    override func willMove(toSuperview newSuperview: UIView?) {
        super.willMove(toSuperview: newSuperview)
        
        viewNameLabel.text = viewName
        
        
        mainTableView.dataSource = (self.parentViewController as! UITableViewDataSource)
        mainTableView.delegate = (self.parentViewController as! UITableViewDelegate)
        
        let myFieldCell = UINib(nibName: consts.InstructionCellName,
                                  bundle: nil)
        mainTableView.register(myFieldCell,
                                forCellReuseIdentifier: consts.InstructionCellName)
        
        vm = GuidelineListViewModelShared(settings: AppleSettings(delegate: UserDefaults.standard),
                                          eventsDispatcher: EventsDispatcher(listener: self.parentViewController as! GuidelineListViewModelSharedEventsListener))
        vm.loadInstructions(forceRefresh: true)
        
        vm.instructions.addObserver{[weak self] itemsObject in
        guard let items = itemsObject as? [Guideline] else { return }
            self?.data = items
            self?.mainTableView.reloadData()
        }
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
    

}

extension DetailFromHomeView: UITableViewDataSource, UITableViewDelegate {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return data.count
    }
    
    
    
    func tableView(_ tableView: UITableView,
                   cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if let cell = mainTableView.dequeueReusableCell(withIdentifier: consts.InstructionCellName) as? InstructionsTableViewCell {
            cell.fill(self.createGuidlinesTile(item: data[indexPath.row]))
            return cell
        }
        
        return UITableViewCell()
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        //TODO: add navigation to Instruction detail View
        self.parentViewController!.performSegue(withIdentifier: consts.NavigationIdentifier, sender: self)
        //vm.openNews(vm.getWorkplaceNewsItem(for: indexPath.row))
    }
    
    func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let destination = segue.destination as? InstructionDetailViewController{
            destination.instructionID = self.data[(mainTableView.indexPathForSelectedRow?.row)!].id
        }
    }
}
