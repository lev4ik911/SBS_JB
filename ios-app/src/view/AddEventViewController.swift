//
//  AddEventViewController.swift
//  ios-app
//
//  Created by ECL User on 8/30/20.
//  Copyright © 2020 IceRock Development. All rights reserved.
//

import Foundation
import UIKit
import MultiPlatformLibrary
import MultiPlatformLibraryMvvm

class AddEventViewController :  UIViewController, UITabBarDelegate {

    struct Consts {
        var StepsCell = "StepTableViewCell"
        var NavigationIdentifier = "add-step-action"
        var TextViewPlaceholder = "Instruction detail"
    }
    
    var isEdit = false;
    var guidlineId = "";
    var consts = Consts()
    var steps = [Step]()
    var vm: GuidelineViewModel!
    var guidline: Guideline!
    var logoPath = ""
    @IBAction func cancel_creating_instruction(_ sender: Any) {
        DeclineInstructionAlert.show(controller: self)
    }
    
    @IBAction func save_new_instruction(_ sender: Any) {
        if (isEdit) {
            vm.onSaveAction()
        }
        else {
            let logoImg = imageView.image
            logoPath = saveImageToDocumentsDirectory(image: logoImg!, withName: "NI_LOGO.png")!
            let gd = Guideline(id: "", name: instructionName.text!, descr: instructionDescription.text!, favourited: 0, author: "", authorId: "", isFavorite: false, rating: RatingSummary.init(positive: 0, negative: 0, overall: 0), imagePath: logoPath, remoteImageId: "", updateImageDateTime: "")
            vm.guideline.setValue(value: gd, async: false)
        
            vm.onSaveAction()
        }
    }
    
    @IBOutlet var imageView: UIImageView!
    @IBOutlet var instructionName: UITextField!
    @IBOutlet weak var instructionDescription: UITextView!
    @IBOutlet var tableView: UITableView!
    
    @IBOutlet var addStepButton: UITabBarItem!
    
    @IBOutlet var addTabBar: UITabBar!
    func createStepTile(
        item: Step
    ) -> StepTableViewCell.CellModel {
        let tmpTitle = String(item.weight) + ". " + item.name
        return StepTableViewCell.CellModel (
            id: item.stepId,
            stepPath: item.imagePath,
            title: tmpTitle,
            description: item.descr
        )
    }
    
    @objc func imageTapped(tapGestureRecognizer: UITapGestureRecognizer)
        {
            // Your action
            let alertController : UIAlertController = UIAlertController(title: "Upload image", message: "Please, upload image from Camera or Photo library", preferredStyle: .actionSheet)
            let cameraAction : UIAlertAction = UIAlertAction(title: "Camera", style: .default, handler: {(cameraAction) in
                print("camera Selected...")

                if UIImagePickerController.isSourceTypeAvailable(UIImagePickerController.SourceType.camera) {
                    self.imagePicker.sourceType = .camera
                    self.present()
                } else {
                    self.present(self.showAlert(Title: "Title", Message: "Camera is not available on this Device or accesibility has been revoked!"), animated: true, completion: nil)
                }

            })

            let libraryAction : UIAlertAction = UIAlertAction(title: "Photo Library", style: .default, handler: {(libraryAction) in
                print("Photo library selected....")
                if UIImagePickerController.isSourceTypeAvailable(UIImagePickerController.SourceType.photoLibrary) {
                    self.imagePicker.sourceType = .photoLibrary
                    self.present()
                }else{
                    self.present(self.showAlert(Title: "Title", Message: "Photo Library is not available on this Device or accesibility has been revoked!"), animated: true, completion: nil)
                }
            })
            let cancelAction : UIAlertAction = UIAlertAction(title: "Cancel", style: .cancel , handler: {(cancelActn) in
            print("Cancel action was pressed")
            })

            alertController.addAction(cameraAction)
            alertController.addAction(libraryAction)
            alertController.addAction(cancelAction)
            alertController.popoverPresentationController?.sourceView = view
            alertController.popoverPresentationController?.sourceRect = view.frame

            self.present(alertController, animated: true, completion: nil)
        }
    
    var imagePicker : UIImagePickerController = UIImagePickerController()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        addTabBar.delegate = self
        imagePicker.delegate = self
        
        vm = GuidelineViewModel(settings: AppleSettings(delegate: UserDefaults.standard), eventsDispatcher: EventsDispatcher(listener: self))
       
        if (isEdit){
            //TODO: EDIT
            
        }
        else {
            //TODO: ADD
        }
        
