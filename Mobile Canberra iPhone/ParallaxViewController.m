//
//  ParallaxViewController.m
//  Mobile Canberra
//
//  Created by Zaki Bouguettaya on 16/09/13.
//  Copyright (c) 2013 Imagine Team. All rights reserved.
//

#import "ParallaxViewController.h"
#import "ACParallaxView.h"
#import "ListViewController.h"
#import "MYIntroductionPanel.h"
#import "MYBlurIntroductionView.h"

@interface ParallaxViewController ()

@end

@implementation ParallaxViewController

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
    
    self.parallaxView.parallax = YES;
    self.parallaxView.parallaxDelegate = self;
    self.parallaxView.refocusParallax = YES;
    [self.view bringSubviewToFront:self.parallaxView];
    [UIView beginAnimations:@"fade in" context:nil];
    [UIView setAnimationDuration:4.0];
    self.parallaxView.alpha = 1.0;
    [UIView commitAnimations];
    
    double delayInSeconds = 5.5;
    dispatch_time_t popTime = dispatch_time(DISPATCH_TIME_NOW, (int64_t)(delayInSeconds * NSEC_PER_SEC));
    dispatch_after(popTime, dispatch_get_main_queue(), ^(void){
        NSMutableArray *_addedItems;
        NSMutableArray *_menuItems;
        _addedItems = [NSMutableArray array];
        _menuItems = [NSMutableArray array];
        
        // Simple menus
        //
        RESideMenuItem *homeItem = [[RESideMenuItem alloc] initWithTitle:@"Home" action:^(RESideMenu *menu, RESideMenuItem *item) {
            ListViewController *viewController = [[ListViewController alloc] init];
            [menu displayContentController:viewController];
        }];
        
               
        RESideMenuItem *helpPlus1 = [[RESideMenuItem alloc] initWithTitle:@"How to use" action:^(RESideMenu *menu, RESideMenuItem *item) {
            //Create Stock Panel with header
            UIView *headerView = [[NSBundle mainBundle] loadNibNamed:@"Header" owner:nil options:nil][0];
            MYIntroductionPanel *panel1 = [[MYIntroductionPanel alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height) title:@"Welcome to Mobile Canberra!" description:@"Mobile Canberra is a powerful platform for showing points of interest and services around Canberra." image:[UIImage imageNamed:@"searchbtn.png"] header:headerView];
            
            
            MYIntroductionPanel *panel2 = [[MYIntroductionPanel alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height) title:@"Main Screen Explanation" description:@"The main screen shows you a list of available services. This list will be updated as more services come online. To search for specific services, enter a search term into the search bar labeled 'I'm looking for..'. Example searches for the 'Bus Stops' service are 'Action', 'Buses', and 'Bus Stops'." image:[UIImage imageNamed:@"aroundmesearch.png"]];
            
            
            MYIntroductionPanel *panel5 = [[MYIntroductionPanel alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height) title:@"Main Screen Explanation" description:@"Each service will be assigned a color, which you can reference when looking at several services on the Map Screen" image:[UIImage imageNamed:@"datasetscolor.png"]];
            
            MYIntroductionPanel *panel3 = [[MYIntroductionPanel alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height) title:@"Main Screen Explanation" description:@"Click the favourites button on the top right to show your favourite services. You will be able to add services to your favourites on the Map Screen" image:[UIImage imageNamed:@"headerfavourites.png"]];
            
            MYIntroductionPanel *panel4 = [[MYIntroductionPanel alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height) title:@"Main Screen Explanation" description:@"Not particularly fussed on a particular dataset, but want to see what's around you generally? Click the 'Around Me' button on the top right to see all the services around you" image:[UIImage imageNamed:@"headeraroundme.png"]];
            
            
            //Add panels to an array
            NSArray *panels = @[panel1, panel2,  panel5, panel3, panel4];
            [menu hide];
            
            //Create the introduction view and set its delegate
            MYBlurIntroductionView *introductionView = [[MYBlurIntroductionView alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height)];
           
          
            [introductionView setBackgroundColor:[UIColor colorWithRed:55.0f/255.0f green:61.0f/255.0f blue:70.0f/255.0f alpha:0.05]];
           
            
            //Build the introduction with desired panels
            [introductionView buildIntroductionWithPanels:panels];
            
            //Add the introduction to your view
            [_sideMenu.view addSubview:introductionView];
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
                [_sideMenu.view addSubview:introductionView];
            
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
            
            
            [introductionView setBackgroundColor:[UIColor colorWithRed:55.0f/255.0f green:61.0f/255.0f blue:70.0f/255.0f alpha:0.05]];
            
            
            //Build the introduction with desired panels
            [introductionView buildIntroductionWithPanels:panels];
            
            //Add the introduction to your view
            [_sideMenu.view addSubview:introductionView];
        }];
        
        
        _sideMenu = [[RESideMenu alloc] initWithItems:@[helpPlus1, privacy, about]];
        _sideMenu.backgroundImage = [UIImage imageNamed:@"MobileCanberraSplash.png"];
       
        _sideMenu.verticalLandscapeOffset = 16;
        
        
        _sideMenu.openStatusBarStyle = UIStatusBarStyleBlackTranslucent;
        
        // Call the home action rather than duplicating the initialisation
        homeItem.action(_sideMenu, homeItem);
        
        [self presentViewController:_sideMenu animated:YES completion:nil];

    });
    
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
