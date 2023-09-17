package patterns.hqdm.utils;

import java.util.ArrayList;
import java.util.List;

import uk.gov.gchq.magmacore.hqdm.model.Thing;
import uk.gov.gchq.magmacore.hqdm.rdf.iri.HQDM;
import uk.gov.gchq.magmacore.hqdm.rdf.iri.IRI;
import uk.gov.gchq.magmacore.service.MagmaCoreService;

public class QueryUtils {
    
    /**
     * Find the HQDM object in the available MagmaCore services that matches the supplied name
     *
     * @param mcDatasets List of MagmaCore services.
     * @param thingName Name of thing to search for.
     */
    public static Thing findThingInServiceByName(
            final List<MagmaCoreService> mcDatasets, 
            final String thingName) {
        for(MagmaCoreService svc : mcDatasets){
            List<Thing> results = svc.findByPredicateIriInTransaction(HQDM.ENTITY_NAME);
           for(Thing res: results){
                if(res.getPredicates().get(HQDM.ENTITY_NAME).iterator().next().toString().equals(thingName)){
                    return res;
                }
            }
        }
        return null;
    }

    /**
     * Find the HQDM object in the available MagmaCore services that matches the supplied predicate and value
     *
     * @param mcDatasets List of MagmaCore services.
     * @param predicate HQDM predicate.
     * @param value Name of thing to search for.
     */
    public static List<Thing> findThingsInServiceByPredicateAndValue(
            final List<MagmaCoreService> mcDatasets, 
            final IRI predicate, 
            final String value) {
        List<Thing> results = new ArrayList<>();
        mcDatasets.forEach(svc -> 
            results.addAll(svc.findByPredicateIriAndValueInTransaction(predicate, value)));            
        return results;
    }

    /**
     * Find the HQDM object in the available MagmaCore services that matches the supplied name
     *
     * @param thingList List of things to search in.
     * @param thingName Name of thing to search for.
     */
    public static Thing findThingByNameInList( final List<Thing> thingList, final String thingName) {
        for(Thing th: thingList){
            if( th.hasThisStringValue(HQDM.ENTITY_NAME, thingName) ){
                return th;
            }
        }          
        return null;
    }


}