        self.tableView.dataSource = self
        self.tableView.delegate = self
        
        let tapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(imageTapped(tapGestureRecognizer:)))
        self.imageView.isUserInteractionEnabled = true
        self.imageView.addGestureRecognizer(tapGestureRecognizer)

        let tempCell = UINib(nibName: consts.StepsCell, bundle: nil)
        self.tableView.register(tempCell, forCellReuseIdentifier: consts.StepsCell)
        self.tableView.rowHeight = UITableViewAutomaticDimension
        self.tableView.estimatedRowHeight = 88
        
        self.instructionName.layer.borderColor = UIColor.lightGray.cgColor
        self.instructionName.layer.borderWidth = 1.0
        self.instructionName.layer.cornerRadius = 8
        
        self.instructionDescription.layer.borderColor = UIColor.lightGray.cgColor
        self.instructionDescription.layer.borderWidth = 1.0
        self.instructionDescription.layer.cornerRadius = 8
        
        self.instructionDescription.delegate = self
        self.instructionDescription.text = consts.TextViewPlaceholder
        self.instructionDescription.textColor = UIColor.lightGray
        self.instructionDescription.selectedTextRange = self.instructionDescription.textRange(from: self.instructionDescription.beginningOfDocument, to: self.instructionDescription.beginningOfDocument)
        
        NotificationCenter.default.addObserver(self, selector: #selector(self.keyboardWillShow), name: NSNotification.Name.UIKeyboardWillShow, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(self.keyboardWillHide), name: NSNotification.Name.UIKeyboardWillHide, object: nil)
        
        self.hideKeyboardWhenTappedAround()
        
        vm.steps.addObserver{[weak self] itemsObject in
            guard let items = itemsObject as? [Step] else { return }
                self?.steps = items
                self?.tableView.reloadData()
            }
    }
        
    @objc func keyboardWillShow(notification: NSNotification) {
           if let keyboardSize = (notification.userInfo?[UIKeyboardFrameEndUserInfoKey] as? NSValue)?.cgRectValue {
            if self.view.frame.origin.y == 0 {
                self.view.frame.origin.y -= keyboardSize.height/2.5
               }
           }
       }

       @objc func keyboardWillHide(notification: NSNotification) {
        if self.view.frame.origin.y != 0 {
            self.view.frame.origin.y = 0
           }
       }
    
    func tabBar(_ tabBar: UITabBar, didSelect item: UITabBarItem) {
        //TODO: add navigation to Instruction detail View
        print("add pressed")
        print("item")

        performSegue(withIdentifier: consts.NavigationIdentifier, sender: self)
        self.tableView.reloadData()
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let destination = segue.destination as? AddStepViewController{
            destination.stepNo = Int32(self.steps.count + 1)
            destination.addEventVC = self
            destination.vm = self.vm
            if (isEditStep){
                destination.stepId = steps[editStepRow].stepId
                
            }
        }
    }
    var isEditStep = false;
    var editStepRow = 0;
}

extension AddEventViewController : UITableViewDataSource, UITableViewDelegate {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return steps.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if let cell = tableView.dequeueReusableCell(withIdentifier: consts.StepsCell) as? StepTableViewCell {
            cell.layoutIfNeeded()
            cell.fill(self.createStepTile(item: steps[indexPath.row]))
            let cgrect = CGRect(x: 0, y: 0, width: cell.pictureImage.frame.width, height: cell.pictureImage.frame.height * 0.61)
            let imageFrame = UIBezierPath(rect: cgrect)
            cell.textView.textContainer.exclusionPaths = [imageFrame]
            return cell
        }
        return UITableViewCell()
    }
}

extension AddEventViewController : UIImagePickerControllerDelegate {
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [String : Any]) {
        imagePicker.dismiss(animated: true, completion: nil)
        guard let selectedImage = info[UIImagePickerControllerOriginalImage] as? UIImage else {
            print("Image not found!")
            return
        }
        imageView.image = selectedImage
    }
}

extension AddEventViewController : UINavigationControllerDelegate {
    func present() {
        self.present(imagePicker, animated: true, completion: nil)
    }
    
    func showAlert(Title : String!, Message : String!)  -> UIAlertController {
        let alertController : UIAlertController = UIAlertController(title: Title, message: Message, preferredStyle: .alert)
        let okAction : UIAlertAction = UIAlertAction(title: "Ok", style: .default) { (alert) in
            print("User pressed ok function")
        }
        alertController.addAction(okAction)
        alertController.popoverPresentationController?.sourceView = view
        alertController.popoverPresentationController?.sourceRect = view.frame
        return alertController
      }
}

