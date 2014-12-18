//SE Formal Methods Project 
//--------------------------------------------
//Petrinet Markings Calculator
//By: Saoud Khalifah 
//--------------------------------------------

import java.util.*;

public class Petrinet{
  
  //All Markings found in the petrinet
  static String  sAllM[] = new String[50];
  //All the transistor associated with each marking in the petrinet, parallel to sAllM
  static int iAllT[] = new int[50];
  //Total Markings Found
  static int iM = 1;
  public static void main(String []args){
    Scanner in = new Scanner(System.in);
    //Input from the user is stored here
    int p, t, i[], o[], m0 = 0, mC;
    String s;
    String si[], so[], smC = "", sCurrentM = "";
    int c = 0;
    int current_m = 1;
    
    //Get input from user
     System.out.println("Number of places:\n");
     p = in.nextInt();
     System.out.println("Number of transitions:\n");
     t = in.nextInt();
     
     //Initialize new input arrays
     i = new int[t];
     o = new int[t];
     si = new String[t];
     so = new String[t];
     s = in.nextLine();
     //Loop the input to get INPUT and OUTPUT matrixes and look for errors
     for(int x = 0; x < t; x++){
       System.out.println("Enter I(t" + (x + 1) + "):\n");
       s = in.nextLine();
       if(s.length() > 0){
         i[x] = Integer.parseInt(s);
         si[x] = s;
       }
       if(s.length() != p){
         System.out.println("Error, reenter values.\n");
         x--;
         continue;
       }
       System.out.println("Enter O(t" + (x + 1) + "):\n");
       s = in.nextLine();
       if(s.length() > 0){
         o[x] = Integer.parseInt(s);
         so[x] = s;
       }
       if(s.length() != p){
         System.out.println("Error, reenter values.\n");
         x--;
         continue;
       }
     }
     
     //Loop input query for I and O of the petrinet
     while(true){
       System.out.println("Enter Initial Marking (M0):\n");
       s = in.nextLine();
       if(s.length() > 0){
         m0 = Integer.parseInt(s);
         smC = s;
       }
       if(s.length() != p){
         System.out.println("Error, reenter Initial Marking.\n");
         continue;
       }
       break;
     }
     
    //Current marking is the initial marking
    mC = m0;
    sAllM[c] = smC;
    iAllT[c] = -1;
    
    //Infinite loop where all markings are calculated and found.
    while(true){
      //No more markings found, exit loop
      if(sAllM[c] == null)
              break;
      
      sCurrentM = smC;
      
      //Loop through all transitions
      for(int f = 0; f < t; f++){
          	
    	  	  //Check if a transition is enabled to fire, and check if it was previously fired by the same transistor
              if(fires(sAllM[c], si[f]) && (iAllT[c] != f)){
            	  	  
            	  	  //Convert marking from string to int for calculation purposes
                      mC = intMarking(sAllM[c]);
                      //important calculation determining future of tokens in the petrinet
                      mC = ((mC - i[f])+o[f]);
                     //Convert integer marking to a string
                      smC = Integer.toString(mC);
                      smC = fillZeros(smC, p);
                      //Inherit any omegas in pervious marking state
                      smC = inheritOmega(sAllM[c], smC);
                      
                      current_m++;
                      //Check if marking has been added already to our array of markings
                      if(!existsAlready(smC)){
                              sAllM[iM] = smC;
                              //This is specially checking if a loop omega is occurring in the petrinet
                              if(c>0 && OmegaExists(sAllM[c], sAllM[iM])){
                            	  sAllM[iM] = addOmega(sAllM[c], sAllM[iM]);
                              }
                              iM++; //Increase total amount of markings found
                              //Add a parallel transistor to the marking found
                              //to indicate what transistor made the marking possible.
                              iAllT[iM-1] = f;
                      }

                      	
              }else if(fires(sAllM[c], si[f]) && (iAllT[c] == f)){
            	 //This is a special case if a transition is creating an omega in a connected place.
                 mC = intMarking(sAllM[c]);
                 mC = ((mC - i[f])+o[f]);
                 smC = Integer.toString(mC);
                 smC = fillZeros(smC, p);
                 smC = inheritOmega(sAllM[c], smC);
                 sCurrentM = sAllM[c];
                 //Check for omega again
                 if(fires(smC, si[f])){
                	 //Omega found.
                	 sAllM[c] = addOmega(sAllM[c], smC);
                	 sAllM[c] = inheritOmega(sCurrentM, sAllM[c]);
                 }else{
                	 //No omega
                     System.out.print("m" + current_m + "(" + smC + ")\n");
                     current_m++;
                     if(!existsAlready(smC)){
                    	 //Store new marking in the global array with omega
                             sAllM[iM] = smC;
                             //Increase total number of markings
                             iM++;
                             iAllT[iM-1] = f;
                     }
                 }
              }
      }
      //Increase 'c', keeps track of which marking is under calculation
      c++;
    }
    
    
    //Print out all the possible markings found.
    System.out.print("\n\nTotal Markings Found = " + iM + "\n");
    for(int uu = 0; uu < iM; uu++){
            System.out.print("m" + uu + "(" + sAllM[uu] + ")\n");
    }



  }
  
