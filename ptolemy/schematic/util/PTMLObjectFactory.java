/* A Factory to create useful objects from XMLElements.

 Copyright (c) 1998-1999 The Regents of the University of California.
 All rights reserved.
 Permission is hereby granted, without written agreement and without
 license or royalty fees, to use, copy, modify, and distribute this
 software and its documentation for any purpose, provided that the above
 copyright notice and the following two paragraphs appear in all copies
 of this software.

 IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY
 FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES
 ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
 THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
 SUCH DAMAGE.

 THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
 PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
 CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 ENHANCEMENTS, OR MODIFICATIONS.

                                        PT_COPYRIGHT_VERSION_2
                                        COPYRIGHTENDKEY

@ProposedRating Red (eal@eecs.berkeley.edu)
@AcceptedRating Red (johnr@eecs.berkeley.edu)
*/

package ptolemy.schematic.util;

import ptolemy.kernel.util.*;
import java.util.Enumeration;
import collections.*;
import java.io.*;
import ptolemy.schematic.xml.*;
import java.net.URL;

//////////////////////////////////////////////////////////////////////////
//// XMLObjectFactory
/**
An XMLObjectFactory supports the creation of useful objects (IconLibraries, 
DomainLibraries, etc.) from XMLElements that represent the root elements
of the correspinding PTML file.

@author Steve Neuendorffer, John Reekie
@version $Id$
*/
public class PTMLObjectFactory {

    /** 
     * Create the root IconLibrary from an XMLElement that was parsed from 
     * the root IconLibrary.  
     * @exception If the XML element does not have a type of "iconlibrary"
     */
    public static IconLibrary createIconLibrary(XMLElement e) 
            throws IllegalActionException {

        PTMLParser parser = null;
        _checkElement(e, "iconlibrary");

        IconLibrary iconlibrary = new IconLibrary();
        Enumeration children = e.childElements();
        while(children.hasMoreElements()) {
            XMLElement child = (XMLElement) children.nextElement();
            String etype = child.getElementType();
            if(etype.equals("icon")) {
                // if it's an Icon, then create it, 
                // and add it to the list of icons.
                iconlibrary.addIcon(_createIcon(child));
            } else if(etype.equals("sublibrary")) {
                // if it's a sublibrary, then add it to the 
                // list of sublibraries.

                String url = "";
                try {
                    URL baseurl = new URL(e.getXMLFileLocation());
                    String offset = child.getAttribute("url");

                    URL newurl = new URL(baseurl,offset);
                    url = newurl.toString();

                    if(parser == null) parser = new PTMLParser();
                    
                    XMLElement sublibtree = parser.parse(url);
                    IconLibrary sublib = createIconLibrary(sublibtree); 
                    iconlibrary.addSubLibrary(sublib);
                }
                catch (Exception ex) {
                    System.out.println("Couldn't parse iconlibrary from url "+
                            url);
                    System.out.println(ex.getMessage());
                }

            } else if(etype.equals("description")) {
                iconlibrary.setDescription(child.getPCData());
            } else if(etype.equals("terminalstyle")) {
            } else {
                _unknownElementType(e, "iconlibrary");
            }
        }
        Enumeration attributes = e.attributeNames();
        while(attributes.hasMoreElements()) {
            String n = (String) attributes.nextElement();
            if (n.equals("name")) {
                try {
                    iconlibrary.setName(_getString(e, n));
                } catch (Exception ex) {};
            }
        }

        return iconlibrary;
            
    }

    /** 
     * Create the root IconLibrary from an XMLElement that was parsed from 
     * the root IconLibrary.  
     * @exception If the XML element does not have a type of "iconlibrary"
     */
    /*    public static DomainLibrary createDomainLibrary(XMLElement e)
    throws IllegalActionException {
        
    }*/
   
    /**
     * Check the validity of the XML element.  This method is used to 
     * check external inputs to this class, which should not be flagged
     * as fatal errors (although the calling code may interpret them as such).
     *
     * @exception IllegalActionException If the element is null, or its
     * type is not equal to the given type.
     */
    private static void _checkElement(XMLElement e, String elementtype) 
            throws IllegalActionException {
        if(e == null) {
            throw new IllegalActionException("_checkElement: " +
                    "Received null XMLElement");
        }
        if(!e.getElementType().equals(elementtype)) {
            throw new IllegalActionException("createIconLibrary: " +
                    "Element type " + e.getElementType() + 
                    "differs from expected " + elementtype + ".");
        }
    }

    private static GraphicElement _createGraphicElement(XMLElement e)
        throws IllegalActionException {

        String name = e.getElementType();
        GraphicElement graphic = new GraphicElement(name);
        Enumeration children = e.childElements();
        while(children.hasMoreElements()) {
            XMLElement child = (XMLElement)children.nextElement();
            _unknownElementType(child, "GraphicElement");
        }
        
        Enumeration attributes = e.attributeNames();
        while(attributes.hasMoreElements()) {
            String n = (String) attributes.nextElement();
            String v = e.getAttribute(n);        
            graphic.setAttribute(n, v);
        }
        return graphic;
    }

