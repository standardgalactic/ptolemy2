/* A labeled box for signal plots.

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
/* FIXME: Confuses metrowerks java: package plot; */

import java.awt.*;
import java.applet.Applet;
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;

//////////////////////////////////////////////////////////////////////////
//// PlotBox
/** 
 * Construct a labeled box within which to place a data plot.  A title,
 * X and Y axis labels, tick marks, and a legend are all supported.
 * The box can be configured either through a file with commands or
 * through direct invocation of the public methods of the class.
 * If a file is used, the file can be given as a URL through the
 * applet parameter called "dataurl". The file contains any number
 * commands, one per line.  Unrecognized commands and commands with
 * syntax errors are ignored.  Comments are denoted by a line starting
 * with a pound sign "#".  The recognized commands include:
 * <pre>
 * TitleText: <i>string</i>
 * XLabel: <i>string</i>
 * YLabel: <i>string</i>
 * </pre>
 * These commands provide a title and labels for the X (horizontal) and Y
 * (vertical) axes.
 * A <i>string</i> is simply a sequence of characters, possibly including spaces.
 * There is no need here to surround them with quotation marks, and in fact,
 * if you do, the quotation marks will be included in the labels.
 * <p>
 * The ranges of the X and Y axes can be optionally given by commands like:
 * <pre>
 * XRange: <i>min</i>, <i>max</i>
 * YRange: <i>min</i>, <i>max</i>
 * </pre>
 * The arguments <i>min</i> and <i>max</i> are numbers, possibly including a sign
 * and a decimal point. If they are not specified, then the ranges are computed
 * automatically from the data.
 * <p>
 * The tick marks for the axes are usually computed automatically from the ranges.
 * Every attempt is made to choose reasonable positions for the tick marks regardless
 * of the data ranges (i.e. powers of ten multiplied by 1, 2, or 5 are used).
 * However, they can also be specified explicitly using commands like:
 * <pre>
 * XTicks: <i>label position, label position, ...</i>
 * YTicks: <i>label position, label position, ...</i>
 * </pre>
 * A <i>label</i> is a string that must be surrounded by quotation marks if it
 * contains any spaces.  A <i>position</i> is a number giving the location of
 * the tick mark along the axis.  For example, a horizontal axis for a frequency
 * domain plot might have tick marks as follows:
 * <pre>
 * XTicks: -PI -3.14159, -PI/2 -1.570795, 0 0, PI/2 1.570795, PI 3.14159
 * </pre>
 * Tick marks could also denote years, months, days of the week, etc.
 * <p>
 * By default, tick marks are connected by a light grey background grid.
 * This grid can be turned off with the following command:
 * <pre>
 * Grid: off
 * </pre>
 * Also, by default, the first ten data sets are shown each in a unique color.
 * The use of color can be turned off with the command:
 * <pre>
 * Color: off
 * </pre>
 * All of the above commands can also be invoked directly by calling the
 * the corresponding public methods from some Java procedure.
 *
 * @author Edward A. Lee
 * @version $Id$
 */
public class PlotBox extends Applet {

    //////////////////////////////////////////////////////////////////////////
    ////                         public methods                           ////
    
    /**
     * Add a legend (displayed at the upper right) for the specified data set
     * (an arbitrary number understood by <code>drawPoint</code>) with the specified string.
     * Short strings generally fit better than long strings.
     */
    public void addLegend(int dataset, String legend) {
        _legendStrings.addElement(legend);
        _legendDatasets.addElement(new Integer(dataset));
    }
    
    /**
     * Specify a tick mark for the X axis.  The label given is placed on the axis
     * at the position given by <i>position</i>. If this is called once or more,
     * automatic generation of tick marks is disabled.  The tick mark will appear
     * only if it is within the X range.
     */
    public void addXTick (String label, double position) {
        if (_xticks == null) {
            _xticks = new Vector();
            _xticklabels = new Vector();
        }
       	_xticks.addElement(new Double(position));
        _xticklabels.addElement(label);
    }
    
