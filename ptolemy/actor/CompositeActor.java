/* An aggregation of actors.

 Copyright (c) 1997- The Regents of the University of California.
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

@ProposedRating Yellow (eal@eecs.berkeley.edu)
*/

package pt.actor;

import pt.kernel.*;
import pt.kernel.util.*;

import java.util.Enumeration;
import collections.LinkedList;

//////////////////////////////////////////////////////////////////////////
//// CompositeActor
/**
A CompositeActor is an aggregation of actors.  It may have a
<i>local director</i>, which is an object of class Director that
is responsible for executing the contained actors.
Normally, at the top level of a hierarchy, a composite actor with no
container will have a local director.  A composite actor at a lower level
of the hierarchy may also have a local director, in which it is called
a <i>wormhole</i>.  A wormhole, despite being a composite, returns
<i>true</i> to its isAtomic() method.  Thus, it behaves both as a
composite and an atomic actor.  Its ports are opaque, but it can
contain actors and relations.
<p>
The getDirector() method returns the local director if there is one.
Otherwise, it returns the <i>executive director</i>, if there is one.
Whatever it returns is called (simply) the <i>director</i> of the
composite (it may be local or executive).
<p>
The <i>executive director</i>, is simply the director of the container,
if it has one.  If there is no container (the composite is at the top
level of the hierarchy), then an executive director can be explicitly
specified using the setExecutiveDirector() method.  Such an executive
director implements the master controller for execution of an application.
It may, for example, be a control panel user interface.
It is responsible for invoking the director.
<p>
A composite actor must have a director in order to be executable.
In fact, it cannot even receive data in its input ports without a director,
since the director is responsible for supplying the receivers to the ports.
If the getDirector() method returns null, then the composite is not
executable.
<p>
If there is no director, then the executive director plays the role
of director.  I.e., for the purposes of execution, the hierarchy is
flattened.  The executive director executes the composite by executing
the contained actors. If there is a director, then the executive director
executes this composite by issuing commands to the director.
<p>
When a composite actor has both a director and an executive director, then
the model of computation implemented by the director need not be the
same as the model of computation implemented by the executive director.
This is the source of the hierarchical heterogeneity in Ptolemy II.
Multiple models of computation can be cleanly nested.
<p>
The ports of a CompositeActor are constrained to be IOPorts, the
relations to be IORelations, and the actors to be instances of
ComponentEntity that implement the Actor interface.  Derived classes
may impose further constraints by overriding newPort(), _addPort(),
newRelation(), _addRelation(), and _addEntity().
<p>
The container is constrained to be an instance of CompositeActor.
Derived classes may impose further constraints by overriding setContainer().

@author Mudit Goel, Edward A. Lee
@version $Id$
@see pt.actors.IOPort
@see pt.actors.IORelation
@see pt.kernel.ComponentEntity
*/
public class CompositeActor extends CompositeEntity implements Actor {

    /** Construct a top-level CompositeActor in the default workspace
     *  with an empty string as its name. Add the actor to the workspace
     *  directory.  You should set the local director or executive director
     *  before attempting to send data to the actor or to execute it.
     *  Increment the version number of the workspace.
     */
    public CompositeActor() {
        super();
    }

    /** Construct a top-level CompositeActor in the specified workspace
     *  with an empty string as a name. You can then change the name with
     *  setName(). If the workspace argument is null, then use the default
     *  workspace.  You should set the local director or executive director
     *  before attempting to send data to the actor or to execute it.
     *  Add the actor to the workspace directory.
     *  Increment the version number of the workspace.
     *  @param workspace The workspace that will list the actor.
     */
    public CompositeActor(Workspace workspace) {
	super(workspace);
    }

