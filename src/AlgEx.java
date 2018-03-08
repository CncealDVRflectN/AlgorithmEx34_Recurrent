import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;

public class AlgEx {
    private static int difSum;
    private static int dominoNum;
    private static int[] positiveDifs;
    private static int[] negativeDifs;

    private static boolean isSimple() {
        return dominoNum == 1 || difSum == 0 || Math.abs(difSum) == 1;
    }

    private static void readFileAndFillDifs() throws Exception {
        FastReader reader = new FastReader("in.txt");
        int tmpDif;

        dominoNum = reader.readNextInt();
        difSum = 0;
        positiveDifs = new int[6];
        negativeDifs = new int[6];

        for (int i = 0; i < dominoNum; i++) {
            tmpDif = reader.readNextInt() - reader.readNextInt();
            difSum += tmpDif;
            if (tmpDif > 0) {
                positiveDifs[tmpDif - 1]++;
            } else if (tmpDif < 0) {
                negativeDifs[-tmpDif - 1]++;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        FastWriter writer = new FastWriter("out.txt", 8);
        int minFlips = 0;
        int tmpDif;
        int sign;
        int curDif;
        int curIndex;
        int nextIndex;
        int[] usedDifs = new int[6];
        int[] search = new int[27];
        int[][][] searchArrs = new int[27][3][6];
        int[] curArr;
        Queue<Integer> queue = new ArrayDeque<>();

        Arrays.fill(search, -1);

        readFileAndFillDifs();
        if (isSimple()) {
            writer.writeInt(0);
            return;
        }

        curArr = (difSum > 0) ? positiveDifs : negativeDifs;
        sign = (difSum > 0) ? 1 : -1;
        while (Math.abs(difSum) > 13) {
            for (tmpDif = 5; tmpDif >= 0 && curArr[tmpDif] == 0; tmpDif--) ;
            difSum -= 2 * (tmpDif + 1) * sign;
            curArr[tmpDif]--;
            usedDifs[tmpDif]++;
            minFlips++;
        }

        queue.add(difSum);

        curIndex = 13 + difSum;
        search[curIndex] = minFlips;
        System.arraycopy(positiveDifs, 0, searchArrs[curIndex][0], 0, 6);
        System.arraycopy(negativeDifs, 0, searchArrs[curIndex][1], 0, 6);
        System.arraycopy(usedDifs, 0, searchArrs[curIndex][2], 0, 6);

        while (!queue.isEmpty()) {
            curDif = queue.poll();
            curIndex = 13 + curDif;
            for (int i = 0; i < 6; i++) {
                if (searchArrs[curIndex][0][i] > 0) {
                    tmpDif = curDif - 2 * (i + 1);
                    nextIndex = 13 + tmpDif;
                    if (Math.abs(tmpDif) <= 13 && search[nextIndex] == -1) {
                        search[nextIndex] = search[curIndex] + 1;
                        System.arraycopy(searchArrs[curIndex][0], 0, searchArrs[nextIndex][0], 0, 6);
                        System.arraycopy(searchArrs[curIndex][1], 0, searchArrs[nextIndex][1], 0, 6);
                        System.arraycopy(searchArrs[curIndex][2], 0, searchArrs[nextIndex][2], 0, 6);
                        searchArrs[nextIndex][0][i]--;
                        queue.add(tmpDif);
                    }
                }

                if (searchArrs[curIndex][1][i] > 0) {
                    tmpDif = curDif + 2 * (i + 1);
                    nextIndex = 13 + tmpDif;
                    if (Math.abs(tmpDif) <= 13 && search[nextIndex] == -1) {
                        search[nextIndex] = search[curIndex] + 1;
                        System.arraycopy(searchArrs[curIndex][0], 0, searchArrs[nextIndex][0], 0, 6);
                        System.arraycopy(searchArrs[curIndex][1], 0, searchArrs[nextIndex][1], 0, 6);
                        System.arraycopy(searchArrs[curIndex][2], 0, searchArrs[nextIndex][2], 0, 6);
                        searchArrs[nextIndex][1][i]--;
                        queue.add(tmpDif);
                    }
                }

                if (searchArrs[curIndex][2][i] > 0) {
                    tmpDif = curDif + 2 * (i + 1) * sign;
                    nextIndex = 13 + tmpDif;
                    if (Math.abs(tmpDif) <= 13 && (search[nextIndex] == -1 || search[nextIndex] > search[curIndex] - 1)) {
                        search[nextIndex] = search[curIndex] - 1;
                        System.arraycopy(searchArrs[curIndex][0], 0, searchArrs[nextIndex][0], 0, 6);
                        System.arraycopy(searchArrs[curIndex][1], 0, searchArrs[nextIndex][1], 0, 6);
                        System.arraycopy(searchArrs[curIndex][2], 0, searchArrs[nextIndex][2], 0, 6);
                        searchArrs[nextIndex][2][i]--;
                        queue.add(tmpDif);
                    }
                }
            }
        }

        minFlips = Integer.MAX_VALUE;
        for (int i = 0; i <= 13; i++) {
            if (search[13 + i] != -1 || search[13 - i] != -1) {
                if (search[13 + i] != -1) {
                    minFlips = search[13 + i];
                }
                if (search[13 - i] != -1) {
                    minFlips = Math.min(minFlips, search[13 - i]);
                }
                break;
            }
        }

        writer.writeInt(minFlips);
    }

    private static class FastReader {
        FileChannel channel;
        MappedByteBuffer buffer;

        FastReader(String fileName) throws IOException {
            channel = new FileInputStream(fileName).getChannel();
            buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        }

        public int readNextInt() throws IOException {
            int num = -1;
            int result = 0;

            while (num < 0 || num > 9) {
                if (!buffer.hasRemaining()) {
                    break;
                }
                num = buffer.get() - '0';
            }
            result += num;

            while (num >= 0 && num <= 9) {
                if (!buffer.hasRemaining()) {
                    break;
                }
                num = buffer.get() - '0';
                if (num >= 0 && num <= 9) {
                    result *= 10;
                    result += num;
                }
            }

            return result;
        }

        public void close() throws IOException {
            channel.close();
        }
    }

    private static class FastWriter {
        FileChannel channel;
        ByteBuffer buffer;

        FastWriter(String fileName, int bufferByteSize) throws IOException {
            buffer = ByteBuffer.allocateDirect(bufferByteSize);
            channel = new FileOutputStream(fileName).getChannel();
            buffer.clear();
        }

        public void writeInt(int num) throws IOException {
            byte[] buf = new byte[8];
            int index = -1;

            do {
                buf[++index] = (byte)('0' + num % 10);
                num /= 10;
            } while (num != 0);

            buffer.clear();
            while (index >= 0) {
                buffer.put(buf[index--]);
            }
            buffer.flip();

            while (buffer.hasRemaining()) {
                channel.write(buffer);
            }
        }

        public void close() throws IOException {
            channel.close();
        }
    }
}
