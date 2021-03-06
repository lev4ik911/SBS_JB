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
    
    struct Consts {
        var InstructionCellName = "InstructionsTableViewCell"
        var NavigationIdentifier = "showinstructiondetail"
    }
    var consts = Consts()
    
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
            isFavorite: true,
            title: item.name,
            author: item.author,
            positiveRating: String(item.rating.positive),
            negativeRating: String(item.rating.negative),
            description: item.descr
        )
    }
    /*
    lazy var table: UITableView = {
        let tableView = UITableView()
        tableView.translatesAutoresizingMaskIntoConstraints = false
        tableView.rowHeight = 90
        tableView.delegate = self
        tableView.dataSource = self
        let myFieldCell = UINib(nibName: consts.InstructionCellName,
                                  bundle: nil)
        tableView.register(myFieldCell,
                           forCellReuseIdentifier: consts.InstructionCellName)

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
*/
    override func viewDidLoad() {
        super.viewDidLoad()
        
        if (LoginAlert.navigate_index > -1) {
            return
        }
        
        self.tableView.dataSource = self
        self.tableView.delegate = self
        
        let myFieldCell = UINib(nibName: consts.InstructionCellName,
                                  bundle: nil)
        self.tableView.register(myFieldCell,
                                forCellReuseIdentifier: consts.InstructionCellName)
                
        self.view.backgroundColor = UIColor.white
        
        self.navigationController?.navigationBar.prefersLargeTitles = true
        self.navigationItem.largeTitleDisplayMode = .always
                
        let search = UISearchController(searchResultsController: nil)
        search.searchResultsUpdater = self
        self.navigationItem.searchController = search

        let refControl = UIRefreshControl()
        self.refreshControl = refControl
        self.refreshControl?.attributedTitle = NSAttributedString(string: "Reload instructions")
        self.refreshControl?.addTarget(self, action: #selector(self.refresh(_:)), for: .valueChanged)
        
        vm = GuidelineListViewModelShared(settings: AppleSettings(delegate: UserDefaults.standard),
           eventsDispatcher: EventsDispatcher(listener: self))
        vm.loadInstructions(forceRefresh: true)
        
        vm.instructions.addObserver{[weak self] itemsObject in
        guard let items = itemsObject as? [Guideline] else { return }
            self?.data = items
            self?.tableView.reloadData()
        }
    }

    @objc func refresh(_ sender: AnyObject) {
       // Code to refresh table view
        self.data.removeAll()
        tableView.reloadData()
        vm.loadInstructions(forceRefresh: true)
        self.refreshControl?.endRefreshing()
    }
    
    override func tableView(_ tableView: UITableView,
                   numberOfRowsInSection section: Int) -> Int {
        //return data.count
        return 2
    }
    
    override func tableView(_ tableView: UITableView,
                   cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if (indexPath.row < 3 && data.count > 0) {
        if let cell = self.tableView.dequeueReusableCell(withIdentifier: consts.InstructionCellName) as? InstructionsTableViewCell {
            cell.fill(self.createGuidlinesTile(item: data[indexPath.row]))
            return cell
        }
        }
        return UITableViewCell()
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        //TODO: add navigation to Instruction detail View
        performSegue(withIdentifier: consts.NavigationIdentifier, sender: self)
        //vm.openNews(vm.getWorkplaceNewsItem(for: indexPath.row))
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let destination = segue.destination as? InstructionDetailViewController{
            destination.instructionID = self.data[(tableView.indexPathForSelectedRow?.row)!].id
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

extension FavoritesViewController: GuidelineListViewModelSharedEventsListener {
    func loadImage(url: String, guideline: Guideline) {
        
    }
    
    func showToast(msg: ToastMessage) {
        Toast.show(message: msg.message, controller: self)
    }
}