    /** Create an actor with a name and a container.
     *  The container argument must not be null, or a
     *  NullPointerException will be thrown.  This actor will use the
     *  workspace of the container for synchronization and version counts.
     *  If the name argument is null, then the name is set to the empty string.
     *  Increment the version of the workspace.
     *  You should set the local director or executive director
     *  before attempting to send data to the actor or to execute it.
     *
     *  @param container The container actor.
     *  @param name The name of this actor.
     *  @exception IllegalActionException If the container is incompatible
     *   with this actor.
     *  @exception NameDuplicationException If the name coincides with
     *   an actor already in the container.
     */
    public CompositeActor(CompositeEntity container, String name)
            throws IllegalActionException, NameDuplicationException {
        super(container, name);
    }

    ////////////////////////////////////////////////////////////////////////
    ////                         public methods                         ////

    /** Clone the actor into the specified workspace. The new object is
     *  <i>not</i> added to the directory of that workspace (you must do this
     *  yourself if you want it there).
     *  NOTE: This will not work if there are level-crossing transitions.
     *  The result is a composite actor with clones of the ports of the
     *  original actor, the contained actors, and the contained relations.
     *  The ports of the returned actor are not connected to anything.
     *  The connections of the relations are duplicated in the new composite,
     *  unless they cross levels, in which case an exception is thrown.
     *  The local director is cloned, if there is one.  The executive director
     *  is cloned only if it has been explicitly specified using
     *  setExecutiveDirector().  It is not cloned if it is inherited from
     *  the container.
     *
     *  @param ws The workspace for the cloned object.
     *  @exception CloneNotSupportedException If the actor contains
     *   level crossing transitions so that its connections cannot be cloned,
     *   or if one of the attributes cannot be cloned.
     *  @return A new CompositeActor.
     */
    public Object clone(Workspace ws) throws CloneNotSupportedException {
        CompositeActor newobj = (CompositeActor)super.clone(ws);
        if (_director != null) {
            newobj._director = (Director)_director.clone();
        } else {
            newobj._director = null;
        }
        if (_execdirector != null) {
            newobj._execdirector = (Director)_execdirector.clone();
        } else {
            newobj._execdirector = null;
        }
        newobj._inputPortsVersion = -1;
        newobj._outputPortsVersion = -1;
        return newobj;
    }

    /** If there is a director, invoke its fire() method.
     *  This method is read-synchronized on the workspace, so the
     *  fire() method of the director need not be (assuming it is only
     *  called from here).
     *  @exception IllegalActionException If there is no director, or if
     *   the director's fire() method throws it.
     */
    public void fire() throws IllegalActionException {
        try {
            workspace().getReadAccess();
            Director dir = getDirector();
            if (dir == null) {
                throw new IllegalActionException(this,
                "Cannot fire without a director.");
            }
            dir.fire();
        } finally {
            workspace().doneReading();
        }
    }

    /** Return the director responsible for execution of the contained
     *  actors.  This will be either the local director (if it exists) or the
     *  executive director (obtained using getExecutiveDirector()).
     *  This method is read-synchronized on the workspace.
     *
     *  @return The director responsible for invocation of inside actors.
     */
    public Director getDirector() {
        try {
            workspace().getReadAccess();
            if (_director != null) return _director;
            return getExecutiveDirector();
        } finally {
            workspace().doneReading();
        }
    }

    /** Return the executive director.
     *  If the executive director has been set using setExecutiveDirector,
     *  then that is what is returned.  Otherwise, the container (if any)
     *  is queried for its director.  If it has none, or there
     *  is no container, then return null. This method is read-synchronized
     *  on the workspace.
     *
     *  @return The executive director.
     */
    public Director getExecutiveDirector() {
        try {
            workspace().getReadAccess();
            if (_execdirector != null) return _execdirector;
            CompositeActor container = (CompositeActor)getContainer();
            if (container != null) return container.getDirector();
            return null;
        } finally {
            workspace().doneReading();
        }
    }

    /** If there is a director, invoke its initialize() method.
     *  This method is read-synchronized on the workspace, so the initialize()
     *  method of the director need not be, assuming it is only called from
     *  here.
     *
     *  @exception IllegalActionException If there is no director, or if
     *   the director's initialize() method throws it.
     */
    public void initialize() throws IllegalActionException {
        try {
            workspace().getReadAccess();
            Director dir = getDirector();
            if (dir == null) {
                throw new IllegalActionException(this,
                "Cannot initialize without a director.");
            }
            dir.initialize();
        } finally {
            workspace().doneReading();
        }
    }

