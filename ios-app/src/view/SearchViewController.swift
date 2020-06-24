//
//  SearchViewController.swift
//  ios-app
//
//  Created by ECL User on 6/12/20.
//  Copyright © 2020 IceRock Development. All rights reserved.
//

import Foundation
import UIKit
import MultiPlatformLibrary
import MultiPlatformLibraryUnits

class SearchViewController : UIViewController, UITableViewDataSource, UITableViewDelegate {
    
    @IBOutlet weak var tableview: UITableView!
       
    var vm : GuidelineListViewModelShared!
    /*
    func createNewsTile(
        id: String,
        picture: UIImage,
        isFavorite: UIImage,
        title: String,
        author: String,
        description: StringDesc,
        positiveRating: String,
        negativeRating: String
        ) -> TableUnitItem {
        // create unit for https://github.com/icerockdev/moko-units
        return UITableViewCellUnit<InstructionsTableViewCell>(
            data: InstructionsTableViewCell.CellModel(
                id: id,
                picture: picture,
                isFavorite: isFavorite,
                title: title,
                author: author,
                positiveRating: positiveRating,
                negativeRating: negativeRating,
                description: description.localized()
            ),
            //itemId: id,
            configurator: nil
        )
    }
    */
    private var dataSource: TableUnitsSource!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        vm = GuidelineListViewModelShared(settings: AppleSettings(delegate: UserDefaults.standard),
           eventsDispatcher: EventsDispatcher(listener: self))
        vm.loadInstructions(forceRefresh: false)
        
        //dataSource = TableUnitsSourceKt.default(for: tableView)
        
        self.tableview.dataSource = self
        self.tableview.delegate = self
        
        self.registerTableViewCells()
        
        vm.instructions.addObserver{[weak self] itemsObject in
        guard let items = itemsObject as? [Guideline] else { return }
            
            //self?.dataSource.unitItems = items
            //self?.tableView.items = items
        }
        
        
    }
    
    private func registerTableViewCells() {
        let myFieldCell = UINib(nibName: "InstructionsTableViewCell",
                                  bundle: nil)
        self.tableview.register(myFieldCell,
                                forCellReuseIdentifier: "InstructionsTableViewCell")
    }
    
    func tableView(_ tableView: UITableView,
                   numberOfRowsInSection section: Int) -> Int {
        return 10
    }
    
    func tableView(_ tableView: UITableView,
                   cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if let cell = tableView.dequeueReusableCell(withIdentifier: "InstructionsTableViewCell") as? InstructionsTableViewCell {
            return cell
        }
        
        return UITableViewCell()
    }
}

extension SearchViewController: GuidelineListViewModelSharedEventsListener {
    func showToast(msg: ToastMessage) {
        
    }
}
