package obp2.cesmi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class LibCESMI {
    static {
        // Extracts ltl3ba executable from resources to be able to run it.
        String platform = System.getProperty("os.name").toLowerCase();
        String resource = "/obp2/cesmi/lib/libcesmi.dylib";
        String suffix = ".dylib";

        Path libraryPath = null;

        // Creates a tmp file on the computer
        try (InputStream inStream = LibCESMI.class.getResourceAsStream(resource)){
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

    public static native long     createContext       (String libPath, boolean asBuchi);
    public static native void     freeContext         (long context);
    public static native int      configurationWidth  (long context);
    public static native int      initial             (long context, int next, byte[] target);
    public static native int      successor           (long context, int next, byte[] source, byte[] target);
    public static native long     flags               (long context, byte[] node);

    public static void main(String[] args) {
        long context = LibCESMI.createContext(
                "/Users/ciprian/Playfield/repositories/plugTEAM-v1.0.0/obp2-divine-bridge/src/main/c/build/libbakery.1.cesmi"
                , true);

        byte[] target = new byte[LibCESMI.configurationWidth(context)];
        Arrays.fill(target, (byte) 1);


        int next = LibCESMI.initial(context, 1, target);

        System.out.println("next: " + next + " target: " + Arrays.toString(target));
        System.out.println("flags: " + Long.toBinaryString(LibCESMI.flags(context, target)));

        next = LibCESMI.initial(context, next, target);

        System.out.println("next: " + next + " target: " + Arrays.toString(target));
        System.out.println("flags: " + Long.toBinaryString(LibCESMI.flags(context, target)));

        byte[] source = target.clone();
        System.out.println("source: " + Arrays.toString(source));
        next = LibCESMI.successor(context, 1, source, target);

        System.out.println("\t\tnext: " + next + " target: " + Arrays.toString(target));
        System.out.println("\t\tflags: " + Long.toBinaryString(LibCESMI.flags(context, target)));


        source = target.clone();
        System.out.println("source: " + Arrays.toString(source));
        next = LibCESMI.successor(context, 1, source, target);

        System.out.println("\t\tnext: " + next + " target: " + Arrays.toString(target));
        long flags =  LibCESMI.flags(context, target);
        boolean isAccepting = ((flags>>1) & 1) != 0;
        System.out.println("\t\tflags: " + Long.toBinaryString(flags) + " bool " + isAccepting);

        next = LibCESMI.successor(context, next, source, target);

        System.out.println("\t\tnext: " + next + " target: " + Arrays.toString(target));
        flags =  LibCESMI.flags(context, target);
        isAccepting = ((flags>>1) & 1) != 0;
        System.out.println("\t\tflags: " + Long.toBinaryString(flags) + " bool " + isAccepting);

        next = LibCESMI.successor(context, next, source, target);

        System.out.println("\t\tnext: " + next + " target: " + Arrays.toString(target));
        flags =  LibCESMI.flags(context, target);
        isAccepting = ((flags>>1) & 1) != 0;
        System.out.println("\t\tflags: " + Long.toBinaryString(flags) + " bool " + isAccepting);

        next = LibCESMI.successor(context, next, source, target);

        System.out.println("\t\tnext: " + next + " target: " + Arrays.toString(target));
        flags =  LibCESMI.flags(context, target);
        isAccepting = ((flags>>1) & 1) != 0;
        System.out.println("\t\tflags: " + Long.toBinaryString(flags) + " bool " + isAccepting);

        next = LibCESMI.successor(context, next, source, target);

        System.out.println("\t\tnext: " + next + " target: " + Arrays.toString(target));
        flags =  LibCESMI.flags(context, target);
        isAccepting = ((flags>>1) & 1) != 0;
        System.out.println("\t\tflags: " + Long.toBinaryString(flags) + " bool " + isAccepting);

    }
}
