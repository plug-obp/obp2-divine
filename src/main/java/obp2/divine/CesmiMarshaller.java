package obp2.divine;

import obp2.core.ByteArrayConfiguration;
import obp2.runtime.core.IMarshaller;
import obp2.runtime.core.defaults.DefaultLanguageService;

public class CesmiMarshaller
        extends DefaultLanguageService<ByteArrayConfiguration, ByteArrayConfiguration, Void>
        implements IMarshaller<ByteArrayConfiguration, ByteArrayConfiguration, Void> {

    public CesmiMarshaller() {
       super();
    }

    @Override
    public byte[] serializeConfiguration(ByteArrayConfiguration configuration) {
        return configuration.state;
    }

    @Override
    public byte[] serializeFireable(ByteArrayConfiguration fireable) {
        return fireable.state;
    }

    @Override
    public byte[] serializeOutput(Void output) {
        return null;
    }

    @Override
    public ByteArrayConfiguration deserializeConfiguration(byte[] buffer) {
        return new ByteArrayConfiguration(buffer);
    }

    @Override
    public ByteArrayConfiguration deserializeFireable(byte[] buffer) {
        return new ByteArrayConfiguration(buffer);
    }

    @Override
    public Void deserializeOutput(byte[] buffer) {
        return null;
    }
}
