# A second order CT system example using TclBlend
#
# @Author: Jie Liu
#
# @Version: %W%  %G%
#
# @Copyright (c) 1997 The Regents of the University of California.
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

#######################################################################
#
# This implements the example from coyote systems
# The one without lookup table. It uses the default ODE solver
# which is a Forward Euler solver.

set sys [java::new ptolemy.actor.TypedCompositeActor]
$sys setName DESystem
set man [java::new ptolemy.actor.Manager]
$sys setManager $man
set dedir [java::new ptolemy.domains.de.kernel.DECQDirector DELocalDirector]
$sys setDirector $dedir

############################################################
### CT subsystem
#
set ctsub [java::new ptolemy.actor.TypedCompositeActor $sys CTSubsystem]
set subin [java::new ptolemy.actor.TypedIOPort $ctsub Pin]
$subin setInput 1
set subout [java::new ptolemy.actor.TypedIOPort $ctsub Pout]
$subout setOutput 1
set ctdir [java::new ptolemy.domains.ct.kernel.CTMixedSignalDirector CTEmbDIR]
$ctsub setDirector $ctdir

#CTActors
set sine [java::new ptolemy.domains.ct.lib.CTSin $ctsub SIN]
set hold [java::new ptolemy.domains.ct.lib.CTZeroOrderHold $ctsub Hold]
set add1 [java::new ptolemy.domains.ct.lib.CTAdd $ctsub Add1]
set intgl1 [java::new ptolemy.domains.ct.lib.CTIntegrator $ctsub Integrator1]
set intgl2 [java::new ptolemy.domains.ct.lib.CTIntegrator $ctsub Integrator2]
set gain0 [java::new ptolemy.domains.ct.lib.CTGain $ctsub Gain0]
set gain1 [java::new ptolemy.domains.ct.lib.CTGain $ctsub Gain1]
set gain2 [java::new ptolemy.domains.ct.lib.CTGain $ctsub Gain2]
set plot [java::new ptolemy.domains.ct.lib.CTPlot $ctsub Plot]
set sampler [java::new ptolemy.domains.ct.lib.CTPeriodicalSampler $ctsub Sample]
#CTports
set sineout [$sine getPort output]
set add1in [$add1 getPort input]
set add1out [$add1 getPort output]
set intgl1in [$intgl1 getPort input]
set intgl1out [$intgl1 getPort output]
set intgl2in [$intgl2 getPort input]
set intgl2out [$intgl2 getPort output]
set gain0in [$gain0 getPort input]
set gain0out [$gain0 getPort output]
set gain1in [$gain1 getPort input]
set gain1out [$gain1 getPort output]
set gain2in [$gain2 getPort input]
set gain2out [$gain2 getPort output]
set plotin [$plot getPort input]
set sampin [$sampler getPort input]
set sampout [$sampler getPort output]
set holdin [$hold getPort input]
set holdout [$hold getPort output]

#CTConnections
set cr0 [$ctsub connect $sineout $gain0in CR0]
set cr1 [$ctsub connect $gain0out $add1in CR1]
set cr2 [$ctsub connect $add1out $intgl1in CR2]
set cr3 [$ctsub connect $intgl1out $intgl2in CR3]
set cr4 [$ctsub connect $intgl2out $plotin CR4]
$gain1in link $cr3
$gain2in link $cr4
$sampin link $cr4
set cr5 [java::new ptolemy.actor.TypedIORelation $ctsub CR5]
$sampout link $cr5
$subout link $cr5
set cr6 [$ctsub connect $gain1out $add1in CR6]
set cr7 [$ctsub connect $gain2out $add1in CR7]
set cr8 [$ctsub connect $holdout $add1in CR8]
set cr9 [java::new ptolemy.actor.TypedIORelation $ctsub CR9]
$holdin link $cr9
$subin link $cr9
$plotin link $cr0
$plotin link $cr8

############################################################
### DE system
#  approximate the FIR filter by a delay and a gain
set delay [java::new ptolemy.domains.de.lib.DEDelay $sys DELAY 0.5]
set fback [java::new ptolemy.domains.ct.lib.CTGain $sys FEEDBACK]
set deplot [java::new ptolemy.domains.de.lib.DEPlot $sys DEPLOT]

# DE ports
set delayin [$delay getPort input]
set delayout [$delay getPort output]
set fbackin [$fback getPort input]
set fbackout [$fback getPort output]
set deplotin [$deplot getPort input]

#DE connections
set dr1 [$sys connect $subout $delayin DR1]
set dr2 [$sys connect $delayout $fbackin DR2]
set dr3 [$sys connect $fbackout $subin DR3]
$deplotin link $dr2 

############################################################
### DEParameters
# 
$dedir setStopTime 20.0
set fb [$fback getAttribute Gain]
$fb setExpression -100.0
$fb parameterChanged [java::null]

############################################################
### CT Director Parameters
#
set initstep [$ctdir getAttribute InitialStepSize]
$initstep setExpression 0.000001
$initstep parameterChanged [java::null]

set minstep [$ctdir getAttribute MinimumStepSize]
$minstep setExpression 1e-6
$minstep parameterChanged [java::null]

set solver1 [$ctdir getAttribute BreakpointODESolver]
set token [java::new ptolemy.data.StringToken ptolemy.domains.ct.kernel.solver.BackwardEulerSolver]
$solver1 setToken $token
$solver1 parameterChanged [java::null]

set solver2 [$ctdir getAttribute DefaultODESolver]
set token [java::new ptolemy.data.StringToken ptolemy.domains.ct.kernel.solver.ExplicitRK23Solver]
$solver2 setToken $token
$solver2 parameterChanged [java::null]

############################################################
### CT Actor Parameters
#
set freq [$sine getAttribute AngleFrequency]
$freq setExpression 0.25
$freq parameterChanged [java::null]

set g0 [$gain0 getAttribute Gain]
$g0 setExpression 500.0
$g0 parameterChanged [java::null]

set g1 [$gain1 getAttribute Gain]
$g1 setExpression -25.0
$g1 parameterChanged [java::null]

set g2 [$gain2 getAttribute Gain]
$g2 setExpression -2500.0
$g2 parameterChanged [java::null]


set ts [$sampler getAttribute SamplePeriod]
$ts setExpression 1.0
$ts parameterChanged [java::null]

$man startRun

