package patterns.hqdm.sign;

import java.util.List;

import patterns.hqdm.utils.MermaidUtils;
import patterns.hqdm.utils.PatternsUtils;
import patterns.hqdm.utils.QueryUtils;
import uk.gov.gchq.magmacore.hqdm.model.Thing;
import uk.gov.gchq.magmacore.hqdm.rdf.iri.HQDM;
import uk.gov.gchq.magmacore.hqdm.rdf.iri.IRI;
import uk.gov.gchq.magmacore.hqdm.rdf.iri.RDFS;
import uk.gov.gchq.magmacore.service.MagmaCoreService;

public class IdentifierApolloExample {
    /**
     * Import identifier example pattern, construct DbTransformation for the 
     * objects and then commit to database.
     *
     * @param mcDatasets {@link List<MagmaCoreService>}.
     * @return 
     */
    public static void fetchApolloIdentifierFromActivityEditor(final List<MagmaCoreService> mcDatasets) {

        // Find existing objects that are members of "Class_of_possible_world_for_abstract_pattern_examples"
        final Thing patternForSaturnVRocketAssembly = 
            QueryUtils.findThingInServiceByName( mcDatasets, "SA-506");

        final List<Thing> identifierForSaturnVRocketAssembly = 
            QueryUtils.findThingsInServiceByPredicateAndValue(mcDatasets, 
                HQDM.CONSISTS_OF_BY_CLASS, 
                new IRI(patternForSaturnVRocketAssembly.getId()));
        
        /*final Thing returnFromLunarSurfaceActivity = 
            eagleActivityService.getInTransaction( new IRI(PatternsUtils.ACTIVITY_EDITOR_BASE, "40ae87d9-8a1b-417c-86a8-a00bd70fe239"));*/
        
        
         // Create node-edge graphs for the AS-605 identifier example
        MermaidUtils.writeLRNodeEdgeGraph(List.of(
                patternForSaturnVRocketAssembly, 
                identifierForSaturnVRocketAssembly.get(0)
                ), 
                List.of("record_created", "record_creator", "comment"), 
                List.of(patternForSaturnVRocketAssembly.getId().split("#")[1]),
                "saturnVIdentifier");

    }
}
