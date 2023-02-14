package org.example;

import java.util.*;

public class AverageNodes {
    public static TreeNode createTree(Integer[] vals) {
        TreeNode[] nodes = new TreeNode[vals.length];
        for (int i = 0; i < vals.length; i++) {
            var currentNode = vals[i] != null ? new TreeNode(vals[i]) : null;
            nodes[i] = currentNode;
            if (i > 0) {
                var parentNode = nodes[(i - 1) / 2];
                if (i % 2 == 0) {
                    parentNode.right = currentNode;
                } else {
                    parentNode.left = currentNode;
                }
            }
        }
        return nodes[0];
    }

    public void averageWithDepthFirst(TreeNode root) {
        Map<Integer, List> levelToValue = new LinkedHashMap<>();
        buildLevelToValues(root, 1, levelToValue);

        for (Integer level : levelToValue.keySet()) {
            int total = 0;
            List<Integer> valuesForLevel = levelToValue.get(level);
            for (int currentValue : valuesForLevel) {
                total += currentValue;
            }
            System.out.println("Level: " + level + " Average:" + ((double) total) / valuesForLevel.size());
        }
    }

    void buildLevelToValues(TreeNode node, int currentLevel, Map<Integer, List> levelToValues) {
        levelToValues.computeIfAbsent(currentLevel, key -> new ArrayList()).add(node.val);
        if (node.right != null) {
            buildLevelToValues(node.right, currentLevel + 1, levelToValues);
        }
        if (node.left != null) {
            buildLevelToValues(node.left, currentLevel + 1, levelToValues);
        }
    }

    public static void main(String[] args) {
        TreeNode root = createTree(new Integer[]{1, 2, 3, 4, 5, 6, 7});
        var obj = new AverageNodes();
        obj.averageWithDepthFirst(root);
    }

}