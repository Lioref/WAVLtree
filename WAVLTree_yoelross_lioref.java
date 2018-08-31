/*Project by: 
Name: Yoel Ross
ID: 200986701 
UserName: yoelross
Name: Liore Finkelstein
ID: 302313630
UserName: lioref 

*/

import java.util.ArrayList;
import java.util.List;


public class WAVLTree {
	public WAVLNode root;
	private int promoteCnt = 0;
	private int demoteCnt = 0;
	private int rotateCnt = 0;

	/**
	 * public WAVLTREE()
	 *
	 * constructs a tester.WAVLTree with a root node
	 * key and value are set to null
	 * external leaves are set as children
	 * rank is set to zero
	 */
	public WAVLTree()
	{
		// create empty root node
		WAVLNode tempRoot = new WAVLNode();
		// set as root
		this.root = tempRoot;
	}
	
 	 /**
 	* public boolean empty()
   	*
   	* returns true if and only if the tree is empty
   	*
	* complexity: O(1)
   	*/
	public boolean empty()
	  {
	   if (this.root.isRealNode()) {
	   	 	return false;
		} else {
	    	return true;
		}
	  }

	  /**
	   * public String search(int k)
	   *
	   * @param k key to search for
	   *
	   * returns the info of an item with key k if it exists in the tree
	   * otherwise, returns null
	   *
	   * wrapper for recursive function
	   *
	   * complexity: O(logn)
	   */
	public String search(int k)
	  {
	  	// if tree is empty, return null
	  	if (this.empty()) {return null;}
	  	// make recursive call
		WAVLNode res = this.search(k, this.root);

	  	if (res==null) {
	  		return null;
		} else {
	  		return res.value;
		}
	  }

	 /**
	 * public String recSearch(int k, IWAVLNode node)
	 * @param k key to search for
	 * @param node node to begin search from
	 * @return info of node with key==k, null if doesnt exist
	 * @precondition node is real
	 *
	 * complexity: O(h), where h is the hight of input node
	 */
	public WAVLNode search(int k, WAVLNode node)
	  {
	  	if (k==node.getKey()) {
			return node;
		} else if ((node.hasLeft()) && (k<node.key)) {
			return search(k, node.left);
		} else if ((node.hasRight()) && (k > node.key)) {
			return search(k, node.right);
		} else {
			return null;
		}
	  }

	/**
	 * wrapper function for recursive bst insert function (no rebalancing operations carried out)
 	 * @param k - key of node to be inserted
	 * @param i - value of nod to be inserted
	 * @return returns pointer to parent of inserted node
	 *
	 * complexity: O(logn)
	 */
 	public WAVLNode simpleInsert(int k, String i) {
	// if tree is empty, set root
		if (this.empty()) {
			this.root = new WAVLNode();
			this.root.toReal(k, i);
			return this.root;
		} else {
			return simpleInsert(k, i, this.root);
		}
	}

	/**
	 * inner recursive function for bst insert (no rebalancing)
	 * @param k - key of node to be inserted
	 * @param i - value of node to be inserted
	 * @param x - current node in search of insertion point
	 * @return pointer to inseted node
	 *
	 * complexity: O(h) where h is the height of x
	 */
	public WAVLNode simpleInsert(int k, String i, WAVLNode x) {
		// if key is allready in tree, return -1
		if (k==x.key) {
			return null;
		} else if (k<x.key && !x.left.real) {
			// turn x.left into a real node, and update key and value
			x.left.toReal(k, i);
			return x.left;
		} else if (k>x.key && !x.right.real) {
			// turn x.right into real node, and update key and value
			x.right.toReal(k, i);
			return x.right;
		} else if (k<x.key) { // recursive call to the left
			return simpleInsert(k, i, x.left);
		} else  { // recursive call to the right
			return simpleInsert(k, i, x.right);
		}
	}

