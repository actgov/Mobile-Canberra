//
//  DataSetListElement.m
//  Mobile Canberra
//
//  Created by Zaki Bouguettaya on 25/09/13.
//  Copyright (c) 2013 Imagine Team. All rights reserved.
//

#import "DataSetListElement.h"

@implementation DataSetListElement

- (id)init
{
    self = [super init];
    if (self) {
       
    }
    return self;
}

- (void)initWithName:(NSString *)name andColor:(NSString *) color andId:(NSString *) datasetId
{
    self.name = name;
    self.color = color;
    self.datasetId = datasetId;
}


@end
