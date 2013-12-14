//
//  MapViewController.m
//  Mobile Canberra
//
//  Created by Zaki Bouguettaya on 16/09/13.
//  Copyright (c) 2013 Imagine Team. All rights reserved.
//

#import "MapViewController.h"
#import "UIView+Animation.h"
#import "SVProgressHUD.h"
#import "ASIHTTPRequest.h"
#import "VPPMapHelper.h"
#import "MapAnnotationExample.h"
#import "VPPMapClusterView.h"
#import "VPPMapCluster.h"
#import "KPAnnotation.h"


@interface MapViewController ()

@end

@implementation MapViewController

UIGestureRecognizer *tapper;
NSMutableDictionary *clickedDatasets;
NSUserDefaults *standardUserDefaults;
int counter;
CLLocationCoordinate2D currentLoc;
bool tryToGetDirections;
MKRoute *route;

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
    clickedDatasets = [[NSMutableDictionary alloc] init];
    self.mapView.delegate = self;
    MKCoordinateSpan span = MKCoordinateSpanMake(0, 360/pow(2, 15)*self.mapView.frame.size.width/256);
    [self.mapView setRegion:MKCoordinateRegionMake(CLLocationCoordinate2DMake(-35.2828, 149.1314), span) animated:YES];
    // sets up the map
    self.treeController = [[KPTreeController alloc] initWithMapView:self.mapView];
    self.treeController.delegate = self;
    self.treeController.animationOptions = UIViewAnimationOptionCurveEaseOut;
    
    self.mapGallery.type = iCarouselTypeLinear;
    self.mapGallery.dataSource = self;
    self.mapGallery.delegate = self;
    counter=0;
}

- (IBAction)goHome:(id)sender{
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (IBAction)favClicked:(id)sender{
    
    [standardUserDefaults setObject:@"is" forKey:@"favclicked"];
    [standardUserDefaults synchronize];
    [self dismissViewControllerAnimated:YES completion:nil];
}


- (IBAction)directiontToMe:(id)sender{
    [self dismissPopup:self];
    tryToGetDirections = YES;
    [SVProgressHUD showWithStatus:@"Searching..."
                         maskType:SVProgressHUDMaskTypeGradient];
    [self.locationManager startUpdatingLocation];
    
    
}



- (void)findDirectionsFrom:(MKMapItem *)source
                        to:(MKMapItem *)destination
{
    MKDirectionsRequest *request = [[MKDirectionsRequest alloc] init];
    request.source = source;
    request.destination = destination;
    request.requestsAlternateRoutes = YES;
    
    MKDirections *directions = [[MKDirections alloc] initWithRequest:request];
    
    [directions calculateDirectionsWithCompletionHandler:
     ^(MKDirectionsResponse *response, NSError *error) {
         
         [SVProgressHUD dismiss];
         if (error) {
            
         }
         else {
             [self.mapView removeOverlay:route.polyline];
             route = response.routes[0];
             [self.mapView addOverlay:route.polyline];
             
         }
     }];
}


#pragma mark - MKMapViewDelegate

- (MKOverlayRenderer *)mapView:(MKMapView *)mapView
            rendererForOverlay:(id<MKOverlay>)overlay
{
    MKPolylineRenderer *renderer = [[MKPolylineRenderer alloc] initWithOverlay:overlay];
    renderer.lineWidth = 5.0;
    renderer.strokeColor = [UIColor purpleColor];
    return renderer;
}




-(UIImage*)imageWithImage: (UIImage*) sourceImage scaledToWidth: (float) i_width
{
    float oldWidth = sourceImage.size.width;
    float scaleFactor = i_width / oldWidth;
    
    float newHeight = sourceImage.size.height * scaleFactor;
    float newWidth = oldWidth * scaleFactor;
    
    UIGraphicsBeginImageContext(CGSizeMake(newWidth, newHeight));
    [sourceImage drawInRect:CGRectMake(0, 0, newWidth, newHeight)];
    UIImage *newImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return newImage;
}

-(void) viewDidAppear:(BOOL)animated{
    [super viewDidAppear:animated];
    
    [self.mapHolder bringSubviewToFront:self.sliderHolder];
    [self.mapHolder bringSubviewToFront:self.searchBtn];
    [self.mapHolder bringSubviewToFront:self.myLoc];
    self.sliderHolder.hidden = NO;
    self.searchBtn.hidden = NO;
    self.myLoc.hidden = NO;
    self.search.delegate = self;
    UIColor *color = [UIColor whiteColor];
    self.search.attributedPlaceholder = [[NSAttributedString alloc] initWithString:@"Search" attributes:@{NSForegroundColorAttributeName: color}];
    
    
    self.locationManager = [[CLLocationManager alloc] init];
    self.locationManager.desiredAccuracy = kCLLocationAccuracyHundredMeters;
    self.locationManager.delegate = self;
    [self.locationManager startUpdatingLocation];
    
    
    tapper = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(handleSingleTap:)];
    tapper.cancelsTouchesInView = FALSE;
    [self.view addGestureRecognizer:tapper];
    if (self.aroundmeclicked==YES){
        [self getAllDatasets];
    }
    else{
        [SVProgressHUD showWithStatus:@"Loading Points.." maskType:SVProgressHUDMaskTypeClear];
        [self performSelectorInBackground:@selector(getListOfPoints) withObject:nil];
   
    }
    
    
}

