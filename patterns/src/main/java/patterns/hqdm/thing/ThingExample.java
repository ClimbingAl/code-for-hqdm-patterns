package patterns.hqdm.thing;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import hqdm.utils.base.BaseCollection;
import hqdm.utils.base.HqdmObjectBaseProperties;
import hqdm.utils.base.IriUtils;
import uk.gov.gchq.magmacore.hqdm.model.Thing;
import uk.gov.gchq.magmacore.hqdm.rdf.iri.HQDM;

import uk.gov.gchq.magmacore.service.MagmaCoreService;
import uk.gov.gchq.magmacore.service.transformation.DbTransformation;

public class ThingExample {

    /**
     * Create a new Thing object, construct DbTransformation for it and then commit
     * to database.
     *
     * @param mcDatasets {@link List<MagmaCoreService>}.
     * @return
     */
    public static void createAndAddThing(BaseCollection baseCollection, final List<MagmaCoreService> mcDatasets, MagmaCoreService thingService, String record_creator) {

        System.out.println("Create Thing data object!");

        System.out.println("\tData generated in TTL in example-files/thing.ttl.");

        // Create the predicates of the object we want to create.
        final HqdmObjectBaseProperties thingProperties = new HqdmObjectBaseProperties(
                HQDM.THING,
                baseCollection.PATTERNS_BASE,
                "Example_Thing_As_Instance_Of_TopLevelHQDM_EntityType_THING",
                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                record_creator,
                "",
                "",
                "");

        // Create the object
        final Thing thingObject = IriUtils.createNewBaseObject(baseCollection, thingProperties);

        // Commit to MC database
        final DbTransformation thingChangeSet = thingService.createDbTransformation(List.of(thingObject));
        thingService.runInWriteTransaction(thingChangeSet);

        try {
            final PrintStream ttl_stream_out = new PrintStream("example-files/thing.ttl");

            thingService.exportTtl(ttl_stream_out);
            ttl_stream_out.close();
        } catch (FileNotFoundException e) {
            System.err.println("thing example write: " + e);
        }

        mcDatasets.add(thingService);

    }

}