	/**
	* public int insert(int k, String i)
	*
	* inserts an item with key k and info i to the WAVL tree. k must be non negative.
	* the tree must remain valid (keep its invariants).
	* returns the number of rebalancing operations, or 0 if no rebalancing operations were necessary.
	* returns -1 if an item with key k already exists in the tree.
	*
	* complexity: O(logn)
	*/
	public int insert(int k, String i) {
		// naive insert
		WAVLNode x = simpleInsert(k, i);

		// if key allready exists
		if (x==null) {
			return -1;
		} else {
			// rebalance
			insertRebalance(x);
			// sum rebalancing operations
			int totalrebalancing = rotateCnt + promoteCnt + demoteCnt;
			// reset counters
			rotateCnt = 0;
			promoteCnt = 0;
			demoteCnt = 0;
			return totalrebalancing;
		}
	}

	/**
	 * deletes tree root without rebalancing operations. used as utility for simpleDelete function.
	 * @return returns null, used as indicator for simpleDelete function.
	 * @precondition tree is not empty
	 *
	 * complexity: O(1)
	 */
	public WAVLNode simpleDeleteRoot() {
		// if root is leaf
		if (this.root.isLeaf()) {
			this.root.toExternal();
			return null;
		} else if (this.root.hasLeft() && !this.root.hasRight()) {  // if root has only left child
			WAVLNode newRoot = this.root.left; // save temp left child
			newRoot.parent = null; // set new root parent to null
			this.root.left = null; // disconnect old root from new
			this.root.nullifyPointers();
			this.root = newRoot; // set new root
			return null;


		} else if (!this.root.hasLeft() && this.root.hasRight()) { // if root has only right child
			WAVLNode newRoot = this.root.right; // save temp new root
			newRoot.parent = null; // make new roots parent null
			this.root.right = null; // disconnect old root from new
			this.root.nullifyPointers();
			this.root = newRoot;
			return null;

		} else { // root has two children
			// find succesor
			WAVLNode succNode = this.successor(this.root);
			WAVLNode succParent = succNode.parent;

			// save temps for key and value
			int tempKey = succNode.key;
			String tempVal = succNode.value;

			// delete succNode
			this.simpleDelete(succNode.key); // recursive call, should be of depth 1

			// exchange key and values
			this.root.key = tempKey;
			this.root.value = tempVal;
			return succParent;
		}
	}

	/**
	 * naive bst deletion
	 * @param k - key of node to delete
	 * @return returns parent of deleted node (returns null if deleted node is root by definition)
	 * @precondition given node must exist in tree
	 *
	 * complexity: O(logn) (uses search + constant number of operations)
	 */
	public WAVLNode simpleDelete(int k) {
		// find node to remove
		WAVLNode remNode = search(k, this.root);
		WAVLNode remParent = remNode.parent; // this will fail is node doesn't exit!

		// if node doesn't exist in tree, return -1
		if (remNode==this.root) { // if remNode is root, use dedicated deletion
			WAVLNode retNode = this.simpleDeleteRoot();
			return retNode;
		} else {
			if (remNode.isLeaf()) { // if node is a leaf, make external
				remNode.toExternal();
			} else if (remNode.hasLeft() && !remNode.hasRight()) { //has only left child
				remNode.left.parent = remNode.parent; // bridge - from rm child, to rm parent
				if (remNode==remNode.parent.left) {
					remNode.parent.left = remNode.left; // A: bridge from rm parent, to rm child
				} else {
					remNode.parent.right = remNode.left; // B: bridge from rm parent to rm child
				}
				remNode.nullifyPointers();
			} else if (!remNode.hasLeft() && remNode.hasRight()) { // has only right child
				remNode.right.parent = remNode.parent; // bridge - from rm child to rm parent
				if (remNode==remNode.parent.left) {
					remNode.parent.left = remNode.right; // C: bridge from rm parent to rm child
				} else {
					remNode.parent.right = remNode.right; // D: bridge from rm parent to rm child
				}
				remNode.nullifyPointers();

			} else if (remNode.hasLeft() && remNode.hasRight()) {  // has two children - complex case
				// find minimal node of rmnode right subtree
				WAVLNode succNode = this.successor(remNode);
				WAVLNode succParent = succNode.parent;

				// save succNode key and val to temp variables (transfer to remnode after delete)
				int tempKey = succNode.key;
				String tempVal = succNode.value;

				// erase succNode - simple case
				this.simpleDelete(succNode.key); // recursion depth will only be 1

				// update remnodes key and value
				remNode.key = tempKey;
				remNode.value = tempVal;

				// return point to succParent
				return succParent;
			}
		return remParent;

		}

	}

