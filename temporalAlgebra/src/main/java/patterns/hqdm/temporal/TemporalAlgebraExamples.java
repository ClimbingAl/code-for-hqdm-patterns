package patterns.hqdm.temporal;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

import hqdm.utils.base.BaseCollection;
import hqdm.utils.base.HqdmObjectBaseProperties;
import hqdm.utils.base.IriUtils;
import uk.gov.gchq.magmacore.hqdm.model.Thing;
import uk.gov.gchq.magmacore.hqdm.rdf.iri.HQDM;

import uk.gov.gchq.magmacore.service.MagmaCoreService;
import uk.gov.gchq.magmacore.service.transformation.DbTransformation;

public class TemporalAlgebraExamples {

    /**
     * Create a new objects, construct DbTransformation for it and then commit
     * to database. This class creates test objects for hqdmHaskell Allen's Temporal
     * algebra.
     * 
     * Time ordered event names (separated by a fixed time period from 
     * "2021-07-05T14:40:25.4368657Z"):
     * 
     *      t1, t2, t3, t4, t5, t6
     * 
     * Allen's Algebra States applid to 4D objects (order of objects important for tests):
     * 
     * 1. Object Z with no temporal bounds, Object A (t1, t2) ==> AllenNull
     * 
     * 2. Object A (t1, t2) precedes Object B (t3, t6) ==> PrecedesSnd
     * 
     * 3. Object B (t3, t6) is preceded by Object A (t1, t2) ==> PrecedesFst
     * 
     * 4. Object A (t1, t2) meets Object C (t2, t5) ==> MeetsSnd
     * 
     * 5. Object C (t2, t5) is met by Object A (t1, t2) ==> MeetsFst
     * 
     * 6. Object C (t2, t5) overlaps with Object B (t3, t6) ==> OverlapsSnd
     * 
     * 7. Object B (t3, t6) is overlapped by Object C (t2, t5) ==> OverlapsFst
     * 
     * 8. Object D (t2, t4) starts Object C (t2, t5) ==> StartsSnd
     * 
     * 9. Object C (t2, t5) started by Object D (t2, t4) ==> StartsFst
     * 
     * 10. Object C (t2, t5) during Object E (t1, t6) ==> DuringSnd
     * 
     * 11. Object E (t1, t6) contains Object C (t2, t5) ==> DuringFst
     * 
     * 12. Object C (t2, t5) during Object F (t1, ) ==> DuringSndUnbounded
     * 
     * 13. Object F (t1, ) contains Object C (t2, t5) ==> DuringFstUnbounded
     * 
     * 14. Object C (t2, t5) finished Object G (t1, t5) ==> EndsSnd
     * 
     * 15. Object G (t1, t5) is finished by Object C (t2, t5) ==> EndsFst
     * 
     * 16. Object A (t1, t2) is equal to Object H (t1, t2) ==> EqualExtent
     * 
     * 17. Object I ( , t2) is equal to Object J ( , t2) with definite end only ==> EqualExtentUnboundedStart
     * 
     * 18. Object K ( t1, ) is equal to Object F ( t1, ) with definite start only ==> EqualExtentUnboundedEnd
     * 
     * Currently not tested: DuringFstBothUnbounded, DuringSndBothUnbounded
     * 
     * (Note: This test framework is only testing temporal interval - extent - reasoning.
     * No poart-hood relation calculus will be tested here.)
     *
     * @param baseCollection {@Link BaseCollection} IRIs for the dataset
     * @param mcDatasets {@link List<MagmaCoreService>} Master list for existing datasets
     * @param temporalAlgebra {@Link MagmaCoreService} The service to add the triples to
     * @param record_creator The String containing id of the records creator
     * @return
     */
    public static void createAndAddTemporalAlgebraExampleObjects(
        BaseCollection baseCollection, 
        final List<MagmaCoreService> mcDatasets, 
        MagmaCoreService temporalAlgebraService, 
        String record_creator) {

        System.out.println("Create Temporal Algebra data objects!");
        System.out.println("\tData generated in TTL in example-files/temporalAlgebra.ttl.");

        // Create timestamps
        int monthIncrement = 3;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSX"); //2025-05-26T11:25:29.015458Z
        String dt1 = LocalDateTime.now().toInstant(ZoneOffset.UTC).toString();  // The initial time in the ordered timestamps for the test events
        String dt2 = LocalDateTime.now().plusMonths(monthIncrement).atOffset(ZoneOffset.UTC).format(formatter).toString();
        String dt3 = LocalDateTime.now().plusMonths(monthIncrement*2).atOffset(ZoneOffset.UTC).format(formatter).toString();
        String dt4 = LocalDateTime.now().plusMonths(monthIncrement*3).atOffset(ZoneOffset.UTC).format(formatter).toString();
        String dt5 = LocalDateTime.now().plusMonths(monthIncrement*4).atOffset(ZoneOffset.UTC).format(formatter).toString();
        String dt6 = LocalDateTime.now().plusMonths(monthIncrement*5).atOffset(ZoneOffset.UTC).format(formatter).toString();

        // Create the predicates of the whole-life individual object we want to create.
        // It needs a class (SET) to be a member_of_kind of
        final Thing individualKindOfObject = IriUtils.createNewBaseObject( baseCollection,
                        new HqdmObjectBaseProperties(
                                        HQDM.KIND_OF_INDIVIDUAL,
                                        baseCollection.PATTERNS_REF_BASE,
                                        "KindOfIndividual_AllenAlgebraTestKindOfIndividual",
                                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                        record_creator));
        individualKindOfObject.addStringValue(baseCollection.COMMENT, "Allen_Algebra_Test_Kind_Of_Individual");

        final Thing classOfStateOfObject = IriUtils.createNewBaseObject( baseCollection,
                        new HqdmObjectBaseProperties(
                                        HQDM.CLASS_OF_STATE,
                                        baseCollection.PATTERNS_REF_BASE,
                                        "ClassOfState_AllenAlgebraTestClassOfState",
                                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                        record_creator));
        classOfStateOfObject.addStringValue(baseCollection.COMMENT, "Allen_Algebra_Test_Class_Of_State");

        // The events need a class_of_point_in_time to be members of
        final Thing classOfEventObject = IriUtils.createNewBaseObject( baseCollection,
                        new HqdmObjectBaseProperties(
                                        HQDM.CLASS_OF_POINT_IN_TIME,
                                        baseCollection.PATTERNS_REF_BASE,
                                        "ClassOfPointInTime_4DAllenAlgebraTestClassOfPointInTime",
                                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                        record_creator));
        classOfEventObject.addStringValue(baseCollection.COMMENT, "4D_Allen_Algebra_Test_Class_Of_Point_In_Time");

        final Thing classOfPossibleWorldObject = IriUtils.createNewBaseObject( baseCollection,
                        new HqdmObjectBaseProperties(
                                        HQDM.CLASS_OF_POSSIBLE_WORLD,
                                        baseCollection.PATTERNS_REF_BASE,
                                        "ClassOfPossibleWorld__4DAllenAlgebraTestClassOfPossibleWorld",
                                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                        record_creator));
        classOfPossibleWorldObject.addStringValue(baseCollection.COMMENT,
                        "4D_Allen_Algebra_Test_Class_Of_Possible_World");

        // Now create the Events and States
        final Thing possibleWorldObject = IriUtils.createNewBaseObject( baseCollection,
                        new HqdmObjectBaseProperties(
                                        HQDM.POSSIBLE_WORLD,
                                        baseCollection.PATTERNS_BASE,
                                        "Possible_World_for_4D_Allen_Algebra_Test_Framework",
                                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                        record_creator));
        possibleWorldObject.addValue(HQDM.MEMBER_OF, classOfPossibleWorldObject.getId());

        // Create the tx events as point_in_time objects.  This is done long-hand to allow the variables 
        // to be handled concretely in the code to ensure the intended object patterns are asserted 
        // for use in Allen Algebra tests.
        final Thing t1 = IriUtils.createNewBaseObject( baseCollection,
                        new HqdmObjectBaseProperties(
                                        HQDM.POINT_IN_TIME,
                                        baseCollection.PATTERNS_BASE,
                                        dt1,
                                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                        record_creator));
        t1.addValue(HQDM.MEMBER_OF, classOfEventObject.getId());
        t1.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldObject.getId());

