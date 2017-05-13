import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

public class AlgEx {
    private static boolean isSimple() {
        return dominoNum == 1 || difSum == 0 || Math.abs(difSum) == 1;
    }

    private static void readFileAndFillDifs() throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader("in.txt"));
        int tmpDif;
        String[] nums;
        dominoNum = Integer.parseInt(reader.readLine());
        difSum = 0;
        positiveDifs = new int[6];
        negativeDifs = new int[6];
        for (int i = 0; i < dominoNum; i++) {
            while ((nums = reader.readLine().split(" ")).length != 2) ;
            tmpDif = Integer.parseInt(nums[0]) - Integer.parseInt(nums[1]);
            difSum += tmpDif;
            if (tmpDif > 0) {
                positiveDifs[tmpDif - 1]++;
            } else if (tmpDif < 0) {
                negativeDifs[-tmpDif - 1]++;
            }
        }
        reader.close();
    }

    private static int difSum;
    private static int dominoNum;
    private static int[] positiveDifs;
    private static int[] negativeDifs;

    public static void main(String[] args) throws Exception {
        FileWriter writer = new FileWriter("out.txt");
        int minFlips = 0;
        int tmpDif;
        int sign;
        int curDif;
        int[] usedDifs = new int[6];
        int[] search = new int[27];
        int[][][] searchArrs = new int[27][3][];
        int[] curArr;
        Queue<Integer> queue = new ArrayDeque<>();
        Arrays.fill(search, -1);
        readFileAndFillDifs();
        if (isSimple()) {
            writer.write("0");
            writer.close();
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
        search[13 + difSum] = minFlips;
        searchArrs[13 + difSum][0] = positiveDifs.clone();
        searchArrs[13 + difSum][1] = negativeDifs.clone();
        searchArrs[13 + difSum][2] = usedDifs.clone();
        while (!queue.isEmpty()) {
            curDif = queue.poll();
            for (int i = 0; i < 6; i++) {
                if (searchArrs[13 + curDif][0][i] > 0) {
                    tmpDif = curDif - 2 * (i + 1);
                    if (Math.abs(tmpDif) <= 13 && search[13 + tmpDif] == -1) {
                        search[13 + tmpDif] = search[13 + curDif] + 1;
                        searchArrs[13 + tmpDif][0] = searchArrs[13 + curDif][0].clone();
                        searchArrs[13 + tmpDif][0][i]--;
                        searchArrs[13 + tmpDif][1] = searchArrs[13 + curDif][1].clone();
                        searchArrs[13 + tmpDif][2] = searchArrs[13 + curDif][2].clone();
                        queue.add(tmpDif);
                    }
                }
            }
            for (int i = 0; i < 6; i++) {
                if (searchArrs[13 + curDif][1][i] > 0) {
                    tmpDif = curDif + 2 * (i + 1);
                    if (Math.abs(tmpDif) <= 13 && search[13 + tmpDif] == -1) {
                        search[13 + tmpDif] = search[13 + curDif] + 1;
                        searchArrs[13 + tmpDif][1] = searchArrs[13 + curDif][1].clone();
                        searchArrs[13 + tmpDif][1][i]--;
                        searchArrs[13 + tmpDif][0] = searchArrs[13 + curDif][0].clone();
                        searchArrs[13 + tmpDif][2] = searchArrs[13 + curDif][2].clone();
                        queue.add(tmpDif);
                    }
                }
            }
            for (int i = 0; i < 6; i++) {
                if (searchArrs[13 + curDif][2][i] > 0) {
                    tmpDif = curDif + 2 * (i + 1) * sign;
                    if (Math.abs(tmpDif) <= 13 && (search[13 + tmpDif] == -1 || search[13 + tmpDif] > search[13 + curDif] - 1)) {
                        search[13 + tmpDif] = search[13 + curDif] - 1;
                        searchArrs[13 + tmpDif][0] = searchArrs[13 + curDif][0].clone();
                        searchArrs[13 + tmpDif][1] = searchArrs[13 + curDif][1].clone();
                        searchArrs[13 + tmpDif][2] = searchArrs[13 + curDif][2].clone();
                        searchArrs[13 + tmpDif][2][i]--;
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
        writer.write(minFlips + "");
        writer.close();
    }
}
