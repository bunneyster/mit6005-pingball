board name=buildTest gravity=9.8 friction1=0.5 friction2=0.1
# This is a test for the builder. The board is quite boring.

ball name=Ball x=10 y=5 xVelocity=0 yVelocity=-10
absorber name=Absorber x=3 y=16 width=5 height=2
squareBumper name=Square x=3 y=12
circleBumper name=Circle x=5 y=12
triangleBumper name=Triangle x=7 y=12 orientation=90
rightFlipper name=FlipperR x=5 y=5 orientation=180
portal name=Portal x=12 y=12 otherPortal=MissingPortal
fire trigger=Square action=Absorber
fire trigger=Circle action=Square
keydown key=space action=Square
keyup key=up action=Circle
style class=green color=00ff00 texture=plant.png