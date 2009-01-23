package ptolemy.apps.apes;

import java.util.HashMap;
import java.util.Map;

import ptolemy.actor.Actor;
import ptolemy.actor.NoRoomException;
import ptolemy.apps.apes.CPUScheduler.TaskState;
import ptolemy.kernel.util.IllegalActionException;

public class AccessPointCallbackDispatcher {  
        
        public native void InitializeC();
        private boolean initialized = false;
        
        public AccessPointCallbackDispatcher() { 
        }
        
        /** Map of taskNames and tasks. */
        private Map<String, Actor> _taskNames = new HashMap();
        
        public void accessPointCallback(double extime, double minNextTime) {
            System.out.println("APD.add");
            CTask task = (CTask) _taskNames.get(Thread.currentThread().getName());
            try {
                task.accessPointCallback(extime, minNextTime);
            } catch (NoRoomException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalActionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        public void addTask(CTask task) { 
            _taskNames.put(task.getName(), task);
        }

    
}
