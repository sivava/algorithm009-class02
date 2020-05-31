package Week_02;

import java.util.ArrayList;
import java.util.List;

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode() {}
    TreeNode(int val) { this.val = val; }
    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}

public class BinaryTreeInorderTraversal {
    List<Integer> ans = new ArrayList<Integer>();
    public List<Integer> inorderTraversal(TreeNode root) {
        preOrder(root);
        return ans;
    }

    private void preOrder(TreeNode node) {
        if (node == null)
            return;

        preOrder(node.left);
        ans.add(node.val);
        preOrder(node.right);
    }
}