    /**
     * Specify a tick mark for the Y axis.  The label given is placed on the axis
     * at the position given by <i>position</i>. If this is called once or more,
     * automatic generation of tick marks is disabled.  The tick mark will appear
     * only if it is within the Y range.
     */
    public void addYTick (String label, double position) {
       	if (_yticks == null) {
            _yticks = new Vector();
     	    _yticklabels = new Vector();
        }
       	_yticks.addElement(new Double(position));
        _yticklabels.addElement(label);
    }
    
    /**
     * Return a string describing this applet.
     */
    public String getAppletInfo() {
        return "PlotBox 1.0: Base class for plots. By: Edward A. Lee, eal@eecs.berkeley.edu";
    }
       
    /**
     * Return information about parameters.
     */
    public String[][] getParameterInfo () {
        String pinfo[][] = {
            {"dataurl",   "url",     "the URL of the data to plot"}
        };
	    return pinfo;
    }
    
    /**
     * Initialize the applet.  If a dataurl parameter has been specified,
     * read the file given by the URL and parse the commands in it.
     */
    public void init() {
        super.init();
		
        _labelfont = new Font("Helvetica", Font.PLAIN, 12);
        _superscriptfont = new Font("Helvetica", Font.PLAIN, 9);
        _titlefont = new Font("Helvetica", Font.BOLD, 14);
        
        _legendStrings = new Vector();
        _legendDatasets = new Vector();
        
        _xticks = null;
        _xticklabels = null;
        _yticks = null;
        _yticklabels = null;

        graphics = this.getGraphics();

        // Check to see whether a data URL has been given.
        // Need the catch here because applets used as components have no parameters.
        String dataurl = null;
        try {
            dataurl = getParameter("dataurl");
        } catch (NullPointerException e) {}
        if (dataurl != null) {
           try {
               URL url = new URL(getDocumentBase(), dataurl);
               URLConnection connection = url.openConnection();
               DataInputStream in = new DataInputStream(connection.getInputStream());
               String line = in.readLine();
               while (line != null) {
                   parseLine(line);
                   line = in.readLine();
               }
           }
           catch (MalformedURLException e) {
               _errorMsg = new String[2];
               _errorMsg[0] = "malformed URL: " + dataurl;
               _errorMsg[1] = e.getMessage();
           }
           catch (IOException e) {
               _errorMsg = new String[2];
               _errorMsg[0] = "Failure reading data: " + dataurl;
               _errorMsg[1] = e.getMessage();
           }
        }
	}
	
	/**
	 * Paint the applet contents, which in this base class is only the axes.
	 */
	public void paint(Graphics g) {
	    drawAxes(true);
    }
    
    /**
     * Set the label for the X (horizontal) axis.  The label will appear on the subsequent
     * call to <code>paint()</code> or <code>drawAxes()</code>.
     */
    public void setXLabel (String label) {
        this._xlabel = label;
    }

    /**
     * Set the label for the Y (vertical) axis.  The label will appear on the subsequent
     * call to <code>paint()</code> or <code>drawAxes()</code>.
     */
    public void setYLabel (String label) {
        this._ylabel = label;
    }

    /**
     * Set the title of the graph.  The title will appear on the subsequent
     * call to <code>paint()</code> or <code>drawAxes()</code>.
     */
    public void setTitle (String title) {
        this._title = title;
    }
    
    /**
     * Set the X (horizontal) range of the plot.  If this is not done explicitly,
     * then the range is computed automatically from data available when <code>paint()</code>
     * or <code>drawAxes()</code> are called.
     * If min and max are identical, then the range is arbitrarily spread by 1.
     */
    public void setXRange (double min, double max) {
        _setXRange(min,max);
        xRangeGiven = true;
    }

