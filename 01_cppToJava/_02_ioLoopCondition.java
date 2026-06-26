import java.util.*;

public class _02_ioLoopCondition{
    public static void main(String[] args){
        Scanner scn = new Scanner(System.in);                           //creating instance for taking input
    
        //integer input
        int n = scn.nextInt();
        System.out.println("Your input integer was : " + n);            //println (it will print in next line)
        // System.out.print("Hello world");                             //print (it  will print in same line)

        scn.nextLine();                                                 //clears this new line                                       //for executing below code

        //string input
        String s = scn.nextLine();                                      //taking input, it will print string with spaces
        // String s = scn.next();                                       //it will print string without spaces (first word)
        System.out.println("Your input string was : " + s);

        scn.close();  // close the scanner to avoid resource leak


        //for , while, do-while, break, continue, if, else, if-else, switch
        //all are same like in c++
    }
}
