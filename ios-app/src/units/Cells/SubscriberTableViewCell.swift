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
        let id: String
        let userPath: String
        
        let userName: String
        let instructionsCount: String
        let suubscribersCount: String
        
        let rating: Double
    }
    
    func fill(_ data: SubscriberTableViewCell.CellModel) {
        //TODO: check the userPath
        let userPicture = UIImage(named: "ic_profile.jpg")
        userAvatar.image = userPicture
        
        userName.text = data.userName
        userSubscribersCount.text = "Subscribers: " + data.suubscribersCount
        userIntructionsCount.text = "Instructions: " + data.instructionsCount
        
        ratingCosmoView.rating = data.rating
    }
    
    func update(_ data: CellModel) {
        
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
}
