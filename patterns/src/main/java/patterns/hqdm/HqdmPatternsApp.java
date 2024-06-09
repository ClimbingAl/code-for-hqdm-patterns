package patterns.hqdm;

import java.util.ArrayList;
import java.util.List;
import hqdm.utils.base.BaseCollection;
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
import uk.gov.gchq.magmacore.service.MagmaCoreServiceFactory;

/**
 * Create a list of MagmaCoreServices and populate with examples generated from
 * code and in ttl input files.
 *
 */
public class HqdmPatternsApp {
    public static void main(String[] args) {

        // Create list of datasets to be used for example generation
        final List<MagmaCoreService> datasets = new ArrayList<MagmaCoreService>();
        final String record_creator = "HqdmPatternProject_User1";
        final BaseCollection baseCollection = new BaseCollection(
            "patterns-rdl", 
            "https://github.com/ClimbingAl/code-for-hqdm-patterns/patterns-rdl#", 
            "patterns", 
            "https://github.com/ClimbingAl/code-for-hqdm-patterns/patterns#");

        final BaseCollection activityEditorBaseCollection = new BaseCollection(
            "diagram", 
            "https://apollo-protocol.github.io/ns/2023/diagram-editor/diagram#", 
            "diagram", 
            "https://apollo-protocol.github.io/ns/2023/diagram-editor/diagram#");

        // Now generate the examples in order
        MagmaCoreService thingService = MagmaCoreServiceFactory.createWithJenaDatabase();
        thingService.register(baseCollection.PREFIX_LIST);
        ThingExample.createAndAddThing( baseCollection, datasets, thingService, record_creator );
        datasets.add(thingService);

        MagmaCoreService individualService = MagmaCoreServiceFactory.createWithJenaDatabase();
        individualService.register(baseCollection.PREFIX_LIST);
        IndividualExample.createAndAddIndividualPattern( baseCollection, datasets, individualService, record_creator );
        datasets.add(individualService);

        MagmaCoreService individualEagleService = MagmaCoreServiceFactory.createWithJenaDatabase();
        individualEagleService.register(baseCollection.PREFIX_LIST);
        IndividualEagleExample.createAndAddIndividualPattern( baseCollection, datasets, individualEagleService, record_creator );
        datasets.add(individualEagleService);

        MagmaCoreService associationService = MagmaCoreServiceFactory.createWithJenaDatabase();
        associationService.register(baseCollection.PREFIX_LIST);
        AssociationExample.createAndAddAssociationPattern( baseCollection, datasets, associationService, record_creator );
        datasets.add(associationService);

        MagmaCoreService associationEagleService = MagmaCoreServiceFactory.createWithJenaDatabase();
        associationEagleService.register(baseCollection.PREFIX_LIST);
        AssociationEagleExample.createAndAddAssociationPattern( baseCollection, datasets, associationEagleService, record_creator );
        datasets.add(associationEagleService);

        MagmaCoreService activityService = MagmaCoreServiceFactory.createWithJenaDatabase();
        activityService.register(baseCollection.PREFIX_LIST);
        ActivityExample.createAndAddActivityPattern( baseCollection, datasets, activityService, record_creator );
        datasets.add(activityService);

        MagmaCoreService eagleActivityService = MagmaCoreServiceFactory.createWithJenaDatabase();
        eagleActivityService.register(activityEditorBaseCollection.PREFIX_LIST);
        ActivityEagleExample.loadEagleActivityFromEditor( activityEditorBaseCollection, datasets, eagleActivityService, record_creator );
        datasets.add(eagleActivityService);

        MagmaCoreService signService = MagmaCoreServiceFactory.createWithJenaDatabase();
        signService.register(baseCollection.PREFIX_LIST);
        SignExample.createAndAddSignPattern( baseCollection, datasets, signService, record_creator );
        datasets.add(signService);

        IdentifierApolloExample.fetchApolloIdentifierFromActivityEditor(eagleActivityService);

        MagmaCoreService systemAndComponentService = MagmaCoreServiceFactory.createWithJenaDatabase();
        systemAndComponentService.register(baseCollection.PREFIX_LIST);
        SystemAndComponentsExample.createAndAddSystemAndSystemComponentGenericPattern( baseCollection, datasets, systemAndComponentService, record_creator );
        datasets.add(systemAndComponentService);

        MagmaCoreService systemAndComponentApolloService = MagmaCoreServiceFactory.createWithJenaDatabase();
        systemAndComponentApolloService.register(baseCollection.PREFIX_LIST);
        SystemAndComponentsApolloExample.createAndAddSystemAndSystemComponentApolloPattern( baseCollection, datasets, systemAndComponentApolloService, record_creator );
        datasets.add(systemAndComponentApolloService);

        System.out.println("Example file generation done.\n");

    }
}