extension AddEventViewController : UITextViewDelegate {
    
    func textView(_ textView: UITextView, shouldChangeTextIn range: NSRange, replacementText text: String) -> Bool {
        
        let currentText: NSString = textView.text! as NSString
        let updatedText = currentText.replacingCharacters(in: range, with:text)
        
        if updatedText.isEmpty {
            textView.text = consts.TextViewPlaceholder
            textView.textColor = UIColor.lightGray
            textView.selectedTextRange = textView.textRange(from: textView.beginningOfDocument, to: textView.beginningOfDocument)
            return false
        }
        else if textView.textColor == UIColor.lightGray && !text.isEmpty {
            textView.text = nil
            textView.textColor = UIColor.black
        }
        
        return true
    }
    
    func textViewDidChangeSelection(_ textView: UITextView) {
        if self.view.window != nil {
            if textView.textColor == UIColor.lightGray {
                textView.selectedTextRange = textView.textRange(from: textView.beginningOfDocument, to: textView.beginningOfDocument)
            }
        }
    }
    
}

extension AddEventViewController : GuidelineViewModelEventsListener {
    func deleteImagesOnDevice(guidelineId: String, stepId: String) {
        
    }
    
    func getDocumentDirectoryPath() -> NSString {
            return NSTemporaryDirectory() as NSString
    }
    
    func rotateImage(image: UIImage) -> UIImage? {
            if (image.imageOrientation == UIImage.Orientation.up ) {
                return image
            }
            UIGraphicsBeginImageContext(image.size)
            image.draw(in: CGRect(origin: CGPoint.zero, size: image.size))
            let copy = UIGraphicsGetImageFromCurrentImageContext()
            UIGraphicsEndImageContext()
            return copy
        }
    
    func saveImageToDocumentsDirectory(image: UIImage, withName: String) -> String? {
        if let data = UIImagePNGRepresentation(rotateImage(image: image)!) {
                let dirPath = getDocumentDirectoryPath()
                let imageFileUrl = URL(fileURLWithPath: dirPath.appendingPathComponent(withName) as String)
                do {
                    try data.write(to: imageFileUrl)
                    print("Successfully saved image at path: \(imageFileUrl)")
                    print("MyApp: \(imageFileUrl.path)")
                    return imageFileUrl.path
                } catch {
                    print("Error saving image: \(error)")
                }
            }
            return nil
    }
    
    func getGuidelineImageData() {
        var bytes = [UInt8]()
        if let data = NSData(contentsOfFile: logoPath) {
            var buffer = [UInt8](repeating: 0, count: data.length)
            
            data.getBytes(&buffer, length: data.length)
            bytes = buffer
        }
        
        let swiftByteArray = bytes
        let intArray : [Int8] = swiftByteArray
            .map { Int8(bitPattern: $0) }
        let kotlinByteArray: KotlinByteArray = KotlinByteArray.init(size: Int32(swiftByteArray.count))
        for (index, element) in intArray.enumerated() {
            kotlinByteArray.set(index: Int32(index), value: element)
        }
        
        vm.uploadGuidelineImage(data: kotlinByteArray)
    }
    
    func getStepImageData(step: Step) {
        var bytes = [UInt8]()
        if let data = NSData(contentsOfFile: step.imagePath) {
            var buffer = [UInt8](repeating: 0, count: data.length)
            
            data.getBytes(&buffer, length: data.length)
            bytes = buffer
        }
        
        let swiftByteArray = bytes
        let intArray : [Int8] = swiftByteArray
            .map { Int8(bitPattern: $0) }
        let kotlinByteArray: KotlinByteArray = KotlinByteArray.init(size: Int32(swiftByteArray.count))
        for (index, element) in intArray.enumerated() {
            kotlinByteArray.set(index: Int32(index), value: element)
        }
        vm.uploadStepImage(step: step, data: kotlinByteArray)
    }
    
    func loadImage(url: String, guidelineId: String, stepId: String, remoteImageId: String, source: Any) {
        
    }
    
    func onAfterDeleteAction() {
        
    }
    
    func onAfterSaveAction() {
        self.dismiss(animated: true, completion: nil)
    }

    func onAfterSaveStepAction() {
        
    }
    
    func onAuthorizationRequired() {
        
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
    
    func onOpenProfile(profileId: String) {
        
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
    
    func showToast(msg: ToastMessage) {
        
    }
    
    func transferTempImage(localPath: String, guidelineId: String, stepId: String, remoteImageId: String, source: Any) {
        
    }
    
}
