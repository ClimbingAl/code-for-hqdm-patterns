package patterns.hqdm.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import uk.gov.gchq.magmacore.hqdm.model.Thing;
import uk.gov.gchq.magmacore.hqdm.rdf.iri.HQDM;
import uk.gov.gchq.magmacore.hqdm.rdf.iri.IRI;
import uk.gov.gchq.magmacore.hqdm.rdf.iri.RDFS;

public class MermaidUtils {
    
    public static final int NAME_LENGTH_BREAK = 15;
    public static final String MERMAID_START = "``` mermaid\n" ;
    public static final String MERMAID_END = "```\n";
    public static final String GRAPH_TD = "graph TD\n";
    public static final String GRAPH_LR = "graph LR\n";


    public static final String STYLE_START = "  style ";
    public static final String STYLE_END = " stroke-width:4px;\n";

    public static final String ARROW = "-->";
    public static final String ARROW_DASHED = "--->";
    public static final String CIRCLE_START = "((";
    public static final String CIRCLE_END = "))";
    public static final String STADIUM_START = "([";
    public static final String STADIUM_END = "])";
    public static final String ROUNDED_START = "(";
    public static final String ROUNDED_END = ")";

    public static final String SUPERTYPE_OF = "-->|supertype_of|";
    public static final String MEMBER_OF = "-...->|member__of|";
    public static final String ABSTRACT_OBJECT_SUBTYPE_OF_THING = "  thing-->|supertype_of|abstract_object;\n";