        final Thing t2 = IriUtils.createNewBaseObject( baseCollection,
                        new HqdmObjectBaseProperties(
                                        HQDM.POINT_IN_TIME,
                                        baseCollection.PATTERNS_BASE,
                                        dt2,
                                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                        record_creator));
        t2.addValue(HQDM.MEMBER_OF, classOfEventObject.getId());
        t2.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldObject.getId());

        final Thing t3 = IriUtils.createNewBaseObject( baseCollection,
                        new HqdmObjectBaseProperties(
                                        HQDM.POINT_IN_TIME,
                                        baseCollection.PATTERNS_BASE,
                                        dt3,
                                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                        record_creator));
        t3.addValue(HQDM.MEMBER_OF, classOfEventObject.getId());
        t3.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldObject.getId());

        final Thing t4 = IriUtils.createNewBaseObject( baseCollection,
                        new HqdmObjectBaseProperties(
                                        HQDM.POINT_IN_TIME,
                                        baseCollection.PATTERNS_BASE,
                                        dt4,
                                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                        record_creator));
        t4.addValue(HQDM.MEMBER_OF, classOfEventObject.getId());
        t4.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldObject.getId());

        final Thing t5 = IriUtils.createNewBaseObject( baseCollection,
                        new HqdmObjectBaseProperties(
                                        HQDM.POINT_IN_TIME,
                                        baseCollection.PATTERNS_BASE,
                                        dt5,
                                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                        record_creator));
        t5.addValue(HQDM.MEMBER_OF, classOfEventObject.getId());
        t5.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldObject.getId());

        final Thing t6 = IriUtils.createNewBaseObject( baseCollection,
                        new HqdmObjectBaseProperties(
                                        HQDM.POINT_IN_TIME,
                                        baseCollection.PATTERNS_BASE,
                                        dt6,
                                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                        record_creator));
        t6.addValue(HQDM.MEMBER_OF, classOfEventObject.getId());
        t6.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldObject.getId());
        
        // Create the individual object with no temporal bounds (as Object Z)
        final Thing objectZ = IriUtils.createNewBaseObject( baseCollection,
                        new HqdmObjectBaseProperties(
                                        HQDM.INDIVIDUAL,
                                        baseCollection.PATTERNS_BASE,
                                        "Object_Z",
                                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                        record_creator));
        objectZ.addValue(HQDM.MEMBER_OF_KIND, individualKindOfObject.getId());
        objectZ.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldObject.getId());


        // Create the states for Objects A to K
        final Thing objectA = IriUtils.createNewBaseObject( baseCollection,
                        new HqdmObjectBaseProperties(
                                        HQDM.STATE,
                                        baseCollection.PATTERNS_BASE,
                                        "Object_A",
                                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                        record_creator));
        objectA.addValue(HQDM.MEMBER_OF, classOfStateOfObject.getId());
        objectA.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldObject.getId());
        objectA.addValue(HQDM.BEGINNING, t1.getId());
        objectA.addValue(HQDM.ENDING, t2.getId());
        objectA.addValue(HQDM.TEMPORAL_PART_OF, objectZ.getId());

        final Thing objectB = IriUtils.createNewBaseObject( baseCollection,
                        new HqdmObjectBaseProperties(
                                        HQDM.STATE,
                                        baseCollection.PATTERNS_BASE,
                                        "Object_B",
                                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                        record_creator));
        objectB.addValue(HQDM.MEMBER_OF, classOfStateOfObject.getId());
        objectB.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldObject.getId());
        objectB.addValue(HQDM.BEGINNING, t3.getId());
        objectB.addValue(HQDM.ENDING, t6.getId());

        final Thing objectC = IriUtils.createNewBaseObject( baseCollection,
                        new HqdmObjectBaseProperties(
                                        HQDM.STATE,
                                        baseCollection.PATTERNS_BASE,
                                        "Object_C",
                                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                        record_creator));
        objectC.addValue(HQDM.MEMBER_OF, classOfStateOfObject.getId());
        objectC.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldObject.getId());
        objectC.addValue(HQDM.BEGINNING, t2.getId());
        objectC.addValue(HQDM.ENDING, t5.getId());

        final Thing objectD = IriUtils.createNewBaseObject( baseCollection,
                        new HqdmObjectBaseProperties(
                                        HQDM.STATE,
                                        baseCollection.PATTERNS_BASE,
                                        "Object_D",
                                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                        record_creator));
        objectD.addValue(HQDM.MEMBER_OF, classOfStateOfObject.getId());
        objectD.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldObject.getId());
        objectD.addValue(HQDM.BEGINNING, t2.getId());
        objectD.addValue(HQDM.ENDING, t4.getId());

        final Thing objectE = IriUtils.createNewBaseObject( baseCollection,
                        new HqdmObjectBaseProperties(
                                        HQDM.STATE,
                                        baseCollection.PATTERNS_BASE,
                                        "Object_E",
                                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                        record_creator));
        objectE.addValue(HQDM.MEMBER_OF, classOfStateOfObject.getId());
        objectE.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldObject.getId());
        objectE.addValue(HQDM.BEGINNING, t1.getId());
        objectE.addValue(HQDM.ENDING, t6.getId());

        final Thing objectF = IriUtils.createNewBaseObject( baseCollection,
                        new HqdmObjectBaseProperties(
                                        HQDM.STATE,
                                        baseCollection.PATTERNS_BASE,
                                        "Object_F",
                                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                        record_creator));
        objectF.addValue(HQDM.MEMBER_OF, classOfStateOfObject.getId());
        objectF.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldObject.getId());
        objectF.addValue(HQDM.BEGINNING, t1.getId());

        final Thing objectG = IriUtils.createNewBaseObject( baseCollection,
                        new HqdmObjectBaseProperties(
                                        HQDM.STATE,
                                        baseCollection.PATTERNS_BASE,
                                        "Object_G",
                                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                        record_creator));
        objectG.addValue(HQDM.MEMBER_OF, classOfStateOfObject.getId());
        objectG.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldObject.getId());
        objectG.addValue(HQDM.BEGINNING, t1.getId());
        objectG.addValue(HQDM.ENDING, t5.getId());

        final Thing objectH = IriUtils.createNewBaseObject( baseCollection,
                        new HqdmObjectBaseProperties(
                                        HQDM.STATE,
                                        baseCollection.PATTERNS_BASE,
                                        "Object_H",
                                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                        record_creator));
        objectH.addValue(HQDM.MEMBER_OF, classOfStateOfObject.getId());
        objectH.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldObject.getId());
        objectH.addValue(HQDM.BEGINNING, t1.getId());
        objectH.addValue(HQDM.ENDING, t2.getId());

       final Thing objectI = IriUtils.createNewBaseObject( baseCollection,
                        new HqdmObjectBaseProperties(
                                        HQDM.STATE,
                                        baseCollection.PATTERNS_BASE,
                                        "Object_I",
                                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                        record_creator));
        objectI.addValue(HQDM.MEMBER_OF, classOfStateOfObject.getId());
        objectI.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldObject.getId());
        objectI.addValue(HQDM.ENDING, t2.getId());

       final Thing objectJ = IriUtils.createNewBaseObject( baseCollection,
                        new HqdmObjectBaseProperties(
                                        HQDM.STATE,
                                        baseCollection.PATTERNS_BASE,
                                        "Object_J",
                                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                        record_creator));
        objectJ.addValue(HQDM.MEMBER_OF, classOfStateOfObject.getId());
        objectJ.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldObject.getId());
        objectJ.addValue(HQDM.ENDING, t2.getId());

        final Thing objectK = IriUtils.createNewBaseObject( baseCollection,
                        new HqdmObjectBaseProperties(
                                        HQDM.STATE,
                                        baseCollection.PATTERNS_BASE,
                                        "Object_K",
                                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                                        record_creator));
        objectK.addValue(HQDM.MEMBER_OF, classOfStateOfObject.getId());
        objectK.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldObject.getId());
        objectK.addValue(HQDM.BEGINNING, t1.getId());

        // Commit to MC database
        final DbTransformation temporalAlgebraChangeSet = temporalAlgebraService.createDbTransformation(
                        List.of(
                                individualKindOfObject,
                                classOfStateOfObject,
                                classOfEventObject,
                                classOfPossibleWorldObject,
                                possibleWorldObject,
                                t1,
                                t2,
                                t3,
                                t4,
                                t5,
                                t6,
                                objectZ,
                                objectA,
                                objectB,
                                objectC,
                                objectD,
                                objectE,
                                objectF,
                                objectG,
                                objectH,
                                objectI,
                                objectJ,
                                objectK
                                ));

        temporalAlgebraService.runInWriteTransaction(temporalAlgebraChangeSet);

        try {
            final PrintStream ttl_stream_out = new PrintStream("example-files/temporalAlgebra.ttl");

            temporalAlgebraService.exportTtl(ttl_stream_out);
            ttl_stream_out.close();
        } catch (FileNotFoundException e) {
            System.err.println("temporal algebra example write: " + e);
        }

        mcDatasets.add(temporalAlgebraService);

    }

}
