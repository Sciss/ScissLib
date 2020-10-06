import de.sciss.io.AudioFile;
import de.sciss.io.AudioFileDescr;

import java.io.File;
import java.io.IOException;

public class Issue2 {
    public static void main(String[] args) {
        try {
            run();
            System.out.println("Done.");

        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void run() throws IOException {
//        final File f = File.createTempFile("test", ".w64");
//        f.deleteOnExit();
        final File f = new File(new File(new File(System.getProperty("user.home")), "Documents"), "test.w64");
        final AudioFileDescr afdOut = new AudioFileDescr();
        afdOut.sampleFormat     = AudioFileDescr.FORMAT_INT;
        afdOut.bitsPerSample    = 24;
        afdOut.channels         = 1;
        afdOut.type             = AudioFileDescr.TYPE_WAVE64;
        afdOut.rate             = 48000.0;
        afdOut.file             = f;
        final AudioFile afOut = AudioFile.openAsWrite(afdOut);
        final float[][] data    = new float[1][1];
        try {
            data[0][0] = 0.1234f;
            afOut.writeFrames(data, 0, 1);
        } finally {
            afOut.cleanUp();
        }

        final AudioFile afIn    = AudioFile.openAsRead(f);
        assert afIn.getFrameNum() == 1;
        assert afIn.getChannelNum() == 1;
        assert afIn.getDescr().rate == 48000.0;
        try {
            data[0][0] = 0.0f;
            afIn.readFrames(data, 0, 1);
            assert data[0][0] == 0.1234f;
        } finally {
            afIn.cleanUp();
        }
    }
}
