package patterns.hqdm;

import java.util.ArrayList;
import java.util.List;
import patterns.hqdm.activity.ActivityEagleExample;
import patterns.hqdm.activity.ActivityExample;
import patterns.hqdm.association.AssociationEagleExample;
import patterns.hqdm.association.AssociationExample;
import patterns.hqdm.individual.IndividualEagleExample;
import patterns.hqdm.individual.IndividualExample;
import patterns.hqdm.sign.IdentifierApolloExample;
import patterns.hqdm.sign.SignExample;
import patterns.hqdm.system.SystemAndComponentsApolloExample;
import patterns.hqdm.system.SystemAndComponentsExample;
import patterns.hqdm.thing.ThingExample;
import uk.gov.gchq.magmacore.service.MagmaCoreService;

/**
 * Create a list of MagmaCoreServices and populate with examples generated from
 * code and in ttl input files.
 *
 */
public class ExamplesApp {
    public static void main(String[] args) {

        // Create list of datasets to be used for example generation
        final List<MagmaCoreService> datasets = new ArrayList<MagmaCoreService>();

        // Now generate the examples in order
        ThingExample.createAndAddThing(datasets);
        IndividualExample.createAndAddIndividualPattern(datasets);
        IndividualEagleExample.createAndAddIndividualPattern(datasets);

        AssociationExample.createAndAddAssociationPattern(datasets);
        AssociationEagleExample.createAndAddAssociationPattern(datasets);

        ActivityExample.createAndAddActivityPattern(datasets);
        ActivityEagleExample.loadEagleActivityFromEditor(datasets);

        SignExample.createAndAddSignPattern(datasets);
        IdentifierApolloExample.fetchApolloIdentifierFromActivityEditor(datasets);

        SystemAndComponentsExample.createAndAddSystemAndSystemComponentGenericPattern(datasets);
        SystemAndComponentsApolloExample.createAndAddSystemAndSystemComponentApolloPattern(datasets);

        System.out.println("Example file generation done.\n");

    }
}
