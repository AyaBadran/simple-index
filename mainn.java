package mainn;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Scanner;

public class mainn {
	
public static void main(String[] args) throws IOException, FileNotFoundException {
    RandomAccessFile data_file = new RandomAccessFile("datafile.bin", "rw");
    RandomAccessFile index_file = new RandomAccessFile("Index.bin", "rw");
    RandomAccessFile indexfile = new RandomAccessFile("index.bin", "rw");

     ArrayList<index> indexes= new ArrayList<>();

    Build_for_first_time( data_file,indexfile,index_file,indexes);
    	
	while (true) 
        {     
         System.out.println("Press 1 to insert new record : ");
         System.out.println("Press 2 to delete a record :");
         System.out.println("Press 3 to update a new record : ");
         System.out.println("Press 4 to search for a record : ");
         System.out.println("press 5 if you want to exit : ");
         Scanner in = new Scanner(System.in);
         int choice = in.nextInt();
         if (choice == 1) 
         {            	
             insert(data_file,indexfile);
             printIndex(indexfile);
         }         	 
         else if (choice == 2) 
         {
             delete(data_file,indexfile);
         } 
         else if (choice == 3) 
         {
        	 update(data_file,indexfile);   	 
         } 
         else if(choice==4)
         {
        	 search(data_file,indexfile);
         }
         else if(choice==5)
         {
             System.out.println("FINISHED!");
                        break;         
         }
         else 
         {
          System.out.println("failed Choice , Try Again :");
          continue;
         }                             
     }//loop
}//main function

public static void Build_for_first_time(RandomAccessFile data_file,RandomAccessFile indexfile,RandomAccessFile index_file, ArrayList<index> indexes) throws IOException
{      
        data_file.seek(0);
        index_file.seek(0);
        //record=12
        for (int offset = 0; offset < data_file.length(); offset += 12) 
        {
            data_file.seek(offset);
            int key = data_file.readInt();
            index_file.writeInt(key);
            index_file.writeInt(offset);
        }
        for (int i = 0; i < index_file.length(); i += 8) 
        {
        	index_file.seek(i);
            int key = index_file.readInt();
            int offset = index_file.readInt();
            index ind = new index();
            ind.key = key;
            ind.offset = offset;
            indexes.add(ind);
        }
        //sort
        for (int i = 0; i < indexes.size(); i++)
        {
            for (int j = 0; j < indexes.size() - 1; j++) 
            {
                if (indexes.get(j).key > indexes.get(j + 1).key) 
                {
                    index temp = new index();
                    temp.key = indexes.get(j).key;
                    temp.offset =indexes.get(j).offset;
                    indexes.get(j).key =indexes.get(j + 1).key;
                    indexes.get(j).offset = indexes.get(j + 1).offset;
                    indexes.get(j + 1).key = temp.key;
                    indexes.get(j + 1).offset = temp.offset;
                }
            }
        }
        
        for (int i = 0; i < indexes.size(); i++)
        {
            index_file.seek(0);
            indexfile.writeInt(indexes.get(i).key);
            indexfile.writeInt(indexes.get(i).offset);
        }   
      //print records of index file 
        printIndex(indexfile);
} // build func

public static void printIndex(RandomAccessFile indexfile) throws IOException 
{
    indexfile.seek(0);
    System.out.println("Key " + "\t" + "offset");
    //record=8
    for (int i = 0; i < indexfile.length(); i += 8) 
    {
    	indexfile.seek(i);
        System.out.print(indexfile.readInt() + "\t" + indexfile.readInt() + "\n");
    }
}
/******************insert*********************/
    public static void insert(RandomAccessFile data_file,RandomAccessFile indexfile) throws IOException 
    {
        Scanner in = new Scanner(System.in);
        System.out.print("Insert ID:  ");
        int id = in.nextInt();
        System.out.print("Insert Price:  ");
        int price = in.nextInt();
        System.out.print("Insert Quantity:  ");
        int quantity = in.nextInt();

        int offset = (int) (data_file.length() - 1);
        data_file.seek(offset);
        data_file.writeInt(id);
        data_file.writeInt(price);
        data_file.writeInt(quantity);
                
        for (int i = 0; i <  indexfile.length(); i += 8)
        {
        	 indexfile.seek(i);

            int key =  indexfile.readInt();
            int valueoffest =  indexfile.readInt();
            if (key >= id) 
            {
            	 indexfile.seek(i);
            	 indexfile.writeInt(id);
            	 indexfile.writeInt(offset);
                
                id = key;
                offset = valueoffest; //will remains the big record
            }
        }
        // the biggest record added in the end of file
        indexfile.writeInt(id);
        indexfile.writeInt(offset);
    }
    
