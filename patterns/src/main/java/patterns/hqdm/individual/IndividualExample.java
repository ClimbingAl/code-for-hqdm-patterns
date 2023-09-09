package patterns.hqdm.individual;

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
import uk.gov.gchq.magmacore.hqdm.model.Thing;
import uk.gov.gchq.magmacore.hqdm.rdf.iri.HQDM;
import uk.gov.gchq.magmacore.service.MagmaCoreService;
import uk.gov.gchq.magmacore.service.MagmaCoreServiceFactory;
import uk.gov.gchq.magmacore.service.transformation.DbTransformation;

public class IndividualExample {

    /**
     * Create a new Individual and temporal part objects, construct DbTransformation for them 
     * and then commit to database.
     *
     * @param mcDatasets {@link List<MagmaCoreService>}.
     * @return 
     */
    public static void createAndAddIndividualPattern(final List<MagmaCoreService> mcDatasets) {

        // Find supertypes
        List<List<Thing>> supertypes = FindSupertypes.findSuperTypes(List.of("physical_object", "state_of_physical_object", "kind_of_physical_object", "class_of_state_of_physical_object"));

        supertypes.forEach(st -> { 
                st.forEach( tl -> {
                        System.out.println(tl.getId() + " ");
                        }); 
                System.out.println(" ");
        });

        MermaidUtils.writeSupertypeGraph(supertypes, "individual");

        System.out.println( "Create Individual data objects!" );
        final MagmaCoreService individualService = MagmaCoreServiceFactory.createWithJenaDatabase();
        individualService.register(PatternsUtils.PREFIX_LIST);

        // Create the predicates of the whole-life individual object we want to create.
        // It needs a class (SET) to be a member_of_kind of
        final HqdmObjectBaseProperties kindOfPhysicalObject = new HqdmObjectBaseProperties(
                HQDM.KIND_OF_PHYSICAL_OBJECT,
                PatternsUtils.PATTERNS_REF_BASE,
                "KindOfPhysicalObject__Lunar_Lander",
                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                "HqdmPatternProject_User1"
        );
        final Thing lunarLanderKindObject = PatternsUtils.createNewBaseObject(kindOfPhysicalObject);
        lunarLanderKindObject.addStringValue(PatternsUtils.COMMENT, "Kind_of_functional_system_that_is_Lunar_Lander");

        final HqdmObjectBaseProperties classOfStateOfPhysicalObjectProperties = new HqdmObjectBaseProperties(
                HQDM.CLASS_OF_STATE_OF_PHYSICAL_OBJECT,
                PatternsUtils.PATTERNS_REF_BASE,
                "ClassOfStateOfPhysicalObject__State_Of_Lunar_Lander",
                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                "HqdmPatternProject_User1"
        );
        final Thing lunarLanderClassOfStateObject = PatternsUtils.createNewBaseObject(classOfStateOfPhysicalObjectProperties);
        lunarLanderClassOfStateObject.addStringValue(PatternsUtils.COMMENT, "Class_of_state_of_system_that_is_temporal_part_of_Lunar_Lander");

        // The events need a class_of_point_in_time to be members of
        final HqdmObjectBaseProperties classOfPointInTimeProperties = new HqdmObjectBaseProperties(
                HQDM.CLASS_OF_POINT_IN_TIME,
                PatternsUtils.PATTERNS_REF_BASE,
                "ClassOfPointInTime__ISO8601_DateTime",
                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                "HqdmPatternProject_User1"
        );
        final Thing classOfPointInTimeObject = PatternsUtils.createNewBaseObject(classOfPointInTimeProperties);
        classOfPointInTimeObject.addStringValue(PatternsUtils.COMMENT, "Class_of_point_in_time_for_events_in_ISO_8601-1:2019_date-time_format");

        // The events need a class_of_point_in_time to be members of
        final HqdmObjectBaseProperties classOfPossibleWorldProperties = new HqdmObjectBaseProperties(
                HQDM.CLASS_OF_POSSIBLE_WORLD,
                PatternsUtils.PATTERNS_REF_BASE,
                "ClassOfPossibleWorld_PatternExamples",
                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                "HqdmPatternProject_User1"
        );
        final Thing classOfPossibleWorldObject = PatternsUtils.createNewBaseObject(classOfPossibleWorldProperties);
        classOfPossibleWorldObject.addStringValue(PatternsUtils.COMMENT, "Class_of_possible_world_for_pattern_examples");

        // Now create the Events and States
        final HqdmObjectBaseProperties possibleWorld = new HqdmObjectBaseProperties(                
                HQDM.POSSIBLE_WORLD,
                PatternsUtils.PATTERNS_BASE,
                "Possible_world1_for_Individual_Pattern_example",
                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                "HqdmPatternProject_User1");
        final Thing possibleWorldObject = PatternsUtils.createNewBaseObject( possibleWorld );
        possibleWorldObject.addValue(HQDM.MEMBER_OF, classOfPossibleWorldObject.getId());


        final HqdmObjectBaseProperties physicalObject = new HqdmObjectBaseProperties(
                HQDM.PHYSICAL_OBJECT,
                PatternsUtils.PATTERNS_BASE,
                "Example_individual_that_is_Lunar_Module_Eagle_LM-5",
                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                "HqdmPatternProject_User1"
        );

        // Create the LM-5 lander object
        final Thing lunarLanderObject = PatternsUtils.createNewBaseObject( physicalObject );
        lunarLanderObject.addValue(HQDM.MEMBER_OF_KIND, lunarLanderKindObject.getId());
        lunarLanderObject.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldObject.getId());
        // No beginning or ending as these are not known BUT a temporal part is known, the Eagle landing on the Moon

