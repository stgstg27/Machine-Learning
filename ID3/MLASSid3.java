/*
*******               Created on 2nd October 2017                           ******
*******  Implementing ID3  on Cencus +Income Data in UCL repository         ****** 

@Author: Saurabh , Abhiram ,Dhruv
*/


// Importing Immportant Libraries
import java.util.*;
import java.io.BufferedReader;
import java.io.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.*;
import java.lang.*;


//Class Node represent structure of node in Tree structure
 class Node
 {
	public Node parent;
	ArrayList<Integer> set_of_instances;
	int depth;
	ArrayList<Integer> Attr;
	double pos = 0.0;
	double neg = 0.0;
	ArrayList<Node> children;
	Boolean leaf;
	int label;
	double entropy;
	int attribute_used;
	public Node()// this node is amd ein order to encounter that root has no parent and we are not able to send NULL as its parent so we are creating a dummy node through this constructor 
	{
		depth = -1;
	} 

	public Node(Node parent , ArrayList<Integer> L,int depth,ArrayList<Integer> Attr)
	{
		System.out.println("Constructor Called");
		System.out.println(depth+1);
		System.out.println("No of attribtues to work with ::" + Attr.size());
		this.parent = parent;
		this.depth = depth + 1;
		this.Attr = new ArrayList<Integer>(Attr);
		children = new ArrayList<Node>();
		this.set_of_instances = new ArrayList<Integer>(); // This will create a shallow copy of the L in set_of_instace, in this way we need to take care that any change done in set_of_instace may refelct on L;
		for(int i=0;i<L.size();i++)
		{
			
			set_of_instances.add(L.get(i));
		}
		//System.out.println(L);
		leaf = false;
		for(int i=0;i<L.size();i++)
		{
			System.out.println("Using this set of instance" + set_of_instances.get(i));
			if(MLASSid3.array[set_of_instances.get(i)][14]==0)
				neg++;
			else
				pos++;
		}
		entropy = calc_entropy();
		if(L.size()==1||L.size()==0)
			leaf = true;
		if(pos==L.size())
		{
			System.out.println("Leaf made with postive example");
			leaf = true;
			label = 1;  
		}
		if(neg==L.size())
		{
			System.out.println("Leaf made with negative example");
			leaf = true;
			label = 0;  
		}

		if(!leaf)
		{
			double max_infogain = -1000.0;
			int attr_value = 0;
			for(int i=0;i<Attr.size();i++)
			{
				double info = infogain(i);
				if(max_infogain<info)
				{
					attr_value = i;
					max_infogain = info;
				}
			}
			this.attribute_used = attr_value;
			if(max_infogain==0)
			{
				leaf = true;
				label = MLASSid3.array[set_of_instances.get(0)][14];

			}
			else
				generate_children(attr_value,max_infogain);
		}
	}

	// Generating Children
	public void generate_children(int attribute,double infogain_attr)
	{
		System.out.println("Inside the code if generating children with attribute :: " + attribute);
		ArrayList<Integer> newattrlist = new ArrayList<Integer>(this.Attr);
		if(newattrlist.size()==0){
			this.leaf = true;
			int x = 0;
			for(int i=0;i<set_of_instances.size();i++)
			{
				if(MLASSid3.array[set_of_instances.get(i)][14]==1)
					x++;
				else
					x--;
			}
			if(x>0)
				this.label = 1;
			else 
				this.label = 0;
		
			System.out.println("reducing no of attributes, hence this becomesa a leaf");
		}
		newattrlist.remove(attribute);
		System.out.println("No of possible values for this attribute" + infogain_attr);
		for(int i=0;i<MLASSid3.attr_possible_values[attribute];i++)
		{
			ArrayList<Integer> ar = new ArrayList<Integer>();
			for(int j = 0;j<set_of_instances.size();j++)
			{
				if(MLASSid3.array[set_of_instances.get(j)][attribute]==i)
				{
					ar.add(set_of_instances.get(j));
				}
			}
			System.out.println("Attribute selected ::"  + attribute);
			Node node = new Node(this,ar,this.depth,newattrlist);
			
			if(node != null)
				children.add(node);
		}
	}


	//Function to calculate entropy of present node 
	public double calc_entropy()
	{
		double a = this.pos;
		double b = this.neg;
		double pa = a / ((double) a + (double) b);
		double pb = b / ((double) a + (double) b);

			double res = 0;
			if (a > 0)
				res += -pa * Math.log(pa);
			if (b > 0)
				res += -pb * Math.log(pb);

			return res / Math.log(2);
	} 


	//Function to calculate entropy for information gain

	public double calc_entropy(double a,double b)
	{
		double pa = a / ((double) a + (double) b);
		double pb = b / ((double) a + (double) b);

			double res = 0;
			if (a > 0)
				res += -pa * Math.log(pa);
			if (b > 0)
				res += -pb * Math.log(pb);

			return res / Math.log(2);
	} 

	//Function to calculate Infogain for Attribute
	
	public double infogain(int x)
	{
		//System.out.println(x);
		int no_of_possible_values = MLASSid3.attr_possible_values[x];
		double a = 0.0;
		double b = 0.0;
		
		double infogain_value = entropy;
		for(int i=0;i<no_of_possible_values;i++)
		{
			double prob_possible_values = 0;
			for(int j=0;j<set_of_instances.size();j++)
			{
				if(MLASSid3.array[set_of_instances.get(j)][x]==i)//MLASSid3.array[L.get(i)][14]==0
				{
					prob_possible_values++;
					if(MLASSid3.array[set_of_instances.get(j)][14]==0)
						a++;
					else
						b++;
				}
			}
			infogain_value = infogain_value - (prob_possible_values/set_of_instances.size())*calc_entropy(a,b);

		}
		return infogain_value;
	}
	public double prune(double max_accuracy , Node root)
	{
		if(this.leaf==true)
		{
			return max_accuracy;
		}
		else
		{
			this.leaf = true;
			int x = 0;
			for(int i=0;i<set_of_instances.size();i++)
			{
				if(MLASSid3.array[set_of_instances.get(i)][14]==1)
					x++;
				else
					x--;
			}
			if(x>0)
				this.label = 1;
			else 
				this.label = 0;
			int ans=0;int caught_pos = 0;int total_pos = 0;
			for(int i=0;i<16281;i++)
			{
				ArrayList<Integer> test_Data = new ArrayList<Integer>(14);
				for(int j=0;j<14;j++)
				{
					test_Data.add(MLASSid3.array[i][j]);
					if(MLASSid3.array[i][14]==1)total_pos++;
				}

				if(root.classify(test_Data)==MLASSid3.array[i][14])
					ans++;
				
				if(root.classify(test_Data)==1)
					caught_pos++;

			}
			
			this.leaf = false;
			System.out.println(ans);
			
			if(ans==16281)
			{
				System.out.println("Depth__baby");
				System.out.println(this.depth);
			}
			double new_accuracy = (ans*100)/16281;
			if(new_accuracy>max_accuracy)
			{
				max_accuracy = new_accuracy;
				return max_accuracy;
			}
			else
			{
				this.leaf = false;
				
				for(int i=0;i<this.children.size();i++)
				{
					return this.children.get(i).prune(max_accuracy,root);
				}
			}
		}
		return max_accuracy;
	}
	// Classfying Function
	public int classify(ArrayList<Integer> test)
	{
		if(leaf==true)
			return label;
		return this.children.get(test.get(attribute_used)).classify(test);
	}
}


