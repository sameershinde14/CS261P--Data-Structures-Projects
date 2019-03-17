package Core;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.logging.Logger;

public class RedBlackTree <T extends Comparable<T>> extends Tree<T>{
	private static final boolean RED   = true;
    private static final boolean BLACK = false;
    
    public RedBlackTree() {
    }
    public RedBlackTree(Logger log) {
    	this.LOGGER = log;
    }
    
    /***************************************************************************
     *  TreeNode helper methods.
     ***************************************************************************/
     // is node x red; false if x is null ?
     private boolean isRed(TreeNode<T> x) {
         if (x == null) return false;
         return x.color == RED;
     }

     // number of node in subtree rooted at x; 0 if x is null
     private int size(TreeNode<T> x) {
         if (x == null) return 0;
         return x.size;
     } 


     /**
      * Returns the number of key-value pairs in this symbol table.
      * @return the number of key-value pairs in this symbol table
      */
     public int size() {
         return size(root);
     }

    /**
      * Is this symbol table empty?
      * @return {@code true} if this symbol table is empty and {@code false} otherwise
      */
     public boolean isEmpty() {
         return root == null;
     }


    /***************************************************************************
     *  Standard BST search.
     ***************************************************************************/

     /**
      * Returns the value associated with the given key.
      * @param key the key
      * @return the value associated with the given key if the key is in the symbol table
      *     and {@code null} if the key is not in the symbol table
      * @throws IllegalArgumentException if {@code key} is {@code null}
      */
     public T get(T key) {
         if (key == null) throw new IllegalArgumentException("argument to get() is null");
         return get(root, key);
     }

     // value associated with the given key in subtree rooted at x; null if no such key
     private T get(TreeNode<T> x, T key) {
         while (x != null) {
             int cmp = key.compareTo(x.value);
             if      (cmp < 0) x = x.left;
             else if (cmp > 0) x = x.right;
             else              return x.value;
         }
         return null;
     }

     /**
      * Does this symbol table contain the given key?
      * @param key the key
      * @return {@code true} if this symbol table contains {@code key} and
      *     {@code false} otherwise
      * @throws IllegalArgumentException if {@code key} is {@code null}
      */
     public boolean contains(T key) {
         return get(key) != null;
     }

    /***************************************************************************
     *  Red-black tree insertion.
     ***************************************************************************/

     /**
      * Inserts the specified key-value pair into the symbol table, overwriting the old 
      * value with the new value if the symbol table already contains the specified key.
      * Deletes the specified key (and its associated value) from this symbol table
      * if the specified value is {@code null}.
      *
      * @param key the key
      * @param val the value
      * @throws IllegalArgumentException if {@code key} is {@code null}
      */
     public void add(T key) {
         if (key == null) throw new IllegalArgumentException("first argument to add() is null");
         long start = System.nanoTime();
         root = add(root,key);
         root.color = BLACK;
         long stop =  System.nanoTime();
         long elapsedTime = stop - start;
         noOfNodes++;
 		 LOGGER.info("SUCCESSFULLY INSERTED: SIZE= "+ noOfNodes +",TIME REQUIRED= "+ elapsedTime);
         // assert check();
     }

     // insert the key-value pair in the subtree rooted at h
     private TreeNode<T> add(TreeNode<T> h, T key) { 
         if (h == null) return new TreeNode<T>(key, RED, 1);

         int cmp = key.compareTo(h.value);
         if      (cmp < 0) h.left  = add(h.left,  key); 
         else if (cmp > 0) h.right = add(h.right, key); 
         else              h.value   = key;

         // fix-up any right-leaning links
         if (isRed(h.right) && !isRed(h.left))      h = rotateLeft(h);
         if (isRed(h.left)  &&  isRed(h.left.left)) h = rotateRight(h);
         if (isRed(h.left)  &&  isRed(h.right))     flipColors(h);
         h.size = size(h.left) + size(h.right) + 1;

         return h;
     }

    /***************************************************************************
     *  Red-black tree deletion.
     ***************************************************************************/

     /**
      * Removes the smallest key and associated value from the symbol table.
      * @throws NoSuchElementException if the symbol table is empty
      */
     public void deleteMin() {
         if (isEmpty()) throw new NoSuchElementException("BST underflow");

         // if both children of root are black, set root to red
         if (!isRed(root.left) && !isRed(root.right))
             root.color = RED;

         root = deleteMin(root);
         if (!isEmpty()) root.color = BLACK;
         noOfNodes--;
         // assert check();
     }

