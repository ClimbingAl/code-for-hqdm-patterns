package patterns.hqdm.activity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import patterns.hqdm.utils.PatternsUtils;
import uk.gov.gchq.magmacore.hqdm.model.Thing;
import uk.gov.gchq.magmacore.hqdm.rdf.iri.HQDM;
import uk.gov.gchq.magmacore.service.MagmaCoreService;
import uk.gov.gchq.magmacore.service.MagmaCoreServiceFactory;

public class ActivityEagleExample {
    
    // Load example
    /**
     * Load Activity Editor ttl file and commit to new mcService dataset
     *
     * @param mcDatasets {@link List<MagmaCoreService>}.
     * @return 
     */
    public static void loadEagleActivityFromEditor(final List<MagmaCoreService> mcDatasets) {

        System.out.println( "Load generic activity data objects." );
        final MagmaCoreService eagleActivityService = MagmaCoreServiceFactory.createWithJenaDatabase();
        eagleActivityService.register(PatternsUtils.PREFIX_LIST);

        try {
            InputStream activityInputStream = new FileInputStream("input-files/activity_diagram_state_of_apollo11_mission_MAPPED.ttl");

            eagleActivityService.importTtl(activityInputStream);
            activityInputStream.close();
            System.out.println("\tData loaded from TTL in input-files/activity_diagram_state_of_apollo11_mission copy.ttl.");

        } catch (FileNotFoundException e) {
            System.err.println("activityInputStream example load: " + e);
        } catch (Exception e){

        }

        final List<Thing> namedThings = 
            eagleActivityService.findByPredicateIriInTransaction(HQDM.ENTITY_NAME);
        
        System.out.println("Number of stes = " + namedThings.size());


    }

}
