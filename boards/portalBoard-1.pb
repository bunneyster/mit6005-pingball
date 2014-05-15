board name=portals1 gravity=0 friction1=0 friction2=0

# This is one side of a portals test.

ball name=localBall x=2 y=10 xVelocity=1 yVelocity=0
portal name=localPortal1 x=5 y=10 otherPortal=localPortal2
portal name=localPortal2 x=15 y=10 otherPortal=localPortal1

ball name=remoteBall x=10 y=1 xVelocity=0 yVelocity=1
portal name=remote1 x=10 y=5 otherBoard=portals2 otherPortal=remote2
portal name=remote2 x=10 y=15 otherBoard=portals2 otherPortal=remote1
