//
//  MainTableCell.m
//  Quicklypayit
//
//  Created by Zaki Bouguettaya on 23/08/13.
//  Copyright (c) 2013 Imagine Team. All rights reserved.
//

#import "MainTableCell.h"
#import <QuartzCore/QuartzCore.h>

@implementation MainTableCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        // Initialization code
    }
    return self;
}

- (void) initWithDataSetName:(NSString *)name withColor:(UIColor *)color{
    self.colorIcon.backgroundColor = color;
    CALayer *imageLayer = self.colorIcon.layer;
    [imageLayer setCornerRadius:self.colorIcon.frame.size.height/2];
    [imageLayer setBorderWidth:0];
    [imageLayer setMasksToBounds:YES];
    [imageLayer setBounds:CGRectMake(self.colorIcon.frame.origin.x, self.colorIcon.frame.origin.y, self.colorIcon.frame.size.height, self.colorIcon.frame.size.height)];
    self.name.text = name;
    [self.name  setFont:[UIFont fontWithName:@"Ubuntu-Medium" size:17]];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
