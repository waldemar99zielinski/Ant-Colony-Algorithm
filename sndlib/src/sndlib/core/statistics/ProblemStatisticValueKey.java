/*
 * $Id: ProblemStatisticValueKey.java 99 2006-07-04 14:05:16Z bzforlow $
 *
 * Copyright (c) 2005-2006 by Konrad-Zuse-Zentrum fuer Informationstechnik Berlin. 
 * (http://www.zib.de)  
 * 
 * Licensed under the ZIB ACADEMIC LICENSE; you may not use this file except 
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.zib.de/Optimization/Software/ziblicense.html
 *
 * as well as in the file LICENSE.txt, contained in the SNDlib distribution 
 * package.
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sndlib.core.statistics;

import sndlib.core.problem.Problem;

/**
 * This interface provides the facility to calculate any statistical value
 * of a given network design {@link sndlib.core.problem.Problem}.
 * 
 * @see ProblemStatistics
 * @see ProblemStatisticValueKeys
 * 
 * @author Roman Klaehne
 */
public interface ProblemStatisticValueKey extends StatisticValueKey<Problem> {

    /**
     * Calculates a specific statistical value of the given problem.
     * 
     * @param problem the problem
     * 
     * @return a statistical value of the given problem
     */
    public double calculateValue(Problem problem);

    /**
     * Returns a textual representative name of the statistic value 
     * identified by this key.
     * 
     * @return the name of the statistic value
     */
    public String getName();
}

