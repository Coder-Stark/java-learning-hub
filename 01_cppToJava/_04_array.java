//array

/*
in c++ 2 ways of array
int arr[size];                   //static (stack)
int *arr = new int[size];        //dynamic (heap)

in java 1 way possible (dynamic)
int []arr = new int [size];
int arr[] = new int [size];        //both are same
*/
public class _04_array {
    public static void main(String[] args){
        int []arr = new int[5];                 //it will auto matically initialize by 0
        arr[0] = 10;
        arr[1] = 20;
        arr[2] = 30;
        arr[3] = 40;
        arr[4] = 50;

        for(int val : arr){
            System.out.print(val + " ");            //10 20 30 40 50
        }
    }
}
//10 20 30 40 50