	/**
	* public int delete(int k)
	*
    * deletes an item with key k from the binary tree, if it is there;
    * the tree must remain valid (keep its invariants).
    * returns the number of rebalancing operations, or 0 if no rebalancing operations were needed.
    * returns -1 if an item with key k was not found in the tree.
	*
	* complexity: O(logn) - (search + constant deletion + logn rebalancing + logn size updates)
    */
    public int delete(int k) {
		// check if tree is empty
		if (this.empty()) {return -1;}

    	// check if key is in tree
		WAVLNode remNode = search(k, this.root);

		// delete and rebalance where necessary
		if (remNode!=null && remNode.parent!=null) { // remnode is in tree and is not root
			WAVLNode remParent = simpleDelete(k);
			deleteRebalance(remParent);
		} else if (remNode!=null && remNode.parent==null) { // remnode is root of tree
			WAVLNode remParent = simpleDeleteRoot(); // should be null!
			if (!(remParent==null)) {
				deleteRebalance(remParent); // if root was deleted, and had two children, rebalancing is needed
			}
		} else { // k not in tree
			return -1;
		}

		// sum rebalancing counters
		int totalRebalancing = rotateCnt + promoteCnt + demoteCnt;
		// reset rebalancing counters
		rotateCnt = 0;
		promoteCnt = 0;
		demoteCnt = 0;

	   	return totalRebalancing;
	   }

   /**
	* public String min()
	*
	* Returns the info of the item with the smallest key in the tree,
	* or null if the tree is empty
	*
	* complexity: O(logn)
	*/
   public String min()
   {
	   if (this.empty()) {
		   return null;
	   }
	   else {
		   WAVLNode x = this.root;
		   while (x.left.isRealNode()) {
			   x = x.left;
		   }
		   return (x.value);
	   }
   }

	/**
	 * public WAVLNode min(WAVLNode x)
	 *
	 * Returns the node with the smallest key in the tree,
	 * or null if the tree is empty
	 *
	 * complexity: O(h) where h is the height of x
	 */
	public WAVLNode min(WAVLNode x)
	{
		if (!x.isRealNode()) {
			return null;
		}
		else {
			while (x.left.isRealNode()) {
				x = x.left;
			}
			return (x);
		}
	}

	/**
	 * public String max()
	 *
	 * Returns the info of the item with the largest key in the tree,
	 * or null if the tree is empty
	 *
	 *
	 * complexity: O(logn)
	 */
	public String max()
	{
		if (this.empty()) {
			return null;
		}
		else {
			WAVLNode x = this.root;
			while (x.right.isRealNode()) {
				x = x.right;
			}
			return (x.value);
		}
	}

	/**
	 * public WAVLNode max(WAVLNode x)
	 *
	 * Returns the node with the largest key in the tree,
	 * or null if the tree is empty
	 *
	 * complexity: O(h) where h is the height of x
	 */
	public WAVLNode max(WAVLNode x)
	{
		if (!x.isRealNode()) {
			return null;
		}
		else {
			while (x.right.isRealNode()) {
				x = x.right;
			}
			return (x);
		}
	}


	/**
	 * public int[] keysToArray()
	 *
	 * Returns a sorted array which contains all keys in the tree,
	 * or an empty array if the tree is empty.
	 *
	 * complexity: O(n)
	 */
	public int[] keysToArray()
	{
		List<WAVLNode> nodes = this.sortedOrder();
		int[] res = new int[nodes.size()];
		if (nodes.size() > 0) {
			for (int i = 0 ; i < nodes.size() ; i++) {
				res[i] = nodes.get(i).key;
			}
		}
		return res;
	}