- (IBAction)aroundMeClicked:(id)sender{
    counter =0;
    [self goToCurrentLoc:self];
    [self getAllDatasets];
}


- (void) getAllDatasets{
    if (counter==0){
        [SVProgressHUD showWithStatus:@"Loading all points around you. Please note that this may take up to 30 seconds..." maskType:SVProgressHUDMaskTypeClear];
    }
    
    for (int i=0;i<self.datasets.count;i++){
        
        DataSetListElement *pointlookedat = (self.datasets)[i];
        
       if ([clickedDatasets objectForKey:pointlookedat.datasetId]==nil){
            counter ++;
            self.clickedDataset =(self.datasets)[i];
            [self performSelectorInBackground:@selector(getListOfPoints) withObject:nil];
            break;
        }
    }
    
    if (counter ==self.datasets.count){
        [SVProgressHUD dismiss];
    }
    
}


- (void)getListOfPoints{
    NSURL *url = [NSURL URLWithString:[NSString stringWithFormat:@"http://mobilecanberra.imagineteamsolutions.com:8080/mobilecanberra/AroundMe?%@=YES", self.clickedDataset.datasetId]];
    
  
    ASIHTTPRequest *request = [ASIHTTPRequest requestWithURL:url];
    [request setTimeOutSeconds:20];
    [request startSynchronous];
    NSError *error = [request error];
    NSDictionary* jsonDict;
    if (!error) {
        NSString *dataString = [request responseString];
        
        NSData *data=[dataString dataUsingEncoding:NSUTF8StringEncoding];
        
       
        
        jsonDict = [NSJSONSerialization
                    JSONObjectWithData:data options:NSJSONReadingAllowFragments
                    error:&error];
        
        
        [self performSelectorOnMainThread:@selector(gotListOfPoints:) withObject:jsonDict  waitUntilDone:YES];
        
    }
    else{
        [self performSelectorOnMainThread:@selector(gotListOfPoints:) withObject:jsonDict  waitUntilDone:YES];
    }
}

