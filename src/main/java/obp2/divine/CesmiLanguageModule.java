package obp2.divine;

import obp2.cesmi.CesmiSimpleTransitionRelation;
import obp2.core.ByteArrayConfiguration;
import obp2.runtime.core.ISimpleTransitionRelationIterator;
import obp2.runtime.core.ITransitionRelation;
import obp2.runtime.core.LanguageModule;
import obp2.runtime.core.empty.NoTransitionRelation;

public class CesmiLanguageModule extends LanguageModule<ByteArrayConfiguration, ByteArrayConfiguration, Void> {
    String cesmiPath;
    boolean asBuchi;
    CesmiSimpleTransitionRelation simpleTransitionRelation;

    public CesmiLanguageModule(
            String cesmiPath,
            boolean asBuchi)
    {
        super(null, null, new CesmiMarshaller());
        this.cesmiPath = cesmiPath;
        this.asBuchi = asBuchi;
        this.isAcceptingPredicate = (c) -> getSimpleByteArrayTransitionRelation().isAccepting(c.state);
    }

    @Override
    public ITransitionRelation<ByteArrayConfiguration, ByteArrayConfiguration, Void> getTransitionRelation() {
        if (this.transitionRelation instanceof NoTransitionRelation) {
            this.transitionRelation = new CesmiTransitionRelation(simpleTransitionRelation());
            this.transitionRelation.setModule(this);
        }
        return this.transitionRelation;
    }

    private CesmiSimpleTransitionRelation simpleTransitionRelation() {
        if (simpleTransitionRelation == null) {
            this.simpleTransitionRelation = new CesmiSimpleTransitionRelation(cesmiPath, asBuchi);
        }
        return simpleTransitionRelation;
    }
    @Override
    public ISimpleTransitionRelationIterator<byte[]> getSimpleByteArrayTransitionRelation() {
        return simpleTransitionRelation();
    }
}
