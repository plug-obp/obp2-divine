package obp2.divine;

import obp2.cesmi.CesmiInstance;
import obp2.cesmi.CesmiSimpleTransitionRelation;
import obp2.core.ByteArrayConfiguration;
import obp2.core.IFiredTransition;
import obp2.core.defaults.FiredTransition;
import obp2.runtime.core.IConcurrentTransitionRelation;
import obp2.runtime.core.ITransitionRelation;
import obp2.runtime.core.defaults.DefaultLanguageService;

import java.util.*;

public class CesmiTransitionRelation extends DefaultLanguageService<ByteArrayConfiguration, ByteArrayConfiguration, Void>
        implements ITransitionRelation<ByteArrayConfiguration, ByteArrayConfiguration, Void>,
        IConcurrentTransitionRelation<CesmiTransitionRelation, ByteArrayConfiguration, ByteArrayConfiguration, Void> {

    CesmiSimpleTransitionRelation simpleTransitionRelation;

    public CesmiTransitionRelation(CesmiSimpleTransitionRelation simpleTransitionRelation) {
        this.simpleTransitionRelation = simpleTransitionRelation;
    }

    CesmiInstance binding() {
        return simpleTransitionRelation.getInstance();
    }

    @Override
    public CesmiTransitionRelation createCopy() {
        return new CesmiTransitionRelation(
                new CesmiSimpleTransitionRelation(
                        simpleTransitionRelation.cesmiPath,
                        simpleTransitionRelation.asBuchi));
    }

    @Override
    public Set<ByteArrayConfiguration> initialConfigurations() {
        Set<ByteArrayConfiguration> configurations = new HashSet<>();

        Iterator<byte[]> iterator = simpleTransitionRelation.initialIterator();
        while (iterator.hasNext()) {
            configurations.add(new ByteArrayConfiguration(iterator.next(), simpleTransitionRelation::isAccepting));
        }
        return configurations;
    }



    @Override
    public Collection<ByteArrayConfiguration> fireableTransitionsFrom(ByteArrayConfiguration source) {
        Collection<ByteArrayConfiguration> targets = new ArrayList<>();
        Iterator<byte[]> iterator = simpleTransitionRelation.nextIterator(source.state);
        while (iterator.hasNext()) {
            targets.add(new ByteArrayConfiguration(iterator.next(), simpleTransitionRelation::isAccepting));
        }
        return targets;
    }

    @Override
    public IFiredTransition<ByteArrayConfiguration, ByteArrayConfiguration, Void> fireOneTransition(ByteArrayConfiguration source, ByteArrayConfiguration action) {
        return new FiredTransition<>(source, action, action);
    }
}
