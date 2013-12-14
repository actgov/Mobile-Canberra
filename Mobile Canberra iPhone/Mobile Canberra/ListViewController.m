//
//  ListViewController.m
//  Mobile Canberra
//
//  Created by Zaki Bouguettaya on 16/09/13.
//  Copyright (c) 2013 Imagine Team. All rights reserved.
//

#import "ListViewController.h"
#import "MainTableCell.h"
#import "MapViewController.h"
#include "RESideMenu.h"
#import "DataSetListElement.h"
#import "SVProgressHUD.h"
#import "ASIHTTPRequest.h"
#import "MYIntroductionPanel.h"
#import "MYBlurIntroductionView.h"

@interface ListViewController ()

@end

@implementation ListViewController


NSUserDefaults *standardUserDefaults;
NSMutableArray* originaldatasets;
BOOL favclicked;
BOOL aroundmeclicked;
UIGestureRecognizer *tapper;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
     standardUserDefaults = [NSUserDefaults standardUserDefaults];
    self.datasets =  [[NSMutableArray alloc] init];
    originaldatasets = [[NSMutableArray alloc] init];
    UIColor *color = [UIColor colorWithRed:104.0/255.0 green:185.0/255.0 blue:213.0/255.0 alpha:1];
    self.search.attributedPlaceholder = [[NSAttributedString alloc] initWithString:@"I'm looking for..." attributes:@{NSForegroundColorAttributeName: color}];
    self.search.delegate = self;
    [self.tableView setDelegate:self];
    [self.tableView setDataSource:self];
    [self.tableView setSeparatorColor:[UIColor colorWithRed:64.0/255.0 green:71.0/255.0 blue:82.0/255.0 alpha:1]];
    [self.helpmefind  setFont:[UIFont fontWithName:@"Ubuntu-Medium" size:18]];
    self.optionIndices = [NSMutableIndexSet indexSetWithIndex:1];
    [SVProgressHUD showWithStatus:@"Getting list of services..." maskType:SVProgressHUDMaskTypeClear];
    [self performSelectorInBackground:@selector(getDatasets) withObject:nil];
    UIPanGestureRecognizer *gestureRecognizer = [[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(swipeHandler:)];
    [self.view addGestureRecognizer:gestureRecognizer];
    tapper = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(handleSingleTap:)];
    tapper.cancelsTouchesInView = FALSE;
    [self.view addGestureRecognizer:tapper];
   
}

- (void)handleSingleTap:(UITapGestureRecognizer *) sender
{
    [self.view endEditing:YES];
}


- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    if (self.search.text.length==0){
        [self.datasets removeAllObjects];
        [self.datasets addObjectsFromArray:originaldatasets];
        [self.tableView reloadData];
        [SVProgressHUD showErrorWithStatus:@"Sorry, invalid search term"];
    }
    else{
        [self.datasets removeAllObjects];
        [self.datasets addObjectsFromArray:originaldatasets];
        [self.view endEditing:YES];
        [SVProgressHUD showWithStatus:@"Searching..." maskType:SVProgressHUDMaskTypeClear];
        [self performSelectorInBackground:@selector(searchDatasets) withObject:nil];
    }
    return YES;
}

- (void)viewDidAppear:(BOOL)animated{
    [super viewDidAppear:animated];
    aroundmeclicked = NO;
    favclicked=NO;
    if ([[standardUserDefaults objectForKey:@"favclicked"] isEqualToString:@"is"]){
        [standardUserDefaults setObject:@"isnt" forKey:@"favclicked"];
        [standardUserDefaults synchronize];
        [self showFavs:self];
    }
    else{

        [self.datasets removeAllObjects];
        [self.datasets addObjectsFromArray:originaldatasets];
        [self.tableView reloadData];
    }
    
}

- (void)swipeHandler:(UIPanGestureRecognizer *)sender
{
    [[self sideMenu] showFromPanGesture:sender];
}

