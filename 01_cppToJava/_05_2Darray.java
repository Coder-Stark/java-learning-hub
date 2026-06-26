/*
in C++ 3 ways to create 2D array
1. int arr[2][3];             //stack (0-> 0,1,2.., 1-> 0, 1, 2,.., 2-> 0, 1, 2...)
2. int * arr[2];              //stack + heap  (array of pointers)
    arr[0] = new int[3];
    arr[1] = new int[4];         
3. int ** arr = new int *[2]   //heap       (array of pointers of pointers)

in Java only 1 way  (heap)
int [][] arr = new int[2][3];

//jagged or dynamic size 2D array
int [][] arr = new int[2][];          //initialise only 1 
  arr[0] = new int[3];
  arr[1] = new int[4];
*/

//rectangular array
/*
public class _05_2Darray {
    public static void main(String[] args){
        int [][]arr = new int [2][3];
        arr[0][0] = 11;
        arr[0][1] = 12;
        arr[0][2] = 13;
        arr[1][0] = 14;
        arr[1][1] = 15;
        arr[1][2] = 16;

        for(int i = 0 ; i < arr.length; i++){
            for(int j = 0; j < arr[i].length; j++){
                System.out.print(arr[i][j] + " ");
            }
            System.out.println();
        }
    }
}
*/
/*
11 12 13 
14 15 16
*/


//jagged array
public class _05_2Darray{
    public static void main(String [] args){
        int [][] arr = new int[2][];
        arr[0] = new int[3];
        arr[1] = new int[4];

        arr[0][0] = 10;
        arr[0][1] = 20;
        arr[0][2] = 30;
        arr[1][0] = 40;
        arr[1][1] = 50;
        arr[1][2] = 60;
        arr[1][3] = 70;

        for(int i = 0; i < arr.length; i++){
            for(int j = 0; j < arr[i].length; j++){
                System.out.print(arr[i][j] + " ");
            }
            System.out.println();
        }
    }
}
/*
10 20 30 
40 50 60 70
*/