public class Tuong {
    public static void main(String[] args) throws Exception {
        Node head = new Node(1);
        head.next = new Node(2);
        head.next.next = new Node(3);
        while(head != null) {
            System.out.print(head.value + " ");
            head = head.next;
        }
    }
}
