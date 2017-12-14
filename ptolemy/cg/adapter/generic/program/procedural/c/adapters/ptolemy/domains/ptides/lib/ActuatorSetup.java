/* Code generator adapter for Receiver.

 Copyright (c) 2009-2014 The Regents of the University of California.
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
package ptolemy.cg.adapter.generic.program.procedural.c.adapters.ptolemy.domains.ptides.lib;

import java.util.LinkedList;
import java.util.List;

import ptolemy.cg.adapter.generic.program.procedural.c.adapters.ptolemy.domains.ptides.kernel.PtidesPreemptiveEDFDirector;
import ptolemy.cg.kernel.generic.program.CodeStream;
import ptolemy.kernel.util.IllegalActionException;

/**
 * A code generation adapter class for ptolemy.domains.ptides.lib.ActuatorSetup.
 * @author Jia Zou, Isaac Liu
 * @version $Id$
 * @since Ptolemy II 10.0
 * @Pt.ProposedRating Red (jiazou)
 * @Pt.AcceptedRating Red (jiazou)
 */
public class ActuatorSetup extends OutputDevice {
    /**
     * Construct a ActuatorSetup adapter.
     * @param actor The associated actor.
     *
     */
    public ActuatorSetup(ptolemy.domains.ptides.lib.ActuatorSetup actor) {
        super(actor);
    }

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    @Override
    public String generateActuatorActuationFuncCode()
            throws IllegalActionException {
        List args = new LinkedList();
        CodeStream _codeStream = _templateParser.getCodeStream();

        _codeStream.clear();
        _codeStream.appendCodeBlock("actuationBlock", args);

        return processCode(_codeStream.toString());
    }

    /**
     * Generate the fire code. In this base class, add the name of the
     * associated component in the comment. It checks the inline parameter
     * of the code generator. If the value is true, it generates the actor
     * fire code and the necessary type conversion code. Otherwise, it
     * generate an invocation to the actor function that is generated by
     * generateFireFunctionCode.
     * @return The generated code.
     * @exception IllegalActionException Not thrown in this base class.
     */
    @Override
    public String generateFireCode() throws IllegalActionException {
        List args = new LinkedList();
        CodeStream _codeStream = _templateParser.getCodeStream();

        ptolemy.domains.ptides.lib.ActuatorSetup actor = (ptolemy.domains.ptides.lib.ActuatorSetup) getComponent();
        PtidesPreemptiveEDFDirector adapter = (PtidesPreemptiveEDFDirector) getAdapter(
                actor.getDirector());

        args.add(adapter.actuators.get(actor).toString());

        _codeStream.clear();
        _codeStream.appendCodeBlock("fireBlock", args);

        return processCode(_codeStream.toString());
    }

    @Override
    public String generateHardwareInitializationCode()
            throws IllegalActionException {
        StringBuffer code = new StringBuffer();
        code.append(processCode(_templateParser.getCodeStream()
                .getCodeBlock("initializeActuationOutput")));
        return code.toString();
    }
}
