//
//  ParallaxViewController.h
//  Mobile Canberra
//
//  Created by Zaki Bouguettaya on 16/09/13.
//  Copyright (c) 2013 Imagine Team. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ACParallaxView.h"
#import "RESideMenu.h"

@interface ParallaxViewController : UIViewController <ACParallaxViewDelegate>

@property (nonatomic, strong) IBOutlet ACParallaxView *parallaxView;
@property (strong, readonly, nonatomic) RESideMenu *sideMenu;


@end
