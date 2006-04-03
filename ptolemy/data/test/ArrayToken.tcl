# Tests for the ArrayToken class
#
# @Author: Yuhong Xiong, contributor: Christopher Brooks
#
# @Version $Id$
#
# @Copyright (c) 1997-2006 The Regents of the University of California.
# All rights reserved.
# 
# Permission is hereby granted, without written agreement and without
# license or royalty fees, to use, copy, modify, and distribute this
# software and its documentation for any purpose, provided that the
# above copyright notice and the following two paragraphs appear in all
# copies of this software.
# 
# IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY
# FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES
# ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
# THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
# SUCH DAMAGE.
# 
# THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY WARRANTIES,
# INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
# MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
# PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
# CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
# ENHANCEMENTS, OR MODIFICATIONS.
# 
# 						PT_COPYRIGHT_VERSION_2
# 						COPYRIGHTENDKEY
#######################################################################

# Ptolemy II test bed, see $PTII/doc/coding/testing.html for more information.

# Load up the test definitions.
if {[string compare test [info procs test]] == 1} then { 
    source testDefs.tcl
} {}

# Uncomment this to get a full report, or set in your Tcl shell window.
# set VERBOSE 1

######################################################################
####
# 
test ArrayToken-1.0 {Create a string array} {
    set val0 [java::new ptolemy.data.StringToken AB]
    set val1 [java::new ptolemy.data.StringToken CD]
    set valArray [java::new {ptolemy.data.Token[]} 2 [list $val0 $val1]]
    set valToken [java::new {ptolemy.data.ArrayToken} $valArray]

    $valToken toString
} {{"AB", "CD"}}

######################################################################
####
# 
test ArrayToken-1.1 {Create an int array using expression} {
    set valToken [java::new {ptolemy.data.ArrayToken String} "{1, 2, 3}"]
    $valToken toString
} {{1, 2, 3}}

test ArrayToken-1.2 {trigger exception when creating empty array using wrong constructor} {
    set valArray [java::new {ptolemy.data.Token[]} 0 ]
    catch {java::new {ptolemy.data.ArrayToken} $valArray} msg

    list $msg
} {{ptolemy.kernel.util.IllegalActionException: The length of the specified array is zero.}}

######################################################################
####
# 
test ArrayToken-1.3 {Create an int array with conversion} {
    set valToken [java::new {ptolemy.data.ArrayToken String} "{1, 2, 3.0}"]
    catch {java::new {ptolemy.data.ArrayToken String} "1.0"} errMsg
    list [$valToken toString] $errMsg
} {{{1.0, 2.0, 3.0}} {ptolemy.kernel.util.IllegalActionException: An array token cannot be created from the expression '1.0'}}

######################################################################
####
# 
test ArrayToken-1.4 {Create an array of nil DoubleTokens} {
    set val0 [java::field ptolemy.data.Token NIL]
    set val1 [java::field ptolemy.data.Token NIL]
    set valArray [java::new {ptolemy.data.Token[]} 2 [list $val0 $val1]]
    set valToken [java::new {ptolemy.data.ArrayToken} $valArray]

    $valToken toString
} {{nil, nil}}

######################################################################
####
# 
test ArrayToken-1.5 {Create an array of DoubleTokens, first one nil} {
    set val0 [java::field ptolemy.data.Token NIL]
    set val1 [java::new ptolemy.data.DoubleToken 2.0]
    set valArray [java::new {ptolemy.data.Token[]} 2 [list $val0 $val1]]
    set valToken [java::new {ptolemy.data.ArrayToken} $valArray]

    $valToken toString
} {{nil, 2.0}}

######################################################################
####
#
test ArrayToken-1.6 {Create an array of DoubleTokens, second one nil} {
    set val0 [java::new ptolemy.data.DoubleToken 2.0]
    set val1 [java::field ptolemy.data.Token NIL]
    set valArray [java::new {ptolemy.data.Token[]} 2 [list $val0 $val1]]
    set valToken [java::new {ptolemy.data.ArrayToken} $valArray]

    $valToken toString
} {{2.0, nil}}

######################################################################
####
# 
test ArrayToken-1.7.1 {Create a nil ArrayToken} {
    set valToken [java::field ptolemy.data.Token NIL]
    set valToken2 [java::new {ptolemy.data.ArrayToken ptolemy.data.Token} \
		      [java::null]]
    list [$valToken toString] [$valToken isNil] \
	[$valToken2 toString] [$valToken2 isNil]
} {nil 1 {{}} 0}

######################################################################
####
# 
test ArrayToken-1.7.2 {Create a Double ArrayToken} {
    set val0 [java::new ptolemy.data.DoubleToken 2.0]
    set valToken [java::new {ptolemy.data.ArrayToken ptolemy.data.Token} \
		      $val0]
    list [$valToken toString] [$valToken isNil]
} {{{}} 0}


