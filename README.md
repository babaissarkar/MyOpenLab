# MyOpenLab
Java based Software for visual block-based programming. In particular, MyOpenLab seems to be widely used to develop control programs that can run on Raspberry Pi and Arduino boards.
MyOpenLab has an architecture that is not oriented around flow programming; it is what's called event driven. In event-driven programs, events determine the flow of the program. 
Typical events include mouse clicks, key presses, and messages from other programs. Perhaps for obvious reasons, graphical user interfaces typically are built around event-driven architectures.

# Important Links
MyOpenLab website : [MyOpenLab](https://myopenlab.org)

MyLibreLab (fork of MyOpenLab, after the original was discontinued) : [MyLibreLab](https://github.com/MyLibreLab/MyLibreLab)

MyLibreLab docs (also useful here) : [MyLibreLab Docs](https://mylibrelab.github.io/user-documentation/)

# Objective
Unfortunately both MyOpenLab and MyLibreLab projects got discontinued. However, the orignal software from [here](https://myopenlab.org) is still usable, but requires Java 8. This repository tries to provide a version of MyOpenLab that runs with the latest java (for now, this is Java 17). Also, I have added a couple of changes, which are described in the Changelog.

# Building
Dependencies :
Rhino and Rhino JSR-223 javascript engine
JSON libarary
JSSC
JAXB libarary
Old java swing GroupLayout jar.

Dependencies are already included in distribution/myopenlab_lib.

To build you will need Java JDK and Apache ANT installed. Run `ant` in this directory. The output will be the `myopenlab.jar` java archive in the `distribution` directory.

# License
Software is available under GNU GPL v3 license. See [here](https://www.gnu.org/licenses/gpl-3.0.html).