    /** Return an enumeration of the input ports.
     *  This method is read-synchronized on the workspace.
     *  @return An enumeration of IOPort objects.
     */
    public Enumeration inputPorts() {
        try {
            workspace().getReadAccess();
            if(_inputPortsVersion != workspace().getVersion()) {
                // Update the cache.
                LinkedList inports = new LinkedList();
                Enumeration ports = getPorts();
                while(ports.hasMoreElements()) {
                    IOPort p = (IOPort)ports.nextElement();
                    if( p.isInput()) {
                        inports.insertLast(p);
                    }
                }
                _cachedInputPorts = inports;
                _inputPortsVersion = workspace().getVersion();
            }
            return _cachedInputPorts.elements();
        } finally {
            workspace().doneReading();
        }
    }

    /** Return true if this actor contains a local director.
     *  Otherwise, return false.  This method is <i>not</i>
     *  synchronized on the workspace, so the caller should be.
     */
    public boolean isAtomic() {
        return _director != null;
    }

    /** Create a new IOPort with the specified name.
     *  The container of the port is set to this actor.
     *  This method is write-synchronized on the workspace.
     *
     *  @param name The name for the new port.
     *  @return The new port.
     *  @exception NameDuplicationException If the actor already has a port
     *   with the specified name.
     */
    public Port newPort(String name)
            throws IllegalActionException, NameDuplicationException {
        try {
            workspace().getWriteAccess();
            IOPort port = new IOPort(this, name);
            return port;
        } catch (IllegalActionException ex) {
            // This exception should not occur, so we throw a runtime
            // exception.
            throw new InternalErrorException(
            "CompositeActor.newPort: Internal error: " + ex.getMessage());
        } finally {
            workspace().doneWriting();
        }
    }

    /** Return a new receiver of a type compatible with the director.
     *  Derived classes may further specialize this to return a reciever
     *  specialized to the particular actor.  This method is <i>not</i>
     *  synchronized on the workspace, so the caller should be.
     *
     *  @exception IllegalActionException If there is no director.
     *  @return A new object implementing the Receiver interface.
     */
    public Receiver newReceiver() throws IllegalActionException {
        Director dir = getDirector();
        if (dir == null) {
            throw new IllegalActionException(this,
            "Cannot create a receiver without a director.");
        }
        return dir.newReceiver();
    }

    /** Create a new IORelation with the specified name, add it to the
     *  relation list, and return it. Derived classes can override
     *  this to create domain-specific subclasses of IORelation.
     *  This method is write-synchronized on the workspace.
     *
     *  @exception IllegalActionException If name argument is null.
     *  @exception NameDuplicationException If name collides with a name
     *   already on the container's contents list.
     */
    public ComponentRelation newRelation(String name)
            throws IllegalActionException, NameDuplicationException {
        try {
            workspace().getWriteAccess();
            IORelation rel = new IORelation(this, name);
            return rel;
        } finally {
            workspace().doneWriting();
        }
    }

    /** Return an enumeration of the output ports.
     *  This method is read-synchronized on the workspace.
     *  @return An enumeration of IOPort objects.
     */
    public Enumeration outputPorts() {
        try {
            workspace().getReadAccess();
            if(_outputPortsVersion != workspace().getVersion()) {
                _cachedOutputPorts = new LinkedList();
                Enumeration ports = getPorts();
                while(ports.hasMoreElements()) {
                    IOPort p = (IOPort)ports.nextElement();
                    if( p.isOutput()) {
                        _cachedOutputPorts.insertLast(p);
                    }
                }
                _outputPortsVersion = workspace().getVersion();
            }
            return _cachedOutputPorts.elements();
        } finally {
            workspace().doneReading();
        }
    }

