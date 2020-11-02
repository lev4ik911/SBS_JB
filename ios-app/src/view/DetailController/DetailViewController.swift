//
//  DetailViewController.swift
//  ios-app
//
//  Created by ecluser on 09.09.2020.
//  Copyright © 2020 IceRock Development. All rights reserved.
//

import UIKit
import MultiPlatformLibrary
import MultiPlatformLibraryMvvm

class DetailViewController: UIViewController {

    @IBOutlet weak var tableView: UITableView!
    
    private var group: DataGroupedItem!
    
    convenience init(group: DataGroupedItem) {
        self.init()
        self.group = group
        title = group.category.description
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        configureTableView(self.tableView)
    }
    
    private func configureTableView(_ tableView: UITableView) {
        
        tableView.delegate = self
        tableView.dataSource = self
        tableView.estimatedRowHeight = 48
        tableView.rowHeight = UITableViewAutomaticDimension
        tableView.tableFooterView = UIView()
        
        registerCellsFor(tableView)
    }
    
    private func registerCellsFor(_ tableView: UITableView) {
        tableView.register(PopularCell.nib, forCellReuseIdentifier: PopularCell.name)
    }
}

// MARK: - UITableViewDelegate
extension DetailViewController: UITableViewDelegate {}

// MARK: - UITableViewDataSource
extension DetailViewController: UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return group.items.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        guard let cell = tableView.dequeueReusableCell(withIdentifier: PopularCell.name) as? PopularCell else {
            return UITableViewCell()
        }
        
        let item = group.items[indexPath.row]
        cell.fill(CellModel.convertGuidelineToCellModel(item))
        
        return cell
    }
}