    /**
     * Set the Y (vertical) range of the plot.  If this is not done explicitly,
     * then the range is computed automatically from data available when <code>paint()</code>
     * or <code>drawAxes()</code> are called.
     * If min and max are identical, then the range is arbitrarily spread by 0.1.
     */
    public void setYRange (double min, double max) {
        _setYRange(min,max);
        yRangeGiven = true;
    }

    //////////////////////////////////////////////////////////////////////////
    ////                         protected methods                        ////

	/**
	 * Clear the current display and draw the axes using the current range, label,
	 * and title information.  If the argument is true, clear the display before
	 * redrawing.
	 */
	protected void drawAxes(boolean clearfirst) {
	    if (graphics == null) {
	        System.out.println("Attempt to draw axes without a Graphics object.");
	        return;
	    }
	    
	    // For use by all text displays below.
	    // FIXME - consolidate for efficiency.
        graphics.setFont(_titlefont);
        FontMetrics tfm = graphics.getFontMetrics();
        graphics.setFont(_superscriptfont);
        FontMetrics sfm = graphics.getFontMetrics();
        graphics.setFont(_labelfont);
        FontMetrics lfm = graphics.getFontMetrics();

	    // If an error message has been set, display it and return.
        if (_errorMsg != null) {
            int fheight = lfm.getHeight() + 2;
            int msgy = fheight;
            graphics.setColor(Color.black);
            for(int i=0; i<_errorMsg.length;i++) {
                graphics.drawString(_errorMsg[i],10, msgy);
                msgy += fheight;
            }
            return;
         }

         // Find the width and height of the total drawing area, and clear it.
         Rectangle drawRect = bounds();
         if (clearfirst) {
             graphics.clearRect(drawRect.x, drawRect.y, drawRect.width, drawRect.height);
         }
        
         // Make sure we have an x and y range
         if (!xRangeGiven) {
             if (xBottom > xTop) {
                 // have nothing to go on.
                 _setXRange(0,0);
             } else {
                 _setXRange(xBottom, xTop);
             }
         }
         if (!yRangeGiven) {
             if (yBottom > yTop) {
                 // have nothing to go on.
                 _setYRange(0,0);
             } else {
                 _setYRange(yBottom, yTop);
             }
         }
         
         // Vertical space for title, if appropriate.
         // NOTE: We assume a one-line title.
         int titley = 0;
         int titlefontheight = tfm.getHeight();
         if (_title != null || yExp != 0) {
             titley = titlefontheight + topPadding;
         }
        
        // Number of vertical tick marks depends on the height of the font
        // for labeling ticks and the height of the window.
        graphics.setFont(_labelfont);
        int labelheight = lfm.getHeight();
        int halflabelheight = labelheight/2;

        // Draw scaling annotation for x axis.
        int ySPos = drawRect.y + drawRect.height - 5; // NOTE: 5 pixel padding on bottom.
        if (xExp != 0 && _xticks == null) {
            int xSPos = drawRect.x + drawRect.width - rightPadding;
            String superscript = Integer.toString(xExp);
            xSPos -= sfm.stringWidth(superscript);
            graphics.setFont(_superscriptfont);
            graphics.drawString(superscript, xSPos, ySPos - halflabelheight);
            xSPos -= lfm.stringWidth("x10");
            graphics.setFont(_labelfont);
            graphics.drawString("x10", xSPos, ySPos);
            bottomPadding = (3 * labelheight)/2 + 5;  // NOTE: 5 pixel padding on bottom
        }
        
        // NOTE: 5 pixel padding on the bottom.
        if (_xlabel != null && bottomPadding < labelheight + 5) {
            bottomPadding = titlefontheight + 5;
        }
        
        // Compute the space needed around the plot, starting with vertical.
        uly = drawRect.y + titley + 5;       // NOTE: padding of 5 pixels below title.
        // NOTE: 3 pixels above bottom labels.
        lry = drawRect.height-labelheight-bottomPadding-3; 
        int height = lry-uly;
        yscale = height/(yMax - yMin);

        ///////////////////// vertical axis

        // Number of y tick marks.
        int ny = 2 + height/(labelheight+10); // NOTE: subjective spacing factor.
        // Compute y increment.
        double yStep=_roundUp((yMax-yMin)/(double)ny);
        
        // Compute y starting point so it is a multiple of yStep.
        double yStart=yStep*Math.ceil(yMin/yStep);
        
        // NOTE: Following disables first tick.  Not a good idea?
        // if (yStart == yMin) yStart+=yStep;
        
        // Define the strings that will label the y axis.
        // Meanwhile, find the width of the widest label.
        // The labels are quantized so that they don't have excess resolution.
        int widesty = 0;
        // These do not get used unless ticks are automatic, but the compiler is
        // not smart enough to allow us to reference them in two distinct conditional
        // clauses unless they are allocated outside the clauses.
        String ylabels[] = new String[ny];
        int ylabwidth[] = new int[ny];
        int ind = 0;
        if (_yticks == null) {
            // automatic ticks
            for (double ypos=yStart; ypos <= yMax; ypos += yStep) {
                // Prevent out of bounds exceptions
                if (ind >= ny) break;
                String yl = Double.toString(Math.floor(ypos*1000.0+0.5)*0.001);
                ylabels[ind] = yl;
                int lw = lfm.stringWidth(yl);
                ylabwidth[ind++] = lw;
                if (lw > widesty) {widesty = lw;}
            }
        } else {
            // explictly specified ticks
            Enumeration nl = _yticklabels.elements();
            while (nl.hasMoreElements()) {
                String label = (String) nl.nextElement();
                int lw = lfm.stringWidth(label);
                if (lw > widesty) {widesty = lw;}
            }            
        }

        // Next we do the horizontal spacing.
        if (_ylabel != null) {
            ulx = drawRect.x + widesty + lfm.stringWidth("W") + leftPadding;
        } else {     
            ulx = drawRect.x + widesty + leftPadding;
        }
        int legendwidth = drawLegend(drawRect.width-rightPadding, uly);
        lrx = drawRect.width-legendwidth-rightPadding;
        int width = lrx-ulx;
        xscale = width/(xMax - xMin);
        
        // White background for the plotting rectangle
        graphics.setColor(Color.white);
        graphics.fillRect(ulx,uly,width,height);

        graphics.setColor(Color.black);
        graphics.drawRect(ulx,uly,width,height);
        
        // NOTE: subjective tick length.
        int tickLength = 5;
        int xCoord1 = ulx+tickLength;
        int xCoord2 = lrx-tickLength;
        
        if (_yticks == null) {
            // auto-ticks
            ind = 0;
            for (double ypos=yStart; ypos <= yMax; ypos += yStep) {
                // Prevent out of bounds exceptions
                if (ind >= ny) break;
                int yCoord1 = lry - (int)((ypos-yMin)*yscale);
                // The lowest label is shifted up slightly to avoid colliding with x labels.
                int offset = 0;
                if (ind > 0) offset = halflabelheight;
                graphics.drawLine(ulx,yCoord1,xCoord1,yCoord1);
                graphics.drawLine(lrx,yCoord1,xCoord2,yCoord1);
                if (grid && yCoord1 != uly && yCoord1 != lry) {
                    graphics.setColor(Color.lightGray);
                    graphics.drawLine(xCoord1,yCoord1,xCoord2,yCoord1);
                    graphics.setColor(Color.black);
                }
                // NOTE: 3 pixel spacing between axis and labels.
                graphics.drawString(ylabels[ind], ulx-ylabwidth[ind++]-3, yCoord1+offset);
            }
        
            // Draw scaling annotation for y axis.
            if (yExp != 0) {
                graphics.drawString("x10", 2, titley);
                graphics.setFont(_superscriptfont);
                graphics.drawString(Integer.toString(yExp), lfm.stringWidth("x10") + 2, 
                       titley-halflabelheight);
                graphics.setFont(_labelfont);
            }
        } else {
            // ticks have been explicitly specified
            Enumeration nt = _yticks.elements();
            Enumeration nl = _yticklabels.elements();
            while (nl.hasMoreElements()) {
                String label = (String) nl.nextElement();
                double ypos = ((Double)(nt.nextElement())).doubleValue();
                int yCoord1 = lry - (int)((ypos-yMin)*yscale);
                int offset = 0;
                if (ypos < lry - labelheight) offset = halflabelheight;
                graphics.drawLine(ulx,yCoord1,xCoord1,yCoord1);
                graphics.drawLine(lrx,yCoord1,xCoord2,yCoord1);
                if (grid && yCoord1 != uly && yCoord1 != lry) {
                    graphics.setColor(Color.lightGray);
                    graphics.drawLine(xCoord1,yCoord1,xCoord2,yCoord1);
                    graphics.setColor(Color.black);
                }
                // NOTE: 3 pixel spacing between axis and labels.
                graphics.drawString(label, ulx - lfm.stringWidth(label) - 3, yCoord1+offset);
            }
        }
        
        ///////////////////// horizontal axis

        int yCoord1 = uly+tickLength;
        int yCoord2 = lry-tickLength;
        if (_xticks == null) {
            // auto-ticks
            // Number of x tick marks.
            // Assume a worst case of 4 characters and a period for each label.
            int maxlabelwidth = lfm.stringWidth("8.888");
        
            int nx = 2 + width/(maxlabelwidth+5); // NOTE: 5 additional pixels between labels.
            // Compute x increment.
            double xStep=_roundUp((xMax-xMin)/(double)nx);
        
            // Compute x starting point so it is a multiple of xStep.
            double xStart=xStep*Math.ceil(xMin/xStep);
        
            // NOTE: Following disables first tick.  Not a good idea?
            // if (xStart == xMin) xStart+=xStep;
        
            // Label the x axis.
            // The labels are quantized so that they don't have excess resolution.
            for (double xpos=xStart; xpos <= xMax; xpos += xStep) {
                String _xlabel = Double.toString(Math.floor(xpos*1000.0+0.5)*0.001);
                xCoord1 = ulx + (int)((xpos-xMin)*xscale);
                graphics.drawLine(xCoord1,uly,xCoord1,yCoord1);
                graphics.drawLine(xCoord1,lry,xCoord1,yCoord2);
                if (grid && xCoord1 != ulx && xCoord1 != lrx) {
                    graphics.setColor(Color.lightGray);
                    graphics.drawLine(xCoord1,yCoord1,xCoord1,yCoord2);
                    graphics.setColor(Color.black);
                }
                int labxpos = xCoord1 - lfm.stringWidth(_xlabel)/2;
                // NOTE: 3 pixel spacing between axis and labels.
                graphics.drawString(_xlabel, labxpos, lry + 3 + labelheight);
            }
        } else {
            // ticks have been explicitly specified
            Enumeration nt = _xticks.elements();
            Enumeration nl = _xticklabels.elements();
            while (nl.hasMoreElements()) {
                String label = (String) nl.nextElement();
                double xpos = ((Double)(nt.nextElement())).doubleValue();
                xCoord1 = ulx + (int)((xpos-xMin)*xscale);
                graphics.drawLine(xCoord1,uly,xCoord1,yCoord1);
                graphics.drawLine(xCoord1,lry,xCoord1,yCoord2);
                if (grid && xCoord1 != ulx && xCoord1 != lrx) {
                    graphics.setColor(Color.lightGray);
                    graphics.drawLine(xCoord1,yCoord1,xCoord1,yCoord2);
                    graphics.setColor(Color.black);
                }
                int labxpos = xCoord1 - lfm.stringWidth(label)/2;
                // NOTE: 3 pixel spacing between axis and labels.
                graphics.drawString(label, labxpos, lry + 3 + labelheight);
            }
        }
        
        ///////////////////// Draw title and axis labels now.
        
    	// Center the title and X label over the plotting region, not the window.
        graphics.setColor(Color.black);
        
        if (_title != null) {
         	graphics.setFont(_titlefont);
            int titlex = ulx + (width - tfm.stringWidth(_title))/2;
            graphics.drawString(_title,titlex,titley);
        }
        
        graphics.setFont(_labelfont);
        if (_xlabel != null) {
            int labelx = ulx + (width - lfm.stringWidth(_xlabel))/2;
            graphics.drawString(_xlabel,labelx,ySPos);
        }
        
        int charcenter = 2 + lfm.stringWidth("W")/2;
        int charheight = labelheight;
        if (_ylabel != null) {
            // Vertical label is fairly complex to draw.
            int yl = _ylabel.length();
            int starty = uly + (lry-uly)/2 - yl*charheight/2 + charheight;
            for (int i = 0; i < yl; i++) {
                String nchar = _ylabel.substring(i,i+1);
                int cwidth = lfm.stringWidth(nchar);
                graphics.drawString(nchar,charcenter - cwidth/2, starty);
                starty += charheight;
            }
        }
    }
    
