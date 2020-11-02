//
//  SubscriberTableViewCell.swift
//  ios-app
//
//  Created by ECL User on 7/24/20.
//  Copyright © 2020 IceRock Development. All rights reserved.
//

import UIKit
import Cosmos

class SubscriberTableViewCell: UITableViewCell {
    
    @IBOutlet var userAvatar: UIImageView!
    @IBOutlet var userName: UILabel!
    @IBOutlet var userSubscribersCount: UILabel!
    @IBOutlet var ratingCosmoView: CosmosView!
    @IBOutlet var userIntructionsCount: UILabel!
        
    typealias DataType = CellModel
    
    struct CellModel {
        let userPath: String
        
        let userName: String
        let instructionsCount: String
        let suubscribersCount: String
        
        let rating: Double
    }
    
    func fill(_ data: SubscriberTableViewCell.CellModel) {
        //TODO: check the userPath
        let userPicture = UIImage(named: "ic_profile.png")
        
        userAvatar.layer.borderWidth = 0.3
        userAvatar.layer.masksToBounds = false
        userAvatar.layer.borderColor = UIColor.gray.cgColor
        userAvatar.layer.cornerRadius = userAvatar.frame.height/2
        userAvatar.clipsToBounds = true

        userAvatar.image = userPicture
        
        userName.text = data.userName
        userSubscribersCount.text = "Subscribers: " + data.suubscribersCount
        userIntructionsCount.text = "Instructions: " + data.instructionsCount
        
        ratingCosmoView.rating = data.rating/10
    }
}
