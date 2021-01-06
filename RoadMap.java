/* C1803744
 *
 * Optionally, if you have any comments regarding your submission, put them here. 
 * For instance, specify here if your program does not generate the proper output or does not do it in the correct manner.
 */

import java.util.*;
import java.io.*;

class Vertex {

	// Constructor: set name, chargingStation and index according to given values,
	// initilaize incidentRoads as empty array
	public Vertex(String placeName, boolean chargingStationAvailable, int idx) {
		name = placeName;
		incidentRoads = new ArrayList<Edge>();
		index = idx;
		chargingStation = chargingStationAvailable;
		distance = 999999;
		prevVertex = null;
	}

	public String getName() {
		return name;
	}

	public boolean hasChargingStation() {
		return chargingStation;
	}

	public ArrayList<Edge> getIncidentRoads() {
		return incidentRoads;
	}

	// Add a road to the array incidentRoads
	public void addIncidentRoad(Edge road) {
		incidentRoads.add(road);
	}

	public int getIndex() {
		return index;
	}

	public int getDistance(){ // new method for returning distance
		return distance;
	}

	public void setDistance(int newDistance){ // new method for setting distance
		distance = newDistance;
	}

	public Vertex getPrevVertex(){ // new method for getting prevVertex
		return prevVertex;
	}

	public void setPrevVertex(Vertex newPrevVertex){ // new method for returning prevVertex
		prevVertex = newPrevVertex;
	}

	private String name; // Name of the place
	private ArrayList<Edge> incidentRoads; // Incident edges
	private boolean chargingStation; // Availability of charging station
	private int index; // Index of this vertex in the vertex array of the map
	private int distance; // Total distance from start vertex
	private Vertex prevVertex; // Prev vertex that lead to this vertex along the shortest path
}

class Edge {
	public Edge(int roadLength, Vertex firstPlace, Vertex secondPlace) {
		length = roadLength;
		incidentPlaces = new Vertex[] { firstPlace, secondPlace };
	}

	public Vertex getFirstVertex() {
		return incidentPlaces[0];
	}

	public Vertex getSecondVertex() {
		return incidentPlaces[1];
	}

	public int getLength() {
		return length;
	}

	private int length;
	private Vertex[] incidentPlaces;
}

// A class that represents a sparse matrix
public class RoadMap {

	// Default constructor
	public RoadMap() {
		places = new ArrayList<Vertex>();
		roads = new ArrayList<Edge>();
	}

	// Auxiliary function that prints out the command syntax
	public static void printCommandError() {
		System.err.println("ERROR: use one of the following commands");
		System.err.println(" - Read a map and print information: java RoadMap -i <MapFile>");
		System.err.println(
				" - Read a map and find shortest path between two vertices with charging stations: java RoadMap -s <MapFile> <StartVertexIndex> <EndVertexIndex>");
	}

	public static void main(String[] args) throws Exception {
		if (args.length == 2 && args[0].equals("-i")) {
			RoadMap map = new RoadMap();
			try {
				map.loadMap(args[1]);
			} catch (Exception e) {
				System.err.println("Error in reading map file");
				System.exit(-1);
			}

			System.out.println("Read road map from " + args[1] + ":");
			map.printMap();
		} else if (args.length == 4 && args[0].equals("-s")) {
			RoadMap map = new RoadMap();
			map.loadMap(args[1]);
			System.out.println("Read road map from " + args[1] + ":");
			map.printMap();

			int startVertexIdx = -1, endVertexIdx = -1;
			try {
				startVertexIdx = Integer.parseInt(args[2]);
				endVertexIdx = Integer.parseInt(args[3]);
			} catch (NumberFormatException e) {
				System.err.println("Error: start vertex and end vertex must be specified using their indices");
				System.exit(-1);
			}

			if (startVertexIdx < 0 || startVertexIdx >= map.numPlaces()) {
				System.err.println("Error: invalid index for start vertex");
				System.exit(-1);
			}

			if (endVertexIdx < 0 || endVertexIdx >= map.numPlaces()) {
				System.err.println("Error: invalid index for end vertex");
				System.exit(-1);
			}

			Vertex startVertex = map.getPlace(startVertexIdx);
			Vertex endVertex = map.getPlace(endVertexIdx);
			if (!map.isConnectedWithChargingStations(startVertex, endVertex)) {
				System.out.println();
				System.out.println("There is no path connecting " + map.getPlace(startVertexIdx).getName() + " and "
						+ map.getPlace(endVertexIdx).getName() + " with charging stations");
			} else {
				ArrayList<Vertex> path = map.shortestPathWithChargingStations(startVertex, endVertex);
				System.out.println();
				System.out.println("Shortest path with charging stations between " + startVertex.getName() + " and "
						+ endVertex.getName() + ":");
				map.printPath(path);
			}

		} else {
			printCommandError();
			System.exit(-1);
		}
	}

