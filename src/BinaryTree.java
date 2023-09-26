import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BinaryTree {
	Node root;
	int maxDegree;

	public static void main(String[] args) {
		/*
	           1
	         /   \
	        2     3
	            /   \
	           4     5
		 */
		BinaryTree tree = new BinaryTree();
		tree.root = tree.new Node(1);
		tree.maxDegree = 3;

		int numNodes = 12;
		Node[] testNodes = new Node[numNodes];
		for (int i = 0; i < numNodes; i++) {
			testNodes[i] = tree.new Node(i);
		}

		try {
			testNodes[1].add(testNodes[6].add(testNodes[9])).add(testNodes[7]);
			testNodes[3].add(testNodes[8]);
			testNodes[2].add(testNodes[3]).add(testNodes[4]);
			tree.root.add(testNodes[1]).add(testNodes[2]).add(testNodes[5].add(testNodes[10]).add(testNodes[11]));
		} catch (Exception e) {
			System.out.println("Tried to add a vertex when already at max degree!");
		}
		System.out.println(tree.root.deepToString());

		Node x = tree.root;

		System.out.println("Closest vertex of which we can defend all children:");
		Distance d = tree.minimumDistance(tree.root, x);
		System.out.println(d);
		System.out.println("Path: " + d.path);
	}

	/**
	 * Recursively finds the closest leaf node to a given node within a binary tree and maintains a path from the
	 * starting node to the closest leaf.
	 *
	 * @param n           The current node being processed.
	 * @param lev         The current depth level in the tree (initially 0).
	 * @param minDist     An object representing the minimum distance found so far. This object will be updated with the
	 *                    minimum distance and the path to the closest leaf.
	 * @param currentPath The list of nodes representing the current path from the root to the current node. This list
	 *                    is updated as the method traverses the tree.
	 * @return a Distance object, containing: vertices to and from, minimum distance and a path of that length.
	 */
	Distance findLeafDown(Node n, int lev, Distance minDist, List<Node> currentPath) {
		if (n == null) return minDist;
		// Add the current node to the path
		currentPath.add(n);
		// If this is a leaf node or can still add children, check if it is closer than the closest so far
		if (n.children.length == 0 || n.children.length <= maxDegree - 2) {
			if (lev < minDist.minDis) {
				minDist.minDis = lev;
				minDist.to = n;
				// Update the path to the closest leaf (copy the currentPath)
				minDist.path = new ArrayList<>(currentPath);
				return minDist;
			}
		}
		for (Node c : n.children) { // recur for subtrees
			findLeafDown(c, lev + 1, minDist, currentPath);
		}
		currentPath.remove(currentPath.size() - 1); // backtrack by removing the current node from the path
		return minDist;
	}

	/**
	 * Finds a closest vertex to a specified vertex with {@code graph.maxDegree - 2} children.
	 *
	 * @param root the root of the graph.
	 * @param v    the vertex from which we are searching.
	 * @return a Distance object containing a closest vertex with {@code graph.maxDegree - 2} children and a shortest
	 * path to it from v.
	 */
	Distance minimumDistance(Node root, Node v) {
		return findLeafDown(v, 0, new Distance(root), new ArrayList<>()); // Find the closest leaf down to v
	}

	static class Distance {
		public ArrayList<Node> path;
		Node from;
		Node to;
		int minDis = Integer.MAX_VALUE;

		public Distance(Node from) {
			this.from = from;
		}

		@Override
		public String toString() {
			return "Distance from " + from.key + " to " + to.key + " is " + minDis;
		}
	}

	class Node {
		int key;
		Node[] children;

		public Node(int key) {
			this.key = key;
			children = new Node[] {};
		}

		public Node(int key, Node[] children) {
			this.key = key;
			this.children = children.clone();
		}

		@Override
		public int hashCode() {
			return key;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Node node = (Node) o;
			if (key != node.key) return false;
			if (children.length != node.children.length) return false;
			for (int i = 0; i < children.length; i++) {
				if (children[i] != node.children[i]) return false;
			}
			return true;
		}

		public Node add(Node n) throws Exception {
			int l = this.children.length;
			if ((l < BinaryTree.this.maxDegree && this.equals(BinaryTree.this.root)) || l + 1 < BinaryTree.this.maxDegree) {
				Node[] current = this.children.clone();
				Node[] newList = new Node[l + 1];
				for (int i = 0; i < l; i++) newList[i] = new Node(current[i].key, current[i].children.clone());
				newList[l] = n;
				this.children = newList;
			} else {
				throw new Exception("Trying to extend degree beyond max for tree");
			}
			return this;
		}

		@Override
		public String toString() {
			return String.valueOf(key);
		}

		public String deepToString() {
			StringBuilder buffer = new StringBuilder(50);
			print(buffer, "", "");
			return buffer.toString();
		}

		private void print(StringBuilder buffer, String prefix, String childrenPrefix) {
			buffer.append(prefix).append(key).append('\n');
			for (Iterator<Node> it = List.of(children).iterator(); it.hasNext(); ) {
				Node next = it.next();
				if (it.hasNext()) next.print(buffer, childrenPrefix + "├── ", childrenPrefix + "│   ");
				else next.print(buffer, childrenPrefix + "└── ", childrenPrefix + "    ");
			}
		}
	}
}