package project4;

/**
 *
 * @author Tony Mendoza
 */
// Used for nodes'y stuff. Or for the linked list...
public class Node {
    public Object data; // Data for this node
    public Node next; // the next node for this node (connecting)
    
    // Constructor to assign an object
    public Node(Object data){
         this.data = data;
         this.next = null;
                 }
    
    // Constructor to assign an object and a connecting node
    public Node(Object data, Node next){
        this.data = data;
        this.next = next;
    }
}