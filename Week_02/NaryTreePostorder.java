package Week_02;

import java.util.ArrayList;
import java.util.List;

class Node {
    public int val;
    public List<Node> children;

    public Node() {}

    public Node(int _val) {
        val = _val;
    }

    public Node(int _val, List<Node> _children) {
        val = _val;
        children = _children;
    }
}

public class NaryTreePostorder {
    List<Integer> ans = new ArrayList<>();
    public List<Integer> postorder(Node root) {
        postOrder(root);
        return ans;
    }

    private void postOrder(Node node) {
        if (node == null)
            return;

        for (Node nod : node.children) {
            postorder(nod);
        }
        ans.add(node.val);
    }
}