<?xml version="1.0" standalone="no"?>
<!DOCTYPE plot PUBLIC "-//UC Berkeley//DTD MoML 1//EN"
    "http://ptolemy.eecs.berkeley.edu/xml/dtd/MoML_1.dtd">
<entity name="higher order" class="ptolemy.moml.EntityLibrary">
  <configure>
    <?moml
      <group>
      <doc>Higher-Order Computation Infrastructure.</doc>

      <entity name="MultiInstanceComposite" class="ptolemy.actor.hoc.MultiInstanceComposite">
        <doc>Creates multiple instances of itself</doc>
        <property name="annotation" class="ptolemy.kernel.util.Attribute">
           <property name="_hideName" class="ptolemy.kernel.util.SingletonAttribute">
           </property>
           <property name="_iconDescription" class="ptolemy.kernel.util.SingletonConfigurableAttribute">
              <configure><svg><text x="20" y="20" style="font-size:14; font-family:SansSerif; fill:blue">Make sure there is a director here!</text></svg></configure>
           </property>
           <property name="_smallIconDescription" class="ptolemy.kernel.util.SingletonConfigurableAttribute">
              <configure>
                <svg> 
                    <text x="20" style="font-size:14; font-family:SansSerif; fill:blue" y="20">-A-</text>
                </svg>
              </configure>
           </property>
           <property name="_controllerFactory" class="ptolemy.vergil.basic.NodeControllerFactory">
            </property>
           <property name="_editorFactory" class="ptolemy.vergil.toolbox.AnnotationEditorFactory">
           </property>
           <property name="_location" class="ptolemy.kernel.util.Location" value="-5.0, 5.0">
           </property>
         </property>
      </entity>

      </group>
    ?>
  </configure>
</entity>
























