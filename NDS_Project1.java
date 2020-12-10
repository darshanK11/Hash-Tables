import java.util.HashSet;
import java.util.Scanner;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class NDS_Project1 {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        int numTableEntries, numFlows, numHashes, numCuckooSteps, numSegments;
        System.out.print("Enter the number of table entries: ");
        numTableEntries = sc.nextInt();
        System.out.print("Enter the number of flows: ");
        numFlows = sc.nextInt();
        System.out.print("Enter the number of hashes: ");
        numHashes = sc.nextInt();
        System.out.print("Enter the number of cuckoo steps: ");
        numCuckooSteps = sc.nextInt();
        System.out.print("Enter the number of segments: ");
        numSegments = sc.nextInt();

        sc.close();

        int[] hashArray = new int[numHashes + 1];
        hashArray[0] = -1;
        int[] segmentArray = new int[numSegments + 1];
        segmentArray[0] = -1;
        for (int i = 1; i < hashArray.length; i++) {
            hashArray[i] = (int) (Math.random() * Integer.MAX_VALUE) + 1;
            if (i < segmentArray.length) {
                segmentArray[i] = hashArray[i];
            }
        }
        if (segmentArray.length > hashArray.length) {
            for (int i = hashArray.length; i < segmentArray.length; i++) {
                segmentArray[i] = (int) (Math.random() * Integer.MAX_VALUE) + 1;
            }
        }

        int[] multiHashTable = new int[numTableEntries + 1];
        int numFlowsInMultiHashTable = 0;
        int[] cuckooHashTable = new int[numTableEntries + 1];
        int numFlowsInCuckooHashTable = 0;
        int[] dLeftHashTable = new int[numTableEntries + 1];
        int numFlowsInDLeftHashTable = 0;

        HashSet<Integer> flowIdHashSet = new HashSet<>();

        for (int i = 1; i <= numFlows; i++) {
            int flowId = (int) (Math.random() * Integer.MAX_VALUE) + 1;

            while (flowIdHashSet.contains(flowId)) {
                flowId = (int) (Math.random() * Integer.MAX_VALUE) + 1;
            }

            flowIdHashSet.add(flowId);

            // Multi Hash table
            boolean foundFlagMultiHashTable = false;
            for (int j = 1; j < hashArray.length; j++) {
                int tableIndex = ((hashArray[j] ^ flowId) % numTableEntries) + 1;
                if (multiHashTable[tableIndex] != 0) {
                    if (multiHashTable[tableIndex] == flowId) {
                        foundFlagMultiHashTable = true;
                        break;
                    }
                }
            }
            if (!foundFlagMultiHashTable) {
                for (int j = 1; j < hashArray.length; j++) {
                    int tableIndex = ((hashArray[j] ^ flowId) % numTableEntries) + 1;
                    if (multiHashTable[tableIndex] == 0) {
                        multiHashTable[tableIndex] = flowId;
                        numFlowsInMultiHashTable++;
                        break;
                    }
                }
            }

            // Cuckoo Hash Table
            boolean foundFlagCuckooHashTable = false;
            for (int j = 1; j < hashArray.length; j++) {
                int tableIndex = ((hashArray[j] ^ flowId) % numTableEntries) + 1;
                if (cuckooHashTable[tableIndex] != 0) {
                    if (cuckooHashTable[tableIndex] == flowId) {
                        foundFlagCuckooHashTable = true;
                        break;
                    }
                }
            }
            boolean recordFlagCuckooHashTable = false;
            if (!foundFlagCuckooHashTable) {
                for (int j = 1; j < hashArray.length; j++) {
                    int tableIndex = ((hashArray[j] ^ flowId) % numTableEntries) + 1;
                    if (cuckooHashTable[tableIndex] == 0) {
                        cuckooHashTable[tableIndex] = flowId;
                        numFlowsInCuckooHashTable++;
                        recordFlagCuckooHashTable = true;
                        break;
                    }
                }
            }
            if (!foundFlagCuckooHashTable && !recordFlagCuckooHashTable) {
                for (int j = 1; j < hashArray.length; j++) {
                    int tableIndex = ((hashArray[j] ^ flowId) % numTableEntries) + 1;
                    if (moveCuckoo(cuckooHashTable, tableIndex, numCuckooSteps, hashArray, numTableEntries)) {
                        cuckooHashTable[tableIndex] = flowId;
                        numFlowsInCuckooHashTable++;
                        break;
                    }
                }
            }

            // D Left Hash Table
            boolean foundFlagDLeftHashTable = false;
            for (int j = 1; j < segmentArray.length; j++) {
                int tableIndex = (((segmentArray[j] ^ flowId) % (int) (numTableEntries / numSegments)) + 1)
                        + ((int) (numTableEntries / numSegments) * (j - 1));
                if (dLeftHashTable[tableIndex] != 0) {
                    if (dLeftHashTable[tableIndex] == flowId) {
                        foundFlagDLeftHashTable = true;
                        break;
                    }
                }
            }
            if (!foundFlagDLeftHashTable) {
                for (int j = 1; j < segmentArray.length; j++) {
                    int tableIndex = (((segmentArray[j] ^ flowId) % (int) (numTableEntries / numSegments)) + 1)
                            + ((int) (numTableEntries / numSegments) * (j - 1));
                    if (dLeftHashTable[tableIndex] == 0) {
                        dLeftHashTable[tableIndex] = flowId;
                        numFlowsInDLeftHashTable++;
                        break;
                    }
                }
            }

        }

        StringBuilder outputMultiHashTable = new StringBuilder("");
        outputMultiHashTable
                .append("MULTI HASH TABLE\n\nNo. of Flows in the Table: " + numFlowsInMultiHashTable + "\n\n");
        StringBuilder outputCuckooHashTable = new StringBuilder("");
        outputCuckooHashTable
                .append("CUCKOO HASH TABLE\n\nNo. of Flows in the Table: " + numFlowsInCuckooHashTable + "\n\n");
        StringBuilder outputDLeftHashTable = new StringBuilder("");
        outputDLeftHashTable
                .append("D LEFT HASH TABLE\n\nNo. of Flows in the Table: " + numFlowsInDLeftHashTable + "\n\n");

        for (int i = 1; i <= numTableEntries; i++) {
            if (multiHashTable[i] == 0) {
                outputMultiHashTable.append("Index " + i + ": 0\n");
            } else {
                outputMultiHashTable.append("Index " + i + ": Flow ID - " + multiHashTable[i] + "\n");
            }
            if (cuckooHashTable[i] == 0) {
                outputCuckooHashTable.append("Index " + i + ": 0\n");
            } else {
                outputCuckooHashTable.append("Index " + i + ": Flow ID - " + cuckooHashTable[i] + "\n");
            }
            if (dLeftHashTable[i] == 0) {
                outputDLeftHashTable.append("Index " + i + ": 0\n");
            } else {
                outputDLeftHashTable.append("Index " + i + ": Flow ID - " + dLeftHashTable[i] + "\n");
            }
        }
        outputMultiHashTable.append("\n");
        outputCuckooHashTable.append("\n");
        outputDLeftHashTable.append("\n");

        try {
            Path fileName = Path.of("DemoOutput_Multi_Hash_Table.txt");
            Files.writeString(fileName, outputMultiHashTable);
            fileName = Path.of("DemoOutput_Cuckoo_Hash_Table.txt");
            Files.writeString(fileName, outputCuckooHashTable);
            fileName = Path.of("DemoOutput_D_Left_Hash_Table.txt");
            Files.writeString(fileName, outputDLeftHashTable);
        } catch (IOException e) {
            System.out.println("IO Exception");
        }

    }

    public static boolean moveCuckoo(int[] cuckooHashTable, int tableIndex, int s, int[] hashArray,
            int numTableEntries) {
        if (s == 0) {
            return false;
        }
        int flowId = cuckooHashTable[tableIndex];
        for (int i = 1; i < hashArray.length; i++) {
            int newIndex = ((hashArray[i] ^ flowId) % numTableEntries) + 1;
            if (newIndex != tableIndex && cuckooHashTable[newIndex] == 0) {
                cuckooHashTable[newIndex] = flowId;
                return true;
            }
        }
        for (int i = 1; i < hashArray.length; i++) {
            int newIndex = ((hashArray[i] ^ flowId) % numTableEntries) + 1;
            if (newIndex != tableIndex && moveCuckoo(cuckooHashTable, newIndex, s - 1, hashArray, numTableEntries)) {
                cuckooHashTable[newIndex] = flowId;
                return true;
            }
        }
        return false;
    }

}