	/**
	 * public String[] infoToArray()
	 *
	 * Returns an array which contains all info in the tree,
	 * sorted by their respective keys,
	 * or an empty array if the tree is empty.
	 *
	 * * complexity: O(n)
	 */
	public String[] infoToArray()
	{
		List<WAVLNode> nodes = this.sortedOrder();
		String[] res = new String[nodes.size()];
		if (nodes.size() > 0) {
			for (int i = 0 ; i < nodes.size() ; i++) {
				res[i] = nodes.get(i).value;
			}
		}
		return res;
	}


	/**
	 * public int size()
	 *
	 * Returns the number of nodes in the tree.
	 *
	 * precondition: none
	 * postcondition: none
	 *
	 * complexity: O(1) (sizes are maintained in log time during insertion and deletion)
	 */
	public int size() {
		if (this.root.isRealNode()) {
			return (this.root.subtreeSize);
		}
		return 0;
	}

	/**
	 * public List<WAVLNode> sortedOrder()
	 *
	 *Wrapper function: Returns A list of Nodes in tester.WAVLTree in Order (sorted)
	 * precondition: none
	 * postcondition: none
	 *
	 * complexity: O(n)
	 */
	public List<WAVLNode> sortedOrder() {
		WAVLNode x = this.root;
		List<WAVLNode> Order = new ArrayList<WAVLNode>();
		return sortedOrder(Order , x);
	}

	/**
	 * public List<WAVLNode> sortedOrder()
	 *
	 *Returns A list of Nodes in tester.WAVLTree in Order (sorted)
	 * precondition: none
	 * postcondition: none
	 *
	 * complexity: O(n)
	 */
	public List<WAVLNode> sortedOrder(List<WAVLNode> Order , WAVLNode x) {
		if (x.isRealNode()) {
			sortedOrder(Order, x.left);
			Order.add(x);
			sortedOrder(Order, x.right);
		}
		return Order;
	}

	/**
	 * public int getRoot()
	 *
	 * Returns the root WAVL node, or null if the tree is empty
	 *
	 * precondition: none
	 * postcondition: none
	 *
	 * complexity: O(1)
	 */
	public IWAVLNode getRoot()
	   {
		   if (this.empty()) {
		   	return null;
		   } else {
		   	return this.root;
		   }
	   }

	/**
	 * public int select(int i)
	 *
	 * Returns the value of the i'th smallest key (return -1 if tree is empty)
	 * Example 1: select(1) returns the value of the node with minimal key
	 * Example 2: select(size()) returns the value of the node with maximal key
	 * Example 3: select(2) returns the value 2nd smallest minimal node, i.e the value of the node minimal node's successor
	 *
	 * precondition: size() >= i > 0
	 * postcondition: none
	 *
	 * complexity: O(logn)
	*/
	public String select(int i) {
		// if i is greater than tree size, return -1
		if ((i>this.root.subtreeSize) || this.empty() || i==0) {
			return null;
		} else {
			WAVLNode iNode = select(this.root, i);
			return iNode.value;
		}
	}

	/**
	 * inner recursive function for select
	 * @param x node to start searching from
	 * @param i ith smallest k to be found
	 * @return node whose key is the i smallest in tree
	 *
	 * complexity: O(h) where h is the height of x
	 */
	public WAVLNode select(WAVLNode x, int i) {
		int r = x.left.subtreeSize;
		if ((i)==r+1) {
			return x;
		} else if (i<r+1) {
			return select(x.left, i);
		} else {
			return select(x.right, i-r-1);
		}
	}

	/**
	 *
	 * @param x
	 * @return succesor of given node. returns null if none exists
	 *
	 * complexity: O(logn)
	 */
	public WAVLNode successor(WAVLNode x) {
		if (x.hasRight()) {
			return this.min(x.right);
		} else {
			WAVLNode p = x.parent;
			while (p!=null && x==p.right) {
				x = p;
				p = x.parent;
			}
		return p;
		}
	}

