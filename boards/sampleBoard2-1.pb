board name=sampleBoard2_1 gravity=20.0 friction1=0.020 friction2=0.020
# This board is meant for multiple players. It is a loose 
# representation of the screenshot in the project handout, with
# some modifications.
# It is the same board as sampleBoard1, but scaled and broken 
# into two halves horizontally. This file contains the left half.


  # define a ball
  ball name=Ball x=0.5 y=0.5 xVelocity=2.5 yVelocity=2.5

  # define a series of square bumpers
  squareBumper name=Square0 x=0 y=2
  squareBumper name=Square1 x=1 y=2
  squareBumper name=Square2 x=2 y=2
  squareBumper name=Square3 x=3 y=2
  squareBumper name=Square4 x=4 y=2
  squareBumper name=Square5 x=5 y=2
  squareBumper name=Square6 x=6 y=2
  squareBumper name=Square7 x=7 y=2
  squareBumper name=Square8 x=8 y=2
  squareBumper name=Square9 x=9 y=2
  squareBumper name=Square10 x=10 y=2
  squareBumper name=Square11 x=11 y=2
  squareBumper name=Square12 x=12 y=2
  squareBumper name=Square13 x=13 y=2
  squareBumper name=Square14 x=14 y=2
  squareBumper name=Square15 x=15 y=2
  
  # define a series of circle bumpers
  circleBumper name=Circle10 x=10 y=3
  circleBumper name=Circle11 x=11 y=4
  circleBumper name=Circle12 x=12 y=5
  circleBumper name=Circle13 x=13 y=6
  circleBumper name=Circle14 x=14 y=7
  circleBumper name=Circle15 x=15 y=8
  
  # define some triangular bumpers
  triangleBumper name=Tri1 x=17 y=11 orientation=270
  triangleBumper name=Tri2 x=18 y=12 orientation=270
  
  # add some flippers
  leftFlipper name=FlipL1 x=16 y=2 orientation=0
  leftFlipper name=FlipL2 x=16 y=9 orientation=0

  # define an absorber to catch the ball at the bottom
  absorber name=Abs x=0 y=19 width=20 height=1 

  # make the absorber self-triggering
  fire trigger=Abs action=Abs 

