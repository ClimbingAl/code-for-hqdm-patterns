package patterns.hqdm.system;

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
import uk.gov.gchq.magmacore.hqdm.model.ClassOfInstalledFunctionalSystemComponent;
import uk.gov.gchq.magmacore.hqdm.model.ClassOfPointInTime;
import uk.gov.gchq.magmacore.hqdm.model.ClassOfStateOfFunctionalSystem;
import uk.gov.gchq.magmacore.hqdm.model.FunctionalSystem;
import uk.gov.gchq.magmacore.hqdm.model.FunctionalSystemComponent;
import uk.gov.gchq.magmacore.hqdm.model.InstalledFunctionalSystemComponent;
import uk.gov.gchq.magmacore.hqdm.model.KindOfFunctionalSystem;
import uk.gov.gchq.magmacore.hqdm.model.KindOfFunctionalSystemComponent;
import uk.gov.gchq.magmacore.hqdm.model.PointInTime;
import uk.gov.gchq.magmacore.hqdm.model.PossibleWorld;
import uk.gov.gchq.magmacore.hqdm.model.Role;
import uk.gov.gchq.magmacore.hqdm.model.Thing;
import uk.gov.gchq.magmacore.hqdm.rdf.iri.HQDM;
import uk.gov.gchq.magmacore.hqdm.rdf.iri.IRI;
import uk.gov.gchq.magmacore.hqdm.rdfbuilders.ClassOfInstalledFunctionalSystemComponentBuilder;
import uk.gov.gchq.magmacore.hqdm.rdfbuilders.ClassOfStateOfFunctionalSystemBuilder;
import uk.gov.gchq.magmacore.hqdm.rdfbuilders.FunctionalSystemComponentBuilder;
import uk.gov.gchq.magmacore.hqdm.rdfbuilders.InstalledFunctionalSystemComponentBuilder;
import uk.gov.gchq.magmacore.hqdm.rdfbuilders.KindOfFunctionalSystemBuilder;
import uk.gov.gchq.magmacore.hqdm.rdfbuilders.KindOfFunctionalSystemComponentBuilder;
import uk.gov.gchq.magmacore.hqdm.rdfbuilders.PointInTimeBuilder;
import uk.gov.gchq.magmacore.service.MagmaCoreService;
import uk.gov.gchq.magmacore.service.MagmaCoreServiceFactory;
import uk.gov.gchq.magmacore.service.transformation.DbTransformation;

import static uk.gov.gchq.magmacore.util.UID.uid;

public class SystemAndComponentsApolloExample {

