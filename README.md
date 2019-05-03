# simple-index
Write a java program (using java.io.RandomAccessFile class) that takes a data file that contain a set of records as following: 
The data record is 12 bytes long, its structure is as follows: 
Product_ID(int) = 4 bytes Product_Price (int)= 4 bytes Product_quantity (int) = 4 bytes 
 
And produce an index file with the following structure: 
Key(int)= 4 bytes Byte offset(int) = 4 bytes 
Hints: 
- You can you the “SampleDataFile.bin” as the input sample data file to test your code 
- The insertion of a new record means adding of new record in the data file and a new record in the index file.
- The data file is unsorted file. - The index file MUST always be sorted. 
- The deletion of a data record should remove its record from the index file [doing any necessary shifting on the index file]. 
- The search function must be implemented as binary search.  
- Binary Search algorithm conducted on the index file should return either the second integer of the record you are searching for (The byte offset of the record) on the sorted file or -1 if the record is not found
 
