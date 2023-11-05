package patterns.hqdm.association;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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

public class AssociationExample {

        /**
         * Create a new Association pattern, construct DbTransformation for them
         * and then commit to database.
         *
         * @param mcDatasets {@link List<MagmaCoreService>}.
         * @return
         */
        public static void createAndAddAssociationPattern(final List<MagmaCoreService> mcDatasets) {

                // Find supertypes
                List<List<Thing>> supertypes = FindSupertypes
                                .findSuperTypes(List.of("association", "participant", "kind_of_association", "role"));

                supertypes.forEach(st -> {
                        st.forEach(tl -> {
                                System.out.println(tl.getId() + " ");
                        });
                        System.out.println(" ");
                });

                MermaidUtils.writeSupertypeGraph(supertypes, "associationTypes");

                System.out.println("Create generic association data objects.");
                final MagmaCoreService associationService = MagmaCoreServiceFactory.createWithJenaDatabase();
                associationService.register(PatternsUtils.PREFIX_LIST);

                // Find existing objects that are members of
                // "Class_of_possible_world_for_abstract_pattern_examples"
                final Thing possibleWorldForGenericExamples = QueryUtils.findThingInServiceByName(mcDatasets,
                                "Possible_World_for_generic_pattern_examples");
                final List<Thing> genericPossibleWorldThings = QueryUtils.findThingsInServiceByPredicateAndValue(
                                mcDatasets,
                                HQDM.PART_OF_POSSIBLE_WORLD,
                                possibleWorldForGenericExamples.getId());

                // Create new reference data classes
                // Kind_of_association, Roles, Participants

                final Thing kindOfGenericAssociationObject = PatternsUtils
                                .createNewBaseObject(new HqdmObjectBaseProperties(
                                                HQDM.KIND_OF_ASSOCIATION,
                                                PatternsUtils.PATTERNS_REF_BASE,
                                                "KindOfAssociation__GenericKindOfAssociation",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                "HqdmPatternProject_User1"));
                kindOfGenericAssociationObject.addStringValue(PatternsUtils.COMMENT,
                                "Kind_of_association_for_generic_pattern_examples");

                final Thing roleOfXThingObject = PatternsUtils.createNewBaseObject(new HqdmObjectBaseProperties(
                                HQDM.ROLE,
                                PatternsUtils.PATTERNS_REF_BASE,
                                "RoleOfXInKindOfAssociation",
                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                "HqdmPatternProject_User1"));
                roleOfXThingObject.addStringValue(PatternsUtils.COMMENT,
                                "Role_of_X_in_a_kind_of_association_for_generic_pattern_examples");
                roleOfXThingObject.addValue(HQDM.PART_OF_BY_CLASS_, kindOfGenericAssociationObject.getId());

                final Thing roleOfYThingObject = PatternsUtils.createNewBaseObject(new HqdmObjectBaseProperties(
                                HQDM.ROLE,
                                PatternsUtils.PATTERNS_REF_BASE,
                                "RoleOfYInKindOfAssociation",
                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                "HqdmPatternProject_User1"));
                roleOfYThingObject.addStringValue(PatternsUtils.COMMENT,
                                "Role_of_Y_in_a_kind_of_association_for_generic_pattern_examples");
                roleOfYThingObject.addValue(HQDM.PART_OF_BY_CLASS_, kindOfGenericAssociationObject.getId());

                // Now add these classes to the kindOfGenericAssociation
                kindOfGenericAssociationObject.addValue(HQDM.CONSISTS_OF_BY_CLASS, roleOfXThingObject.getId());
                kindOfGenericAssociationObject.addValue(HQDM.CONSISTS_OF_BY_CLASS, roleOfYThingObject.getId());

                final Thing individualKindOfYObject = PatternsUtils.createNewBaseObject(
                                new HqdmObjectBaseProperties(
                                                HQDM.KIND_OF_INDIVIDUAL,
                                                PatternsUtils.PATTERNS_REF_BASE,
                                                "KindOfIndividual_Y",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                "HqdmPatternProject_User1"));
                individualKindOfYObject.addStringValue(PatternsUtils.COMMENT, "Kind_of_Y");

                final Thing classOfStateOfYObject = PatternsUtils.createNewBaseObject(
                                new HqdmObjectBaseProperties(
                                                HQDM.CLASS_OF_STATE,
                                                PatternsUtils.PATTERNS_REF_BASE,
                                                "ClassOfState_Y",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                "HqdmPatternProject_User1"));
                classOfStateOfYObject.addStringValue(PatternsUtils.COMMENT, "Class_of_state_of_Y");

                // Now generate the three states that form the generic association between a
                // state of X and a state of a new individual Y
                // Create the state of X that is participant in this Association example
                final Thing stateOfXParticipantObject = PatternsUtils.createNewBaseObject(
                                new HqdmObjectBaseProperties(
                                                HQDM.PARTICIPANT,
                                                PatternsUtils.PATTERNS_BASE,
                                                "Example_participant_state_of_X",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                "HqdmPatternProject_User1"));
                stateOfXParticipantObject.addValue(HQDM.MEMBER_OF_KIND, roleOfXThingObject.getId());
                stateOfXParticipantObject.addValue(HQDM.MEMBER_OF,
                                QueryUtils.findThingInServiceByName(mcDatasets, "ClassOfState_X").getId());
                stateOfXParticipantObject.addValue(HQDM.PART_OF_POSSIBLE_WORLD, QueryUtils
                                .findThingInServiceByName(mcDatasets, "Possible_World_for_generic_pattern_examples")
                                .getId());
                stateOfXParticipantObject.addValue(HQDM.BEGINNING,
                                QueryUtils.findThingInServiceByName(mcDatasets, "t1").getId());
                stateOfXParticipantObject.addValue(HQDM.ENDING,
                                QueryUtils.findThingInServiceByName(mcDatasets, "t2").getId());
                stateOfXParticipantObject.addValue(HQDM.TEMPORAL_PART_OF, QueryUtils
                                .findThingByNameInList(genericPossibleWorldThings, "Example_individual_X").getId());

                final Thing individualYObject = PatternsUtils.createNewBaseObject(
                                new HqdmObjectBaseProperties(
                                                HQDM.INDIVIDUAL,
                                                PatternsUtils.PATTERNS_BASE,
                                                "Example_individual_Y",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                "HqdmPatternProject_User1"));
                individualYObject.addValue(HQDM.MEMBER_OF_KIND, individualKindOfYObject.getId());
                individualYObject.addValue(HQDM.PART_OF_POSSIBLE_WORLD, QueryUtils
                                .findThingInServiceByName(mcDatasets, "Possible_World_for_generic_pattern_examples")
                                .getId());
                individualYObject.addValue(HQDM.BEGINNING,
                                QueryUtils.findThingInServiceByName(mcDatasets, "t0").getId());
                individualYObject.addValue(HQDM.ENDING, QueryUtils.findThingInServiceByName(mcDatasets, "t3").getId());

                // Create the state of X that is participant in this Association example
                final Thing stateOfYParticipantObject = PatternsUtils.createNewBaseObject(
                                new HqdmObjectBaseProperties(
                                                HQDM.PARTICIPANT,
                                                PatternsUtils.PATTERNS_BASE,
                                                "Example_participant_state_of_Y",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                "HqdmPatternProject_User1"));
                stateOfYParticipantObject.addValue(HQDM.MEMBER_OF_KIND, roleOfYThingObject.getId());
                stateOfYParticipantObject.addValue(HQDM.MEMBER_OF, classOfStateOfYObject.getId());
                stateOfYParticipantObject.addValue(HQDM.PART_OF_POSSIBLE_WORLD, QueryUtils
                                .findThingInServiceByName(mcDatasets, "Possible_World_for_generic_pattern_examples")
                                .getId());
                stateOfYParticipantObject.addValue(HQDM.BEGINNING,
                                QueryUtils.findThingInServiceByName(mcDatasets, "t1").getId());
                stateOfYParticipantObject.addValue(HQDM.ENDING,
                                QueryUtils.findThingInServiceByName(mcDatasets, "t2").getId());
                stateOfYParticipantObject.addValue(HQDM.TEMPORAL_PART_OF, individualYObject.getId());

                // Now make Association
                final Thing associationXYObject = PatternsUtils.createNewBaseObject(
                                new HqdmObjectBaseProperties(
                                                HQDM.ASSOCIATION,
                                                PatternsUtils.PATTERNS_BASE,
                                                "Example_association_between_X_and_Y",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                "HqdmPatternProject_User1"));
                associationXYObject.addValue(HQDM.MEMBER_OF_KIND, kindOfGenericAssociationObject.getId());
                associationXYObject.addValue(HQDM.BEGINNING,
                                QueryUtils.findThingInServiceByName(mcDatasets, "t1").getId());
                associationXYObject.addValue(HQDM.ENDING,
                                QueryUtils.findThingInServiceByName(mcDatasets, "t2").getId());
                associationXYObject.addValue(HQDM.PART_OF_POSSIBLE_WORLD, QueryUtils
                                .findThingInServiceByName(mcDatasets, "Possible_World_for_generic_pattern_examples")
                                .getId());
                associationXYObject.addValue(HQDM.CONSISTS_OF_PARTICIPANT, stateOfXParticipantObject.getId());
                associationXYObject.addValue(HQDM.CONSISTS_OF_PARTICIPANT, stateOfYParticipantObject.getId());

                stateOfXParticipantObject.addValue(HQDM.PARTICIPANT_IN, associationXYObject.getId());
                stateOfYParticipantObject.addValue(HQDM.PARTICIPANT_IN, associationXYObject.getId());

                final DbTransformation associationChangeSet = associationService.createDbTransformation(
                                List.of(
                                                classOfStateOfYObject,
                                                individualKindOfYObject,
                                                individualYObject,
                                                kindOfGenericAssociationObject,
                                                roleOfXThingObject,
                                                roleOfYThingObject,
                                                stateOfXParticipantObject,
                                                stateOfYParticipantObject,
                                                associationXYObject));
                associationService.runInTransaction(associationChangeSet);

                // Output files

                // Create node-edge graphs for the individual page
                MermaidUtils.writeLRNodeEdgeGraph(
                                List.of(associationXYObject, stateOfXParticipantObject, stateOfYParticipantObject),
                                List.of("record_created", "record_creator", "comment"),
                                List.of(associationXYObject.getId().split("#")[1]),
                                "genericAssociationAndParticipants");

                MermaidUtils.writeLRNodeEdgeGraph(List.of(
                                associationXYObject,
                                stateOfXParticipantObject,
                                stateOfYParticipantObject,
                                classOfStateOfYObject,
                                individualKindOfYObject,
                                individualYObject,
                                kindOfGenericAssociationObject,
                                roleOfXThingObject,
                                roleOfYThingObject),
                                List.of("record_created", "record_creator", "comment"),
                                List.of(associationXYObject.getId().split("#")[1],
                                                stateOfXParticipantObject.getId().split("#")[1],
                                                stateOfYParticipantObject.getId().split("#")[1]),
                                "genericAssociationAndParticipantsFull");

                try {
                        final PrintStream ttl_stream_out = new PrintStream(
                                        "example-files/associationGenericPattern.ttl");
                        final PrintStream stmt_stream_out = new PrintStream(
                                        "example-files/associationGenericPattern.stmt");

                        associationService.exportTtl(ttl_stream_out);
                        ttl_stream_out.close();
                        System.out.println(
                                        "\tData being generated as TTL in example-files/associationGenericPattern.ttl.");

                        associationService.exportStatements(stmt_stream_out);
                        stmt_stream_out.close();
                        System.out.println(
                                        "\tData generated as statements in example-files/associationGenericPattern.stmt.");

                } catch (FileNotFoundException e) {
                        System.err.println("associationGenericPattern example write: " + e);
                }

                mcDatasets.add(associationService);

        }

}
