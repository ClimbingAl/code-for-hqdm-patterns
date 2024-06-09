package patterns.hqdm.association;

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
import hqdm.utils.query.QueryUtils;
import uk.gov.gchq.magmacore.hqdm.model.Thing;
import uk.gov.gchq.magmacore.hqdm.rdf.iri.HQDM;
import uk.gov.gchq.magmacore.hqdm.rdf.iri.IRI;
import uk.gov.gchq.magmacore.service.MagmaCoreService;
import uk.gov.gchq.magmacore.service.MagmaCoreServiceFactory;
import uk.gov.gchq.magmacore.service.transformation.DbTransformation;

public class AssociationEagleExample {

        /**
         * Create a new Association for Buzz Aldrin in LM, construct DbTransformation
         * for them
         * and then commit to database.
         *
         * @param mcDatasets {@link List<MagmaCoreService>}.
         * @return
         */
        public static void createAndAddAssociationPattern( BaseCollection baseCollection, final List<MagmaCoreService> mcDatasets, MagmaCoreService associationService, String record_creator ) {

                // Find supertypes
                List<List<Thing>> supertypes = FindSupertypes.findSuperTypes( baseCollection, List.of(
                                "association",
                                "participant",
                                "state_of_person",
                                "state_of_functional_object",
                                "kind_of_association",
                                "role"));

                supertypes.forEach(st -> {
                        st.forEach(tl -> {
                                System.out.println(tl.getId() + " ");
                        });
                        System.out.println(" ");
                });

                MermaidUtils.writeSupertypeGraph(supertypes, "associationEagleTypes");

                System.out.println("Create Buzz Aldrin association as occupier of Eagle Lander Module data objects!");

                // Create Buzz Aldrin as an individual
                final Thing personKindObject = IriUtils.createNewBaseObject( baseCollection,
                                new HqdmObjectBaseProperties(
                                                HQDM.KIND_OF_PERSON,
                                                baseCollection.PATTERNS_REF_BASE,
                                                "KindOfPerson",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));
                personKindObject.addStringValue(baseCollection.COMMENT,
                                "Kind_of_person_as_a_referenceable_data_object.");

                final Thing classOfStateOfPersonObject = IriUtils.createNewBaseObject( baseCollection,
                                new HqdmObjectBaseProperties(
                                                HQDM.CLASS_OF_STATE_OF_PERSON,
                                                baseCollection.PATTERNS_REF_BASE,
                                                "ClassOfStateOfPerson",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));
                classOfStateOfPersonObject.addStringValue(baseCollection.COMMENT,
                                "Class_of_state_of_person_as_a_referenceable_data_object.");
                classOfStateOfPersonObject.addValue(HQDM.PART__OF_BY_CLASS, personKindObject.getId());

                // Get the necessary LM-5 data objects from the database

                // Find existing objects that are members of
                // "Class_of_possible_world_for_abstract_pattern_examples"
                final Thing possibleWorldForEagleExamples = QueryUtils.findThingInServiceByName(mcDatasets,
                                "Possible_world_for_Apollo_pattern_examples");
                final List<Thing> apolloPossibleWorldThings = QueryUtils.findThingsInServicesByPredicateAndValue(
                                mcDatasets,
                                HQDM.PART_OF_POSSIBLE_WORLD,
                                possibleWorldForEagleExamples.getId());

                final Thing kindOfCrewInLanderAssociationObject = IriUtils.createNewBaseObject( baseCollection,
                                new HqdmObjectBaseProperties(
                                        HQDM.KIND_OF_ASSOCIATION,
                                        baseCollection.PATTERNS_REF_BASE,
                                        "KindOfAssociation__CrewInLunarLanderKindOfAssociation",
                                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                        record_creator));
                kindOfCrewInLanderAssociationObject.addStringValue(baseCollection.COMMENT,
                                "Kind_of_association_for_human_crew_in_a_particular_Lunar_Lander_Module.");

                final Thing roleOfLunarLanderObject = IriUtils.createNewBaseObject( baseCollection,new HqdmObjectBaseProperties(
                                HQDM.ROLE,
                                baseCollection.PATTERNS_REF_BASE,
                                "RoleOfLunarLanderAsOccupiedByCrew",
                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                record_creator));
                roleOfLunarLanderObject.addStringValue(baseCollection.COMMENT,
                                "Role_of_Lunar_Lander_Module_in_association_as_occupied_by_its_human_crew.");
                roleOfLunarLanderObject.addValue(HQDM.PART_OF_BY_CLASS_, kindOfCrewInLanderAssociationObject.getId());
                roleOfLunarLanderObject
                                .addValue(HQDM.HAS_SUPERCLASS,
                                        QueryUtils.findThingInServiceByName(mcDatasets,
                                                                "ClassOfStateOfFunctionalSystem__State_Of_Lunar_Lander")
                                                                .getId());

                final Thing roleOfLunarLanderCrewObject = IriUtils.createNewBaseObject( baseCollection,
                                new HqdmObjectBaseProperties(
                                        HQDM.ROLE,
                                        baseCollection.PATTERNS_REF_BASE,
                                        "RoleOfPersonAsOccupierOfLunarLander",
                                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                        record_creator));
                roleOfLunarLanderCrewObject.addStringValue(baseCollection.COMMENT,
                                "Role_of_Person_as_crew_in_association_as_occupier_of_Lunar_Lander_Module.");
                roleOfLunarLanderCrewObject.addValue(HQDM.PART_OF_BY_CLASS_,
                                kindOfCrewInLanderAssociationObject.getId());
                roleOfLunarLanderCrewObject.addValue(HQDM.HAS_SUPERCLASS, classOfStateOfPersonObject.getId());

                // Now add these classes to the kindOfGenericAssociation
                kindOfCrewInLanderAssociationObject.addValue(HQDM.CONSISTS_OF_BY_CLASS,
                                roleOfLunarLanderObject.getId());
                kindOfCrewInLanderAssociationObject.addValue(HQDM.CONSISTS_OF_BY_CLASS,
                                roleOfLunarLanderCrewObject.getId());

                // Get/generate events from Buzz entering LM5 to Exiting to Lunar surface
                // Create the start and end events for the lander on the moon
                final ZonedDateTime lmpEnteredLMDateTime = ZonedDateTime.parse("1969-07-20T20:12:52+00:00[UTC]");
                final ZonedDateTime lmpExitedLMDateTime = ZonedDateTime.parse("1969-07-21T02:51:16+00:00[UTC]");

                final Thing buzzEnteredLM5EventObject = IriUtils.createNewBaseObject( baseCollection,
                                new HqdmObjectBaseProperties(
                                                HQDM.POINT_IN_TIME,
                                                baseCollection.PATTERNS_BASE,
                                                LocalDateTime.ofInstant(lmpEnteredLMDateTime.toInstant(),
                                                                ZoneOffset.UTC).toString(),
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));
                buzzEnteredLM5EventObject.addValue(HQDM.MEMBER_OF, QueryUtils
                                .findThingInServiceByName(mcDatasets, "ClassOfPointInTime__ISO8601_DateTime").getId());
                buzzEnteredLM5EventObject.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldForEagleExamples.getId());

