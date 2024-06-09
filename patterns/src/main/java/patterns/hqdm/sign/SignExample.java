package patterns.hqdm.sign;

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
import static uk.gov.gchq.magmacore.util.UID.uid;

import uk.gov.gchq.magmacore.hqdm.rdfbuilders.EventBuilder;
import uk.gov.gchq.magmacore.hqdm.rdfbuilders.KindOfAssociationBuilder;
import uk.gov.gchq.magmacore.hqdm.rdfbuilders.PatternBuilder;
import uk.gov.gchq.magmacore.hqdm.rdfbuilders.RecognizingLanguageCommunityBuilder;
import uk.gov.gchq.magmacore.hqdm.rdfbuilders.RepresentationByPatternBuilder;
import uk.gov.gchq.magmacore.hqdm.rdfbuilders.RepresentationBySignBuilder;
import uk.gov.gchq.magmacore.hqdm.rdfbuilders.RoleBuilder;
import uk.gov.gchq.magmacore.hqdm.rdfbuilders.SignBuilder;
import uk.gov.gchq.magmacore.hqdm.model.ClassOfEvent;
import uk.gov.gchq.magmacore.hqdm.model.Event;
import uk.gov.gchq.magmacore.hqdm.model.Individual;
import uk.gov.gchq.magmacore.hqdm.model.KindOfAssociation;
import uk.gov.gchq.magmacore.hqdm.model.Pattern;
import uk.gov.gchq.magmacore.hqdm.model.PossibleWorld;
import uk.gov.gchq.magmacore.hqdm.model.RecognizingLanguageCommunity;
import uk.gov.gchq.magmacore.hqdm.model.RepresentationByPattern;
import uk.gov.gchq.magmacore.hqdm.model.RepresentationBySign;
import uk.gov.gchq.magmacore.hqdm.model.Role;
import uk.gov.gchq.magmacore.hqdm.model.Sign;
import uk.gov.gchq.magmacore.hqdm.model.Thing;
import uk.gov.gchq.magmacore.hqdm.rdf.iri.HQDM;
import uk.gov.gchq.magmacore.hqdm.rdf.iri.IRI;
import uk.gov.gchq.magmacore.service.MagmaCoreService;
import uk.gov.gchq.magmacore.service.transformation.DbTransformation;

