# Hash-Tables

In this project, I have implemented 3 types of Hash Tables.
1. Multi Hash Table
2. Cuckoo Hash Table
3. d-Left Hash Table

## How to Run

Run the following commands:
```bash
javac NDS_Project1.java
java NDS_Project1
```

Now, the code will ask for the following details:
1. Number of Table Entries
2. Number of Flows
3. Number of Hashes
4. Number of Cuckoo Steps
5. Number of Segments

Next, the code will run and ouput files for the 3 hash tables.

The output files will be named (These are different from the output files that I provided):
1. DemoOutput_Multi_Hash_Table.txt
2. DemoOutput_Cuckoo_Hash_Table.txt
3. DemoOutput_D_Left_Hash_Table.txt

The output files are structured as follows:
The first line will be the name of the hash table.

The second line onwards is the actual hash table. This shows the Index of the table and the Flow ID of the flow that is inserted at that Index. If now Flow is inserted, the 0 is displayed.
