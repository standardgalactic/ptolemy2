package ptolemy.data.properties;

import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.NameDuplicationException;
import ptolemy.kernel.util.NamedObj;
import ptolemy.kernel.util.StringAttribute;

public class AnnotationAttribute extends StringAttribute {

    /** Construct a PropertyAttribute with the specified name, and container.
     * @param container Container
     * @param name The given name for the attribute.
     * @exception IllegalActionException If the attribute is not of an
     *  acceptable class for the container, or if the name contains a period.
     *  @exception NameDuplicationException If the name coincides with
     *  an attribute already in the container.
     */
    public AnnotationAttribute(NamedObj container, String name)
    throws IllegalActionException, NameDuplicationException {
        super(container, name);
    }

    /**
     * Set the name of the attribute and error-check for name format.
     * A proper name should contain an use-case identifier and an 
     * attribute label, separated by "::". 
     */
    public void setName(String name) throws IllegalActionException,
    NameDuplicationException {
        super.setName(name);
        _checkAttributeName(name);
    }

    /**
     * Check the name of the annotation attribute. The given name contains
     * an use case identifier and the annotation label. The two parts are
     * separated by the symbol "::" (two consecutive semicolons). The use
     * case identifier needs to be associated with a PropertySolver in 
     * the model. Bad  
     * @param name The given name of the annotation attribute.
     * @throws IllegalActionException Thrown if no PropertySolver can
     *  be found using the given name.
     * @throws NameDuplicationException Not thrown in this method.
     */
    private void _checkAttributeName(String name) 
    throws IllegalActionException, NameDuplicationException {
        String usecaseName = getUseCaseIdentifier();

        // FIXME: Cannot check if there is an assoicated solver
        // because it may not be instantiated yet.
        
//        List solvers = toplevel().attributeList(PropertySolver.class);
//        if (solvers.isEmpty()) {
//            throw new IllegalActionException(
//                    "No use case found for annotation: " + usecaseName + ".");
//        } else {
//            try {
//                ((PropertySolver)solvers.get(0)).findSolver(usecaseName);
//
//            } catch (IllegalActionException ex) {
//                throw new IllegalActionException(
//                        "No use case found for annotation: " + usecaseName + ".");
//            }
//        }
    }

    public String getUseCaseIdentifier() throws IllegalActionException {
        String[] tokens = getName().split("::");
        
        if (tokens.length == 2) {
            return tokens[0];
            
        } else if (tokens.length == 3) {
            // If it is an extended use-case identifier,
            // which would contain an extra "::" symbol.
            return tokens[0] + "::" + tokens[1];
        }
        
        throw new IllegalActionException(
                "Bad annotation attribute name: " + getName()
                + ". (should have form USECASE::LABEL)");
    }
}
