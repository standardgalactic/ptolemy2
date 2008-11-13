/* Top-level window for Ptolemy models with a menubar and status bar.

 Copyright (c) 1998-2008 The Regents of the University of California.
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
 */
package ptolemy.actor.gui;

import ptolemy.gui.Top;


//////////////////////////////////////////////////////////////////////////
//// TopPack

/**
 This interface allows for alternate pack() methods to be called from 
 TableauFrame which allows for functionality such as alternate menu systems 
 in Vergil.  

 @author Chad Berkley
 @version $Id: TableauFrame.java 49900 2008-06-20 17:37:40Z cxh $
 @since Ptolemy II 1.0
 @Pt.ProposedRating Red (berkley)
 */
public interface TopPack {

  /**
   * method to implement to override pack
   */
  public void pack(Top t, boolean alreadyCalled);

  /** 
   * allows the overrider to pass an object back to the calling object
   */
  public Object getObject(Object identifier);
}