public class SignExample {
        /**
         * Create a new Sign data model pattern, using the MagmaCore-
         * hqdm.rdfbuilders to construct the model.
         *
         * @param mcDatasets {@link List<MagmaCoreService>}.
         * @return
         */
        public static void createAndAddSignPattern(BaseCollection baseCollection, final List<MagmaCoreService> mcDatasets, MagmaCoreService signService, String record_creator) {

                // Find supertypes
                List<List<Thing>> supertypes = FindSupertypes.findSuperTypes(baseCollection, List.of(
                                "sign",
                                "representation_by_sign",
                                "recognizing_language_community",
                                "class_of_representation",
                                "class_of_sign",
                                "kind_of_organization",
                                "role"));

                supertypes.forEach(st -> {
                        st.forEach(tl -> {
                                System.out.println(tl.getId() + " ");
                        });
                        System.out.println(" ");
                });

                MermaidUtils.writeSupertypeGraph(supertypes, "signTypes");

                System.out.println("Create generic sign and pattern data objects.");

                // Find existing objects that are members of
                // "Class_of_possible_world_for_abstract_pattern_examples"
                final PossibleWorld possibleWorldForGenericExamples = (PossibleWorld) QueryUtils
                                .findThingInServiceByName(mcDatasets, "Possible_World_for_generic_pattern_examples");
                final ClassOfEvent classOfEventForGenericExamples = (ClassOfEvent) QueryUtils
                                .findThingInServiceByName(mcDatasets, "ClassOfEvent_ExampleEvents");

                // Thing being represented
                final Individual thingBeingRepresented = (Individual) QueryUtils.findThingInServiceByName(mcDatasets,
                                "Example_individual_X");

                // Pattern
                final Pattern genericPattern = new PatternBuilder(new IRI(baseCollection.PATTERNS_REF_BASE, uid()))
                                .build();
                IriUtils.addBasePropertiesToThing(baseCollection, genericPattern,
                                new HqdmObjectBaseProperties(
                                                baseCollection.PATTERNS_REF_BASE,
                                                "P@773rN", // This string is the example pattern.
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));
                genericPattern.addStringValue(baseCollection.COMMENT,
                                "This_is_an_example_pattern_held_as_the_data_EntityName");

                final Role exampleRLCRole = new RoleBuilder(new IRI(baseCollection.PATTERNS_REF_BASE, uid())).build();
                IriUtils.addBasePropertiesToThing(baseCollection, exampleRLCRole,
                                new HqdmObjectBaseProperties(
                                                baseCollection.PATTERNS_REF_BASE,
                                                "Kind_of_recognizing_language_community_for_sign_examples",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));

                // Events
                final Event ts1 = new EventBuilder(new IRI(baseCollection.PATTERNS_BASE, uid()))
                                .part_Of_Possible_World_M(possibleWorldForGenericExamples)
                                .member_Of(classOfEventForGenericExamples)
                                .build();
                IriUtils.addBasePropertiesToThing(baseCollection, ts1,
                                new HqdmObjectBaseProperties(
                                                baseCollection.PATTERNS_BASE,
                                                "ts1",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));

                final Event ts2 = new EventBuilder(new IRI(baseCollection.PATTERNS_BASE, uid()))
                                .part_Of_Possible_World_M(possibleWorldForGenericExamples)
                                .member_Of(classOfEventForGenericExamples)
                                .build();
                IriUtils.addBasePropertiesToThing(baseCollection, ts2,
                                new HqdmObjectBaseProperties(
                                                baseCollection.PATTERNS_BASE,
                                                "ts2",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));

                // Recognizing language community
                final RecognizingLanguageCommunity exampleRecognizingLanguageCommunity = new RecognizingLanguageCommunityBuilder(
                                new IRI(baseCollection.PATTERNS_BASE, uid()))
                                .part_Of_Possible_World_M(possibleWorldForGenericExamples)
                                .member_Of_Kind_M(exampleRLCRole)
                                .beginning(ts1)
                                .ending(ts2)
                                .build();
                IriUtils.addBasePropertiesToThing(baseCollection, exampleRecognizingLanguageCommunity,
                                new HqdmObjectBaseProperties(
                                                baseCollection.PATTERNS_BASE,
                                                "Example_RecognizingLanguageCommunity",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));

                // Representation by pattern
                final RepresentationByPattern exampleRepresentationByPattern = new RepresentationByPatternBuilder(
                                new IRI(baseCollection.PATTERNS_REF_BASE, uid()))
                                .consists_Of_By_Class_M(genericPattern)
                                .consists_Of_In_Members_M(exampleRecognizingLanguageCommunity)
                                .represented_M(thingBeingRepresented)
                                .build();
                IriUtils.addBasePropertiesToThing(baseCollection, exampleRepresentationByPattern,
                                new HqdmObjectBaseProperties(
                                                baseCollection.PATTERNS_REF_BASE,
                                                "Representation_by_pattern_class_for_sign_examples",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));

                // Representation by sign
                final KindOfAssociation exampleKindOfRepresentationBySign = new KindOfAssociationBuilder(
                                new IRI(baseCollection.PATTERNS_REF_BASE, uid()))
                                .build();
                IriUtils.addBasePropertiesToThing(baseCollection, exampleKindOfRepresentationBySign,
                                new HqdmObjectBaseProperties(
                                                baseCollection.PATTERNS_REF_BASE,
                                                "KindOfAssociation_Kind_of_representation_by_sign_class_for_sign_examples",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));

                final RepresentationBySign exampleRepresentationBySign = new RepresentationBySignBuilder(
                                new IRI(baseCollection.PATTERNS_REF_BASE, uid()))
                                .part_Of_Possible_World_M(possibleWorldForGenericExamples)
                                .member_Of_Kind_M(exampleKindOfRepresentationBySign)
                                .member_Of__M(exampleRepresentationByPattern)
                                .represents_M(thingBeingRepresented)
                                .consists_Of_Participant(exampleRecognizingLanguageCommunity)
                                .beginning(ts1)
                                .ending(ts2)
                                .build();
                IriUtils.addBasePropertiesToThing(baseCollection, exampleRepresentationBySign,
                                new HqdmObjectBaseProperties(
                                                baseCollection.PATTERNS_BASE,
                                                "Example_RepresentationBySign",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));

                // Sign
                final Sign exampleSign = new SignBuilder(new IRI(baseCollection.PATTERNS_BASE, uid()))
                                .part_Of_Possible_World_M(possibleWorldForGenericExamples)
                                .member_Of__M(genericPattern)
                                .beginning(ts1)
                                .ending(ts2)
                                .participant_In_M(exampleRepresentationBySign)
                                .build();
                IriUtils.addBasePropertiesToThing(baseCollection, exampleSign,
                                new HqdmObjectBaseProperties(
                                                baseCollection.PATTERNS_BASE,
                                                "Example_Sign",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                record_creator));

                exampleRepresentationBySign.addValue(HQDM.CONSISTS_OF_PARTICIPANT, exampleSign.getId());

                // Commit to sign MagmaCore service
                final DbTransformation signChangeSet = signService.createDbTransformation(
                                List.of(
                                                genericPattern,
                                                exampleRLCRole,
                                                ts1,
                                                ts2,
                                                exampleRecognizingLanguageCommunity,
                                                exampleRepresentationByPattern,
                                                exampleRepresentationBySign,
                                                exampleSign,
                                                exampleKindOfRepresentationBySign));
                signService.runInWriteTransaction(signChangeSet);

                // Output files
                // Create node-edge graphs for the sign page
                MermaidUtils.writeLRNodeEdgeGraph(
                                List.of(exampleRepresentationBySign,
                                                exampleSign,
                                                exampleRecognizingLanguageCommunity,
                                                thingBeingRepresented),
                                List.of("record_created", "record_creator", "comment"),
                                List.of(exampleRepresentationBySign.getId().toString().split("#")[1],
                                                thingBeingRepresented.getId().toString().split("#")[1]),
                                "genericRepresentationBySignAndParticipants");

                MermaidUtils.writeLRNodeEdgeGraph(
                                List.of(genericPattern,
                                                exampleRLCRole,
                                                ts1,
                                                ts2,
                                                exampleRecognizingLanguageCommunity,
                                                exampleRepresentationByPattern,
                                                exampleRepresentationBySign,
                                                exampleSign,
                                                exampleKindOfRepresentationBySign),
                                List.of("record_created", "record_creator", "comment"),
                                List.of(exampleRepresentationBySign.getId().toString().split("#")[1],
                                                exampleSign.getId().toString().split("#")[1],
                                                exampleRecognizingLanguageCommunity.getId().toString().split("#")[1],
                                                thingBeingRepresented.getId().toString().split("#")[1]),
                                "genericRepresentationBySignAndParticipantsFull");

                try {
                        final PrintStream ttl_stream_out = new PrintStream(
                                        "example-files/representationBySignGenericPattern.ttl");

                        signService.exportTtl(ttl_stream_out);
                        ttl_stream_out.close();
                        System.out.println(
                                        "\tData being generated as TTL in example-files/representationBySignGenericPattern.ttl.");

                } catch (FileNotFoundException e) {
                        System.err.println("representationBySignGenericPattern example write: " + e);
                }

                mcDatasets.add(signService);

        }
}
