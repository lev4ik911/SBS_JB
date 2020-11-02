//
//  AddStepViewController.swift
//  ios-app
//
//  Created by ECL User on 8/30/20.
//  Copyright © 2020 IceRock Development. All rights reserved.
//

import Foundation
import UIKit
import MultiPlatformLibrary

class AddStepViewController : UIViewController {
    
    struct Consts {
        var TextViewPlaceholder = "Step description"
    }
    var consts = Consts()
    var isEdit = false
    var stepId = ""
    var stepNo: Int32 = 0
    var newStep : Step!
    var addEventVC : AddEventViewController!
    var vm: GuidelineViewModel!
    
    @IBAction func cancel_adding_step(_ sender: Any) {
        DeclineStepAlert.show(controller: self)
    }
    
    @IBAction func add_new_step(_ sender: Any) {
        let stepName = nameText.text!
        let stepDescription = descriptionText.text!
        let stepWeight = Int32(stepNo)
        
        let formatter = DateFormatter()
        let dateString = formatter.string(from: Date())

        let imageName = "NI_Step_"+String(stepWeight)+".png"
        let imagePath = saveImageToDocumentsDirectory(image: imageView.image!, withName: imageName)
        //let remoteImageId = (isEdit) ? imagePath : "";
        
        newStep = Step(stepId: stepId, name: stepName, descr: stepDescription, weight: stepWeight, imagePath: imagePath!, remoteImageId: "", updateImageDateTime: "")
        
        vm.onSaveStepAction(step: newStep)
        self.dismiss(animated: true, completion: nil)
    }
    
    func getDocumentDirectoryPath() -> NSString {
            return NSTemporaryDirectory() as NSString
    }
    
    func saveImageToDocumentsDirectory(image: UIImage, withName: String) -> String? {
        if let data = UIImagePNGRepresentation(addEventVC.rotateImage(image: image)!) {
                let dirPath = getDocumentDirectoryPath()
                let imageFileUrl = URL(fileURLWithPath: dirPath.appendingPathComponent(withName) as String)
                do {
                    try data.write(to: imageFileUrl)
                    print("Successfully saved image at path: \(imageFileUrl)")
                    return imageFileUrl.path
                } catch {
                    print("Error saving image: \(error)")
                }
            }
            return nil
        }
    
    @IBOutlet var imageView: UIImageView!
    @IBOutlet var nameText: UITextField!
    @IBOutlet var descriptionText: UITextView!
    
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
        imagePicker.delegate = self
        
        let tapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(imageTapped(tapGestureRecognizer:)))
        self.imageView.isUserInteractionEnabled = true
        self.imageView.addGestureRecognizer(tapGestureRecognizer)
        
        self.nameText.layer.borderColor = UIColor.lightGray.cgColor
        self.nameText.layer.borderWidth = 1.0
        self.nameText.layer.cornerRadius = 8
                
        self.descriptionText.layer.borderColor = UIColor.lightGray.cgColor
        self.descriptionText.layer.borderWidth = 1.0
        self.descriptionText.layer.cornerRadius = 8
        self.descriptionText.delegate = self
        self.descriptionText.text = consts.TextViewPlaceholder
        self.descriptionText.textColor = UIColor.lightGray
        self.descriptionText.selectedTextRange = self.descriptionText.textRange(from: self.descriptionText.beginningOfDocument, to: self.descriptionText.beginningOfDocument)
        
        NotificationCenter.default.addObserver(self, selector: #selector(self.keyboardWillShow), name: NSNotification.Name.UIKeyboardWillShow, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(self.keyboardWillHide), name: NSNotification.Name.UIKeyboardWillHide, object: nil)
        
        self.hideKeyboardWhenTappedAround()
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
    
}

extension AddStepViewController : UINavigationControllerDelegate {
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


extension AddStepViewController : UIImagePickerControllerDelegate {
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [String : Any]) {
        imagePicker.dismiss(animated: true, completion: nil)
        guard let selectedImage = info[UIImagePickerControllerOriginalImage] as? UIImage else {
            print("Image not found!")
            return
        }
        imageView.image = selectedImage
    }
}

extension AddStepViewController : UITextViewDelegate {
    
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