- (void)gotListOfPoints:(NSDictionary*) jsonDict{
    NSNumber* success = [jsonDict objectForKey:@"success"];
    if ([success integerValue]==1){
        [clickedDatasets setValue:@"selected" forKey:self.clickedDataset.datasetId];
       
        UIImage* pinImage;
        
        if ([self.clickedDataset.color isEqualToString:@"red"]){
            pinImage = [self imageWithImage:[UIImage imageNamed:@"redpin"] scaledToWidth:25.0];
        }
        else if([self.clickedDataset.color isEqualToString:@"blue"]){
            pinImage = [self imageWithImage:[UIImage imageNamed:@"bluepin"] scaledToWidth:25.0];
        }
        else if([self.clickedDataset.color isEqualToString:@"yellow"]){
            pinImage = [self imageWithImage:[UIImage imageNamed:@"yellowpin"] scaledToWidth:25.0];
        }
        else if ([self.clickedDataset.color isEqualToString:@"orange"]){
           pinImage = [self imageWithImage:[UIImage imageNamed:@"orangepin"] scaledToWidth:25.0];
        }
        else if ([self.clickedDataset.color isEqualToString:@"lime"]){
            pinImage = [self imageWithImage:[UIImage imageNamed:@"limepin"] scaledToWidth:25.0];
        }
        else if ([self.clickedDataset.color isEqualToString:@"cyan"]){
            pinImage = [self imageWithImage:[UIImage imageNamed:@"cyanpin"] scaledToWidth:25.0];
        }
        else if ([self.clickedDataset.color isEqualToString:@"purple"]){
            pinImage = [self imageWithImage:[UIImage imageNamed:@"purplepin"] scaledToWidth:25.0];
        }
        else if ([self.clickedDataset.color isEqualToString:@"pride"]){
            pinImage = [self imageWithImage:[UIImage imageNamed:@"pridepin"] scaledToWidth:25.0];
        }
        else if ([self.clickedDataset.color isEqualToString:@"akaroa"]){
            pinImage = [self imageWithImage:[UIImage imageNamed:@"akaroapin"] scaledToWidth:25.0];
        }
        else if ([self.clickedDataset.color isEqualToString:@"amber"]){
            pinImage = [self imageWithImage:[UIImage imageNamed:@"amberpin"] scaledToWidth:25.0];
        }
        else if ([self.clickedDataset.color isEqualToString:@"amulet"]){
            pinImage = [self imageWithImage:[UIImage imageNamed:@"amuletpin"] scaledToWidth:25.0];
        }
        else if ([self.clickedDataset.color isEqualToString:@"beaver"]){
            pinImage = [self imageWithImage:[UIImage imageNamed:@"beaverpin"] scaledToWidth:25.0];
        }
        
        NSArray *locs =[jsonDict objectForKey:self.clickedDataset.datasetId];
        NSMutableArray *tempPlaces = [NSMutableArray new];
        for (int i=0;i<locs.count;i++){
            NSDictionary *object =[locs objectAtIndex:i];
            NSNumber *lat  = [object objectForKey:@"lat"];
            NSNumber *lon  = [object objectForKey:@"lon"];
            MapAnnotationExample *ann = [[MapAnnotationExample alloc] init];
            ann.coordinate = CLLocationCoordinate2DMake([lat doubleValue],[lon doubleValue]);
            ann.userData = object;
            ann.color =[object objectForKey:@"color"];
            ann.image = pinImage;
            ann.title =[object objectForKey:@"pointname"];
            ann.subtitle =[object objectForKey:@"pointtype"];
            [tempPlaces addObject:ann];
        }
        [self.treeController setAnnotations:tempPlaces];
       
        if (self.aroundmeclicked==NO){
             [SVProgressHUD dismiss];
            
        }
        else{
            [self getAllDatasets];
        }
        
        
        
        
    }
    else {
        [SVProgressHUD showErrorWithStatus:@"Sorry, something went wrong. Please try again later"];
    }
    
}



- (IBAction)showMenu:(id)sender {
    [[self sideMenu] show];
}

