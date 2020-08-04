//
//  StepTableViewCell.swift
//  ios-app
//
//  Created by ECL User on 7/2/20.
//  Copyright © 2020 IceRock Development. All rights reserved.
//

import Foundation
import UIKit
import MultiPlatformLibraryUnits

class StepTableViewCell : UITableViewCell, Fillable {
    
    @IBOutlet var pictureImage: UIImageView!
    @IBOutlet var descriptionLabel: UITextView!
    @IBOutlet var titleLabel: UILabel!
    
    typealias DataType = CellModel
    
    struct CellModel {
        let id: String
        let stepPath: String
        
        let title: String
        
        let description: String
    }
    
    func fill(_ data: StepTableViewCell.CellModel) {
        //TODO: check the stepPath
        let stepPicture = UIImage(named: "ic_profile.jpg")
        
        pictureImage.image = stepPicture
        
        titleLabel.text = data.title
        
        descriptionLabel.text = data.description
    }
    
    func update(_ data: CellModel) {
        
    }
}

extension StepTableViewCell: Reusable {
    static func reusableIdentifier() -> String {
        return "StepTableViewCell"
    }
    
    static func xibName() -> String {
        return "StepTableViewCell"
    }
    
    static func bundle() -> Bundle {
        return Bundle.main
    }
}