public class MLASSid3
{
	
    // instance variables - replace the example below with your own
    private int x;
    int test ;
     public static int array[][]  = new int[32562][15];

     // Enumeration to give integer value to string attribute
     public static int attr_possible_values[] = new int[14];
	 public enum workclass {Private, Self_emp_not_inc, Self_emp_inc, Federal_gov, Local_gov, State_gov, Without_pay, Never_worked}
	 public enum education {Bachelors, Some_college, eleventh, HS_grad, Prof_school, Assoc_acdm, Assoc_voc, ninth, seventh_eighth, tweleth, Masters, first_fourth, tenth, Doctorate, fifth_sixth, Preschool}
	 public enum marital_status {Married_civ_spouse, Divorced, Never_married, Separated, Widowed, Married_spouse_absent, Married_AF_spouse}
	 public enum occupation {Tech_support, Craft_repair, Other_service, Sales, Exec_managerial, Prof_specialty, Handlers_cleaners, Machine_op_inspct, Adm_clerical, Farming_fishing, Transport_moving, Priv_house_serv, Protective_serv, Armed_Forces}
     public enum relationship{Wife, Own_child, Husband, Not_in_family, Other_relative, Unmarried}
	 public enum race{White, Asian_Pac_Islander, Amer_Indian_Eskimo, Other, Black}
	 public enum sex {Female, Male}

