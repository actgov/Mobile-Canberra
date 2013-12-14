//
//  DataSetListElement.h
//  Mobile Canberra
//
//  Created by Zaki Bouguettaya on 25/09/13.
//  Copyright (c) 2013 Imagine Team. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface DataSetListElement : NSObject

@property (strong, nonatomic) NSString *name;
@property (strong, nonatomic) NSString *color;
@property (strong, nonatomic) NSString *datasetId;

- (void)initWithName:(NSString *)name andColor:(NSString *) color andId:(NSString *) datasetId ;

@end