        // Create the start and end events for the lander on the moon
        final ZonedDateTime landedStartDateTime = ZonedDateTime.parse("1969-07-20T20:17:40+00:00[UTC]");
        final HqdmObjectBaseProperties eagleMoonLandedBeginningProperties = new HqdmObjectBaseProperties(
                HQDM.POINT_IN_TIME,
                PatternsUtils.PATTERNS_BASE,
                LocalDateTime.ofInstant(landedStartDateTime.toInstant(), ZoneOffset.UTC).toString(),
                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                "HqdmPatternProject_User1"
        );

        final ZonedDateTime landedEndDateTime = ZonedDateTime.parse("1969-07-21T17:54:01+00:00[UTC]");
        final HqdmObjectBaseProperties eagleMoonLandedEndingProperties = new HqdmObjectBaseProperties(
                HQDM.POINT_IN_TIME,
                PatternsUtils.PATTERNS_BASE,
                LocalDateTime.ofInstant(landedEndDateTime.toInstant(), ZoneOffset.UTC).toString(),
                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                "HqdmPatternProject_User1"
        );

        final Thing eagleMoonLandedBeginningObject = PatternsUtils.createNewBaseObject( eagleMoonLandedBeginningProperties );
        eagleMoonLandedBeginningObject.addValue(HQDM.MEMBER_OF, classOfPointInTimeObject.getId());
        eagleMoonLandedBeginningObject.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldObject.getId());

        final Thing eagleMoonLandedEndingObject = PatternsUtils.createNewBaseObject( eagleMoonLandedEndingProperties );
        eagleMoonLandedEndingObject.addValue(HQDM.MEMBER_OF, classOfPointInTimeObject.getId());
        eagleMoonLandedEndingObject.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldObject.getId());

        // Create the state of the Eagle Moon Lander on the Moon
        final HqdmObjectBaseProperties stateOfLunarLanderProperties = new HqdmObjectBaseProperties(
                HQDM.INDIVIDUAL,
                PatternsUtils.PATTERNS_BASE,
                "Example_state_of_Lunar_Module_Eagle_LM-5",
                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                "HqdmPatternProject_User1"
        );

        // Create the state of LM-5 lander object
        final Thing stateOfLanderObject = PatternsUtils.createNewBaseObject( stateOfLunarLanderProperties );
        stateOfLanderObject.addValue(HQDM.MEMBER_OF, lunarLanderClassOfStateObject.getId());
        stateOfLanderObject.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldObject.getId());
        stateOfLanderObject.addValue(HQDM.BEGINNING, eagleMoonLandedBeginningObject.getId());
        stateOfLanderObject.addValue(HQDM.ENDING, eagleMoonLandedEndingObject.getId());
        stateOfLanderObject.addValue(HQDM.TEMPORAL_PART_OF, lunarLanderObject.getId());

        lunarLanderObject.addValue(HQDM.ENDING, eagleMoonLandedEndingObject.getId());

        // Commit to MC database
        final DbTransformation individualChangeSet = individualService.createDbTransformation(
            List.of(lunarLanderKindObject, 
                lunarLanderClassOfStateObject, 
                classOfPossibleWorldObject,
                classOfPointInTimeObject, 
                possibleWorldObject,
                eagleMoonLandedBeginningObject,
                eagleMoonLandedEndingObject,
                lunarLanderObject, 
                stateOfLanderObject));
        individualService.runInTransaction(individualChangeSet);

        // Create node-edge graphs for the individual page
        MermaidUtils.writeLRNodeEdgeGraph(List.of(lunarLanderObject, lunarLanderKindObject), 
                List.of("record_created", "record_creator", "comment"), 
                List.of(lunarLanderObject.getId().split("#")[1]),
                "individualAndKind");

        MermaidUtils.writeLRNodeEdgeGraph(List.of(
                lunarLanderObject, 
                lunarLanderKindObject, 
                lunarLanderClassOfStateObject, 
                classOfPossibleWorldObject,
                classOfPointInTimeObject, 
                possibleWorldObject,
                eagleMoonLandedBeginningObject,
                eagleMoonLandedEndingObject,
                stateOfLanderObject), 
                List.of("record_created", "record_creator", "comment"), 
                List.of(lunarLanderObject.getId().split("#")[1]),
                "individualExample");
        
        // Temporal Parts graphs
        MermaidUtils.writeLRNodeEdgeGraph(List.of(
                        stateOfLanderObject, 
                        eagleMoonLandedBeginningObject,
                        eagleMoonLandedEndingObject), 
                List.of("record_created", "record_creator", "comment", "member_of_kind", "member_of","temporal_part_of", "part_of_possible_world"), 
                List.of("beginning", "ending"),
                "temporalPartOfIncEvents");
        
        MermaidUtils.writeLRNodeEdgeGraph(List.of(lunarLanderObject, stateOfLanderObject), 
                List.of("record_created", "record_creator", "comment", "member_of_kind", "member_of", "part_of_possible_world", "beginning", "ending"), 
                List.of("temporal_part_of"),
                "temporalPartOf");
        
        MermaidUtils.writeLRNodeEdgeGraph(List.of(lunarLanderObject, stateOfLanderObject), 
                List.of("record_created", "record_creator", "comment", "member_of_kind", "member_of", "beginning", "ending"), 
                List.of("temporal_part_of", "part_of_possible_world"),
                "temporalPartOfAllRels");

        // Set Membership
        MermaidUtils.writeLRNodeEdgeGraph(List.of(stateOfLanderObject, lunarLanderClassOfStateObject), 
                List.of("record_created", "record_creator", "comment", "member_of_kind", "temporal_part_of", "part_of_possible_world", "beginning", "ending"), 
                List.of("member_of", lunarLanderClassOfStateObject.getId().split("#")[1]),
                "member_of");
        
        MermaidUtils.writeLRNodeEdgeGraph(List.of(lunarLanderObject, lunarLanderKindObject), 
                List.of("record_created", "record_creator", "comment", "member_of", "temporal_part_of", "part_of_possible_world", "beginning", "ending"),
                List.of("member_of_kind"), 
                "member_of_kind");
        
        // Order
        MermaidUtils.writeLRNodeEdgeGraph(List.of(stateOfLanderObject), 
                List.of("record_created", "record_creator", "comment", "member_of_kind", "part_of_possible_world", "member_of"), 
                List.of("beginning", "ending"),
                "orderEvents");
        
        MermaidUtils.writeLRNodeEdgeGraph(List.of(stateOfLanderObject), 
                List.of("record_created", "record_creator", "comment", "member_of_kind", "part_of_possible_world", "beginning", "ending", "member_of", "data_EntityName", "type"), 
                List.of("temporal_part_of"),
                "rel");

        try {
            final PrintStream ttl_stream_out = new PrintStream("example-files/individualPattern.ttl");
            final PrintStream stmt_stream_out = new PrintStream("example-files/individualPattern.stmt");

            individualService.exportTtl(ttl_stream_out);
            ttl_stream_out.close();
            System.out.println("\tData being generated as TTL in example-files/individualPattern.ttl.");

            individualService.exportStatements(stmt_stream_out);
            stmt_stream_out.close();
            System.out.println("\tData generated as statements in example-files/individualPattern.stmt.");

        } catch (FileNotFoundException e) {
            System.err.println("individualPattern example write: " + e);
        } 

        mcDatasets.add(individualService);

    }

}