	 public enum native_country{United_States, Cambodia, England, Puerto_Rico, Canada, Germany, Outlying_US, India, Japan, Greece, South, China, Cuba, Iran, Honduras, Philippines, Italy, Poland, Jamaica, Vietnam, Mexico, Portugal, Ireland, France, Dominican_Republic, Laos, Ecuador, Taiwan, Haiti, Columbia, Hungary, Guatemala, Nicaragua, Scotland, Thailand, Yugoslavia, El_Salvador, Trinadad_and_Tobago, Peru, Hong, Holand_Netherlands}
    /**	
     * Constructor for objects of class ID3_RUN
     */
    public  MLASSid3()
    {
        // initialise instance variables
        
        x = 0;
        test=0;
    }

    
    public static void datapre(String line,int i,int comma_no,int start,int data_no,int x)
    {

        String temp = line.substring(start,i);

		if(comma_no==0)
		{
			if(temp.equals("?"))
			{
				array[data_no][comma_no] = -1;
					return;
			}

			int num = Integer.parseInt(temp);
			array[data_no][comma_no] = num;
			attr_possible_values[comma_no] = 3;
		}
		
		if(comma_no==1)
		{
			if(temp.equals("?"))
			{
				array[data_no][comma_no] = -1;
					return;
			}
			
			temp = temp.replace('-','_');

			array[data_no][comma_no] = workclass.valueOf(temp).ordinal();

			attr_possible_values[comma_no] = 8;
			
		}
		
		
		if(comma_no==2)
		{
			if(temp.equals("?"))
			{
				array[data_no][comma_no] = -1;
					return;
			}

			int num = Integer.parseInt(temp);

			array[data_no][comma_no] = num;

			attr_possible_values[comma_no] = 3;
		}
		
		if(comma_no==3)
		{
			if(temp.equals("?"))
			{
				array[data_no][comma_no] = -1;
				return;
			}

			if(temp.equals("11th"))
			{
				temp = "eleventh";
			}
			if(temp.equals("9th"))
			{
				temp = "ninth";
			}
			if(temp.equals("7th-8th"))
			{
				temp = "seventh_eighth";
			}
			
			
			if(temp.equals("12th"))
			{
				temp = "tweleth";
			}
			
			
			if(temp.equals("1st-4th"))
			{
				temp = "first_fourth";
			}
			
			if(temp.equals("10th"))
			{
				temp = "tenth";
			}
			
			
			if(temp.equals("5th-6th"))
			{
				temp = "fifth_sixth";
			}
			temp = temp.replace('-','_');

			array[data_no][comma_no] = education.valueOf(temp).ordinal();
			attr_possible_values[comma_no] = education.values().length;
		}
		
		
		
		if(comma_no==4)
		{
			if(temp.equals("?"))
			{
				array[data_no][comma_no] = -1;
					return;
			}
			
			int num = Integer.parseInt(temp);

			array[data_no][comma_no] = num;
			attr_possible_values[comma_no] = 3;
		}
		
		else if(comma_no==5)
		{
			if(temp.equals("?"))
			{
				array[data_no][comma_no] = -1;
					return;
			}

			temp = temp.replace('-','_');
	
			array[data_no][comma_no] = marital_status.valueOf(temp).ordinal();
			attr_possible_values[comma_no] = marital_status.values().length;
		}
		if(comma_no==6)
		{
			if(temp.equals("?"))
			{
				array[data_no][comma_no] = -1;
					return;
			}

			temp = temp.replace('-','_');

			array[data_no][comma_no] = occupation.valueOf(temp).ordinal();
			attr_possible_values[comma_no] = occupation.values().length;
		}
		if(comma_no==7)
		{
			if(temp.equals("?"))
			{
				array[data_no][comma_no] = -1;
					return;
			}

			temp = temp.replace('-','_');

			array[data_no][comma_no] = relationship.valueOf(temp).ordinal();
			attr_possible_values[comma_no] = relationship.values().length;
			
		}
		if(comma_no==8)
		{
			if(temp.equals("?"))
			{
				array[data_no][comma_no] = -1;
				return;
			}

			temp = temp.replace('-','_');

			array[data_no][comma_no] = race.valueOf(temp).ordinal();
			attr_possible_values[comma_no] = race.values().length;
		}
		if(comma_no==9)
		{
			if(temp.equals("?"))
			{
				array[data_no][comma_no] = -1;
				return;
			}

			temp = temp.replace('-','_');

			array[data_no][comma_no] = sex.valueOf(temp).ordinal();
			attr_possible_values[comma_no] = sex.values().length;

		}
		if(comma_no==10)
		{
			if(temp.equals("?"))
			{
				array[data_no][comma_no] = -1;
				return;
			}

			int num = Integer.parseInt(temp);
			array[data_no][comma_no] = num;
			attr_possible_values[comma_no] = 3;
		}
		
		
		if(comma_no==11)
		{
			if(temp.equals("?"))
			{
				array[data_no][comma_no] = -1;
				return;
			}

			int num = Integer.parseInt(temp);
			array[data_no][comma_no] = num;
			attr_possible_values[comma_no] = 3;
		}
		if(comma_no==12)
		{
			if(temp.equals("?"))
			{
				array[data_no][comma_no] = -1;
				return;
			}

			int num = Integer.parseInt(temp);
			array[data_no][comma_no] = num;
			attr_possible_values[comma_no] = 3;
		}

		if(comma_no==13)
		{

		if(temp.equals("Outlying-US(Guam-USVI-etc)"))
		{
			temp = "Outlying_US";
		}
		if(temp.equals("Trinadad&Tobago"))
		{
			temp = "Trinadad_and_Tobago";
		}
			if(temp.equals("?"))
			{
				array[data_no][comma_no] = -1;
				return;
			}

			temp = temp.replace('-','_');
			array[data_no][comma_no] = native_country.valueOf(temp).ordinal();
			attr_possible_values[comma_no] = native_country.values().length;// Also try one v/s all over here especially for the case of india vs all
		}
		if(comma_no==14)
		{
			if(temp.equals(">50K"))
			{
				array[data_no][comma_no] = 1;
			}
			else
			{
				array[data_no][comma_no] = 0;
			}
		}
		
	}
	public static void missing_no_fill()
	{
	for(int i=0;i<13;i++)
	{
		int mini = array[0][i];
				int maxi = 0;
				int j;
				for( j=0;j<32561;j++)
				{
					if(array[j][i]==-1)
						continue;
					else{
						mini = Math.min(mini,array[j][i]);
						
						maxi = Math.max(maxi,array[j][i]);
					}
				}
				
				double tab1 = (mini+maxi)/2;
				int to_fill = (int)tab1;
				
				for( j=0;j<32561;j++)
				{
					if(array[j][i]!=-1)
						continue;
					else{
						array[j][i] = to_fill;
					}
				}
	}
			
	
	}

