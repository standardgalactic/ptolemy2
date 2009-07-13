/*
 * A class that represents a meet function term.
 * 
 * Copyright (c) 1998-2009 The Regents of the University of California. All
 * rights reserved. Permission is hereby granted, without written agreement and
 * without license or royalty fees, to use, copy, modify, and distribute this
 * software and its documentation for any purpose, provided that the above
 * copyright notice and the following two paragraphs appear in all copies of
 * this software.
 * 
 * IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR
 * DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT
 * OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF THE UNIVERSITY OF
 * CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE PROVIDED HEREUNDER IS ON AN
 * "AS IS" BASIS, AND THE UNIVERSITY OF CALIFORNIA HAS NO OBLIGATION TO PROVIDE
 * MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 * 
 * PT_COPYRIGHT_VERSION_2 COPYRIGHTENDKEY
 * 
 */
package ptolemy.data.properties.lattice;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ptolemy.data.properties.Property;
import ptolemy.graph.InequalityTerm;
import ptolemy.kernel.util.IllegalActionException;

//////////////////////////////////////////////////////////////////////////
//// MeetFunction

/**
 * A class that represents the property term of a meet function. A meet function
 * is defined to return the least upper bound values of all its inputs, assuming
 * the inputs are elements from a common lattice.
 * 
 * @author Man-Kit Leung
 * @version $Id$
 * @since Ptolemy II 7.1
 * @Pt.ProposedRating Red (mankit)
 * @Pt.AcceptedRating Red (mankit)
 */

public class MeetFunction extends MonotonicFunction {

    public MeetFunction(PropertyConstraintSolver solver, List<Object> objects) {
        this(solver, objects.toArray());
    }

    public MeetFunction(PropertyConstraintSolver solver, Set<Object> objects) {
        this(solver, objects.toArray());
    }

    public MeetFunction(PropertyConstraintSolver solver, Object... objects) {
        _solver = solver;
        for (Object object : objects) {
            _terms.add(_solver.getPropertyTerm(object));
        }
    }

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /**
     * Add variables to the meet function.
     * @param variables The list of variables to be added.
     */
    public void addVariables(List<PropertyTerm> variables) {
        _terms.addAll(variables);
    }

    /**
     * Return the function result.
     * @return A Property.
     * @exception IllegalActionException
     */
    public Object getValue() throws IllegalActionException {
        Property meetValue = null;
        Property termValue = null;

        for (PropertyTerm term : _terms) {
            if (term.isEffective()) {
                termValue = (Property) term.getValue();

                meetValue = meetValue == null ? termValue : _solver
                        .getLattice().greatestLowerBound(meetValue, termValue);
            }
        }
        return meetValue;
    }

    public String toString() {
        String result = "meet(";

        for (PropertyTerm term : _terms) {
            if (term.isEffective()) {
                result += term;
                break;
            }
        }

        for (PropertyTerm term : _terms) {
            if (term.isEffective()) {
                result += " /\\ " + term;
            }
        }

        return result + ")";
    }

    public boolean isEffective() {
        for (PropertyTerm term : _terms) {
            if (term.isEffective()) {
                return true;
            }
        }
        return false;
    }

    public void setEffective(boolean isEffective) {
        throw new AssertionError(
                "Cannot set the effectiveness of a MeetFunction term.");
    }

    ///////////////////////////////////////////////////////////////
    ////                       private inner variable          ////

    private final PropertyConstraintSolver _solver;

    private final List<PropertyTerm> _terms = new LinkedList<PropertyTerm>();

    protected InequalityTerm[] _getDependentTerms() {
        InequalityTerm[] terms = new InequalityTerm[_terms.size()];
        System.arraycopy(_terms.toArray(), 0, terms, 0, _terms.size());
        return terms;
    }

}
