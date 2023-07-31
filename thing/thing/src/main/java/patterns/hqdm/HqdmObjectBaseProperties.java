package patterns.hqdm;

import uk.gov.gchq.magmacore.hqdm.rdf.iri.HqdmIri;
import uk.gov.gchq.magmacore.hqdm.rdf.iri.IriBase;

public class HqdmObjectBaseProperties {
    final public HqdmIri hqdmType;
    final public IriBase iriBase;
    final public String entityName;
    final public String recordCreated;
    final public String recordCreator;
    final public String recordCopyCreated;
    final public String recordLogicallyDeleted;
    final public String whyDeleted;

    HqdmObjectBaseProperties(
        HqdmIri hqdmType,
        IriBase iriBase,
        String entityName,
        String recordCreated,
        String recordCreator,
        String recordCopyCreated,
        String recordLogicallyDeleted,
        String whyDeleted
    ){
        this.hqdmType = hqdmType;
        this.iriBase = iriBase;
        this.entityName = entityName;
        this.recordCreated = recordCreated;
        this.recordCreator = recordCreator;
        this.recordCopyCreated = recordCopyCreated;
        this.recordLogicallyDeleted = recordLogicallyDeleted;
        this.whyDeleted = whyDeleted;
    }

    public HqdmIri getType(){
        return hqdmType;
    }

    public IriBase getIriBase(){
        return iriBase;
    }

    public String getName(){
        return entityName;
    }

    public String getCreated(){
        return recordCreated;
    }

    public String getCreator(){
        return recordCreator;
    }

    public String getCopyCreated(){
        return recordCopyCreated;
    }

    public String getLogicallyDeleted(){
        return recordLogicallyDeleted;
    }

    public String getWhyDeleted(){
        return whyDeleted;
    }

}
