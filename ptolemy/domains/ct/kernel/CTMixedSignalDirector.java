/* A CT Director that handles the interaction with event based domains.

 Copyright (c) 1998-2000 The Regents of the University of California.
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
@ProposedRating Green (liuj@eecs.berkeley.edu)
@AcceptedRating Yellow (johnr@eecs.berkeley.edu)
*/

package ptolemy.domains.ct.kernel;

import ptolemy.domains.ct.kernel.util.TotallyOrderedSet;
import ptolemy.kernel.util.*;
import ptolemy.actor.Actor;
import ptolemy.actor.CompositeActor;
import ptolemy.actor.TypedCompositeActor;
import ptolemy.actor.Director;
import ptolemy.actor.IOPort;
import ptolemy.data.expr.Parameter;
import ptolemy.data.type.BaseType;
import ptolemy.data.DoubleToken;
import java.util.Iterator;


//////////////////////////////////////////////////////////////////////////
//// CTMixedSignalDirector
/**
This a CTDirector that supports the interaction of the continuous-time
simulation with event-based domains. This director can both serve as
a top-level director and an embedded director that is contained by
a composite actor in an event-based domain. If it is a top-level
director, it acts exactly like a CTMultiSolverDirector. If it is
embedded in another event-based domain, it will run ahead of the global
time and prepare to roll back if necessary.
<P>
This class has an additional parameter than the CTMultiSolverDirector,
which is the maximum run ahead of time length (<code>runAheadLength</code>).
The default value is 1.0.
<P>
The run ahead of time is achieved by the following mechanism.<Br>
<UL>
<LI> At the initialize stage of the execution, the director will request
a fire at the global current time.
<LI> At each prefire stage the execution, the fire end time is computed
based on the current time of the executive director, t1, the next iteration
time of the executive director, t2, the value of the parameter
<code>runAheadLength</code>, t3. The fire end time is t1+min(t2, t3)
<LI> At the prefire stage, the local current time is compared with the
current time of the executive director. If the local time is later than
the executive director time, then the directed system will rollback to a
"known good" state.
<LI> The "known good" state is the state of the system at the time when
local time is equal to the current time of the executive director.
<LI> At the fire stage, the director will stop at the first of the two times,
the fire end time and the first detected event time.
</UL>

@author  Jie Liu
@version $Id$
*/
public class CTMixedSignalDirector extends CTMultiSolverDirector {
    /** Construct a director in the default workspace with an empty string
     *  as its name. The director is added to the list of objects in
     *  the workspace. Increment the version number of the workspace.
     *  All the parameters takes their default values.
     */
    public CTMixedSignalDirector() {
        super();
    }

    /** Construct a director in the  workspace with an empty name.
     *  The director is added to the list of objects in the workspace.
     *  Increment the version number of the workspace.
     *  All the parameters takes their default values.
     *  @param workspace The workspace of this object.
     */
    public CTMixedSignalDirector(Workspace workspace)  {
        super(workspace);
    }

    /**  Construct a director in the given container with the given name.
     *  If the container argument must not be null, or a
     *  NullPointerException will be thrown.
     *  If the name argument is null, then the name is set to the
     *  empty string. Increment the version number of the workspace.
     *  All the parameters takes their default values.
     *  @param workspace Object for synchronization and version tracking
     *  @param name Name of this director.
     *  @exception IllegalActionException If the director is not compatible
     *  with the specified container.  May be thrown in a derived class.
     */
    public CTMixedSignalDirector(CompositeActor container, String name)
            throws IllegalActionException {
        super(container, name);
    }

    ///////////////////////////////////////////////////////////////////
    ////                     ports and parameters                  ////

    /** parameter of default runaheadlength.
     */
    public Parameter runAheadLength;

    ///////////////////////////////////////////////////////////////////
    ////                       public variables                    ////

