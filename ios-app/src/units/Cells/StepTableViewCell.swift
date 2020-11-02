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

class StepTableViewCell : UITableViewCell {
    
    @IBOutlet var pictureImage: UIImageView!
    @IBOutlet var titleLabel: UILabel!
    @IBOutlet var textView: UITextView!
    
    typealias DataType = CellModel
    
    struct CellModel {
        let id: String
        let stepPath: String
        let title: String
        let description: String
    }
    
    func fill(_ data: StepTableViewCell.CellModel) {
        //TODO: check the stepPath
        let stepPicture = UIImage(named: "iba_logo.png")
        
        pictureImage.layer.borderWidth = 0.3
        pictureImage.layer.masksToBounds = false
        pictureImage.layer.borderColor = UIColor.gray.cgColor
        pictureImage.layer.cornerRadius = 20
        pictureImage.clipsToBounds = true
        pictureImage.image = stepPicture
        
        titleLabel.text = " " + data.title
        
        textView.text = data.description
        textView.backgroundColor = UIColor(white: 1, alpha: 0.1)
    }
}
