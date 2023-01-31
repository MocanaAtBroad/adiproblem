package org.example;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class RefHierarchyProblem {
    private static final String JSON_EXAMPLE_1 = """
            {
            "full_hierarchy": {
            "id": "top",
            "children": [
            {
            "id": "a",
            "children": [
            {
            "id": "b",
            "children": [
            {
            "id": "c",
            "children": [
            {
            "id": "d",
            "children": []
            }
            ]
            }
            ]
            }
            ]
            }
            ]
            },
            "ref_hierarchy": [
            "a",
            "c"
            ],
            "item_map": {
            "a": "this",
            "b": "is",
            "c": "an",
            "d": "example"
            }
            }""";


    /**
     * Traverse the hierarchy object recursively until finding an object that has the matching id
     *
     * @param searchId        the value of id property we are trying to match
     * @param hierarchyObject the object to traverse
     * @return the matching JSONObject or null if not found
     */
    public JSONObject findHierarchyById(String searchId, JSONObject hierarchyObject) {
        if (!hierarchyObject.has("id")) {
            throw new RuntimeException("Was expecting hierarchy object to have id property");
        }
        if (searchId.equals(hierarchyObject.getString("id"))) {
            return hierarchyObject;
        } else {
            JSONArray children = hierarchyObject.optJSONArray("children");
            if (children == null) {
                throw new RuntimeException("Was expecting hierarchy object to have a children array property");
            }
            if (children.length() == 1) {
                return findHierarchyById(searchId, children.getJSONObject(0));
            } else if (children.length() > 1) {
                throw new RuntimeException("Was expecting children array property to have max length of 1");
            } else if (children.length() == 0) {
                return null;
            }
        }
        return null;

    }

    /**
     * Traverse recursively JSON hierarchy object recording in given map each item's id and relative depth.
     *
     * @param hierarchyObject the top level job in the hierarchy
     * @param idToDepthMap    map of id of object as key and relative depth as key
     * @param currentDepth    initial depth of top level object
     */
    public void buildIdToDepthMap(JSONObject hierarchyObject, Map<String, Integer> idToDepthMap, int currentDepth) {
        String id = hierarchyObject.optString("id");
        if (id == null) {
            throw new RuntimeException("Found object with no id property");
        }
        idToDepthMap.put(id, currentDepth);

        JSONArray children = hierarchyObject.optJSONArray("children");
        if (children == null) {
            throw new RuntimeException("Did not find array children in object " + hierarchyObject);
        }
        if (children.length() == 1) {
            buildIdToDepthMap(children.getJSONObject(0), idToDepthMap, ++currentDepth);
        }
    }

    public void printTreeObject(String jsonModelText) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonModelText);
        } catch (JSONException e) {
            throw new RuntimeException("Could not read the JSON string", e);
        }
        // Get main sections of JSON model
        JSONObject fullHierarchy = jsonObject.optJSONObject("full_hierarchy");
        if (fullHierarchy == null) {
            throw new RuntimeException("Could not find full_hierarchy object");
        }
        JSONArray refHierarchy = jsonObject.optJSONArray("ref_hierarchy");
        if (refHierarchy == null) {
            throw new RuntimeException("Could not find ref_hierarchy");
        }
        JSONObject itemMapObject = jsonObject.optJSONObject("item_map");
        if (itemMapObject == null) {
            throw new RuntimeException("Could not find item_map");
        }
        Map<String, Object> idToStringValueMap = itemMapObject.toMap();

        refHierarchy.forEach(rootId -> {
            if (rootId instanceof String) {
                // get just the part of the hierarchy of interest
                JSONObject rootHierarchyObject = findHierarchyById((String) rootId, fullHierarchy);

                // Collect the item ids, relative depth, in order
                // using LinkedHashMap to preserve the order of the items in hierarchy
                Map<String, Integer> idToDepthMap = new LinkedHashMap<>();
                buildIdToDepthMap(rootHierarchyObject, idToDepthMap, 1);

                for (Map.Entry<String, Integer> idToDepth : idToDepthMap.entrySet()) {
                    Object stringValue = idToStringValueMap.get(idToDepth.getKey());
                    if (!(stringValue instanceof String)) {
                        throw new RuntimeException("Was expecting item_map property to contain strings");
                    }

                    // the output
                    System.out.print("-".repeat(idToDepth.getValue()));
                    System.out.println(" " + stringValue);
                }
            } else {
                throw new RuntimeException("ref_hierarchy array had members that were not strings");
            }
        });

    }

    public static void main(String[] args) {
        var obj = new RefHierarchyProblem();
        obj.printTreeObject(JSON_EXAMPLE_1);
    }

}
