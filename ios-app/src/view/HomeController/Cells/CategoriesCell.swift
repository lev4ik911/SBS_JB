//
//  CategoriesCell.swift
//  ios-app
//
//  Created by ecluser on 31.08.2020.
//  Copyright © 2020 IceRock Development. All rights reserved.
//

import UIKit

class CategoriesCell: UITableViewCell, NibLoadable {

    override func awakeFromNib() {
        super.awakeFromNib()
        configureCell()
    }
    
    private func configureCell() {
        self.selectionStyle = .none
        self.separatorInset = UIEdgeInsets(top: 0, left: UIScreen.main.bounds.width, bottom: 0, right: 0)
    }
}