    /**
     * Draw the legend in the upper right corner and return the width (in pixels)
     * used up.  The arguments give the upper right corner of the region where the
     * legend should be placed.
     */
    protected int drawLegend(int urx, int ury) {
        // FIXME: consolidate all these for efficiency
        graphics.setFont(_labelfont);
        FontMetrics lfm = graphics.getFontMetrics();
        int spacing = lfm.getHeight();

        Enumeration v = _legendStrings.elements();
        Enumeration i = _legendDatasets.elements();
        int ypos = ury + spacing;
        int maxwidth = 0;
        while (v.hasMoreElements()) {
            String legend = (String) v.nextElement();
            // NOTE: relies on _legendDatasets having the same number of entries.
            int dataset = ((Integer) i.nextElement()).intValue();
            // NOTE: 6 pixel width of point assumed.
            drawPoint(dataset, urx-3, ypos-3, false, false); 
            int width = lfm.stringWidth(legend);
            if (width > maxwidth) maxwidth = width;
            graphics.drawString(legend, urx - 15 - width, ypos);
            ypos += spacing;
        }
        return 22 + maxwidth;  // NOTE: subjective spacing parameter.
    }
    
    /**
     * Put a mark corresponding to the specified dataset at the specified
     * x and y position. There is no range checking here so that this method
     * can be used to place points in the legend, which is outside the plot.
     * In this base class, a point is a filled circle 6 pixels across.
     * Note that marks greater than about 6 pixels in size will not
     * look very good since they will overlap axis labels and may not fit well
     * in the legend.  The <i>connected</i> argument is ignored, but in derived classes,
     * it specifies whether the point should be connected by a line to previously
     * drawn points.
     */
    protected void drawPoint(int dataset, int xpos, int ypos, boolean connected, boolean clip) {
        boolean pointinside = ypos <= lry && ypos >= uly && xpos <= lrx && xpos >= ulx;
        if (!pointinside && clip) {return;}
        // Points are only distinguished up to 10 data sets.
        dataset %= 10;
        if (usecolor) {
            graphics.setColor(colors[dataset]);
        }
        graphics.fillOval(xpos-1, ypos-1, 3, 3);
        graphics.setColor(Color.black);
    }

