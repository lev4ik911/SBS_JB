//
//  ViewController.swift
//  ios-app
//
//  Created by ECL User on 6/5/20.
//  Copyright © 2020 IceRock Development. All rights reserved.
//

import UIKit
import Foundation
import MultiPlatformLibrary

class MainViewController : UIViewController {
    
    var vm : GuidelineListViewModelShared!

    override func viewDidLoad() {
        super.viewDidLoad()
        
        /*
        vm = GuidelineListViewModelShared(settings: AppleSettings(delegate: UserDefaults.standard),
           eventsDispatcher: EventsDispatcher(listener: self))
        vm.loadInstructions(forceRefresh: false)
        
        // binding methods from https://github.com/icerockdev/moko-mvvm
        //activityIndicator.bindVisibility(liveData: vm.state.isLoadingState())
        //tableView.bindVisibility(liveData: vm.state.isSuccessState())
        //emptyView.bindVisibility(liveData: vm.state.isEmptyState())
        //errorView.bindVisibility(liveData: vm.state.isErrorState())

        // in/out generics of Kotlin removed in swift, so we should map to valid class
        //let errorText: LiveData<StringDesc> = viewModel.state.error().map { $0 as? StringDesc ?? StringDesc.Raw(string: "") } as! LiveData<StringDesc>
        //errorLabel.bindText(liveData: errorText)

        // datasource from https://github.com/icerockdev/moko-units
        //dataSource = TableUnitsSourceKt.default(for: tableView)
        
        
        //vm.instructions.addObserver{[weak self] itemsObject in
        //guard let items = itemsObject as? [Guideline] else { return }
        //self?.dataSource.unitItems = items
        //self?.tableView.reloadData()
            
        ///}
        */
    }
    
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    @IBAction func onRetryPressed() {
        //vm.onRetryPressed()
    }
    
    @objc func onRefresh() {
        /*vm.onRefresh { [weak self] in
            self?.refreshControl.endRefreshing()
        }*/
    }
    
}

extension MainViewController: GuidelineListViewModelSharedEventsListener {
    func showToast(msg: ToastMessage) {
        
    }
    
}
