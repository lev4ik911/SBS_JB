//
//  HomeHeaderView.swift
//  ios-app
//
//  Created by ecluser on 30.08.2020.
//  Copyright © 2020 IceRock Development. All rights reserved.
//

import UIKit

class HomeHeaderView: UIView, NibLoadable {

    @IBOutlet weak var headerTitle: UILabel!
    @IBOutlet weak var actionButton: UIButton!

    private var categoryType: CategoryType!
    var didClickCategory: ((_ category: CategoryType)->())?
    
    override func awakeFromNib() {
        super.awakeFromNib()
        configureHeaderView()
    }
    
    func setCategoryBy(_ categoryType: CategoryType) {
        self.categoryType = categoryType
        self.headerTitle.text = categoryType.description
    }
    
    private func configureHeaderView() {
        actionButton.addTarget(self, action: #selector(seeAllAction(_ :)), for: .touchUpInside)
    }
    
    @objc private func seeAllAction(_ sender: UIButton) {
        didClickCategory?(categoryType)
    }
}
