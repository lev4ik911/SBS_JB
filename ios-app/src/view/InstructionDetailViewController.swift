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
    
    var instructionID : String = ""
    
    struct CellsNames {
        var StepsCell = "StepTableViewCell"
        var FeedbacksCell = "CommentTableViewCell"
    }
    var cellNames = CellsNames()
    
    var vm: GuidelineViewModel!
    
    var steps = [Step]()
    var feedbacks = [Feedback]()
    
    @IBOutlet var instructionTitle: UILabel!
    @IBOutlet var instructionImage: UIImageView!
    
    @IBOutlet var positiveLabel: UILabel!
    @IBOutlet var positiveImage: UIImageView!
    
    @IBOutlet var negativeLabel: UILabel!
    @IBOutlet var negativeImage: UIImageView!
    
    @IBOutlet var descriptionLabel: UITextView!
    
    
    @IBOutlet var segmentedControl: UISegmentedControl!
    
    @IBOutlet var segmentedTable: UITableView!
    @IBOutlet var feedbackTable: UITableView!
    
    @IBAction func segmentedIndexChanged(_ sender: Any) {
        switch segmentedControl.selectedSegmentIndex
        {
        case 0:
            // show steps table
            segmentedTable.isHidden = false
            feedbackTable.isHidden = true
        case 1:
            // show comments table
            segmentedTable.isHidden = true
            feedbackTable.isHidden = false
        default:
            break
        }
    }
    
    func createStepTile(
        item: Step
    ) -> StepTableViewCell.CellModel {
        return StepTableViewCell.CellModel (
            id: item.stepId,
            stepPath: item.imagePath,
            title: item.name,
            description: item.descr
        )
    }
    
    func createFeedbackTile(
        item: Feedback
    ) -> CommentTableViewCell.CellModel {
        return CommentTableViewCell.CellModel (
            id: item.id,
            avatar: "",
            rating: item.rating,
            description: item.comment!
        )
    }
    
    func setTableProperty(_ tableView: UITableView, trowHeight: Int, cellType: String){
        tableView.rowHeight = CGFloat(trowHeight)
        let tempCell = UINib(nibName: cellType, bundle: nil)
        tableView.register(tempCell, forCellReuseIdentifier: cellType)
        
        tableView.dataSource = self
        tableView.delegate = self
    }
    
    func setTablesProperties() {
        setTableProperty(segmentedTable, trowHeight: 90, cellType: cellNames.StepsCell)
        setTableProperty(feedbackTable, trowHeight: 64, cellType: cellNames.FeedbacksCell)

        self.feedbackTable.isHidden = true
    }
   
    override func viewDidLoad(){
        super.viewDidLoad()
        
        vm = GuidelineViewModel(settings: AppleSettings(delegate: UserDefaults.standard),
        eventsDispatcher: EventsDispatcher(listener: self))
                
        vm.loadGuideline(instructionId: instructionID, forceRefresh: true)
        
        vm.steps.addObserver{[weak self] itemsObject in
            guard let items = itemsObject as? [Step] else { return }
            self?.steps = items
            self?.segmentedTable.reloadData()
        }
        
        vm.feedback.addObserver{[weak self] itemsObject in
            guard let items = itemsObject as? [Feedback] else { return }
            self?.feedbacks = items
            self?.feedbackTable.reloadData()
        }
        
        vm.guideline.addObserver{[weak self] item in
            guard let guidline = item else {return}
            self?.instructionTitle.text = guidline.name
            self?.positiveLabel.text = String(guidline.rating.positive)
            self?.negativeLabel.text = String(guidline.rating.negative)
            self?.descriptionLabel.text = guidline.descr
        }
        
        let tmpImage = UIImage(named: "ic_paneer.jpg")
        instructionImage.image = tmpImage
        
        setTablesProperties()
    }
    
}

extension InstructionDetailViewController : UITableViewDataSource, UITableViewDelegate {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if tableView == self.segmentedTable
        {
            return steps.count
        }
        else if tableView == self.feedbackTable
        {
            return feedbacks.count
        }
        return 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if tableView == segmentedTable {
            if let cell = tableView.dequeueReusableCell(withIdentifier: cellNames.StepsCell) as? StepTableViewCell {
                    cell.fill(self.createStepTile(item: steps[indexPath.row]))
                    return cell
            }
        }
        else if tableView == feedbackTable {
            if let cell = tableView.dequeueReusableCell(withIdentifier: cellNames.FeedbacksCell) as? CommentTableViewCell {
                    cell.fill(self.createFeedbackTile(item: feedbacks[indexPath.row]))
                    return cell
            }
        }
        return UITableViewCell()
    }
}

extension InstructionDetailViewController : GuidelineViewModelEventsListener{
    func showToast(msg: ToastMessage) {
        print("////////////////////////////")
        print(msg.type)
        print(msg.message)
        print("////////////////////////////")
    }
    
    func onAfterDeleteAction() {
        
    }
    
    func onAfterSaveAction() {
        
    }
    
    func onAfterSaveStepAction() {
        
    }
    
    func onCallInstructionEditor(instructionId: String) {
        
    }
    
    func onClosePreviewStepAction() {
        
    }
    
    func onEditGuidelineImage() {
        
    }
    
    func onEditStep(stepWeight: Int32) {
        
    }
    
    func onEditStepImage(editStep: Step) {
        
    }
    
    func onLoadImageFromAPI(step: Step) {
        
    }
    
    func onOpenProfile(profileId: Int32) {
        
    }
    
    func onPreviewStepAction(step: Step) {
        
    }
    
    func onPreviewStepNextAction(currentStep: Step) {
        
    }
    
    func onPreviewStepPreviousAction(currentStep: Step) {
        
    }
    
    func onRatingDownAction() {
        
    }
    
    func onRatingUpAction() {
        
    }
    
    func onRemoveInstruction() {
        
    }
    
    func onRemoveStep(step: Step) {
        
    }
}