	public static void cont_datarep(){
	
		int arr[] = {0,2,4,10,11,12};

		for(int i=0;i<6;i++)
		{
			if(arr[i]==0)
			{
				for(int j=0;j<32561;j++)
			{
					if(array[j][arr[i]]==-1)
						continue;
					else{
						if(array[j][arr[i]]>=0&&array[j][arr[i]]<=30)
						{
							array[j][arr[i]] = 0;
						}
						
						if(array[j][arr[i]]>30&&array[j][arr[i]]<=60)
						{
							array[j][arr[i]] = 1;
						}
						
						
						if(array[j][arr[i]]>60)
						{
							array[j][arr[i]] = 2;
						}
						
					}
				}
			}
			
			if(arr[i]==2)// For array 2 we don't know how to proceed	
			{
				int mini = array[0][arr[i]];
				int maxi = 0;
				int j;
				for( j=0;j<32561;j++)
				{
					if(array[j][arr[i]]==-1)
						continue;
					else{
						mini = Math.min(mini,array[j][arr[i]]);
						
						maxi = Math.max(maxi,array[j][arr[i]]);
					}
				}
				
					double tab1 = Math.floor((maxi+mini)/3);
					double tab2 = 2*tab1;
				
				for( j=0;j<32561;j++)
				{
					if(array[j][arr[i]]>=0&&array[j][arr[i]]<=tab1)
						{
							array[j][arr[i]] = 0;
						}
						
						if(array[j][arr[i]]>tab1&&array[j][arr[i]]<=tab2)
						{
							array[j][arr[i]] = 1;
						}
						
						
						if(array[j][arr[i]]>tab2)
						{
							array[j][arr[i]] = 2;
						}
				}
				
						
			}
			
			if(arr[i]==4)
			{
				int j;
				for( j=0;j<32561;j++)
				{
					if(array[j][arr[i]]==-1)
						continue;
					else{
						if(array[j][arr[i]]>=0&&array[j][arr[i]]<=8)
						{
							array[j][arr[i]] = 0;
						}
						
						if(array[j][arr[i]]>8&&array[j][arr[i]]<=12)
						{
							array[j][arr[i]] = 1;
						}
						
						
						if(array[j][arr[i]]>12)
						{
							array[j][arr[i]] = 2;
						}
						
					}
				}
			}
			
			if(arr[i]==10) // we don't knwo for this also
			{
				int mini = array[0][arr[i]];
				int maxi = 0;
				int j;
				for( j=0;j<32561;j++)
				{
					if(array[j][arr[i]]==-1)
						continue;
					else{
						mini = Math.min(mini,array[j][arr[i]]);
						
						maxi = Math.max(maxi,array[j][arr[i]]);
					}
				}
				
					double tab1 = Math.floor((maxi+mini)/3);
					double tab2 = 2*tab1;
				
				for( j=0;j<32561;j++)
				{
					if(array[j][arr[i]]>=0&&array[j][arr[i]]<=tab1)
						{
							array[j][arr[i]] = 0;
						}
						
						if(array[j][arr[i]]>tab1&&array[j][arr[i]]<=tab2)
						{
							array[j][arr[i]] = 1;
						}
						
						
						if(array[j][arr[i]]>tab2)
						{
							array[j][arr[i]] = 2;
						}
				}
						
			}
		
			if(arr[i]==11) // we don;t knwonfor this also
			{

			int mini = array[0][arr[i]];
			int maxi = 0;
			int j;
				for( j=0;j<32561;j++)
				{
					if(array[j][arr[i]]==-1)
						continue;
					else{
						mini = Math.min(mini,array[j][arr[i]]);
						
						maxi = Math.max(maxi,array[j][arr[i]]);
					}
				}
				
					double tab1 = Math.floor((maxi+mini)/3);
					double tab2 = 2*tab1;
				
					for( j=0;j<32561;j++)
				{
					if(array[j][arr[i]]>=0&&array[j][arr[i]]<=tab1)
						{
							array[j][arr[i]] = 0;
						}
						
						if(array[j][arr[i]]>tab1&&array[j][arr[i]]<=tab2)
						{
							array[j][arr[i]] = 1;
						}
						
						
						if(array[j][arr[i]]>tab2)
						{
							array[j][arr[i]] = 2;
						}
				}
						
			}	
			
			if(arr[i]==12)
			{
				for(int j=0;j<32561;j++)
				{
					if(array[j][arr[i]]==-1)
						continue;
					else{
						if(array[j][arr[i]]>=0&&array[j][arr[i]]<=40)
						{
							array[j][arr[i]] = 0;
						}
						
						if(array[j][arr[i]]>40&&array[j][arr[i]]<=100)
						{
							array[j][arr[i]] = 1;
						}
						
						
						if(array[j][arr[i]]>100)
						{
							array[j][arr[i]] = 2;
						}
						
					}
				}
			}
		}
	
	}