  public static int Binary_search(RandomAccessFile indexfile , int id) throws IOException
  {
       int offestmysearch = -1;
       int low = 0;
       int high = ((int) indexfile.length() - 1) / 8;//number of records in index file
       while (low <= high) 
       {
           int mid = low + (high - low) / 2;

           indexfile.seek(mid * 8);
           int check = indexfile.readInt(); //first field
           if (check == id) 
           {
               offestmysearch = indexfile.readInt(); //second field
               return offestmysearch;
           } 
           else if (check < id)
           {
               low = mid + 1;
           } 
           else 
           {
               high = mid - 1;
           }
           }
       return offestmysearch;
    }

    public static void search(RandomAccessFile data_file,RandomAccessFile indexfile) throws IOException 
    {
    	
    	Scanner in = new Scanner(System.in);
        System.out.print("Enter ID to search");
        int id = in.nextInt();
        int offset = Binary_search(indexfile , id);;
        if (offset != -1) 
        {
        	data_file.seek(offset);
            System.out.println("Product Found");    
            System.out.println("Product ID " + data_file.readInt());
            System.out.println("Product Price " + data_file.readInt());
            System.out.println("Product Quantity " + data_file.readInt());
        } 
        else
        {
            System.out.println("Product Not Found");
        }
    }

    public static void delete(RandomAccessFile data_file,RandomAccessFile indexfile) throws IOException
    {
    	Scanner in = new Scanner(System.in);
        System.out.print("Enter ID to search");
        int id = in.nextInt();
    	 int offset = Binary_search(indexfile , id);
         if (offset != -1)
         {
         	data_file.seek(offset);
         	data_file.writeChar('*');
            int  off_of_deleted_rec_indexfile = -1;
            //linear search to find offset of deleted record key in index file to delete in from here else
            for (int i = 0; i < indexfile.length(); i += 8)
            {
            	indexfile.seek(i);
               int nkey=indexfile.readInt();
                if (nkey == id) 
                {
                    off_of_deleted_rec_indexfile = i;
                    break;
                }
            }
            
            for (int j = off_of_deleted_rec_indexfile + 8; j < indexfile.length(); j += 8) 
            {
                indexfile.seek(j);
                int key = indexfile.readInt();
                int value = indexfile.readInt();
                indexfile.seek(j - 8);
                indexfile.writeInt(key);
                indexfile.writeInt(value);
            }

            indexfile.setLength(indexfile.length() - 8);
        } 
        else //if offset=-1
        {
            System.out.println("Product Not Found");
        }
   }
    
    
    public static void update(RandomAccessFile data_file,RandomAccessFile indexfile) throws IOException
    {
    	Scanner in = new Scanner(System.in);
        System.out.print("Enter ID to search");
        int id = in.nextInt();
        
   	    int offset = Binary_search(indexfile , id);
        if (offset != -1) 
        {
             System.out.print("offset of record will be updated in data file "+ offset);
             data_file.seek(offset+4);
          	 Scanner s = new Scanner(System.in);
             System.out.print("Enter updated Price ");
             int price = s.nextInt();
             System.out.print("Enter updated Quantity ");
             int quantity = s.nextInt();
             data_file.writeInt(price);
             data_file.writeInt(quantity);
        	
        }
        else 
        {
            System.out.println("Product Not Found");
        }
    }//end of update function
}//mainn class l ra2esy