    /** The number of rollbacks. Used for statistics.
     */
    public int NROLL = 0;

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /** React to a change in an attribute. If the changed attribute matches
     *  a parameter of the director, then the corresponding private copy of the
     *  parameter value will be updated.
     *  @param param The changed parameter.
     *  @exception IllegalActionException If the superclass throws it.
     */
    public void attributeChanged(Parameter param)
            throws IllegalActionException {
        if(param == runAheadLength) {
            if(_debugging) _debug("run ahead time updating.");
            _runAheadLength = ((DoubleToken)param.getToken()).doubleValue();
        } else {
            super.attributeChanged(param);
        }
    }

    /** Return true since this director can be an inside director.
     *  @return True always.
     */
    public boolean canBeInsideDirector() {
        return true;
    }

    /** Return the end time of this director's firing.
     *
     *  @return The fire end time.
     */
    public final double getIterationEndTime() {
        return _iterationEndTime;
    }

    /** Return the time of the outside domain. If this is the top level
     *  return the current time.
     *  @return The outside current time.
     */
    public double getOutsideTime() {
        if(_isTopLevel()) {
            return getCurrentTime();
        }
        return _outsideTime;
    }

    /** Execute the directed (sub)system to the fire end time.
     *  If this is a top-level director, the fire end time if the
     *  current time at the beginning of the fire() method plus the
     *  the step size of one successful step.
     *  Otherwise, it executes until one of the following conditions
     *  is satisfied. 1) The fire end time computed in the prefire()
     *  method is reached. 2) An event is generated.
     *  It saves the state of the system at the current time of the executive
     *  director as the "known good" state, and runs ahead of that time.
     *  The "known good" state is used for roll back.
     *  @exception IllegalActionException If thrown by the ODE solver,
     *       or the prefire(), fire(), or postfire() methods of an actor.
     */
    public void fire() throws IllegalActionException {
        if(_isTopLevel()) {
            super.fire();
            return;
        }
        CompositeActor ca = (CompositeActor) getContainer();
        Director exe = ca.getExecutiveDirector(); // it must not be null.
        if (_isEventPhase()) {
            if(_debugging) _debug(getFullName(),
                    "In event phase execution.");
            _eventPhaseExecution();
            _setEventPhase(false);
            if(_debugging) _debug(getFullName(),
                    "Request a refire at the current time." +
                    exe.getCurrentTime(),
                    "--END of fire");
            exe.fireAt(ca, exe.getCurrentTime());
            return;
        } else {
            while(true) {
                if(isBPIteration()) {
                    // Just after a breakpoint iteration. This is the known
                    // good state.
                    _markStates();
                }
                _setIterationBeginTime(getCurrentTime());
                // guarantee to stop at the iteration end time.
                fireAt(null, getIterationEndTime());
                //Refine step size
                setCurrentStepSize(getSuggestedNextStepSize());
                _processBreakpoints();
                if(_debugging) _debug(getName(),
                        "Resolved stepsize: " + getCurrentStepSize() +
                        " One iteration from " + getCurrentTime() +
                        " to " + (getCurrentStepSize()+getCurrentTime()));
                _fireOneIteration();
                if (_isStoppedByEvent()) {
                    if(_debugging) {
                        _debug(getFullName() + " fire stopped by event.",
                                "at " + getCurrentTime(),
                                "request refire at " + getCurrentTime(),
                                "set Event phase to TRUE");
                    }
                    exe.fireAt(ca, getCurrentTime());
                    _setEventPhase(true);
                    return;
                } else if (Math.abs(getCurrentTime()- getIterationEndTime())
                        < getTimeResolution()) {
                    if(_debugging) {
                        _debug(getFullName() + " fire stopped regularly.",
                                "at " + getCurrentTime(),
                                "request refire at " + getIterationEndTime(),
                                "set Event phase to FALSE");
                    }
                    exe.fireAt(ca, getIterationEndTime());
                    _setEventPhase(false);
                    return;
                }
            }
        }
    }

