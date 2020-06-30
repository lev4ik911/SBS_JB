//
//  FavoritesController.swift
//  ios-app
//
//  Created by ECL User on 6/12/20.
//  Copyright © 2020 IceRock Development. All rights reserved.
//

import UIKit
import MultiPlatformLibrary


class FavoritesViewController : UITableViewController  {
    
    var filtered = [Guideline]()
    var filterring = false
    
    var vm : GuidelineListViewModelShared!
    var data = [Guideline]()
    
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
    
    lazy var table: UITableView = {
        let tableView = UITableView()
        tableView.translatesAutoresizingMaskIntoConstraints = false
        tableView.rowHeight = 90
        tableView.delegate = self
        tableView.dataSource = self
        let myFieldCell = UINib(nibName: "InstructionsTableViewCell",
                                  bundle: nil)
        tableView.register(myFieldCell,
                                forCellReuseIdentifier: "InstructionsTableViewCell")

        return tableView
    }()
    
    var tableConstraints: [NSLayoutConstraint]  {
        var constraints = [NSLayoutConstraint]()
        constraints.append(NSLayoutConstraint(item: self.tableView, attribute: .left, relatedBy: .equal,
                                              toItem: self.view, attribute: .left, multiplier: 1.0, constant: 1.0))
        constraints.append(NSLayoutConstraint(item: self.tableView, attribute: .right, relatedBy: .equal,
                                              toItem: self.view, attribute: .right, multiplier: 1.0, constant: 1.0))
        constraints.append(NSLayoutConstraint(item: self.tableView, attribute: .top, relatedBy: .equal,
                                              toItem: self.view, attribute: .top, multiplier: 1.0, constant: 1.0))
        constraints.append(NSLayoutConstraint(item: self.tableView, attribute: .bottom, relatedBy: .equal,
                                              toItem: self.view, attribute: .bottom, multiplier: 1.0, constant: 1.0))
        return constraints
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.tableView.rowHeight=90
        let myFieldCell = UINib(nibName: "InstructionsTableViewCell",
                                  bundle: nil)
        self.tableView.register(myFieldCell,
                                forCellReuseIdentifier: "InstructionsTableViewCell")
                
        self.view.backgroundColor = UIColor.white
        
        self.navigationController?.navigationBar.prefersLargeTitles = true
        self.navigationItem.largeTitleDisplayMode = .always
                
        let search = UISearchController(searchResultsController: nil)
        search.searchResultsUpdater = self
        self.navigationItem.searchController = search

        vm = GuidelineListViewModelShared(settings: AppleSettings(delegate: UserDefaults.standard),
           eventsDispatcher: EventsDispatcher(listener: self))
        vm.loadInstructions(forceRefresh: true)
        
        vm.instructions.addObserver{[weak self] itemsObject in
        guard let items = itemsObject as? [Guideline] else { return }
            self?.data = items
            self?.tableView.reloadData()
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    
    override func tableView(_ tableView: UITableView,
                   numberOfRowsInSection section: Int) -> Int {
        return data.count
    }
    
    override func tableView(_ tableView: UITableView,
                   cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if let cell = self.tableView.dequeueReusableCell(withIdentifier: "InstructionsTableViewCell") as? InstructionsTableViewCell {
            cell.fill(self.createGuidlinesTile(item: data[indexPath.row]))
            return cell
        }
        
        return UITableViewCell()
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        //TODO: add navigation to Instruction detail View
        performSegue(withIdentifier: "showinstructiondetail", sender: self)
        //vm.openNews(vm.getWorkplaceNewsItem(for: indexPath.row))
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let destination = segue.destination as? InstructionDetailViewController{
            destination.instruction = self.data[(tableView.indexPathForSelectedRow?.row)!]
        }
    }
    
}

extension FavoritesViewController: UISearchResultsUpdating {
    func updateSearchResults(for searchController: UISearchController) {
        /*
        if let text = searchController.searchBar.text, !text.isEmpty {
            self.filtered = self.data.filter({ (item) -> Bool in
                return item .lowercased().contains(text.lowercased())
            })
            self.filterring = true
        }
        else {
            self.filterring = false
            self.filtered = [String]()
        }
        self.table.reloadData()
        */
    }
}
/*
extension FavoritesViewController : UITableViewDataSource, UITableViewDelegate{
    override func tableView(_ tableView: UITableView,
                   numberOfRowsInSection section: Int) -> Int {
        return data.count
    }
    
    override func tableView(_ tableView: UITableView,
                   cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if let cell = tableView.dequeueReusableCell(withIdentifier: "InstructionsTableViewCell") as? InstructionsTableViewCell {
            cell.fill(self.createGuidlinesTile(item: data[indexPath.row]))
            return cell
        }
        
        return UITableViewCell()
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        //TODO: add navigation to Instruction detail View
        if let vcMissionDetail : ViewControllerMissionDetail = self.storyboard?.instantiateViewControllerWithIdentifier("MissionDetail") as ViewControllerMissionDetail {

            //set label text before presenting the viewController
            vcMissionDetail.label.text = "Test"

            //load detail view controller
            self.presentViewController(vcMissionDetail, animated: true, completion: nil)
        }
        //vm.openNews(vm.getWorkplaceNewsItem(for: indexPath.row))
    }
}
*/
extension FavoritesViewController: GuidelineListViewModelSharedEventsListener {
    func showToast(msg: ToastMessage) {

    }
}
