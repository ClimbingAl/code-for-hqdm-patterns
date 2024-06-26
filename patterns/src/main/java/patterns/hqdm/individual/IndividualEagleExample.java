package patterns.hqdm.individual;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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

public class IndividualEagleExample {

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
                                .findSuperTypes(baseCollection, List.of("functional_object", "state_of_functional_object",
                                                "kind_of_functional_object", "class_of_state_of_functional_object"));

                supertypes.forEach(st -> {
                        st.forEach(tl -> {
                                System.out.println(tl.getId() + " ");
                        });
                        System.out.println(" ");
                });

                MermaidUtils.writeSupertypeGraph(supertypes, "individualEagle");

                System.out.println("Create Individual Eagle data objects!");

                // Create the predicates of the whole-life individual object we want to create.
                // It needs a class (SET) to be a member_of_kind of
                final Thing lunarLanderKindObject = IriUtils.createNewBaseObject( baseCollection,
                                new HqdmObjectBaseProperties(
                                                HQDM.KIND_OF_FUNCTIONAL_SYSTEM,
                                                baseCollection.PATTERNS_REF_BASE,
                                                "KindOfFunctionalSystem__Lunar_Lander",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));
                lunarLanderKindObject.addStringValue(baseCollection.COMMENT,
                                "Kind_of_functional_system_that_is_Lunar_Lander._Note_this_is_subclass_of_class_of_ordinary_functional_object.");

                final Thing lunarLanderClassOfStateObject = IriUtils.createNewBaseObject( baseCollection,
                                new HqdmObjectBaseProperties(
                                                HQDM.CLASS_OF_STATE_OF_FUNCTIONAL_SYSTEM,
                                                baseCollection.PATTERNS_REF_BASE,
                                                "ClassOfStateOfFunctionalSystem__State_Of_Lunar_Lander",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));
                lunarLanderClassOfStateObject.addStringValue(baseCollection.COMMENT,
                                "Class_of_state_of_functional_system_that_is_temporal_part_of_Lunar_Lander");
                lunarLanderClassOfStateObject.addValue(HQDM.PART__OF_BY_CLASS, lunarLanderKindObject.getId());

                // The events need a class_of_point_in_time to be members of
                final Thing classOfPointInTimeObject = IriUtils.createNewBaseObject( baseCollection,
                                new HqdmObjectBaseProperties(
                                                HQDM.CLASS_OF_POINT_IN_TIME,
                                                baseCollection.PATTERNS_REF_BASE,
                                                "ClassOfPointInTime__ISO8601_DateTime",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));
                classOfPointInTimeObject.addStringValue(baseCollection.COMMENT,
                                "Class_of_point_in_time_for_events_in_ISO_8601-1:2019_date-time_format");

                // Class of possible world for the possible worlds used in the Eagle examples
                final Thing classOfPossibleWorldObject = IriUtils.createNewBaseObject( baseCollection,
                                new HqdmObjectBaseProperties(
                                                HQDM.CLASS_OF_POSSIBLE_WORLD,
                                                baseCollection.PATTERNS_REF_BASE,
                                                "ClassOfPossibleWorld_PatternExamples",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));
                classOfPossibleWorldObject.addStringValue(baseCollection.COMMENT,
                                "Class_of_possible_world_for_pattern_examples");

                // Now create the Events and States
                final Thing possibleWorldObject = IriUtils.createNewBaseObject( baseCollection,
                                new HqdmObjectBaseProperties(
                                                HQDM.POSSIBLE_WORLD,
                                                baseCollection.PATTERNS_BASE,
                                                "Possible_world_for_Apollo_pattern_examples",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));
                possibleWorldObject.addValue(HQDM.MEMBER_OF, classOfPossibleWorldObject.getId());

