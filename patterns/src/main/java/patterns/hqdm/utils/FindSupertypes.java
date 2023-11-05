package patterns.hqdm.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.gov.gchq.magmacore.hqdm.model.Thing;
import uk.gov.gchq.magmacore.hqdm.rdf.iri.HQDM;
import uk.gov.gchq.magmacore.hqdm.rdf.iri.IRI;
import uk.gov.gchq.magmacore.hqdm.rdf.iri.RDFS;
import uk.gov.gchq.magmacore.service.MagmaCoreService;
import uk.gov.gchq.magmacore.service.MagmaCoreServiceFactory;

public class FindSupertypes {

    /**
     * Compute the type hierarchy using hqdmAllAsData
     *
     * @param types listing the names of the types to compute supertypes of.
     * @return typeHierarchy The type hierarchy in layers.
     */
    public static List<List<Thing>> findSuperTypes(final List<String> types) {

        final MagmaCoreService hqdmService = MagmaCoreServiceFactory.createWithJenaDatabase();
        hqdmService.register(PatternsUtils.PREFIX_LIST);

        final List<List<Thing>> typeHierarchy = new ArrayList<>();

        try {
            final FileInputStream hqdmInputStream = new FileInputStream("source-files/hqdmAllAsData.ttl");
            hqdmService.importTtl(hqdmInputStream);
            hqdmInputStream.close();

        } catch (FileNotFoundException e) {
            System.err.println("hqdmAllAsData not found: " + e);
        } catch (IOException ioe) {
            System.err.println("hqdmAllAsData io error: " + ioe);
        }

        final List<Thing> inputTypes = new ArrayList<Thing>();
        types.forEach(typeToResove -> {
            List<Thing> queryResults = null;
            if (hqdmService != null) {
                // queryResults =
                // hqdmService.findByEntityNameInTransaction(List.of(typeToResove.toLowerCase()));
                queryResults = hqdmService.findByPredicateIriAndValueInTransaction(RDFS.RDF_TYPE,
                        new IRI(HQDM.HQDM, typeToResove.toLowerCase()));
            }
            if (queryResults != null) {
                if (!inputTypes.contains(queryResults.get(0))) {
                    inputTypes.add(queryResults.get(0));
                }
            }
        });

        typeHierarchy.add(inputTypes);
        List<Thing> currentTypes = List.copyOf(inputTypes);

        boolean atThing = false;
        do {
            final List<Thing> nextTypes = new ArrayList<>();
            currentTypes.forEach(ct -> {
                findImmediateSuperTypes(ct, hqdmService).forEach(st -> {
                    boolean typeAlreadyInList = false;
                    for (List<Thing> thl : typeHierarchy) {
                        for (Thing th : thl) {
                            if (th.getId().equals(st.getId())) {
                                typeAlreadyInList = true;
                            }
                        }
                    }
                    ;
                    if (!typeAlreadyInList) {
                        boolean alreadyInNext = false;
                        for (Thing nxt : nextTypes) {
                            if (nxt.getId().equals(st.getId())) {
                                alreadyInNext = true;
                            }
                        }
                        if (!alreadyInNext) {
                            nextTypes.add(st);
                        }

                    }
                });
            });

            if (nextTypes.size() > 0) {
                typeHierarchy.add(nextTypes);
                currentTypes = List.copyOf(nextTypes);
                for (Thing ct : currentTypes) {
                    if (ct.getId().contains("e5ec5d9e-afea-44f7-93c9-699cd5072d90")) {
                        atThing = true;
                    } else {
                        atThing = false;
                    }
                }
            } else {
                atThing = true;
            }

        } while (!atThing);

        return typeHierarchy;

    }

    private static List<Thing> findImmediateSuperTypes(final Thing type, MagmaCoreService hqdmService) {

        final List<Thing> superTypes = new ArrayList<Thing>();

        Set<Object> stSet;
        Map<Object, Set<Object>> stPredicates = type.getPredicates();
        stSet = stPredicates.get(HQDM.HAS_SUPERTYPE);
        Set<Object> scSet;
        scSet = stPredicates.get(HQDM.HAS_SUPERCLASS);
        if (scSet != null) {
            if (stSet == null) {
                stSet = new HashSet<Object>();
            }
            for (Object obj : scSet) {
                boolean selfReferenceTest = false;

                if (obj != null) {
                    if (stSet != null) {
                        for (Object typ : stSet) {
                            if (obj.toString().equals(typ.toString())) {
                                selfReferenceTest = true;
                            }
                        }
                    }
                }
                if (!selfReferenceTest) {
                    stSet.add(obj);
                }
            }

        }
        if (stSet != null) {
            for (Object stObj : stSet) {
                Thing stQueryResult = hqdmService.getInTransaction((IRI) stObj);
                if (stQueryResult != null) {
                    superTypes.add(stQueryResult);
                }
            }
        }

        return superTypes;

    }
}