     // delete the key-value pair with the minimum key rooted at h
     private TreeNode<T> deleteMin(TreeNode<T> h) { 
         if (h.left == null)
             return null;

         if (!isRed(h.left) && !isRed(h.left.left))
             h = moveRedLeft(h);

         h.left = deleteMin(h.left);
         return balance(h);
     }


     /**
      * Removes the largest key and associated value from the symbol table.
      * @throws NoSuchElementException if the symbol table is empty
      */
     public void deleteMax() {
         if (isEmpty()) throw new NoSuchElementException("BST underflow");

         // if both children of root are black, set root to red
         if (!isRed(root.left) && !isRed(root.right))
             root.color = RED;

         root = deleteMax(root);
         if (!isEmpty()) root.color = BLACK;
         noOfNodes--;
         // assert check();
     }

     // delete the key-value pair with the maximum key rooted at h
     private TreeNode<T> deleteMax(TreeNode<T> h) { 
         if (isRed(h.left))
             h = rotateRight(h);

         if (h.right == null)
             return null;

         if (!isRed(h.right) && !isRed(h.right.left))
             h = moveRedRight(h);

         h.right = deleteMax(h.right);

         return balance(h);
     }

     /**
      * Removes the specified key and its associated value from this symbol table     
      * (if the key is in this symbol table).    
      *
      * @param  key the key
      * @throws IllegalArgumentException if {@code key} is {@code null}
      */
     public void remove(T key) { 
         if (key == null) throw new IllegalArgumentException("argument to delete() is null");
         if (!contains(key)) return;
         long start = System.nanoTime();
         // if both children of root are black, set root to red
         if (!isRed(root.left) && !isRed(root.right))
             root.color = RED;

         root = delete(root, key);
         if (!isEmpty()) root.color = BLACK;
         long stop = System.nanoTime();
         long elapsedTime = stop - start;
         noOfNodes--;
         LOGGER.info("SUCCESSFULLY REMOVED: SIZE= "+ noOfNodes +",TIME REQUIRED= "+ elapsedTime);
         // assert check();
     }

     // delete the key-value pair with the given key rooted at h
     private TreeNode<T> delete(TreeNode<T> h, T key) { 
         // assert get(h, key) != null;

         if (key.compareTo(h.value) < 0)  {
             if (!isRed(h.left) && !isRed(h.left.left))
                 h = moveRedLeft(h);
             h.left = delete(h.left, key);
         }
         else {
             if (isRed(h.left))
                 h = rotateRight(h);
             if (key.compareTo(h.value) == 0 && (h.right == null))
                 return null;
             if (!isRed(h.right) && !isRed(h.right.left))
                 h = moveRedRight(h);
             if (key.compareTo(h.value) == 0) {
                 TreeNode<T> x = min(h.right);
                 h.value = x.value;
                 // h.val = get(h.right, min(h.right).key);
                 // h.key = min(h.right).key;
                 h.right = deleteMin(h.right);
             }
             else h.right = delete(h.right, key);
         }
         return balance(h);
     }

    /***************************************************************************
     *  Red-black tree helper functions.
     ***************************************************************************/

     // make a left-leaning link lean to the right
     private TreeNode<T> rotateRight(TreeNode<T> h) {
         // assert (h != null) && isRed(h.left);
    	 long start = System.nanoTime();
         TreeNode<T> x = h.left;
         h.left = x.right;
         x.right = h;
         x.color = x.right.color;
         x.right.color = RED;
         x.size = h.size;
         h.size = size(h.left) + size(h.right) + 1;
         long stop = System.nanoTime();
         long elapsedTime = stop - start;
         LOGGER.info("SUCCESSFULLY ROTATE RIGHT: SIZE= "+ noOfNodes +",TIME REQUIRED= "+ elapsedTime);
         return x;
     }

     // make a right-leaning link lean to the left
     private TreeNode<T> rotateLeft(TreeNode<T> h) {
         // assert (h != null) && isRed(h.right);
    	 long start = System.nanoTime();
         TreeNode<T> x = h.right;
         h.right = x.left;
         x.left = h;
         x.color = x.left.color;
         x.left.color = RED;
         x.size = h.size;
         h.size = size(h.left) + size(h.right) + 1;
         long stop = System.nanoTime();
         long elapsedTime = stop - start;
         LOGGER.info("SUCCESSFULLY ROTATE LEFT: SIZE= "+ noOfNodes +",TIME REQUIRED= "+ elapsedTime);
         return x;
     }

