/*
A base class for C code generators in Ptolemy II.

Copyright (c) 2001 The University of Maryland.
All rights reserved.

Permission is hereby granted, without written agreement and without
license or royalty fees, to use, copy, modify, and distribute this
software and its documentation for any purpose, provided that the above
copyright notice and the following two paragraphs appear in all copies
of this software.

IN NO EVENT SHALL THE UNIVERSITY OF MARYLAND BE LIABLE TO ANY PARTY
FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES
ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
THE UNIVERSITY OF MARYLAND HAS BEEN ADVISED OF THE POSSIBILITY OF
SUCH DAMAGE.

THE UNIVERSITY OF MARYLAND SPECIFICALLY DISCLAIMS ANY WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
MARYLAND HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
ENHANCEMENTS, OR MODIFICATIONS.

                                        PT_COPYRIGHT_VERSION_2
                                        COPYRIGHTENDKEY

@ProposedRating Red (ssb@eng.umd.edu)
@AcceptedRating Red (ssb@eng.umd.edu)
*/

package ptolemy.copernicus.c;

import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.ArrayType;
import soot.RefType;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;


/* A base class for C code generators in Ptolemy II.

   @author Shuvra S. Bhattacharyya
   @version $Id$

*/



public abstract class CodeGenerator {

    /** Construct a new code generator */
    public CodeGenerator() {
        _context = new Context();
    }

    ///////////////////////////////////////////////////////////////////
    ////                  public methods                           ////

    /** Turn off (disable) single class mode translation
     *  (see {@link Context#getSingleClassMode()}).
     */
    public void clearSingleClassMode() {
        _context.clearSingleClassMode();
    }

    /** Given a class, return the code generated by this code generator for
     *  the class.
     *  @param source the class.
     *  @return the generated code.
     */
    public abstract String generate(SootClass source);

    /** Turn on (enable) single class mode translation
     *  (see {@link Context#getSingleClassMode()}).
     */
    public void setSingleClassMode() {
        _context.setSingleClassMode();
    }

    ///////////////////////////////////////////////////////////////////
    ////                  protected methods                        ////

    /** Enclose a given string of text within appropriate delimiters to
     *  form a comment in the generated code.
     *  Also, append a new line after the comment.
     *  @param text the text to place in the generated comment.
     *  @return The generated comment.
     */
    protected final String _comment(String text) {
        return("/* " + text + " */\n");
    }

    /** Generate include directives for all types that are required for the
     *  class that we are generating code for.
     *  @param source the class that we are generating code for.
     *  @return The generated include directives.
     */
    protected String _generateIncludeDirectives() {
        StringBuffer headerCode = new StringBuffer();

        Iterator includeFiles = _context.getIncludeFiles();
        if (includeFiles.hasNext()) {
            headerCode.append(_comment("System and runtime include files"));
        }
        while (includeFiles.hasNext()) {
            headerCode.append("#include ");
            headerCode.append((String)(includeFiles.next()));
            headerCode.append("\n");
        }

        Iterator requiredTypes = _getRequiredIncludeFiles();
        if (requiredTypes.hasNext()) {
            headerCode.append("\n" + _comment("Converted classes"));
        }
        while (requiredTypes.hasNext()) {
            headerCode.append("#include \"");
            headerCode.append((String)(requiredTypes.next()));
            headerCode.append("\"\n");
        }

        return headerCode.toString();
    }

    /** Generate header code for a method. Parameter names are not included
     *  in the generated code.
     *  @param method the method.
     *  @return the header code.
     */
    protected String _generateMethodHeader(SootMethod method) {
        StringBuffer header = new StringBuffer();
        Type returnType = method.getReturnType();
        header.append(CNames.typeNameOf(returnType));
        _updateRequiredTypes(returnType);
        header.append(" ");
        header.append(CNames.functionNameOf(method));
        header.append("(");
        header.append(_generateParameterTypeList(method));
        header.append(")");
        return header.toString();
    }

    /** Generate code for the parameter type list of a method,
     *  excluding parentheses.
     *  @param method the method.
     *  @return code for the parameter type list.
     */
    protected String _generateParameterTypeList(SootMethod method) {
        StringBuffer code = new StringBuffer();
        Iterator parameters = method.getParameterTypes().iterator();
        int numberOfParameters = 0;
        if (!method.isStatic()) {
            code.append(CNames.instanceNameOf(method.getDeclaringClass()));
            numberOfParameters++;
        }
        while (parameters.hasNext()) {
            if ((++numberOfParameters) > 1) code.append(", ");
            Type parameterType = (Type)(parameters.next());
            code.append(CNames.typeNameOf(parameterType));
            _updateRequiredTypes(parameterType);
        }
        return code.toString();
    }

    /** Return an iterator over the include files required by
     *  the generated code. Each element
     *  in the iterator is a String that gives the name of an include file.
     *  @return the names of the required include files.
     */
    protected Iterator _getRequiredIncludeFiles() {
        return _requiredTypeMap.values().iterator();
    }

    /** Return a string that generates an indentation string (a sequence
     *  of spaces) for the given indentation level. Each indentation
     *  level unit is four characters wide.
     *  @param level The indentation level.
     *  @return The indentation string that corresponds to the given
     *  indentation level.
     */
    protected String _indent(int level) {
        StringBuffer indent = new StringBuffer();
        int i;
        for (i = 0; i < level; i++) {
            indent.append("    ");
        }
        return indent.toString();
    }

    /** Remove a class from the list of required types (types whose associated
     *  include files must be imported) if the class exists in the list.
     *  @param source the class.
     */
    protected void _removeRequiredType(SootClass source) {
        if (_requiredTypeMap.containsKey(source)) {
            _requiredTypeMap.remove(source);
        }
    }

    /** Register a type as a type that must be imported into the generated code
     *  through an #include directive. The request is processed only if the
     *  argument
     *  is a RefType, or if it is an ArrayType with a RefType as the base type.
     *  All other requests are ignored. Duplicate requests are also ignored.
     *  @param type the type.
     */
    protected void _updateRequiredTypes(Type type) {
        if (!_context.getDisableImports()) {
            SootClass source = null;
            if (type instanceof RefType) {
                source = ((RefType)type).getSootClass();
            } else if ((type instanceof ArrayType) &&
                    (((ArrayType)type).baseType instanceof RefType)) {
                source = ((RefType)(((ArrayType)type).baseType)).getSootClass();
            }
            if (source != null) {
                if (!_requiredTypeMap.containsKey(source)) {
                    _requiredTypeMap.put(source,
                            CNames.includeFileNameOf(source));
                }
            }
        }
    }

    /** Issue a warning message to standard error.
     *  @param message the warning message.
     */
    protected void _warn(String message) {
        System.err.println("C code generation warning: " + message + "\n");
    }

    ///////////////////////////////////////////////////////////////////
    ////                  protected variables                      ////

    // Code generation context information.
    protected Context _context;

    ///////////////////////////////////////////////////////////////////
    ////                  private variables                        ////

    // Mapping from classes that the current class depends on to their
    // include file names.
    private HashMap _requiredTypeMap = new HashMap();

}
