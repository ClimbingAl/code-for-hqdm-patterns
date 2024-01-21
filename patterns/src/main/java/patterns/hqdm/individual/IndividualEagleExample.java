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
import uk.gov.gchq.magmacore.hqdm.rdf.iri.IRI;
import uk.gov.gchq.magmacore.service.MagmaCoreService;
import uk.gov.gchq.magmacore.service.MagmaCoreServiceFactory;
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
        public static void createAndAddIndividualPattern(final List<MagmaCoreService> mcDatasets) {

                // Find supertypes
                List<List<Thing>> supertypes = FindSupertypes
                                .findSuperTypes(List.of("functional_object", "state_of_functional_object",
                                                "kind_of_functional_object", "class_of_state_of_functional_object"));

                supertypes.forEach(st -> {
                        st.forEach(tl -> {
                                System.out.println(tl.getId() + " ");
                        });
                        System.out.println(" ");
                });

                MermaidUtils.writeSupertypeGraph(supertypes, "individualEagle");

                System.out.println("Create Individual Eagle data objects!");
                final MagmaCoreService individualService = MagmaCoreServiceFactory.createWithJenaDatabase();
                individualService.register(PatternsUtils.PREFIX_LIST);

                // Create the predicates of the whole-life individual object we want to create.
                // It needs a class (SET) to be a member_of_kind of
                final Thing lunarLanderKindObject = PatternsUtils.createNewBaseObject(
                                new HqdmObjectBaseProperties(
                                                HQDM.KIND_OF_FUNCTIONAL_SYSTEM,
                                                PatternsUtils.PATTERNS_REF_BASE,
                                                "KindOfFunctionalSystem__Lunar_Lander",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                "HqdmPatternProject_User1"));
                lunarLanderKindObject.addStringValue(PatternsUtils.COMMENT,
                                "Kind_of_functional_system_that_is_Lunar_Lander._Note_this_is_subclass_of_class_of_ordinary_functional_object.");

                final Thing lunarLanderClassOfStateObject = PatternsUtils.createNewBaseObject(
                                new HqdmObjectBaseProperties(
                                                HQDM.CLASS_OF_STATE_OF_FUNCTIONAL_SYSTEM,
                                                PatternsUtils.PATTERNS_REF_BASE,
                                                "ClassOfStateOfFunctionalSystem__State_Of_Lunar_Lander",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                "HqdmPatternProject_User1"));
                lunarLanderClassOfStateObject.addStringValue(PatternsUtils.COMMENT,
                                "Class_of_state_of_functional_system_that_is_temporal_part_of_Lunar_Lander");
                lunarLanderClassOfStateObject.addValue(HQDM.PART__OF_BY_CLASS, new IRI(lunarLanderKindObject.getId()));

                // The events need a class_of_point_in_time to be members of
                final Thing classOfPointInTimeObject = PatternsUtils.createNewBaseObject(
                                new HqdmObjectBaseProperties(
                                                HQDM.CLASS_OF_POINT_IN_TIME,
                                                PatternsUtils.PATTERNS_REF_BASE,
                                                "ClassOfPointInTime__ISO8601_DateTime",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                "HqdmPatternProject_User1"));
                classOfPointInTimeObject.addStringValue(PatternsUtils.COMMENT,
                                "Class_of_point_in_time_for_events_in_ISO_8601-1:2019_date-time_format");

                // Class of possible world for the possible worlds used in the Eagle examples
                final Thing classOfPossibleWorldObject = PatternsUtils.createNewBaseObject(
                                new HqdmObjectBaseProperties(
                                                HQDM.CLASS_OF_POSSIBLE_WORLD,
                                                PatternsUtils.PATTERNS_REF_BASE,
                                                "ClassOfPossibleWorld_PatternExamples",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                "HqdmPatternProject_User1"));
                classOfPossibleWorldObject.addStringValue(PatternsUtils.COMMENT,
                                "Class_of_possible_world_for_pattern_examples");

                // Now create the Events and States
                final Thing possibleWorldObject = PatternsUtils.createNewBaseObject(
                                new HqdmObjectBaseProperties(
                                                HQDM.POSSIBLE_WORLD,
                                                PatternsUtils.PATTERNS_BASE,
                                                "Possible_world_for_Apollo_pattern_examples",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                "HqdmPatternProject_User1"));
                possibleWorldObject.addValue(HQDM.MEMBER_OF, new IRI(classOfPossibleWorldObject.getId()));

                // Create the LM-5 lander object
                final Thing lunarLanderObject = PatternsUtils.createNewBaseObject(
                                new HqdmObjectBaseProperties(
                                                HQDM.FUNCTIONAL_SYSTEM,
                                                PatternsUtils.PATTERNS_BASE,
                                                "Example_individual_that_is_Lunar_Module_Eagle_LM-5",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                "HqdmPatternProject_User1"));
                lunarLanderObject.addValue(HQDM.MEMBER_OF_KIND, new IRI(lunarLanderKindObject.getId()));
                lunarLanderObject.addValue(HQDM.PART_OF_POSSIBLE_WORLD, new IRI(possibleWorldObject.getId()));
                // No beginning or ending as these are not known BUT a temporal part is known,
                // the Eagle landing on the Moon

                // Create the start and end events for the lander on the moon
                final ZonedDateTime landedStartDateTime = ZonedDateTime.parse("1969-07-20T20:17:40+00:00[UTC]");
                final ZonedDateTime landedEndDateTime = ZonedDateTime.parse("1969-07-21T17:54:01+00:00[UTC]");

                final Thing eagleMoonLandedBeginningObject = PatternsUtils.createNewBaseObject(
                                new HqdmObjectBaseProperties(
                                                HQDM.POINT_IN_TIME,
                                                PatternsUtils.PATTERNS_BASE,
                                                LocalDateTime.ofInstant(landedStartDateTime.toInstant(), ZoneOffset.UTC)
                                                                .toString(),
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                "HqdmPatternProject_User1"));
                eagleMoonLandedBeginningObject.addValue(HQDM.MEMBER_OF, new IRI(classOfPointInTimeObject.getId()));
                eagleMoonLandedBeginningObject.addValue(HQDM.PART_OF_POSSIBLE_WORLD, new IRI(possibleWorldObject.getId()));

                final Thing eagleMoonLandedEndingObject = PatternsUtils.createNewBaseObject(
                                new HqdmObjectBaseProperties(
                                                HQDM.POINT_IN_TIME,
                                                PatternsUtils.PATTERNS_BASE,
                                                LocalDateTime.ofInstant(landedEndDateTime.toInstant(), ZoneOffset.UTC)
                                                                .toString(),
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                "HqdmPatternProject_User1"));
                eagleMoonLandedEndingObject.addValue(HQDM.MEMBER_OF, new IRI(classOfPointInTimeObject.getId()));
                eagleMoonLandedEndingObject.addValue(HQDM.PART_OF_POSSIBLE_WORLD, new IRI(possibleWorldObject.getId()));

                // Create the state of LM-5 lander object while on the lunar surface
                final Thing stateOfLanderObject = PatternsUtils.createNewBaseObject(
                                new HqdmObjectBaseProperties(
                                                HQDM.STATE_OF_FUNCTIONAL_SYSTEM,
                                                PatternsUtils.PATTERNS_BASE,
                                                "Example_state_of_Lunar_Lander_Module_Eagle_LM-5",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                "HqdmPatternProject_User1"));
                stateOfLanderObject.addValue(HQDM.MEMBER_OF, new IRI(lunarLanderClassOfStateObject.getId()));
                stateOfLanderObject.addValue(HQDM.PART_OF_POSSIBLE_WORLD, new IRI(possibleWorldObject.getId()));
                stateOfLanderObject.addValue(HQDM.BEGINNING, new IRI(eagleMoonLandedBeginningObject.getId()));
                stateOfLanderObject.addValue(HQDM.ENDING, new IRI(eagleMoonLandedEndingObject.getId()));
                stateOfLanderObject.addValue(HQDM.TEMPORAL_PART_OF, new IRI(lunarLanderObject.getId()));

                lunarLanderObject.addValue(HQDM.ENDING, new IRI(eagleMoonLandedEndingObject.getId()));

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
                                List.of(lunarLanderObject.getId().split("#")[1]),
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
                                List.of("member_of", lunarLanderClassOfStateObject.getId().split("#")[1]),
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
                        final PrintStream stmt_stream_out = new PrintStream(
                                        "example-files/individualEaglePattern.stmt");

                        individualService.exportTtl(ttl_stream_out);
                        ttl_stream_out.close();
                        System.out.println(
                                        "\tData being generated as TTL in example-files/individualEaglePattern.ttl.");

                        individualService.exportStatements(stmt_stream_out);
                        stmt_stream_out.close();
                        System.out.println(
                                        "\tData generated as statements in example-files/individualEaglePattern.stmt.");

                } catch (FileNotFoundException e) {
                        System.err.println("individualEaglePattern example write: " + e);
                }

                mcDatasets.add(individualService);

        }

}
