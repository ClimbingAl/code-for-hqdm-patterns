package patterns.hqdm;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import uk.gov.gchq.magmacore.hqdm.model.Thing;
import uk.gov.gchq.magmacore.hqdm.rdf.iri.HQDM;

import uk.gov.gchq.magmacore.service.MagmaCoreService;
import uk.gov.gchq.magmacore.service.transformation.DbTransformation;

public class ThingExample {



    /**
     * Create a new Thing object, construct DbTransformation for it and then commit to database.
     *
     * @param mcService {@link MagmaCoreService}.
     * @return 
     */
    public static void createAndAddThing(final MagmaCoreService mcService) {

        // Create the predicates of the object we want to create.
        final HqdmObjectBaseProperties thingProperties = new HqdmObjectBaseProperties(
                HQDM.THING,
                PatternsUtils.PATTERNS_BASE,
                "Example_Thing_As_Instance_Of_TopLevelHQDM_EntityType_THING",
                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                "HqdmPatternProject_User1",
                "",
                "",
                ""
        );

        // Create the object
        final Thing thingObject = PatternsUtils.createNewBaseObject( thingProperties );

        // Commit to MC database
        final DbTransformation thingChangeSet = mcService.createDbTransformation(List.of(thingObject));
        mcService.runInTransaction(thingChangeSet);

    }
    
}
