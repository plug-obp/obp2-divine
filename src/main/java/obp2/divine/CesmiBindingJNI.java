package obp2.divine;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class CesmiBindingJNI {

    static {
        // Extracts ltl3ba executable from resources to be able to run it.
        String platform = System.getProperty("os.name").toLowerCase();
        String resource = "/obp2/divine/lib/libcesmi.dylib";
        String suffix = ".dylib";

        Path libraryPath = null;

        // Creates a tmp file on the computer
        try (InputStream inStream = CesmiBindingJNI.class.getResourceAsStream(resource)){
            libraryPath = Files.createTempFile("libcesmi", suffix);

            try (OutputStream outStream = Files.newOutputStream(libraryPath)) {
                byte[] buffer = new byte[1024*10];
                int len = inStream.read(buffer);
                while (len != -1) {
                    outStream.write(buffer, 0, len);
                    len = inStream.read(buffer);
                }
            }

            libraryPath.toFile().setExecutable(true, false);
            System.load(libraryPath.toString());
        } catch (IOException e) {
            System.err.println("Can't create temp file to extract libcesmi");
        }
    }

    private native int configurationWidth();
    private native long createContext(boolean has_ltl);
    private native void freeContext(long handle);
    private native boolean    initial(long handle, byte[] target, int target_width);
    private native boolean    next(long handle, byte[] source, int source_width, byte[] target, int target_width);
    private native boolean isAccepting(long handle, byte[] source, int source_width);

    public int configuration_width = 0;
    long handle = 0;
    public boolean omegaAcceptance;
    public CesmiBindingJNI(boolean omegaAcceptance) {
        this.omegaAcceptance = omegaAcceptance;
        configuration_width = configurationWidth();
        System.out.println("configuration width: " + configuration_width);
        handle = createContext(omegaAcceptance);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (handle != 0) {
            freeContext(handle);
        }
    }

    public boolean initial(byte[] target) {
        return initial(handle, target, configuration_width);
    }

    public boolean next(byte[] source, byte[] target) {
        return  next(handle, source, configuration_width, target, configuration_width);
    }

    public boolean next(byte[] source, byte[] target, int target_width) {
        return  next(handle, source, configuration_width, target, target_width);
    }

    public boolean isAccepting(byte[] source) {
        return isAccepting(handle, source, configuration_width);
    }


    public static void main(String[] args) {
        CesmiBindingJNI o = new CesmiBindingJNI(false);
        byte[] target = new byte[o.configuration_width];
        Arrays.fill(target, (byte) 1);
        boolean has_next = o.initial(target);

        System.out.println("has_next: " + has_next + " target: " + Arrays.toString(target));
        System.out.println("isAccepting: " + o.isAccepting(target));

        for (int i=0; i<10; i++) {
            byte[] source = target.clone();
            has_next = o.next(source, target);

            System.out.println("has_next: " + has_next + " target: " + Arrays.toString(target));
            System.out.println("isAccepting: " + o.isAccepting(target));
        }
    }
}