                // Create the LM-5 lander object
                final Thing lunarLanderObject = IriUtils.createNewBaseObject( baseCollection,
                                new HqdmObjectBaseProperties(
                                                HQDM.FUNCTIONAL_SYSTEM,
                                                baseCollection.PATTERNS_BASE,
                                                "Example_individual_that_is_Lunar_Module_Eagle_LM-5",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));
                lunarLanderObject.addValue(HQDM.MEMBER_OF_KIND, lunarLanderKindObject.getId());
                lunarLanderObject.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldObject.getId());
                // No beginning or ending as these are not known BUT a temporal part is known,
                // the Eagle landing on the Moon

                // Create the start and end events for the lander on the moon
                final ZonedDateTime landedStartDateTime = ZonedDateTime.parse("1969-07-20T20:17:40+00:00[UTC]");
                final ZonedDateTime landedEndDateTime = ZonedDateTime.parse("1969-07-21T17:54:01+00:00[UTC]");

                final Thing eagleMoonLandedBeginningObject = IriUtils.createNewBaseObject( baseCollection,
                                new HqdmObjectBaseProperties(
                                                HQDM.POINT_IN_TIME,
                                                baseCollection.PATTERNS_BASE,
                                                LocalDateTime.ofInstant(landedStartDateTime.toInstant(), ZoneOffset.UTC)
                                                                .toString(),
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));
                eagleMoonLandedBeginningObject.addValue(HQDM.MEMBER_OF, classOfPointInTimeObject.getId());
                eagleMoonLandedBeginningObject.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldObject.getId());

                final Thing eagleMoonLandedEndingObject = IriUtils.createNewBaseObject( baseCollection,
                                new HqdmObjectBaseProperties(
                                                HQDM.POINT_IN_TIME,
                                                baseCollection.PATTERNS_BASE,
                                                LocalDateTime.ofInstant(landedEndDateTime.toInstant(), ZoneOffset.UTC)
                                                                .toString(),
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));
                eagleMoonLandedEndingObject.addValue(HQDM.MEMBER_OF, classOfPointInTimeObject.getId());
                eagleMoonLandedEndingObject.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldObject.getId());

                // Create the state of LM-5 lander object while on the lunar surface
                final Thing stateOfLanderObject = IriUtils.createNewBaseObject( baseCollection,
                                new HqdmObjectBaseProperties(
                                                HQDM.STATE_OF_FUNCTIONAL_SYSTEM,
                                                baseCollection.PATTERNS_BASE,
                                                "Example_state_of_Lunar_Lander_Module_Eagle_LM-5",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));
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
                individualService.runInWriteTransaction(individualChangeSet);

                // Create node-edge graphs for the individual page
                MermaidUtils.writeLRNodeEdgeGraph(List.of(lunarLanderObject, lunarLanderKindObject),
                                List.of("record_created", "record_creator", "comment"),
                                List.of(lunarLanderObject.getId().toString().split("#")[1]),
                                "individualAndKindEagleExample");

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
                                List.of(lunarLanderObject.getId().toString().split("#")[1]),
                                "individualEagleExample");

                // Temporal Parts graphs
                MermaidUtils.writeLRNodeEdgeGraph(List.of(
                                stateOfLanderObject,
                                eagleMoonLandedBeginningObject,
                                eagleMoonLandedEndingObject),
                                List.of("record_created", "record_creator", "comment", "member_of_kind", "member_of",
                                                "temporal_part_of", "part_of_possible_world"),
                                List.of("beginning", "ending"),
                                "temporalPartOfIncEvents");

                MermaidUtils.writeLRNodeEdgeGraph(List.of(lunarLanderObject, stateOfLanderObject),
                                List.of("record_created", "record_creator", "comment", "member_of_kind", "member_of",
                                                "part_of_possible_world", "beginning", "ending"),
                                List.of("temporal_part_of"),
                                "temporalPartOf");

                MermaidUtils.writeLRNodeEdgeGraph(List.of(lunarLanderObject, stateOfLanderObject),
                                List.of("record_created", "record_creator", "comment", "member_of_kind", "member_of",
                                                "beginning", "ending"),
                                List.of("temporal_part_of", "part_of_possible_world"),
                                "temporalPartOfAllRels");

                // Set Membership
                MermaidUtils.writeLRNodeEdgeGraph(List.of(stateOfLanderObject, lunarLanderClassOfStateObject),
                                List.of("record_created", "record_creator", "comment", "member_of_kind",
                                                "temporal_part_of", "part_of_possible_world", "beginning", "ending"),
                                List.of("member_of", lunarLanderClassOfStateObject.getId().toString().split("#")[1]),
                                "member_of");

                MermaidUtils.writeLRNodeEdgeGraph(List.of(lunarLanderObject, lunarLanderKindObject),
                                List.of("record_created", "record_creator", "comment", "member_of", "temporal_part_of",
                                                "part_of_possible_world", "beginning", "ending"),
                                List.of("member_of_kind"),
                                "member_of_kind");

                // Order
                MermaidUtils.writeLRNodeEdgeGraph(List.of(stateOfLanderObject),
                                List.of("record_created", "record_creator", "comment", "member_of_kind",
                                                "part_of_possible_world", "member_of"),
                                List.of("beginning", "ending"),
                                "orderEvents");

                MermaidUtils.writeLRNodeEdgeGraph(List.of(stateOfLanderObject),
                                List.of("record_created", "record_creator", "comment", "member_of_kind",
                                                "part_of_possible_world", "beginning", "ending", "member_of",
                                                "data_EntityName", "type"),
                                List.of("temporal_part_of"),
                                "rel");

                try {
                        final PrintStream ttl_stream_out = new PrintStream("example-files/individualEaglePattern.ttl");

                        individualService.exportTtl(ttl_stream_out);
                        ttl_stream_out.close();
                        System.out.println(
                                        "\tData being generated as TTL in example-files/individualEaglePattern.ttl.");

                } catch (FileNotFoundException e) {
                        System.err.println("individualEaglePattern example write: " + e);
                }

                mcDatasets.add(individualService);

        }

}
