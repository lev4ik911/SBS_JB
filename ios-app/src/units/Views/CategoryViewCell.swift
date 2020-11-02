//
//  CategoryViewCell.swift
//  ios-app
//
//  Created by ECL User on 7/29/20.
//  Copyright © 2020 IceRock Development. All rights reserved.
//

import UIKit

class CategoryViewCell: UICollectionViewCell {
    @IBOutlet var category: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
    }
    
    func updateTitle(_ title: String) {
        self.category.text = title
    }

}
