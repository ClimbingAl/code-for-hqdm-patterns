package patterns.hqdm.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import uk.gov.gchq.magmacore.hqdm.model.Thing;
import uk.gov.gchq.magmacore.hqdm.rdf.iri.HQDM;
import uk.gov.gchq.magmacore.hqdm.rdf.iri.IRI;
import uk.gov.gchq.magmacore.hqdm.rdf.iri.RDFS;
import uk.gov.gchq.magmacore.service.MagmaCoreService;
import uk.gov.gchq.magmacore.service.transformation.DbCreateOperation;
import uk.gov.gchq.magmacore.service.transformation.DbDeleteOperation;

public class QueryUtils {

    /**
     * Find the HQDM object in the available MagmaCore services that matches the supplied name.
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
            final Object value) {
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

    /**
     * Remove duplicate events from a MagmaCore service (e.g. if imported date contains duplicates)
     *
     * @param mcSvc MagmaCore service to dedupe events from.
     */
    /*public static List<Thing> removeDuplicateEventsFromMCService(
            final MagmaCoreService mcSvc) {
        final List<Thing> baseEventList = new ArrayList<>();
        final List<String> eventNameList = new ArrayList<>();
        final Map<String, String> idNameMap = new HashMap<>();
        
        mcSvc.findByPredicateIriAndValueInTransaction(RDFS.RDF_TYPE, HQDM.EVENT).forEach( event -> {
            baseEventList.add(event);
            Set<Object> nameSet;
            nameSet = event.getPredicates().get(HQDM.ENTITY_NAME);
            if(nameSet != null){
                for(Object entityName: nameSet){
                    idNameMap.put( event.getId(), entityName.toString());
                    eventNameList.add(entityName.toString());
                }
            }
        });
 
        // Create de-duped list of event names
        List<String> listOfDuplicatedNames = new ArrayList<>(new HashSet<>(eventNameList));
        for(String eventName: eventNameList){
            if(idNameMap.values().stream().filter(v -> v.equals(eventName)).count() > 1){
                if(!listOfDuplicatedNames.contains(eventName)){
                    listOfDuplicatedNames.add(eventName);
                }
            }
        }
        
        listOfDuplicatedNames.forEach( duplicatedName -> {
            List<String> ids = new ArrayList<String>(idNameMap.entrySet()
                .stream()
                .filter(entry -> Objects.equals(entry.getValue(), duplicatedName))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet()));

            // Get first instance and use it to be the one that survives
            final String survivingId = ids.get(0);
            ids.remove(0);
            final List<Thing> survivingThingFetched = baseEventList.stream().filter(v -> v.getId().equals(survivingId)).toList();

            List<DbCreateOperation> predicatesToAdd = new ArrayList<>();
            List<DbDeleteOperation> predicatesToDelete = new ArrayList<>();

            /*ids.forEach( idDel -> {
                List<Thing> thingToDelete = baseEventList.stream().filter(v -> v.getId().equals(idDel)).toList();
                if( !thingToDelete.isEmpty() ){
                    Thing delThing = thingToDelete.get(0);
                    String id = delThing.getId();
                    delThing.getPredicates()

                    });
                }
            });*/

        /*});

        // Iterate through base event list and 
        // 1. replace predicates to it with predicate to de-duped event
        // 2. remove event from service

        return baseEventList;
    } */

}