	// Load matrix entries from a text file
	public void loadMap(String filename) {
		File file = new File(filename);
		places.clear();
		roads.clear();

		try {
			Scanner sc = new Scanner(file);

			// Read the first line: number of vertices and number of edges
			int numVertices = sc.nextInt();
			int numEdges = sc.nextInt();

			for (int i = 0; i < numVertices; ++i) {
				// Read the vertex name and its charing station flag
				String placeName = sc.next();
				int charginStationFlag = sc.nextInt();
				boolean hasChargingStataion = (charginStationFlag == 1);

				// Add your code here to create a new vertex using the information above and add
				// it to places
				places.add(new Vertex(placeName, hasChargingStataion, i)); // adds the info to the places ArrayList
			}

			for (int j = 0; j < numEdges; ++j) {
				// Read the edge length and the indices for its two vertices
				int vtxIndex1 = sc.nextInt();
				int vtxIndex2 = sc.nextInt();
				int length = sc.nextInt();
				Vertex vtx1 = places.get(vtxIndex1);
				Vertex vtx2 = places.get(vtxIndex2);

				// Add your code here to create a new edge using the information above and add
				// it to roads
				roads.add(new Edge(length, vtx1, vtx2)); // adds the edges to the roads ArrayList
				// You should also set up incidentRoads for each vertex
				places.get(vtxIndex1).addIncidentRoad(new Edge(length, vtx1, vtx2)); // sets up first incident road
				places.get(vtxIndex2).addIncidentRoad(new Edge(length, vtx2, vtx1)); // sets up reverse incident road from end vertex
			}

			sc.close();

			// Add your code here if approparite
		} catch (Exception e) {
			e.printStackTrace();
			places.clear();
			roads.clear();
		}
	}