    /**
     * Compute the supertype graph and write to file using Mermaid notation
     *
     * @param typeHierarchy List of layers of Thing objects representing the supertypes.
     * @param exampleName File name of type hierarchy graph 
     */
    public static void writeSupertypeGraph(final List<List<Thing>> typeHierarchy, final String exampleName) {

        final List<Thing> nodes = new ArrayList<>();
        typeHierarchy.forEach( layer -> {
            layer.forEach( node -> {
                nodes.add(node);
            });
        });
        
		try {
			final FileWriter fWriter = new FileWriter(new File("example-files/" + exampleName + "Supertypes.mermaid"));
			final BufferedWriter bWriter = new BufferedWriter(fWriter);

            bWriter.write(MERMAID_START);
            bWriter.write(GRAPH_TD);

            // Declare the node - edge connections
            final ListIterator<List<Thing>> hierarchyIterator2 = typeHierarchy.listIterator(typeHierarchy.size());
            boolean topLevelToggle = false;
            boolean abstractObjectToggle = false;
            while(hierarchyIterator2.hasPrevious()) {
                final List<Thing> nextNodes = hierarchyIterator2.previous();

                if(topLevelToggle){
                    for(Thing thing : nextNodes){
                        final String nodeName = thing.getPredicates().get(RDFS.RDF_TYPE).iterator().next().toString().split("#")[1];

                        Set<Object> supertypes = thing.getPredicates().get(HQDM.HAS_SUPERTYPE);
                        if(supertypes!=null){
                            for(Object st : supertypes){
                                String mermaidNodeLine = "  ";

                                IRI stIri = (IRI) st;
                                Thing stThing = null;
                                for(Thing node : nodes){
                                    if( node.getId().equals(stIri.toString()) ){ 
                                        stThing = node; 
                                    }
                                }

                                String stName = stThing.getPredicates().get(RDFS.RDF_TYPE).iterator().next().toString().split("#")[1];
                                if(stName.equals("class")){stName="Class";}
                                String nodeNameMod;
                                if(nodeName.equals("class")){nodeNameMod = "Class";} else { nodeNameMod = nodeName;}
                                if(stName!="" && (!stName.equals(nodeNameMod))){
                                    mermaidNodeLine+=(stName + ROUNDED_START + insertBRifTooLong(stName) + ROUNDED_END + 
                                            SUPERTYPE_OF + 
                                            nodeNameMod + ROUNDED_START + insertBRifTooLong(nodeNameMod) + ROUNDED_END );
                                    bWriter.write(mermaidNodeLine + ";");
                                    bWriter.newLine();
                                }    
                            }
                        }

                        Set<Object> superclasses = thing.getPredicates().get(HQDM.HAS_SUPERCLASS);
                        if(superclasses!=null){
                            abstractObjectToggle = true;
                            for(Object sc : superclasses){
                                String mermaidNodeLine = "  ";

                                IRI scIri = (IRI) sc;
                                Thing scThing = null;
                                for(Thing node : nodes){
                                    if( node.getId().equals(scIri.toString()) ){ 
                                        scThing = node; 
                                    }
                                }
                                if(scThing!=null){ 
                                    String stName = scThing.getPredicates().get(RDFS.RDF_TYPE).iterator().next().toString().split("#")[1];
                                    if(stName.equals("class")){stName="Class";}
                                    String nodeNameMod;
                                    if(nodeName.equals("class")){nodeNameMod = "Class";} else { nodeNameMod = nodeName;}
                                    if(stName!=""  && (!stName.equals(nodeNameMod))){
                                        mermaidNodeLine+=(stName + ROUNDED_START + insertBRifTooLong(stName) + ROUNDED_END + 
                                            SUPERTYPE_OF + 
                                            nodeNameMod + ROUNDED_START + insertBRifTooLong(nodeNameMod) + ROUNDED_END );
                                        bWriter.write(mermaidNodeLine + ";");
                                        bWriter.newLine();
                                    }
                                }
                            }
                        }
                    }  
                } else {
                    topLevelToggle = true;
                }

                if(!hierarchyIterator2.hasPrevious()){
                   for(Thing nn : nextNodes){
                        bWriter.write( STYLE_START + nn.getPredicates().get(RDFS.RDF_TYPE).iterator().next().toString().split("#")[1] + STYLE_END);
                   }
                }
            }

            if(abstractObjectToggle){
                bWriter.write(ABSTRACT_OBJECT_SUBTYPE_OF_THING);
            }

            bWriter.write(MERMAID_END);
			bWriter.close();
			System.out.println("Mermaid supertype file writing done for example: " + exampleName);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

        /* EXAMPLE TYPE HIERARCHY

        ``` mermaid
        graph TD
        id1[thing];
        style id1 stroke-width:4px;
        id2[abstract_object] & id3[spatio_temporal <br> _extent];
        id1-->|supertype_of|id2;
        id1-->|supertype_of|id3;
        id4[class];
        id2-->|supertype_of|id4;
        id1-...->|member__of|id4;
        ```

        */
    }

 /**
     * Compute the left-to-right node-edge graph and write to file using Mermaid notation
     *
     * @param nodeEdgeGroups List of layers of Thing objects representing the node groups.
     */
    public static void writeLRNodeEdgeGraph(final List<Thing> nodeEdgeGroups, 
        final List<String> predicatesToExclude, 
        final List<String> namesInBold,
        final String exampleName) {

        try {
			final FileWriter fWriter = new FileWriter(new File("example-files/" + exampleName + "NodeEdgeGraph.mermaid"));
			final BufferedWriter bWriter = new BufferedWriter(fWriter);

            bWriter.write(MERMAID_START);
            bWriter.write(GRAPH_LR);

            // Declare the node - edge connections
            final ListIterator<Thing> nodeEdgeThingIterator = nodeEdgeGroups.listIterator();
            while(nodeEdgeThingIterator.hasNext()) {
                final Thing thing = nodeEdgeThingIterator.next();

                //for(Thing thing : nextNodes){
                    {
                    final Map<Object,Set<Object>> nodeEdgesMap = thing.getPredicates();

                    nodeEdgesMap.forEach((predicate, objects) -> {
                        final String predicateName = predicate.toString().split("#")[1];
                        if(!predicatesToExclude.contains(predicateName)){
                            final String thingNameShort = thing.getId().split("#")[1];
                            final List<String> thingNamePrefix =  Arrays. asList(thing.getId().split("#")[0].split("/"));
                            final List<String> nameArray = Arrays.asList( thingNameShort.split("(?<=\\G.{18})") );
                            String thingNamePrint = thingNamePrefix.get(thingNamePrefix.size()-1);
                            for(String str : nameArray){
                                thingNamePrint+=" <br> " + str;
                            }
                            if(namesInBold.contains(thingNameShort)){
                                thingNamePrint = "<b> " + thingNamePrint;
                            }
                            final String thingName = (thingNameShort + CIRCLE_START + "\"" + thingNamePrint + "\"" + CIRCLE_END);

                            objects.forEach( obj -> {
                                String mermaidNodeLine = "  ";
                                String objName;

                                if( obj.toString().contains("#") ){
                                    final String objNameShort = obj.toString().split("#")[1];
                                    final String[] objNamePrefixParts = obj.toString().split("#")[0].split("/");
                                    final String objNamePrefix = objNamePrefixParts[ objNamePrefixParts.length - 1];
                                    final List<String> nameObjArray = Arrays.asList( objNameShort.split("(?<=\\G.{18})") );
                                    String objNamePrint = objNamePrefix;
                                    for(String str : nameObjArray){
                                        objNamePrint+=" <br> " + str;
                                    }
                                    if(namesInBold.contains(objNameShort)){
                                        objNamePrint = "<b> " + objNamePrint;
                                    }
                                    if(objNamePrefix.contains("hqdm")){
                                        objName = objNameShort + (STADIUM_START + "\"" + objNamePrint + "\"" + STADIUM_END);
                                    } else {
                                        objName = objNameShort + (CIRCLE_START + "\"" + objNamePrint + "\"" + CIRCLE_END);
                                    }
                                } else {
                                    String addB = "";
                                    String tmpName = obj.toString();
                                    if(tmpName.contains(" ")){
                                        tmpName = tmpName.replace(" ", "_");
                                    }
                                    if(namesInBold.contains(obj.toString())){
                                        addB = "<b> ";
                                    }
                                    objName = tmpName + "[\"" + addB + insertBRifTooLong(tmpName) + "\"]";
                                }

                                String predicatePrefix = "";
                                if(namesInBold.contains(predicateName))
                                {
                                    predicatePrefix = "<b> ";
                                }

                                mermaidNodeLine += ( thingName + "--->|" + "\"" + predicatePrefix + predicateName + "\"" + "|" + objName );

                                try {
                                    bWriter.write( mermaidNodeLine + ";" );
                                    bWriter.newLine();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            });
                        }

                    });                               
                }
            }

            bWriter.write( MERMAID_END );
			bWriter.close();
			System.out.println( "Mermaid nodeEdge file writing done for example: " + exampleName );
		}
		catch (Exception e) {
			e.printStackTrace();
		}

        /* EXAMPLE LEFT-RIGHT CIRCULAR-NODE <-> EDGE

        ``` mermaid
        graph LR
        id1((patterns: <br> 7a14739a-7bdd-49a0- <br> a935-3ef41f0cf955)) --->|patterns-rdl:record_created| p1(2023-08-01T10:40:51.880960800Z);
        id1 --->|patterns-rdl:record_creator| p2(HqdmPatternProject_User1);
        id1 --->|hqdm:EntityName| p3(Example_Thing_As_Instance_Of_ <br> TopLevelHQDM_EntityType_THING);
        ```
        */
    }

    private static String insertBRifTooLong(final String inputString){
        if(inputString.length()>NAME_LENGTH_BREAK+1){
            int index = inputString.indexOf("_", NAME_LENGTH_BREAK);
            if(index != -1){
                return (inputString.substring(0, index) + " <br> " + inputString.substring(index) );
            } else if(inputString.length() > NAME_LENGTH_BREAK + 5){
                return (inputString.substring(0, NAME_LENGTH_BREAK) + " <br> " + inputString.substring(NAME_LENGTH_BREAK));
            } else {
                return inputString;
            }

        } else {
            return inputString;
        }
    }

}