                final Thing buzzExitedLM5EventObject = IriUtils.createNewBaseObject( baseCollection,
                                new HqdmObjectBaseProperties(
                                                HQDM.POINT_IN_TIME,
                                                baseCollection.PATTERNS_BASE,
                                                LocalDateTime.ofInstant(lmpExitedLMDateTime.toInstant(), ZoneOffset.UTC)
                                                                .toString(),
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));
                buzzExitedLM5EventObject.addValue(HQDM.MEMBER_OF, QueryUtils
                                .findThingInServiceByName(mcDatasets, "ClassOfPointInTime__ISO8601_DateTime").getId());
                buzzExitedLM5EventObject.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldForEagleExamples.getId());

                // Now generate the three states that form the generic association between a
                // state of X and a state of a new individual Y
                // Create the state of Lunar Lander Module that is participant in this
                // Association example
                final Thing participantStateOfLunarLanderObject = IriUtils.createNewBaseObject( baseCollection,
                                new HqdmObjectBaseProperties(
                                                HQDM.PARTICIPANT,
                                                baseCollection.PATTERNS_BASE,
                                                "Example_participant_state_of_Eagle_Lunar_Lander_in_descent",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));
                participantStateOfLunarLanderObject.addValue(HQDM.MEMBER_OF_KIND, roleOfLunarLanderObject.getId());
                participantStateOfLunarLanderObject.addValue(HQDM.MEMBER_OF,
                                QueryUtils.findThingInServiceByName(mcDatasets, "ClassOfState_X").getId());
                participantStateOfLunarLanderObject.addValue(HQDM.PART_OF_POSSIBLE_WORLD, QueryUtils
                                .findThingInServiceByName(mcDatasets, "Possible_World_for_generic_pattern_examples")
                                .getId());
                participantStateOfLunarLanderObject.addValue(HQDM.BEGINNING, buzzEnteredLM5EventObject.getId());
                participantStateOfLunarLanderObject.addValue(HQDM.ENDING, buzzExitedLM5EventObject.getId());
                participantStateOfLunarLanderObject
                                .addValue(HQDM.TEMPORAL_PART_OF,
                                        QueryUtils.findThingByNameInList(apolloPossibleWorldThings,
                                                                "Example_individual_that_is_Lunar_Module_Eagle_LM-5")
                                                                .getId());

                final Thing buzzAldrinObject = IriUtils.createNewBaseObject( baseCollection,
                                new HqdmObjectBaseProperties(
                                                HQDM.PERSON,
                                                baseCollection.PATTERNS_BASE,
                                                "Buzz_Aldrin",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));
                buzzAldrinObject.addValue(HQDM.MEMBER_OF_KIND, personKindObject.getId());
                buzzAldrinObject.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldForEagleExamples.getId());

                // Create the state of X that is participant in this Association example
                final Thing participantStateOfBuzzAldrinObject = IriUtils.createNewBaseObject( baseCollection,
                                new HqdmObjectBaseProperties(
                                                HQDM.PARTICIPANT,
                                                baseCollection.PATTERNS_BASE,
                                                "Example_participant_state_of_Buzz_Aldrin_in_LM5",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));
                participantStateOfBuzzAldrinObject.addValue(HQDM.MEMBER_OF_KIND, roleOfLunarLanderCrewObject.getId());
                participantStateOfBuzzAldrinObject.addValue(HQDM.MEMBER_OF, classOfStateOfPersonObject.getId());
                participantStateOfBuzzAldrinObject.addValue(HQDM.PART_OF_POSSIBLE_WORLD,
                                possibleWorldForEagleExamples.getId());
                participantStateOfBuzzAldrinObject.addValue(HQDM.BEGINNING, buzzEnteredLM5EventObject.getId());
                participantStateOfBuzzAldrinObject.addValue(HQDM.ENDING, buzzExitedLM5EventObject.getId());
                participantStateOfBuzzAldrinObject.addValue(HQDM.TEMPORAL_PART_OF, buzzAldrinObject.getId());

                // Now make Association
                final Thing associationBuzzInLM5Object = IriUtils.createNewBaseObject( baseCollection,
                                new HqdmObjectBaseProperties(
                                                HQDM.ASSOCIATION,
                                                baseCollection.PATTERNS_BASE,
                                                "Association_of_Buzz_in_LM5_during_descent",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));
                associationBuzzInLM5Object.addValue(HQDM.MEMBER_OF_KIND, kindOfCrewInLanderAssociationObject.getId());
                associationBuzzInLM5Object.addValue(HQDM.BEGINNING, buzzEnteredLM5EventObject.getId());
                associationBuzzInLM5Object.addValue(HQDM.ENDING, buzzExitedLM5EventObject.getId());
                associationBuzzInLM5Object.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldForEagleExamples.getId());
                associationBuzzInLM5Object.addValue(HQDM.CONSISTS_OF_PARTICIPANT,
                                participantStateOfBuzzAldrinObject.getId());
                associationBuzzInLM5Object.addValue(HQDM.CONSISTS_OF_PARTICIPANT,
                                participantStateOfLunarLanderObject.getId());

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
                associationService.runInWriteTransaction(associationChangeSet);

                // Output files

                // Create node-edge graphs for the association Eagle page
                MermaidUtils.writeLRNodeEdgeGraph(List.of(
                                associationBuzzInLM5Object,
                                participantStateOfBuzzAldrinObject,
                                participantStateOfLunarLanderObject),
                                List.of("record_created", "record_creator", "comment"),
                                List.of(associationBuzzInLM5Object.getId().toString().split("#")[1],
                                                participantStateOfBuzzAldrinObject.getId().toString().split("#")[1],
                                                participantStateOfLunarLanderObject.getId().toString().split("#")[1]),
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
                                roleOfLunarLanderObject),
                                List.of("record_created", "record_creator", "comment"),
                                List.of(associationBuzzInLM5Object.getId().toString().split("#")[1],
                                                participantStateOfBuzzAldrinObject.getId().toString().split("#")[1],
                                                participantStateOfLunarLanderObject.getId().toString().split("#")[1]),
                                "buzzInLM5AssociationAndParticipantsFull");

                try {
                        final PrintStream ttl_stream_out = new PrintStream(
                                        "example-files/associationBuzzInLM5Pattern.ttl");

                        associationService.exportTtl(ttl_stream_out);
                        ttl_stream_out.close();
                        System.out.println(
                                        "\tData being generated as TTL in example-files/associationBuzzInLM5Pattern.ttl.");


                } catch (FileNotFoundException e) {
                        System.err.println("associationBuzzInLM5Pattern example write: " + e);
                }

                mcDatasets.add(associationService);

        }
}
