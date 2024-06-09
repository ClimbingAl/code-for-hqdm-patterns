package patterns.hqdm.system;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import hqdm.utils.base.BaseCollection;
import hqdm.utils.base.HqdmObjectBaseProperties;
import hqdm.utils.base.IriUtils;
import hqdm.utils.mermaid.MermaidUtils;
import hqdm.utils.query.QueryUtils;
import uk.gov.gchq.magmacore.hqdm.model.ClassOfInstalledFunctionalSystemComponent;
import uk.gov.gchq.magmacore.hqdm.model.ClassOfPointInTime;
import uk.gov.gchq.magmacore.hqdm.model.ClassOfStateOfOrdinaryFunctionalObject;
import uk.gov.gchq.magmacore.hqdm.model.FunctionalSystem;
import uk.gov.gchq.magmacore.hqdm.model.FunctionalSystemComponent;
import uk.gov.gchq.magmacore.hqdm.model.InstalledFunctionalSystemComponent;
import uk.gov.gchq.magmacore.hqdm.model.KindOfFunctionalSystem;
import uk.gov.gchq.magmacore.hqdm.model.KindOfFunctionalSystemComponent;
import uk.gov.gchq.magmacore.hqdm.model.KindOfOrdinaryFunctionalObject;
import uk.gov.gchq.magmacore.hqdm.model.OrdinaryFunctionalObject;
import uk.gov.gchq.magmacore.hqdm.model.PointInTime;
import uk.gov.gchq.magmacore.hqdm.model.PossibleWorld;
import uk.gov.gchq.magmacore.hqdm.model.Role;
import uk.gov.gchq.magmacore.hqdm.rdf.iri.HQDM;
import uk.gov.gchq.magmacore.hqdm.rdf.iri.IRI;
import uk.gov.gchq.magmacore.hqdm.rdfbuilders.ClassOfInstalledFunctionalSystemComponentBuilder;
import uk.gov.gchq.magmacore.hqdm.rdfbuilders.ClassOfStateOfOrdinaryFunctionalObjectBuilder;
import uk.gov.gchq.magmacore.hqdm.rdfbuilders.FunctionalSystemBuilder;
import uk.gov.gchq.magmacore.hqdm.rdfbuilders.FunctionalSystemComponentBuilder;
import uk.gov.gchq.magmacore.hqdm.rdfbuilders.InstalledFunctionalSystemComponentBuilder;
import uk.gov.gchq.magmacore.hqdm.rdfbuilders.KindOfFunctionalSystemComponentBuilder;
import uk.gov.gchq.magmacore.hqdm.rdfbuilders.KindOfOrdinaryFunctionalObjectBuilder;
import uk.gov.gchq.magmacore.service.MagmaCoreService;
import uk.gov.gchq.magmacore.service.transformation.DbTransformation;

import static uk.gov.gchq.magmacore.util.UID.uid;

public class SystemAndComponentsExample {

