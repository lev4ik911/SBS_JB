//
//  PopularCell.swift
//  ios-app
//
//  Created by ecluser on 30.08.2020.
//  Copyright © 2020 IceRock Development. All rights reserved.
//

import UIKit

class PopularCell: UITableViewCell, NibLoadable {
    
    @IBOutlet weak var logoView: UIImageView!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var ratingLabel: UILabel!
    @IBOutlet weak var authorLabel: UILabel!
    @IBOutlet weak var descriptionLabel: UILabel!
    @IBOutlet weak var popularButton: UIButton!
    
    @IBOutlet weak var containerView: UIView!

    override func awakeFromNib() {
        super.awakeFromNib()
        configureCell()
    }

    private func configureCell() {
        self.selectionStyle = .none
        self.separatorInset = UIEdgeInsets(top: 0, left: UIScreen.main.bounds.width, bottom: 0, right: 0)
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        DispatchQueue.main.async {
            self.configureShadow()
            self.configureLogoView()
        }
    }
    
}

extension PopularCell {
    func fill(_ data: CellModel) {
        //TODO: checking of ImagePath
        let tmpImage = UIImage(named: "iba_logo.png")
        
        let favoriteImage = (data.isFavorite ?
                           UIImage(systemName: "star.fill") :
                           UIImage(systemName: "star"))
        
        logoView.layer.borderWidth = 0.3
        logoView.layer.masksToBounds = false
        logoView.layer.borderColor = UIColor.gray.cgColor
        logoView.layer.cornerRadius = 10
        logoView.clipsToBounds = true

        logoView.image = tmpImage
        popularButton.setImage(favoriteImage, for: .normal)
        titleLabel.text = data.title
        authorLabel.text = data.author
        ratingLabel.text = "Feedback: \(data.positiveRating) / \(data.negativeRating)"
        
        #warning("This will be code for configuring attributes of the rating label")
        
        guard !data.description.isEmpty else { descriptionLabel.isHidden = true; return }
        descriptionLabel.text = data.description
    }
}

extension PopularCell {
    
    private func configureShadow() {
        containerView.clipsToBounds = true
        containerView.layer.cornerRadius = 10
        
        containerView.layer.masksToBounds = false
        containerView.layer.shadowOffset = CGSize(width: 0, height: 2)
        containerView.layer.shadowColor = UIColor.darkGray.cgColor
        containerView.layer.shadowOpacity = 0.1
        containerView.layer.shadowRadius = 4
        
        containerView.layer.shadowPath = UIBezierPath(roundedRect: containerView.bounds, cornerRadius: 10).cgPath
        containerView.layer.shouldRasterize = true
        containerView.layer.rasterizationScale = UIScreen.main.scale
    }
    
    private func configureLogoView() {
        logoView.clipsToBounds = true
        logoView.backgroundColor = .lightGray
        logoView.layer.masksToBounds = false
    }
}
