package patterns.hqdm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import uk.gov.gchq.magmacore.hqdm.rdf.iri.HQDM;
import uk.gov.gchq.magmacore.hqdm.rdf.iri.IriBase;
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

        System.out.println("Data generated in TTL in example-files/thing.ttl.\n");
        try {
            final PrintStream ttl_stream_out = new PrintStream("example-files/thing.ttl");
            final PrintStream stmt_stream_out = new PrintStream("example-files/thing.stmt");

            service.exportTtl(ttl_stream_out);
            ttl_stream_out.close();

            System.out.println("Data generated as statements in example-files/thing.stmt.\n");
            service.exportStatements(stmt_stream_out);
            stmt_stream_out.close();
        } catch (FileNotFoundException e) {
            System.err.println("thing example write: " + e);
        } catch (IOException e) {
            System.err.println("thing example write: " + e);
        }

        System.out.println("\nDone\n");

    }
}
