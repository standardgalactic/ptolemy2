/** A class representing the type of a multi-dimensional array.

 Copyright (c) 1997-1999 The Regents of the University of California.
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

@ProposedRating Red (neuendor@eecs.berkeley.edu)
@AcceptedRating Red (cxh@eecs.berkeley.edu)

*/

package ptolemy.data.type;

import ptolemy.graph.InequalityTerm;
import ptolemy.graph.Inequality;	/* Needed for javadoc */ 
import ptolemy.kernel.util.IllegalActionException;
import java.util.Enumeration;

//////////////////////////////////////////////////////////////////////////
//// DimensionType
/**
A class representing the size of a multi-dimensional array as a type.

@author Steve Neuendorffer
$Id$

*/

public class DimensionType implements Type implements InequalityTerm
{
    /**
     * Create a new dimension type variable, initialized to 
     * DimensionType.BOTTOM;
     */
    public DimensionType() {
        this(DimensionType.BOTTOM);
        _isSettable = true;
    }

    /** 
     * Create a new dimension type constant with the given characteristics.
     * @param dimensions The number of dimensions of the new type.
     * @param size An array of the size in each dimension of the new type.
     */
    public DimensionType(int dimensions, int size[]) {
        _dimensions = dimensions;
        _size = size;
        _isSettable = false;
    }

    /**
     * Create a new dimension type constant with the value given by the 
     * given DimensionType.
     */
    public DimensionType(DimensionType type) {
        _dimensions = type._dimensions;
        _size = type._size;
        _isSettable = false;
    }

    /** Return the Object associated with this term. If this term is
     *  not associated with a particular Object, or it is not necessary
     *  to obtain the reference of the associated Object, this method
     *  can return <code>null</code>.
     *  @return null.
     */
    public Object getAssociatedObject() {
        return null;
    }
    

    /** Return the value of this term.  If this term is a constant,
     *  return that constant; if this term is a variable, return the
     *  current value of that variable; if this term is a function,
     *  return the evaluation of that function based on the current
     *  value of variables in the function.
     *  @return an Object representing an element in the underlying CPO.
     */
    public Object getValue() {
        return this;
    }

    /** Return an array of variables contained in this term.
     *  If this term is a constant, return an array of size zero;
     *  if this term is a variable, return an array of size one that
     *  contains this variable; if this term is a function, return an
     *  array containing all the variables in the function.
     *  @return an array of InequalityTerms
     */
    public InequalityTerm[] getVariables() {
        InequalityTerm terms[];
        if(isSettable()) {
            terms = new InequalityTerm[1];
            terms[0] = this;
        } else {
            terms = new InequalityTerm[0];
        }
        return terms;
    }

    /** Return true if the given type is equal to this type.   In other words, 
     *  an object with this type can be expressed as an object of Type t with 
     *  no conversion.
     */
    public boolean isEqualTo(Type t) {
        if(_dimensions != t._dimensions) return false;
        int i;
        for(i = 0; i < _dimensions; i++) {
            if(_size[i] != t._size[i]) return false;
        }
        return true;
    }

    /** Check whether this term can be set to a specific element of the
     *  underlying CPO. Only variable terms are settable, constant
     *  and function terms are not.
     *  @return <code>true</code> if this term is a variable;
     *   <code>false</code> otherwise.
     */
    public boolean isSettable() {
        return _isSettable;
    }

    /** Check whether the current type of this term is acceptable,
     *  and return true if it is.  Normally, a type is acceptable
     *  if it represents an instantiable object.
     *  @return True if the current type is acceptable.
     */
    public boolean isTypeAcceptable() {
        if(this.isEqualTo(TOP)) return false;
        if(this.isEqualTo(BOTTOM)) return false;
        if(_dimensions < 0) return false;
        if(_dimensions > 2) return false;
        return true;
    }

    /** Set the value of this term to the specified CPO element.
     *  Only terms consisting of a single variable can have their
     *  values set.
     *  @param e an Object representing an element in the
     *   underlying CPO.
     *  @exception IllegalActionException If this term is not a variable.
     */
    public void setValue(Object e)
            throws IllegalActionException {
        if(!e instanceof DimensionType) throw new IllegalActionException(this,
                e, "Cannot setvalue of a DimensionType to something that" +
                " is not a DimensionType");
        if(!isSettable()) throw new IllegalActionException(this, 
                "This dimension type is a constant and cannot have its "+
                "value set!");
        _dimensions = type._dimensions;
        _size = type._size;        
    }

    /** 
     * An unspecified dimension type.
     */
    public static final DimensionType BOTTOM = new DimensionType(-1, null);
    
    /**
     * A general dimension type.
     */
    public static final DimensionType TOP = new DimensionType(-2, null);
    
    /** The number of dimensions of an object of this type.
     *  0 = scalar, 1 = 1D array, 2 = 2D array, etc.
     */
    private int _dimensions;
   
    /** The dimensions of an object of this type.
     *  The array has a length given by _dimensions
     */
    private int _size[];
    
    /** 
     * True if this type is a constant type.  False if this type is a
     * variable type.  This value is set at construction and cannot be changed.
     */
    private int _isSettable;
}

