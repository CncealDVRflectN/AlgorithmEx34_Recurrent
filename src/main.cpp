#include <iostream>
#include <queue>

using namespace std;

int difSum;
int dominoNum;
int *positiveDifs;
int *negativeDifs;

void init() {
    ios_base::sync_with_stdio(false);
    cin.tie(NULL);
}

void fillArray(int *arr, int length, int num) {
    for (int i = 0; i < length; i++)
    {
        arr[i] = num;
    }
}

void cloneArray(int *arrDest, int *arrSource, int length) {
    for (int i = 0; i < length; i++)
    {
        arrDest[i] = arrSource[i];
    }
}

bool isSimple() {
    return dominoNum == 1 || difSum == 0 || abs(difSum) == 1;
}

void writeInt(int x) {
    char buf[8];
    char *p = buf;
    do
    {
        *p++ = '0' + x % 10;
        x /= 10;
    }
    while (x);
    do
    {
        putchar(*--p);
    }
    while (p > buf);
}

void readFileAndFillDifs() {
    int tmpDif;
    char buf;

    difSum = 0;
    dominoNum = 0;
    while ((buf = getchar() - '0') >= 0 && buf <= 9)
    {
        dominoNum *= 10;
        dominoNum += buf;
    }
    positiveDifs = (int *) calloc(6, sizeof(int));
    negativeDifs = (int *) calloc(6, sizeof(int));
    fillArray(positiveDifs, 6, 0);
    fillArray(negativeDifs, 6, 0);

    for (int i = 0; i < dominoNum; i++)
    {
        while ((buf = getchar() - '0') < 0 || buf > 9);
        tmpDif = buf;
        while ((buf = getchar() - '0') < 0 || buf > 9);
        tmpDif -= buf;
        difSum += tmpDif;

        if (tmpDif > 0) {
            positiveDifs[tmpDif - 1]++;
        } else if (tmpDif < 0) {
            negativeDifs[-tmpDif - 1]++;
        }
    }
}

int main() {
    int minFlips = 0;
    int tmpDif = 0;
    int sign = 0;
    int curDif = 0;
    int curIndex;
    int nextIndex;
    int *usedDifs = (int *) calloc(6, sizeof(int));
    int *search = (int *) calloc(27, sizeof(int));
    int *curArr;
    int ***searchArrs = (int ***) calloc(27, sizeof(int **));
    queue<int> indexQueue;

    freopen("in.txt", "r", stdin);
    freopen("out.txt", "w", stdout);
    init();
    fillArray(search, 27, -1);
    fillArray(usedDifs, 6, 0);
    readFileAndFillDifs();
    for (int i = 0; i < 27; i++)
    {
        searchArrs[i] = (int **) calloc(3, sizeof(int *));
        for (int j = 0; j < 3; j++)
        {
            searchArrs[i][j] = (int *) calloc(6, sizeof(int *));
        }
    }

    if (isSimple())
    {
        writeInt(0);
        return 0;
    }

    curArr = (difSum > 0) ? positiveDifs : negativeDifs;
    sign = (difSum > 0) ? 1 : -1;

    while (abs(difSum) > 13)
    {
        for (tmpDif = 5; tmpDif >= 0 && curArr[tmpDif] == 0; tmpDif--);
        difSum -= 2 * (tmpDif + 1) * sign;
        curArr[tmpDif]--;
        usedDifs[tmpDif]++;
        minFlips++;
    }

    indexQueue.push(difSum);
    curIndex = 13 + difSum;
    search[curIndex] = minFlips;
    cloneArray(searchArrs[curIndex][0], positiveDifs, 6);
    cloneArray(searchArrs[curIndex][1], negativeDifs, 6);
    cloneArray(searchArrs[curIndex][2], usedDifs, 6);

    while(!indexQueue.empty())
    {
        curDif = indexQueue.front();
        indexQueue.pop();
        curIndex = 13 + curDif;

        for (int i = 0; i < 6; i++)
        {
            if (searchArrs[curIndex][0][i] > 0)
            {
                tmpDif = curDif - 2 * (i + 1);
                nextIndex = 13 + tmpDif;
                if (abs(tmpDif) <= 13 && search[nextIndex] == -1)
                {
                    search[nextIndex] = search[curIndex] + 1;
                    cloneArray(searchArrs[nextIndex][0], searchArrs[curIndex][0], 6);
                    cloneArray(searchArrs[nextIndex][1], searchArrs[curIndex][1], 6);
                    cloneArray(searchArrs[nextIndex][2], searchArrs[curIndex][2], 6);
                    searchArrs[nextIndex][0][i]--;
                    indexQueue.push(tmpDif);
                }
            }

            if (searchArrs[curIndex][1][i] > 0)
            {
                tmpDif = curDif + 2 * (i + 1);
                nextIndex = 13 + tmpDif;
                if (abs(tmpDif) <= 13 && search[nextIndex] == -1)
                {
                    search[nextIndex] = search[curIndex] + 1;
                    cloneArray(searchArrs[nextIndex][0], searchArrs[curIndex][0], 6);
                    cloneArray(searchArrs[nextIndex][1], searchArrs[curIndex][1], 6);
                    cloneArray(searchArrs[nextIndex][2], searchArrs[curIndex][2], 6);
                    searchArrs[nextIndex][1][i]--;
                    indexQueue.push(tmpDif);
                }
            }

            if (searchArrs[curIndex][2][i] > 0)
            {
                tmpDif = curDif + 2 * (i + 1) * sign;
                nextIndex = 13 + tmpDif;
                if (abs(tmpDif) <= 13 && (search[nextIndex] == -1 || search[nextIndex] > search[curIndex] - 1))
                {
                    search[nextIndex] = search[curIndex] - 1;
                    cloneArray(searchArrs[nextIndex][0], searchArrs[curIndex][0], 6);
                    cloneArray(searchArrs[nextIndex][1], searchArrs[curIndex][1], 6);
                    cloneArray(searchArrs[nextIndex][2], searchArrs[curIndex][2], 6);
                    searchArrs[nextIndex][2][i]--;
                    indexQueue.push(tmpDif);
                }
            }
        }
    }

    minFlips = dominoNum;
    for (int i = 0; i <= 13; i++)
    {
        if (search[13 + i] != -1 || search[13 - i] != -1)
        {
            if (search[13 + i] != -1)
            {
                minFlips = search[13 + i];
            }
            if (search[13 - i] != -1)
            {
                minFlips = min(minFlips, search[13 - i]);
            }
            break;
        }
    }

    writeInt(minFlips);

    return 0;
}