    /** In addition to initialize the execution as in CTMultiSolverDirector,
     *  register a fire-at-current-time to the executive director,
     *  if this director is not at the top-level.
     *  @see CTMultiSolverDirector#initialize()
     *  @exception IllegalActionException If this director has no container or
     *       no scheduler, or thrown by a contained actor.
     */
    public void initialize() throws IllegalActionException {
        if(_debugging) _debug(getFullName() + " initialize.");
        super.initialize();
        if(!_isTopLevel()) {
            TypedCompositeActor ca = (TypedCompositeActor)getContainer();
            Director exe = ca.getExecutiveDirector();
            exe.fireAt(ca, getCurrentTime());
        }
    }

    /** If this is a top-level director, behave exactly as a
     *  CTMultiSolverDirector, otherwise return true always.
     *  @return True if this is not a top-level director or the simulation
     *     is not finished.
     *  @exception IllegalActionException Not thrown in this base class.
     */
    public boolean postfire() throws IllegalActionException {
        if(_isTopLevel()) {
            return super.postfire();
        } else {
            return true;
        }
    }

    /** Returns true always, indicating that the (sub)system is always ready
     *  for one iteration. The schedule is recomputed if there are mutations
     *  occurred after last iteration. Note that mutations can only
     *  occur between iterations in the CT domain.
     *  <P>
     *  If this is not a top-level director, some additional work is done
     *  to synchronize time with the executive director. In particular,
     *  it will compare its local time, say t, with the current time
     *  of the executive director, say t0.
     *  If t == t0, do nothing. <BR>
     *  If t > t0, then rollback to the "known good" time, which should be
     *  less than the outside time. And catch up
     *  to the outside time. <BR>
     *  If t < t0, then throw an exception because the CT subsystem
     *  should always run ahead of time. <BR>
     *  <P>
     *  The iteration end time is computed. If this is a
     *  top-level director, the iteration end time is the (local) current
     *  time plus the (local) current step size.
     *  If this director is not a top-level director, the time is
     *  resolved from the current time of the outside domains, say t1,
     *  the next iteration time of the outside domain, say t2,
     *  the runAheadLength parameter of this director, say t3.
     *  The iteration end time is set
     *  to be <code>t1 + min(t2, t3)</code>. The iteration end time may be
     *  further refined by the fire() method due to event detection.
     *  In particular, when the first event is detected, say at t4,
     *  then the iteration end time is set to t4.
     *  @return true Always
     *  @exception IllegalActionException If the local time is
     *       less than the current time of the executive director,
     *       or thrown by a directed actor.
     */
    public boolean prefire() throws IllegalActionException {
        //System.out.println(getName() + " currrent director time " +
        //        getCurrentTime());

        super.prefire(); // always returns true.
        if(!_isTopLevel()) {
            // synchronize time.
            CompositeActor ca = (CompositeActor) getContainer();
            // ca should have beed checked in _isTopLevel()
            Director exe = ca.getExecutiveDirector();
            _outsideTime = exe.getCurrentTime();
            double timeAcc = getTimeResolution();
            double nextIterTime = exe.getNextIterationTime();
            double runlength = nextIterTime - _outsideTime;
            if(_debugging) _debug(getName(), "Outside Time = " + _outsideTime,
                    "NextIterationTime = " + nextIterTime,
                    "Inferred run length = " + runlength);
            if(runlength < 0 ) {
                throw new InvalidStateException(this, "Outside domain"
                        + " time going backward."
                        + " Current time = " + _outsideTime
                        + ", but the next iteration time = " + nextIterTime);
            }
            if (runlength == 0 ) {
                // This only happens when the current time of the outside
                // domain is the stop time. So return false and stop 
                // executing.
                return false;
            }
    
            if(_debugging) _debug( "Current Time " + getCurrentTime()
                    + "Outside domain current time " + _outsideTime
                    + " next iteration time " + nextIterTime
                    + "run length "+ runlength);

            // Synchronization, handle round up error.
            if(runlength < timeAcc) {
                exe.fireAt(ca, nextIterTime);
                if(_debugging) _debug("Next iteration is too near" +
                        " (but not sync). Request a refire at:"+nextIterTime);
                return false;
            }
            if(Math.abs (_outsideTime -getCurrentTime()) < timeAcc) {
                if(_debugging) _debug("Round up current time " +
                        getCurrentTime() + " to outside time " +_outsideTime);
                setCurrentTime(_outsideTime);
            }
            if (_outsideTime > getCurrentTime()) {
                throw new IllegalActionException(this, exe,
                        " time collapse.");
            }
            // Check for rollback.
            if (_outsideTime < getCurrentTime()) {
                if(_debugging) _debug(getName() + " rollback from: " +
                        getCurrentTime() + " to: " +_knownGoodTime +
                        "due to outside time " +_outsideTime );
                if(STAT) {
                    NROLL ++;
                }
                _rollback();
                // set a catch-up destination time.
                fireAt(null, _outsideTime);
                _catchUp();
            }
            if(runlength < _runAheadLength) {
                _setIterationEndTime(nextIterTime);
            } else {
                _setIterationEndTime(_outsideTime + _runAheadLength );
            }
            // Now it's guaranteed that the current time is the outside time.
            if(_debugging) _debug(getName(), "Iteration end time = " +
                    getIterationEndTime(), "End of Prefire");
        }
        return true;
    }