    /**
     * Parse a line that gives plotting information.  In this base
     * class, only lines pertaining to the title and labels are processed.
     * Everything else is ignored. Return true if the line is recognized.
     */
    protected boolean parseLine (String line) {
        // Parse commands in the input file, ignoring lines with syntax errors or
        // unrecognized commands.
        if (line.startsWith("#")) {
            // comment character
            return true;
        }
        if (line.startsWith("TitleText:")) {
            setTitle((line.substring(10)).trim());
            return true;
        }
        if (line.startsWith("XLabel:")) {
            setXLabel((line.substring(7)).trim());
            return true;
        }
        if (line.startsWith("YLabel:")) {
            setYLabel((line.substring(7)).trim());
            return true;
        }
        if (line.startsWith("XRange:")) {
        	int comma = line.indexOf(",", 7);
        	if (comma > 0) {
        	    String min = (line.substring(7,comma)).trim();
        	    String max = (line.substring(comma+1)).trim();
        	    try {
        	        Double dmin = new Double(min);
        	        Double dmax = new Double(max);
        	        setXRange(dmin.doubleValue(), dmax.doubleValue());
        	    } catch (NumberFormatException e) {
        	        // ignore if format is bogus.
        	    }
        	}
        	return true;
        }
        if (line.startsWith("YRange:")) {
        	int comma = line.indexOf(",", 7);
        	if (comma > 0) {
        	    String min = (line.substring(7,comma)).trim();
        	    String max = (line.substring(comma+1)).trim();
        	    try {
        	        Double dmin = new Double(min);
        	        Double dmax = new Double(max);
        	        setYRange(dmin.doubleValue(), dmax.doubleValue());
        	    } catch (NumberFormatException e) {
        	        // ignore if format is bogus.
        	    }
        	}
        	return true;
        }
        if (line.startsWith("XTicks:")) {
            // example:
            // XTicks "label" 0, "label" 1, "label" 3
            boolean cont = true;
            _parsePairs(line.substring(7), true);
        	return true;
        }
        if (line.startsWith("YTicks:")) {
            // example:
            // YTicks "label" 0, "label" 1, "label" 3
            boolean cont = true;
            _parsePairs(line.substring(7), false);
        	return true;
        }
        
        if (line.startsWith("Grid:")) {
            if (line.indexOf("off",5) >= 0) {
                grid = false;
            }
            return true;
        }
        if (line.startsWith("Color:")) {
            if (line.indexOf("off",6) >= 0) {
                usecolor = false;
            }
            return true;
        }
        return false;
    }
    
