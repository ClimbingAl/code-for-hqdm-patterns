package patterns.hqdm.activity;

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
import hqdm.utils.query.QueryUtils;
import uk.gov.gchq.magmacore.hqdm.model.Thing;
import uk.gov.gchq.magmacore.hqdm.rdf.iri.HQDM;
import uk.gov.gchq.magmacore.hqdm.rdf.iri.IRI;
import uk.gov.gchq.magmacore.service.MagmaCoreService;
import uk.gov.gchq.magmacore.service.MagmaCoreServiceFactory;
import uk.gov.gchq.magmacore.service.transformation.DbTransformation;

public class ActivityExample {

        /**
         * Create a new Activity pattern, construct DbTransformation for them
         * and then commit to database. Write example files from the HQDM objects.
         *
         * @param mcDatasets {@link List<MagmaCoreService>}.
         * @return
         */
        public static void createAndAddActivityPattern( BaseCollection baseCollection, final List<MagmaCoreService> mcDatasets, MagmaCoreService activityService, String record_creator ) {

                // Find supertypes
                List<List<Thing>> supertypes = FindSupertypes.findSuperTypes( baseCollection,
                                List.of("activity", "participant", "kind_of_activity", "role"));

                MermaidUtils.writeSupertypeGraph(supertypes, "activityTypes");

                System.out.println("Create generic activity data objects.");

                // Find existing objects that are members of the righ class of possible world
                final Thing possibleWorldForGenericExamples = QueryUtils.findThingInServiceByName(
                                mcDatasets,
                                "Possible_World_for_generic_pattern_examples");
                final List<Thing> genericPossibleWorldThings = QueryUtils.findThingsInServicesByPredicateAndValue(
                                mcDatasets,
                                HQDM.PART_OF_POSSIBLE_WORLD,
                                possibleWorldForGenericExamples.getId());

                // Create new reference data classes
                // Kind_of_activity, Roles, Participants

                final Thing kindOfGenericActivityObject = IriUtils.createNewBaseObject( baseCollection,
                                new HqdmObjectBaseProperties(
                                                HQDM.KIND_OF_ACTIVITY,
                                                baseCollection.PATTERNS_REF_BASE,
                                                "KindOfActivity__GenericKindOfActivity",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));
                kindOfGenericActivityObject.addStringValue(
                                baseCollection.COMMENT,
                                "Kind_of_activity_for_generic_pattern_examples");

                final Thing roleOfXThingObject = IriUtils.createNewBaseObject( baseCollection,
                                new HqdmObjectBaseProperties(
                                                HQDM.ROLE,
                                                baseCollection.PATTERNS_REF_BASE,
                                                "RoleOfXInAKindOfActivity",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));
                roleOfXThingObject.addStringValue(baseCollection.COMMENT,
                                "Role_of_X_in_a_kind_of_activity_for_generic_pattern_examples");
                roleOfXThingObject.addValue(HQDM.PART_OF_BY_CLASS_, kindOfGenericActivityObject.getId());

                final Thing roleOfZThingObject = IriUtils.createNewBaseObject( baseCollection,
                        new HqdmObjectBaseProperties(
                                HQDM.ROLE,
                                baseCollection.PATTERNS_REF_BASE,
                                "RoleOfZInAKindOfActivity",
                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                record_creator));
                roleOfZThingObject.addStringValue(baseCollection.COMMENT,
                                "Role_of_Z_in_a_kind_of_activity_for_generic_pattern_examples");
                roleOfZThingObject.addValue(HQDM.PART_OF_BY_CLASS_, kindOfGenericActivityObject.getId());

                // Now add these classes to the kindOfGenericActivity
                kindOfGenericActivityObject.addValue(HQDM.CONSISTS_OF_BY_CLASS, roleOfXThingObject.getId());
                kindOfGenericActivityObject.addValue(HQDM.CONSISTS_OF_BY_CLASS, roleOfZThingObject.getId());

                final Thing individualKindOfZObject = IriUtils.createNewBaseObject( baseCollection,
                                new HqdmObjectBaseProperties(
                                                HQDM.KIND_OF_INDIVIDUAL,
                                                baseCollection.PATTERNS_REF_BASE,
                                                "KindOfIndividual_Z",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));
                individualKindOfZObject.addStringValue(baseCollection.COMMENT, "Kind_of_Z");

                final Thing classOfStateOfZObject = IriUtils.createNewBaseObject( baseCollection,
                                new HqdmObjectBaseProperties(
                                                HQDM.CLASS_OF_STATE,
                                                baseCollection.PATTERNS_REF_BASE,
                                                "ClassOfState_Z",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));
                classOfStateOfZObject.addStringValue(baseCollection.COMMENT, "Class_of_state_of_Z");

                // Now generate the three states that form the generic activity between a state
                // of X and a state of a new individual Z
                // Create the state of X that is participant in this Activity example
                final Thing stateOfXParticipantObject = IriUtils.createNewBaseObject( baseCollection,
                                new HqdmObjectBaseProperties(
                                                HQDM.PARTICIPANT,
                                                baseCollection.PATTERNS_BASE,
                                                "Example_participant_state_of_X",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));
                stateOfXParticipantObject.addValue(HQDM.MEMBER_OF_KIND, roleOfXThingObject.getId());
                stateOfXParticipantObject.addValue(HQDM.MEMBER_OF,
                                QueryUtils.findThingInServiceByName(mcDatasets, "ClassOfState_X").getId());
                stateOfXParticipantObject.addValue(HQDM.PART_OF_POSSIBLE_WORLD,
                                QueryUtils.findThingInServiceByName(mcDatasets,
                                                "Possible_World_for_generic_pattern_examples").getId());
                stateOfXParticipantObject.addValue(HQDM.BEGINNING,
                                QueryUtils.findThingInServiceByName(mcDatasets, "t1").getId());
                stateOfXParticipantObject.addValue(HQDM.ENDING,
                                QueryUtils.findThingInServiceByName(mcDatasets, "t2").getId());
                stateOfXParticipantObject.addValue(HQDM.TEMPORAL_PART_OF,
                                QueryUtils.findThingByNameInList(genericPossibleWorldThings, "Example_individual_X")
                                                .getId());

                final Thing individualZObject = IriUtils.createNewBaseObject( baseCollection,
                                new HqdmObjectBaseProperties(
                                                HQDM.INDIVIDUAL,
                                                baseCollection.PATTERNS_BASE,
                                                "Example_individual_Z",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));
                individualZObject.addValue(HQDM.MEMBER_OF_KIND, individualKindOfZObject.getId());
                individualZObject.addValue(HQDM.PART_OF_POSSIBLE_WORLD,
                                QueryUtils.findThingInServiceByName(mcDatasets,
                                                "Possible_World_for_generic_pattern_examples").getId());
                individualZObject.addValue(HQDM.BEGINNING,
                                QueryUtils.findThingInServiceByName(mcDatasets, "t0").getId());
                individualZObject.addValue(HQDM.ENDING,
                        QueryUtils.findThingInServiceByName(mcDatasets, "t3").getId());

                // Create the state of X that is participant in this Activity example
                final Thing stateOfZParticipantObject = IriUtils.createNewBaseObject( baseCollection,
                                new HqdmObjectBaseProperties(
                                                HQDM.PARTICIPANT,
                                                baseCollection.PATTERNS_BASE,
                                                "Example_participant_state_of_Z",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));
                stateOfZParticipantObject.addValue(HQDM.MEMBER_OF_KIND, roleOfZThingObject.getId());
                stateOfZParticipantObject.addValue(HQDM.MEMBER_OF, classOfStateOfZObject.getId());
                stateOfZParticipantObject.addValue(HQDM.PART_OF_POSSIBLE_WORLD,
                                QueryUtils.findThingInServiceByName(mcDatasets,
                                                "Possible_World_for_generic_pattern_examples").getId());
                stateOfZParticipantObject.addValue(HQDM.BEGINNING,
                                QueryUtils.findThingInServiceByName(mcDatasets, "t1").getId());
                stateOfZParticipantObject.addValue(HQDM.ENDING,
                                QueryUtils.findThingInServiceByName(mcDatasets, "t2").getId());
                stateOfZParticipantObject.addValue(HQDM.TEMPORAL_PART_OF, individualZObject.getId());

                // Now make Activity
                final Thing activityXZObject = IriUtils.createNewBaseObject( baseCollection,
                                new HqdmObjectBaseProperties(
                                                HQDM.ACTIVITY,
                                                baseCollection.PATTERNS_BASE,
                                                "Example_activity_involving_X_and_Z",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));
                activityXZObject.addValue(HQDM.MEMBER_OF_KIND, kindOfGenericActivityObject.getId());
                activityXZObject.addValue(HQDM.BEGINNING,
                                QueryUtils.findThingInServiceByName(mcDatasets, "t1").getId());
                activityXZObject.addValue(HQDM.ENDING,
                                QueryUtils.findThingInServiceByName(mcDatasets, "t2").getId());
                activityXZObject.addValue(HQDM.PART_OF_POSSIBLE_WORLD,
                                QueryUtils.findThingInServiceByName(mcDatasets,
                                                "Possible_World_for_generic_pattern_examples").getId());
                activityXZObject.addValue(HQDM.CONSISTS_OF_PARTICIPANT, stateOfXParticipantObject.getId());
                activityXZObject.addValue(HQDM.CONSISTS_OF_PARTICIPANT, stateOfZParticipantObject.getId());

                stateOfXParticipantObject.addValue(HQDM.PARTICIPANT_IN, activityXZObject.getId());
                stateOfZParticipantObject.addValue(HQDM.PARTICIPANT_IN, activityXZObject.getId());

                // Caused event example
                // The events need a class_of_point_in_time to be members of
                final Thing classOfCausedEventObject = IriUtils.createNewBaseObject( baseCollection,
                                new HqdmObjectBaseProperties(
                                                HQDM.CLASS_OF_EVENT,
                                                baseCollection.PATTERNS_REF_BASE,
                                                "ClassOfEvent_ExampleCausedEvent",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));
                classOfCausedEventObject.addStringValue(baseCollection.COMMENT,
                                "Class_of_caused_event_for_generic_pattern_examples");

                final Thing completedEvent = IriUtils.createNewBaseObject( baseCollection,
                                new HqdmObjectBaseProperties(
                                                HQDM.EVENT,
                                                baseCollection.PATTERNS_BASE,
                                                "Example_activity_completed_event",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));
                completedEvent.addValue(HQDM.PART_OF_POSSIBLE_WORLD,
                                QueryUtils.findThingInServiceByName(
                                                mcDatasets,
                                                "Possible_World_for_generic_pattern_examples").getId());
                completedEvent.addValue(HQDM.MEMBER_OF, classOfCausedEventObject.getId());
                activityXZObject.addValue(HQDM.CAUSES, completedEvent.getId());

                final DbTransformation activityChangeSet = activityService.createDbTransformation(List.of(
                                classOfStateOfZObject,
                                individualKindOfZObject,
                                individualZObject,
                                kindOfGenericActivityObject,
                                roleOfXThingObject,
                                roleOfZThingObject,
                                stateOfXParticipantObject,
                                stateOfZParticipantObject,
                                activityXZObject,
                                completedEvent,
                                classOfCausedEventObject));
                activityService.runInWriteTransaction(activityChangeSet);

                // Output files

                // Create node-edge graphs for the individual page
                MermaidUtils.writeLRNodeEdgeGraph(
                                List.of(activityXZObject, stateOfXParticipantObject, stateOfZParticipantObject),
                                List.of("record_created", "record_creator", "comment"),
                                List.of(activityXZObject.getId().toString().split("#")[1]),
                                "genericActivityAndParticipants");

                MermaidUtils.writeLRNodeEdgeGraph(List.of(
                                activityXZObject,
                                stateOfXParticipantObject,
                                stateOfZParticipantObject,
                                classOfStateOfZObject,
                                individualKindOfZObject,
                                individualZObject,
                                kindOfGenericActivityObject,
                                roleOfXThingObject,
                                roleOfZThingObject,
                                classOfCausedEventObject,
                                completedEvent),
                                List.of("record_created", "record_creator", "comment"),
                                List.of(activityXZObject.getId().toString().split("#")[1],
                                                stateOfXParticipantObject.getId().toString().split("#")[1],
                                                stateOfZParticipantObject.getId().toString().split("#")[1]),
                                "genericActivityAndParticipantsFull");

                try {
                        final PrintStream ttl_stream_out = new PrintStream("example-files/activityGenericPattern.ttl");

                        activityService.exportTtl(ttl_stream_out);
                        ttl_stream_out.close();
                        System.out.println(
                                        "\tData being generated as TTL in example-files/activityGenericPattern.ttl.");

                } catch (FileNotFoundException e) {
                        System.err.println("activityGenericPattern example write: " + e);
                }

                mcDatasets.add(activityService);

        }
}
