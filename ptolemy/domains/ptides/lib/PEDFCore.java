/* This is an earliest deadline first scheduler.

@Copyright (c) 2008-2011 The Regents of the University of California.
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

package ptolemy.domains.ptides.lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import ptolemy.actor.Actor;
import ptolemy.actor.util.Time;
import ptolemy.data.IntToken;
import ptolemy.data.expr.Parameter;
import ptolemy.kernel.CompositeEntity;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.NameDuplicationException;
import ptolemy.kernel.util.NamedObj;

/** This is an earliest deadline first scheduler.
 *
 * @author Patricia Derler
   @version $Id$
   @since Ptolemy II 0.2

   @Pt.ProposedRating Red (derler)
   @Pt.AcceptedRating Red (derler)
 */
public class PEDFCore extends FPPCore {

    /** Create a new actor in the specified container with the specified
     *  name.  The name must be unique within the container or an exception
     *  is thrown. The container argument must not be null, or a
     *  NullPointerException will be thrown.
     *
     *  @param container The container.
     *  @param name The name of this actor within the container.
     *  @exception IllegalActionException If this actor cannot be contained
     *   by the proposed container (see the setContainer() method).
     *  @exception NameDuplicationException If the name coincides with
     *   an entity already in the container.
     */
    public PEDFCore(CompositeEntity container, String name)
            throws IllegalActionException, NameDuplicationException {
        super(container, name);
    }

    /** Initialize local variables.
     *  @throws IllegalActionException Thrown in super class.
     */
    @Override
    public Time initialize() throws IllegalActionException {
        super.initialize();
        _deadlines = new HashMap();
        return null;
    }

    /** Schedule a new actor for execution and return the next time
     *  this scheduler has to perform a reschedule.
     *  @param actor The actor to be scheduled.
     *  @param currentPlatformTime The current platform time.
     *  @param deadline The event deadline.
     *  @param executionTime The execution time of the actor.
     *  @return Relative time when this Scheduler has to be executed
     *    again.
     *  @throws IllegalActionException Thrown if actor paramaters such
     *    as execution time or priority cannot be read.
     */
    public Time schedule(Actor actor, Time currentPlatformTime,
            Double deadline, Time executionTime)
            throws IllegalActionException {
        if (!_currentlyExecuting.contains(actor)) {
            _deadlines.put(actor, deadline);
        }
        Time time = super.schedule(actor, currentPlatformTime, deadline,
                executionTime);
        if (lastScheduledActorFinished()) {
            _deadlines.put(actor, null);
        }
        return time;
    }

    ///////////////////////////////////////////////////////////////////
    //                      protected methods                        //

    /** Get the deadline of the actor and return it as the priority.
     *  The priority is treated equal to deadline in this scheduler:
     *  lower value for the priority means higher priority, lower
     *  deadline means higher priority.
     *  @param actor The actor.
     *  @return The priority of the actor or, if the actor has no priority
     *    assigned, the lowest priority.
     *  @throws IllegalActionException Thrown if parameter cannot be read.
     */
    protected double _getPriority(Actor actor) throws IllegalActionException {
        return _deadlines.get(actor);
    }

    ///////////////////////////////////////////////////////////////////
    //                        private methods                        //

    ///////////////////////////////////////////////////////////////////
    //                      private variables                        //

    // For every firing request store the deadline
    private HashMap<Actor, Double> _deadlines;

}