    //////////////////////////////////////////////////////////////////////////
    ////                           protected variables                    ////
    
    Graphics graphics;

	// The range of the plot and exponent.
    protected double yMax, yMin, xMax, xMin;
    // The power of ten by which the range numbers should be multiplied.
    protected int yExp, xExp;
    // Whether the ranges have been given.
    protected boolean xRangeGiven = false;
    protected boolean yRangeGiven = false;
    // The minimum and maximum values registered so far, for auto ranging.
    protected double xBottom = Double.MAX_VALUE;
    protected double xTop = Double.MIN_VALUE;
    protected double yBottom = Double.MAX_VALUE;
    protected double yTop = Double.MIN_VALUE;
    
    // Whether to draw a background grid.
    protected boolean grid = true;
    
    // Derived classes can increment these to make space around the plot.
    protected int topPadding = 5;
    protected int bottomPadding = 5;
    protected int rightPadding = 10;
    protected int leftPadding = 10;

    // The plot rectangle in pixels.
    // The naming convention is: "ulx" = "upper left x", where "x" is
    // the horizontal dimension.
    protected int ulx, uly, lrx, lry;

    // Scaling used in plotting points.
    protected double yscale, xscale;
    
    // Indicator whether to use colors
    protected boolean usecolor = true;

    // Default colors, by data set.
    static protected Color[] colors = {
        new Color(0xcd0000),   // red3
        new Color(0x4a708b),   // skyblue4
        new Color(0x008b00),   // green4
        new Color(0x8b8b83),   // ivory4
        new Color(0x000000),   // black
        new Color(0xeec900),   // gold2
        new Color(0x8a2be2),   // blueviolet
        new Color(0x53868b),   // cadetblue4
        new Color(0xd2691e),   // chocolate
        new Color(0x556b2f),   // darkolivegreen
    };
        
