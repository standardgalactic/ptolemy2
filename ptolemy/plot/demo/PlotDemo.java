/* Demo for a signal plotter.

@Author: Edward A. Lee
@Version: $Id$

@Copyright (c) 1997 The Regents of the University of California.
All rights reserved.

Permission is hereby granted, without written agreement and without
license or royalty fees, to use, copy, modify, and distribute this
software and its documentation for any purpose, provided that the
above copyright notice and the following two paragraphs appear in all
copies of this software.

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
*/
/* Confuses metrowerks java: package plot; */

import java.awt.*;
import java.applet.Applet;

//////////////////////////////////////////////////////////////////////////
//// PlotDemo
/** 
 * Plot a variety of test signals.
 */
public class PlotDemo extends Plot {

    //////////////////////////////////////////////////////////////////////////
    ////                         public methods                           ////
   
   /* FIXME: To make into an application?
	public static void main(String args[]) {
		Applet applet = new Plot();
		Frame frame = new PlotFrame("Plot",applet,500,400);
	} 
	*/
	
    /**
     * Return a string describing this applet.
     */
    public String getAppletInfo() {
        return "PlotDemo 1.0: Demo of Plot. By: Edward A. Lee, eal@eecs.berkeley.edu";
    }

    /**
     * Initialize the applet.  Here we step through an example of what the
     * the applet can do.
     */
    public void init () {
        super.init();
        
        setTitle("Simple Line Plot Demo");
        setYRange(-4,4);
        setXRange(0,100);
        setXLabel("time");
        setYLabel("value");
        addYTick("-PI", -Math.PI);
        addYTick("-PI/2", -Math.PI/2);
        addYTick("0",0);
        addYTick("PI/2", Math.PI/2);
        addYTick("PI", Math.PI);
        setNumSets(10);
        setMarksStyle("none");
        
        boolean first = true;
        for (int i=0; i <= 100; i++) {
            addPoint(0,(double)i,5 * Math.cos(Math.PI * i/20), !first);
            addPoint(1,(double)i, 4.5 * Math.cos(Math.PI * i/25), !first);
            addPoint(2,(double)i, 4 * Math.cos(Math.PI * i/30), !first);
            addPoint(3,(double)i, 3.5* Math.cos(Math.PI * i/35), !first);
            addPoint(4,(double)i, 3 * Math.cos(Math.PI * i/40), !first);
            addPoint(5,(double)i, 2.5 * Math.cos(Math.PI * i/45), !first);
            addPoint(6,(double)i, 2 * Math.cos(Math.PI * i/50), !first);
            addPoint(7,(double)i, 1.5 * Math.cos(Math.PI * i/55), !first);
            addPoint(8,(double)i, 1 * Math.cos(Math.PI * i/60), !first);
            addPoint(9,(double)i, 0.5 * Math.cos(Math.PI * i/65), !first);
            first = false;
        }
    }
}