	/**
	 *
	 * @param x node to find predecessor of
	 * @return predecessor node, null if doesn't exist
	 *
	 * complexity: O(logn)
	 */
	public WAVLNode predecessor(WAVLNode x) {
		if (x.left!=null) {
			return this.max(x.left);
		} else {
			WAVLNode p = x.parent;
			while (p!=null & x==p.left) {
				x = p;
				p = x.parent;
			}
			return p;
		}
	}

	/**
	 * preforms left rotation on node x
	 * @param x node to start rotation at
	 * @precondition x=x.parent.right
	 *
	 * complexity: O(1)
	 *
	 */
	public void rotateLeft(WAVLNode x) {
		WAVLNode p = x.parent;
		// rotate
		p.right = x.left; // move x left subtree to p's right
		p.right.parent = p; // update x left subtree parent to p
		x.left = p; // make p the left subtree of x

		// if parent is root
		if (p==this.root) {
			this.root = x;
			this.root.parent = null;
			p.parent = x; // make x ps parent
		} else { // if parent is not root
			x.parent = p.parent;
			if (p==p.parent.left) {
				p.parent.left = x; // if p is left child
			} else {
				p.parent.right = x; // if p is right child
			}
			p.parent = x; // make x ps parent
		}
		rotateCnt++;
	}

	/**
	 * preforms right rotation on node x
	 * @param x node to start rotation at
	 * @precondition x=x.parent.left
	 *
	 * complexity: O(1)
	 */
	public void rotateRight(WAVLNode x) {
		WAVLNode p = x.parent;
		// rotate
		p.left = x.right; // set x right subtree to be P left subtree
		p.left.parent = p; // modify x.right parent to be p
		x.right = p; // make pe x.right

		// if parent is root
		if (p==this.root) {
			this.root = x;
			x.parent = null;
			p.parent = x;
		} else {
			x.parent = p.parent;
			if (p==p.parent.left) { // if p is left child
				p.parent.left = x;
			} else {
				p.parent.right = x; // if p is right child
			}
			p.parent = x; // make ps parent x
		}
		rotateCnt++;
	}

	/**
	 * @param x node to start double rotation at
	 * @precondition - x is not the root
	 * @precondition - x.parent is not the root
	 * @post - if x=x.parent.right, rotation is left,right. otherwise, right,left.
	 *
	 * complexity: O(1)
	 */
	public void doubleRotate(WAVLNode x) {
		if (x==x.parent.right) {
			this.rotateLeft(x);
			this.rotateRight(x);
		} else {
			this.rotateRight(x);
			this.rotateLeft(x);
		}
	}

	/**
	 * rebalancing function, preforms necessary rebalacing after insertion.
	 * @param x inserted node.
	 *
	 * complexity: O(logn) - (search + rebalancing + size update)
	 */
	public void insertRebalance(WAVLNode x) {

		if (x.parent != null && !x.parent.isZeroOneNode()) {
			//nodeToRootSizeInc(x); // walk up the tree, and increase sizes of subtrees
		} else {
			while ((x.parent != null) && x.parent.isZeroOneNode()) { //Promotion loop
				x.parent.promoteRank();
				x.parent.increaseSubtreeSize(); //Increases subtreeSize from leaf to rotate point
				x = x.parent;
			}
		}

		if (x.parent != null && x.rankDifference()==0){
			if (x == x.parent.left) { //Case one - x is Left child
				WAVLNode y = x.right;
				WAVLNode z = x.parent;
				if ((!y.isRealNode()) || (y.rankDifference() == 2)) { //Case A
					rotateRight(x);
					updateSubtreeSize(x);
					z.demoteRank();
					nodeToRootSizeInc(x);
				}
				else if (y.rankDifference() == 1) {  //Case B
					doubleRotate(y);
					updateSubtreeSize(y);
					y.promoteRank();
					x.demoteRank();
					z.demoteRank();
					nodeToRootSizeInc(y);
				}
			}
			else if (x==x.parent.right) { //Case two - x is Right child
				WAVLNode y = x.left;
				WAVLNode z = x.parent;
				if ((!y.isRealNode()) || (y.rankDifference() == 2)) {//Case A
					rotateLeft(x);
					updateSubtreeSize(x);
					z.demoteRank();
					nodeToRootSizeInc(x);
				}
				else if (y.rankDifference() == 1) {//Case B
					doubleRotate(y);
					updateSubtreeSize(y);
					y.promoteRank();
					x.demoteRank();
					z.demoteRank();
					nodeToRootSizeInc(y);
				}
			}
		} else {
			nodeToRootSizeInc(x);
		}
	}