  //Inherit an omega from a previous marking to sync data.
  public static String inheritOmega(String m1, String m2){
	  String ret = "";
	  //Check m1 if there are omegas and check in if m2 if there is a match
	  //if not, inherit this omega in the new marking.
	  for(int x = 0; x < m1.length(); x++){
		  if(m1.charAt(x) == 'W')
			  ret = ret + "W";
		  else
			  ret = ret + m2.charAt(x);
	  }
	  return ret;
	  
  }
  
  //Convert marking to int, maintaining the omega and numbers without creating a binary number
  public static int intMarking(String s){
	  int ret = 0;
	  String sNew = "";
	  //Convert strng marking to an int
	  //This function takes consideration of omegas also
	  for(int i = 0;i < s.length(); i++){
		  if(s.charAt(i) == 'W')
			  sNew = sNew + "1";
		  else
			  sNew = sNew + s.charAt(i);
	  }
	  ret = Integer.parseInt(sNew);
	  return ret;
  }
  
  //Check if the marking exists in our global array.
  public static boolean existsAlready(String s){
          for(int i = 0; i < iM; i++){
                  if(sAllM[i].equals(s))
                          return true;
          }
          return false;
  }
  
  //Fill marking with zeros, this is to prevent an 'int' variable being interpreted as a binary number
  public static String fillZeros(String s, int p){
    String ret = s;
    for(int x = 0; x < (p - (s.length())); x++){
      ret = "0" + ret;
    }
    return ret;
  }
  
  //Important function that determines if a transition can be fired in the current marking state.
  public static boolean fires(String m, String i){
    int mm[] = new int[m.length()];
    int ii[] = new int[i.length()];
    for(int x = 0; x < m.length(); x++){
      //Check the marking against the input matrix
      //and if firing is possible at currrent transistor matrix.
      mm[x] = Character.digit(m.charAt(x), 10);
      ii[x] = Character.digit(i.charAt(x), 10);
      if(m.charAt(x) == 'W')
    	  mm[x] = 1;
      if(ii[x] == 0)
        continue;
      else if(ii[x] > mm[x])
        return false;
    }
    return true;
  }
  
  //Add omega to marking if its occurring in a certain place
  public static String addOmega(String m1, String m2){
	  int mm1[] = new int[m1.length()];
	  int mm2[] = new int[m2.length()];
	  String ret = "";
	  for(int x = 0; x < m1.length(); x++){
	      mm1[x] = Character.digit(m1.charAt(x), 10);
	      mm2[x] = Character.digit(m2.charAt(x), 10);
	      if(mm2[x] > mm1[x] && (mm2[x] != 1))
	    	  ret = ret + "W";
	      else
	    	  ret = ret + m2.charAt(x);
	  }
	  return ret;
	  
  }
  
  //Check if omega exists in the compared markings
  public static boolean OmegaExists(String m1, String m2){
	  int mm1[] = new int[m1.length()];
	  int mm2[] = new int[m2.length()];

	  for(int x = 0; x < m1.length(); x++){
	      mm1[x] = Character.digit(m1.charAt(x), 10);
	      mm2[x] = Character.digit(m2.charAt(x), 10);
	      if(mm1[x] == 1 || mm1[x] == 0)
	    	  continue;
	      if(mm2[x] > (mm1[x]))
	    	  return true;
	  }
	  return false;

	  
  }

}