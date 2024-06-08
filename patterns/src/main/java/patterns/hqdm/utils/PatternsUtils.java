package patterns.hqdm.utils;

import static uk.gov.gchq.magmacore.hqdm.rdf.iri.RDFS.RDF_TYPE;
import static uk.gov.gchq.magmacore.util.UID.uid;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import uk.gov.gchq.magmacore.hqdm.model.ClassOfPointInTime;
import uk.gov.gchq.magmacore.hqdm.model.PointInTime;
import uk.gov.gchq.magmacore.hqdm.model.PossibleWorld;
import uk.gov.gchq.magmacore.hqdm.model.Thing;
import uk.gov.gchq.magmacore.hqdm.rdf.HqdmObjectFactory;
import uk.gov.gchq.magmacore.hqdm.rdf.iri.HQDM;
import uk.gov.gchq.magmacore.hqdm.rdf.iri.HqdmIri;
import uk.gov.gchq.magmacore.hqdm.rdf.iri.IRI;
import uk.gov.gchq.magmacore.hqdm.rdf.iri.IriBase;
import uk.gov.gchq.magmacore.hqdm.rdfbuilders.PointInTimeBuilder;

public class PatternsUtils {

    /** IriBase for Reference Data Library. */
    public static final IriBase PATTERNS_REF_BASE = new IriBase("patterns-rdl",
            "https://github.com/ClimbingAl/code-for-hqdm-patterns/patterns-rdl#");

    /** IriBase for Patterns Examples data. */
    public static final IriBase PATTERNS_BASE = new IriBase("patterns",
            "https://github.com/ClimbingAl/code-for-hqdm-patterns/patterns#");

    /** IriBase for Activity Editor data. */
    public static final IriBase ACTIVITY_EDITOR_BASE = new IriBase("diag",
            "https://apollo-protocol.github.io/ns/2023/diagram-editor/diagram#");

    /**
     * A system-agreed date and time record on which this record was first created.
     */
    public static final HqdmIri CREATED = new HqdmIri(PATTERNS_REF_BASE, "record_created");

    /**
     * A system-agreed string for the person, organisation or system that first
     * created this record.
     */
    public static final HqdmIri CREATOR = new HqdmIri(PATTERNS_REF_BASE, "record_creator");

    /** A system-agreed date and time on which this record was logically deleted. */
    public static final HqdmIri DELETED = new HqdmIri(PATTERNS_REF_BASE, "record_logically_deleted");

    /** A system-agreed string that states why the record was logically deleted. */
    public static final HqdmIri WHY_DELETED = new HqdmIri(PATTERNS_REF_BASE, "why_deleted");

    /**
     * A human-interpretable date and time on which this record was first created.
     */
    public static final HqdmIri COPIED = new HqdmIri(PATTERNS_REF_BASE, "record_copy_created");

    /** A human-interpretable concise comment or description. */
    public static final HqdmIri COMMENT = new HqdmIri(PATTERNS_REF_BASE, "comment");

    // Create map of prefixes
    public static final List<IriBase> PREFIX_LIST = List.of(
            PatternsUtils.PATTERNS_BASE,
            PatternsUtils.PATTERNS_REF_BASE,
            PatternsUtils.ACTIVITY_EDITOR_BASE,
            HQDM.HQDM);