    /** Invoke the postfire() method of the director and transfer output
     *  data (if this is a wormhole).  Specifically, if there is a
     *  local director (isAtomic() returns
     *  true), transfer any data from the output ports of this composite to
     *  the ports connected on the outside. The transfer is accomplished
     *  by calling the transferOuputs() method of the executive director.
     *  If there is no executive director, then no transfer occurs.
     *  This method is read-synchronized on the workspace.
     *
     *  @exception IllegalActionException If there is no director,
     *   or if the director's postfire() method throws it.
     */
    public void postfire() throws IllegalActionException {
        try {
            workspace().getReadAccess();
            Director dir = getDirector();
            if (dir == null) {
                throw new IllegalActionException(this,
                "Cannot postfire without a director.");
            }
            dir.postfire();
            if (isAtomic()) {
                // The composite actor is a wormhole.
                // Use the executive director to transfer outputs.
                Director edir = getExecutiveDirector();
                if (edir != null) {
                    Enumeration ports = outputPorts();
                    while(ports.hasMoreElements()) {
                        IOPort p = (IOPort)ports.nextElement();
                        try {
                            edir.transferOutputs(p);
                        } catch (CloneNotSupportedException ex) {
                            // Rethrow as a disallowed action.
                            throw new IllegalActionException(ex.getMessage());
                        }
                    }
                }
            }
        } finally {
            workspace().doneReading();
        }
    }

    /** Transfer input data (if this is a wormhole) and invoke the prefire()
     *  method of the director. Specifically, if there is a local director
     *  (isAtomic() returns true), transfer any data from the input ports
     *  of this composite to the ports connected on the inside. The transfer
     *  is accomplished by calling the transferInputs() method of the local
     *  director.   This method returns true if the actor is ready to fire
     *  (determined by the prefire() method of the director).
     *  This method is read-synchronized on the workspace.
     *
     *  @return True if the actor is ready for firing, false otherwise.
     *  @exception IllegalActionException If there is no director,
     *   or if the director's prefire() method throws it.
     */
    public boolean prefire() throws IllegalActionException {
        try {
            workspace().getReadAccess();
            Director dir = getDirector();
            if (dir == null) {
                throw new IllegalActionException(this,
                "Cannot prefire without a director.");
            }
            if (isAtomic()) {
                // The composite actor is a wormhole.
                // Use the local director to transfer outputs.
                Enumeration ports = inputPorts();
                while(ports.hasMoreElements()) {
                    IOPort p = (IOPort)ports.nextElement();
                    try {
                        _director.transferInputs(p);
                    } catch (CloneNotSupportedException ex) {
                        // Rethrow as a disallowed action.
                        throw new IllegalActionException(ex.getMessage());
                    }
                }
            }
            return dir.prefire();
        } finally {
            workspace().doneReading();
        }
    }

    /** Override the base class to ensure that the proposed container
     *  is an instance of CompositeActor. If it is, call the base class
     *  setContainer() method.
     *
     *  @param entity The proposed container.
     *  @exception IllegalActionException If the action would result in a
     *   recursive containment structure, or if
     *   this entity and container are not in the same workspace, or
     *   if the argument is not a CompositeActor.
     *  @exception NameDuplicationException If the container already has
     *   an entity with the name of this entity.
     */
    public void setContainer(CompositeEntity container)
            throws IllegalActionException, NameDuplicationException {
        if (!(container instanceof CompositeActor)) {
            throw new IllegalActionException(container, this,
                    "CompositeActor can only be contained by instances of " +
                    "CompositeActor.");
        }
        super.setContainer(container);
    }

    /** Set the local director for execution of this CompositeActor.
     *  If this is not at the top level of the hierarchy, calling this
     *  method with a non-null argument turns the composite into a wormhole.
     *  Calling it with a null argument reverses this.
     *  The container of the specified director is set to this composite
     *  actor, and if there was previously a local director, its container
     *  is set to null. This method is write-synchronized on the workspace.
     *
     *  @param director The Director responsible for execution.
     */
    public void setDirector(Director director) {
        try {
            workspace().getWriteAccess();
            // If there was a previous director, we need to reset it.
            if (_director != null) _director._makeDirectorOf(null);
            director._makeDirectorOf(this);
            _director = director;
        } finally {
            workspace().doneWriting();
        }
    }