- (IBAction)addToFav:(id)sender{
   
    if ([annot.subtitle isEqualToString:@"Bus Stop"]){
        [standardUserDefaults setObject:@"favourited" forKey:@"22rs-ycdh"];
        
    }
    else if([annot.subtitle isEqualToString:@"Public Toilet"]){
        [standardUserDefaults setObject:@"favourited" forKey:@"3tyf-txjn"];
    }
    else if([annot.subtitle isEqualToString:@"Playground"]){
       [standardUserDefaults setObject:@"favourited" forKey:@"gk9r-4a8z"];
    }
    else if([annot.subtitle isEqualToString:@"Library"]){
        [standardUserDefaults setObject:@"favourited" forKey:@"hssi-h7fk"];
    }
    else if([annot.subtitle isEqualToString:@"ACT Tafe Campus"]){
        [standardUserDefaults setObject:@"favourited" forKey:@"s8n7-y27s"];
    }
    else if([annot.subtitle isEqualToString:@"ACT Schools"]){
        [standardUserDefaults setObject:@"favourited" forKey:@"q8rt-q8cy"];
    }
    else if([annot.subtitle isEqualToString:@"Public Furniture"]){
        [standardUserDefaults setObject:@"favourited" forKey:@"ch39-bukk"];
    }
    else if([annot.subtitle isEqualToString:@"Public Art"]){
        [standardUserDefaults setObject:@"favourited" forKey:@"j746-krni"];
    }
    else if([annot.subtitle isEqualToString:@"Public Barbeques"]){
        [standardUserDefaults setObject:@"favourited" forKey:@"n3b4-mm52"];
    }
    else if([annot.subtitle isEqualToString:@"Basketball Courts"]){
        [standardUserDefaults setObject:@"favourited" forKey:@"dez5-bbet"];
    }
    else if([annot.subtitle isEqualToString:@"Drinking Fountains"]){
        [standardUserDefaults setObject:@"favourited" forKey:@"vtby-6ybz"];
    }
    else if([annot.subtitle isEqualToString:@"Skate Parks"]){
        [standardUserDefaults setObject:@"favourited" forKey:@"genc-x5ru"];
    }
    
   
    [standardUserDefaults synchronize];
   
    [SVProgressHUD showSuccessWithStatus:@"Added dataset to favourites!"];
}

- (void)handleSingleTap:(UITapGestureRecognizer *) sender
{
    [self.view endEditing:YES];
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    
    [self.view endEditing:YES];
    
    if(self.search.text.length>0){
        [SVProgressHUD showWithStatus:@"Searching for address.." maskType:SVProgressHUDMaskTypeClear];
        [self performSelectorInBackground:@selector(searchAddressInBackground) withObject:nil];
    }
    
    return YES;
}

- (IBAction)searchclicked:(id)sender {
    if (self.search.hidden==YES){
        [self.sliderHolder raceTo:CGPointMake(0, 90) withSnapBack:YES];
        self.search.hidden = NO;
        
        [self.sliderHolder bringSubviewToFront:self.search];
        [self.search pulse:1.5 continuously:NO];
        
    }
    else{
        if(self.search.text.length>0){
            [SVProgressHUD showWithStatus:@"Searching for address.." maskType:SVProgressHUDMaskTypeClear];
            [self performSelectorInBackground:@selector(searchAddressInBackground) withObject:nil];
        }
        else{
            [SVProgressHUD showErrorWithStatus:@"No address entered!"];
        }
    }
    
}

- (IBAction) goMapLegend:(id)sender {
    
    self.mapLegendButton.hidden=YES;
    self.mapLegendGallery.hidden =NO;
    [self.mapLegendGallery raceTo:CGPointMake(0, self.view.frame.size.height-40) withSnapBack:YES];
    
    [self.view bringSubviewToFront:self.mapLegendGallery];
    [self.mapLegendGallery bringSubviewToFront:self.mapGallery];
    
    if (self.search.hidden==NO){
        self.search.hidden=YES;
    }

    
}



#pragma mark - iCarousel datasource methods
- (NSUInteger)numberOfItemsInCarousel:(iCarousel *)carousel
{
   
    return self.datasets.count;
}