	/**
	 * rebalancing function, called from delete method, performs necessary rebalacing after deletion.
	 * @param x deleted node's parent.
	 *
	 * complexity: O(logn) - (search + rebalancing + size update)
	 */
	public void deleteRebalance(WAVLNode x) throws NullPointerException{
		if (x.isTwoTwoNode() && (x.isLeaf())) { // Two-two leaf violation
			x.demoteRank();
			x.decreaseSubtreeSize();
			x = x.parent; // in this case, the three child is x. adjust x to parent, so that next block is generic

		}
		if (x!=null && x.hasThreeChild()) { // 3-Child violation
			x = x.getThreeChild();
			WAVLNode y = x.getSibling();
			while ((x.rankDifference() == 3) && ((y.rankDifference() ==2) || (y.isTwoTwoNode()))) {
				if (y.rankDifference() == 2) {
					x.parent.demoteRank();
					x.parent.decreaseSubtreeSize();
				} else {
					y.demoteRank();
					x.parent.demoteRank();
				}
				x = x.parent;
				if (x == this.root) {
					break;
				} else {
					y = x.getSibling();
				}
			}
			// while should either fix rank rule, or we still have three child
			if (x!=null && (x.rankDifference() == 3)) { //Rank rule does not hold: x is a 3-child and y is not 2-2 node
				WAVLNode z = x.parent;
				WAVLNode v = y.left;
				WAVLNode w = y.right;
				if (x == x.parent.left) { //Case 1: x is the left (3)child
					if (w.rankDifference() == 1) {
						rotateLeft(y);
						updateSubtreeSize(y);
						y.promoteRank();
						z.demoteRank();
						if (z.isLeaf()) {
							z.demoteRank();
						}
						nodeToRootSizeDec(y);
					}
					else { //w.rankDifference() == 2, y has no external children
						doubleRotate(v);
						updateSubtreeSize(v);
						v.promoteRank();
						v.promoteRank();
						y.demoteRank();
						z.demoteRank();
						z.demoteRank();
						nodeToRootSizeDec(v);
					}
				}
				else { //Case 2: x is the right (3)child
					if (v.rankDifference() == 1) {
						rotateRight(y);
						updateSubtreeSize(y);
						y.promoteRank();
						z.demoteRank();
						if (z.isLeaf()) {
							z.demoteRank();
						}
						nodeToRootSizeDec(y);
					}
					else { //v.rankDifference == 2
						doubleRotate(w);
						updateSubtreeSize(w);
						w.promoteRank();
						w.promoteRank();
						y.demoteRank();
						z.demoteRank();
						z.demoteRank();
						nodeToRootSizeDec(w);
					}

				}
			} else if (x!=null) {
				updateSubtreeSize(x);
				nodeToRootSizeDec(x); // if no corrections were made, decrease back to root
			}
		} else if (x!=null) {
			updateSubtreeSize(x);
			nodeToRootSizeDec(x); // if no corrections were made, decrease back to root
		}
	}

	/**
	 * walks up the tree from node x (exclusive) and increases x.parent by 1
	 * @param x
	 *
	 * complexity: O(logn)
	 */
	public void nodeToRootSizeInc(WAVLNode x) {
		WAVLNode xPar = x.parent;

		while (xPar!=null) {
			xPar.increaseSubtreeSize();
			xPar = xPar.parent;
		}
	}

	/**
	 * walks up the tree from x (exclusive) and decreases x.parent by 1
	 * @param x
	 *
	 * complexity: O(logn)
	 */
	public void nodeToRootSizeDec(WAVLNode x) {
		x = x.parent;
		while (x!=null) {
			x.decreaseSubtreeSize();
			x = x.parent;
		}
	}

