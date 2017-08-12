import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;




 class TSQueue implements Runnable {

	private static  LinkedList <Integer>list = new LinkedList <Integer>();
	private long timeout;
	private int count;
	private static Lock lock = new ReentrantLock(true);
	private static Condition Empty1 = lock.newCondition();
	private static Condition Full1 = lock.newCondition();
	private static Condition Empty2 = lock.newCondition();
	private static Condition Full2 = lock.newCondition();
	private static Condition Empty3 = lock.newCondition();
	private static Condition Full3 = lock.newCondition();
	private int work;
	private int number;
	private int priority;
	private static int w3count=0;
	private static int w2count=0;
	private static int w1count=0;
	private static int r3count=0;
	private static int r2count=0;
	private static int r1count=0;

	public TSQueue(int n, long t,int work,int number,int priority){
		this.timeout=t;
		this.count=n;
		this.number=number;
		this.work=work;
		this.priority=priority;
		
	}
	 public void run() {
	
		 int q=0;
		 if(work==1 && number>0)
			if( put(number,priority)){
				System.out.println("write "+number+" into list");
			
			}else{
				System.out.println("unable to write "+number+" into list");
			}
		 else if(work==2)
			 q= get(priority);
		 if(q > 0){
			 System.out.println("read "+q+" from list");
		 }else{
			 System.out.println("unable to read from list");
		 }
		 
		 
		 
	 }
	public boolean put(int value, int priority) {
		
		lock.lock();
		if(priority==3){
			w3count++;
	
		}else if(priority==2){
			w2count++;
	
		}else if(priority==1){
			w1count++;
		
		}
		
		
		lock.unlock();
		while(w3count!=0 && priority!=3){
			System.out.print("");
		}
		while(w3count==0 && w2count!=0 && priority!=2){
			System.out.print("");
		}
		
		lock.lock();
	
		while(list.size()>=count){
			try {
				if(priority==3){
					
					Full3.await(2, TimeUnit.SECONDS);
					
				}else if(priority==2){
				
					Full2.await(2, TimeUnit.SECONDS);
					
				}else if(priority==1){
					
					Full1.await(2, TimeUnit.SECONDS);
					
				}
				
			}
		 catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		list.addLast(number);
		if(priority==3){
			w3count--;
	
		}else if(priority==2){
			w2count--;
	
		}else if(priority==1){
			w1count--;
		
		}
		if(r3count!=0){
		Empty3.signal();
		}else if(r2count!=0){
		Empty2.signal();	
		}else if(r1count!=0){
			Empty1.signal();
		}
		lock.unlock();
		
		
		return true;
	}
	public int get(int priority) {
		lock.lock();
		if(priority==3){
			r3count++;
		
		}else if(priority==2){
			r2count++;

		}else if(priority==1){
			r1count++;
		
		}
		lock.unlock();
		while(r3count!=0 && priority!=3){
			System.out.print("");
		}
		while(r3count==0 && r2count!=0 && priority!=2){
			System.out.print("");
		}
		lock.lock();
		while(list.size()<=0){
			try {
				if(priority==3){
				
					Empty3.await(timeout, TimeUnit.MILLISECONDS);
				
				}else if(priority==2){
				
					Empty2.await(timeout, TimeUnit.MILLISECONDS);
				
				}else if(priority==1){
				
					Empty1.await(timeout, TimeUnit.MILLISECONDS);
				
				}
				
				
			}
				catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return -1;
			}
		}
		int x=(int) list.removeFirst();
		if(priority==3){
			r3count--;
		
		}else if(priority==2){
			r2count--;

		}else if(priority==1){
			r1count--;
		
		}
		if(w3count!=0){
			Full3.signal();
			}else if(w2count!=0){
				Full2.signal();	
			}else if(w1count!=0){
				Full1.signal();
			}
	
		lock.unlock();
		return x;
	}
}

public class project4 {
	 
    public static void main(String[] args) {

    	  int[]first=new int[10000];
    	  int[]second=new int[10000];
    	  int[]Third=new int[10000];
    	  int[]Fourth=new int[10000];
    	  TSQueue[]thread=new TSQueue[100];
    String line="";
               try {
                   // FileReader reads text files in the default encoding.
                   FileReader fileReader1 = 
                       new FileReader("p4.txt");

                   // Always wrap FileReader in BufferedReader.
                   BufferedReader bufferedReader1 = 
                       new BufferedReader(fileReader1);
                
                   int g=0;
                  
                   
                   while((line = bufferedReader1.readLine()) != null) {
                	   
                	   String[]threaddetail=line.split(" ");
                	   if(threaddetail[1].equals("put")){
                		   first[g]=Integer.parseInt(threaddetail[0]);
                		   second[g]=1;
                		   Third[g]=Integer.parseInt(threaddetail[2]);
                		   Fourth[g]=Integer.parseInt(threaddetail[3]);
                		   
                	   }else if(threaddetail[1].equals("get")){
                		   first[g]=Integer.parseInt(threaddetail[0]);
                		   second[g]=2;
                		   Third[g]=0;
                		   Fourth[g]=Integer.parseInt(threaddetail[2]);
                		   
                	   }
                	 
                 g++;
           
                   	
                   	}
        
	                    
               }
               catch(FileNotFoundException ex) {
                   System.out.println(
                       "Unable to open file " );                
               }
               catch(IOException ex) {
                   System.out.println(
                       "Error reading file " 
                     );                  
                   // Or we could just do this: 
                   // ex.printStackTrace();
               }
               
               
               ExecutorService executor = Executors.newFixedThreadPool(100);
          
               for (int i = 0; i < 10000; i++) {
                   Runnable worker = new TSQueue(10,1,second[i],Third[i],Fourth[i]);
                   executor.execute(worker);
                 }
            /*   executor.shutdown();
               while (!executor.isTerminated()) {
               }*/
    }
}
    