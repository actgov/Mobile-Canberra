//
//  MapViewController.h
//  Mobile Canberra
//
//  Created by Zaki Bouguettaya on 16/09/13.
//  Copyright (c) 2013 Imagine Team. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <CoreLocation/CoreLocation.h>
#import "DataSetListElement.h"
#import <MapKit/MapKit.h>
#import "RESideMenu.h"
#import "KPTreeController.h"
#import "iCarousel.h"
#import "ListViewController.h"


@interface MapViewController : UIViewController <MKMapViewDelegate, CLLocationManagerDelegate, UITextFieldDelegate, KPTreeControllerDelegate, iCarouselDataSource, iCarouselDelegate>

@property (nonatomic, strong) IBOutlet UIView *mapHolder;
@property (nonatomic, strong) IBOutlet UIView *sliderHolder;
@property (nonatomic, strong) IBOutlet UIView *popupHolder;
@property (nonatomic, strong) IBOutlet UIButton *searchBtn;
@property (nonatomic, strong) IBOutlet UIButton *myLoc;
@property (nonatomic, strong) IBOutlet UITextField *search;
@property (nonatomic, strong) IBOutlet UITextView *pointDetails;
@property (nonatomic, strong) IBOutlet MKMapView *mapView;
@property (strong, nonatomic) CLLocationManager *locationManager;
@property (strong, nonatomic) DataSetListElement *clickedDataset;
@property (nonatomic, strong) KPTreeController *treeController;
@property (nonatomic, strong) IBOutlet UIImageView *popupBk;
@property (nonatomic, strong) IBOutlet UITextView *popupTitle;
@property (nonatomic, strong) IBOutlet UIButton *popupClose;
@property (nonatomic, strong) IBOutlet UIButton *popupDirections;
@property (strong, nonatomic) IBOutlet UIView *mapLegendGallery;
@property (strong, nonatomic) NSMutableArray *datasets;
@property BOOL aroundmeclicked;
@property (strong, nonatomic) IBOutlet iCarousel *mapGallery;


@property (weak, nonatomic) IBOutlet UIButton *mapLegendButton;



- (IBAction)showMenu:(id)sender;
- (IBAction)goHome:(id)sender;
- (IBAction)searchclicked:(id)sender;
- (IBAction)dismissPopup:(id)sender;
- (IBAction)goToCurrentLoc:(id)sender;
- (IBAction)goMapLegend:(id)sender;
- (IBAction)addToFav:(id)sender;
- (IBAction)favClicked:(id)sender;
- (IBAction)aroundMeClicked:(id)sender;
- (IBAction)directiontToMe:(id)sender;

@end