- (IBAction)aroundMeClicked:(id)sender{
    // Simple menus
    //
    RESideMenuItem *mainitem = [[RESideMenuItem alloc] initWithTitle:@"main" action:^(RESideMenu *menu, RESideMenuItem *item) {
        MapViewController *mapViewController = [[MapViewController alloc] initWithNibName:@"MapViewController" bundle:nil];
        
        DataSetListElement *dataset = [self.datasets objectAtIndex:0];
        mapViewController.clickedDataset = dataset;
        mapViewController.datasets = self.datasets;
            mapViewController.aroundmeclicked = YES;
      
        [menu displayContentController:mapViewController];
        
    }];
    
    RESideMenuItem *home = [[RESideMenuItem alloc] initWithTitle:@"Home" action:^(RESideMenu *menu, RESideMenuItem *item) {
        [self.mapSideMenu dismissViewControllerAnimated:YES completion:nil];
        [menu hide];
    }];
    
    
    RESideMenuItem *helpPlus1 = [[RESideMenuItem alloc] initWithTitle:@"How to use" action:^(RESideMenu *menu, RESideMenuItem *item) {
        //Create Stock Panel with header
        UIView *headerView = [[NSBundle mainBundle] loadNibNamed:@"Header" owner:nil options:nil][0];
        MYIntroductionPanel *panel1 = [[MYIntroductionPanel alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height) title:@"Map Screen Explanation" description:@"Click on a marker to see the details of the point of interest. Once a marker is clicked, the details will be displayed. You will also have the option to obtain directions to the marker, and add the service to which the marker belongs to your favourites." image:[UIImage imageNamed:@"pindetails.png"] header:headerView];
        
        
        MYIntroductionPanel *panel2 = [[MYIntroductionPanel alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height) title:@"Map Screen Explanation" description:@"When zoomed out, the markers will cluster together. Zoom in to see individual markers" image:[UIImage imageNamed:@""]];
        
        
        MYIntroductionPanel *panel5 = [[MYIntroductionPanel alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height) title:@"Main Screen Explanation" description:@"Press the search button on the side to bring out a search entry, where you can look up service locations around a particular address. Press the triangle icon to zoom to your current location" image:[UIImage imageNamed:@"currentlocabout.png"]];
        
        MYIntroductionPanel *panel3 = [[MYIntroductionPanel alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height) title:@"Main Screen Explanation" description:@"Click the triangle at the bottom of the screen to bring up the key gallery. Click datasets in the gallery to load additional services onto the map." image:[UIImage imageNamed:@"galleryopened.png"]];
        
        
        
        //Add panels to an array
        NSArray *panels = @[panel1, panel2,  panel5, panel3];
        [menu hide];
        
        //Create the introduction view and set its delegate
        MYBlurIntroductionView *introductionView = [[MYBlurIntroductionView alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height)];
        
        
        [introductionView setBackgroundColor:[UIColor colorWithRed:55.0f/255.0f green:61.0f/255.0f blue:70.0f/255.0f alpha:0.05]];
        
        
        //Build the introduction with desired panels
        [introductionView buildIntroductionWithPanels:panels];
        
        //Add the introduction to your view
        [self.mapSideMenu.view addSubview:introductionView];
    }];
    
    
    
    RESideMenuItem *privacy = [[RESideMenuItem alloc] initWithTitle:@"Privacy Policy" action:^(RESideMenu *menu, RESideMenuItem *item) {
        //Create Stock Panel with header
        
        MYIntroductionPanel *panel1 = [[MYIntroductionPanel alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height) title:@"Privacy Policy" description:@"Your privacy is very important to us. Accordingly, we have developed this policy in order for you to understand how we collect, use, communicate and disclose and make use of personal information. \nWe will only retain  information as long as necessary for the fulfillment of those purposes.\nWe will collect personal information by lawful and fair means and, where appropriate, with the knowledge or consent of the individual concerned.\nWe will protect personal information by reasonable security safeguards against loss or theft, as well as unauthorized access, disclosure, copying, use or modification. We will make readily available to customers information about our policies and practices relating to the management of personal information." image:[UIImage imageNamed:@""] header:nil];
        
        
        
        
        //Add panels to an array
        NSArray *panels = @[panel1];
        [menu hide];
        
        //Create the introduction view and set its delegate
        MYBlurIntroductionView *introductionView = [[MYBlurIntroductionView alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height)];
        
        
        [introductionView setBackgroundColor:[UIColor colorWithRed:55.0f/255.0f green:61.0f/255.0f blue:70.0f/255.0f alpha:0.05]];
        
        
        //Build the introduction with desired panels
        [introductionView buildIntroductionWithPanels:panels];
        
        //Add the introduction to your view
        [self.mapSideMenu.view addSubview:introductionView];
        
    }];
    
    
    
    // Simple menu with an alert
    //
    RESideMenuItem *about = [[RESideMenuItem alloc] initWithTitle:@"About" action:^(RESideMenu *menu, RESideMenuItem *item) {
        
        MYIntroductionPanel *panel1 = [[MYIntroductionPanel alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height) title:@"About" description:@"An ACT Government Initiative\n\nDeveloped by the Imagine Team Pty Ltd, designed by Zoo Advertising Pty Ltd\n\nPlease be advised that in some rare instances, some service locations may be inaccurate.\n\nLibraries Used:\n    MyBlurIntroductionView\n    iCarousel\n    KPClustering\n    SVProgessHUD\n    RESSideMenu\n    ACParallax\n\nPlease email support@imagineteamsolutions.com for more details." image:[UIImage imageNamed:@""] header:nil];
        //Add panels to an array
        NSArray *panels = @[panel1];
        [menu hide];
        
        //Create the introduction view and set its delegate
        MYBlurIntroductionView *introductionView = [[MYBlurIntroductionView alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height)];
        
        
        [introductionView setBackgroundColor:[UIColor colorWithRed:55.0f/255.0f green:61.0f/255.0f blue:70.0f/255.0f alpha:0.05]];
        
        
        //Build the introduction with desired panels
        [introductionView buildIntroductionWithPanels:panels];
        
        //Add the introduction to your view
        [self.mapSideMenu.view addSubview:introductionView];
    }];
    
    
    self.mapSideMenu = [[RESideMenu alloc] initWithItems:@[home, helpPlus1, privacy, about]];
    self.mapSideMenu.backgroundImage = [UIImage imageNamed:@"MobileCanberraSplash.png"];
    
    self.mapSideMenu.verticalLandscapeOffset = 16;
    self.mapSideMenu.openStatusBarStyle = UIStatusBarStyleBlackTranslucent;
    
    // Call the home action rather than duplicating the initialisation
    mainitem.action(self.mapSideMenu, mainitem);
    [standardUserDefaults setObject:@"isnt" forKey:@"favclicked"];
    [standardUserDefaults synchronize];
    self.mapSideMenu.modalTransitionStyle = UIModalTransitionStyleFlipHorizontal;
    [self presentViewController:self.mapSideMenu animated:YES completion:nil];
}

