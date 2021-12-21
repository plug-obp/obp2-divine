package obp2.divine;

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

    public CesmiTransitionRelation(boolean omegaAcceptance) {
        this.simpleTransitionRelation = new CesmiSimpleTransitionRelation(omegaAcceptance);
    }

    CesmiBindingJNI binding() {
        return simpleTransitionRelation.getBinding();
    }

    @Override
    public CesmiTransitionRelation createCopy() {
        return new CesmiTransitionRelation(binding().omegaAcceptance);
    }

    @Override
    public Set<ByteArrayConfiguration> initialConfigurations() {
        Set<ByteArrayConfiguration> configurations = new HashSet<>();

        Iterator<byte[]> iterator = simpleTransitionRelation.initialIterator();
        while (iterator.hasNext()) {
            configurations.add(new ByteArrayConfiguration(iterator.next().clone(), binding()::isAccepting));
        }
        return configurations;
    }



    @Override
    public Collection<ByteArrayConfiguration> fireableTransitionsFrom(ByteArrayConfiguration source) {
        Collection<ByteArrayConfiguration> targets = new ArrayList<>();
        Iterator<byte[]> iterator = simpleTransitionRelation.nextIterator(source.state);
        while (iterator.hasNext()) {
            targets.add(new ByteArrayConfiguration(iterator.next().clone(), binding()::isAccepting));
        }
        return targets;
    }

    @Override
    public IFiredTransition<ByteArrayConfiguration, ByteArrayConfiguration, Void> fireOneTransition(ByteArrayConfiguration source, ByteArrayConfiguration action) {
        return new FiredTransition<>(source, action, action);
    }
}
