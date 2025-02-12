/*
MyOpenLab by Carmelo Salafia www.myopenlab.de
Copyright (C) 2004  Carmelo Salafia cswi@gmx.de

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package VisualLogic;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Loader 
{    
    URLClassLoader cl;

  public Object ladeClasse(String elementPath, String pfad, String klassename ) throws Exception
  {
    URL url;
    Object o = null;
    
    try
    {        
        url = new File(pfad).toURI().toURL(); 
        
        URL url2 = new File(elementPath).toURI().toURL(); 

        cl = new URLClassLoader( new URL[]{ url, url2 }, Thread.currentThread().getContextClassLoader());
                
        Class<?> c = cl.loadClass(klassename );
        o = c.getDeclaredConstructor().newInstance();    
    } 
    catch (Exception | UnsupportedClassVersionError ex)
    {
        Logger.getLogger(Loader.class.getName()).log(Level.SEVERE, null, ex);     
    }
    return o;
  }
 
  public Object ladeClasseDriver(URL[] urls, String klassename)
  {    
    Object o = null;
    try (URLClassLoader cl = new URLClassLoader(urls, Thread.currentThread().getContextClassLoader())) {
    	
			Class<?> c = cl.loadClass( klassename );
			o = c.getDeclaredConstructor().newInstance();  
    } 
    catch (Exception | UnsupportedClassVersionError ex)
    {
        //Logger.getLogger(Loader.class.getName()).log(Level.SEVERE, null, ex);
        ex.printStackTrace();
    }
    return o;
  }

}
