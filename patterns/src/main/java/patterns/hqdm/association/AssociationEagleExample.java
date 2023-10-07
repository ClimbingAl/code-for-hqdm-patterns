package patterns.hqdm.association;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import patterns.hqdm.utils.FindSupertypes;
import patterns.hqdm.utils.HqdmObjectBaseProperties;
import patterns.hqdm.utils.MermaidUtils;
import patterns.hqdm.utils.PatternsUtils;
import patterns.hqdm.utils.QueryUtils;
import uk.gov.gchq.magmacore.hqdm.model.Thing;
import uk.gov.gchq.magmacore.hqdm.rdf.iri.HQDM;
import uk.gov.gchq.magmacore.service.MagmaCoreService;
import uk.gov.gchq.magmacore.service.MagmaCoreServiceFactory;
import uk.gov.gchq.magmacore.service.transformation.DbTransformation;

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

        System.out.println( "Create Buzz Aldrin association as occupier of Eagle Lander Module data objects!" );
        final MagmaCoreService associationService = MagmaCoreServiceFactory.createWithJenaDatabase();
        associationService.register(PatternsUtils.PREFIX_LIST);

        // Create Buzz Aldrin as an individual
        final Thing personKindObject = PatternsUtils.createNewBaseObject(
                new HqdmObjectBaseProperties(
                        HQDM.KIND_OF_PERSON,
                        PatternsUtils.PATTERNS_REF_BASE,
                        "KindOfPerson",
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                        "HqdmPatternProject_User1"
                ));
        personKindObject.addStringValue(PatternsUtils.COMMENT, "Kind_of_person_as_a_referenceable_data_object.");

        final Thing classOfStateOfPersonObject = PatternsUtils.createNewBaseObject(
                new HqdmObjectBaseProperties(
                        HQDM.CLASS_OF_STATE_OF_PERSON,
                        PatternsUtils.PATTERNS_REF_BASE,
                        "ClassOfStateOfPerson",
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                        "HqdmPatternProject_User1"
                ));
        classOfStateOfPersonObject.addStringValue(PatternsUtils.COMMENT, "Class_of_state_of_person_as_a_referenceable_data_object.");
        classOfStateOfPersonObject.addValue(HQDM.PART__OF_BY_CLASS, personKindObject.getId());

        // Get the necessary LM-5 data objects from the database

        // Find existing objects that are members of "Class_of_possible_world_for_abstract_pattern_examples"
        final Thing possibleWorldForEagleExamples = 
            QueryUtils.findThingInServiceByName( mcDatasets, "Possible_world_for_Apollo_pattern_examples");
        final List<Thing> apolloPossibleWorldThings = 
            QueryUtils.findThingsInServiceByPredicateAndValue(mcDatasets, 
                HQDM.PART_OF_POSSIBLE_WORLD, 
                possibleWorldForEagleExamples.getId());
        
        
        final Thing kindOfCrewInLanderAssociationObject = PatternsUtils.createNewBaseObject(new HqdmObjectBaseProperties(
                HQDM.KIND_OF_ASSOCIATION,
                PatternsUtils.PATTERNS_REF_BASE,
                "KindOfAssociation__CrewInLunarLanderKindOfAssociation",
                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                "HqdmPatternProject_User1"
        ) );
        kindOfCrewInLanderAssociationObject.addStringValue(PatternsUtils.COMMENT, "Kind_of_association_for_human_crew_in_a_particular_Lunar_Lander_Module.");

        final Thing roleOfLunarLanderObject = PatternsUtils.createNewBaseObject(new HqdmObjectBaseProperties(
                HQDM.ROLE,
                PatternsUtils.PATTERNS_REF_BASE,
                "RoleOfLunarLanderAsOccupiedByCrew",
                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                "HqdmPatternProject_User1"
        ) );
        roleOfLunarLanderObject.addStringValue(PatternsUtils.COMMENT, "Role_of_Lunar_Lander_Module_in_association_as_occupied_by_its_human_crew.");
        roleOfLunarLanderObject.addValue(HQDM.PART_OF_BY_CLASS_, kindOfCrewInLanderAssociationObject.getId());
        roleOfLunarLanderObject.addValue(HQDM.HAS_SUPERCLASS, QueryUtils.findThingInServiceByName(mcDatasets, "ClassOfStateOfFunctionalObject__State_Of_Lunar_Lander").getId());

        final Thing roleOfLunarLanderCrewObject = PatternsUtils.createNewBaseObject(new HqdmObjectBaseProperties(
                HQDM.ROLE,
                PatternsUtils.PATTERNS_REF_BASE,
                "RoleOfPersonAsOccupierOfLunarLander",
                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                "HqdmPatternProject_User1"
        ) );
        roleOfLunarLanderCrewObject.addStringValue(PatternsUtils.COMMENT, "Role_of_Person_as_crew_in_association_as_occupier_of_Lunar_Lander_Module.");
        roleOfLunarLanderCrewObject.addValue(HQDM.PART_OF_BY_CLASS_, kindOfCrewInLanderAssociationObject.getId());
        roleOfLunarLanderCrewObject.addValue(HQDM.HAS_SUPERCLASS, classOfStateOfPersonObject.getId());

        // Now add these classes to the kindOfGenericAssociation
        kindOfCrewInLanderAssociationObject.addValue(HQDM.CONSISTS_OF_BY_CLASS, roleOfLunarLanderObject.getId());
        kindOfCrewInLanderAssociationObject.addValue(HQDM.CONSISTS_OF_BY_CLASS, roleOfLunarLanderCrewObject.getId());

        // Get/generate events from Buzz entering LM5 to Exiting to Lunar surface
        // Create the start and end events for the lander on the moon
        final ZonedDateTime lmpEnteredLMDateTime = ZonedDateTime.parse("1969-07-20T20:12:52+00:00[UTC]");
        final ZonedDateTime lmpExitedLMDateTime = ZonedDateTime.parse("1969-07-21T02:51:16+00:00[UTC]");
        
        final Thing buzzEnteredLM5EventObject = PatternsUtils.createNewBaseObject( 
                new HqdmObjectBaseProperties(
                        HQDM.POINT_IN_TIME,
                        PatternsUtils.PATTERNS_BASE,
                        LocalDateTime.ofInstant(lmpEnteredLMDateTime.toInstant(), ZoneOffset.UTC).toString(),
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                        "HqdmPatternProject_User1"
                ));
        buzzEnteredLM5EventObject.addValue(HQDM.MEMBER_OF, QueryUtils.findThingInServiceByName(mcDatasets, "ClassOfPointInTime__ISO8601_DateTime").getId());
        buzzEnteredLM5EventObject.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldForEagleExamples.getId());

        final Thing buzzExitedLM5EventObject = PatternsUtils.createNewBaseObject( 
                new HqdmObjectBaseProperties(
                        HQDM.POINT_IN_TIME,
                        PatternsUtils.PATTERNS_BASE,
                        LocalDateTime.ofInstant(lmpExitedLMDateTime.toInstant(), ZoneOffset.UTC).toString(),
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                        "HqdmPatternProject_User1"
                ));
        buzzExitedLM5EventObject.addValue(HQDM.MEMBER_OF, QueryUtils.findThingInServiceByName(mcDatasets, "ClassOfPointInTime__ISO8601_DateTime").getId());
        buzzExitedLM5EventObject.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldForEagleExamples.getId());

        // Now generate the three states that form the generic association between a state of X and a state of a new individual Y
        // Create the state of Lunar Lander Module that is participant in this Association example
        final Thing participantStateOfLunarLanderObject = PatternsUtils.createNewBaseObject(  
                new HqdmObjectBaseProperties(
                        HQDM.PARTICIPANT,
                        PatternsUtils.PATTERNS_BASE,
                        "Example_participant_state_of_Eagle_Lunar_Lander_in_descent",
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                        "HqdmPatternProject_User1"
                ));
        participantStateOfLunarLanderObject.addValue(HQDM.MEMBER_OF_KIND, roleOfLunarLanderObject.getId());
        participantStateOfLunarLanderObject.addValue(HQDM.MEMBER_OF, QueryUtils.findThingInServiceByName(mcDatasets, "ClassOfState_X").getId());
        participantStateOfLunarLanderObject.addValue(HQDM.PART_OF_POSSIBLE_WORLD, QueryUtils.findThingInServiceByName(mcDatasets, "Possible_World_for_generic_pattern_examples").getId());
        participantStateOfLunarLanderObject.addValue(HQDM.BEGINNING, buzzEnteredLM5EventObject.getId());
        participantStateOfLunarLanderObject.addValue(HQDM.ENDING, buzzExitedLM5EventObject.getId());
        participantStateOfLunarLanderObject.addValue(HQDM.TEMPORAL_PART_OF, QueryUtils.findThingByNameInList(apolloPossibleWorldThings, "Example_individual_that_is_Lunar_Module_Eagle_LM-5").getId());

        final Thing buzzAldrinObject = PatternsUtils.createNewBaseObject( 
                new HqdmObjectBaseProperties(
                        HQDM.PERSON,
                        PatternsUtils.PATTERNS_BASE,
                        "Buzz_Aldrin",
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                        "HqdmPatternProject_User1"
                ));
        buzzAldrinObject.addValue(HQDM.MEMBER_OF_KIND, personKindObject.getId());
        buzzAldrinObject.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldForEagleExamples.getId());

        // Create the state of X that is participant in this Association example
        final Thing participantStateOfBuzzAldrinObject = PatternsUtils.createNewBaseObject(  
                new HqdmObjectBaseProperties(
                        HQDM.PARTICIPANT,
                        PatternsUtils.PATTERNS_BASE,
                        "Example_participant_state_of_Buzz_Aldrin_in_LM5",
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                        "HqdmPatternProject_User1"
                ));
        participantStateOfBuzzAldrinObject.addValue(HQDM.MEMBER_OF_KIND, roleOfLunarLanderCrewObject.getId());
        participantStateOfBuzzAldrinObject.addValue(HQDM.MEMBER_OF, classOfStateOfPersonObject.getId());
        participantStateOfBuzzAldrinObject.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldForEagleExamples.getId());
        participantStateOfBuzzAldrinObject.addValue(HQDM.BEGINNING, buzzEnteredLM5EventObject.getId());
        participantStateOfBuzzAldrinObject.addValue(HQDM.ENDING, buzzExitedLM5EventObject.getId());
        participantStateOfBuzzAldrinObject.addValue(HQDM.TEMPORAL_PART_OF, buzzAldrinObject.getId());

        // Now make Association
        final Thing associationBuzzInLM5Object = PatternsUtils.createNewBaseObject(  
                new HqdmObjectBaseProperties(
                        HQDM.ASSOCIATION,
                        PatternsUtils.PATTERNS_BASE,
                        "Association_of_Buzz_in_LM5_during_descent",
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                        "HqdmPatternProject_User1"
                ));
        associationBuzzInLM5Object.addValue(HQDM.MEMBER_OF_KIND, kindOfCrewInLanderAssociationObject.getId());
        associationBuzzInLM5Object.addValue(HQDM.BEGINNING, buzzEnteredLM5EventObject.getId());
        associationBuzzInLM5Object.addValue(HQDM.ENDING, buzzExitedLM5EventObject.getId());
        associationBuzzInLM5Object.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldForEagleExamples.getId());
        associationBuzzInLM5Object.addValue(HQDM.CONSISTS_OF_PARTICIPANT, participantStateOfBuzzAldrinObject.getId());
        associationBuzzInLM5Object.addValue(HQDM.CONSISTS_OF_PARTICIPANT, participantStateOfLunarLanderObject.getId());

        participantStateOfBuzzAldrinObject.addValue(HQDM.PARTICIPANT_IN, associationBuzzInLM5Object.getId());
        participantStateOfLunarLanderObject.addValue(HQDM.PARTICIPANT_IN, associationBuzzInLM5Object.getId());

        final DbTransformation associationChangeSet = associationService.createDbTransformation(
            List.of(
                classOfStateOfPersonObject,
                personKindObject,
                kindOfCrewInLanderAssociationObject,
                roleOfLunarLanderObject,
                roleOfLunarLanderCrewObject,
                buzzEnteredLM5EventObject,
                buzzExitedLM5EventObject,
                participantStateOfBuzzAldrinObject,
                participantStateOfLunarLanderObject,
                associationBuzzInLM5Object));
        associationService.runInTransaction(associationChangeSet);

        // Output files

        // Create node-edge graphs for the association Eagle page
        MermaidUtils.writeLRNodeEdgeGraph(List.of(
                associationBuzzInLM5Object, 
                participantStateOfBuzzAldrinObject, 
                participantStateOfLunarLanderObject ), 
                List.of("record_created", "record_creator", "comment"), 
                List.of(associationBuzzInLM5Object.getId().split("#")[1], participantStateOfBuzzAldrinObject.getId().split("#")[1], participantStateOfLunarLanderObject.getId().split("#")[1]),
                "buzzInLM5AssociationAndParticipants");

        MermaidUtils.writeLRNodeEdgeGraph(List.of(
                associationBuzzInLM5Object, 
                participantStateOfBuzzAldrinObject, 
                participantStateOfLunarLanderObject,
                classOfStateOfPersonObject,
                personKindObject,    
                buzzAldrinObject,
                buzzEnteredLM5EventObject,
                buzzExitedLM5EventObject,
                kindOfCrewInLanderAssociationObject,
                roleOfLunarLanderCrewObject,
                roleOfLunarLanderObject ), 
                List.of("record_created", "record_creator", "comment"), 
                List.of(associationBuzzInLM5Object.getId().split("#")[1], participantStateOfBuzzAldrinObject.getId().split("#")[1], participantStateOfLunarLanderObject.getId().split("#")[1]),
                "buzzInLM5AssociationAndParticipantsFull");

        try {
            final PrintStream ttl_stream_out = new PrintStream("example-files/associationBuzzInLM5Pattern.ttl");
            final PrintStream stmt_stream_out = new PrintStream("example-files/associationBuzzInLM5Pattern.stmt");

            associationService.exportTtl(ttl_stream_out);
            ttl_stream_out.close();
            System.out.println("\tData being generated as TTL in example-files/associationBuzzInLM5Pattern.ttl.");

            associationService.exportStatements(stmt_stream_out);
            stmt_stream_out.close();
            System.out.println("\tData generated as statements in example-files/associationBuzzInLM5Pattern.stmt.");

        } catch (FileNotFoundException e) {
            System.err.println("associationBuzzInLM5Pattern example write: " + e);
        } 

        mcDatasets.add(associationService);

    }
}
