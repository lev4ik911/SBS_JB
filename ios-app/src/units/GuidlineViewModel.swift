//
//  GuidlineViewModel.swift
//  ios-app
//
//  Created by ECL User on 6/5/20.
//  Copyright © 2020 IceRock Development. All rights reserved.
//
/*
import Foundation
import MultiPlatformLibrary


class GuidlineViewModel: ObservableObject {
  @Published var guidlines = [Guideline]()
  @Published var isError = false
  
   private let repository: GuidelineRepository
  init(repository: GuidelineRepository) {
    self.repository = repository
  }
    
  /*
  func getAllGuidlines() {
    repository.testCall()(onSuccess: { [weak self] data in
      self?.guidlines = data
      }, onFailure: { [weak self] throwable in
        self?.isError = true
      }
    )
  }
    */
    func getAllGuidlines() -> Guideline{
        return repository.testCall()
        
    }
}
*/
