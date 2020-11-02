//
//  CommentTableViewCell.swift
//  ios-app
//
//  Created by ECL User on 7/2/20.
//  Copyright © 2020 IceRock Development. All rights reserved.
//

import Foundation
import UIKit
import MultiPlatformLibraryUnits

class CommentTableViewCell : UITableViewCell {
    typealias DataType = CellModel
    
    struct CellModel {
        let id: String
        let avatar: String
        let authorName: String
        let rating: Int32
                
        let description: String
    }
    
    @IBOutlet var authorLabel: UILabel!
    @IBOutlet var descriptionText: UILabel!
    @IBOutlet var ratingImage: UIImageView!
    @IBOutlet var avatarImage: UIImageView!
    
    func fill(_ data: CommentTableViewCell.CellModel) {
        //TODO: check the avatarPath
        let userPicture = UIImage(named: "ic_profile.png")
        avatarImage.layer.borderWidth = 0.3
        avatarImage.layer.masksToBounds = false
        avatarImage.layer.borderColor = UIColor.gray.cgColor
        avatarImage.layer.cornerRadius = avatarImage.frame.height/2
        avatarImage.clipsToBounds = true
        avatarImage.image = userPicture
        
        let ratingPicture = (data.rating > 0 ?
            UIImage(systemName: "hand.thumbsup.fill") :
            UIImage(systemName: "hand.thumbsdown.fill"))
        ratingImage.layer.masksToBounds = false
        ratingImage.clipsToBounds = true
        ratingImage.image = ratingPicture
        
        authorLabel.text = data.authorName
        descriptionText.text = data.description
    }
    
}
