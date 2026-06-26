import java.util.*;
public class _07_arrayList {
    public static void main(String[] args){
        ArrayList<Integer>list = new ArrayList<>();
        System.out.println(list + "-> " + list.size());
        
        //add or insert
        list.add(10);
        list.add(20);
        list.add(30);
        list.add(40);
        
        System.out.println(list + "-> " + list.size());
        
        //add at particular idx
        list.add(1, 1000);
        System.out.println(list + "-> " + list.size());
        
        //get value with index
        int val = list.get(1);
        System.out.println(val);

        //set value
        list.set(1, 2000);
        System.out.println(list + "-> " + list.size());
        
        //remove (using idx)
        list.remove(1);
        System.out.println(list + "-> " + list.size());

        
    }
}
/*
//add
[]-> 0
[10, 20, 30, 40]-> 4

//add at particular idx
[10, 1000, 20, 30, 40]-> 5

//get value 
1000

//set value or update value
[10, 2000, 20, 30, 40]-> 5

//remove (via idx)
[10, 20, 30, 40]-> 4
*/