    /**
     * Populate a new HQDM object using {@link HqdmObjectFactory} create() method to
     * generate a new Thing and add any supplied base properties.
     *
     * @param hqdmObjBaseProperties {@link HqdmObjectBaseProperties} with properties
     *                              to use.
     * @return {@link Thing} The generated Thing, or one of its sub-types.
     */
    public static Thing createNewBaseObject(final HqdmObjectBaseProperties hqdmObjBaseProperties) {

        final var objId = new IRI(hqdmObjBaseProperties.getIriBase(), uid());
        final var baseObject = HqdmObjectFactory.create(hqdmObjBaseProperties.getType(), objId);

        if (hqdmObjBaseProperties.getType() != null) {
            baseObject.addValue(RDF_TYPE, hqdmObjBaseProperties.getType());
        }

        if (hqdmObjBaseProperties.getName() != "") {
            baseObject.addStringValue(HQDM.ENTITY_NAME, hqdmObjBaseProperties.getName());
        }

        if (hqdmObjBaseProperties.getCreated() != "") {
            baseObject.addStringValue(CREATED, hqdmObjBaseProperties.getCreated());
        }

        if (hqdmObjBaseProperties.getCreator() != "") {
            baseObject.addStringValue(CREATOR, hqdmObjBaseProperties.getCreator());
        }

        if (hqdmObjBaseProperties.getLogicallyDeleted() != "") {
            baseObject.addStringValue(DELETED, hqdmObjBaseProperties.getLogicallyDeleted());
        }

        if (hqdmObjBaseProperties.getWhyDeleted() != "") {
            baseObject.addStringValue(WHY_DELETED, hqdmObjBaseProperties.getWhyDeleted());
        }

        if (hqdmObjBaseProperties.getCopyCreated() != "") {
            baseObject.addStringValue(COPIED, hqdmObjBaseProperties.getCopyCreated());
        }

        return baseObject;
    }

    /**
     * Populate an existing HQDM object with any supplied base properties.
     *
     * @param thing                 {@link Thing} to add properties to.
     * @param hqdmObjBaseProperties {@link HqdmObjectBaseProperties} with properties
     *                              to use.
     */
    public static void addBasePropertiesToThing(final Thing thing,
            final HqdmObjectBaseProperties hqdmObjBaseProperties) {

        if (hqdmObjBaseProperties.getType() != null) {
            thing.addValue(RDF_TYPE, hqdmObjBaseProperties.getType());
        }

        if (hqdmObjBaseProperties.getName() != "") {
            thing.addStringValue(HQDM.ENTITY_NAME, hqdmObjBaseProperties.getName());
        }

        if (hqdmObjBaseProperties.getCreated() != "") {
            thing.addStringValue(CREATED, hqdmObjBaseProperties.getCreated());
        }

        if (hqdmObjBaseProperties.getCreator() != "") {
            thing.addStringValue(CREATOR, hqdmObjBaseProperties.getCreator());
        }

        if (hqdmObjBaseProperties.getLogicallyDeleted() != "") {
            thing.addStringValue(DELETED, hqdmObjBaseProperties.getLogicallyDeleted());
        }

        if (hqdmObjBaseProperties.getWhyDeleted() != "") {
            thing.addStringValue(WHY_DELETED, hqdmObjBaseProperties.getWhyDeleted());
        }

        if (hqdmObjBaseProperties.getCopyCreated() != "") {
            thing.addStringValue(COPIED, hqdmObjBaseProperties.getCopyCreated());
        }

    }


     /**
     * Create point in time event based on supplied properties.
     *
     * @param dateTime              XML dateTime to add properties to.
     * @param classOfPointInTime    {@link ClassOfPointInTime}.
     * @param possibleWorld         {@link PossibleWorld}.
     * @param recordCreator         Name of user creating record.
     */
    public static PointInTime createPointInTime(final String dateTime,
        final ClassOfPointInTime classOfPointInTime,
        final PossibleWorld possibleWorld,
        final String recordCreator
        ) {

            final ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateTime);

            final PointInTime pointInTime = new PointInTimeBuilder(
                new IRI(PatternsUtils.PATTERNS_BASE, uid()))
                .part_Of_Possible_World_M(possibleWorld)
                .member_Of(classOfPointInTime)
                .build();

            PatternsUtils.addBasePropertiesToThing(pointInTime,
                new HqdmObjectBaseProperties(
                        LocalDateTime.ofInstant(zonedDateTime.toInstant(), ZoneOffset.UTC).toString(),
                        LocalDateTime.now().toInstant(ZoneOffset.UTC).toString(),
                        recordCreator));
            
            return pointInTime;
    }
}
