/* A code generation helper class for actor.lib.DiscreteRandomSource
 @Copyright (c) 2005-2006 The Regents of the University of California.
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
 PROVIDED HEREUNDER IS ON AN \"AS IS\" BASIS, AND THE UNIVERSITY OF
 CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, 
 ENHANCEMENTS, OR MODIFICATIONS.

 PT_COPYRIGHT_VERSION_2
 COPYRIGHTENDKEY

 */
package ptolemy.codegen.c.actor.lib;

import ptolemy.kernel.util.IllegalActionException;

/**
 A code generation helper class for ptolemy.actor.lib.DiscreteRandomSource. 
 
 @author Man-Kit Leung
 @version $Id$
 @since Ptolemy II 6.0
 @Pt.ProposedRating Green (mankit)
 @Pt.AcceptedRating Green (mankit)
 */
public class DiscreteRandomSource extends RandomSource {

    /**
     * Construct a DiscreteRandomSource helper.
     * @param actor The associated actor.
     */
    public DiscreteRandomSource(ptolemy.actor.lib.DiscreteRandomSource actor) {
        super(actor);
    }
    
    ///////////////////////////////////////////////////////////////////
    ////                         protected methods                 ////

    /** Generate code for producing a new random number.
     *  @exception IllegalActionException Not thrown in this base class.
     */
    protected String _generateRandomNumber()
            throws IllegalActionException {
        return _generateBlockCode("randomBlock");
    }
}