######################################################################
####
# 
test ArrayToken-2.0 {test add} {
    set t1 [java::new {ptolemy.data.ArrayToken String} "{1, 2, 3}"]
    set t2 [java::new {ptolemy.data.ArrayToken String} "{4, 5, 6}"]
    set t3 [java::new {ptolemy.data.IntToken String} "5"]
    set tadd [$t1 add $t2]
    set tadd2 [$t1 elementAdd $t3]
    list [$tadd toString] [$tadd2 toString]
} {{{5, 7, 9}} {{6, 7, 8}}}

######################################################################
####
# 
test ArrayToken-2.0.2 {test add with a nil} {
    set t1 [java::new {ptolemy.data.ArrayToken String} "{1, 2, nil}"]
    set t2 [java::new {ptolemy.data.ArrayToken String} "{4, 5, 6}"]
    set t3 [java::new {ptolemy.data.IntToken String} "5"]
    set tadd [$t1 add $t2]
    set tadd2 [$t1 elementAdd $t3]
    list [$tadd toString] [$tadd2 toString]
} {{{5, 7, nil}} {{6, 7, nil}}}

######################################################################
####
# 
test ArrayToken-2.1 {test subtract} {
    set t1 [java::new {ptolemy.data.ArrayToken String} "{nil, 2, 3}"]
    set t2 [java::new {ptolemy.data.ArrayToken String} "{0.5, 1.5, 6.0}"]
    set t3 [java::new {ptolemy.data.IntToken String} "5"]
    set tadd [$t1 subtract $t2]
    set tadd2 [$t1 elementSubtract $t3]
    list [$tadd toString] [$tadd2 toString]
} {{{nil, 0.5, -3.0}} {{nil, -3, -2}}}

######################################################################
####
# 
test ArrayToken-2.2 {test multiply} {
    set t1 [java::new {ptolemy.data.ArrayToken String} "{1, nil, 3}"]
    set t2 [java::new {ptolemy.data.ArrayToken String} "{0.5, 1.5, 6.0}"]
    set t3 [java::new {ptolemy.data.IntToken String} "5"]
    set tadd [$t1 multiply $t2]
    set tadd2 [$t1 elementMultiply $t3]
    list [$tadd toString] [$tadd2 toString]
} {{{0.5, nil, 18.0}} {{5, nil, 15}}}

######################################################################
####
#
test ArrayToken-2.3 {test divide} {
    set t1 [java::new {ptolemy.data.ArrayToken String} "{1, 3, 3}"]
    set t2 [java::new {ptolemy.data.ArrayToken String} "{0.5, 1.5, 6.0}"]
    set t3 [java::new {ptolemy.data.IntToken String} "5"]
    set tadd [$t1 divide $t2]
    set tadd2 [$t1 elementDivide $t3]
    list [$tadd toString] [$tadd2 toString]
} {{{2.0, 2.0, 0.5}} {{0, 0, 0}}}

######################################################################
####
# 
test ArrayToken-2.4 {test modulo} {
    set t1 [java::new {ptolemy.data.ArrayToken String} "{-1, 1, 5}"]
    set t2 [java::new {ptolemy.data.ArrayToken String} "{3.0, 3.0, -3.0}"]
    set t3 [java::new {ptolemy.data.IntToken String} "5"]
    set tadd [$t1 modulo $t2]
    set tadd2 [$t1 elementModulo $t3]
    list [$tadd toString] [$tadd2 toString]
} {{{-1.0, 1.0, 2.0}} {{-1, 1, 0}}}

######################################################################
####
# 
test ArrayToken-3.0 {test equals on an array of Doubles} {
    set t1 [java::new {ptolemy.data.ArrayToken String} "{0.5, 1.5, 6.0}"]
    set t2 [java::new {ptolemy.data.ArrayToken String} "{0.5, 1.5, 6.0}"]
    set t3 [java::new {ptolemy.data.ArrayToken String} "{0.5, 1.5, -6.0}"]
    set t4 [java::new {ptolemy.data.ArrayToken String} "{0.5, 1.5, nil}"]

    list [$t1 equals $t1] [$t1 equals $t2] [$t1 equals $t3] [$t4 equals $t4]
} {1 1 0 0}

######################################################################
####
# 
test ArrayToken-3.1 {test hashCode on an array of Doubles} {
    set t1 [java::new {ptolemy.data.ArrayToken String} "{0.5, 1.5, 6.0}"]
    set t2 [java::new {ptolemy.data.ArrayToken String} "{0.5, 1.5, 6.0}"]
    set t3 [java::new {ptolemy.data.ArrayToken String} "{0.5, 1.5, -6.0}"]

    list [$t1 hashCode] [$t2 hashCode] [$t3 hashCode]
} {0 0 0}

