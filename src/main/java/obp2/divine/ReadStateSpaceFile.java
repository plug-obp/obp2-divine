package obp2.divine;

import java.io.File;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class ReadStateSpaceFile {

    private static String bigExcelFile = "/Users/ciprian/RESEARCH/T_AntiZero_Emilien/obelix/obelix/_build/adding.4.bin";

    public static void main(String[] args) throws Exception
    {
        try (RandomAccessFile file = new RandomAccessFile(new File(bigExcelFile), "r"))
        {
            //Get file channel in read-only mode
            FileChannel fileChannel = file.getChannel();

            //Get direct byte buffer access using channel.map() operation
            MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());

            // the buffer now reads the file as if it were loaded in memory.
            System.out.println(buffer.isLoaded());  //prints false
            System.out.println(buffer.capacity());  //Get the size based on content size of file

            /*
             * configuration_size
             * # of configurations
             * configuration-1
             * configuration-2
             * ...
             * configuration n
             * */

            byte[] data = new byte[4];
            buffer.get(data);
            int configuration_size = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getInt();

            data = new byte[8];
            buffer.get(data);
            int number_of_configurations = (int) ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getLong();

            System.out.println("configuration size:" + configuration_size + "\n# configurations: " + number_of_configurations );

            CompactLinearScanSet table = new CompactLinearScanSet(
                    (int) (number_of_configurations * (1.25)),
                    configuration_size,
                    Arrays::hashCode
            );

            for (int i = 0; i<number_of_configurations; i++) {
                data = new byte[configuration_size];
                buffer.get(data);
                table.add(data);
            }

            fileChannel.close();
        }
    }
}