    /** Set the executive director for execution of this CompositeActor.
     *  This can only be done for a composite actor that has no container.
     *  For others, the executive director is inherited from the container
     *  (via its getDirector() method).  This is used for example to specify
     *  a master control panel that supervises the execution of an application.
     *  If the executive director is explicitly specified
     *  using this method, then it is cloned when this composite is cloned.
     *  Otherwise, it is not cloned, since it is assumed that the clone
     *  will inherit its executive director from its container.
     *  This method is write-synchronized on the workspace.
     *
     *  @param execdir The executive director.
     */
    public void setExecutiveDirector(Director execdir)
            throws IllegalActionException {
        try {
            workspace().getWriteAccess();
            if (getContainer() != null && execdir != null) {
                throw new IllegalActionException(this, execdir,
                "Cannot set the executive director of an actor "
                + "with a container.");
            }
            // If there was a previous exec director, we need to reset it.
            if (_execdirector != null) _execdirector._makeExecDirectorOf(null);
            execdir._makeExecDirectorOf(this);
            _execdirector = execdir;
            return;
        } finally {
            workspace().doneWriting();
        }
    }

    /** If there is a director, invoke its wrapup() method.
     *  This method is read-synchronized on the workspace.
     *
     *  @exception IllegalActionException If there is no director,
     *   or if the director's wrapup() method throws it.
     */
    public void wrapup() throws IllegalActionException {
        try {
            workspace().getReadAccess();
            Director dir = getDirector();
            if (dir == null) {
                throw new IllegalActionException(this,
                "Cannot wrapup without a director.");
            }
            dir.wrapup();
        } finally {
            workspace().doneReading();
        }
    }

    /////////////////////////////////////////////////////////////////////////
    ////                         public variables                        ////

    /** Indicate that the description(int) method should include the
     *  director and executive director.
     */
    public static final int DIRECTOR = 512;

    ///////////////////////////////////////////////////////////////////////
    ////                      protected methods                        ////

    /** Add an actor to this container with minimal error checking.
     *  This overrides the base-class method to make sure the argument
     *  implements the Actor interface, and to register the actor with
     *  the director, if there is one. This
     *  method does not alter the actor in any way.
     *  It is <i>not</i> synchronized on the workspace, so the
     *  caller should be.
     *
     *  @param entity Actor to contain.
     *  @exception IllegalActionException If the actor has no name, or the
     *   action would result in a recursive containment structure, or the
     *   argument does not implement the Actor interface.
     *  @exception NameDuplicationException If the name collides with a name
     *   already on the actor contents list.
     */
    protected void _addEntity(ComponentEntity entity)
            throws IllegalActionException, NameDuplicationException {
        if (!(entity instanceof Actor)) {
            throw new IllegalActionException(this, entity,
            "CompositeActor can only contain entities that implement the "
            + "Actor interface.");
        }
        super._addEntity(entity);
        // Register the actor with the director.
        // NOTE: Should this be implemented using publish and subscribe,
        // e.g. using MutationListener?
        Director dir = getDirector();
        if (dir != null) {
            dir.registerNewActor((Actor)entity);
        }
    }

    /** Add a port to this actor. This overrides the base class to
     *  throw an exception if the added port is not an instance of
     *  IOPort.  This method should not be used directly.  Call the
     *  setContainer() method of the port instead. This method does not set
     *  the container of the port to point to this actor.
     *  It assumes that the port is in the same workspace as this
     *  actor, but does not check.  The caller should check.
     *  This method is <i>not</i> synchronized on the workspace, so the
     *  caller should be.
     *
     *  @param port The port to add to this actor.
     *  @exception IllegalActionException If the port class is not
     *   acceptable to this actor, or the port has no name.
     *  @exception NameDuplicationException If the port name collides with a
     *   name already in the actor.
     */
    protected void _addPort(Port port)
            throws IllegalActionException, NameDuplicationException {
        if (!(port instanceof ComponentPort)) {
            throw new IllegalActionException(this, port,
            "CompositeActor can only contain instances of IOPort.");
        }
        super._addPort(port);
    }

