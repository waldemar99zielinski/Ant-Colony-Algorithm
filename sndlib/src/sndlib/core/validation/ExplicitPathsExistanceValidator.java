/*
 * $Id: ExplicitPathsExistanceValidator.java 442 2008-01-23 14:53:36Z roman.klaehne $
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

package sndlib.core.validation;

import sndlib.core.model.AdmissiblePathModel;
import sndlib.core.network.Demand;
import sndlib.core.problem.Problem;
import sndlib.core.util.SNDlibMessages;

import com.atesio.utils.message.MessageKey;
import com.atesio.utils.message.Messages;
import com.atesio.utils.message.MessageKey.SimpleMessageKey;

/**
 * If the admissible path model is set to <tt>EXPLICIT_LIST</tt> this 
 * validator checks at least one admissible path is specified for each 
 * {@link sndlib.core.network.Demand} in the network.<br/>
 * 
 * @see sndlib.core.model.AdmissiblePathModel
 * @see sndlib.core.network.AdmissiblePath
 * @see sndlib.core.network.Demand
 * 
 * @author Roman Klaehne
 */
class ExplicitPathsExistanceValidator implements ProblemValidator {

    private static class ErrorKeys {

        final static MessageKey NO_EXPLICIT_PATHS_FOR_DEMAND = new SimpleMessageKey(
            "validator.problem.error.noExplicitPathsForDemand");
    }

    /**
     * Constructs a new instance of this validator.
     */
    ExplicitPathsExistanceValidator() {

        /* nothing to initialize */
    }

    /**
     * If the admissible path model is set to <tt>EXPLICIT_LIST</tt> this 
     * validator checks at least one admissible path is specified for each 
     * demand in the network.<br/>
     * <br/>
     * 
     * If the admissible path model is not <tt>EXPLICIT_LIST</tt> this method 
     * has no effect.
     * 
     * @param problem the problem to validate
     * @param errors the message container into those the errors are put
     */
    public void validate(Problem problem, Messages errors) {

        if(problem.getAdmissiblePathModel() != AdmissiblePathModel.EXPLICIT_LIST) {
            return;
        }

        for(Demand demand : problem.getNetwork().demands()) {

            if(demand.admissiblePathCount() == 0) {
                errors.add(SNDlibMessages.error(
                    ErrorKeys.NO_EXPLICIT_PATHS_FOR_DEMAND, demand.getId()));
            }
        }
    }
}
