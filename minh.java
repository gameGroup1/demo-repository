import java.util.Scanner;
public class minh {
    public static void game(){
        System.out.println("tung");
    }
    public static void main(String[]args){
        Scanner sc=new Scanner(System.in);
        String tuong=sc.next();
        int comp=sc.nextInt();
        if(comp==1){
            System.out.println("tuongdglo"+"-"+tuong);
            game();
        }
            else System.out.println("tuongkolo");
    }
}