     // flip the colors of a node and its two children
     private void flipColors(TreeNode<T> h) {
         // h must have opposite color of its two children
         // assert (h != null) && (h.left != null) && (h.right != null);
         // assert (!isRed(h) &&  isRed(h.left) &&  isRed(h.right))
         //    || (isRed(h)  && !isRed(h.left) && !isRed(h.right));
         h.color = !h.color;
         h.left.color = !h.left.color;
         h.right.color = !h.right.color;
     }

     // Assuming that h is red and both h.left and h.left.left
     // are black, make h.left or one of its children red.
     private TreeNode<T> moveRedLeft(TreeNode<T> h) {
         // assert (h != null);
         // assert isRed(h) && !isRed(h.left) && !isRed(h.left.left);

         flipColors(h);
         if (isRed(h.right.left)) { 
             h.right = rotateRight(h.right);
             h = rotateLeft(h);
             flipColors(h);
         }
         return h;
     }

     // Assuming that h is red and both h.right and h.right.left
     // are black, make h.right or one of its children red.
     private TreeNode<T> moveRedRight(TreeNode<T> h) {
         // assert (h != null);
         // assert isRed(h) && !isRed(h.right) && !isRed(h.right.left);
         flipColors(h);
         if (isRed(h.left.left)) { 
             h = rotateRight(h);
             flipColors(h);
         }
         return h;
     }

     // restore red-black tree invariant
     private TreeNode<T> balance(TreeNode<T> h) {
         // assert (h != null);
    	 long start = System.nanoTime();
         if (isRed(h.right))                      h = rotateLeft(h);
         if (isRed(h.left) && isRed(h.left.left)) h = rotateRight(h);
         if (isRed(h.left) && isRed(h.right))     flipColors(h);

         h.size = size(h.left) + size(h.right) + 1;
         long stop = System.nanoTime();
         long elapsedTime = stop - start;
         LOGGER.info("SUCCESSFULLY BALANCED: SIZE= "+ noOfNodes +",TIME REQUIRED= "+ elapsedTime);
         return h;
     }


    /***************************************************************************
     *  Utility functions.
     ***************************************************************************/

     /**
      * Returns the height of the BST (for debugging).
      * @return the height of the BST (a 1-node tree has height 0)
      */
     public int height() {
         return height(root);
     }
     private int height(TreeNode<T> x) {
         if (x == null) return -1;
         return 1 + Math.max(height(x.left), height(x.right));
     }

    /***************************************************************************
     *  Ordered symbol table methods.
     ***************************************************************************/

     /**
      * Returns the smallest key in the symbol table.
      * @return the smallest key in the symbol table
      * @throws NoSuchElementException if the symbol table is empty
      */
     public T min() {
         if (isEmpty()) throw new NoSuchElementException("calls min() with empty symbol table");
         return min(root).value;
     } 

     // the smallest key in subtree rooted at x; null if no such key
     private TreeNode<T> min(TreeNode<T> x) { 
         // assert x != null;
         if (x.left == null) return x; 
         else                return min(x.left); 
     } 

     /**
      * Returns the largest key in the symbol table.
      * @return the largest key in the symbol table
      * @throws NoSuchElementException if the symbol table is empty
      */
     public T max() {
         if (isEmpty()) throw new NoSuchElementException("calls max() with empty symbol table");
         return max(root).value;
     } 

     // the largest key in the subtree rooted at x; null if no such key
     private TreeNode<T> max(TreeNode<T> x) { 
         // assert x != null;
         if (x.right == null) return x; 
         else                 return max(x.right); 
     } 


     /**
      * Returns the largest key in the symbol table less than or equal to {@code key}.
      * @param key the key
      * @return the largest key in the symbol table less than or equal to {@code key}
      * @throws NoSuchElementException if there is no such key
      * @throws IllegalArgumentException if {@code key} is {@code null}
      */
     public T floor(T key) {
         if (key == null) throw new IllegalArgumentException("argument to floor() is null");
         if (isEmpty()) throw new NoSuchElementException("calls floor() with empty symbol table");
         TreeNode<T> x = floor(root, key);
         if (x == null) return null;
         else           return x.value;
     }    