######################################################################
####
# 
test ArrayToken-3.2 {test isEqualTo and isCloseTo on an array of Doubles} {
    set t1 [java::new {ptolemy.data.ArrayToken String} "{0.5, 1.5, 6.0}"]
    set t2 [java::new {ptolemy.data.ArrayToken String} "{0.5, 1.5, 6.0}"]
    set t3 [java::new {ptolemy.data.ArrayToken String} "{0.5, 1.5, -6.0}"]
    set t4 [java::new {ptolemy.data.ArrayToken String} "{0.5, 1.5, nil}"]

    set res1 [$t1 {isEqualTo} $t1]
    set res2 [$t1 {isEqualTo} $t2]
    set res3 [$t1 {isEqualTo} $t3]
    set res4 [$t1 {isCloseTo} $t1]
    set res5 [$t1 {isCloseTo} $t2]
    set res6 [$t1 {isCloseTo} $t3]
    set res7 [$t4 {isCloseTo} $t4]
    list [$res1 toString] [$res2 toString] [$res3 toString] \
	    [$res4 toString] [$res5 toString] [$res6 toString] [$res7 toString]

} {true true false true true false false}

######################################################################
####
#
test ArrayToken-3.3 {test isEqualTo on an array of Complexes} {
    set t1 [java::new {ptolemy.data.ArrayToken String} \
	    "{0.5 - 10.0, 0.0 + 0.0, -10.0 + 10.0}"]

    set t2 [java::new {ptolemy.data.ArrayToken String} \
	    "{0.5 -10.0, 0.0 + 0.0, -10.0 + 10.0}"]
    set t3 [java::new {ptolemy.data.ArrayToken String} \
	    "{0.5 -10.0, 0.0 + 0.0, -10.0 + 10000.0}"]

    set res1 [$t1 {isEqualTo} $t1]
    set res2 [$t1 {isEqualTo} $t2]
    set res3 [$t1 {isEqualTo} $t3]
    set res4 [$t1 {isCloseTo} $t1]
    set res5 [$t1 {isCloseTo} $t2]
    set res6 [$t1 {isCloseTo} $t3]
    list [$res1 toString] [$res2 toString] [$res3 toString] \
	    [$res4 toString] [$res5 toString] [$res6 toString]
} {true true false true true false}


######################################################################
####
# 
test ArrayToken-4.0 {test isCloseTo on an array of Doubles} {
    # A is close to B if abs((a-b)/a)<epsilon  
    set epsilon 0.001
    set oldEpsilon [java::field ptolemy.math.Complex EPSILON]
    java::field ptolemy.math.Complex EPSILON $epsilon

    set t1 [java::new {ptolemy.data.ArrayToken String} "{0.5, 1.5, 6.0}"]

    set a [expr {0.5 - 0.05 * $epsilon}]
    set b [expr {1.5 + 0.05 * $epsilon}]
    set c [expr {6.0 + 0.05 * $epsilon}]
    set t2 [java::new {ptolemy.data.ArrayToken String} "{$a, $b, $c}"]

    set d [expr {0.5 - 2.0 * $epsilon}]
    set e [expr {1.5 + 2.0 * $epsilon}]
    set f [expr {6.0 + 2.0 * $epsilon}]
    set t3 [java::new {ptolemy.data.ArrayToken String} "{$d, $e, $f} "]

    set res1 [$t1 {isEqualTo} $t1]
    set res2 [$t1 {isEqualTo} $t2]
    set res3 [$t1 {isEqualTo} $t3]
    set res4 [$t1 {isCloseTo} $t1]
    set res5 [$t1 {isCloseTo} $t2]
    set res6 [$t1 {isCloseTo} $t3]

    java::field ptolemy.math.Complex EPSILON $oldEpsilon

    list [$res1 toString] [$res2 toString] [$res3 toString] \
	    [$res4 toString] [$res5 toString] [$res6 toString]
} {true false false true true false}

######################################################################
####
#
test ArrayToken-4.1 {test isCloseTo on an array of Complexes} {
    set epsilon 0.001
    set oldEpsilon [java::field ptolemy.math.Complex EPSILON]
    java::field ptolemy.math.Complex EPSILON $epsilon

    set t1 [java::new {ptolemy.data.ArrayToken String} \
	    "{0.5 - 10.0, 0.0 + 0.0, -10.0 + 10.0}"]

    set t2 [java::new {ptolemy.data.ArrayToken String} \
	    "{0.5 -10.0, 0.0 + 0.0, -10.0 + 10.0}"]
    set t3 [java::new {ptolemy.data.ArrayToken String} \
	    "{0.5 -10.0, 0.0 + 0.0, -10.0 + 10000.0}"]

    set res1 [$t1 {isEqualTo} $t1]
    set res2 [$t1 {isEqualTo} $t2]
    set res3 [$t1 {isEqualTo} $t3]
    set res4 [$t1 {isCloseTo} $t1]
    set res5 [$t1 {isCloseTo} $t2]
    set res6 [$t1 {isCloseTo} $t3]

    java::field ptolemy.math.Complex EPSILON $oldEpsilon

    list [$res1 toString] [$res2 toString] [$res3 toString] \
	    [$res4 toString] [$res5 toString] [$res6 toString]
} {true true false true true false}

