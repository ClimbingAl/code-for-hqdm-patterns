package patterns.hqdm.sign;

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
import uk.gov.gchq.magmacore.service.MagmaCoreServiceFactory;
import uk.gov.gchq.magmacore.service.transformation.DbTransformation;

public class SignExample {
        /**
         * Create a new Sign data model pattern, using the MagmaCore-
         * hqdm.rdfbuilders to construct the model.
         *
         * @param mcDatasets {@link List<MagmaCoreService>}.
         * @return
         */
        public static void createAndAddSignPattern(final List<MagmaCoreService> mcDatasets) {

                // Find supertypes
                List<List<Thing>> supertypes = FindSupertypes.findSuperTypes(List.of(
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
                final MagmaCoreService signService = MagmaCoreServiceFactory.createWithJenaDatabase();
                signService.register(PatternsUtils.PREFIX_LIST);

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
                final Pattern genericPattern = new PatternBuilder(new IRI(PatternsUtils.PATTERNS_REF_BASE, uid()))
                                .build();
                PatternsUtils.addBasePropertiesToThing(genericPattern,
                                new HqdmObjectBaseProperties(
                                                PatternsUtils.PATTERNS_REF_BASE,
                                                "P@773rN", // This string is the example pattern.
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                "HqdmPatternProject_User1"));
                genericPattern.addStringValue(PatternsUtils.COMMENT,
                                "This_is_an_example_pattern_held_as_the_data_EntityName");

                final Role exampleRLCRole = new RoleBuilder(new IRI(PatternsUtils.PATTERNS_REF_BASE, uid())).build();
                PatternsUtils.addBasePropertiesToThing(exampleRLCRole,
                                new HqdmObjectBaseProperties(
                                                PatternsUtils.PATTERNS_REF_BASE,
                                                "Kind_of_recognizing_language_community_for_sign_examples",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                "HqdmPatternProject_User1"));

                // Events
                final Event ts1 = new EventBuilder(new IRI(PatternsUtils.PATTERNS_BASE, uid()))
                                .part_Of_Possible_World_M(possibleWorldForGenericExamples)
                                .member_Of(classOfEventForGenericExamples)
                                .build();
                PatternsUtils.addBasePropertiesToThing(ts1,
                                new HqdmObjectBaseProperties(
                                                PatternsUtils.PATTERNS_BASE,
                                                "ts1",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                "HqdmPatternProject_User1"));

                final Event ts2 = new EventBuilder(new IRI(PatternsUtils.PATTERNS_BASE, uid()))
                                .part_Of_Possible_World_M(possibleWorldForGenericExamples)
                                .member_Of(classOfEventForGenericExamples)
                                .build();
                PatternsUtils.addBasePropertiesToThing(ts2,
                                new HqdmObjectBaseProperties(
                                                PatternsUtils.PATTERNS_BASE,
                                                "ts2",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                "HqdmPatternProject_User1"));

                // Recognizing language community
                final RecognizingLanguageCommunity exampleRecognizingLanguageCommunity = new RecognizingLanguageCommunityBuilder(
                                new IRI(PatternsUtils.PATTERNS_BASE, uid()))
                                .part_Of_Possible_World_M(possibleWorldForGenericExamples)
                                .member_Of_Kind_M(exampleRLCRole)
                                .beginning(ts1)
                                .ending(ts2)
                                .build();
                PatternsUtils.addBasePropertiesToThing(exampleRecognizingLanguageCommunity,
                                new HqdmObjectBaseProperties(
                                                PatternsUtils.PATTERNS_BASE,
                                                "Example_RecognizingLanguageCommunity",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                "HqdmPatternProject_User1"));

                // Representation by pattern
                final RepresentationByPattern exampleRepresentationByPattern = new RepresentationByPatternBuilder(
                                new IRI(PatternsUtils.PATTERNS_REF_BASE, uid()))
                                .consists_Of_By_Class_M(genericPattern)
                                .consists_Of_In_Members_M(exampleRecognizingLanguageCommunity)
                                .represented_M(thingBeingRepresented)
                                .build();
                PatternsUtils.addBasePropertiesToThing(exampleRepresentationByPattern,
                                new HqdmObjectBaseProperties(
                                                PatternsUtils.PATTERNS_REF_BASE,
                                                "Representation_by_pattern_class_for_sign_examples",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                "HqdmPatternProject_User1"));

                // Representation by sign
                final KindOfAssociation exampleKindOfRepresentationBySign = new KindOfAssociationBuilder(
                                new IRI(PatternsUtils.PATTERNS_REF_BASE, uid()))
                                .build();
                PatternsUtils.addBasePropertiesToThing(exampleKindOfRepresentationBySign,
                                new HqdmObjectBaseProperties(
                                                PatternsUtils.PATTERNS_REF_BASE,
                                                "KindOfAssociation_Kind_of_representation_by_sign_class_for_sign_examples",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                "HqdmPatternProject_User1"));

                final RepresentationBySign exampleRepresentationBySign = new RepresentationBySignBuilder(
                                new IRI(PatternsUtils.PATTERNS_REF_BASE, uid()))
                                .part_Of_Possible_World_M(possibleWorldForGenericExamples)
                                .member_Of_Kind_M(exampleKindOfRepresentationBySign)
                                .member_Of__M(exampleRepresentationByPattern)
                                .represents_M(thingBeingRepresented)
                                .consists_Of_Participant(exampleRecognizingLanguageCommunity)
                                .beginning(ts1)
                                .ending(ts2)
                                .build();
                PatternsUtils.addBasePropertiesToThing(exampleRepresentationBySign,
                                new HqdmObjectBaseProperties(
                                                PatternsUtils.PATTERNS_BASE,
                                                "Example_RepresentationBySign",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                "HqdmPatternProject_User1"));

                // Sign
                final Sign exampleSign = new SignBuilder(new IRI(PatternsUtils.PATTERNS_BASE, uid()))
                                .part_Of_Possible_World_M(possibleWorldForGenericExamples)
                                .member_Of__M(genericPattern)
                                .beginning(ts1)
                                .ending(ts2)
                                .participant_In_M(exampleRepresentationBySign)
                                .build();
                PatternsUtils.addBasePropertiesToThing(exampleSign,
                                new HqdmObjectBaseProperties(
                                                PatternsUtils.PATTERNS_BASE,
                                                "Example_Sign",
                                                LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                                "HqdmPatternProject_User1"));

                exampleRepresentationBySign.addValue(HQDM.CONSISTS_OF_PARTICIPANT, new IRI(exampleSign.getId()));

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
                signService.runInTransaction(signChangeSet);

                // Output files
                // Create node-edge graphs for the sign page
                MermaidUtils.writeLRNodeEdgeGraph(
                                List.of(exampleRepresentationBySign,
                                                exampleSign,
                                                exampleRecognizingLanguageCommunity,
                                                thingBeingRepresented),
                                List.of("record_created", "record_creator", "comment"),
                                List.of(exampleRepresentationBySign.getId().split("#")[1],
                                                thingBeingRepresented.getId().split("#")[1]),
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
                                List.of(exampleRepresentationBySign.getId().split("#")[1],
                                                exampleSign.getId().split("#")[1],
                                                exampleRecognizingLanguageCommunity.getId().split("#")[1],
                                                thingBeingRepresented.getId().split("#")[1]),
                                "genericRepresentationBySignAndParticipantsFull");

                try {
                        final PrintStream ttl_stream_out = new PrintStream(
                                        "example-files/representationBySignGenericPattern.ttl");
                        final PrintStream stmt_stream_out = new PrintStream(
                                        "example-files/representationBySignGenericPattern.stmt");

                        signService.exportTtl(ttl_stream_out);
                        ttl_stream_out.close();
                        System.out.println(
                                        "\tData being generated as TTL in example-files/representationBySignGenericPattern.ttl.");

                        signService.exportStatements(stmt_stream_out);
                        stmt_stream_out.close();
                        System.out.println(
                                        "\tData generated as statements in example-files/representationBySignGenericPattern.stmt.");

                } catch (FileNotFoundException e) {
                        System.err.println("representationBySignGenericPattern example write: " + e);
                }

                mcDatasets.add(signService);

        }
}
