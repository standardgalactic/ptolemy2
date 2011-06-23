/*
 Task that will be used to execute a Ptolemy simulation.

 Copyright (c) 2011 The Regents of the University of California.
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

package ptserver.control;

import java.net.URL;

import ptolemy.actor.Manager;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.KernelException;
import ptserver.communication.RemoteModel;
import ptserver.communication.RemoteModel.RemoteModelType;

///////////////////////////////////////////////////////////////////
//// SimulationTask

/** Launch the simulation on the current thread under the provided
 *  ticket reference and wait for the user to issue control commands.
 *
 *  @author Justin Killian
 *  @version $Id$
 *  @since Ptolemy II 8.0
 *  @Pt.ProposedRating Red (jkillian)
 *  @Pt.AcceptedRating Red (jkillian)
 */
public class SimulationTask implements Runnable {

    /** Create an instance of the simulation task to be run by the Ptolemy
     *  server application.
     *  @param ticket Reference to the simulation request.
     *  @exception Exception If the simulation encounters a problem setting
     *  the director or getting workspace access.
     */
    public SimulationTask(Ticket ticket) throws Exception {
        _remoteModel = new RemoteModel(RemoteModelType.SERVER);

        // Set the MQTT client and load the model specified within the ticket.
        getRemoteModel().loadModel(new URL(ticket.getModelUrl()));
    }

    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /** Close the remote model.
     */
    public void close() {
        getRemoteModel().close();
    }

    /** Start the execution of the simulation by kicking off the thread.
     */
    public void run() {
        try {
            getRemoteModel().getTopLevelActor().getManager().execute();
        } catch (IllegalActionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (KernelException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /** Get the manager responsible for coordinating the model of computation.
     *  @return The Manager used to control the simulation
     */
    public Manager getManager() {
        return getRemoteModel().getManager();
    }

    /** Return the task's remote model.
     *  @return the remoteModel of the instance.
     */
    public RemoteModel getRemoteModel() {
        return _remoteModel;
    }

    ///////////////////////////////////////////////////////////////////
    ////                private variables

    /** The remote model that is used to replaced model actors.
     */
    private final RemoteModel _remoteModel;
}