    //////////////////////////////////////////////////////////////////////////
    ////                         private methods                          ////

    /**
     * Parse a string of the form: "word num, word num, word num, ..."
     * where the word must be enclosed in quotes if it contains spaces,
     * and the number is interpreted as a floating point number.  Ignore
     * any incorrectly formatted fields.  Append the words in order to the
     * vector wordved and the numbers (as Doubles) to the vector numvec.
     */
    private void _parsePairs (String line, boolean xtick) {    
        int start = 0;
        boolean cont = true;
        while (cont) {
        	int comma = line.indexOf(",", start);
        	String pair;
        	if (comma > start) {
        	    pair = (line.substring(start,comma)).trim();
            } else {
      	        pair = (line.substring(start)).trim();
       	        cont = false;
       	    }
       	    int close;
       	    int open = 0;
       	    if (pair.startsWith("\"")) {
        	    close = pair.indexOf("\"",1);
        	    open = 1;
        	} else {
                close = pair.indexOf(" ");	        
            }
       	    if (close > 0) {
       	        String label = pair.substring(open,close);
       	        String index = (pair.substring(close+1)).trim();
       	        try {
       	            double idx = (Double.valueOf(index)).doubleValue();
       	            if (xtick) addXTick(label, idx);
       	            else addYTick(label,idx);
       	        } catch (NumberFormatException e) {
       	            // ignore if format is bogus.
       	        }
       	    }
            start = comma + 1;
       	    comma = line.indexOf(",",start);
       	}
    }

