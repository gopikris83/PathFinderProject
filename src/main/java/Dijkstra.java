/***************************************************************************************************
 * Name : Gopi Krishna Ravichandran
 * Email : gopikrishna.83@gmail.com
 * Date: 12-Dec-2020
 * Source: https://www.baeldung.com/java-dijkstra (Used this reference for shortest path algorithm)
 ***************************************************************************************************/

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class Dijkstra {
    public static void main(String[] args) {

        //source is the start location and destination is the object to reach
        String source = args[0].toLowerCase(), destination = args[1].toLowerCase();

        //Read the config file and each location-to-object-path as a pathString
        List<String> pathStrings = new ArrayList<>();
        try {
            Scanner input = new Scanner(new FileInputStream(new File("input.txt")));
            input.useDelimiter(System.getProperty("line.separator"));
            while(input.hasNext()) {
                pathStrings.add(input.next());
            }
            input.close();
        } catch (FileNotFoundException e) {
            System.out.println("Please create the file input file named " + new File("input.txt").getAbsolutePath() + " and provide the Estate layout in it");
            System.exit(0);
        }

        Map<String,Node> nodeMap = new HashMap<>();

        List<String> path;
        String name;
        String[] lastTwoItems;
        Node node, parent;
        //Parse the pathStrings with delimiter ':', '-' to split the all last locations and all given objects.
        // Added all the locations and objects to the list
        for(String pathString: pathStrings) {
            path = new ArrayList<>();
            path.addAll(Arrays.asList(pathString.split(":")));
            lastTwoItems = path.get(path.size()-1).split("-");
            path.remove(path.size()-1);
            path.add(lastTwoItems[0].trim());
            path.add(lastTwoItems[1].trim());


            //Set all locations with the distances (Weights). All nodes are initialized with Max Value.
            name = path.get(0).trim().toLowerCase();
            if(nodeMap.get(name) == null) {
                node = new Node(name);
                node.setDistance(Integer.MAX_VALUE);
                nodeMap.put(name,node);
            }
            parent=nodeMap.get(name);
            for (int index = 1; index < path.size(); index++) {
                name = path.get(index).trim().toLowerCase();
                //Passing the adjacent nodes to the constructor
                if (nodeMap.get(name) == null) {
                    node = new Node(name.trim());
                    node.setDistance(Integer.MAX_VALUE);
                    nodeMap.put(name, node);
                }
                node = nodeMap.get(name);
                // The adjacentNodes attribute is used to associate immediate neighbors with edge length.
                parent.addAdjacentNode(node, 1);
                node.addAdjacentNode(parent,1);
                parent = node;
            }
        }
        //The core idea is to continuously eliminate longer paths between the starting node and all possible destinations.
        //The shortestPath and distance attributes are set for each node in the graph
        Graph graph = new Graph();
        graph.setNodes(new HashSet<Node>(nodeMap.values()));
        graph = calculateShortestPathFromSource(graph,nodeMap.get(source));
        Set<Node> resultNodes = graph.getNodes();

        System.out.println("You are in the "+source);
        for(Node resultNode : resultNodes) {
            if(resultNode.getName().equalsIgnoreCase(destination)) {
                List<Node> shortestPath = resultNode.getShortestPath();
                shortestPath.remove(0);
                for(Node shortestPathNode : shortestPath) {
                    System.out.print(" Go to "+ shortestPathNode.getName());
                }
            }
        }
        System.out.print(" get "+destination);
    }
    //calculateShortestPathFromSource is a list of nodes that describes the shortest path calculated from the starting node.
    public static Graph calculateShortestPathFromSource(Graph graph, Node source) {
        source.setDistance(0);

        Set<Node> settledNodes = new HashSet<>();
        Set<Node> unsettledNodes = new HashSet<>();

        unsettledNodes.add(source);

        while (unsettledNodes.size() != 0) {
            //The getLowestDistanceNode() method, returns the node with the lowest distance from the unsettled nodes set
            Node currentNode = getLowestDistanceNode(unsettledNodes);
            unsettledNodes.remove(currentNode);
            for (Map.Entry< Node, Integer> adjacencyPair:
                    currentNode.getAdjacentNodes().entrySet()) {
                Node adjacentNode = adjacencyPair.getKey();
                Integer edgeWeight = adjacencyPair.getValue();
                if (!settledNodes.contains(adjacentNode)) {
                    //calculateMinimumDistance() method compares the actual distance with the newly calculated one while following the newly explored path
                    calculateMinimumDistance(adjacentNode, edgeWeight, currentNode);
                    unsettledNodes.add(adjacentNode);
                }
            }
            settledNodes.add(currentNode);
        }
        return graph;
    }

    private static Node getLowestDistanceNode(Set < Node > unsettledNodes) {
        Node lowestDistanceNode = null;
        int lowestDistance = Integer.MAX_VALUE;
        for (Node node: unsettledNodes) {
            int nodeDistance = node.getDistance();
            if (nodeDistance < lowestDistance) {
                lowestDistance = nodeDistance;
                lowestDistanceNode = node;
            }
        }
        return lowestDistanceNode;
    }

    private static void calculateMinimumDistance(Node evaluationNode,
                                                 Integer edgeWeight, Node sourceNode) {
        Integer sourceDistance = sourceNode.getDistance();
        if (sourceDistance + edgeWeight < evaluationNode.getDistance()) {
            evaluationNode.setDistance(sourceDistance + edgeWeight);
            LinkedList<Node> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
            shortestPath.add(sourceNode);
            evaluationNode.setShortestPath(shortestPath);
        }
    }
}

class Graph {

    private Set<Node> nodes = new HashSet<>();

    public void addNode(Node nodeA) {
        nodes.add(nodeA);
    }

    public Set<Node> getNodes() {
        return nodes;
    }

    public void setNodes(Set<Node> nodes) {
        this.nodes = nodes;
    }
}

class Node {

    private String name;

    private List<Node> shortestPath = new LinkedList<>();

    private Integer distance = Integer.MAX_VALUE;

    private Map<Node, Integer> adjacentNodes = new HashMap<>();

    public void addDestination(Node destination, int distance) {
        adjacentNodes.put(destination, distance);
    }

    public Node(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Node> getShortestPath() {
        return shortestPath;
    }

    public void setShortestPath(List<Node> shortestPath) {
        this.shortestPath = shortestPath;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public Map<Node, Integer> getAdjacentNodes() {
        return adjacentNodes;
    }

    public void setAdjacentNodes(Map<Node, Integer> adjacentNodes) {
        this.adjacentNodes = adjacentNodes;
    }

    public void addAdjacentNode(Node node, Integer distance) {
        this.adjacentNodes.put(node,distance);
    }
}

/*
Bonus question #1: How would you modify your program if we knew that the gentleman had multiple ways to get to some of the rooms?

This case is handled since we pick the shortest path found by pairing all paths containing the source with all paths containing the destinations
*/

/*
Bonus question #2: Assuming multiple paths, how would you modify the program if walking up the staircase was more strenuous than walking down the same?

This can be handled by adding additional weightage in distance to account for path which involve walking up the staircase
*/