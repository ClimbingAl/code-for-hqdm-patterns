package patterns.hqdm;

import java.io.PrintStream;
import java.util.List;

import uk.gov.gchq.magmacore.hqdm.rdf.iri.HQDM;
import uk.gov.gchq.magmacore.hqdm.rdf.iri.IriBase;
import uk.gov.gchq.magmacore.hqdm.rdf.util.Pair;
import uk.gov.gchq.magmacore.service.MagmaCoreService;
import uk.gov.gchq.magmacore.service.MagmaCoreServiceFactory;

/**
 * Create a very basic HQDM data object; an instance of Thing.
 *
 */
public class ThingApp 
{
    public static void main( String[] args )
    {
        final PrintStream stream_out = System.out;

        // Create map of prefixes
        final List<IriBase> listPrefixes = List.of(
                PatternsUtils.PATTERNS_BASE,
                PatternsUtils.PATTERNS_REF_BASE,
                HQDM.HQDM
                );

        System.out.println( "Create Thing data object!" );
        final MagmaCoreService service = MagmaCoreServiceFactory.createWithJenaDatabase();
        service.register(listPrefixes);
        

        ThingExample.createAndAddThing(service);

        System.out.println("Data generated in TTL:\n");
        service.exportTtl(stream_out);
        System.out.println("\nData generated as statements:\n");
        service.exportStatements(stream_out);

        System.out.println("\nDone\n");

    }
}