- (IBAction)showFavs:(id)sender{
    if (favclicked==NO){
    for (int i=0;i<self.datasets.count;i++){
        DataSetListElement *dataset = [self.datasets objectAtIndex:i];
        if ([standardUserDefaults objectForKey:dataset.datasetId]==nil){
            [self.datasets removeObjectAtIndex:i];
          
        }
    }
    }
    else{
    
        [self.datasets removeAllObjects];
        [self.datasets addObjectsFromArray:originaldatasets];
        
    }
    
    favclicked = !favclicked;
    [self.tableView reloadData];
}


-(UIColor*)getColor:(NSString*)color{
    UIColor* passed;
    
    if ([color isEqualToString:@"red"]){
        passed= [UIColor colorWithRed:255.0/255.0 green:0/255.0 blue:0/255.0 alpha:1];
    }
    else if ([color isEqualToString:@"blue"]){
        passed= [UIColor colorWithRed:0/255.0 green:180/255.0 blue:240/255.0 alpha:1];
    }
    else if ([color isEqualToString:@"yellow"]){
        passed= [UIColor colorWithRed:233/255.0 green:203/255.0 blue:95/255.0 alpha:1];
    }
    else if ([color isEqualToString:@"orange"]){
        passed= [UIColor colorWithRed:253/255.0 green:149/255.0 blue:65/255.0 alpha:1];
    }
    else if ([color isEqualToString:@"lime"]){
        passed= [UIColor colorWithRed:120/255.0 green:254/255.0 blue:11/255.0 alpha:1];
    }
    else if ([color isEqualToString:@"cyan"]){
        passed= [UIColor colorWithRed:11/255.0 green:254/255.0 blue:228/255.0 alpha:1];
    }
    else if ([color isEqualToString:@"purple"]){
        passed= [UIColor colorWithRed:3/255.0 green:95/255.0 blue:147/255.0 alpha:1];
    }
    else if ([color isEqualToString:@"pride"]){
        passed= [UIColor colorWithRed:242/255.0 green:65/255.0 blue:253/255.0 alpha:1];
    }
    else if ([color isEqualToString:@"akaroa"]){
        passed = [UIColor colorWithRed:212/255.0 green:196/255.0 blue:168/255.0 alpha:1];
    }
    else if ([color isEqualToString:@"amber"]){
        passed = [UIColor colorWithRed:255/255.0 green:191/255.0 blue:0/255.0 alpha:1];
    }
    else if ([color isEqualToString:@"amulet"]){
        passed = [UIColor colorWithRed:123/255.0 green:159/255.0 blue:128/255.0 alpha:1];
    }
    else if ([color isEqualToString:@"beaver"]){
        passed = [UIColor colorWithRed:146/255.0 green:111/255.0 blue:91/255.0 alpha:1];
    }
    
    
    return passed;
}


