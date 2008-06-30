/*
@Copyright (c) 2008 The Regents of the University of California.
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
package ptolemy.domains.ptides.kernel;

import java.util.Comparator;
import java.util.TreeSet;

import ptolemy.actor.AbstractReceiver;
import ptolemy.actor.Actor;
import ptolemy.actor.Director;
import ptolemy.actor.IOPort;
import ptolemy.actor.NoRoomException;
import ptolemy.actor.NoTokenException;
import ptolemy.actor.util.Time;
import ptolemy.data.DoubleToken;
import ptolemy.data.IntToken;
import ptolemy.data.Token;
import ptolemy.domains.de.kernel.DEEvent;
import ptolemy.kernel.util.IllegalActionException;

/**
 * Receivers in the Ptides domain use a this timed queue to sort and events in
 * the receivers.
 * 
 * @author Patricia Derler
 */
public class PrioritizedTimedQueue extends AbstractReceiver {
	/**
	 * Construct an empty queue with no container.
	 */
	public PrioritizedTimedQueue() {
	}

	/**
	 * Construct an empty queue with the specified IOPort container.
	 * 
	 * @param container
	 *            The IOPort that contains this receiver.
	 * @exception IllegalActionException
	 *                If this receiver cannot be contained by the proposed
	 *                container.
	 */
	public PrioritizedTimedQueue(IOPort container)
			throws IllegalActionException {
		super(container); 
	}

	/**
	 * Construct an empty queue with the specified IOPort container and
	 * priority.
	 * 
	 * @param container
	 *            The IOPort that contains this receiver.
	 * @param priority
	 *            The priority of this receiver.
	 * @exception IllegalActionException
	 *                If this receiver cannot be contained by the proposed
	 *                container.
	 */
	public PrioritizedTimedQueue(IOPort container, int priority)
			throws IllegalActionException {
		super(container);
		_priority = priority; 
	}

	// /////////////////////////////////////////////////////////////////
	// // public methods ////
	
	@Override
	public void clear() throws IllegalActionException {
	    super.clear();
	    _queue.clear();
	}

	/**
	 * Take the the oldest token off of the queue and return it. If the queue is
	 * empty, throw a NoTokenException. If there are other tokens left on the
	 * queue after this removal, then set the receiver time of this receiver to
	 * equal that of the next oldest token. Update the TimeKeeper that manages
	 * this PrioritizedTimedQueue. If there are any receivers in the TimeKeeper
	 * with receiver times of PrioritizedTimedQueue.IGNORE, remove the first
	 * token from these receivers.
	 * 
	 * @return The oldest token off of the queue.
	 * @exception NoTokenException
	 *                If the queue is empty.
	 */
	public Token get() {
		Token token;
		Event event = (Event) _queue.first();
		_queue.remove(event); 
		token = event.getToken();
		return token;
	}
	
	// TODO: remove
	public Token getNewestButDontRemove() {
        Token token;
        Event event = null;
        while (_queue.size() > 0) {
            event = (Event) _queue.first(); 
            _queue.remove(_queue.first());
        } 
            _queue.add(event);
        token = event.getToken();
        return token;
    }

	/**
	 * Similar to get() but if not only token but also time stamp is required,
	 * this method is used.
	 * 
	 * @return Event containing token and time stamp.
	 */
	public Event getEvent() {
		Event event = (Event) _queue.first();
		_queue.remove(event); 
		return event;
	}

	/**
	 * Get the queue capacity of this receiver.
	 * 
	 * @return The capacity of this receiver's queue.
	 * @see #setCapacity(int)
	 */
	public int getCapacity() {
		return _queue.size();
	}

	/**
	 * Return true if the number of tokens stored in the queue is less than the
	 * capacity of the queue. Return false otherwise.
	 * 
	 * @return True if the queue is not full; return false otherwise.
	 */
	public boolean hasRoom() {
		return true;
		// implement bounds on ressources here
	}

