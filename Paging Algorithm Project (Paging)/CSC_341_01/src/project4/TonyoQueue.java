package project4;

/**
 *
 * @author Tony Mendoza
 *
 */
public class TonyoQueue {

    protected TonyoSingleLinkedList list; // The single linked list that is used to store data

    // The default constructor initalizing some stuff.
    public TonyoQueue() {
        this.list = new TonyoSingleLinkedList();
    }

    // Adding data to the yQueue by importing an array
    public TonyoQueue(Object[] dataSet) {
        this.list = new TonyoSingleLinkedList();
        for (int i = 0; i < dataSet.length; i++) {
            offer((Object) dataSet[i]);
        }
    }

    // Offer as in, add
    public void offer(Object item) {
        list.addLast(item);
    }

    // Polling is like remove except no error is thrown
    public Object poll() {
        return (Object) list.removeFirst();
    }

    // Peeking is just looking at the first element without removing
    public Object peek() {
        return (Object) list.getNode(0).data;
    }

    public Object get(int index) {
        return (Object) list.getNode(index).data;
    }

    // Return the string value, which is all the elements together.
    @Override
    public String toString() {
        StringBuilder stb = new StringBuilder();
        for (int i = 0; i < list.getSize(); i++) {
            stb.append("[" + list.getNode(i).data + "]");
        }
        return stb.toString();
    }

    // Return the "physical" size of the linked list
    public int getSize() {
        return list.getSize();
    }

    public Object[] toArray() {
        return toArray(0);
    }

    public Object[] toArray(int capacity) {
        int s = getSize();
        if (capacity > s) {
            s = capacity;
        }
        Object[] array = new Object[s];

        int i = 0;
        Node node = list.head;
        while (node.next != null) {
            array[i] = node.next.data;
            i++;
            node = node.next;
        }

        return array;
    }
}