-(void)getDatasets{
    NSURL *url = [NSURL URLWithString:[NSString stringWithFormat:@"http://mobilecanberra.imagineteamsolutions.com:8080/mobilecanberra/GetListOfDatasets"]];
    

    ASIHTTPRequest *request = [ASIHTTPRequest requestWithURL:url];
    [request startSynchronous];
    NSError *error = [request error];
    NSDictionary* jsonDict;
    if (!error) {
        NSData *data = [request responseData];
        
        jsonDict = [NSJSONSerialization
                    JSONObjectWithData:data options:kNilOptions
                    error:&error];
        
        NSError *error;
        
        jsonDict = [NSJSONSerialization
                    JSONObjectWithData:data options:NSJSONReadingAllowFragments
                    error:&error];
      
        
        [self performSelectorOnMainThread:@selector(gotDatasets:) withObject:jsonDict  waitUntilDone:YES];
        
    }
    else {
        [self performSelectorOnMainThread:@selector(gotDatasets:) withObject:jsonDict  waitUntilDone:YES];
    }
    
}
-(void)gotDatasets:(NSDictionary*) jsonDict {
    NSNumber *success = [jsonDict objectForKey:@"success"];
    if ([success integerValue]==1){
        [SVProgressHUD showSuccessWithStatus:@"Done!"];
        NSArray *listOfDatasets = [jsonDict objectForKey:@"listOfDatasets"];
        for (int i=0;i<listOfDatasets.count;i++){
            NSDictionary *dataobject = [listOfDatasets objectAtIndex:i];
            DataSetListElement *dataset = [[DataSetListElement alloc] init];
            [dataset initWithName:[dataobject objectForKey:@"name"] andColor:[dataobject objectForKey:@"color"] andId:[dataobject objectForKey:@"id"]];
            [self.datasets addObject:dataset];
            [originaldatasets addObject:dataset];
        }
        
        [self.tableView reloadData];
    }
    else{
        [SVProgressHUD showErrorWithStatus:@"Sorry, something went wrong. Please try again later"];
        
    }
}


-(void)searchDatasets{
    NSURL *url = [NSURL URLWithString:[NSString stringWithFormat:@"http://mobilecanberra.imagineteamsolutions.com:8080/mobilecanberra/SearchDatasets?searchterm=%@",self.search.text]];
    
   
    ASIHTTPRequest *request = [ASIHTTPRequest requestWithURL:url];
    [request startSynchronous];
    NSError *error = [request error];
    NSDictionary* jsonDict;
    if (!error) {
        NSData *data = [request responseData];
        
        jsonDict = [NSJSONSerialization
                                  JSONObjectWithData:data options:kNilOptions
                                  error:&error];
        
        NSError *error;
        
        jsonDict = [NSJSONSerialization
                    JSONObjectWithData:data options:NSJSONReadingAllowFragments
                    error:&error];
    
        
        [self performSelectorOnMainThread:@selector(gotFilteredDatasets:) withObject:jsonDict  waitUntilDone:YES];
        
    }
    else {
        [self performSelectorOnMainThread:@selector(gotFilteredDatasets:) withObject:jsonDict  waitUntilDone:YES];
    }

}
-(void)gotFilteredDatasets:(NSDictionary*) jsonDict {
    NSNumber *success = [jsonDict objectForKey:@"success"];
    if ([success integerValue]==1){
        
         NSArray *listOfDatasets = [jsonDict objectForKey:@"results"];
        for (int i=0;i<listOfDatasets.count;i++){
             NSString *dataobjectname = [listOfDatasets objectAtIndex:i];
           
            NSMutableArray *toRemove = [[NSMutableArray alloc] init];
            DataSetListElement *filtered = nil;
            for (int z=0;z<self.datasets.count;z++){
                DataSetListElement *dataset = [self.datasets objectAtIndex:z];
               
                if ([dataset.name isEqualToString:dataobjectname]==true){
                    filtered = dataset;
                }
            }
            if (filtered!=nil){
                [self.datasets removeAllObjects];
                [self.datasets addObject:filtered];
                [SVProgressHUD showSuccessWithStatus:@"Done!"];
            }
            else {
                [SVProgressHUD showErrorWithStatus:@"Sorry, no services were found with your query"];
            }
            
        }
       
        [self.tableView reloadData];
    }
    else{
        [SVProgressHUD showErrorWithStatus:@"Sorry, something went wrong. Please try again later"];

    }
}