    /** Add a relation to this container. This method should not be used
     *  directly.  Call the setContainer() method of the relation instead.
     *  This method does not set the container of the relation to refer
     *  to this container. This method is <i>not</i> synchronized on the
     *  workspace, so the caller should be.
     *
     *  @param relation Relation to contain.
     *  @exception IllegalActionException If the relation has no name, or is
     *   not an instance of IORelation.
     *  @exception NameDuplicationException If the name collides with a name
     *   already on the contained relations list.
     */
    protected void _addRelation(ComponentRelation relation)
            throws IllegalActionException, NameDuplicationException {
        if (!(relation instanceof IORelation)) {
            throw new IllegalActionException(this, relation,
            "CompositeActor can only contain instances of IORelation.");
        }
        super._addRelation(relation);
    }

    /** Return a description of the object.  The level of detail depends
     *  on the argument, which is an or-ing of the static final constants
     *  defined in the NamedObj class and this class.  Lines are indented
     *  according to to the level argument using the protected method
     *  _getIndentPrefix(). Zero, one or two brackets can be specified
     *  to surround the returned description.  If one is specified it is
     *  the leading bracket. This is used by derived classes that will
     *  append to the description. Those derived classes are responsible
     *  for the closing bracket. An argument other than 0, 1, or 2 is
     *  taken to be equivalent to 0.
     *  This method is <i>not</i> read-synchronized on the workspace,
     *  so the caller should be.
     *
     *  @param detail The level of detail.
     *  @param indent The amount of indenting.
     *  @param bracket The number of surrounding brackets (0, 1, or 2).
     *  @return A description of the object.
     */
    protected String _description(int detail, int indent, int bracket){
        try {
            workspace().getReadAccess();
            String result;
            if (bracket == 1 || bracket == 2) {
                result = super._description(detail, indent, 1);
            } else {
                result = super._description(detail, indent, 0);
            }
            if ((detail & DIRECTOR) != 0) {
                if (result.trim().length() > 0) {
                    result += " ";
                }
                result += "director {\n";
                Director dir = getDirector();
                if (dir != null) {
                    result += dir._description(detail, indent+1, 2);
                    result += "\n";
                }
                result += _getIndentPrefix(indent) + "} executivedirector {\n";
                dir = getExecutiveDirector();
                if (dir != null) {
                    result += dir._description(detail, indent+1, 2);
                }
                result += _getIndentPrefix(indent) + "}";
            }
            if (bracket == 2) result += "}";
            return result;
        } finally {
            workspace().doneReading();
        }
    }

    /** Remove the specified actor. This method should not be used
     *  directly.  Call the setContainer() method of the actor instead with
     *  a null argument.
     *  The actor is assumed to be contained by this composite (otherwise,
     *  nothing happens). This does not alter the actor in any way.
     *  This method is <i>not</i> synchronized on the workspace, so the
     *  caller should be.
     *  @param entity The actor to remove.
     */
    protected void _removeEntity(ComponentEntity entity) {
        super._removeEntity(entity);
        // Deregister the actor with the director.
        // NOTE: Should this be implemented using publish and subscribe,
        // e.g. using MutationListener?
        Director dir = getDirector();
        if (dir != null) {
            dir.deregisterNewActor((Actor)entity);
        }
        
    }

    /////////////////////////////////////////////////////////////////////
    ////                         private variables                      ////

    private Director _director, _execdirector;

    // Cached lists of input and output ports.
    private transient long _inputPortsVersion = -1;
    private transient LinkedList _cachedInputPorts;
    private transient long _outputPortsVersion = -1;
    private transient LinkedList _cachedOutputPorts;
}
