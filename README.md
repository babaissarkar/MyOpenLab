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

# Licensing and Contributors
Software is available under GNU GPL v3 license. See [here](https://www.gnu.org/licenses/gpl-3.0.html) or in the `license.txt` file included.  

## Original Contributors
MyOpenLab Project Initiator: Carmelo Salafia  
Email: cswi@gmx.de  
http://www.myopenlab.de

MyOpenLab Development, documentation and examples:  
Robinson Javier Velásquez.  
Electronics and Telecomunications Engineer  
Catholic University of Colombia  
Email: javiervelasquez125@gmail.com  
Bogotá D,C; - Colombia.  
http://myopenlab.org  

Spanisch Übersetzung, Elemente und Element Docs: Prof. José Manuel Ruiz Gutiérrez  
eMail : j.m.r.gutierrez@gmail.com  
Tomelloso (Ciudad Real) ESPAÑA.  

Thanks to:  
Universidad Católica de Colombia  
Catholic University of Colombia  
https://www.ucatolica.edu.co/portal/  

## New contributions
Source code have been imported from (https://github.com/sPyOpenSource/myopenlab) on 20.05.2023.  

Please see the Info dialog (Help -> Info) inside this software for detailed information on contributors and licensing.  

Any further changes by me (babaissarkar) in this repository are © 2023 Subhraman Sarkar and are available under the same license (GNU GPL v3). Changed are documented in the CHANGELOG file.

# Building
**Dependencies :**
* [Rhino and Rhino JSR-223 javascript engine](https://github.com/mozilla/rhino)
* [JSON library](https://github.com/stleary/JSON-java)
* [JSSC Serial Port library](https://github.com/java-native/jssc)
* [Java XML Binding library](https://mvnrepository.com/artifact/javax.xml.bind/jaxb-api)
* Old Java Swing GroupLayout library (included)
  (needed as not all code has not been moved to new Java Swing Group Layout, which is included with java.)

Dependencies are already included in `distribution/myopenlab_lib`.

To build you will need Java JDK and Apache ANT installed. Run `ant` in this directory. The output will be the `myopenlab.jar` java archive in the `distribution` directory. A prebuilt copy is included in the `distribution` folder.

# AppImage
A preliminary AppImage of MyOpenLab is now available in Releases section. The AppImage contains the program + dependency libraries + a subset of AdoptOpenJDK Temurin Java 17. The AppImage needs AppImageTool, and can be built by : `appimagetool.AppImage MyOpenLab.AppDir MyOpenLab.AppImage`.

# Running
You will need all the files in the `distribution` directory. Clone this repository or download the Zip from the Code button at top right. Then navigate to the distribution folder and run the `start-` file corresponding to your OS. You will need Java JRE installed (at least version 15). (Tested on Linux so far).
An AppImage is now available, and can be run easily by making the file executable (`chmod +x MyOpenLab.AppImage`) and running it (`./MyOpenLab.AppImage`).


