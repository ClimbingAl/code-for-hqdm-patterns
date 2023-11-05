package patterns.hqdm.thing;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import patterns.hqdm.utils.HqdmObjectBaseProperties;
import patterns.hqdm.utils.PatternsUtils;
import uk.gov.gchq.magmacore.hqdm.model.Thing;
import uk.gov.gchq.magmacore.hqdm.rdf.iri.HQDM;

import uk.gov.gchq.magmacore.service.MagmaCoreService;
import uk.gov.gchq.magmacore.service.MagmaCoreServiceFactory;
import uk.gov.gchq.magmacore.service.transformation.DbTransformation;

public class ThingExample {

    /**
     * Create a new Thing object, construct DbTransformation for it and then commit
     * to database.
     *
     * @param mcDatasets {@link List<MagmaCoreService>}.
     * @return
     */
    public static void createAndAddThing(final List<MagmaCoreService> mcDatasets) {

        System.out.println("Create Thing data object!");
        final MagmaCoreService thingService = MagmaCoreServiceFactory.createWithJenaDatabase();
        thingService.register(PatternsUtils.PREFIX_LIST);

        System.out.println("\tData generated in TTL in example-files/thing.ttl.");

        // Create the predicates of the object we want to create.
        final HqdmObjectBaseProperties thingProperties = new HqdmObjectBaseProperties(
                HQDM.THING,
                PatternsUtils.PATTERNS_BASE,
                "Example_Thing_As_Instance_Of_TopLevelHQDM_EntityType_THING",
                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                "HqdmPatternProject_User1",
                "",
                "",
                "");

        // Create the object
        final Thing thingObject = PatternsUtils.createNewBaseObject(thingProperties);

        // Commit to MC database
        final DbTransformation thingChangeSet = thingService.createDbTransformation(List.of(thingObject));
        thingService.runInTransaction(thingChangeSet);

        try {
            final PrintStream ttl_stream_out = new PrintStream("example-files/thing.ttl");
            final PrintStream stmt_stream_out = new PrintStream("example-files/thing.stmt");

            thingService.exportTtl(ttl_stream_out);
            ttl_stream_out.close();

            System.out.println("\tData generated as statements in example-files/thing.stmt.");
            thingService.exportStatements(stmt_stream_out);
            stmt_stream_out.close();
        } catch (FileNotFoundException e) {
            System.err.println("thing example write: " + e);
        }

        mcDatasets.add(thingService);

    }

}