- (IBAction)showMenu:(id)sender {
    
   [[self sideMenu] show];

}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#

#pragma mark - TableView source & delegate

- (int)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (int)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    
    return self.datasets.count;
}


- (UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section
{
    UIView *view = [[UIView alloc] init];
    
    return view;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSString *cellIdentifier = @"MainCellID";
    
    MainTableCell *cell = [tableView dequeueReusableCellWithIdentifier:cellIdentifier];
    if (cell == nil){
        cell = [[[NSBundle mainBundle] loadNibNamed: @"MainTableCell" owner: nil options: nil] objectAtIndex: 0];
    }
    DataSetListElement *dataset = [self.datasets objectAtIndex:indexPath.row];

    [cell initWithDataSetName:dataset.name withColor:[self getColor:dataset.color]];
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 75;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [self.tableView deselectRowAtIndexPath:self.tableView.indexPathForSelectedRow animated:YES];
    
    // Simple menus
    //
    RESideMenuItem *mainitem = [[RESideMenuItem alloc] initWithTitle:@"main" action:^(RESideMenu *menu, RESideMenuItem *item) {
        MapViewController *mapViewController = [[MapViewController alloc] initWithNibName:@"MapViewController" bundle:nil];
        DataSetListElement *dataset = [self.datasets objectAtIndex:indexPath.row];
        mapViewController.clickedDataset = dataset;
        mapViewController.datasets = self.datasets;
        
        [menu displayContentController:mapViewController];

    }];
    
    RESideMenuItem *home = [[RESideMenuItem alloc] initWithTitle:@"Home" action:^(RESideMenu *menu, RESideMenuItem *item) {
        [self.mapSideMenu dismissViewControllerAnimated:YES completion:nil];
        [menu hide];
    }];
    
    
    RESideMenuItem *helpPlus1 = [[RESideMenuItem alloc] initWithTitle:@"How to use" action:^(RESideMenu *menu, RESideMenuItem *item) {
        //Create Stock Panel with header
        UIView *headerView = [[NSBundle mainBundle] loadNibNamed:@"Header" owner:nil options:nil][0];
        MYIntroductionPanel *panel1 = [[MYIntroductionPanel alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height) title:@"Map Screen Explanation" description:@"Click on a marker to see the details of the point of interest. Once a marker is clicked, the details will be displayed. You will also have the option to obtain directions to the marker, and add the service to which the marker belongs to your favourites." image:[UIImage imageNamed:@"pindetails.png"] header:headerView];
        
        
        MYIntroductionPanel *panel2 = [[MYIntroductionPanel alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height) title:@"Map Screen Explanation" description:@"When zoomed out, the markers will cluster together. Zoom in to see individual markers" image:[UIImage imageNamed:@""]];
        
        
        MYIntroductionPanel *panel5 = [[MYIntroductionPanel alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height) title:@"Main Screen Explanation" description:@"Press the search button on the side to bring out a search entry, where you can look up service locations around a particular address. Press the triangle icon to zoom to your current location" image:[UIImage imageNamed:@"currentlocabout.png"]];
        
        MYIntroductionPanel *panel3 = [[MYIntroductionPanel alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height) title:@"Main Screen Explanation" description:@"Click the triangle at the bottom of the screen to bring up the key gallery. Click datasets in the gallery to load additional services onto the map." image:[UIImage imageNamed:@"galleryopened.png"]];
    
        
        
        //Add panels to an array
        NSArray *panels = @[panel1, panel2,  panel5, panel3];
        [menu hide];
        
        //Create the introduction view and set its delegate
        MYBlurIntroductionView *introductionView = [[MYBlurIntroductionView alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height)];
        
        
        [introductionView setBackgroundColor:[UIColor colorWithRed:55.0f/255.0f green:61.0f/255.0f blue:70.0f/255.0f alpha:0.5]];
        
        
        //Build the introduction with desired panels
        [introductionView buildIntroductionWithPanels:panels];
        
        //Add the introduction to your view
        [self.mapSideMenu.view addSubview:introductionView];
    }];
    
    
    
    RESideMenuItem *privacy = [[RESideMenuItem alloc] initWithTitle:@"Privacy Policy" action:^(RESideMenu *menu, RESideMenuItem *item) {
        //Create Stock Panel with header
        
        MYIntroductionPanel *panel1 = [[MYIntroductionPanel alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height) title:@"Privacy Policy" description:@"Your privacy is very important to us. Accordingly, we have developed this policy in order for you to understand how we collect, use, communicate and disclose and make use of personal information. \nWe will only retain  information as long as necessary for the fulfillment of those purposes.\nWe will collect personal information by lawful and fair means and, where appropriate, with the knowledge or consent of the individual concerned.\nWe will protect personal information by reasonable security safeguards against loss or theft, as well as unauthorized access, disclosure, copying, use or modification. We will make readily available to customers information about our policies and practices relating to the management of personal information." image:[UIImage imageNamed:@""] header:nil];
        
        
        
        
        //Add panels to an array
        NSArray *panels = @[panel1];
        [menu hide];
        
        //Create the introduction view and set its delegate
        MYBlurIntroductionView *introductionView = [[MYBlurIntroductionView alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height)];
        
        
        [introductionView setBackgroundColor:[UIColor colorWithRed:55.0f/255.0f green:61.0f/255.0f blue:70.0f/255.0f alpha:0.5]];
        
        
        //Build the introduction with desired panels
        [introductionView buildIntroductionWithPanels:panels];
        
        //Add the introduction to your view
        [self.mapSideMenu.view addSubview:introductionView];
        
    }];
    
    
    
    // Simple menu with an alert
    //
    RESideMenuItem *about = [[RESideMenuItem alloc] initWithTitle:@"About" action:^(RESideMenu *menu, RESideMenuItem *item) {
        
        MYIntroductionPanel *panel1 = [[MYIntroductionPanel alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height) title:@"About" description:@"An ACT Government / NICTA eGOV Cluster Initiative\n\nDeveloped by the Imagine Team Pty Ltd, designed by Zoo Advertising Pty Ltd. \n\nPlease be advised that in some instances, service locations may be inaccurate. In this event could you please email GIO@act.gov.au\n\nLibraries Used:\n     MyBlurIntroductionView\n     iCarousel\n     KPClustering\n     SVProgessHUD\n     RESSideMenu\n     ACParallax\n\nPlease email support@imagineteamsolutions.com for more development details." image:[UIImage imageNamed:@""] header:nil];
        //Add panels to an array
        NSArray *panels = @[panel1];
        [menu hide];
        
        //Create the introduction view and set its delegate
        MYBlurIntroductionView *introductionView = [[MYBlurIntroductionView alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height)];
        
        
        [introductionView setBackgroundColor:[UIColor colorWithRed:55.0f/255.0f green:61.0f/255.0f blue:70.0f/255.0f alpha:0.5]];
        
        
        //Build the introduction with desired panels
        [introductionView buildIntroductionWithPanels:panels];
        
        //Add the introduction to your view
        [self.mapSideMenu.view addSubview:introductionView];
    }];
    
    
    self.mapSideMenu = [[RESideMenu alloc] initWithItems:@[home, helpPlus1, privacy, about]];
    self.mapSideMenu.backgroundImage = [UIImage imageNamed:@"MobileCanberraSplash.png"];
    
    self.mapSideMenu.verticalLandscapeOffset = 16;
    
    
    self.mapSideMenu.openStatusBarStyle = UIStatusBarStyleBlackTranslucent;
    
    // Call the home action rather than duplicating the initialisation
    mainitem.action(self.mapSideMenu, mainitem);
    [standardUserDefaults setObject:@"isnt" forKey:@"favclicked"];
    [standardUserDefaults synchronize];
    self.mapSideMenu.modalTransitionStyle = UIModalTransitionStyleFlipHorizontal;
    [self presentViewController:self.mapSideMenu animated:YES completion:nil];
    
    
    
    
    
  
    
}



@end
