package patterns.hqdm.sign;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import patterns.hqdm.utils.MermaidUtils;
import patterns.hqdm.utils.QueryUtils;
import uk.gov.gchq.magmacore.hqdm.model.Thing;
import uk.gov.gchq.magmacore.hqdm.rdf.iri.HQDM;
import uk.gov.gchq.magmacore.hqdm.rdf.iri.IRI;
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

        final Thing identifierForSaturnVRocketAssembly = 
            QueryUtils.findThingsInServiceByPredicateAndValue(mcDatasets, 
                HQDM.CONSISTS_OF_BY_CLASS, 
                new IRI(patternForSaturnVRocketAssembly.getId())).get(0);
        
        // Create node-edge graphs for the AS-605 identifier example
        MermaidUtils.writeLRNodeEdgeGraph(List.of(
                patternForSaturnVRocketAssembly, 
                identifierForSaturnVRocketAssembly
                ), 
                List.of("record_created", "record_creator", "comment"), 
                List.of(patternForSaturnVRocketAssembly.getId().split("#")[1]),
                "saturnVIdentifier");
        
        final Thing representationBySignForSaturnVRocketAssembly = 
            QueryUtils.findThingsInServiceByPredicateAndValue(mcDatasets, 
                HQDM.MEMBER_OF_, 
                new IRI(identifierForSaturnVRocketAssembly.getId())).get(0);
        
        final List<Thing> participantsInRBS = 
            QueryUtils.findThingsInServiceByPredicateAndValue(mcDatasets, 
                HQDM.PARTICIPANT_IN, 
                new IRI(representationBySignForSaturnVRocketAssembly.getId()));
        
        Set<Object> representedPredicateSet = representationBySignForSaturnVRocketAssembly.getPredicates().get(HQDM.REPRESENTS);
        String representedId = null;
        Thing representedThing = null;
        if(representedPredicateSet != null){
            for(Object entityId: representedPredicateSet){
                representedId =  entityId.toString();
            }
            representedThing = QueryUtils.getThingInServicesById(mcDatasets, representedId);
        }

        // Get kind of thing
        Set<Object> rocketPredicateSet = representedThing.getPredicates().get(HQDM.MEMBER_OF_KIND);
        String kindId = null;
        Thing kindThing = null;
        if(rocketPredicateSet != null){
            for(Object entityId: rocketPredicateSet){
                if( !entityId.toString().contains("hqdmtop.github.io") ){
                    kindId =  entityId.toString();
                }
            }
            kindThing = QueryUtils.getThingInServicesById(mcDatasets, kindId);
        }
        
        MermaidUtils.writeLRNodeEdgeGraph(List.of(
                representationBySignForSaturnVRocketAssembly,
                participantsInRBS.get(0),
                participantsInRBS.get(1),
                representedThing,
                kindThing
                ), 
                List.of("record_created", "record_creator", "comment"), 
                List.of(patternForSaturnVRocketAssembly.getId().split("#")[1], representedThing.getId().split("#")[1]),
                "saturnVRepresentationBySignAllParticipantRels");

        // Create node-edge graphs for the SA-506 identifier example
        // Exclude participant_in relations that are not involved in this RBS
        participantsInRBS.forEach(participant -> {
            List<Object> removeList = new ArrayList<>();
            participant.getPredicates().get(HQDM.PARTICIPANT_IN).forEach( node -> {
                if(!(node.toString().equals(representationBySignForSaturnVRocketAssembly.getId().toString()))){
                    removeList.add(node);
                }
            });
            removeList.forEach( node -> {
                participant.removeValue(HQDM.PARTICIPANT_IN, node);
            }); 
        });

        MermaidUtils.writeLRNodeEdgeGraph(List.of(
                representedThing,
                kindThing
                ), 
                List.of("record_created", "record_creator", "comment"), 
                List.of(representedThing.getId().split("#")[1]),
                "saturnVObjectsOnly");

        MermaidUtils.writeLRNodeEdgeGraph(List.of(
                representationBySignForSaturnVRocketAssembly,
                participantsInRBS.get(0),
                participantsInRBS.get(1),
                representedThing,
                kindThing
                ), 
                List.of("record_created", "record_creator", "comment"), 
                List.of(representationBySignForSaturnVRocketAssembly.getId().split("#")[1], representedThing.getId().split("#")[1]),
                "saturnVRepresentationBySign");
        
        MermaidUtils.writeLRNodeEdgeGraph(List.of(
                patternForSaturnVRocketAssembly, 
                identifierForSaturnVRocketAssembly,
                representationBySignForSaturnVRocketAssembly,
                participantsInRBS.get(0),
                participantsInRBS.get(1),
                representedThing,
                kindThing
                ), 
                List.of("record_created", "record_creator", "comment"), 
                List.of(patternForSaturnVRocketAssembly.getId().split("#")[1], representedThing.getId().split("#")[1]),
                "saturnVRBSAndIdentifier");
        
        MermaidUtils.writeLRNodeEdgeGraph(List.of(
                patternForSaturnVRocketAssembly, 
                identifierForSaturnVRocketAssembly,
                representedThing,
                kindThing
                ), 
                List.of("record_created", "record_creator", "comment"), 
                List.of(patternForSaturnVRocketAssembly.getId().split("#")[1], representedThing.getId().split("#")[1]),
                "saturnVIdentifierAndPatternRepresented");

    }
}
