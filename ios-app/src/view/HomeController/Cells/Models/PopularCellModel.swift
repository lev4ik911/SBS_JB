//
//  PopularCellModel.swift
//  ios-app
//
//  Created by ecluser on 31.08.2020.
//  Copyright © 2020 IceRock Development. All rights reserved.
//

import Foundation
import MultiPlatformLibrary
import MultiPlatformLibraryMvvm

struct CellModel {
    let id: String
    let picturePath: String
    let isFavorite: Bool
    let title: String
    let author: String
    let positiveRating: String
    let negativeRating: String
    let description: String
    
    static func convertGuidelineToCellModel(_ guideline: Guideline) -> CellModel {
        return CellModel(id: guideline.id,
                         picturePath: guideline.imagePath,
                         isFavorite: guideline.isFavorite,
                         title: guideline.name,
                         author: guideline.author,
                         positiveRating: String(guideline.rating.positive),
                         negativeRating: String(guideline.rating.negative),
                         description: guideline.descr)
    }
}
