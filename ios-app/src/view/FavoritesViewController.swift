//
//  FavoritesController.swift
//  ios-app
//
//  Created by ECL User on 6/12/20.
//  Copyright © 2020 IceRock Development. All rights reserved.
//

import Foundation
import UIKit
import MultiPlatformLibrary
import MultiPlatformLibraryUnits

class FavoritesViewController : UIViewController {
    
    //let data = ["One","Two","Three","Four","Five",]
       
    @IBOutlet weak var tableview: UITableView!
       
    var vm : GuidelineListViewModelShared!
    
    var data : [Guideline] = []
    
    func createGuidlinesTile(
        id: String,
        picture: UIImage,
        isFavorite: UIImage,
        title: String,
        author: String,
        description: String,
        positiveRating: String,
        negativeRating: String
    ) -> InstructionsTableViewCell.CellModel {

        return InstructionsTableViewCell.CellModel(
                id: id,
                picture: picture,
                isFavorite: isFavorite,
                title: title,
                author: author,
                positiveRating: positiveRating,
                negativeRating: negativeRating,
                description: description
            )
    }
    
    private func configureTableView() {
        let myFieldCell = UINib(nibName: "InstructionsTableViewCell",
                                  bundle: nil)
        self.tableview.register(myFieldCell,
                                forCellReuseIdentifier: "InstructionsTableViewCell")
        
        tableview.delegate = self
        tableview.dataSource = self
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        vm = GuidelineListViewModelShared(settings: AppleSettings(delegate: UserDefaults.standard),
           eventsDispatcher: EventsDispatcher(listener: self))
        vm.loadInstructions(forceRefresh: true)
                
        vm.instructions.addObserver{[weak self] itemsObject in
        guard let items = itemsObject as? [Guideline] else { return }
            print(items.count)
            self?.data = items
            //self?.dataSource.unitItems = items
            //self?.tableView.items = items
        }
        
        
        self.configureTableView()
    }
}

extension FavoritesViewController : UITableViewDataSource, UITableViewDelegate{
    func tableView(_ tableView: UITableView,
                   numberOfRowsInSection section: Int) -> Int {
        var cellCount = 0
        if let array = vm.instructions.value {
            let items = array as? [Guideline]
            cellCount = items?.count as! Int
        }
        cellCount=data.count
        print(cellCount)
        return cellCount
    }
    
    func tableView(_ tableView: UITableView,
                   cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if let cell = tableView.dequeueReusableCell(withIdentifier: "InstructionsTableViewCell") as? InstructionsTableViewCell {
            if let array =  vm.instructions.value { let item = array[indexPath.row] as? Guideline
                
                let tmpImage = UIImage(named: "ic_paneer.jpg")!
                let isFavorite = (item!.isFavorite ?
                    UIImage(systemName: "star.fill") :
                    UIImage(systemName: "star"))!
                
                cell.fill(self.createGuidlinesTile(
                    id: item!.id,
                    picture: tmpImage,
                    isFavorite: isFavorite,
                    title: item!.name,
                    author: item!.author,
                    description: item!.description(),
                    positiveRating: String(item!.rating.positive),
                    negativeRating: String(item!.rating.negative)
                )
            )
            }
            return cell
        }
        
        return UITableViewCell()
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
           //vm.openNews(vm.getWorkplaceNewsItem(for: indexPath.row))
       }
}

extension FavoritesViewController: GuidelineListViewModelSharedEventsListener {
    func showToast(msg: ToastMessage) {
    
        print(msg.type)
    }
    
}