    /**
     * Given a number, round up to the nearest power of ten
     * times 1, 2, or 5.
     *
     * Note: The argument must be strictly positive.
     */
     private double _roundUp(double val) {
         int exponent, idx;
         exponent = (int) Math.floor(Math.log(val)*_log10scale);
         val *= Math.pow(10, -exponent);
         if (val > 5.0) val = 10.0;
         else if (val > 2.0) val = 5.0;
         else if (val > 1.0) val = 2.0;
         val *= Math.pow(10, exponent);
         return val;
    }

    /**
     * Internal implementation of setXRange, so that it can be called when autoranging.
     */
    private void _setXRange (double min, double max) {
        if (min == max) {
            min -= 1.0;
            max += 1.0;
        }
        // Find the exponent.
        double largest = Math.max(Math.abs(min),Math.abs(max));
        xExp = (int) Math.floor(Math.log(largest)*_log10scale);
        // Use the exponent only if it's larger than 1 in magnitude.
        if (xExp > 1 || xExp < -1) {
            double xs = 1.0/Math.pow(10.0,(double)xExp);
            xMin = min*xs;
            xMax = max*xs;
        } else {
            xMin = min;
            xMax = max;
            xExp = 0;
        }
    }

    /**
     * Internal implementation of setYRange, so that it can be called when autoranging.
     */
    private void _setYRange (double min, double max) {
        if (min == max) {
            min -= 0.1;
            max += 0.1;
        }
        // Find the exponent.
        double largest = Math.max(Math.abs(min),Math.abs(max));
        yExp = (int) Math.floor(Math.log(largest)*_log10scale);
        // Use the exponent only if it's larger than 1 in magnitude.
        if (yExp > 1 || xExp < -1) {
            double ys = 1.0/Math.pow(10.0,(double)yExp);
            yMin = min*ys;
            yMax = max*ys;
        } else {
            yMin = min;
            yMax = max;
            yExp = 0;
        }
    }

    //////////////////////////////////////////////////////////////////////////
    ////                         private variables                        ////
    
    private Font _labelfont, _superscriptfont, _titlefont;
    
    // For use in calculating log base 10.  A log times this is a log base 10.
    private static final double _log10scale = 1/Math.log(10);
    
    // An array of strings for reporting errors.
    private String _errorMsg[];
    
    // The title and label strings.
    private String _xlabel, _ylabel, _title;
    
    // Legend information.
    private Vector _legendStrings;
    private Vector _legendDatasets;
    
    // If XTicks or YTicks are given
    private Vector _xticks, _xticklabels, _yticks, _yticklabels;
}
