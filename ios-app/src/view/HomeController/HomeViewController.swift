//
//  HomeViewController.swift
//  ios-app
//
//  Created by ECL User on 6/12/20.
//  Copyright © 2020 IceRock Development. All rights reserved.
//

import Foundation
import UIKit
import MultiPlatformLibrary
import MultiPlatformLibraryMvvm

enum CategoryType: CaseIterable {
    case categories
    case popular
    case recommended
    case favorites
    
    var description: String {
        switch self {
        case .categories: return "Categories"
        case .popular: return "Popular"
        case .recommended: return "Recommended"
        case .favorites: return "Favorites"
        }
    }
}

struct DataGroupedItem {
    let category: CategoryType
    let items: [Guideline]
}

class HomeViewController : UIViewController {
    
    @IBOutlet weak var tableView: UITableView!
    
    var vm : GuidelineListViewModelShared!
    var groupObjects: [DataGroupedItem] = []
    
    private lazy var activityIndicator: UIActivityIndicatorView = {
       let indicator = UIActivityIndicatorView()
        indicator.activityIndicatorViewStyle = .medium
        indicator.isHidden = false
        indicator.startAnimating()
        
        return indicator
    }()
    
    override func viewDidLoad(){
        super.viewDidLoad()
        
        vm = GuidelineListViewModelShared(settings: AppleSettings(delegate: UserDefaults.standard),
        eventsDispatcher: EventsDispatcher(listener: self))
        
        configureTableView(tableView)
        fetchAllData()
    }
    
    private func configureTableView(_ tableView: UITableView) {
        
        tableView.delegate = self
        tableView.dataSource = self
        tableView.estimatedRowHeight = 48
        tableView.estimatedSectionHeaderHeight = 48
        tableView.rowHeight = UITableViewAutomaticDimension
        tableView.sectionHeaderHeight = UITableViewAutomaticDimension
        tableView.tableFooterView = UIView()
        tableView.refreshControl = UIRefreshControl()
        tableView.refreshControl?.addTarget(self, action: #selector(reloadData), for: .valueChanged)
        
        registerCellsFor(tableView)
    }
    
    private func registerCellsFor(_ tableView: UITableView) {
        tableView.register(PopularCell.nib, forCellReuseIdentifier: PopularCell.name)
        tableView.register(CategoriesCell.nib, forCellReuseIdentifier: CategoriesCell.name)
    }
    
    private func fetchAllData() {
        loadCategory(.categories)
        loadCategory(.popular)
        loadCategory(.recommended)
        loadCategory(.favorites)
    }
    
    @objc private func reloadData() {
        tableView.refreshControl?.beginRefreshing()
        fetchAllData()
    }
}

// MARK: - Guideline Events Listener Delegate
extension HomeViewController: GuidelineListViewModelSharedEventsListener {
    func loadImage(url: String, guideline: Guideline) {
        
    }
    
    func showToast(msg: ToastMessage) {
        Toast.show(message: msg.message, controller: self)
    }
}

// MARK: - Fetch Data
extension HomeViewController {
    
    func loadCategory(_ category: CategoryType) {
        
        loadItemsForTest { (guidline) in
            guard !self.groupObjects.contains(where: { $0.category == category }) else {
                self.groupObjects.removeAll(where: { $0.category == category })
                self.groupObjects.append(DataGroupedItem(category: category, items: guidline))
                self.tableView.refreshControl?.endRefreshing()
                self.tableView.reloadData()
                return
            }

            self.groupObjects.append(DataGroupedItem(category: category, items: guidline))
            
            self.tableView.refreshControl?.endRefreshing()
            self.tableView.reloadData()

        }
    }
    
    private func loadItemsForTest(completion: @escaping([Guideline]) -> ()) {

        tableView.tableHeaderView = activityIndicator
        activityIndicator.bindVisibility(liveData: vm.isLoading)

        tableView.bindVisibility(liveData: vm.isLoading, inverted: true)
        vm.loadInstructions(forceRefresh: true)
        vm.instructions.addObserver{ itemsObject in
            guard let items = itemsObject as? [Guideline] else { return }
            let slice = items.count > 0 ? items : items
            completion(slice)
        }
    }
}

extension HomeViewController: UITableViewDelegate {
    
}

extension HomeViewController: UITableViewDataSource {
    
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        let header = HomeHeaderView.loadFromNib()
        header.setCategoryBy(groupObjects[section].category)
        header.didClickCategory = { category in            
            let detailVC = DetailViewController(group: self.groupObjects[section])
            self.navigationController?.pushViewController(detailVC, animated: true)
        }
        
        return header
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return groupObjects.count
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        switch groupObjects[section].category {
        case .categories:
            return 1
        default:
            let groupItemsCount = groupObjects[section].items.count
            let count = groupItemsCount < 3 ? groupItemsCount : 3
            return count
        }
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        switch groupObjects[indexPath.section].category {
        case .categories:
            guard let categoriesCell = tableView.dequeueReusableCell(withIdentifier: CategoriesCell.name) as? CategoriesCell else {
                return UITableViewCell()
            }
            
            //TODO: Здесь нужно передать не определенный айтем, а массив, принадлежащий этой категории. который пойдет в коллекшен вью внутри этой ячейки
            return categoriesCell
        default:
            guard let cell = tableView.dequeueReusableCell(withIdentifier: PopularCell.name) as? PopularCell else {
                return UITableViewCell()
            }
            
            let item = groupObjects[indexPath.section].items[indexPath.row]
            cell.fill(CellModel.convertGuidelineToCellModel(item))
            
            return cell
        }
    }
}
