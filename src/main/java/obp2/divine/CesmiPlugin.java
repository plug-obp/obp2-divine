package obp2.divine;

import obp2.core.ByteArrayConfiguration;
import obp2.runtime.core.ILanguageModule;
import obp2.runtime.core.ILanguagePlugin;
import obp2.runtime.core.LanguageModule;

import java.io.File;
import java.net.URI;
import java.util.function.Function;

public class CesmiPlugin implements ILanguagePlugin<URI, ByteArrayConfiguration, ByteArrayConfiguration, Void> {
    @Override
    public String getName() {
        return "CESMI";
    }

    @Override
    public String[] getExtensions() {
        return new String[]{".cesmi"};
    }

    @Override
    public Function<URI, ILanguageModule<ByteArrayConfiguration, ByteArrayConfiguration, Void>> languageModuleFunction() {
        return this::getModule;
    }
    public ILanguageModule<ByteArrayConfiguration, ByteArrayConfiguration, Void> getModule(URI explicitProgramURI) {
        return getModule(new File(explicitProgramURI));
    }

    public ILanguageModule<ByteArrayConfiguration, ByteArrayConfiguration, Void> getModule(File programFile) {
        boolean hasLTL = true;
        CesmiTransitionRelation transitionRelation = new CesmiTransitionRelation(hasLTL);

        return new LanguageModule(
                transitionRelation,
                (c) -> transitionRelation.binding().isAccepting(((ByteArrayConfiguration)c).state),
                new CesmiMarshaller()
        );

//        return new CesmiLanguageModule(
//                hasLTL,
//                transitionRelation,
//                (c) -> transitionRelation.binding().isAccepting(c.state),
//                new CesmiMarshaller()
//        );
    }

    @Override
    public boolean isOmegaRegular() {
        return true;
    }
}
