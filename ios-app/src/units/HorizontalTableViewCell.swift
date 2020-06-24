//
//  HorizontalTableViewCell.swift
//  ios-app
//
//  Created by ECL User on 6/15/20.
//  Copyright © 2020 IceRock Development. All rights reserved.
//

import Foundation
import UIKit
import MultiPlatformLibraryUnits

class HorizontalTableViewCell : UITableViewCell, Fillable {
    
    typealias DataType = CellModel
    
    struct CellModel {
        let id: String
        let picture: UIImage
        let isFavorite: Bool
        let title: String
        let author: String
        
        let positiveRating: String
        let negativeRating: String
        
        let description: String
    }
    
    
    @IBOutlet private var pictureImage: UIImage!
    @IBOutlet private var isFavoriteButton: UIButton!
    
    @IBOutlet private var titleLabel: UILabel!
    @IBOutlet private var authorLabel: UILabel!
    
    @IBOutlet private var positiveRatingLabel: UILabel!
    @IBOutlet private var negativeRatingLabel: UILabel!
    
    @IBOutlet private var descriptionLabel: UILabel!
    
    func fill(_ data: HorizontalTableViewCell.CellModel) {
        pictureImage = data.picture
        
        titleLabel.text = data.title
        authorLabel.text = data.author
        
        positiveRatingLabel.text = data.positiveRating
        negativeRatingLabel.text = data.negativeRating
        
        descriptionLabel.text = data.description
    }
    
    func update(_ data: CellModel) {
        
    }
}

extension HorizontalTableViewCell: Reusable {
    static func reusableIdentifier() -> String {
        return "HorizontalTableViewCell"
    }
    
    static func xibName() -> String {
        return "HorizontalTableViewCell"
    }
    
    static func bundle() -> Bundle {
        return Bundle.main
    }
}
