//
//  NibLoadable.swift
//  ios-app
//
//  Created by ecluser on 30.08.2020.
//  Copyright © 2020 IceRock Development. All rights reserved.
//

import UIKit

protocol NibLoadable: class {
    static var nib: UINib { get }
}

extension NibLoadable {
    static var nib: UINib {
        return UINib(nibName: name, bundle: Bundle.init(for: self))
    }
    
    static var name: String {
        return String(describing: self)
    }
}

extension NibLoadable where Self: UIView {
    static func loadFromNib() -> Self {
        guard let view = nib.instantiate(withOwner: nil, options: nil).first as? Self else { fatalError() }
        return view
    }
    
    func loadFromNib(owner: Any? = nil) {
        guard let view = Self.nib.instantiate(withOwner: owner, options: nil).first as? UIView else { fatalError() }
        view.frame = self.bounds
        view.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        addSubview(view)
    }
}