	/**
	 * Return true if the queue capacity minus the queue size is greater than
	 * the argument.
	 * 
	 * @param numberOfTokens
	 *            The number of tokens to put into the queue.
	 * @return True if the queue is not full; return false otherwise.
	 * @exception IllegalArgumentException
	 *                If the argument is not positive. This is a runtime
	 *                exception, so it does not need to be declared explicitly.
	 */
	public boolean hasRoom(int numberOfTokens) throws IllegalArgumentException {
		return true;
		// implement bounds on ressources here
	}

	/**
	 * Return true if there are tokens stored on the queue. Return false if the
	 * queue is empty.
	 * 
	 * @return True if the queue is not empty; return false otherwise.
	 */
	public boolean hasToken() {
		return _queue.size() > 0;
	}

	/**
	 * Returns true if there is a token with the specified time stamp.
	 * 
	 * @param time
	 *            Time for which a token is required.
	 * @return True if the first element in the queue has the specified time
	 *         stamp.
	 */
	public boolean hasToken(Time time) {
		if (_queue.size() == 0)
			return false;
		return (((Event) _queue.first())._timeStamp.equals(time));
	}

	/**
	 * Return true if queue size is at least the argument.
	 * 
	 * @param numberOfTokens
	 *            The number of tokens to get from the queue.
	 * @return True if the queue has enough tokens.
	 * @exception IllegalArgumentException
	 *                If the argument is not positive. This is a runtime
	 *                exception, so it does not need to be declared explicitly.
	 */
	public boolean hasToken(int numberOfTokens) throws IllegalArgumentException {
		if (numberOfTokens < 1) {
			throw new IllegalArgumentException(
					"hasToken() requires a positive argument.");
		}

		return (_queue.size() > numberOfTokens);
	}

	/**
	 * Throw an exception, since this method is not used in DDE.
	 * 
	 * @param token
	 *            The token to be put to the receiver.
	 * @exception NoRoomException
	 *                If the receiver is full.
	 */
	public void put(Token token) {
		throw new NoRoomException("put(Token) is not used in the "
				+ "DDE domain.");
	}

	/**
	 * Put a token on the queue with the specified time stamp and set the last
	 * time value to be equal to this time stamp. If the queue is empty
	 * immediately prior to putting the token on the queue, then set the
	 * receiver time value to be equal to the last time value. If the queue is
	 * full, throw a NoRoomException. Time stamps can not be set to negative
	 * values that are not equal to IGNORE or INACTIVE; otherwise an
	 * IllegalArgumentException will be thrown.
	 * 
	 * @param token
	 *            The token to put on the queue.
	 * @param time
	 *            The time stamp of the token.
	 * @exception NoRoomException
	 *                If the queue is full.
	 */
	public void put(Token token, Time time) throws NoRoomException { 
		Event event = new Event(token, time); 
		try {
			_queue.add(event); // is only inserted if same event not already
								// exists
		} catch (NoRoomException e) { 
			throw e;
		}
	}

	/**
	 * Reset local flags. The local flags of this receiver impact the local
	 * notion of time of the actor that contains this receiver. This method is
	 * not synchronized so the caller should be.
	 */
	public void reset() {
		Director director = ((Actor) getContainer().getContainer()
				.getContainer()).getDirector();
  

	}

	/**
	 * Set the queue capacity of this receiver.
	 * 
	 * @param capacity
	 *            The capacity of this receiver's queue.
	 * @exception IllegalActionException
	 *                If the superclass throws it.
	 * @see #getCapacity()
	 */
	public void setCapacity(int capacity) throws IllegalActionException {
		// TODO
	}

	// /////////////////////////////////////////////////////////////////
	// // package friendly variables ////
	/**
	 * This time value is used in conjunction with completionTime to indicate
	 * that a receiver will continue operating indefinitely.
	 */
	static final double ETERNITY = -5.0;

	/** This time value indicates that the receiver contents should be ignored. */
	static final double IGNORE = -1.0;

