//
//  RecommendedFragment.swift
//  ios-app
//
//  Created by ECL User on 7/29/20.
//  Copyright © 2020 IceRock Development. All rights reserved.
//

import Foundation
import UIKit

class CustomView : UIView {

    var items = ["Кулинария", "IBA Docs", "Ремонт авто", "Медицина  ", "IBA Info    ", "СМК"]
    private let sectionInsets = UIEdgeInsets(top: 5.0, left: 2.0, bottom: 5.0, right: 2.0)
    private let itemsPerRow: CGFloat = 3
    
    @IBOutlet var contentView: UIView!
    @IBOutlet weak var collectionView: UICollectionView!
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        commonInit()
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        commonInit()
    }
    
    private func commonInit() {
        let bundle = Bundle(for: type(of: self))
        bundle.loadNibNamed("CustomView", owner: self, options: nil)
        addSubview(contentView)
        contentView.frame = bounds
        contentView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        //contentView.backgroundColor = .red
        initCollectionView()
    }
    
    private func initCollectionView() {
        let nib = UINib(nibName: "CategoryViewCell", bundle: nil)
        collectionView.register(nib, forCellWithReuseIdentifier: "CategoryViewCell")
        collectionView.dataSource = self
        if let layout = collectionView.collectionViewLayout as? UICollectionViewFlowLayout {
            layout.estimatedItemSize = CGSize(width: 60, height: 20)
            layout.itemSize = UICollectionViewFlowLayoutAutomaticSize
        }
    }
}

extension CustomView: UICollectionViewDataSource {
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return items.count
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        guard let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "CategoryViewCell", for: indexPath) as? CategoryViewCell else {
            fatalError("can't dequeue CustomCell")
        }

        cell.updateTitle(items[indexPath.row])

        return cell
    }
}

extension CustomView : UICollectionViewDelegateFlowLayout {
    //1
    func collectionView(_ collectionView: UICollectionView,
                        layout collectionViewLayout: UICollectionViewLayout,
                        sizeForItemAt indexPath: IndexPath) -> CGSize {
      //2
      let paddingSpace = sectionInsets.left * (itemsPerRow + 1)
      let availableWidth = self.frame.width - paddingSpace
      let widthPerItem = availableWidth / itemsPerRow
      
      return CGSize(width: widthPerItem, height: widthPerItem)
    }
    
    //3
    func collectionView(_ collectionView: UICollectionView,
                        layout collectionViewLayout: UICollectionViewLayout,
                        insetForSectionAt section: Int) -> UIEdgeInsets {
      return sectionInsets
    }
    
    // 4
    func collectionView(_ collectionView: UICollectionView,
                        layout collectionViewLayout: UICollectionViewLayout,
                        minimumLineSpacingForSectionAt section: Int) -> CGFloat {
      return sectionInsets.left
    }
}
 
