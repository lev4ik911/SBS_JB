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

class CommentTableViewCell : UITableViewCell, Fillable {
    typealias DataType = CellModel
    
    struct CellModel {
        let id: String
        let avatar: String
        let rating: Int32
                
        let description: String
    }
    
    @IBOutlet var descriptionText: UITextView!
    @IBOutlet var ratingImage: UIImageView!
    @IBOutlet var avatarImage: UIImageView!
    
    func fill(_ data: CommentTableViewCell.CellModel) {
        //TODO: check the avatarPath
        let userPicture = UIImage(named: "ic_profile.jpg")
        
        let ratingPicture = (data.rating > 0 ?
                           UIImage(systemName: "hand.thumbsup.fill") :
                           UIImage(systemName: "hand.thumbsdown.fill"))
        
        avatarImage.layer.borderWidth = 0
        avatarImage.layer.masksToBounds = false
        avatarImage.layer.borderColor = UIColor.black.cgColor
        avatarImage.layer.cornerRadius = avatarImage.frame.height/2
        avatarImage.clipsToBounds = true
        
        avatarImage.image = userPicture
        ratingImage.image = ratingPicture
                
        descriptionText.text = data.description
    }
    
    func update(_ data: CellModel) {
        
    }
    
}

extension CommentTableViewCell: Reusable {
    static func reusableIdentifier() -> String {
        return "CommentTableViewCell"
    }
    
    static func xibName() -> String {
        return "CommentTableViewCell"
    }
    
    static func bundle() -> Bundle {
        return Bundle.main
    }
}