	/** This time value indicates that the receiver is no longer active. */
	static final double INACTIVE = -2.0;

	/** The time stamp of the newest token to be placed in the queue. */
	//protected Time _lastTime;

	/** The priority of this receiver. */
	public int _priority = 0;

	// /////////////////////////////////////////////////////////////////
	// // package friendly methods ////

	/**
	 * Return true if this receiver has a NullToken at the front of the queue;
	 * return false otherwise. This method is not synchronized so the caller
	 * should be.
	 * 
	 * @return True if this receiver contains a NullToken in the oldest queue
	 *         position; return false otherwise.
	 */
	boolean _hasNullToken() {

		return false;
	}

  

	// /////////////////////////////////////////////////////////////////
	// // private variables ////

	/** The set in which this receiver stores tokens. */
	protected TreeSet<Event> _queue = new TreeSet<Event>(new TimeComparator());

	// The time stamp of the earliest token that is still in the queue.
	// private Time _receiverTime;

	/**
	 * An Event is an aggregation consisting of a Token, a time stamp and
	 * destination Receiver. Both the token and destination receiver are allowed
	 * to have null values. This is particularly useful in situations where the
	 * specification of the destination receiver may be considered redundant.
	 */
	public static class Event {

		/**
		 * Construct an Event with a token and time stamp.
		 * 
		 * @param token
		 *            Token for the event.
		 * @param time
		 *            Time stamp of the event.
		 */
		public Event(Token token, Time time) {
			_token = token;
			_timeStamp = time;
		}

		// /////////////////////////////////////////////////////////
		// // public inner methods ////

		/**
		 * Return the time stamp of this event.
		 * 
		 * @return The time stamp of the event.
		 */
		public Time getTime() {
			return _timeStamp;
		}

		/**
		 * Return the token of this event.
		 * 
		 * @return The token of the event.
		 */
		public Token getToken() {
			return _token;
		}

		// /////////////////////////////////////////////////////////
		// // private inner variables ////
		/** Time stamp of this event. */
		Time _timeStamp;

		/** Token of this event. */
		Token _token = null;
	}

	/**
	 * Compare two events according to - time stamp - value did not find a way
	 * to compare Tokens, therefore am comparing DoubleTokens and IntTokens
	 * here. If other kinds of Tokens are used, this Comparer needs to be
	 * extended.
	 * 
	 * @author Patricia Derler
	 * 
	 */
	public static class TimeComparator implements Comparator {

		/**
		 * Compare two events according to time stamps and values.
		 * 
		 * FIXME Because there is no general compare method for tokens, I
		 * implemented the comparison for int and double tokens. A more general
		 * compare is required.
		 * 
		 * @param arg0
		 *            First event.
		 * @param arg1
		 *            Second event.
		 * @return -1 if event arg0 should be processed before event arg1, 0 if
		 *         they should be processed at the same time, 1 if arg1 should
		 *         be processed before arg0.
		 */
		public int compare(Object arg0, Object arg1) {
			Event event1 = (Event) arg0;
			Event event2 = (Event) arg1;
			Time time1 = event1._timeStamp;
			Time time2 = event2._timeStamp;
			if (time1.compareTo(time2) != 0)
				return time1.compareTo(time2);

			if (event1._token instanceof DoubleToken) {
				DoubleToken token1 = (DoubleToken) event1._token;
				DoubleToken token2 = (DoubleToken) event2._token;
				if (token1.doubleValue() < token2.doubleValue())
					return -1;
				else if (token1.doubleValue() > token2.doubleValue())
					return 1;
				else
					return 0;
			} else if (event1._token instanceof IntToken) {
				IntToken token1 = (IntToken) event1._token;
				IntToken token2 = (IntToken) event2._token;
				if (token1.intValue() < token2.intValue())
					return -1;
				else if (token1.intValue() > token2.intValue())
					return 1;
				else
					return 0;
			}
			return 0;
		}

	}
}
