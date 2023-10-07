package patterns.hqdm.sign;

import java.util.List;

import patterns.hqdm.utils.FindSupertypes;
import patterns.hqdm.utils.MermaidUtils;
import uk.gov.gchq.magmacore.hqdm.model.Thing;
import uk.gov.gchq.magmacore.service.MagmaCoreService;

public class SignExample {
    /**
     * Create a new Sign data model pattern, construct DbTransformation for the 
     * objects and then commit to database.
     *
     * @param mcDatasets {@link List<MagmaCoreService>}.
     * @return 
     */
    public static void createAndAddSignPattern(final List<MagmaCoreService> mcDatasets) {

        // Find supertypes
        List<List<Thing>> supertypes = FindSupertypes.findSuperTypes(List.of(
            "sign", 
            "representation_by_sign", 
            "recognizing_language_community", 
            "class_of_representation",
            "class_of_sign",
            "kind_of_organization",
            "role"));

        supertypes.forEach(st -> { 
                st.forEach( tl -> {
                        System.out.println(tl.getId() + " ");
                        }); 
                System.out.println(" ");
        });

        MermaidUtils.writeSupertypeGraph(supertypes, "signTypes");
    }
}
