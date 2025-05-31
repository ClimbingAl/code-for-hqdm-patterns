package patterns.hqdm;

import java.util.ArrayList;
import java.util.List;
import hqdm.utils.base.BaseCollection;

import patterns.hqdm.temporal.TemporalAlgebraExamples;
import uk.gov.gchq.magmacore.service.MagmaCoreService;
import uk.gov.gchq.magmacore.service.MagmaCoreServiceFactory;

/**
*   Create a list of MagmaCoreServices and populate with temporal algebra examples generated from
 *  code.
 */
public class TemporalAlgebraApp {
    public static void main(String[] args) {

        // Create list of datasets to be used for example generation
        final List<MagmaCoreService> datasets = new ArrayList<MagmaCoreService>();
        final String record_creator = "HqdmPatternProject_User1";
        final BaseCollection baseCollection = new BaseCollection(
            "temporalAlgebra-rdl", 
            "https://github.com/ClimbingAl/code-for-hqdm-patterns/temporalAlgebra-rdl#", 
            "temporalAlgebra", 
            "https://github.com/ClimbingAl/code-for-hqdm-patterns/temporalAlgebra#");

        // Now generate the examples in order
        MagmaCoreService temporalAlgebraService = MagmaCoreServiceFactory.createWithJenaDatabase();
        temporalAlgebraService.register(baseCollection.PREFIX_LIST);
        TemporalAlgebraExamples.createAndAddTemporalAlgebraExampleObjects( baseCollection, datasets, temporalAlgebraService, record_creator );
        
        

        
    }
}