     // the largest key in the subtree rooted at x less than or equal to the given key
     private TreeNode<T> floor(TreeNode<T> x, T key) {
         if (x == null) return null;
         int cmp = key.compareTo(x.value);
         if (cmp == 0) return x;
         if (cmp < 0)  return floor(x.left, key);
         TreeNode<T> t = floor(x.right, key);
         if (t != null) return t; 
         else           return x;
     }

     /**
      * Returns the smallest key in the symbol table greater than or equal to {@code key}.
      * @param key the key
      * @return the smallest key in the symbol table greater than or equal to {@code key}
      * @throws NoSuchElementException if there is no such key
      * @throws IllegalArgumentException if {@code key} is {@code null}
      */
     public T ceiling(T key) {
         if (key == null) throw new IllegalArgumentException("argument to ceiling() is null");
         if (isEmpty()) throw new NoSuchElementException("calls ceiling() with empty symbol table");
         TreeNode<T> x = ceiling(root, key);
         if (x == null) return null;
         else           return x.value;  
     }

     // the smallest key in the subtree rooted at x greater than or equal to the given key
     private TreeNode<T> ceiling(TreeNode<T> x, T key) {  
         if (x == null) return null;
         int cmp = key.compareTo(x.value);
         if (cmp == 0) return x;
         if (cmp > 0)  return ceiling(x.right, key);
         TreeNode<T> t = ceiling(x.left, key);
         if (t != null) return t; 
         else           return x;
     }

     /**
      * Return the key in the symbol table whose rank is {@code k}.
      * This is the (k+1)st smallest key in the symbol table. 
      *
      * @param  k the order statistic
      * @return the key in the symbol table of rank {@code k}
      * @throws IllegalArgumentException unless {@code k} is between 0 and
      *        <em>n</em>–1
      */
     public T select(int k) {
         if (k < 0 || k >= size()) {
             throw new IllegalArgumentException("argument to select() is invalid: " + k);
         }
         TreeNode<T> x = select(root, k);
         return x.value;
     }

     // the key of rank k in the subtree rooted at x
     private TreeNode<T> select(TreeNode<T> x, int k) {
         // assert x != null;
         // assert k >= 0 && k < size(x);
         int t = size(x.left); 
         if      (t > k) return select(x.left,  k); 
         else if (t < k) return select(x.right, k-t-1); 
         else            return x; 
     } 

     /**
      * Return the number of keys in the symbol table strictly less than {@code key}.
      * @param key the key
      * @return the number of keys in the symbol table strictly less than {@code key}
      * @throws IllegalArgumentException if {@code key} is {@code null}
      */
     public int rank(T key) {
         if (key == null) throw new IllegalArgumentException("argument to rank() is null");
         return rank(key, root);
     } 

     // number of keys less than key in the subtree rooted at x
     private int rank(T key, TreeNode<T> x) {
         if (x == null) return 0; 
         int cmp = key.compareTo(x.value); 
         if      (cmp < 0) return rank(key, x.left); 
         else if (cmp > 0) return 1 + size(x.left) + rank(key, x.right); 
         else              return size(x.left); 
     } 

    /***************************************************************************
     *  Range count and range search.
     ***************************************************************************/

     /**
      * Returns all keys in the symbol table as an {@code Iterable}.
      * To iterate over all of the keys in the symbol table named {@code st},
      * use the foreach notation: {@code for (T key : st.keys())}.
      * @return all keys in the symbol table as an {@code Iterable}
      */
     public Iterable<T> keys() {
         if (isEmpty()) return new LinkedList<T>();
         return keys(min(), max());
     }

     /**
      * Returns all keys in the symbol table in the given range,
      * as an {@code Iterable}.
      *
      * @param  lo minimum endpoint
      * @param  hi maximum endpoint
      * @return all keys in the sybol table between {@code lo} 
      *    (inclusive) and {@code hi} (inclusive) as an {@code Iterable}
      * @throws IllegalArgumentException if either {@code lo} or {@code hi}
      *    is {@code null}
      */
     public Iterable<T> keys(T lo, T hi) {
         if (lo == null) throw new IllegalArgumentException("first argument to keys() is null");
         if (hi == null) throw new IllegalArgumentException("second argument to keys() is null");

         Queue<T> queue = new LinkedList<T>();
         // if (isEmpty() || lo.compareTo(hi) > 0) return queue;
         keys(root, queue, lo, hi);
         return queue;
     } 