	/**
	 * update subtree sizes of nodes involved in rotation
	 * @param x
	 *
	 * complexity: O(1)
	 */
	public void updateSubtreeSize(WAVLNode x) {
		// if x is leaf
		if (x.isLeaf()) {
			x.subtreeSize = 1;
		} else if (x.hasLeft() && !x.hasRight()) { // if x has only left child
			x.left.subtreeSize = x.left.left.subtreeSize + x.left.right.subtreeSize + 1;
			x.subtreeSize = x.left.subtreeSize + 1;
		} else if (!x.hasLeft() && x.hasRight()) { // if x has only right child
			x.right.subtreeSize = x.right.right.subtreeSize + x.right.left.subtreeSize + 1;
			x.subtreeSize = x.right.subtreeSize + 1;
		} else { //x has two real children
			x.left.subtreeSize = x.left.left.subtreeSize + x.left.right.subtreeSize +1;
			x.right.subtreeSize = x.right.left.subtreeSize + x.right.right.subtreeSize +1;
			x.subtreeSize = x.left.subtreeSize + x.right.subtreeSize +1;
		}
	}


	// INNER CLASS - WAVLNODE
	public class WAVLNode implements IWAVLNode {
		public int key;
		public String value;
		public WAVLNode parent = null;
		public WAVLNode left = null;
		public WAVLNode right = null;
		public Integer rank = null;
		public int subtreeSize;
		public boolean real;

		/**
		 *
		 * @param key
		 * @param value
		 *
		 * constructs a WAVL node with given key and value
		 * external set to false
		 */

		public WAVLNode(Integer key, String value)
		{
			this.key = key;
			this.value = value;
			this.real = true;
			this.subtreeSize = 1;
		}

		/**
		 * construct an external wavl node
		 * key and value are set to null
		 * rank is set to -1
		 */

		public WAVLNode()
		{
			this.value = null;
			this.real = false;
			this.rank = -1;
			this.key = -1;
			this.subtreeSize = 0;

		}

		public IWAVLNode getLeft()
		{
			return this.left;
		}

		public IWAVLNode getRight()
		{
			return this.right;
		}

		public int getSubtreeSize()
		{
			return this.subtreeSize;
		}

		public int getKey() {return this.key;}

		public String getValue() {return this.value;}

		public boolean isRealNode() {return this.real;}

		/**
		 * @precondition leaves have not real children
		 * @return true if node has real left child
		 *
		 * complexity: O(1)
		 */
		public boolean hasLeft()
		{
			return this.left.isRealNode();

		}

		/**
		 * @precondition leaves have not real children
		 * @return true if node has real rigth child
		 *
		 * complexity: O(1)
		 */
		public boolean hasRight()
		{
			return this.right.isRealNode();
		}

		/**
		 * @precondition leaves have not real children
		 * @return true if both left and right children are not real
		 *
		 * complexity: O(1)
		 */
		public boolean isLeaf()
		{
			return (!hasRight() && !hasLeft());
		}

		/**
		 * increases rank by 1
		 *
		 * complexity: O(1)
		 */
		public void promoteRank()
		{
			this.rank++;
			promoteCnt++;
		}

		/**
		 * decreases rank by 1
		 *
		 * complexity: O(1)
		 */
		public void demoteRank() {
			this.rank--;
			demoteCnt++;
		}

		/**
		 * returns node balance factor (left.rank-right.rank)
		 *
		 * complexity: O(1)
		 */
		public int balanceFactor() {return this.left.rank-this.right.rank;}

		/**
		 * returns rank difference r(p(node))-r(node). if node is root, return null
		 *
		 * complexity: O(1)
		 */
		public int rankDifference() {
			if (this.parent==null) {
				return -1;
			} else {
				return this.parent.rank-this.rank;
			}
		}

