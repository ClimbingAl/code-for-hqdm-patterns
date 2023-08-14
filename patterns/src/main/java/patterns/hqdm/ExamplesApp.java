package patterns.hqdm;

import java.util.ArrayList;
import java.util.List;

import patterns.hqdm.individual.IndividualExample;
import patterns.hqdm.thing.ThingExample;
import uk.gov.gchq.magmacore.service.MagmaCoreService;

/**
 * Create a very basic HQDM data object; an instance of Thing.
 *
 */
public class ExamplesApp 
{
    public static void main( String[] args )
    {

        // Create list of datasets to be used for example generation
        final List<MagmaCoreService> datasets = new ArrayList<MagmaCoreService>();
       
        ThingExample.createAndAddThing(datasets);
        IndividualExample.createAndAddIndividualPattern(datasets);
        
        System.out.println("Done\n");

    }
}