######################################################################
####
# 
test ArrayToken-5.1 {Construct an array of doubles with a nil} {
    set t [java::new {ptolemy.data.ArrayToken String} "{1.0, nil, 2.0, NaN}"]
    set nilToken [$t getElement 1]
    #set nilDoubleToken [java::cast ptolemy.data.DoubleToken $nilToken]
    list [$t toString] [$nilToken toString] \
	[[$nilToken getType] toString]
} {{{1.0, nil, 2.0, NaN}} nil double}

######################################################################
####
# 
test ArrayToken-5.2 {Construct an array of doubles with a nil as 1st element} {
    set t [java::new {ptolemy.data.ArrayToken String} "{nil, 2.0}"]
    set nilToken [$t getElement 0]
    #set nilDoubleToken [java::cast ptolemy.data.DoubleToken $nilToken]
    list [$t toString] [$nilToken toString] \
	[[$nilToken getType] toString]

} {{{nil, 2.0}} nil double}

######################################################################
####
# 
test ArrayToken-6.1 {Construct an array of ints with a nil} {
    set t [java::new {ptolemy.data.ArrayToken String} "{1, nil, 2}"]
    set nilToken [$t getElement 1]
    #set nilIntToken [java::cast ptolemy.data.IntToken $nilToken]
    list [$t toString] [$nilToken toString] \
	[[$nilToken getType] toString]

} {{{1, nil, 2}} nil int}

######################################################################
####
# 
test ArrayToken-6.2 {Construct an array of int with a nil as 1st element} {
    set t [java::new {ptolemy.data.ArrayToken String} "{nil, 2}"]
    set nilToken [$t getElement 0]
    #set nilIntToken [java::cast ptolemy.data.IntToken $nilToken]
    list [$t toString] [$nilToken toString] \
	[[$nilToken getType] toString]
} {{{nil, 2}} nil int}

######################################################################
####
# 
test ArrayToken-7.0 {extract} {
    set t [java::new {ptolemy.data.ArrayToken String} \
	       "{\"red\",\"green\",\"blue\"}"]
    set r1 [$t extract [java::new {ptolemy.data.ArrayToken String} \
			    "{true, false, true}"]]
    set r2 [$t extract [java::new {ptolemy.data.ArrayToken String} \
			    "{2,0,1,1}"]]
    list [$r1 toString] [$r2 toString]
} {{{"red", "blue"}} {{"blue", "red", "green", "green"}}}

######################################################################
####
# 
test ArrayToken-7.1 {extract, wrong length of boolean array} {
    # Uses 7.0 above
    catch {$t extract [java::new {ptolemy.data.ArrayToken String} \
			   "{true}"]} errMsg
    list $errMsg
} {{ptolemy.kernel.util.IllegalActionException: When the argument is an array of booleans, it must have the same length as this array.}} 

######################################################################
####
# 
test ArrayToken-7.2 {extract, wrong type} {
    # Uses 7.0 above
    catch {$t extract [java::new {ptolemy.data.ArrayToken String} \
			   "{1.0}"]} errMsg
    list $errMsg
} {{ptolemy.kernel.util.IllegalActionException: The argument must be {boolean} or {int}.}} 

######################################################################
####
# 
test ArrayToken-8.0 {getElementPrototype} {
    set valToken [java::new {ptolemy.data.ArrayToken String} "{1, 2, 3}"]
    [[$valToken getElementPrototype] getType] toString
} {int}

######################################################################
####
# 
test ArrayToken-9.0 {one} {
    set valToken [java::new {ptolemy.data.ArrayToken String} "{1, 2, 3}"]
    [$valToken one] toString
} {{1, 1, 1}}


######################################################################
####
# 
test ArrayToken-9.0 {subarray} {
    set t [java::new {ptolemy.data.ArrayToken String} "{1, 2, 3, 4}"]
    catch {$t subarray -1 1} errMsg
    set r1 [$t subarray 5 3]
    set r2 [$t subarray 2 2]
    list $errMsg [$r1 toString] [$r2 toString]
} {{ptolemy.kernel.util.IllegalActionException: index argument of subarray() must be non-negative.} {{}} {{3, 4}}}
