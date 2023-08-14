package patterns.hqdm.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.gov.gchq.magmacore.hqdm.model.Thing;
import uk.gov.gchq.magmacore.hqdm.rdf.HqdmObjectFactory;
import uk.gov.gchq.magmacore.hqdm.rdf.iri.HQDM;
import uk.gov.gchq.magmacore.hqdm.rdf.iri.IRI;
import uk.gov.gchq.magmacore.service.MagmaCoreService;
import uk.gov.gchq.magmacore.service.MagmaCoreServiceFactory;

public class FindSupertypes {
    
    /**
     * Populate a new HQDM object using {@link HqdmObjectFactory} create() method to 
     * generate a new Thing and add any supplied base properties.
     *
     * @param mcService {@link HqdmObjectBaseProperties} with properties to use.
     * @return {@link Thing} The generated Thing, or one of its sub-types.
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
        } catch(IOException ioe) {
            System.err.println("hqdmAllAsData io error: " + ioe);
        }

        final List<Thing> inputTypes = new ArrayList<Thing>();
        types.forEach( typeToResove -> {
            Map<String,Thing> queryResults = null;
            if(hqdmService != null){
                queryResults = hqdmService.findByEntityNameInTransaction(List.of(typeToResove.toLowerCase()));
            }
            if(queryResults != null){
                inputTypes.add(queryResults.get(typeToResove));
            }
        });

        typeHierarchy.add(inputTypes);
        List<Thing> currentTypes = List.copyOf(inputTypes);

        boolean atThing = false;
        do{
            final List<Thing> nextTypes = new ArrayList<>();
            currentTypes.forEach( ct -> {
                findImmediateSuperTypes( ct, hqdmService).forEach(st -> {
                    nextTypes.add(st);
                });
            });

            typeHierarchy.add(nextTypes);
            currentTypes = List.copyOf(nextTypes);
            for(Thing ct : currentTypes){
                if(ct.getId().contains("e5ec5d9e-afea-44f7-93c9-699cd5072d90")){
                    atThing = true;
                } else {
                    atThing = false;
                }
            }

        } while( !atThing );

        return typeHierarchy;

    }

    private static List<Thing> findImmediateSuperTypes(final Thing type, MagmaCoreService hqdmService) {

        final List<Thing> superTypes = new ArrayList<Thing>();
        
        Set<Object> stSet;
        Map<Object,Set<Object>> stPredicates = type.getPredicates();
        stSet = stPredicates.get(HQDM.HAS_SUPERTYPE);
        Set<Object> scSet;
        scSet = stPredicates.get(HQDM.HAS_SUPERCLASS);
        if(scSet != null){
            boolean selfReferenceTest = false;
            for (Object obj : scSet) {
                if( obj.toString().equals(type.getPredicates().get(HQDM.HAS_SUPERTYPE).toString()) ){
                    selfReferenceTest = true; // CHECK THAT THIS WORKS
                }
            }
            if(!selfReferenceTest){
                stSet = scSet;
            }   
        }
        if(stSet != null){
            for(Object stObj: stSet){
                Thing stQueryResult = hqdmService.getInTransaction( (IRI) stObj);
                if(stQueryResult != null){
                    superTypes.add(stQueryResult);
                }
            }
        }
    
        return superTypes;

    }
}