    /**
     * Query System, add component parts and the installed states
     * for them and then commit to database.
     *
     * @param mcDatasets {@link List<MagmaCoreService>}.
     * @return
     */
    public static void createAndAddSystemAndSystemComponentGenericPattern(BaseCollection baseCollection, final List<MagmaCoreService> mcDatasets, MagmaCoreService systemComponentService, String record_creator) {

        System.out.println("Create Generic System and System Components data objects.");

        // Find existing objects that are members of
        // "Class_of_possible_world_for_abstract_pattern_examples"
        final PossibleWorld possibleWorldForGenericExamples = (PossibleWorld) QueryUtils.findThingInServiceByName(
                mcDatasets,
                "Possible_World_for_generic_pattern_examples");
        
        final Role intendedRoleOfSystem = (Role) IriUtils.createNewBaseObject( baseCollection,new HqdmObjectBaseProperties(
                    HQDM.ROLE,
                    baseCollection.PATTERNS_REF_BASE,
                    "RoleOfGenericSystem",
                    LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                    record_creator));
        intendedRoleOfSystem.addStringValue(baseCollection.COMMENT,
                    "Generic_role_of_System_in_intended_activity.");

        final KindOfFunctionalSystem genericSystemKind = (KindOfFunctionalSystem) IriUtils.createNewBaseObject( baseCollection,
                new HqdmObjectBaseProperties(
                        HQDM.KIND_OF_FUNCTIONAL_SYSTEM,
                        baseCollection.PATTERNS_REF_BASE,
                        "KindOfFunctionalSystem__Generic_System",
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                        record_creator));

        genericSystemKind.addStringValue(baseCollection.COMMENT,
                "Kind_of_functional_system_that_is_generic._Note_this_is_subclass_of_class_of_ordinary_functional_object.");
        genericSystemKind.addValue(HQDM.INTENDED_ROLE_BY_CLASS, intendedRoleOfSystem.getId());

        final FunctionalSystem genericSystem = 
                new FunctionalSystemBuilder(new IRI(baseCollection.PATTERNS_BASE, uid()) )
                        .member_Of_Kind(genericSystemKind)
                        .intended_Role_M(intendedRoleOfSystem)
                        .part_Of_Possible_World_M(possibleWorldForGenericExamples)
                        .build();
        IriUtils.addBasePropertiesToThing(baseCollection, genericSystem,
                new HqdmObjectBaseProperties(
                        "An_Actual_System",
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                        record_creator));

        // Create the classes of ordinary functional object that is installed as a system component
        final Role intendedRoleOfOrdinaryFunctionalObject = (Role) IriUtils.createNewBaseObject( baseCollection,new HqdmObjectBaseProperties(
                    HQDM.ROLE,
                    baseCollection.PATTERNS_REF_BASE,
                    "RoleOfOrdinaryFunctionalObject",
                    LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                    record_creator));
        intendedRoleOfOrdinaryFunctionalObject.addStringValue(baseCollection.COMMENT,
                    "Generic_role_of_Ordinary_Functional_Object.");

        final KindOfOrdinaryFunctionalObject genericOrdinaryFunctionalObjectKind = new KindOfOrdinaryFunctionalObjectBuilder( 
                new IRI(baseCollection.PATTERNS_REF_BASE, uid()) )
                .intended_Role_By_Class_M(intendedRoleOfOrdinaryFunctionalObject)
                .build();
        IriUtils.addBasePropertiesToThing(baseCollection, genericOrdinaryFunctionalObjectKind,
                new HqdmObjectBaseProperties(
                        "Kind_Of_Generic_Ordinary_Functional_Object",
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                        record_creator));
        
        final ClassOfStateOfOrdinaryFunctionalObject ordinaryFunctionalObjectClassOfState = new ClassOfStateOfOrdinaryFunctionalObjectBuilder(
                new IRI(baseCollection.PATTERNS_REF_BASE, uid()))
                .build();
        IriUtils.addBasePropertiesToThing(baseCollection,ordinaryFunctionalObjectClassOfState,
                new HqdmObjectBaseProperties(
                        "ClassOfStateOfOrdinaryFunctionalObject__Class_Of_State_Of_Generic_Ordinary_Functional_Object",
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                        record_creator));
        ordinaryFunctionalObjectClassOfState.addStringValue(baseCollection.COMMENT,
                "Class_of_state_of_ordinary_functioal_object_whose_members_are_temporal_part_of_members_of_Kind_Of_Ordinary_Functional_Object");
        ordinaryFunctionalObjectClassOfState.addValue(HQDM.PART__OF_BY_CLASS, genericOrdinaryFunctionalObjectKind.getId());


        // Create the classes of system component
        final Role intendedRoleOfGenericSystemComponent = (Role) IriUtils.createNewBaseObject( baseCollection,new HqdmObjectBaseProperties(
                    HQDM.ROLE,
                    baseCollection.PATTERNS_REF_BASE,
                    "RoleOfGenericSystemComponent",
                    LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                    record_creator));
        intendedRoleOfGenericSystemComponent.addStringValue(baseCollection.COMMENT,
                    "Role_of_Generic_System_Component_when_functioning_in_intended_activities.");

        final KindOfFunctionalSystemComponent genericComponentKind = new KindOfFunctionalSystemComponentBuilder(
                new IRI(baseCollection.PATTERNS_REF_BASE, uid()))
                .part__Of_By_Class(genericSystemKind)
                .build();
        IriUtils.addBasePropertiesToThing(baseCollection,genericComponentKind,
                new HqdmObjectBaseProperties(
                        "KindOfSystemComponent__Kind_of_Generic_Component",
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                        record_creator));
        genericComponentKind.addStringValue(baseCollection.COMMENT,
                "Kind_of_system_component_whose_members_are_temporal_part_of_members_of_Generic_System");

        final ClassOfInstalledFunctionalSystemComponent classOfInstalledGenericOrdinaryFunctionalObject = new ClassOfInstalledFunctionalSystemComponentBuilder(
                new IRI(baseCollection.PATTERNS_REF_BASE, uid()))
                .part__Of_By_Class(genericComponentKind)
                .has_Superclass(ordinaryFunctionalObjectClassOfState)
                .has_Superclass(genericComponentKind)  // Should be class of state of this kind.  Optional
                .build();
        IriUtils.addBasePropertiesToThing(baseCollection,classOfInstalledGenericOrdinaryFunctionalObject,
                new HqdmObjectBaseProperties(
                        "ClassOfInstalledFunctionalSystemComponent__Class_of_Installed_Generic_Ordinary_Functional_Object",
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                        record_creator));
        classOfInstalledGenericOrdinaryFunctionalObject.addStringValue(baseCollection.COMMENT,
                "Class_of_installed_system_component_whose_members_are_temporal_part_of_members_of_Kind_Of_Generic_Ordinary_Functional_Object_AND_Kind_of_Generic_Component");

        // Add the system component classes to the kind of system that they are components of 
        genericSystemKind.addValue(HQDM.HAS_COMPONENT_BY_CLASS, genericComponentKind.getId());

        //////////// CREATE ACTUAL STATES NOW ////////////
        // Events
        final ClassOfPointInTime classOfPointInTimeObject = (ClassOfPointInTime) QueryUtils.findThingInServiceByName(
                mcDatasets,
                "ClassOfPointInTime__ISO8601_DateTime");

        final PointInTime genericSystemStart = IriUtils.createPointInTime( baseCollection,
                "2024-01-01T00:00:00+00:00[UTC]", 
                classOfPointInTimeObject, 
                possibleWorldForGenericExamples, 
                record_creator);

        final PointInTime genericSystemComponentInstalledStart = IriUtils.createPointInTime( baseCollection,
                "2024-01-14T13:31:00+00:00[UTC]", 
                classOfPointInTimeObject, 
                possibleWorldForGenericExamples, 
                record_creator);

        final PointInTime genericSystemComponentUninstalled = IriUtils.createPointInTime( baseCollection,
                "2024-01-24T13:31:00+00:00[UTC]", 
                classOfPointInTimeObject, 
                possibleWorldForGenericExamples, 
                record_creator);

        final PointInTime genericSystemEnd = IriUtils.createPointInTime( baseCollection,
                "2024-03-10T00:00:00+00:00[UTC]", 
                classOfPointInTimeObject, 
                possibleWorldForGenericExamples, 
                record_creator);


        // System states
        genericSystem.addValue(HQDM.BEGINNING, genericSystemStart.getId());
        genericSystem.addValue(HQDM.ENDING, genericSystemEnd.getId());

        // System component states
        final FunctionalSystemComponent genericSystemComponent = new FunctionalSystemComponentBuilder(
                new IRI(baseCollection.PATTERNS_BASE, uid()))
                .part_Of_Possible_World_M(possibleWorldForGenericExamples)
                .member_Of_Kind_M(genericComponentKind)
                .component_Of_M(genericSystem)
                .beginning(genericSystemStart)
                .ending(genericSystemEnd)
                .intended_Role_M(intendedRoleOfGenericSystemComponent)
                .build();
        IriUtils.addBasePropertiesToThing(baseCollection,genericSystemComponent,
                new HqdmObjectBaseProperties(
                        "An_Actual_Generic_System_Component",
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                        record_creator));

        // Actual physical things that are installed, for a period of time, in the genericSystem
        final OrdinaryFunctionalObject genericObject = (OrdinaryFunctionalObject) IriUtils.createNewBaseObject( baseCollection,new HqdmObjectBaseProperties(
                        HQDM.ORDINARY_FUNCTIONAL_OBJECT,
                        baseCollection.PATTERNS_BASE,
                        "An_actual_generic_ordinary_functional_object",
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                        record_creator));
        genericObject.addValue(HQDM.PART_OF_POSSIBLE_WORLD, possibleWorldForGenericExamples.getId());
        genericObject.addValue(HQDM.MEMBER_OF_KIND, genericOrdinaryFunctionalObjectKind.getId());

        final InstalledFunctionalSystemComponent installedGenericObject = new InstalledFunctionalSystemComponentBuilder(
                new IRI(baseCollection.PATTERNS_BASE, uid()))
                .part_Of_Possible_World_M(possibleWorldForGenericExamples)
                .member_Of(classOfInstalledGenericOrdinaryFunctionalObject)
                .beginning(genericSystemComponentInstalledStart)
                .ending(genericSystemComponentUninstalled)
                .temporal_Part_Of(genericSystemComponent)
                .temporal__Part_Of(genericObject)
                .build();

        IriUtils.addBasePropertiesToThing(baseCollection,installedGenericObject,
                new HqdmObjectBaseProperties(
                        "Generic_Object_Installed_As_Generic_System_Component",
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                        record_creator));

        // All system and system component objects created

        // Commit to MC database
        final DbTransformation systemChangeSet = systemComponentService.createDbTransformation(
                List.of(
                        genericSystemKind,
                        genericSystem,
                        intendedRoleOfOrdinaryFunctionalObject,
                        genericOrdinaryFunctionalObjectKind,
                        ordinaryFunctionalObjectClassOfState,
                        intendedRoleOfSystem,
                        intendedRoleOfGenericSystemComponent,
                        genericComponentKind,
                        classOfInstalledGenericOrdinaryFunctionalObject,
                        genericSystemStart,
                        genericSystemComponentInstalledStart,
                        genericSystemComponentUninstalled,
                        genericSystemEnd,
                        genericSystemComponent,
                        genericObject,
                        installedGenericObject
                        ));
        systemComponentService.runInWriteTransaction(systemChangeSet);

        // Create node-edge graphs
         MermaidUtils.writeLRNodeEdgeGraph(
                List.of(
                        genericSystem,
                        genericSystemKind
                        ),
                List.of("record_created", "record_creator", "comment"),
                List.of(genericSystem.getId().toString().split("#")[1]),
                "systemAndKindExample");

        MermaidUtils.writeLRNodeEdgeGraph(
                List.of(
                        genericSystem,
                        genericSystemComponent
                        ),
                List.of("record_created", "record_creator", "comment"),
                List.of(genericSystem.getId().toString().split("#")[1]),
                "systemAndComponentExample");
        
        MermaidUtils.writeLRNodeEdgeGraph(
                List.of(
                        genericSystem,
                        genericSystemComponent,
                        genericSystemKind,
                        genericComponentKind
                        ),
                List.of("record_created", "record_creator", "comment"),
                List.of(genericSystem.getId().toString().split("#")[1]),
                "systemAndComponentAndKindsExample");

        MermaidUtils.writeLRNodeEdgeGraph(
                List.of(
                        genericSystem,
                        genericSystemComponent,
                        genericObject,
                        installedGenericObject
                        ),
                List.of("record_created", "record_creator", "comment"),
                List.of(genericSystem.getId().toString().split("#")[1], genericSystemComponent.getId().toString().split("#")[1]),
                "systemAndInstalledComponentExample");

        MermaidUtils.writeLRNodeEdgeGraph(
                List.of(
                        genericSystem,
                        genericSystemComponent,
                        genericObject,
                        installedGenericObject,
                        genericSystemStart,
                        genericSystemComponentInstalledStart,
                        genericSystemComponentUninstalled,
                        genericSystemEnd
                        ),
                List.of("record_created", "record_creator", "comment"),
                List.of(genericSystem.getId().toString().split("#")[1], genericSystemComponent.getId().toString().split("#")[1]),
                "systemAndInstalledComponentEventsExample");

        MermaidUtils.writeLRNodeEdgeGraph(List.of(
                        genericSystemKind,
                        genericSystem,
                        intendedRoleOfOrdinaryFunctionalObject,
                        genericOrdinaryFunctionalObjectKind,
                        ordinaryFunctionalObjectClassOfState,
                        intendedRoleOfGenericSystemComponent,
                        genericComponentKind,
                        classOfInstalledGenericOrdinaryFunctionalObject,
                        genericSystemStart,
                        genericSystemComponentInstalledStart,
                        genericSystemComponentUninstalled,
                        genericSystemEnd,
                        genericSystemComponent,
                        genericObject,
                        installedGenericObject
                ),
                List.of("record_created", "record_creator", "comment"),
                List.of(genericSystem.getId().toString().split("#")[1], genericSystemComponent.getId().toString().split("#")[1]),
                "systemAndComponentExampleFull");

        try {
            final PrintStream ttl_stream_out = new PrintStream("example-files/systemAndComponentPattern.ttl");

            systemComponentService.exportTtl(ttl_stream_out);
            ttl_stream_out.close();
            System.out.println(
                    "\tData being generated as TTL in example-files/systemAndComponentPattern.ttl.");

        } catch (FileNotFoundException e) {
            System.err.println("systemAndComponentPattern example write: " + e);
        }

        // Create events around this happening        
        final PointInTime genericSystemComponentReInstalledStart = IriUtils.createPointInTime( baseCollection,
                "2024-02-21T09:18:00+00:00[UTC]", 
                classOfPointInTimeObject, 
                possibleWorldForGenericExamples, 
                record_creator);

        // Create installed state
        final InstalledFunctionalSystemComponent reInstalledGenericObject = new InstalledFunctionalSystemComponentBuilder(
                new IRI(baseCollection.PATTERNS_BASE, uid()))
                .part_Of_Possible_World_M(possibleWorldForGenericExamples)
                .member_Of(classOfInstalledGenericOrdinaryFunctionalObject)
                .beginning(genericSystemComponentReInstalledStart)
                .ending(genericSystemEnd)
                .temporal_Part_Of(genericSystemComponent)
                .temporal__Part_Of(genericObject)
                .build();

        IriUtils.addBasePropertiesToThing(baseCollection,reInstalledGenericObject,
                new HqdmObjectBaseProperties(
                        "Generic_Object_Re-Installed_As_Generic_System_Component",
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                        record_creator));

         final DbTransformation systemSecondChangeSet = systemComponentService.createDbTransformation(
                List.of(
                        genericSystemComponentReInstalledStart,
                        reInstalledGenericObject
                        ));
        systemComponentService.runInWriteTransaction(systemSecondChangeSet);

        MermaidUtils.writeLRNodeEdgeGraph(
                List.of(
                        genericSystem,
                        genericSystemComponent,
                        installedGenericObject,
                        reInstalledGenericObject
                        ),
                List.of("record_created", "record_creator", "comment"),
                List.of(genericSystem.getId().toString().split("#")[1]),
                "systemAndInstalledComponentsExample");

        try {
            final PrintStream ttl_stream_out = new PrintStream("example-files/systemInstallAndUinistallPattern.ttl");

            systemComponentService.exportTtl(ttl_stream_out);
            ttl_stream_out.close();
            System.out.println(
                    "\tData being generated as TTL in example-files/systemInstallAndUinistallPattern.ttl.");

        } catch (FileNotFoundException e) {
            System.err.println("systemInstallAndUinistallPattern example write: " + e);
        }
        
        mcDatasets.add(systemComponentService);
    }
}