    /**
     * Query LM-5 System, add component parts and the installed states
     * for them and then commit to database.
     *
     * @param mcDatasets {@link List<MagmaCoreService>}.
     * @return
     */
    public static void createAndAddSystemAndSystemComponentApolloPattern(BaseCollection baseCollection, final List<MagmaCoreService> mcDatasets, MagmaCoreService systemComponentService, String record_creator) {

        System.out.println("Create Eagle System Components data objects.");

        // Find existing objects that are members of
        // "Class_of_possible_world_for_abstract_pattern_examples"
        final PossibleWorld possibleWorldForEagleExamples = (PossibleWorld) QueryUtils.findThingInServiceByName(
                mcDatasets,
                "Possible_world_for_Apollo_pattern_examples");

        final KindOfFunctionalSystem lunarLanderKind = (KindOfFunctionalSystem) QueryUtils.findThingInServiceByName(
                mcDatasets,
                "KindOfFunctionalSystem__Lunar_Lander");

        final FunctionalSystem lunarLander = (FunctionalSystem) QueryUtils.findThingsInServicesByPredicateAndValue(
                mcDatasets,
                HQDM.MEMBER_OF_KIND,
                lunarLanderKind.getId()).get(0);

        final Thing classOfStateOfLunarLander = QueryUtils.findThingInServiceByName(mcDatasets,
                "ClassOfStateOfFunctionalSystem__State_Of_Lunar_Lander");

        // Create the classes of systems for the Ascent and Descent Systems

        final KindOfFunctionalSystem lunarLanderAscentSystemKind = (KindOfFunctionalSystem) IriUtils.createNewBaseObject(baseCollection,
                new HqdmObjectBaseProperties(
                        HQDM.KIND_OF_FUNCTIONAL_SYSTEM,
                        baseCollection.PATTERNS_REF_BASE,
                        "KindOfFunctionalSystem__Lunar_Lander_Ascent_System",
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                        record_creator));

        lunarLanderAscentSystemKind.addStringValue(baseCollection.COMMENT,
                "Kind_of_functional_system_that_is_Lunar_Lander_Ascent_Module._Note_this_is_subclass_of_class_of_ordinary_functional_object.");

        final KindOfFunctionalSystem lunarLanderDescentSystemKind = (KindOfFunctionalSystem) IriUtils.createNewBaseObject(baseCollection, 
                new HqdmObjectBaseProperties(
                        HQDM.KIND_OF_FUNCTIONAL_SYSTEM,
                        baseCollection.PATTERNS_REF_BASE,
                        "KindOfFunctionalSystem__Lunar_Lander_Descent_System",
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                        record_creator));

        lunarLanderDescentSystemKind.addStringValue(baseCollection.COMMENT,
                "Kind_of_functional_system_that_is_Lunar_Lander_Descent_Module._Note_this_is_subclass_of_class_of_ordinary_functional_object.");

        final ClassOfStateOfFunctionalSystem lunarLanderAscentModuleClassOfState = new ClassOfStateOfFunctionalSystemBuilder(
                new IRI(baseCollection.PATTERNS_REF_BASE, uid()))
                .build();
        IriUtils.addBasePropertiesToThing(baseCollection, lunarLanderAscentModuleClassOfState,
                new HqdmObjectBaseProperties(
                        "ClassOfStateOfFunctionalSystem__Class_Of_State_Of_Lunar_Lander_Ascent_System",
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                        record_creator));
        lunarLanderAscentModuleClassOfState.addStringValue(baseCollection.COMMENT,
                "Class_of_state_of_functional_system_whose_members_are_temporal_part_of_members_of_Lunar_Lander_Ascent_System");
        lunarLanderAscentModuleClassOfState.addValue(HQDM.PART__OF_BY_CLASS, lunarLanderAscentSystemKind.getId());

        final ClassOfStateOfFunctionalSystem lunarLanderDescentModuleClassOfState = new ClassOfStateOfFunctionalSystemBuilder(
                new IRI(baseCollection.PATTERNS_REF_BASE, uid()))
                .build();
        IriUtils.addBasePropertiesToThing(baseCollection, lunarLanderDescentModuleClassOfState,
                new HqdmObjectBaseProperties(
                        "ClassOfStateOfFunctionalSystem__Class_Of_State_Of_Lunar_Lander_Descent_System",
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                        record_creator));
        lunarLanderDescentModuleClassOfState.addStringValue(baseCollection.COMMENT,
                "Class_of_state_of_functional_system_whose_members_are_temporal_part_of_members_of_Lunar_Lander_Descent_System");
        lunarLanderDescentModuleClassOfState.addValue(HQDM.PART__OF_BY_CLASS, lunarLanderDescentSystemKind.getId());

        // Create the classes of system components for the Ascent and Descent System
        // components

        final KindOfFunctionalSystemComponent lunarLanderAscentSystemComponentKind = new KindOfFunctionalSystemComponentBuilder(
                new IRI(baseCollection.PATTERNS_REF_BASE, uid()))
                .part__Of_By_Class(lunarLanderKind)
                .build();
        IriUtils.addBasePropertiesToThing(baseCollection, lunarLanderAscentSystemComponentKind,
                new HqdmObjectBaseProperties(
                        "KindOfSystemComponent__Kind_of_Lunar_Lander_Ascescent_System_Component",
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                        record_creator));
        lunarLanderAscentSystemComponentKind.addStringValue(baseCollection.COMMENT,
                "Kind_of_system_component_whose_members_are_temporal_part_of_members_of_Lunar_Lander_Asescent_System");

        final ClassOfInstalledFunctionalSystemComponent classOfInstalledLunarLanderAscentModule = new ClassOfInstalledFunctionalSystemComponentBuilder(
                new IRI(baseCollection.PATTERNS_REF_BASE, uid()))
                .part__Of_By_Class(lunarLanderAscentSystemComponentKind)
                .has_Superclass(lunarLanderAscentModuleClassOfState)
                .build();
        IriUtils.addBasePropertiesToThing(baseCollection, classOfInstalledLunarLanderAscentModule,
                new HqdmObjectBaseProperties(
                        "ClassOfInstalledFunctionalSystemComponent__Class_of_Installed_Lunar_Lander_Ascent_System_Component",
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                        record_creator));
        classOfInstalledLunarLanderAscentModule.addStringValue(baseCollection.COMMENT,
                "Class_of_installed_system_component_whose_members_are_temporal_part_of_members_of_Lunar_Lander_Ascent_System_AND_Lunar_Lander_Ascent_System_Component");

        final KindOfFunctionalSystemComponent lunarLanderDescentSystemComponentKind = new KindOfFunctionalSystemComponentBuilder(
                new IRI(baseCollection.PATTERNS_REF_BASE, uid()))
                .part__Of_By_Class(lunarLanderKind)
                .build();
        IriUtils.addBasePropertiesToThing(baseCollection, lunarLanderDescentSystemComponentKind,
                new HqdmObjectBaseProperties(
                        "KindOfSystemComponent__Kind_of_Lunar_Lander_Ascescent_System_Component",
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                        record_creator));
        lunarLanderDescentSystemComponentKind.addStringValue(baseCollection.COMMENT,
                "Kind_of_system_component_whose_members_are_temporal_part_of_members_of_Lunar_Lander_Descent_System");

        final ClassOfInstalledFunctionalSystemComponent classOfInstalledLunarLanderDescentModule = new ClassOfInstalledFunctionalSystemComponentBuilder(
                new IRI(baseCollection.PATTERNS_REF_BASE, uid()))
                .part__Of_By_Class(lunarLanderDescentSystemComponentKind)
                .has_Superclass(lunarLanderDescentModuleClassOfState)
                .build();
        IriUtils.addBasePropertiesToThing(baseCollection, classOfInstalledLunarLanderDescentModule,
                new HqdmObjectBaseProperties(
                        "ClassOfInstalledFunctionalSystemComponent__Class_of_Installed_Lunar_Lander_Descent_System_Component",
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                        record_creator));
        classOfInstalledLunarLanderDescentModule.addStringValue(baseCollection.COMMENT,
                "Class_of_installed_system_component_whose_members_are_temporal_part_of_members_of_Lunar_Lander_Descent_System_AND_Lunar_Lander_Descent_System_Component");
        
        // Placeholder roles - need to join with Activity Modeller roles. Builders not used here as cardinality dependencies can't yet be met.
        final Role intendedRoleOfDescentSystemComponent = (Role) IriUtils.createNewBaseObject(baseCollection, new HqdmObjectBaseProperties(
                    HQDM.ROLE,
                    baseCollection.PATTERNS_REF_BASE,
                    "RoleOfLunarLanderDescentSystemComponent",
                    LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                    record_creator));
        intendedRoleOfDescentSystemComponent.addStringValue(baseCollection.COMMENT,
                    "Generic_role_of_Lunar_Lander_Descent_Module_System_Component_when_functioning_in_intended_activities.");

        final Role intendedRoleOfAscentSystemComponent = (Role) IriUtils.createNewBaseObject(baseCollection, new HqdmObjectBaseProperties(
                    HQDM.ROLE,
                    baseCollection.PATTERNS_REF_BASE,
                    "RoleOfLunarLanderAscentSystemComponent",
                    LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                    record_creator));
        intendedRoleOfAscentSystemComponent.addStringValue(baseCollection.COMMENT,
                    "Generic_role_of_Lunar_Lander_Ascent_Module_System_Component_when_functioning_in_intended_activities.");

        // Add the system component classes to the Lunar Lander as
        // consists_of_component_by_class
        final KindOfFunctionalSystem lunarLanderKindModified = new KindOfFunctionalSystemBuilder(
                lunarLanderKind.getId())
                .has_Component_By_Class_M(lunarLanderAscentSystemComponentKind)
                .has_Component_By_Class_M(lunarLanderDescentSystemComponentKind)
                .build();
        IriUtils.addBasePropertiesToThing(baseCollection, lunarLanderKindModified,
                new HqdmObjectBaseProperties(
                        "KindOfFunctionalSystem__Lunar_Lander",
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                        record_creator));
        lunarLanderKindModified.addStringValue(baseCollection.COMMENT,
                "Kind_of_functional_system_that_is_Lunar_Lander._Note_this_is_subclass_of_class_of_ordinary_functional_object.");
        lunarLanderKindModified.addStringValue(baseCollection.COMMENT,
                "Modified_reference_data_class_during_system_and_system_component_Apollo_worked_example.");

        //////////// CREATE ACTUAL STATES NOW ////////////
        // Events

        final ClassOfPointInTime classOfPointInTimeObject = (ClassOfPointInTime) QueryUtils.findThingInServiceByName(
                mcDatasets,
                "ClassOfPointInTime__ISO8601_DateTime");

        final PointInTime landerSystemEndPointInTime = (PointInTime) QueryUtils.findThingInServiceByName(mcDatasets,
                "1969-07-21T17:54:01");

        final PointInTime landerSystemStartPointInTime = IriUtils.createPointInTime( baseCollection,
                "1968-01-01T20:17:40+00:00[UTC]", 
                classOfPointInTimeObject, 
                possibleWorldForEagleExamples, 
                record_creator);

        final PointInTime landerDescentSystemInstalledStartPointInTime = IriUtils.createPointInTime( baseCollection,
                "1968-01-14T20:32:00+00:00[UTC]", 
                classOfPointInTimeObject, 
                possibleWorldForEagleExamples, 
                record_creator);

        final PointInTime landerAscentSystemInstalledStartPointInTime = IriUtils.createPointInTime( baseCollection,
                "1968-03-23T18:56:00+00:00[UTC]", 
                classOfPointInTimeObject, 
                possibleWorldForEagleExamples, 
                record_creator);

        // System component states
        final FunctionalSystemComponent ascentSystemComponent = new FunctionalSystemComponentBuilder(
                new IRI(baseCollection.PATTERNS_BASE, uid()))
                .part_Of_Possible_World_M(possibleWorldForEagleExamples)
                .member_Of_Kind_M(lunarLanderAscentSystemComponentKind)
                .component_Of_M(lunarLander)
                .beginning(landerSystemStartPointInTime)
                .intended_Role_M(intendedRoleOfAscentSystemComponent)
                .build();
        IriUtils.addBasePropertiesToThing(baseCollection, ascentSystemComponent,
                new HqdmObjectBaseProperties(
                        "Ascent_Module_System_Component_1969-059C",
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                        record_creator));

        final FunctionalSystemComponent descentSystemComponent = new FunctionalSystemComponentBuilder(
                new IRI(baseCollection.PATTERNS_BASE, uid()))
                .part_Of_Possible_World_M(possibleWorldForEagleExamples)
                .member_Of_Kind_M(lunarLanderDescentSystemComponentKind)
                .component_Of_M(lunarLander)
                .beginning(landerSystemStartPointInTime)
                .intended_Role_M(intendedRoleOfDescentSystemComponent)
                .build();
        IriUtils.addBasePropertiesToThing(baseCollection,descentSystemComponent,
                new HqdmObjectBaseProperties(
                        "Descent_Module_System_Component_1969-059D",
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                        record_creator));

        // Systems that are installed, for a period of time, in LM-5
        // Ascent system
        final FunctionalSystem ascentSystem = (FunctionalSystem) IriUtils.createNewBaseObject(baseCollection, new HqdmObjectBaseProperties(
                        HQDM.FUNCTIONAL_SYSTEM,
                        baseCollection.PATTERNS_BASE,
                        "Ascent_Module_Functional_System_1969-059C",
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                        record_creator));
        ascentSystem.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldForEagleExamples.getId());
        ascentSystem.addValue(HQDM.MEMBER_OF_KIND, lunarLanderAscentSystemKind.getId());

