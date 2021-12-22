package obp2.cesmi;

public class CesmiInstance implements AutoCloseable {
    long context;
    public CesmiInstance(String cesmiPath, boolean asBuchi) {
        context = LibCESMI.createContext(cesmiPath, asBuchi);
    }

    public int initial(int next, byte[] target) {
        return LibCESMI.initial(context, next, target);
    }

    public int successor(int next, byte[] source, byte[] target) {
        return  LibCESMI.successor(context, next, source, target);
    }

    public long flags(byte[] source) {
        return LibCESMI.flags(context, source);
    }

    public int configurationWidth() {
        return LibCESMI.configurationWidth(context);
    }

    @Override
    public void close() throws Exception {
        if (context != 0) {
            LibCESMI.freeContext(context);
            context = 0;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (context != 0) {
            LibCESMI.freeContext(context);
            context = 0;
        }
    }
}