    /** Return true if it
     *  transfers data from an input port of the container to the
     *  ports it is connected to on the inside.  The port argument must
     *  be an opaque input port.  If any channel of the input port
     *  has no data, then that channel is ignored. The execution
     *  phase is set to event phase if there's any data transferred.
     *
     *  @exception IllegalActionException If the port is not an opaque
     *   input port.
     *  @param port The port to transfer tokens from.
     *  @return True if data are transferred.
     */
    public boolean transferInputs(IOPort port)
            throws IllegalActionException {
        boolean trans = super.transferInputs(port);
        if (trans) {
            _setEventPhase(true);
        }
        return trans;
    }

    ////////////////////////////////////////////////////////////////////////
    ////                         protected methods                      ////

    /** Catch up the simulation from a known good state to the outside
     *  current time. There should be no breakpoints of any kind
     *  in this process. If the current time is greater than or equal
     *  to the outside time, then do nothing.
     *  @exception IllegalActionException If thrown from the execution
     *  method from any actor.
     */
    protected void _catchUp() throws IllegalActionException {
        if (getCurrentTime() >= getOutsideTime()) {
            return;
        }
        _setIterationBeginTime(getCurrentTime());
        while(getCurrentTime() < (getOutsideTime()-getTimeResolution())) {
            setCurrentStepSize(getSuggestedNextStepSize());
            _processBreakpoints();
            _fireOneIteration();
        }
        if(_debugging)
            _debug(getFullName() + " Catch up time" + getCurrentTime());
    }

    /** Initialize parameters in addition to the parameters inherited
     *  from CTMultiSolverDirector. In this class the additional
     *  parameter is the maximum run ahead time length
     *  (<code>runAheadLength</code>). The default value is 1.0.
     */
    protected void _initParameters() {
        super._initParameters();
        try {
            _runAheadLength = 1.0;
            runAheadLength = new Parameter(this,
                    "runAheadLength", new DoubleToken(_runAheadLength));
            runAheadLength.setTypeEquals(BaseType.DOUBLE);
        } catch (IllegalActionException e) {
            //Should never happens. The parameters are always compatible.
            throw new InternalErrorException("Parameter creation error.");
        } catch (NameDuplicationException ex) {
            throw new InvalidStateException(this,
                    "Parameter name duplication.");
        }
    }

    /** Return true if the current phase of fire is an event phase.
     *  @return True if the current phase is an event phase.
     */
    protected boolean _isEventPhase() {
        return _inEventPhase ;
    }

