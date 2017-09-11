package project4;

import java.util.Scanner;

/**
 *
 * @author Tony Mendoza
 */
public class project4 {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.println("=> Welcome to Project 4. Page fault stuff.\n");

        System.out.println("=> How many page references?");
        int a = input.nextInt(); // interpret user input for length of references
        System.out.println("=> How many pages?");
        int b = input.nextInt(); // interpret user input for length of actual pages
        System.out.println("=> How many (physical) frames?");
        int c = input.nextInt(); // interpret user input length of frames
        String reference = GenerateReferenceString(a, b);

        // Print generated reference string
        System.out.println("=> Reference string: " + reference + "\n");

        // Print page faults, and show each page fault created through the algorithm
        System.out.println("=> Total Page Faults (FIFO): " + FIFO(reference, c, true) + "\n");
        System.out.println("=> Total Page Faults (LRU): " + LRU(reference, c, true) + "\n");
        System.out.println("=> Total Page Faults (OPT): " + OPT(reference, c, true) + "\n");
        
        // Print only the amount of page faults for each algorithm for the maximum page to 1
        System.out.println("\n=> Comparing algorithms for frames decremnting from total amount of pages. Reference string: " + reference);
        for (int i = b; i >= 1; i--) {
            System.out.println("\t=> Frames: " + i);
            System.out.println("\t\t=> Total Page Faults (FIFO): " + FIFO(reference, i, false));
            System.out.println("\t\t=> Total Page Faults (LRU): " + LRU(reference, i, false));
            System.out.println("\t\t=> Total Page Faults (OPT): " + OPT(reference, i, false));
        }
    }

    // First In, Fisrt Out
    static int FIFO(String reference, int frameSize, boolean doPrint) {
        // print name of algorithm
        if (doPrint) {
            System.out.println("=> FIFO: " + reference);
        }

        int pageFaults = 0, i = 0, l = 0; // variables to keep track of algrotihm
        TonyoQueue frames = new TonyoQueue(); // stores frames in current page fault
        String[] refArray = reference.split(", "); // the reference string is split to create an array for easier traversal

        for (i = 0; i < refArray.length && i < frameSize + l;) { // loop for every page or until there is 4 unique pages saved in frames
            frames.offer(refArray[i]); // offer the unique reference to queue
            pageFaults++; // increment page faults
            // print current page fault (frame) 
            if (doPrint) {
                PrintFrames(frames.toArray(frameSize));
            }
            i++; // iterate

            // loop for every saved frame in the current page fault or until there is no more page references
            for (int j = 0; j < frames.getSize() && i < refArray.length; j++) {
                if (frames.get(j).equals(refArray[i])) { // if the frame is not unique, then look for the next unique reference.
                    l++; // keep searching
                    i++; // iterate
                    j = 0; // reset to compare for next reference array
                }
            }
        }

        for (i = frameSize; i < refArray.length; i++) { // loop for the rest of the reference string
            boolean exists = false; // set to determine if a reference exists inside the current page fault

            for (int j = 0; j < frameSize; j++) { // loop for every frame in the current page fault
                if (frames.get(j).equals(refArray[i])) { // if it is there, then it exists and the loop can break
                    exists = true;
                    break;
                }
            }

            if (!exists) { // if it does not exist
                frames.poll(); // remove the first frame
                frames.offer(refArray[i]); // add the next frame to the back
                pageFaults++; // increment page faults
            }
            if (doPrint) { // print current page fault
                PrintFrames(frames.toArray(frameSize));
            }
        }
        // return the amount of page faults
        return pageFaults;
    }

    // Least recenty used
    static int LRU(String reference, int frameSize, boolean doPrint) {
        // print name of algorithm
        if (doPrint) {
            System.out.println("=> LRU: " + reference);
        }

        int pageFaults = 0, i = 0, l = 0; // variables to keep track of algrotihm
        String[] frames = new String[frameSize]; // stores frames in current page fault
        TonyoStack stack = new TonyoStack(); // used to keep sorting in track
        String[] refArray = reference.split(", "); // the reference string is split to create an array for easier traversal

        for (i = 0; i < refArray.length && i < frameSize + l;) { // loop for every page or until there is 4 unique pages saved in frames
            frames[i - l] = refArray[i]; // set the unique reference
            stack.push(refArray[i]); // push reference onto stack
            pageFaults++; // increment page faults
            // print current page fault (frame)
            if (doPrint) {
                PrintFrames(frames);
            }
            i++; // iterate
            // loop for every saved frame in the current page fault or until there is no more page references
            // or until a null frame is met
            for (int j = 0; j < frames.length && frames[j] != null && i < refArray.length; j++) {
                if (frames[j].equals(refArray[i])) {  // if the frame is not unique, then look for the next unique reference.
                    stack.push(stack.remove(j)); // remove then push reference array from stack
                    l++; // keep searching
                    i++; // iterate
                    j = 0; // reset to compare for next reference array
                }
            }
        }

        for (i = frameSize + l; i < refArray.length; i++) { // loop for the rest of the reference string
            boolean exists = false; // set to determine if a reference exists 
            boolean add = true; // set to determine if it should be added
            
            for (int j = 0; j < stack.getSize(); j++) { // loop for every item in the stack
                if (stack.get(j).equals(refArray[i])) { // if item is in stack, then it exists and loop can break
                    exists = true;
                    break;
                }
            }
            for (int j = 0; j < frameSize; j++) { // loop for every frame in the current page fault
                if (frames[j].equals(refArray[i])) { // if it is there, then it should not be added and the loop can break
                    add = false;
                    break;
                }
            }

            if (!exists) { // if it does not exists
                stack.push(refArray[i]); // push reference onto stack
                add = true; // add it to the frames
            } else { // else
                // check where it is in the stack
                for (int j = 0; j < stack.getSize() && stack.get(j) != null; j++) {
                    if (stack.get(j).equals(refArray[i])) { // if found, after break
                        stack.push(stack.remove(j)); // then push it on the stack
                        break;
                    }
                }
            }
            if (add) { // if you can add
                // the string that needs to be removed will most likely have been pushed down the stack
                // at the top position - the size of the frames - 1
                String temp = (String) stack.get(stack.getSize() - frameSize - 1);
                for (int j = 0; j < frames.length; j++) { // loop through every frame of the current page fault
                    if (temp.equals(frames[j])) { // when it is found, break after
                        frames[j] = (String) stack.peek(); // set frame to current page reference
                        break;
                    }
                }
                pageFaults++; // increment page faults
                if (doPrint) { // print current page fault
                    PrintFrames(frames);
                }
            }
        }
        // return the amount of page faults
        return pageFaults;
    }

    // Optimal algorithm
    static int OPT(String reference, int frameSize, boolean doPrint) {
        // print name of algorithm
        if (doPrint) {
            System.out.println("=> OPT: " + reference);
        }

        int pageFaults = 0, i = 0, l = 0; // variables to keep track of algrotihm
        String[] frames = new String[frameSize]; // stores frames in current page fault
        String[] refArray = reference.split(", "); // the reference string is split to create an array for easier traversal
        int[] check = new int[frameSize]; //  used to keep track of upcoming frames

        for (i = 0; i < refArray.length && i < frameSize + l;) { // loop for every page or until there is 4 unique pages saved in frames
            frames[i - l] = refArray[i]; // set the unique reference 
            pageFaults++; // increment page faults
            check[i - l] = refArray.length; // set the check element to a default number
            for (int n = i + 1; n < refArray.length; n++) { // loop through the rest reference of the array
                if (refArray[i].equals(refArray[n])) { // if there is a next occurence
                    check[i - l] = n; // set the next occurence in the check array
                    break;
                }
            }
            i++; // iterate
            // loop for every saved frame in the current page fault or until there is no more page references
            for (int j = 0; j < frames.length && i < frames.length && i < refArray.length; j++) {
                if (frames[j] != null && frames[j].equals(refArray[i])) { // if the frame is not unique, then look for the next unique reference.
                    check[i - l] = refArray.length;  // set the check element to a default number
                    for (int n = i + 1; n < refArray.length; n++) { // loop through the rest of the reference array
                        if (refArray[i].equals(refArray[n])) { // if there is a next occurencea
                            break;
                        }
                            check[i - l] = n; // set the next occurence in the check array
                    }
                    l++; // keep searching
                    i++; // iterate
                    j = 0; // reset to compare for next reference array
                }
            }
            
            // print current page fault (frame) 
            if (doPrint) {
                PrintFrames(frames);
            }
        }

        for (i = frameSize + l; i < refArray.length; i++) { // loop for the rest of the reference string
            int replace = -1; // set the determine which frame in the page fault to replace
            boolean exists = false; // set to determine if a reference exists inside the current page fault

            for (int n = 0; n < frameSize; n++) { // loop for every frame in the current page fault
                if (frames[n].equals(refArray[i])) { // if it is there, then it exists and the loop can break
                    exists = true;
                    replace = n; // also set which frame should be replaced (itself)
                    break;
                } else if (replace == -1 || check[replace] < check[n]) { // 
                    replace = n; // set which frame should be replaced (itself)
                }
            }

            if (replace != -1) { // if there is a frame to replace
                if (!exists) { // but it doesn't exist
                    frames[replace] = refArray[i]; // replace it
                    pageFaults++; // increment page faults
                    if (doPrint) { // print current page fault
                        PrintFrames(frames);
                    }
                }
                check[replace] = refArray.length; // set the check element to a default number
                for (int n = i + 1; n < refArray.length; n++) { // loop through the rest of the reference array
                    if (refArray[i].equals(refArray[n])) { // if there is a next occurence, break after
                        check[replace] = n; // also set the next occurence in the check array
                        break;
                    }
                }
            }
        }
        // return the amount of page faults
        return pageFaults;
    }

    // Print frames
    static void PrintFrames(Object[] array) {
        System.out.print("\t=> Page Fault: [ ");
        for (int n = 0; n < array.length; n++) {
            if (array[n] != null) {
                if (Integer.parseInt((String) array[n]) < 10) {
                    System.out.print("0" + array[n]);
                } else {
                    System.out.print(array[n]);
                }
            } else {
                System.out.print("  ");
            }
            if (n < array.length - 1) {
                System.out.print(" | ");
            }
        }
        System.out.println(" ]");
    }

    static String GenerateReferenceString(int length, int pageSize) {
        String reference = "";
        for (int i = 0; i < length; i++) {
            reference += (int) (Math.random() * pageSize);
            if (i < length - 1) {
                reference += ", ";
            }
        }
        return reference;
    }
}
