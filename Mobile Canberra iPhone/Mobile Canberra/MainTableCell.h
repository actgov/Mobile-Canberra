//
//  MainTableCell.h
//  Quicklypayit
//
//  Created by Zaki Bouguettaya on 23/08/13.
//  Copyright (c) 2013 Imagine Team. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface MainTableCell : UITableViewCell

@property (nonatomic, strong) IBOutlet UILabel *name;
@property (nonatomic, strong) IBOutlet UIView *colorIcon;



- (void) initWithDataSetName:(NSString *)name withColor:(UIColor *)color;

@end
