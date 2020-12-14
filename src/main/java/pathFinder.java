/*******************************************************************************************
 * Name : Gopi Krishna Ravichandran
 * Email : gopikrishna.83@gmail.com
 * Date: 12-Dec-2020
 ********************************************************************************************/

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class pathFinder {
    public static void main(String[] args) {
        //source is the start location and destination is the object to reach
        String source = args[0].toLowerCase(), destination = args[1].toLowerCase();

        //Read the config file and parse each location-to-object-path as a pathString
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

        //Tokenize each of selected pathStrings to a String array containing the Estate and every location at each step on the path till the end object
        String[] path;
        List<String[]> sourcePaths = new ArrayList<>();
        for(String pathString: pathStrings) {
            if(pathString.toLowerCase().contains(source)) {
                path = pathString.split(":");
                path[path.length-1] = path[path.length-1].split("-")[0].trim();
                sourcePaths.add(path);
            }
        }
        //Tokenize each of such selected pathStrings to a String array containing the Estate, every location at each step on the path till the end object
        List<String[]> destinationPaths = new ArrayList<>();
        for(String pathString: pathStrings) {
            if(pathString.toLowerCase().contains(destination)) {
                path = pathString.split(":");
                path[path.length-1] = path[path.length-1].split("-")[0].trim();
                destinationPaths.add(path);
            }
        }

        //optimalSourcePath is the optimal location-to-object-path picked from the list of all candidate pathStrings containing the startLocation
        //optimalDestinationPath is the optimal location-to-object-path picked from the list of all candidate pathStrings containing the object to be reached
        String [] optimalSourcePath = new String[0], optimalDestinationPath = new String[0];
        //indexOfSourceInOptimalSourcePath is the array index of source in optimalSourcePath
        //optimalPathsDivergeAt is the array index of the location where the optimalSourcePath and optimalDestinationPath diverge into separate paths while
        //walking from the Estate entrance
        int indexOfSourceInOptimalSourcePath=0, optimalPathsDivergeAt=0;

        //the code below calculates the optimal path to reach the destination from the source.
        // Every candidate path containing the source is paired up with every candidate path reaching the destination
        // and the pair which the optimal way reach the destination from the source is evaluated. The distance to
        //be covered is calculated for each such pair and the pair giving the optimal minimum distance is arrived at.
        int optimalDistanceToWalk = Integer.MAX_VALUE, index, distanceToWalk,  indexOfSource, pathsDivergeAt;

        //loop through every possible pair of candidate paths containing the source and destination
        for (String[] sourcePath : sourcePaths) {
            for (String[] destinationPath : destinationPaths) {

                //begin from the starting point and move ahead on both source and destination candidate paths until they pass through the same location
                //and figure out the location at which paths diverge
                index = 0;
                while (index<sourcePath.length && index<destinationPath.length
                        && sourcePath[index].equalsIgnoreCase(destinationPath[index])) {
                    index++;
                    System.out.println("First While:"+index);
                }
                pathsDivergeAt = index - 1;

                //after the paths diverge, figure out how much further on the path the source location is
                while (index<sourcePath.length
                        && !sourcePath[index].equalsIgnoreCase(source)) {
                    index++;
                    System.out.println("Second While:"+index);
                }
                indexOfSource = index;

                //total distance to walk = distance on the source path from the source to the diverging point + distance on the destination path from the diverging point till end
                distanceToWalk = (indexOfSource - pathsDivergeAt) + (destinationPath.length - pathsDivergeAt);

                //if the distance to walk for this pair of source and destination paths is lesser than the previous known distance, record the source and destination path details
                if (distanceToWalk < optimalDistanceToWalk) {
                    optimalDistanceToWalk = distanceToWalk;
                    optimalSourcePath = sourcePath;
                    optimalDestinationPath = destinationPath;
                    indexOfSourceInOptimalSourcePath = indexOfSource;
                    optimalPathsDivergeAt = pathsDivergeAt;
                }
            }
        }


        //print the path from source until the diverging point walking back towards the path-origin i.e Estate
        for(index=indexOfSourceInOptimalSourcePath;index>optimalPathsDivergeAt && index<optimalSourcePath.length;index--) {
            System.out.print(optimalSourcePath[index] + " -> ");
        }
        //print the path on the destination path beginning at the diverging point until the destination
        for(index=optimalPathsDivergeAt;index<optimalDestinationPath.length;index++) {
            System.out.print(optimalDestinationPath[index] + " -> ");
        }
        System.out.println(destination);


        System.out.print("You are in " + source + ".");
        if(!source.equalsIgnoreCase(optimalSourcePath[optimalPathsDivergeAt])) {
            //print the path from source until the diverging point walking back towards the path-origin i.e Estate
            for (index = indexOfSourceInOptimalSourcePath - 1; index > optimalPathsDivergeAt; index--) {
                System.out.print(" Go to " + optimalSourcePath[index]);
            }
            //print the path on the destination path beginning at the diverging point until the destination
            System.out.println();
            for (index = optimalPathsDivergeAt; index < optimalDestinationPath.length; index++) {
                System.out.print("Go to " + optimalDestinationPath[index] + " ");
            }
        }
        System.out.print("get " + destination);

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