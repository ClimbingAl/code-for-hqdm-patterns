package patterns.hqdm.activity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

import hqdm.utils.base.BaseCollection;
import hqdm.utils.mermaid.MermaidUtils;
import uk.gov.gchq.magmacore.hqdm.model.Thing;
import uk.gov.gchq.magmacore.hqdm.rdf.iri.HQDM;
import uk.gov.gchq.magmacore.hqdm.rdf.iri.IRI;
import uk.gov.gchq.magmacore.service.MagmaCoreService;

public class ActivityEagleExample {

    /**
     * Load Activity Editor ttl file and commit to new mcService dataset.
     * Generate example mermaid and ttl files from it.
     *
     * @param mcDatasets {@link List<MagmaCoreService>}.
     * @return
     */
    public static void loadEagleActivityFromEditor( BaseCollection baseCollection, final List<MagmaCoreService> mcDatasets, MagmaCoreService eagleActivityService, String record_creator ) {

        System.out.println("Load Activity Editor Eagle activity data objects.");
        
        try {
            InputStream activityInputStream = new FileInputStream(
                    "input-files/activity_diagram_state_of_apollo11_mission.ttl");

            eagleActivityService.importTtl(activityInputStream);
            activityInputStream.close();
            System.out.println("\tData loaded from TTL in input-files/activity_diagram_state_of_apollo11_mission.ttl.");

            final PrintStream ttl_stream_out = new PrintStream("example-files/activityEaglePattern.ttl");

            eagleActivityService.exportTtl(ttl_stream_out);
            ttl_stream_out.close();
            System.out.println("\tData being generated as TTL in example-files/activityEaglePattern.ttl.");

        } catch (FileNotFoundException e) {
            System.err.println("activityInputStream example load: " + e);
        } catch (Exception e) {

        }

        final List<Thing> namedThings = eagleActivityService.findByPredicateIriInTransaction(HQDM.ENTITY_NAME);
        System.out.println("Number of entities with a data_EntityName predicate = " + namedThings.size());

        // Create node-edge graphs for the association Eagle page
        final Thing returnFromLunarSurfaceActivity = eagleActivityService.getInTransaction(
                new IRI(baseCollection.PATTERNS_BASE, "40ae87d9-8a1b-417c-86a8-a00bd70fe239"));
        final Thing participantBuzz = eagleActivityService.getInTransaction(
                new IRI(baseCollection.PATTERNS_BASE, "d1f7335c-c255-4358-9dc3-2d78aa3ba81d")); // Buzz Aldrin
                                                                                                      // participant
                                                                                                      // state
        final Thing participantLM5AscentModule = eagleActivityService.getInTransaction(
                new IRI(baseCollection.PATTERNS_BASE, "ab9fd44e-ff12-4a21-915a-849816c5198e")); // LM-5 Ascent
                                                                                                      // Module
                                                                                                      // participant
                                                                                                      // state
        final Thing participantNeil = eagleActivityService.getInTransaction(
                new IRI(baseCollection.PATTERNS_BASE, "6b50a479-07ca-4cac-a65d-f3813259b8d9")); // Neil Armstrong
                                                                                                      // participant
                                                                                                      // state

        final Thing lm5AscentModule = eagleActivityService.getInTransaction(
                new IRI(baseCollection.PATTERNS_BASE, "4974518d-53c0-4f56-8017-3a989bf36065"));
        final Thing neilArmstrongPerson = eagleActivityService.getInTransaction(
                new IRI(baseCollection.PATTERNS_BASE, "a589d9e5-919f-476d-aa54-8c6dac941061"));
        final Thing buzzAldrinPerson = eagleActivityService.getInTransaction(
                new IRI(baseCollection.PATTERNS_BASE, "7bbf504e-f834-40dc-b060-974827fbef76"));

        MermaidUtils.writeLRNodeEdgeGraph(List.of(
                returnFromLunarSurfaceActivity,
                participantLM5AscentModule,
                participantBuzz,
                participantNeil),
                List.of("record_created", "record_creator", "comment"),
                List.of(returnFromLunarSurfaceActivity.getId().toString().split("#")[1],
                        participantBuzz.getId().toString().split("#")[1],
                        participantNeil.getId().toString().split("#")[1],
                        participantLM5AscentModule.getId().toString().split("#")[1]),
                "activityLM5AscentAndParticipants");

        final Thing kindOfLunarAscentObject = eagleActivityService.getInTransaction(
                new IRI(baseCollection.PATTERNS_BASE, "b41158ca-2619-4097-9cf5-890979343127"));
        final Thing roleOfLunarLanderLMPObject = eagleActivityService.getInTransaction(
                new IRI(baseCollection.PATTERNS_BASE, "becf0e5b-f094-43eb-be03-a36de244a05d"));
        final Thing roleOfLunarLanderCMDObject = eagleActivityService.getInTransaction(
                new IRI(baseCollection.PATTERNS_BASE, "9f55ec51-3926-46bc-9c1e-a96f1379ce14"));
        final Thing roleOfLunarLanderObject = eagleActivityService.getInTransaction(
                new IRI(baseCollection.PATTERNS_BASE, "b4eebfca-cd08-40b4-a6f9-bfe5f793c93c"));
        final Thing beginning1 = eagleActivityService.getInTransaction(
                new IRI(baseCollection.PATTERNS_BASE, "a0f6879b-1127-438d-bb78-43ae5de9d2c5"));
        final Thing ending1 = eagleActivityService.getInTransaction(
                new IRI(baseCollection.PATTERNS_BASE, "158e354d-ffbb-481c-981f-b8d609860a69"));

        MermaidUtils.writeLRNodeEdgeGraph(List.of(
                returnFromLunarSurfaceActivity,
                participantLM5AscentModule,
                participantBuzz,
                participantNeil,
                lm5AscentModule,
                neilArmstrongPerson,
                buzzAldrinPerson,
                kindOfLunarAscentObject,
                roleOfLunarLanderLMPObject,
                roleOfLunarLanderCMDObject,
                roleOfLunarLanderObject,
                beginning1,
                ending1),
                List.of("record_created", "record_creator", "comment"),
                List.of(returnFromLunarSurfaceActivity.getId().toString().split("#")[1],
                        participantBuzz.getId().toString().split("#")[1],
                        participantNeil.getId().toString().split("#")[1],
                        participantLM5AscentModule.getId().toString().split("#")[1]),
                "activityLM5AscentAndParticipantsFull");

        returnFromLunarSurfaceActivity.addStringValue(HQDM.ENTITY_NAME, "Return_from_Lunar_Surface_to_CSM");
        participantLM5AscentModule.addStringValue(HQDM.ENTITY_NAME, "Participant_state_of_LM5_Ascent_Module");
        participantBuzz.addStringValue(HQDM.ENTITY_NAME, "Participant_state_of_Buzz_Aldrin");
        participantNeil.addStringValue(HQDM.ENTITY_NAME, "Participant_state_of_Neil_Armstrong");
        lm5AscentModule.addStringValue(HQDM.ENTITY_NAME, "LM-5");
        neilArmstrongPerson.addStringValue(HQDM.ENTITY_NAME, "Neil_Armstrong");
        buzzAldrinPerson.addStringValue(HQDM.ENTITY_NAME, "Buzz_Aldrin");

        MermaidUtils.writeLRNodeEdgeGraph(List.of(
                returnFromLunarSurfaceActivity,
                participantLM5AscentModule,
                participantBuzz,
                participantNeil),
                List.of("record_created", "record_creator", "comment"),
                List.of("Return_from_Lunar_Surface_to_CSM",
                        "Participant_state_of_LM5_Ascent_Module",
                        "Participant_state_of_Buzz_Aldrin",
                        "Participant_state_of_Neil_Armstrong",
                        "data_EntityName"),
                "activityLM5AscentAndParticipantsWithNamePredicates");

        mcDatasets.add(eagleActivityService);
    }

}
