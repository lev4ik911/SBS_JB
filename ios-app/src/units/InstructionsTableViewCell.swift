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
        let picture: UIImage
        let isFavorite: UIImage
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
    
    let tmpImage = UIImage(named: "ic_paneer.jpg")
    
    func fill(_ data: InstructionsTableViewCell.CellModel) {
        guidPicture.image = tmpImage
        
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
