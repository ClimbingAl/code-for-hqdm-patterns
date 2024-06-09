package patterns.hqdm.individual;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import hqdm.utils.types.FindSupertypes;
import hqdm.utils.base.BaseCollection;
import hqdm.utils.base.HqdmObjectBaseProperties;
import hqdm.utils.base.IriUtils;
import hqdm.utils.mermaid.MermaidUtils;
import uk.gov.gchq.magmacore.hqdm.model.Thing;
import uk.gov.gchq.magmacore.hqdm.rdf.iri.HQDM;
import uk.gov.gchq.magmacore.service.MagmaCoreService;
import uk.gov.gchq.magmacore.service.transformation.DbTransformation;

public class IndividualExample {

        /**
         * Create a new Individual and temporal part objects, construct DbTransformation
         * for them
         * and then commit to database.
         *
         * @param mcDatasets {@link List<MagmaCoreService>}.
         * @return
         */
        public static void createAndAddIndividualPattern(BaseCollection baseCollection, final List<MagmaCoreService> mcDatasets, MagmaCoreService individualService, String record_creator) {

                // Find supertypes
                List<List<Thing>> supertypes = FindSupertypes
                                .findSuperTypes(baseCollection, List.of("individual", "state", "kind_of_individual", "class_of_state"));

                supertypes.forEach(st -> {
                        st.forEach(tl -> {
                                System.out.println(tl.getId() + " ");
                        });
                        System.out.println(" ");
                });

                MermaidUtils.writeSupertypeGraph(supertypes, "individual");

                System.out.println("Create Individual data objects!");

                // Create the predicates of the whole-life individual object we want to create.
                // It needs a class (SET) to be a member_of_kind of
                final Thing individualKindOfXObject = IriUtils.createNewBaseObject( baseCollection,
                                new HqdmObjectBaseProperties(
                                                HQDM.KIND_OF_INDIVIDUAL,
                                                baseCollection.PATTERNS_REF_BASE,
                                                "KindOfIndividual_X",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));
                individualKindOfXObject.addStringValue(baseCollection.COMMENT, "Kind_of_X");

                final Thing classOfStateOfXObject = IriUtils.createNewBaseObject( baseCollection,
                                new HqdmObjectBaseProperties(
                                                HQDM.CLASS_OF_STATE,
                                                baseCollection.PATTERNS_REF_BASE,
                                                "ClassOfState_X",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));
                classOfStateOfXObject.addStringValue(baseCollection.COMMENT, "Class_of_state_of_X");

                // The events need a class_of_point_in_time to be members of
                final Thing classOfEventObject = IriUtils.createNewBaseObject( baseCollection,
                                new HqdmObjectBaseProperties(
                                                HQDM.CLASS_OF_EVENT,
                                                baseCollection.PATTERNS_REF_BASE,
                                                "ClassOfEvent_ExampleEvents",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));
                classOfEventObject.addStringValue(baseCollection.COMMENT, "Class_of_event_for_Generic_pattern_examples");

                final Thing classOfGenericPossibleWorldObject = IriUtils.createNewBaseObject( baseCollection,
                                new HqdmObjectBaseProperties(
                                                HQDM.CLASS_OF_POSSIBLE_WORLD,
                                                baseCollection.PATTERNS_REF_BASE,
                                                "ClassOfPossibleWorld__GenericPatternExamples",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));
                classOfGenericPossibleWorldObject.addStringValue(baseCollection.COMMENT,
                                "Class_of_possible_world_for_generic_pattern_examples");

                // Now create the Events and States
                final Thing possibleWorldObject = IriUtils.createNewBaseObject( baseCollection,
                                new HqdmObjectBaseProperties(
                                                HQDM.POSSIBLE_WORLD,
                                                baseCollection.PATTERNS_BASE,
                                                "Possible_World_for_generic_pattern_examples",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));
                possibleWorldObject.addValue(HQDM.MEMBER_OF, classOfGenericPossibleWorldObject.getId());

                // Create the start and end events for individual X
                final Thing individualXBeginningObject = IriUtils.createNewBaseObject( baseCollection,
                                new HqdmObjectBaseProperties(
                                                HQDM.EVENT,
                                                baseCollection.PATTERNS_BASE,
                                                "t0",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));
                individualXBeginningObject.addValue(HQDM.MEMBER_OF, classOfEventObject.getId());
                individualXBeginningObject.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldObject.getId());

                final Thing individualXEndingObject = IriUtils.createNewBaseObject( baseCollection,
                                new HqdmObjectBaseProperties(
                                                HQDM.EVENT,
                                                baseCollection.PATTERNS_BASE,
                                                "t3",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));
                individualXEndingObject.addValue(HQDM.MEMBER_OF, classOfEventObject.getId());
                individualXEndingObject.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldObject.getId());

                final Thing stateOfXBeginningObject = IriUtils.createNewBaseObject( baseCollection,
                                new HqdmObjectBaseProperties(
                                                HQDM.EVENT,
                                                baseCollection.PATTERNS_BASE,
                                                "t1",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));
                stateOfXBeginningObject.addValue(HQDM.MEMBER_OF, classOfEventObject.getId());
                stateOfXBeginningObject.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldObject.getId());

                final Thing stateOfXEndingObject = IriUtils.createNewBaseObject( baseCollection,
                                new HqdmObjectBaseProperties(
                                                HQDM.EVENT,
                                                baseCollection.PATTERNS_BASE,
                                                "t2",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));
                stateOfXEndingObject.addValue(HQDM.MEMBER_OF, classOfEventObject.getId());
                stateOfXEndingObject.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldObject.getId());

                // Create the individual object
                final Thing individualXObject = IriUtils.createNewBaseObject( baseCollection,
                                new HqdmObjectBaseProperties(
                                                HQDM.INDIVIDUAL,
                                                baseCollection.PATTERNS_BASE,
                                                "Example_individual_X",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));
                individualXObject.addValue(HQDM.MEMBER_OF_KIND, individualKindOfXObject.getId());
                individualXObject.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldObject.getId());
                individualXObject.addValue(HQDM.BEGINNING, individualXBeginningObject.getId());
                individualXObject.addValue(HQDM.ENDING, individualXEndingObject.getId());

                // Create the state of X
                final Thing stateOfXObject = IriUtils.createNewBaseObject( baseCollection,
                                new HqdmObjectBaseProperties(
                                                HQDM.STATE,
                                                baseCollection.PATTERNS_BASE,
                                                "Example_state_of_X",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));
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
                individualService.runInWriteTransaction(individualChangeSet);

                // Create node-edge graphs for the individual page
                MermaidUtils.writeLRNodeEdgeGraph(List.of(individualXObject, individualKindOfXObject),
                                List.of("record_created", "record_creator", "comment"),
                                List.of(individualXObject.getId().toString().split("#")[1]),
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
                                stateOfXEndingObject),
                                List.of("record_created", "record_creator", "comment"),
                                List.of(individualXObject.getId().toString().split("#")[1]),
                                "individualExample");

                try {
                        final PrintStream ttl_stream_out = new PrintStream("example-files/individualPattern.ttl");

                        individualService.exportTtl(ttl_stream_out);
                        ttl_stream_out.close();
                        System.out.println("\tData being generated as TTL in example-files/individualPattern.ttl.");

                } catch (FileNotFoundException e) {
                        System.err.println("individualPattern example write: " + e);
                }

                mcDatasets.add(individualService);

        }

}