	// Return the shortest path between two given vertex, with charging stations on
	// each itermediate vertex.
	public ArrayList<Vertex> shortestPathWithChargingStations(Vertex startVertex, Vertex endVertex) {

		// Initialize an empty path
		ArrayList<Vertex> path = new ArrayList<Vertex>();

		// Sanity check for the case where the start vertex and the end vertex are the
		// same
		if (startVertex.getIndex() == endVertex.getIndex()) {
			path.add(startVertex);
			return path;
		}

		// Add your code here
		ArrayList<Integer> visited = new ArrayList<Integer>(); // sets up visited and unvisited ArrayLists
		ArrayList<Integer> unvisited = new ArrayList<Integer>();
		ArrayList<Edge> incidentRoads;
		Integer currentVertex;
		Integer distanceHold = 0;
		Vertex pathHold;
		for (int i = 0; i < places.size(); ++i){ // sets it up so all Vertexes that don't have charging stations or don't have any incident roads are removed form the algorithm
			if ((places.get(i).hasChargingStation() ==  true || i == startVertex.getIndex() || i == endVertex.getIndex()) && places.get(i).getIncidentRoads().isEmpty() == false){
				unvisited.add(i);
			}		
		}
		currentVertex = startVertex.getIndex();
		startVertex.setDistance(0);
		while (unvisited.isEmpty() == false ){ // start of Dijkstra's Algorithm
			visited.add(currentVertex);
			unvisited.remove(new Integer(currentVertex));
			distanceHold = 9999;
			incidentRoads = places.get(currentVertex).getIncidentRoads();
			for (int i = 0; i < incidentRoads.size(); ++i){ // adjusts all the values for the nodes connected nodes
				if ((incidentRoads.get(i).getLength()+places.get(currentVertex).getDistance())<incidentRoads.get(i).getSecondVertex().getDistance()){
					incidentRoads.get(i).getSecondVertex().setDistance(incidentRoads.get(i).getLength()+places.get(currentVertex).getDistance());
					incidentRoads.get(i).getSecondVertex().setPrevVertex(places.get(currentVertex));
				}
			}
			for (int j = 0; j < unvisited.size(); ++j){ // picks the next node with the shortest current distance that hasnt already been visited
				if((places.get(unvisited.get(j)).getDistance())<distanceHold){
					currentVertex = unvisited.get(j);
					distanceHold = places.get(unvisited.get(j)).getDistance();
				}
			}

		}
		pathHold = endVertex;
		while (true){ // works out the shortest path by using the prevVeertex attribute for each node
			path.add(pathHold);
			if (pathHold.getPrevVertex() == null){
				break;
			}
			pathHold = pathHold.getPrevVertex();
		}
		Collections.reverse(path); // reverses the path
		return path;
		



	}

	// Check if two vertices are connected by a path with charging stations on each itermediate vertex.
	// Return true if such a path exists; return false otherwise.
	// The worst-case time complexity of your algorithm should be no worse than O(v + e),
	// where v and e are the number of vertices and the number of edges in the graph.
	public boolean isConnectedWithChargingStations(Vertex startVertex, Vertex endVertex) {
		// Sanity check
		if (startVertex.getIndex() == endVertex.getIndex()) {
			return true;
		}
		// Add your code here
		int currentVertex;
		ArrayList<Edge> paths;
		ArrayList<Integer> visited = new ArrayList<Integer>(); // creates visited ArrayList
		Stack<Integer> stack = new Stack<Integer>(); //creates new stack
		stack.push(startVertex.getIndex());
		while (stack.size() != 0){ // start of Depth First Pre order traversal
			currentVertex = stack.pop();
			if (visited.contains(currentVertex) == false ){
				visited.add(currentVertex);
				paths = places.get(currentVertex).getIncidentRoads();
				for (int i = 0; i < paths.size(); ++i){
					if (paths.get(i).getSecondVertex().getIndex() == endVertex.getIndex()){ // checks if end vertex
						return true;
					}
					if (paths.get(i).getSecondVertex().hasChargingStation() == true){ // checks if vertex has a charging station
						stack.push(paths.get(i).getSecondVertex().getIndex());
					}
				}
			}
		}
		return false;
	}

	public void printMap() {
		System.out.println("The map contains " + this.numPlaces() + " places and " + this.numRoads() + " roads");
		System.out.println();

		System.out.println("Places:");

		for (Vertex v : places) {
			System.out.println("- name: " + v.getName() + ", charging station: " + v.hasChargingStation());
		}

		System.out.println();
		System.out.println("Roads:");

		for (Edge e : roads) {
			System.out.println("- (" + e.getFirstVertex().getName() + ", " + e.getSecondVertex().getName()
					+ "), length: " + e.getLength());
		}
	}

	public void printPath(ArrayList<Vertex> path) {
		System.out.print("(  ");

		for (Vertex v : path) {
			System.out.print(v.getName() + "  ");
		}

		System.out.println(")");
	}

	public int numPlaces() {
		return places.size();
	}

	public int numRoads() {
		return roads.size();
	}

	public Vertex getPlace(int idx) {
		return places.get(idx);
	}

	private ArrayList<Vertex> places;
	private ArrayList<Edge> roads;
}