    /** Return true if the current fire phase is stopped due to
     *  the occurrence of events (predictable or unpredictable).
     *  @return True if the current fire phase is stopped by an event.
     */
    protected boolean _isStoppedByEvent() {
        // predictable breakpoints
        double bp;
        TotallyOrderedSet breakPoints = getBreakPoints();
        double tnow = getCurrentTime();
        if(breakPoints != null) {
            while (!breakPoints.isEmpty()) {
                bp = ((Double)breakPoints.first()).doubleValue();
                if(bp < (tnow-getTimeResolution())) {
                    // break point in the past or at now.
                    breakPoints.removeFirst();
                } else if(Math.abs(bp-tnow) < getTimeResolution() &&
                        bp < getIterationEndTime()){
                    // break point now! stoped by event
                    return true;
                } else {
                    break;
                }
            }
        }
        // unpredictable breakpoints. Detecte current events.
        CTScheduler sched = (CTScheduler)getScheduler();
        Iterator evgens = sched.eventGeneratorList().iterator();
        while(evgens.hasNext()) {
            CTEventGenerator evg = (CTEventGenerator) evgens.next();
            if(evg.hasCurrentEvent()) {
                return true;
            }
        }
        return false;
    }

    /**Return true if this is a top-level director. A syntax sugar.
     * @return True if this director is at the top-level.
     */
    protected final boolean _isTopLevel() {
        long version = workspace().getVersion();
        if (version == _mutationVersion) {
            return _isTop;
        }
        try {
            workspace().getReadAccess();
            CompositeActor container = (CompositeActor)getContainer();
            if(container.getExecutiveDirector() == null) {
                _isTop = true;
            } else {
                _isTop = false;
            }
            _mutationVersion = version;
        } finally {
            workspace().doneReading();
            return _isTop;
        }
    }

    /** Mark the current state as the known good state. Call the
     *  markStates() method on all CTStatefulActors. Save the current time
     *  as the "known good" time.
     */
    protected void _markStates() {
        CTScheduler scheduler = (CTScheduler) getScheduler();
        Iterator memactors = scheduler.statefulActorList().iterator();
        while(memactors.hasNext()) {
            CTStatefulActor mem = (CTStatefulActor)memactors.next();
            if(_debugging) _debug("Save State..."+
                    ((Nameable)mem).getName());
            mem.markState();
        }
        _knownGoodTime = getCurrentTime();
    }

    /** Rollback the system to a "known good" state. All the actors with
     *  states are called to restore their saved states. The
     *  current time of the director is set to the time of the "known
     *  good" state.
     *  @exception IllegalActionException If thrown by the goToMarkedState()
     *       method of an actor.
     */
    protected void _rollback() throws IllegalActionException{
        CTScheduler scheduler = (CTScheduler) getScheduler();
        Iterator memactors = scheduler.statefulActorList().iterator();
        while(memactors.hasNext()) {
            CTStatefulActor mem = (CTStatefulActor)memactors.next();
            if(_debugging) _debug("Restore State..."+
                    ((Nameable)mem).getName());
            mem.goToMarkedState();
        }
        setCurrentTime(_knownGoodTime);
    }

    /** True argument sets the phase to be event phase.
     *  @param eventPhase True to set the current phase to an event phase.
     */
    protected void _setEventPhase(boolean eventPhase) {
        _inEventPhase = eventPhase;
    }

    /** Set the stop time for this iteration.
     *  @param The fire end time.
     */
    protected void _setIterationEndTime(double time ) {
        if(time < getCurrentTime()) {
            throw new InvalidStateException(this,
                    " Fire end time" + time + " is less than" +
                    " the current time." + getCurrentTime());
        }
        _iterationEndTime = time;
    }

    ///////////////////////////////////////////////////////////////////
    ////                         private variables                 ////

    // version of mutation. If this version is not the workspace
    // version then every thing related to mutation need to be updated.
    private long _mutationVersion = -1;

    // Illustrate if this is the top level director.
    private boolean _isTop;

    // The time for the "known good" state.
    private double _knownGoodTime;

    // The current outside time.
    private double _outsideTime;

    // variable of runaheadlength
    private double _runAheadLength;

    // the end time of a fire.
    private double _iterationEndTime;

    // whether in the emit event phase;
    private boolean _inEventPhase = false;
}
