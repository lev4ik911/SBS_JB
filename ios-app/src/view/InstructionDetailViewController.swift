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
import Nuke
import AlamofireImage

class InstructionDetailViewController : UIViewController {
    
    var instructionID : String = ""
    
    @IBOutlet var bottomView: UIView!
    @IBOutlet var topView: UIView!
    struct CellsNames {
        var StepsCell = "StepTableViewCell"
        var FeedbacksCell = "CommentTableViewCell"
    }
    var cellNames = CellsNames()
    
    var vm: GuidelineViewModel!
    var guidline : Guideline!
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
    @IBOutlet var profileImageView: UIImageView!
    
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
        let tmpTitle = String(item.weight) + ". " + item.name
        print(item.imagePath)
        return StepTableViewCell.CellModel (
            id: item.stepId,
            stepPath: item.imagePath,
            title: tmpTitle,
            description: item.descr
        )
    }
    
    func createFeedbackTile(
        item: Feedback
    ) -> CommentTableViewCell.CellModel {
        return CommentTableViewCell.CellModel (
            id: item.id,
            avatar: "",
            authorName: item.author,
            rating: item.rating,
            description: item.comment!
        )
    }
    
    func setTableProperty(_ tableView: UITableView, cellType: String){
        tableView.dataSource = self
        tableView.delegate = self
        
        let tempCell = UINib(nibName: cellType, bundle: nil)
        tableView.register(tempCell, forCellReuseIdentifier: cellType)
        tableView.rowHeight = UITableViewAutomaticDimension
        tableView.estimatedRowHeight = 88
    }
    
    func setTablesProperties() {
        setTableProperty(segmentedTable, cellType: cellNames.StepsCell)
        setTableProperty(feedbackTable, cellType: cellNames.FeedbacksCell)

        self.feedbackTable.isHidden = true
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.segmentedTable.layoutSubviews()
        self.feedbackTable.layoutSubviews()
    }
   
    @IBOutlet var parentView: UIView!
    override func viewDidLoad(){
        super.viewDidLoad()
        
        /*
        parentView.layer.borderWidth = 5
        parentView.layer.borderColor = UIColor.blue.cgColor
        
        bottomView.layer.borderWidth = 1
        bottomView.layer.borderColor = UIColor.black.cgColor
        
        topView.layer.borderWidth = 1
        topView.layer.borderColor = UIColor.red.cgColor
        */
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
            self?.guidline = guidline
            self?.instructionTitle.text = guidline.name
            self?.positiveLabel.text = String(guidline.rating.positive)
            self?.negativeLabel.text = String(guidline.rating.negative)
            self?.descriptionLabel.text = guidline.descr
            //let tmpImage = UIImage(contentsOfFile: guidline.imagePath)
            //self?.instructionImage.image = tmpImage
        }
        
        /*
        let tmpImage = UIImage(named: "iba_logo.png")
        instructionImage.image = glImagePath
        */
        
        let positiveTap = UITapGestureRecognizer(target: self, action: #selector(self.positiveFeedback))
        positiveImage.addGestureRecognizer(positiveTap)
        positiveImage.isUserInteractionEnabled = true
        
        let negativeTap = UITapGestureRecognizer(target: self, action: #selector(self.negativeFeedback))
        negativeImage.addGestureRecognizer(negativeTap)
        negativeImage.isUserInteractionEnabled = true
        
        let userTap = UITapGestureRecognizer(target: self, action: #selector(imageTapped(tapGestureRecognizer:)))
        profileImageView.addGestureRecognizer(userTap)
        profileImageView.isUserInteractionEnabled = true
        
        profileImageView.image = UIImage(named: "ic_profile.png")
        profileImageView.layer.borderWidth = 0.3
        profileImageView.layer.masksToBounds = false
        profileImageView.layer.borderColor = UIColor.gray.cgColor
        profileImageView.layer.cornerRadius = profileImageView.frame.height/2
        profileImageView.clipsToBounds = true
        
        setTablesProperties()
    }
    
    @objc func positiveFeedback()
    {
        vm.onRatingUpButtonClick()
    }
    
    @objc func negativeFeedback()
    {
        vm.onRatingDownButtonClick()
    }
    
    @objc func imageTapped(tapGestureRecognizer: UITapGestureRecognizer)
    {
        let profileImage = tapGestureRecognizer.view as! UIImageView
        // Your action
        let storyboard = UIStoryboard(name: "Profile", bundle: nil)
        let pvc = DetailProfileViewController()
        pvc.profileId = self.guidline.authorId
        //let vc = storyboard.instantiateViewController(withIdentifier: "ProfileID") as UIViewController
        pvc.modalPresentationStyle = .fullScreen
        present(pvc, animated: true, completion: nil)
    }
}

extension InstructionDetailViewController {
    func alertWithTF(ratingType: Int32) {

        let msg = (ratingType == 1) ? "WOW! You are the amazing!" : "Why u didn't like this?"
        let alert = UIAlertController(title: "New feedback", message: "Please, leave your feedback about this guidline", preferredStyle: UIAlertController.Style.alert )

        let save = UIAlertAction(title: "Add", style: .default) { (alertAction) in
            let textField = alert.textFields![0] as UITextField
            let tmpRating = RatingCreate(rating: ratingType, comment: textField.text!)
            self.vm.createFeedback(newFeedback: tmpRating)
        }
        let cancel = UIAlertAction(title: "Cancel", style: .default) { (alertAction) in }
        
        alert.addTextField { (textField) in
            textField.placeholder = msg
            textField.textColor = .black
        }

        alert.addAction(save)
        alert.addAction(cancel)

        self.present(alert, animated:true, completion: nil)
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
                cell.layoutIfNeeded()
                cell.fill(self.createStepTile(item: steps[indexPath.row]))
                let cgrect = CGRect(x: 0, y: 0, width: cell.pictureImage.frame.width, height: cell.pictureImage.frame.height * 0.61)
                let imageFrame = UIBezierPath(rect: cgrect)
                cell.textView.textContainer.exclusionPaths = [imageFrame]
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
    
    private func tableView(tableView: UITableView, heightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {
        return UITableViewAutomaticDimension
        
    }
    
    private func tableView(tableView: UITableView, estimatedHeightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {
        return UITableViewAutomaticDimension
    }
    
    /*
    func getData(from url: URL, completion: @escaping (Data?, URLResponse?, Error?) -> ()) {
        URLSession.shared.dataTask(with: url, completionHandler: completion).resume()
    }
    
    func downloadImage(from url: URL) {
        print("Download Started")
        getData(from: url) { data, response, error in
            guard let data = data, error == nil else { return }
            print(response?.suggestedFilename ?? url.lastPathComponent)
            print("Download Finished")
            DispatchQueue.main.async() { [weak self] in
                self?.imageView.image = UIImage(data: data)
            }
        }
    }
     */
}

extension UIImageView {
    func downloaded(from url: URL, contentMode mode: UIView.ContentMode = .scaleAspectFit) {
        contentMode = mode
        URLSession.shared.dataTask(with: url) { data, response, error in
            guard
                let httpURLResponse = response as? HTTPURLResponse, httpURLResponse.statusCode == 200,
                let mimeType = response?.mimeType, mimeType.hasPrefix("image"),
                let data = data, error == nil,
                let image = UIImage(data: data)
                else { return }
            DispatchQueue.main.async() { [weak self] in
                self?.image = image
            }
        }.resume()
    }
    func downloaded(from link: String, contentMode mode: UIView.ContentMode = .scaleAspectFit) {
        guard let url = URL(string: link) else { return }
        downloaded(from: url, contentMode: mode)
    }
}

extension InstructionDetailViewController : GuidelineViewModelEventsListener{
    func deleteImagesOnDevice(guidelineId: String, stepId: String) {
        
    }
    
    func getGuidelineImageData() {
        
    }
    
    func getStepImageData(step: Step) {
        
    }
    
    func loadImage(url: String, guidelineId: String, stepId: String, remoteImageId: String, source: Any) {
        //Toast.show(message: url, controller: self)
        let request = URLRequest(url: URL.init(fileURLWithPath: url))
        //DataRequest.addAcceptableImageContentTypes(["binary/octet-stream"])
        
        let tUrl = URL.init(fileURLWithPath: url)
        /*
        var request = URLRequest(url: tUrl!)
        request.httpMethod = "GET"
        //request.setValue("application/json", forHTTPHeaderField:"Content-Type")
        request.setValue("application/octet-stream;q=0.9, application/json;q=0.1", forHTTPHeaderField: "Accept")
        request.timeoutInterval = 60.0
        URLSession.shared.dataTask(with: request) {
                (data: Data?, response: URLResponse?, error: Error?) -> Void in
        }.resume()
        */
        Nuke.loadImage(with: tUrl, into: self.instructionImage)
        
        //self.instructionImage.downloaded(from: url)
    }
    
    func transferTempImage(localPath: String, guidelineId: String, stepId: String, remoteImageId: String, source: Any) {
        
    }
    
    func onAuthorizationRequired() {
        Toast.show(message: "Need authorzation", controller: self)
        LoginAlert.show(controller: self, mainTabBarcontroller: self.tabBarController ?? nil)
    }
    
    func onOpenProfile(profileId: String) {
        
    }
    
    func showToast(msg: ToastMessage) {
        Toast.show(message: msg.message, controller: self)
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
        Toast.show(message: step.descr, controller: self)
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
        Toast.show(message: "Negative rating added", controller: self)
        alertWithTF(ratingType: -1)
    }
    
    func onRatingUpAction() {
        Toast.show(message: "Positive rating added", controller: self)
        alertWithTF(ratingType: 1)
    }
    
    func onRemoveInstruction() {
        
    }
    
    func onRemoveStep(step: Step) {
        
    }
}
