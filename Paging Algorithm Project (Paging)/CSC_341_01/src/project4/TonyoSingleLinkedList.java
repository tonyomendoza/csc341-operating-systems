package project4;

/**
 *
 * @author Tony Mendoza
 */
public class TonyoSingleLinkedList {
    protected Node head; // Used for keeping the list intact
    protected int size; // The amount of elements in the list

    // default constructor
    public TonyoSingleLinkedList() {
        this.head = new Node  (-1); // create the head
        size = 0; // no elements in the list
    }

    // Add an element to be first in the list
    public void addFirst(Object item) {
        Node first = new Node(item);
        first.next = head.next;
        head.next = first;
        size++;
    }

    // Add an element after a given element
    public void addAfter(Node target, Object item) {
        if (size == 0) {
            addFirst(item);
            return;
        }
        Node insert = new Node(item);
        insert.next = target.next;
        target.next = insert;
        size++;
    }

    // add an element to the end of the list
    public void addLast(Object item) {
        if (size == 0) {
            addFirst(item);
            return;
        }
        addAfter(getNode(size - 1), item);
    }

    // retrieve a node at a specified index by iterating, O(n)
    public Node getNode(int index) {
        if (index < 0 || index > size) {
            return null;
        }
        Node iter = head;
        for (int i = 0; i <= index; i++) {
            iter = iter.next;
        }
        return iter;
    }

    // Remove the first node of a list, return its data
    public Object removeFirst() {
        if (size == 0) { // size 0 indicates no data in the linkedList
            return null;
        }
        Node removed = head.next;
        head.next = head.next.next;
        size--;
        return removed.data;
    }

    // print the list
    public void printList() {
        Node temp = head;
        System.out.print("Data: ");
        while (temp.next != null) {
            System.out.print(temp.next.data + "->");
            temp = temp.next;
        }
        System.out.println();
    }

    // Print the list in reverse
    public void printReverse() {
        System.out.print("Reverse-Data: ");
        printReverse(getNode(0));
    }

    // Print a linkededlist in reverse
    public void printReverse(Node head) {
        if (head.next == null) {
            return;
        }
        printReverse(head.next);
        System.out.print(head.next.data + "<-");
    }

    // Search through the  list to see if it exists inside
    public int indexOf(Object object) {
        Node nodeRef = head;
        int index = 0;
        while (nodeRef != null) {
            if (nodeRef.data == object) {
                return index;
            } else {
                index++;
            }
            nodeRef = nodeRef.next;
        }
        return - 1; // Return -1 if it doesn't exist inside the stack
    }

    // Remove the element after the referenced node
    public Object removeAfter(Node node) {
        // assign the next to the temporary node
        Node temp = node.next;
        // If there is a node left, then add the rest of the nodes to the referenced node.
        // Return the temporary node
        if (temp != null) {
            node.next = temp.next;
            size--;
            return temp.data;
        } else {
            return null;
        }
    }

    // Return the "physical" size of the array
    public int getSize() {
        return size;
    }
}