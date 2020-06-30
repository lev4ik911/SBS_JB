//
//  InstructionsTableViewCell.swift
//  ios-app
//
//  Created by ECL User on 6/15/20.
//  Copyright © 2020 IceRock Development. All rights reserved.
//

import Foundation
import UIKit
import MultiPlatformLibraryUnits

class InstructionsTableViewCell : UITableViewCell, Fillable {
    
    typealias DataType = CellModel
    
    struct CellModel {
        let id: String
        let picturePath: String
        let isFavorite: Bool
        let title: String
        let author: String
        
        let positiveRating: String
        let negativeRating: String
        
        let description: String
    }
    
    
    @IBOutlet weak var guidPicture: UIImageView!
    
    @IBOutlet weak var isFavoriteButton: UIImageView!
    
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var authorLabel: UILabel!
    
    @IBOutlet weak var positiveRatingLabel: UILabel!
    @IBOutlet weak var negativeRatingLabel: UILabel!
    
    @IBOutlet weak var descriptionLabel: UILabel!
    
    
    func fill(_ data: InstructionsTableViewCell.CellModel) {
        //TODO: checking of ImagePath
        let tmpImage = UIImage(named: "ic_paneer.jpg")
        
        let favoriteImage = (data.isFavorite ?
                           UIImage(systemName: "star.fill") :
                           UIImage(systemName: "star"))
        
        guidPicture.layer.borderWidth = 1
        guidPicture.layer.masksToBounds = false
        guidPicture.layer.borderColor = UIColor.black.cgColor
        guidPicture.layer.cornerRadius = guidPicture.frame.height/2
        guidPicture.clipsToBounds = true

        guidPicture.image = tmpImage
        isFavoriteButton.image = favoriteImage
        titleLabel.text = data.title
        authorLabel.text = data.author
        
        positiveRatingLabel.text = data.positiveRating
        negativeRatingLabel.text = data.negativeRating
        
        descriptionLabel.text = data.description
    }
    
    func update(_ data: CellModel) {
        
    }
}

extension InstructionsTableViewCell: Reusable {
    static func reusableIdentifier() -> String {
        return "InstructionsTableViewCell"
    }
    
    static func xibName() -> String {
        return "InstructionsTableViewCell"
    }
    
    static func bundle() -> Bundle {
        return Bundle.main
    }
}
