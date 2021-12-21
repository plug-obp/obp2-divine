package obp2.divine;

import obp2.core.ByteArrayConfiguration;
import obp2.runtime.core.ISimpleTransitionRelationIterator;
import obp2.runtime.core.LanguageModule;

import java.util.function.Predicate;

public class CesmiLanguageModule extends LanguageModule<ByteArrayConfiguration, ByteArrayConfiguration, Void> {
    boolean hasLTL;


    public CesmiLanguageModule(boolean hasLTL, CesmiTransitionRelation transitionRelation, Predicate<ByteArrayConfiguration> isAcceptingPredicate) {
        super(transitionRelation, isAcceptingPredicate);
        this.hasLTL = hasLTL;
    }

    @Override
    public ISimpleTransitionRelationIterator<byte[]> getSimpleByteArrayTransitionRelation() {
        return new CesmiSimpleTransitionRelation(hasLTL);
    }
}
