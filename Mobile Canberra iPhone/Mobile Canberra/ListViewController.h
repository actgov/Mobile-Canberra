//
//  ListViewController.h
//  Mobile Canberra
//
//  Created by Zaki Bouguettaya on 16/09/13.
//  Copyright (c) 2013 Imagine Team. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "RESideMenu.h"

@interface ListViewController : UIViewController <UITableViewDataSource, UITableViewDelegate, UITextFieldDelegate>


@property (nonatomic, strong) IBOutlet UITextField *search;
@property (nonatomic, strong) IBOutlet UILabel *helpmefind;
@property (nonatomic, strong) IBOutlet UITableView *tableView;
@property (nonatomic, strong) NSMutableIndexSet *optionIndices;
@property (nonatomic, strong) NSMutableArray* datasets;
@property BOOL favclicked;
@property (strong , nonatomic) RESideMenu *mapSideMenu;

- (IBAction)showMenu:(id)sender;
- (IBAction)showFavs:(id)sender;
- (IBAction)aroundMeClicked:(id)sender;

@end
