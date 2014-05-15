MIT 6.005 Project: Pingball
---------------------------

This is an implementation of the Pingball project assignment given in MIT's
6.005 software engineering course, in the Spring 2014 semester.


Try it out
----------

To see Phase 1 in action, start up the server first.

CLASSPATH=bin:physics.jar java pb.cli.PingballServer --port 10987

Type this in the console to connect the boards.
h sampleBoard2_1 sampleBoard2_2

Then connect the clients (in two tabs side-by-side). 

# Connect the right tab first.
CLASSPATH=bin:physics.jar java pb.cli.PingballClient --host localhost \
    --port 10987 boards/sampleBoard2-2.pb
# Then connect the left tab.
CLASSPATH=bin:physics.jar java pb.cli.PingballClient --host localhost \
    --port 10987 boards/sampleBoard2-1.pb


To see portals in action, leave the server up, and start up two clients.

CLASSPATH=bin:physics.jar java pb.cli.PingballClient --host localhost \
    --port 10987 boards/portalBoard-1.pb
CLASSPATH=bin:physics.jar java pb.cli.PingballClient --host localhost \
    --port 10987 boards/portalBoard-2.pb
    
Phase 2 custom features are demonstrated by the following (large) board.

CLASSPATH=bin:physics.jar java pb.cli.PingballClient boards/large.pb

sampleBoard3 is the most interesting staff-provided single-player board. 

CLASSPATH=bin:physics.jar java pb.cli.PingballClient boards/sampleBoard3.pb


Package Structure
-----------------

Client-side code:
* pb.board - general concepts used by the client-side model (Board)
* pb.gizmos - the various board elements (ball and gadgets); a reader that
              understands pb.board and pb.gizmos.Ball should be able to
              read any other class on its own
* pb.renderer - draws the board on the client UI
* pb.parse - hand-rolled parser for .pb files
* pb.client - client-side logic
* pb.testing - common functionality shared by unit tests

Shared code:
* pb.proto - the network protocol used between the client and the server 
* pb.net - low-level networking abstractions used by the client and the server
* pb.cli - the command-line tools (PingballClient and PingballServer)

Server-side code:
* pb.server - server-side logic

Unused code:
* pb.warmup - our warm-up code was here


Overview
--------

The code aims to follow SRP (the Single Responsibility Principle), and has many
loosely-coupled classes. Most of the classes are not thread-safe, but make it
easy to understand what threads their instances should be contained to. Threaded
code is restricted to a couple of classes (pb.client.ClientController and
pb.server.Dispatch) and mostly consists of starting up threads. The threads use
BlockingQueue instances to communicate with each other, and every object that
goes down a queue is thread-safe by being immutable. Classes are loosely coupled
and layered so they can be tested independently, and most classes have
corresponding Test files.

The JavaDocs for the following classes give a good tour of the codebase. The
order below is the suggested reading order.

1. pb.board.Board
2. pb.board.Gizmo
3. pb.board.SolidGizmo
4. pb.render.RenderManager
5. pb.render.Renderer
6. pb.proto.Message
7. pb.net.SocketPusher
8. pb.net.SocketFetcher
9. pb.server.Dispatcher
10. pb.client.ClientController


Copyright
---------

The source code in third_party/physics is Copyright (C) 1999-2014 by the
Massachusetts Institute of Technology and released under the MIT license
embedded in the .java files. 

The source code and test data in this project is Copyright (C) 2014 by Victor
Costan and Staphany Park, and released under the MIT license in the LICENSE.txt
file.

