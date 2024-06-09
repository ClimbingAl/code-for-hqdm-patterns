package patterns.hqdm.association;

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

public class AssociationExample {

        /**
         * Create a new Association pattern, construct DbTransformation for them
         * and then commit to database.
         *
         * @param mcDatasets {@link List<MagmaCoreService>}.
         * @return
         */
        public static void createAndAddAssociationPattern(BaseCollection baseCollection, final List<MagmaCoreService> mcDatasets, MagmaCoreService associationService, String record_creator) {

                // Find supertypes
                List<List<Thing>> supertypes = FindSupertypes
                                .findSuperTypes(baseCollection, List.of("association", "participant", "kind_of_association", "role"));

                supertypes.forEach(st -> {
                        st.forEach(tl -> {
                                System.out.println(tl.getId() + " ");
                        });
                        System.out.println(" ");
                });

                MermaidUtils.writeSupertypeGraph(supertypes, "associationTypes");

                System.out.println("Create generic association data objects.");

                // Find existing objects that are members of
                // "Class_of_possible_world_for_abstract_pattern_examples"
                final Thing possibleWorldForGenericExamples = QueryUtils.findThingInServiceByName(mcDatasets,
                                "Possible_World_for_generic_pattern_examples");
                final List<Thing> genericPossibleWorldThings = QueryUtils.findThingsInServicesByPredicateAndValue(
                                mcDatasets,
                                HQDM.PART_OF_POSSIBLE_WORLD,
                                possibleWorldForGenericExamples.getId());

                // Create new reference data classes
                // Kind_of_association, Roles, Participants

                final Thing kindOfGenericAssociationObject = IriUtils.createNewBaseObject( baseCollection,
                                new HqdmObjectBaseProperties(
                                                HQDM.KIND_OF_ASSOCIATION,
                                                baseCollection.PATTERNS_REF_BASE,
                                                "KindOfAssociation__GenericKindOfAssociation",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));
                kindOfGenericAssociationObject.addStringValue(baseCollection.COMMENT,
                                "Kind_of_association_for_generic_pattern_examples");

                final Thing roleOfXThingObject = IriUtils.createNewBaseObject( baseCollection,new HqdmObjectBaseProperties(
                                HQDM.ROLE,
                                baseCollection.PATTERNS_REF_BASE,
                                "RoleOfXInKindOfAssociation",
                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                record_creator));
                roleOfXThingObject.addStringValue(baseCollection.COMMENT,
                                "Role_of_X_in_a_kind_of_association_for_generic_pattern_examples");
                roleOfXThingObject.addValue(HQDM.PART_OF_BY_CLASS_, kindOfGenericAssociationObject.getId());

                final Thing roleOfYThingObject = IriUtils.createNewBaseObject( baseCollection,new HqdmObjectBaseProperties(
                                HQDM.ROLE,
                                baseCollection.PATTERNS_REF_BASE,
                                "RoleOfYInKindOfAssociation",
                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                record_creator));
                roleOfYThingObject.addStringValue(baseCollection.COMMENT,
                                "Role_of_Y_in_a_kind_of_association_for_generic_pattern_examples");
                roleOfYThingObject.addValue(HQDM.PART_OF_BY_CLASS_, kindOfGenericAssociationObject.getId());

                // Now add these classes to the kindOfGenericAssociation
                kindOfGenericAssociationObject.addValue(HQDM.CONSISTS_OF_BY_CLASS, roleOfXThingObject.getId());
                kindOfGenericAssociationObject.addValue(HQDM.CONSISTS_OF_BY_CLASS, roleOfYThingObject.getId());

                final Thing individualKindOfYObject = IriUtils.createNewBaseObject( baseCollection,
                                new HqdmObjectBaseProperties(
                                                HQDM.KIND_OF_INDIVIDUAL,
                                                baseCollection.PATTERNS_REF_BASE,
                                                "KindOfIndividual_Y",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));
                individualKindOfYObject.addStringValue(baseCollection.COMMENT, "Kind_of_Y");

                final Thing classOfStateOfYObject = IriUtils.createNewBaseObject( baseCollection,
                                new HqdmObjectBaseProperties(
                                                HQDM.CLASS_OF_STATE,
                                                baseCollection.PATTERNS_REF_BASE,
                                                "ClassOfState_Y",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));
                classOfStateOfYObject.addStringValue(baseCollection.COMMENT, "Class_of_state_of_Y");

                // Now generate the three states that form the generic association between a
                // state of X and a state of a new individual Y
                // Create the state of X that is participant in this Association example
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
                stateOfXParticipantObject.addValue(HQDM.PART_OF_POSSIBLE_WORLD, QueryUtils
                                .findThingInServiceByName(mcDatasets, "Possible_World_for_generic_pattern_examples")
                                .getId());
                stateOfXParticipantObject.addValue(HQDM.BEGINNING,
                                QueryUtils.findThingInServiceByName(mcDatasets, "t1").getId());
                stateOfXParticipantObject.addValue(HQDM.ENDING,
                                QueryUtils.findThingInServiceByName(mcDatasets, "t2").getId());
                stateOfXParticipantObject.addValue(HQDM.TEMPORAL_PART_OF, QueryUtils
                                .findThingByNameInList(genericPossibleWorldThings, "Example_individual_X").getId());

                final Thing individualYObject = IriUtils.createNewBaseObject( baseCollection,
                                new HqdmObjectBaseProperties(
                                                HQDM.INDIVIDUAL,
                                                baseCollection.PATTERNS_BASE,
                                                "Example_individual_Y",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));
                individualYObject.addValue(HQDM.MEMBER_OF_KIND, individualKindOfYObject.getId());
                individualYObject.addValue(HQDM.PART_OF_POSSIBLE_WORLD, QueryUtils
                                .findThingInServiceByName(mcDatasets, "Possible_World_for_generic_pattern_examples")
                                .getId());
                individualYObject.addValue(HQDM.BEGINNING,
                                QueryUtils.findThingInServiceByName(mcDatasets, "t0").getId());
                individualYObject.addValue(HQDM.ENDING, QueryUtils.findThingInServiceByName(mcDatasets, "t3").getId());

                // Create the state of X that is participant in this Association example
                final Thing stateOfYParticipantObject = IriUtils.createNewBaseObject( baseCollection,
                                new HqdmObjectBaseProperties(
                                                HQDM.PARTICIPANT,
                                                baseCollection.PATTERNS_BASE,
                                                "Example_participant_state_of_Y",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));
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
                final Thing associationXYObject = IriUtils.createNewBaseObject( baseCollection,
                                new HqdmObjectBaseProperties(
                                                HQDM.ASSOCIATION,
                                                baseCollection.PATTERNS_BASE,
                                                "Example_association_between_X_and_Y",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));
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
                associationService.runInWriteTransaction(associationChangeSet);

                // Output files

                // Create node-edge graphs for the individual page
                MermaidUtils.writeLRNodeEdgeGraph(
                                List.of(associationXYObject, stateOfXParticipantObject, stateOfYParticipantObject),
                                List.of("record_created", "record_creator", "comment"),
                                List.of(associationXYObject.getId().toString().split("#")[1]),
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
                                List.of(associationXYObject.getId().toString().split("#")[1],
                                                stateOfXParticipantObject.getId().toString().split("#")[1],
                                                stateOfYParticipantObject.getId().toString().split("#")[1]),
                                "genericAssociationAndParticipantsFull");

                try {
                        final PrintStream ttl_stream_out = new PrintStream(
                                        "example-files/associationGenericPattern.ttl");

                        associationService.exportTtl(ttl_stream_out);
                        ttl_stream_out.close();
                        System.out.println(
                                        "\tData being generated as TTL in example-files/associationGenericPattern.ttl.");

                } catch (FileNotFoundException e) {
                        System.err.println("associationGenericPattern example write: " + e);
                }

                mcDatasets.add(associationService);

        }

}