     // add the keys between lo and hi in the subtree rooted at x
     // to the queue
     private void keys(TreeNode<T> x, Queue<T> queue, T lo, T hi) { 
         if (x == null) return; 
         int cmplo = lo.compareTo(x.value); 
         int cmphi = hi.compareTo(x.value); 
         if (cmplo < 0) keys(x.left, queue, lo, hi); 
         if (cmplo <= 0 && cmphi >= 0) queue.add(x.value); 
         if (cmphi > 0) keys(x.right, queue, lo, hi); 
     } 

     /**
      * Returns the number of keys in the symbol table in the given range.
      *
      * @param  lo minimum endpoint
      * @param  hi maximum endpoint
      * @return the number of keys in the sybol table between {@code lo} 
      *    (inclusive) and {@code hi} (inclusive)
      * @throws IllegalArgumentException if either {@code lo} or {@code hi}
      *    is {@code null}
      */
     public int size(T lo, T hi) {
         if (lo == null) throw new IllegalArgumentException("first argument to size() is null");
         if (hi == null) throw new IllegalArgumentException("second argument to size() is null");

         if (lo.compareTo(hi) > 0) return 0;
         if (contains(hi)) return rank(hi) - rank(lo) + 1;
         else              return rank(hi) - rank(lo);
     }


    /***************************************************************************
     *  Check integrity of red-black tree data structure.
     ***************************************************************************/
     public boolean check() {
         if (!isBST())            LOGGER.severe("Not in symmetric order");
         if (!isSizeConsistent()) LOGGER.severe("Subtree counts not consistent");
         if (!isRankConsistent()) LOGGER.severe("Ranks not consistent");
         if (!is23())             LOGGER.severe("Not a 2-3 tree");
         if (!isBalanced())       LOGGER.severe("Not balanced");
         return isBST() && isSizeConsistent() && isRankConsistent() && is23() && isBalanced();
     }

     // does this binary tree satisfy symmetric order?
     // Note: this test also ensures that data structure is a binary tree since order is strict
     private boolean isBST() {
         return isBST(root, null, null);
     }

     // is the tree rooted at x a BST with all keys strictly between min and max
     // (if min or max is null, treat as empty constraint)
     // Credit: Bob Dondero's elegant solution
     private boolean isBST(TreeNode<T> x, T min, T max) {
         if (x == null) return true;
         if (min != null && x.value.compareTo(min) <= 0) return false;
         if (max != null && x.value.compareTo(max) >= 0) return false;
         return isBST(x.left, min, x.value) && isBST(x.right, x.value, max);
     } 

     // are the size fields correct?
     private boolean isSizeConsistent() { return isSizeConsistent(root); }
     private boolean isSizeConsistent(TreeNode<T> x) {
         if (x == null) return true;
         if (x.size != size(x.left) + size(x.right) + 1) return false;
         return isSizeConsistent(x.left) && isSizeConsistent(x.right);
     } 

     // check that ranks are consistent
     private boolean isRankConsistent() {
         for (int i = 0; i < size(); i++)
             if (i != rank(select(i))) return false;
         for (T key : keys())
             if (key.compareTo(select(rank(key))) != 0) return false;
         return true;
     }

     // Does the tree have no red right links, and at most one (left)
     // red links in a row on any path?
     private boolean is23() { return is23(root); }
     private boolean is23(TreeNode<T> x) {
         if (x == null) return true;
         if (isRed(x.right)) return false;
         if (x != root && isRed(x) && isRed(x.left))
             return false;
         return is23(x.left) && is23(x.right);
     } 

     // do all paths from root to leaf have same number of black edges?
     private boolean isBalanced() { 
         int black = 0;     // number of black links on path from root to min
         TreeNode<T> x = root;
         while (x != null) {
             if (!isRed(x)) black++;
             x = x.left;
         }
         return isBalanced(root, black);
     }

     // does every path from the root to a leaf have the given number of black links?
     private boolean isBalanced(TreeNode<T> x, int black) {
         if (x == null) return black == 0;
         if (!isRed(x)) black--;
         return isBalanced(x.left, black) && isBalanced(x.right, black);
     } 
}
