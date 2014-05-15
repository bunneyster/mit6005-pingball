board name=sampleBoard2_2 gravity=20.0 friction1=0.020 friction2=0.020
# This board is meant for multiple players. It is a loose 
# representation of the screenshot in the project handout, with
# some modifications.
# It is the same board as sampleBoard1, but scaled and broken 
# into two halves horizontally. This file contains the right half.


  # the ball is defined in sampleBoard2-1.txt

  # define a series of square bumpers
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
  squareBumper name=Square16 x=16 y=2
  squareBumper name=Square17 x=17 y=2
  squareBumper name=Square18 x=18 y=2
  squareBumper name=Square19 x=19 y=2
  
  # define a series of circle bumpers
  circleBumper name=Circle4 x=4 y=8
  circleBumper name=Circle5 x=5 y=7
  circleBumper name=Circle6 x=6 y=6
  circleBumper name=Circle7 x=7 y=5
  circleBumper name=Circle8 x=8 y=4
  circleBumper name=Circle9 x=9 y=3
  
  # define some triangular bumpers
  triangleBumper name=Tri1 x=1 y=12 orientation=180
  triangleBumper name=Tri2 x=2 y=11 orientation=180
  
  # add some flippers
  rightFlipper name=FlipR1 x=2 y=2 orientation=0
  rightFlipper name=FlipR2 x=2 y=9 orientation=0

  # define an absorber to catch the ball at the bottom
  absorber name=Abs x=0 y=19 width=20 height=1 

  # make the absorber self-triggering
  fire trigger=Abs action=Abs 