		/**
		 * converts external node to real node, adding key, value and external children
		 * @precondition node is external
		 *
		 * complexity: O(1)
		 */
		public void toReal(int k, String v) {
			// if node is allready real
			if (this.real) {System.out.println("to Real applyed to real node.");}

			// make real, add key and value
			this.real = true;
			this.key = k;
			this.value = v;
			this.rank = 0;
			this.subtreeSize = 1;

			// add external children
			WAVLNode leftExt = new WAVLNode();
			leftExt.parent = this;
			WAVLNode rightExt = new WAVLNode();
			rightExt.parent = this;
			this.left = leftExt;
			this.right = rightExt;
		}

		/**
		 * converts real node to external node, adjusting rank, real field, and pointers.
		 *
		 * complexity: O(1)
		 */
		public void toExternal() {
			// key and val
			this.key = -1;
			this.value = null;
			// disconnect children from node
			this.left.parent = null;
			this.right.parent = null;
			// disconnect node from children
			this.right = null;
			this.left = null;
			// reset rank and real
			this.rank = -1;
			this.real = false;
			// set size to 0
			this.subtreeSize = 0;

		}

		/**
		 * returns true for a zero-one or one-zero node.
		 * @precondition node is a real node
		 *
		 * complexity: O(1)
		 */
		public boolean isZeroOneNode(){
			if (((this.left.rankDifference() == 0) && (this.right.rankDifference() == 1))
					|| ((this.left.rankDifference() == 1) && (this.right.rankDifference() == 0))) {
				return true;
			}
			return false;
		}

		/**
		 * returns true for a one-one node.
		 * @precondition node is a real node
		 *
		 * complexity: O(1)
		 */
		public boolean isOneOneNode(){
			if ((this.left.rankDifference() == 1) && (this.right.rankDifference()==1)){
				return true;
			}
			return false;
		}

		/**
		 * returns true for a two-two leaf.
		 * @precondition node is a real node
		 *
		 * complexity: O(1)
		 */
		public boolean isTwoTwoNode() {
			if ((this.left.rankDifference() == 2)
					&& (this.right.rankDifference() == 2)) {
				return true;
			}
			return false;
		}
		/**
		 * returns true for if a node had a child with rank difference 3.
		 *
		 * complexity: O(1)
		 */
		public boolean hasThreeChild(){
			if ((this.left.rankDifference() == 3) || (this.right.rankDifference() == 3)) {
				return true;
			}
			return false;
		}

		/**
		 * returns three child of a node
		 * @precondition node has three child
		 *
		 * complexity: O(1)
		 *
		 */
		public WAVLNode getThreeChild() {
			if (this.left.rankDifference() == 3) {
				return this.left;
			}
			else {
				return this.right;
			}
		}

		/**
		 * returns sibling of a node
		 * @precondition node has sibling
		 *
		 * complexity: O(1)
		 */
		public WAVLNode getSibling() {
			if (this == this.parent.left) {
				return this.parent.right;
			}
			else {
				return this.parent.left;
			}
		}

		/**
		 * assigns null to children and parent
		 *
		 * complexity: O(1)
		 */
		public void nullifyPointers() {
			this.parent = null;
			this.left = null;
			this.right = null;
		}

		/**
		 * increases subtree size by one
		 *
		 * complexity: O(1)
		 */
		public void increaseSubtreeSize() {
			this.subtreeSize++;
		}

		/**
		 * decreases subtree size by one
		 *
		 * complexity: O(1)
		 */
		public void decreaseSubtreeSize() {
			this.subtreeSize--;
		}

	}


	public interface IWAVLNode {
		public int getKey(); //returns node's key (for virtuval node return -1)
		public String getValue(); //returns node's value [info] (for virtuval node return null)
		public IWAVLNode getLeft(); //returns left child (if there is no left child return null)
		public IWAVLNode getRight(); //returns right child (if there is no right child return null)
		public boolean isRealNode(); // Returns True if this is a non-virtual WAVL node (i.e not a virtual leaf or a sentinal)
		public int getSubtreeSize(); // Returns the number of real nodes in this node's subtree (Should be implemented in O(1))

	}
}


