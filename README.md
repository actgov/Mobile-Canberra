# Mobile Canberra
<hr />

Welcome to Mobile Canberra, an initiative by the [ACT Government](http://www.cmd.act.gov.au/open_government) and the [NICTA eGOV Cluster](http://www.egovernmentcluster.org.au/), and developed by the [Imagine Team](http://imagineteamsolutions.com). The app is [available on iOS and Android](http://imagineteamsolutions.com/app/mobile-canberra-phase-2/).

![SVProgressHUD-iOS7](http://imagineteamsolutions.com/imagineteam/wp-content/uploads/2013/12/mobilecanberrascreen.png)


Mobile Canberra is a powerful platform for showing points of interest and services around Canberra. 

The app provides access to geolocational Government datasets and services. Current services include bus stops, 
public toilets, playgrounds, libraries, TAFE campuses, schools, public art, and public furniture. Additional services will be loaded onto the app as they become available. 

The services are available from [http://www.data.act.gov.au/](http://www.data.act.gov.au/)

<br>
  
<hr />
##Android Installation
<br>
###After importing as an Android Project into Eclipse, add the following third-party libraries:
<br>
  - [Android-async-http](https://github.com/loopj/android-async-http)
  - [AndroidSideMenu](https://github.com/dmitry-zaitsev/AndroidSideMenu)
  - [Google Play Services](http://developer.android.com/google/play-services/setup.html)
  - [Android Support Library](http://developer.android.com/tools/support-library/index.html)
  - [Clusterkraf](https://github.com/twotoasters/clusterkraf)

<br>

###Usage
####There are two main activities:
- DatasetsActivity: Shows list of ACT services available
- MapActivity: Draws points of services on a map

<br>
Simple!

<hr />
##iPhone Installation
<br>
###Add the following frameworks to your project:
  - MapKit
  - libs.dylib
  - CoreGraphics
  - MobileCoreServices
  - SystemConfiguration
  - CFNetwork
  - Accelerate
  - QuartzCore
  - OpenGLES
  - libcucore.dylib
  - libc++.dylib
  - ImageIO
  - GLKit
  - CoreText
  - CoreLocation
  - CoreData
  - AVFoundation
  - CoreMotion
  - UIKit
  - Foundation
 
###Mobile Canberra iOS uses the following third-party libraries:
  - [MYBlurIntroductionView](https://github.com/MatthewYork/MYBlurIntroductionView)
  - [iCarousel](https://github.com/nicklockwood/iCarousel)
  - [KingPin](https://github.com/itsbonczek/kingpin)
  - [VPPMap](https://github.com/vicpenap/VPPMap)
  - [ASIHTTPRequest](http://allseeing-i.com/ASIHTTPRequest/)
  - [SVProgressHUD](https://github.com/samvermette/SVProgressHUD)
  - [RESideMenu](https://github.com/romaonthego/RESideMenu)
  - [ACParallax](https://github.com/acoomans/Parallax)
  - [CoolUIViewAnimations](https://github.com/pdetagyos/CoolUIViewAnimations)

<br>

###Usage
####There are three main view controllers:
- ParallaxViewController: Handles the parallax effect after the splash screen
- ListViewController: Shows list of ACT services available
- MapViewController: Draws points of services on a map

<br>
Simple!

<br>

<hr />
License
----
MIT
