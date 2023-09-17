package patterns.hqdm.association;

import java.util.List;

import patterns.hqdm.utils.FindSupertypes;
import patterns.hqdm.utils.MermaidUtils;
import patterns.hqdm.utils.PatternsUtils;
import uk.gov.gchq.magmacore.hqdm.model.Thing;
import uk.gov.gchq.magmacore.service.MagmaCoreService;
import uk.gov.gchq.magmacore.service.MagmaCoreServiceFactory;

public class AssociationEagleExample {
    
    /**
     * Create a new Association for Buzz Aldrin in LM, construct DbTransformation for them 
     * and then commit to database.
     *
     * @param mcDatasets {@link List<MagmaCoreService>}.
     * @return 
     */
    public static void createAndAddAssociationPattern(final List<MagmaCoreService> mcDatasets) {

        // Find supertypes
        List<List<Thing>> supertypes = FindSupertypes.findSuperTypes(List.of("association", "participant", "state_of_person", "state_of_functional_object", "kind_of_association", "role"));

        supertypes.forEach(st -> { 
                st.forEach( tl -> {
                        System.out.println(tl.getId() + " ");
                        }); 
                System.out.println(" ");
        });

        MermaidUtils.writeSupertypeGraph(supertypes, "associationEagleTypes");

        System.out.println( "Create Buzz Aldrin association as occpier of Eagle Lander Module data objects!" );
        final MagmaCoreService associationService = MagmaCoreServiceFactory.createWithJenaDatabase();
        associationService.register(PatternsUtils.PREFIX_LIST);


        // Create time instants for Buzz Aldrin entering and leaving LM


        mcDatasets.add(associationService);

    }
}