- (UIView *)carousel:(iCarousel *)carousel viewForItemAtIndex:(NSUInteger)index reusingView:(UIView *)view
{
    UILabel *label = nil;
    UIView *colorIcon = nil;
    
    
        view = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 120, 40)];
       // ((UIImageView *)view).image = [UIImage imageNamed:@"page.png"];
        view.contentMode = UIViewContentModeCenter;
        colorIcon = [[UIView alloc]initWithFrame:CGRectMake(0, 10, 20, 20)];
        CALayer *imageLayer = colorIcon.layer;
        [imageLayer setCornerRadius:colorIcon.frame.size.height/2];
        [imageLayer setBorderWidth:0];
        [imageLayer setMasksToBounds:YES];
        [imageLayer setBounds:CGRectMake(colorIcon.frame.origin.x, colorIcon.frame.origin.y, colorIcon.frame.size.height, colorIcon.frame.size.height)];
        [view addSubview:colorIcon];
        label = [[UILabel alloc] initWithFrame:CGRectMake(25, 0, 100, 40)];
        label.backgroundColor = [UIColor clearColor];
        label.textColor = [UIColor whiteColor];
        [label setFont:[UIFont fontWithName:@"Ubuntu-Medium" size:13]];
        //view.backgroundColor = [UIColor blueColor];
        [view addSubview:label];
        view.tag =129;
    
    
    
    
    DataSetListElement *dataset = [self.datasets objectAtIndex:index];

    label.text = [NSString stringWithFormat:@"%@",dataset.name];
    colorIcon.backgroundColor = [self getColor:dataset.color];
    
    return view;
}

- (CGFloat)carousel:(iCarousel *)_carousel valueForOption:(iCarouselOption)option withDefault:(CGFloat)value
{
    //customize carousel display
    switch (option)
    {
        case iCarouselOptionWrap:
        {
            //normally you would hard-code this to YES or NO
            return YES;
        }
        case iCarouselOptionSpacing:
        {
            //add a bit of spacing between the item views
            return value * 1.05f;
        }
        case iCarouselOptionFadeMax:
        {
            
                return 0.5f;
            
         
        }
        default:
        {
            return value;
        }
    }
}

#pragma mark iCarousel taps

- (void)carousel:(iCarousel *)carousel didSelectItemAtIndex:(NSInteger)index
{

    self.clickedDataset = (self.datasets)[index];
 
    if ([clickedDatasets objectForKey:self.clickedDataset.datasetId]!=nil){
        [SVProgressHUD showErrorWithStatus:@"You've already selected this service"];
    }
    else{
        
        
        [SVProgressHUD showWithStatus:@"Loading Points.." maskType:SVProgressHUDMaskTypeClear];
        [self performSelectorInBackground:@selector(getListOfPoints) withObject:nil];
    }
    
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



- (IBAction)dismissPopup:(id)sender{
    
    [self.popupHolder curlUpAndAway:1.0];
    
}

- (IBAction)goToCurrentLoc:(id)sender{
    [self.locationManager startUpdatingLocation];
}


-(void)searchAddressInBackground{
    
    
    NSURL *url = [NSURL URLWithString:[NSString stringWithFormat:@"http://maps.googleapis.com/maps/api/geocode/json?address=%@+ACT&sensor=true", self.search.text]];
    

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
    
        
        [self performSelectorOnMainThread:@selector(searchAddressInForeground:) withObject:jsonDict  waitUntilDone:YES];
        
    }
    else{
        [self performSelectorOnMainThread:@selector(searchAddressInForeground:) withObject:jsonDict  waitUntilDone:YES];
    }
    
}

-(void)searchAddressInForeground:(NSDictionary*) jsonDict{
    NSString *status = [jsonDict objectForKey:@"status"];
    if ([status isEqualToString:@"OK"]){
        NSArray *response = [jsonDict objectForKey:@"results"];
        NSDictionary *geometry = [response objectAtIndex:0];
        geometry =[geometry objectForKey:@"geometry"];
        NSDictionary *location = [geometry objectForKey:@"location"];
        [SVProgressHUD dismiss];
        NSNumber *lat =[location objectForKey:@"lat"];
        NSNumber *lon=[location objectForKey:@"lng"];
        [self.mapView setCenterCoordinate:CLLocationCoordinate2DMake([lat doubleValue], [lon doubleValue])];
        
        
    }
    else{
        [SVProgressHUD showErrorWithStatus:@"Please check that you've entered a valid address"];
    }
    
    
}



- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    
}




#pragma mark - CLLocationManagerDelegate

- (void)locationManager:(CLLocationManager *)manager didFailWithError:(NSError *)error
{
    
   if (tryToGetDirections){
       
       tryToGetDirections=NO;
       [SVProgressHUD showErrorWithStatus:@"Sorry, your current location is unavailable"];

   }
    
}

- (void)locationManager:(CLLocationManager *)manager didUpdateToLocation:(CLLocation *)newLocation fromLocation:(CLLocation *)oldLocation
{
 
    currentLoc =CLLocationCoordinate2DMake(newLocation.coordinate.latitude, newLocation.coordinate.longitude);

    MKCoordinateRegion viewRegion = MKCoordinateRegionMakeWithDistance(CLLocationCoordinate2DMake(newLocation.coordinate.latitude, newLocation.coordinate.longitude), 300, 300);
    MKCoordinateRegion adjustedRegion = [self.mapView regionThatFits:viewRegion];
    [self.mapView setRegion:adjustedRegion animated:YES];
    self.mapView.showsUserLocation = YES;
    [self stopLocationManager];
    if (tryToGetDirections){
    
        tryToGetDirections = NO;
        // San Francisco Caltrain Station
        
        CLLocationCoordinate2D fromCoordinate = currentLoc;
        // Mountain View Caltrain Station
        CLLocationCoordinate2D toCoordinate   = CLLocationCoordinate2DMake(annot.coordinate.latitude,
                                                                           annot.coordinate.longitude);
        
        MKPlacemark *fromPlacemark = [[MKPlacemark alloc] initWithCoordinate:fromCoordinate
                                                           addressDictionary:nil];
        
        MKPlacemark *toPlacemark   = [[MKPlacemark alloc] initWithCoordinate:toCoordinate
                                                           addressDictionary:nil];
        
        MKMapItem *fromItem = [[MKMapItem alloc] initWithPlacemark:fromPlacemark];
        
        MKMapItem *toItem   = [[MKMapItem alloc] initWithPlacemark:toPlacemark];
        
        [self findDirectionsFrom:fromItem
                              to:toItem];
    }
}

- (void)stopLocationManager
{
    [self.locationManager stopUpdatingLocation];
    
}

- (void)mapView:(MKMapView *)mapView regionDidChangeAnimated:(BOOL)animated {
    [self.treeController refresh:YES];
}


KPAnnotation * annot;
- (void)mapView:(MKMapView *)mapView didSelectAnnotationView:(MKAnnotationView *)view {
  
    if([view.annotation isKindOfClass:[KPAnnotation class]]){
        
        KPAnnotation *cluster = (KPAnnotation *)view.annotation;
        
        if(cluster.annotations.count > 1){
          
        }
        else {
            annot = cluster;
        }
    }
    
    
}

- (UIImage*) resizeImageForAnnotation:(UIImage*)image {
    return image;
}


- (MKAnnotationView*) buildAnnotationViewWithAnnotation:(id<VPPMapCustomAnnotation>)annotation
                                        reuseIdentifier:(NSString*)identifier
                                             forMapView:(MKMapView*)theMapView {
    
    MKAnnotationView *customImageView = [[MKAnnotationView alloc] initWithAnnotation:annotation
                                                                     reuseIdentifier:identifier];
    
    
    customImageView.image = [self resizeImageForAnnotation:annotation.image];
    customImageView.opaque = NO;
    [customImageView setCanShowCallout:NO];
    
    // if (self.showsDisclosureButton) {
    UIButton* rightButton = [UIButton buttonWithType:UIButtonTypeDetailDisclosure];
    [rightButton addTarget:self
                    action:@selector(open:)
          forControlEvents:UIControlEventTouchUpInside];
    customImageView.rightCalloutAccessoryView = rightButton;
    // }
    
    return customImageView;
}