    private static Icon _createIcon(XMLElement e)
        throws IllegalActionException {

        _verifyElement(e, "icon");

        Icon icon = new Icon();
        Enumeration children = e.childElements();
        while(children.hasMoreElements()) {
            XMLElement child = (XMLElement)children.nextElement();
            String etype = child.getElementType();
            if(etype.equals("tclscript")) {
                // if it's an Icon, then create it, 
                // and add it to the list of icons.
            } else if(etype.equals("description")) {
                icon.setDescription(child.getPCData());
            } else if(etype.equals("terminal")) {
            } else if(etype.equals("xmlgraphic")) {
                Enumeration graphics = child.childElements();
                while(graphics.hasMoreElements()) {
                    XMLElement graphic = (XMLElement)graphics.nextElement();
                    String gtype = graphic.getElementType();                
                    GraphicElement g = _createGraphicElement(graphic);
                    icon.addGraphicElement(g);
                }
            } else {
                _unknownElementType(e, "icon");
            }    
        }
        Enumeration attributes = e.attributeNames();
        while(attributes.hasMoreElements()) {
            String n = (String) attributes.nextElement();
            if (n.equals("name")) {
                try {
                    icon.setName(_getString(e, n));
                } catch (Exception ex) {};
            }
        }
        return icon;
    }

    private static Terminal _createTerminal(XMLElement e)
        throws IllegalActionException {

        _verifyElement(e, "terminal");

        Terminal terminal = new Terminal();
        Enumeration children = e.childElements();
        while(children.hasMoreElements()) {
            XMLElement child = (XMLElement)children.nextElement();
            _unknownElementType(child, "terminal");
        }

        Enumeration attributes = e.attributeNames();
        while(attributes.hasMoreElements()) {
            String n = (String) attributes.nextElement();
            if (n.equals("name")) {
                try {                    
                    terminal.setName(_getString(e, n));
                } catch (Exception ex) {};
            } else if (n.equals("input")) {
                terminal.setInput(_getBoolean(e, n));
            } else if (n.equals("output")) {
                terminal.setOutput(_getBoolean(e, n));
            } else if (n.equals("multi")) {
                terminal.setMulti(_getBoolean(e, n));
            } else if (n.equals("x")) {
                terminal.setX(_getDouble(e, n));
            } else if (n.equals("y")) {
                terminal.setY(_getDouble(e, n));
            }
        }
        return terminal;
    }

    /** Return a boolean corresponding to the value of the attribute with
     * the given name in the given XMLElement.
     * @throws IllegalActionException If the value of the XML attribute
     *  is not "true" or "false"
     */
    private static boolean _getBoolean(XMLElement e, String name) 
        throws IllegalActionException {
        String v = e.getAttribute(name);
        if(v == "true") 
            return true;
        else if(v == "false")
            return false;
        else throw new IllegalActionException(
                "Attribute " + name + " with value " + v + 
                    " does not represent a valid boolean.");
    }
   
    /** Return a double corresponding to the value of the attribute with
     * the given name in the given XMLElement.
     * @throws IllegalActionException If the value of the XML attribute
     *  does not represent a valid double
     */
    private static double _getDouble(XMLElement el, String name) 
        throws IllegalActionException {
        String v = el.getAttribute(name);
        try {
            Double d = new Double(v);
            return d.doubleValue();
        } catch (NumberFormatException e) {
            throw new IllegalActionException(
                    "Attribute " + name + " with value " + v + 
                    " does not represent a valid double.");
        }
    }

    /** Return a boolean corresponding to the value of the attribute with
     * the given name in the given XMLElement.
     */
    private static String _getString(XMLElement el, String name) {
        String v = el.getAttribute(name);
        return v;
    }

    /** 
     * Print a message about the unknown element
     */
    private static void _unknownElementType(XMLElement el, String parent) {
            String etype = el.getElementType();
                System.out.println("Unrecognized element type = " +
                    etype + " found in " + parent);
    }
        
        /**
     * Check the validity of the XML element.   This method is very similar 
     * to _checkElement, except that it is used internally to check state
     * that should already be true, unless the code is broken.
     *
     * @exception InternalErrorException If the element is null, or its
     * type is not equal to the given type.
     */
     private static void _verifyElement(XMLElement e, String elementtype) {
        if(e == null) {
            throw new InternalErrorException("_checkElement: " +
                    "Received null XMLElement");
        }
        if(!e.getElementType().equals(elementtype)) {
            throw new InternalErrorException("createIconLibrary: " +
                    "Element type " + e.getElementType() + 
                    "differs from expected " + elementtype + ".");
        }
     }

                   
}

