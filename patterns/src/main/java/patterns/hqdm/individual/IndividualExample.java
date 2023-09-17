package patterns.hqdm.individual;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
        List<List<Thing>> supertypes = FindSupertypes.findSuperTypes(List.of("individual", "state", "kind_of_individual", "class_of_state"));

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
        final Thing individualKindOfXObject = PatternsUtils.createNewBaseObject(
                new HqdmObjectBaseProperties(
                        HQDM.KIND_OF_INDIVIDUAL,
                        PatternsUtils.PATTERNS_REF_BASE,
                        "KindOfIndividual_X",
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                        "HqdmPatternProject_User1"
                ));
        individualKindOfXObject.addStringValue(PatternsUtils.COMMENT, "Kind_of_X");

        final Thing classOfStateOfXObject = PatternsUtils.createNewBaseObject(
                new HqdmObjectBaseProperties(
                        HQDM.CLASS_OF_STATE,
                        PatternsUtils.PATTERNS_REF_BASE,
                        "ClassOfState_X",
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                        "HqdmPatternProject_User1"
                ) );
        classOfStateOfXObject.addStringValue(PatternsUtils.COMMENT, "Class_of_state_of_X");

        // The events need a class_of_point_in_time to be members of
        final Thing classOfEventObject = PatternsUtils.createNewBaseObject(
                new HqdmObjectBaseProperties(
                        HQDM.CLASS_OF_EVENT,
                        PatternsUtils.PATTERNS_REF_BASE,
                        "ClassOfEvent_ExampleEvents",
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                        "HqdmPatternProject_User1"
                ) );
        classOfEventObject.addStringValue(PatternsUtils.COMMENT, "Class_of_event_for_Generic_pattern_examples");

        // The events need a class_of_point_in_time to be members of
        final Thing classOfGenericPossibleWorldObject = PatternsUtils.createNewBaseObject(
                new HqdmObjectBaseProperties(
                        HQDM.CLASS_OF_POSSIBLE_WORLD,
                        PatternsUtils.PATTERNS_REF_BASE,
                        "ClassOfPossibleWorld__GenericPatternExamples",
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                        "HqdmPatternProject_User1"
                ) );
        classOfGenericPossibleWorldObject.addStringValue(PatternsUtils.COMMENT, "Class_of_possible_world_for_generic_pattern_examples");

        // Now create the Events and States
        final Thing possibleWorldObject = PatternsUtils.createNewBaseObject(
                new HqdmObjectBaseProperties(                
                        HQDM.POSSIBLE_WORLD,
                        PatternsUtils.PATTERNS_BASE,
                        "Possible_World_for_generic_pattern_examples",
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                        "HqdmPatternProject_User1"
                ) );
        possibleWorldObject.addValue(HQDM.MEMBER_OF, classOfGenericPossibleWorldObject.getId());


        // Create the start and end events for individual X
        final Thing individualXBeginningObject = PatternsUtils.createNewBaseObject( 
                new HqdmObjectBaseProperties(
                        HQDM.EVENT,
                        PatternsUtils.PATTERNS_BASE,
                        "t0",
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                        "HqdmPatternProject_User1"
                ) );
        individualXBeginningObject.addValue(HQDM.MEMBER_OF, classOfEventObject.getId());
        individualXBeginningObject.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldObject.getId());


        final Thing individualXEndingObject = PatternsUtils.createNewBaseObject( 
                new HqdmObjectBaseProperties(
                        HQDM.EVENT,
                        PatternsUtils.PATTERNS_BASE,
                        "t3",
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                        "HqdmPatternProject_User1"
                ) );
        individualXEndingObject.addValue(HQDM.MEMBER_OF, classOfEventObject.getId());
        individualXEndingObject.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldObject.getId());

        final Thing stateOfXBeginningObject = PatternsUtils.createNewBaseObject( 
                new HqdmObjectBaseProperties(
                        HQDM.EVENT,
                        PatternsUtils.PATTERNS_BASE,
                        "t1",
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                        "HqdmPatternProject_User1"
                ) );
        stateOfXBeginningObject.addValue(HQDM.MEMBER_OF, classOfEventObject.getId());
        stateOfXBeginningObject.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldObject.getId());


        final Thing stateOfXEndingObject = PatternsUtils.createNewBaseObject( 
                new HqdmObjectBaseProperties(
                        HQDM.EVENT,
                        PatternsUtils.PATTERNS_BASE,
                        "t2",
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                        "HqdmPatternProject_User1"
                ) );
        stateOfXEndingObject.addValue(HQDM.MEMBER_OF, classOfEventObject.getId());
        stateOfXEndingObject.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldObject.getId());


        // Create the individual object
        final Thing individualXObject = PatternsUtils.createNewBaseObject( 
                new HqdmObjectBaseProperties(
                        HQDM.INDIVIDUAL,
                        PatternsUtils.PATTERNS_BASE,
                        "Example_individual_X",
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                        "HqdmPatternProject_User1"
                ));
        individualXObject.addValue(HQDM.MEMBER_OF_KIND, individualKindOfXObject.getId());
        individualXObject.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldObject.getId());
        individualXObject.addValue(HQDM.BEGINNING, individualXBeginningObject.getId());
        individualXObject.addValue(HQDM.ENDING, individualXEndingObject.getId());

        // Create the state of X
        final Thing stateOfXObject = PatternsUtils.createNewBaseObject(  
                new HqdmObjectBaseProperties(
                        HQDM.STATE,
                        PatternsUtils.PATTERNS_BASE,
                        "Example_state_of_X",
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                        "HqdmPatternProject_User1"
                ));
        stateOfXObject.addValue(HQDM.MEMBER_OF, classOfStateOfXObject.getId());
        stateOfXObject.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldObject.getId());
        stateOfXObject.addValue(HQDM.BEGINNING, stateOfXBeginningObject.getId());
        stateOfXObject.addValue(HQDM.ENDING, stateOfXEndingObject.getId());
        stateOfXObject.addValue(HQDM.TEMPORAL_PART_OF, individualXObject.getId());


        // Commit to MC database
        final DbTransformation individualChangeSet = individualService.createDbTransformation(
            List.of(individualKindOfXObject, 
                classOfStateOfXObject, 
                classOfGenericPossibleWorldObject,
                classOfEventObject, 
                possibleWorldObject,
                individualXBeginningObject, 
                individualXEndingObject,
                stateOfXBeginningObject,
                stateOfXEndingObject,
                individualXObject,
                stateOfXObject));
        individualService.runInTransaction(individualChangeSet);

        // Create node-edge graphs for the individual page
        MermaidUtils.writeLRNodeEdgeGraph(List.of(individualXObject, individualKindOfXObject), 
                List.of("record_created", "record_creator", "comment"), 
                List.of(individualXObject.getId().split("#")[1]),
                "individualAndKind");

        MermaidUtils.writeLRNodeEdgeGraph(List.of(
                individualXObject, 
                stateOfXObject,
                individualKindOfXObject,
                classOfStateOfXObject, 
                classOfGenericPossibleWorldObject,
                classOfEventObject, 
                possibleWorldObject,
                individualXBeginningObject,
                individualXEndingObject,
                stateOfXBeginningObject,
                stateOfXEndingObject ), 
                List.of("record_created", "record_creator", "comment"), 
                List.of(individualXObject.getId().split("#")[1]),
                "individualExample");
        

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