- (MKAnnotationView *)mapView:(MKMapView *)mapView viewForAnnotation:(id<MKAnnotation>)annotation {
    
        
        KPAnnotation *a = (KPAnnotation *)annotation;
        
        if([annotation isKindOfClass:[MKUserLocation class]]){
            return nil;
        }
        
        if(a.annotations.count>1){
            
            
            
            VPPMapClusterView *clusterView = (VPPMapClusterView *)[mapView dequeueReusableAnnotationViewWithIdentifier:@"cluster"];
            
            if (!clusterView) {
                clusterView = [[VPPMapClusterView alloc] initWithAnnotation:a reuseIdentifier:@"cluster"];
            }
            
            clusterView.title = [NSString stringWithFormat:@"%d",[[(VPPMapCluster*)annotation annotations] count]];
            clusterView.canShowCallout = NO;
            return clusterView;
        }
        else {
            
            MKPinAnnotationView *v = (MKPinAnnotationView *)[mapView dequeueReusableAnnotationViewWithIdentifier:@"pin"];
            
            if(!v){
                v = [[MKPinAnnotationView alloc] initWithAnnotation:annotation
                                                    reuseIdentifier:@"pin"];
            }
            MapAnnotationExample *annot = [a.annotations anyObject];
            v.image = annot.image;
            // if (self.showsDisclosureButton) {
            UIButton* rightButton = [UIButton buttonWithType:UIButtonTypeDetailDisclosure];
            [rightButton addTarget:self
                            action:@selector(open:)
                  forControlEvents:UIControlEventTouchUpInside];
            v.rightCalloutAccessoryView = rightButton;
            v.canShowCallout = YES;
            return  v;
        }
        
        
        
    
}

#pragma mark - KPTreeControllerDelegate

- (void)treeController:(KPTreeController *)tree configureAnnotationForDisplay:(KPAnnotation *)annotation {
     MapAnnotationExample *annot = [annotation.annotations anyObject];
    annotation.title = annot.title;
    annotation.subtitle = annot.subtitle;
}


#pragma mark VPPMapHelperDelegate

