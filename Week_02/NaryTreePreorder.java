package Week_02;

import java.util.ArrayList;
import java.util.List;

public class NaryTreePreorder {
    List<Integer> ans = new ArrayList<>();
    public List<Integer> preorder(Node root) {
        preOrder(root);
        return ans;
    }

    private void preOrder(Node node) {
        if (node == null)
            return;

        ans.add(node.val);
        for (Node nod : node.children) {
            preOrder(nod);
        }
    }
}