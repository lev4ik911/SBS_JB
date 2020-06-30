//
//  InstructionDetailViewController.swift
//  ios-app
//
//  Created by ECL User on 6/26/20.
//  Copyright © 2020 IceRock Development. All rights reserved.
//

import Foundation
import UIKit
import MultiPlatformLibrary

class InstructionDetailViewController : UIViewController {
    
    var instruction : Guideline = Guideline.init(id: "", name: "", descr: "", author: "", isFavorite: false, rating: RatingSummary(positive: 0, negative: 0, overall: 0), imagePath: "", updateImageTimeSpan: 0)
    
    @IBOutlet var instructionTitle: UILabel!
    @IBOutlet var instructionImage: UIImageView!
    
    @IBOutlet var positiveLabel: UILabel!
    @IBOutlet var positiveImage: UIImageView!
    
    @IBOutlet var negativeLabel: UILabel!
    @IBOutlet var negativeImage: UIImageView!
    
    @IBOutlet var descriptionLabel: UITextView!
    
    override func viewDidLoad(){
        super.viewDidLoad()
        
        instructionTitle.text = instruction.name
        let tmpImage = UIImage(named: "ic_paneer.jpg")
        instructionImage.image = tmpImage
        
        positiveLabel.text = String(instruction.rating.positive)
        negativeLabel.text = String(instruction.rating.negative)
        
        descriptionLabel.text = instruction.descr
    }
    
    
    
    
    
}

extension InstructionDetailViewController: GuidelineListViewModelSharedEventsListener {
    func showToast(msg: ToastMessage) {

    }
}