- (void) open:(id<MKAnnotation>)annotation {

   
    MapAnnotationExample *oc = [annot.annotations anyObject];
    NSDictionary * pointData = oc.userData;
    self.pointDetails.text = [pointData objectForKey:@"pointname"];
    self.popupTitle.text = oc.subtitle;
    NSString *pinColor  = oc.color;

    if ([pinColor isEqualToString:@"red"]){
        [self.popupBk setImage: [UIImage imageNamed:@"redlargepinbk"]];
        [self.popupClose setImage:[UIImage imageNamed:@"closemarkerredbtn"] forState:UIControlStateNormal];
        [self.popupDirections setImage:[UIImage imageNamed:@"redtakemetherebtn"] forState:UIControlStateNormal];
        
    }
    else if([pinColor isEqualToString:@"blue"]){
        [self.popupBk setImage: [UIImage imageNamed:@"bluelargepinbk"]];
        [self.popupClose setImage:[UIImage imageNamed:@"closemarkerbluebtn"] forState:UIControlStateNormal];
        [self.popupDirections setImage:[UIImage imageNamed:@"bluetakemetherebtn"] forState:UIControlStateNormal];
        
    }
    else if([pinColor isEqualToString:@"yellow"]){
        [self.popupBk setImage: [UIImage imageNamed:@"yellowlargepinbk"]];
        [self.popupClose setImage:[UIImage imageNamed:@"closemarkeryellowbtn"] forState:UIControlStateNormal];
        [self.popupDirections setImage:[UIImage imageNamed:@"yellowtakemetherebtn"] forState:UIControlStateNormal];
        
    }
    else if ([pinColor isEqualToString:@"orange"]){
        [self.popupBk setImage: [UIImage imageNamed:@"orangelargepinbk"]];
        [self.popupClose setImage:[UIImage imageNamed:@"closemarkerorangebtn"] forState:UIControlStateNormal];
        [self.popupDirections setImage:[UIImage imageNamed:@"orangetakemetherebtn"] forState:UIControlStateNormal];
    }
    else if ([pinColor isEqualToString:@"lime"]){
        [self.popupBk setImage: [UIImage imageNamed:@"limelargepinbk"]];
        [self.popupClose setImage:[UIImage imageNamed:@"closemarkerlimebtn"] forState:UIControlStateNormal];
        [self.popupDirections setImage:[UIImage imageNamed:@"limetakemetherebtn"] forState:UIControlStateNormal];
    }
    else if ([pinColor isEqualToString:@"cyan"]){
        [self.popupBk setImage: [UIImage imageNamed:@"cyanlargepinbk"]];
        [self.popupClose setImage:[UIImage imageNamed:@"closemarkercyanbtn"] forState:UIControlStateNormal];
        [self.popupDirections setImage:[UIImage imageNamed:@"cyantakemetherebtn"] forState:UIControlStateNormal];
    }
    else if ([pinColor isEqualToString:@"purple"]){
        [self.popupBk setImage: [UIImage imageNamed:@"purplelargepinbk"]];
        [self.popupClose setImage:[UIImage imageNamed:@"closemarkerpurplebtn"] forState:UIControlStateNormal];
        [self.popupDirections setImage:[UIImage imageNamed:@"purpletakemetherebtn"] forState:UIControlStateNormal];
    }
    else if ([pinColor isEqualToString:@"pride"]){
        [self.popupBk setImage: [UIImage imageNamed:@"pridelargepinbk"]];
        [self.popupClose setImage:[UIImage imageNamed:@"closemarkerpridebtn"] forState:UIControlStateNormal];
        [self.popupDirections setImage:[UIImage imageNamed:@"pridetakemetherebtn"] forState:UIControlStateNormal];
    }
    else if ([pinColor isEqualToString:@"akaroa"]){
        [self.popupBk setImage: [UIImage imageNamed:@"akaroalargepinbk"]];
        [self.popupClose setImage:[UIImage imageNamed:@"closemarkerakaroabtn"] forState:UIControlStateNormal];
        [self.popupDirections setImage:[UIImage imageNamed:@"akaroatakemetherebtn"] forState:UIControlStateNormal];
    }
    else if ([pinColor isEqualToString:@"amber"]){
        [self.popupBk setImage: [UIImage imageNamed:@"amberlargepinbk"]];
        [self.popupClose setImage:[UIImage imageNamed:@"closemarkeramberbtn"] forState:UIControlStateNormal];
        [self.popupDirections setImage:[UIImage imageNamed:@"ambertakemetherebtn"] forState:UIControlStateNormal];
    }
    else if ([pinColor isEqualToString:@"amulet"]){
        [self.popupBk setImage: [UIImage imageNamed:@"amuletlargepinbk"]];
        [self.popupClose setImage:[UIImage imageNamed:@"closemarkeramuletbtn"] forState:UIControlStateNormal];
        [self.popupDirections setImage:[UIImage imageNamed:@"amulettakemetherebtn"] forState:UIControlStateNormal];
    }
    else if ([pinColor isEqualToString:@"beaver"]){
        [self.popupBk setImage: [UIImage imageNamed:@"beaverlargepinbk"]];
        [self.popupClose setImage:[UIImage imageNamed:@"closemarkerbeaverbtn"] forState:UIControlStateNormal];
        [self.popupDirections setImage:[UIImage imageNamed:@"beavertakemetherebtn"] forState:UIControlStateNormal];
    }
    
    
    
    
    if (self.search.hidden==NO){
        self.search.hidden=YES;
    }
    if (self.mapLegendButton.hidden==YES){
        self.mapLegendButton.hidden=NO;
    }
    [self.view bringSubviewToFront:self.popupHolder];
    [self.popupHolder curlDown:1.0];
    self.popupHolder.hidden=NO;
    
}






@end