        final InstalledFunctionalSystemComponent installedAscentSystem = new InstalledFunctionalSystemComponentBuilder(
                new IRI(baseCollection.PATTERNS_BASE, uid()))
                .part_Of_Possible_World_M(possibleWorldForEagleExamples)
                .member_Of(classOfInstalledLunarLanderAscentModule)
                .beginning(landerAscentSystemInstalledStartPointInTime)
                .ending(landerSystemEndPointInTime)
                .temporal_Part_Of(ascentSystemComponent)
                .temporal__Part_Of(ascentSystem)
                .build();

        IriUtils.addBasePropertiesToThing(baseCollection, installedAscentSystem,
                new HqdmObjectBaseProperties(
                        "Ascent_Module_Installed_As_LM-5_System_Component_1969-059C",
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                        record_creator));

        // Descent system
        final FunctionalSystem descentSystem = (FunctionalSystem) IriUtils.createNewBaseObject(baseCollection, new HqdmObjectBaseProperties(
                        HQDM.FUNCTIONAL_SYSTEM,
                        baseCollection.PATTERNS_REF_BASE,
                        "Descent_Module_Functional_System_1969-059D",
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                        record_creator));
        descentSystem.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldForEagleExamples.getId());
        descentSystem.addValue(HQDM.MEMBER_OF_KIND, lunarLanderDescentSystemKind.getId());

        final InstalledFunctionalSystemComponent installedDescentSystem = new InstalledFunctionalSystemComponentBuilder(
                new IRI(baseCollection.PATTERNS_BASE, uid()))
                .part_Of_Possible_World_M(possibleWorldForEagleExamples)
                .member_Of(classOfInstalledLunarLanderDescentModule)
                .beginning(landerDescentSystemInstalledStartPointInTime)
                .ending(landerSystemEndPointInTime)
                .temporal_Part_Of(descentSystemComponent)
                .temporal__Part_Of(descentSystem)
                .build();

        IriUtils.addBasePropertiesToThing(baseCollection, installedDescentSystem,
                new HqdmObjectBaseProperties(
                        "Descent_Module_Installed_As_LM-5_System_Component_1969-059D",
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                        record_creator));

        // All system and system component objects created

        // Commit to MC database
        final DbTransformation systemChangeSet = systemComponentService.createDbTransformation(
                List.of(
                        lunarLander,
                        lunarLanderKindModified,
                        lunarLanderAscentSystemKind,
                        lunarLanderDescentSystemKind,
                        lunarLanderAscentModuleClassOfState,
                        lunarLanderDescentModuleClassOfState,
                        lunarLanderAscentSystemComponentKind,
                        classOfInstalledLunarLanderAscentModule,
                        lunarLanderDescentSystemComponentKind,
                        classOfInstalledLunarLanderDescentModule,
                        intendedRoleOfAscentSystemComponent,
                        intendedRoleOfDescentSystemComponent,
                        landerSystemStartPointInTime,
                        landerSystemEndPointInTime,
                        landerDescentSystemInstalledStartPointInTime,
                        landerAscentSystemInstalledStartPointInTime,
                        ascentSystemComponent,
                        descentSystemComponent,
                        ascentSystem,
                        installedAscentSystem,
                        descentSystem,
                        installedDescentSystem));
        systemComponentService.runInWriteTransaction(systemChangeSet);

        // Create node-edge graphs
        MermaidUtils.writeLRNodeEdgeGraph(
                List.of(
                        lunarLander,
                        ascentSystemComponent,
                        descentSystemComponent
                        ),
                List.of("references"),
                List.of(lunarLander.getId().toString().split("#")[1]),
                "systemAndComponentEagleExample");

        MermaidUtils.writeLRNodeEdgeGraph(
                List.of(
                        lunarLander,
                        ascentSystemComponent,
                        descentSystemComponent,
                        ascentSystem,
                        installedAscentSystem,
                        descentSystem,
                        installedDescentSystem
                        ),
                List.of("record_created", "record_creator", "comment"),
                List.of(lunarLander.getId().toString().split("#")[1]),
                "systemAndInstalledComponentEagleExample");

        MermaidUtils.writeLRNodeEdgeGraph(
                List.of(
                        lunarLander,
                        ascentSystemComponent,
                        descentSystemComponent,
                        ascentSystem,
                        installedAscentSystem,
                        descentSystem,
                        installedDescentSystem,
                        landerSystemStartPointInTime,
                        landerDescentSystemInstalledStartPointInTime,
                        landerAscentSystemInstalledStartPointInTime,
                        landerSystemEndPointInTime
                        ),
                List.of("record_created", "record_creator", "comment"),
                List.of(lunarLander.getId().toString().split("#")[1]),
                "systemAndInstalledComponentEventsEagleExample");

        MermaidUtils.writeLRNodeEdgeGraph(List.of(
                lunarLanderKindModified,
                lunarLanderAscentSystemKind,
                lunarLanderDescentSystemKind,
                lunarLanderAscentModuleClassOfState,
                lunarLanderDescentModuleClassOfState,
                lunarLanderAscentSystemComponentKind,
                classOfInstalledLunarLanderAscentModule,
                lunarLanderDescentSystemComponentKind,
                classOfInstalledLunarLanderDescentModule,
                intendedRoleOfAscentSystemComponent,
                intendedRoleOfDescentSystemComponent,
                landerSystemStartPointInTime,
                landerDescentSystemInstalledStartPointInTime,
                landerAscentSystemInstalledStartPointInTime,
                landerSystemEndPointInTime,
                lunarLander,
                ascentSystemComponent,
                descentSystemComponent,
                ascentSystem,
                installedAscentSystem,
                descentSystem,
                installedDescentSystem),
                List.of("record_created", "record_creator", "comment"),
                List.of(lunarLander.getId().toString().split("#")[1]),
                "systemAndComponentEagleExampleFull");

        try {
            final PrintStream ttl_stream_out = new PrintStream("example-files/systemAndComponentEaglePattern.ttl");

            systemComponentService.exportTtl(ttl_stream_out);
            ttl_stream_out.close();
            System.out.println(
                    "\tData being generated as TTL in example-files/systemAndComponentEaglePattern.ttl.");

        } catch (FileNotFoundException e) {
            System.err.println("systemAndComponentEaglePattern example write: " + e);
        }

        ///// REMEMBER TO DELETE lunarLander Kind now that it has system components by class from original dataset as updated object is
        ///// in this one.  Also, beware that lunarlander is stored in this ttl file and is in the database twice - should delete this.

        // Now add another instance of the ascent module being installed and then removed prior to it leaving the factory, 
        // to illusrtrate the pattern.


        // Create events around this happening
        final PointInTime landerAscentSystemFactoryFirstInstallPointInTime = IriUtils.createPointInTime( baseCollection,
                "1968-01-26T09:23:00+00:00[UTC]", 
                classOfPointInTimeObject, 
                possibleWorldForEagleExamples, 
                record_creator);

        final PointInTime landerAscentSystemFactoryUninistallPointInTime = IriUtils.createPointInTime( baseCollection,
                "1968-02-18T15:42:00+00:00[UTC]", 
                classOfPointInTimeObject, 
                possibleWorldForEagleExamples, 
                record_creator);

        // Create installed state
         final InstalledFunctionalSystemComponent initialInstalledAscentSystem = new InstalledFunctionalSystemComponentBuilder(
                new IRI(baseCollection.PATTERNS_BASE, uid()))
                .part_Of_Possible_World_M(possibleWorldForEagleExamples)
                .member_Of(classOfInstalledLunarLanderAscentModule)
                .beginning(landerAscentSystemFactoryFirstInstallPointInTime)
                .ending(landerAscentSystemFactoryUninistallPointInTime)
                .temporal_Part_Of(ascentSystemComponent)
                .temporal__Part_Of(ascentSystem)
                .build();

                IriUtils.addBasePropertiesToThing(baseCollection, initialInstalledAscentSystem,
                new HqdmObjectBaseProperties(
                        "Ascent_Module_Initial_Test_Install_As_LM-5_System_Component_1969-059C",
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                        record_creator));

         final DbTransformation systemSecondChangeSet = systemComponentService.createDbTransformation(
                List.of(
                        landerAscentSystemFactoryFirstInstallPointInTime,
                        landerAscentSystemFactoryUninistallPointInTime,
                        initialInstalledAscentSystem
                        ));
        systemComponentService.runInWriteTransaction(systemSecondChangeSet);

        try {
            final PrintStream ttl_stream_out = new PrintStream("example-files/systemInstallAndUinistallEaglePattern.ttl");

            systemComponentService.exportTtl(ttl_stream_out);
            ttl_stream_out.close();
            System.out.println(
                    "\tData being generated as TTL in example-files/systemInstallAndUinistallEaglePattern.ttl.");

        } catch (FileNotFoundException e) {
            System.err.println("systemInstallAndUinistallEaglePattern example write: " + e);
        }
        
        mcDatasets.add(systemComponentService);
    }
}
