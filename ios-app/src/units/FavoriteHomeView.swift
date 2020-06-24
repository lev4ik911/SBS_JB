//
//  FavoriteHomeView.swift
//  ios-app
//
//  Created by ECL User on 6/22/20.
//  Copyright © 2020 IceRock Development. All rights reserved.
//

import Foundation
import MultiPlatformLibrary
import MultiPlatformLibraryUnits

class FavoritesHomeView : UIView  {
    @IBOutlet var tableview: UITableView!
    

}

extension FavoritesHomeView : UITableViewDataSource, UITableViewDelegate{
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        return UITableViewCell()
    }
}