	public static void data_preprocess(String file)
	{
		int no_of_instance = 32561;
        BufferedReader br;
        try {
          br = new BufferedReader(new FileReader(file)); 
			
			String line;
			int data_no = 0;
    while ((line = br.readLine()) != null) {
       int l = line.length();
       int start = 0;
       int end = 0;
       int comma_no = 0; 
       int x =0 ;
       for(int i=0;i<l;i++)
       {
           if(line.charAt(i)==',')
           {
               datapre(line,i,comma_no,start,data_no,3); // preprocess data
               comma_no++; // increasing the attribute
			   start = i+2;
			   if(comma_no==14)
				{
					datapre(line,l,14,start,data_no,3);
				}
           }
           
       }
     
	   cont_datarep();
	   	missing_no_fill();

		data_no++;
		System.out.println(data_no);
    
    }

		}// try ends over here
		catch(Exception e)
		{
			System.out.println("Error");

		}
	}// End of Data_preprocess
	
    public static void main(String args[])
    {
        data_preprocess("adult.data.txt");
        ArrayList<Integer> Attr= new ArrayList<Integer>(15);
        ArrayList<Integer> set_of_instance = new ArrayList<Integer>();
        for(int i = 0; i <14; i++){Attr.add(i);}
        for(int i = 0; i <= 32560; i++, set_of_instance.add(i));
	 	
	 	Node dummyNode = new Node();
     	Node root = new Node(dummyNode,set_of_instance,0,Attr);
		
		int array_rf[][] = new int[32761][15];
		
		for(int i = 0; i< 32562; i++){
			for (int j = 0; j < 15; j++){
				array_rf[i][j] = array	[i][j];
			}
		}
     	
     	data_preprocess("adult.test_ex.txt");
     	int ans = 0,total_pos=0,caught_pos=0;
     	for(int i=0;i<16281;i++)
     	{
     		ArrayList<Integer> test_Data = new ArrayList<Integer>(14);
     		for(int j=0;j<14;j++)
     		{
     			test_Data.add(array[i][j]);
				if(array[i][14]==1)total_pos++;
     		}

     		if(root.classify(test_Data)==array[i][14])
     			ans++;
			if(root.classify(test_Data)==1)
				caught_pos++;

     	}
		
     	double accuracy = (ans*100)/16281;
		//double recall = (caught_pos*100)/total_pos;
     	System.out.println(accuracy);
		for(int i=0;i<16281;i++){
			if(array[i][14]==1)total_pos++;
		}
		//System.out.println(total_pos);
		
		
		for(int i = 0; i< 32562; i++){
			for (int j = 0; j < 15; j++){
				int temp = array[i][j];
				array[i][j] = array_rf[i][j];
				array_rf[i][j] = temp;
			}
		}
		
		
		int size = 8000;
		int no_of_att = 7;
		ArrayList<Integer> Attrib = new ArrayList<Integer>();
		for(int i = 0; i <14; i++){Attrib.add(i);}
				
		int node_ans[][] = new int[16281][4];
		for(int p=0;p<4;p++)
		{
			System.out.println("Making tree");
			Collections.shuffle(set_of_instance);
			Collections.shuffle(Attrib);
			
			ArrayList<Integer> newtuple = new ArrayList<Integer>();
			ArrayList<Integer> new_attr_list = new ArrayList<Integer>();
			
			for(int i=0;i<size;i++)
			{
				newtuple.add(set_of_instance.get(i));
			}
			
			for(int i=0;i<no_of_att;i++)
			{
				new_attr_list.add(Attrib.get(i));
			}
			
			
			Node rf_root = new Node(dummyNode,newtuple,0,new_attr_list);
			
			for(int i=0;i<16281;i++)
			{
				ArrayList<Integer> test_Data = new ArrayList<Integer>(14);
				for(int j=0;j<14;j++)
				{
					test_Data.add(array_rf[i][j]);
				}
				for(int q = 0; q< 32562; q++){
			for (int j = 0; j < 15; j++){
				int temp = array[q][j];
				array[q][j] = array_rf[q][j];
				array_rf[q][j] = temp;
			}
		}
				node_ans[i][p] = rf_root.classify(test_Data);
				for(int q = 0; q< 32562; q++){
			for (int j = 0; j < 15; j++){
				int temp = array[q][j];
				array[q][j] = array_rf[q][j];
				array_rf[q][j] = temp;
			}
		}
				
			}
	}
		int rf_ans = 0;
		for(int i=0;i<16281;i++)
			{
				int x = 0;
				for(int j=0;j<4;j++)
				{
						x = x + node_ans[i][j];
				}
				if(x>=2)
					x = 1;
				else
					x = 0;
				if(x==array_rf[i][14])
					rf_ans++;
				
			}
			System.out.println("Random Forest Accuracy");
			System.out.println(rf_ans);
	 /*
		for(int i = 0; i< 32562; i++){
			for (int j = 0; j < 15; j++){
				int temp = array[i][j];
				array[i][j] = array_rf[i][j];
				array_rf[i][j] = temp;
			}
		}*/
	 System.out.println("Normal id3 ::" + accuracy);
	 double accracy_after_prunning =  root.prune(accuracy,root);
	 System.out.println(accracy_after_prunning);

	 
	 }
	 
}// End of class

