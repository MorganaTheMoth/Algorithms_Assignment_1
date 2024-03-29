import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

public class BFS {
    public static void main(String[] Args){
        //Scanner imp = new Scanner(System.in);
        //System.out.println("Please input the name of the file containing the maze (including the file extention.)");
        String Fname = Args[0]; //imp.nextLine();
        int[] steps = new int[2];
        //System.out.println(Args[0]);
        long start = System.currentTimeMillis();
        String rawData = "";
        try{
            File opfil = new File(Fname);
            Scanner reader = new Scanner(opfil);
            while(reader.hasNextLine()){
                rawData = reader.nextLine();
            }
        }
        catch(FileNotFoundException e){
            System.out.println("There is no file.");
        }
        // getting the data back from the generator
        List<Cloneable> data = generateMatrix(rawData);
        int[] export = bfs((int[][]) data.get(0), (int[]) data.get(1), (int[]) data.get(2), steps);
        System.out.println("Maze path is: " + (export.length - 2 )+ " steps long"); // go to remove the start and finish nodes
        long end = System.currentTimeMillis();
        System.out.println("This program took: " + (end - start) + "ms to complete");
        System.out.println("Maze solution: " + Arrays.toString(export));
        PrinttextMaze((int[][]) data.get(0), (int[]) data.get(1), (int[]) data.get(2), export);
    }

    static int[] bfs(int[][] maze, int[] start, int[] finish, int[] steps){
        //System.out.println(Arrays.deepToString(maze) + "\n" + Arrays.toString(start) + "\n" + Arrays.toString(finish));
        Queue<int[]> bfs = new LinkedList<>();
        boolean[][] visList = new boolean[maze.length][maze[0].length];
        int[] result = new int[0];
        LinkedList<LinkedList<Integer> > PossiblePaths = new LinkedList<>();
        createPath(start[0] * maze[0].length + start[1], PossiblePaths, start, maze, finish);

        int i;
        boolean found = false;
        while(!found){
            if(start[0] == finish[0] && start[1] == finish[1]){
                //System.out.println("gotya");
                //System.out.println(Arrays.deepToString(visList));
                found = true;
            }
            //this just loops through the directions. enqueueing all possible ones.
            for(i = 0; i < 4; i ++){
                if(isPossible(maze, i, start[0], start[1], visList)){
                    int[] imp = new int[2];
                    switch (i) {
                        case 0 -> {
                            imp[0] = start[0];
                            imp[1] = start[1] + 1;
                        }
                        case 1 -> {
                            imp[0] = start[0] + 1;
                            imp[1] = start[1];
                        }
                        case 2 -> {
                            imp[0] = start[0];
                            imp[1] = start[1] - 1;
                        }
                        case 3 -> {
                            imp[0] = start[0] - 1;
                            imp[1] = start[1];
                        }
                    }
                    result = createPath((imp[0] * maze[0].length  + imp[1]), PossiblePaths, start, maze, finish);
                    //System.out.println(imp[0] * maze[0].length + imp[1]);
                    bfs.add(imp);
                }
            }
            //System.out.print(Arrays.toString(start) + " ");
            //System.out.print(start[0] * maze[0].length + start[1] + " ");
            if(bfs.isEmpty()){ break; } // breaking if the Q is empty

            visList[start[0]][start[1]] = true; // this is to avoid repatation.

            start = bfs.remove();
            steps[0] ++;
        }
        //System.out.println(PossiblePaths);
        System.out.println("This program took " + (steps[0] - 1 )+ " steps total");
        return result;
    }

    static int[] createPath(int newmove, LinkedList<LinkedList<Integer>> paths, int[] currentIndx, int[][] maze, int[] finish){
        // loop through the paths in the linked list.
        // - See if the node can be added to the end of any of them
        // - return the first
        int i;
        LinkedList<Integer> temp;
        for(i =0; i < paths.size(); i++){       // looping through the linked list
            //temp = paths.get(i);
            if(!paths.get(i).contains(newmove)){        // checking if the node is already in the path
                if(checkAdjacency(newmove, paths.get(i).getLast(), currentIndx, maze)){
                    //temp.add(newmove);
                    paths.get(i).add(newmove);
                    if(Arrays.equals(finish, currentIndx)){  // print it if it is complete
                        int j = 0;
                        int[] export = new int[paths.get(i).size()];
                        for(j = 0; paths.get(i).size() - 1 > j; j++){
                            export[j] = paths.get(i).get(j) + 1;
                        }
                        return export;
                        //System.out.println( "The primary path is: " + Arrays.toString(export));
                    }
                    break;                      // break the loop when it is added
                }
                else{
                    // checking if this is a splinter path. is so create a new path.
                    if( paths.get(i).size() >= 1){
                        if(checkAdjacency(newmove, paths.get(i).get(paths.get(i).size()-2), currentIndx, maze)) {
                            // removing the last index and adding the new move.
                            temp = (LinkedList<Integer>) paths.get(i).clone();
                            temp.removeLast();
                            temp.add(newmove);
                            // adding the new path to the amalgam of paths
                            paths.add(temp);
                            //System.out.println(paths.get(i));
                            //System.out.println(temp);

                            if(Arrays.equals(finish, currentIndx)){  // print it if it is complete
                                int j = 0;
                                int[] export = new int[paths.get(i).size()];
                                for(j = 0; paths.get(i).size() - 1 > j; j++){
                                    export[j] = paths.get(i).get(j) + 1;
                                }
                                return export;
                                //System.out.println( "The primary path is: " + Arrays.toString(export));
                            }
                            break;
                        }
                    }
                }
            }
        }
        // if there is nothing in the current linked list create a new one.
        if(paths.size() == 0){
            temp = new LinkedList<Integer>();
            temp.add(newmove);
            paths.add(temp);
        }
        // if it gets to this there was a massive problem
        return new int[0];
    }

    static boolean checkAdjacency(int newNode, int tailNode, int[] indx, int[][] maze){
        // indx [row, col]
        //if(newNode - 1 == )
        // Checking if they are adjacent in actuality
        // Checking if it can move
        // right, check left, check up, check down. , checking if it is adjacent
        // once we check if we really are adjacent we check if it is a legal move
        int Col = indx[1];
        int Row = indx[0];

        if(newNode == tailNode - 1){                // Left
            // check if the move is possible
            if(Col - 1 >= 0){
                if (maze[Row][Col - 1] == 1 || maze[Row][Col - 1] == 3){
                    return true;
                }
            }
        }
        if(newNode == tailNode + 1){                // Right
            if (Col + 1 < maze[0].length) {
                if (maze[Row][Col] != 0 && maze[Row][Col] != 2) {
                    return true;
                }
            }
        }
        if(newNode == (tailNode + maze[0].length)){    // Down
            if (Row + 1 < maze.length){
                if (maze[Row][Col] != 0 && maze[Row][Col] != 1) {
                    return true;
                }
            }
        }
        if(newNode == (tailNode - maze[0].length)){    // up
            if(Row - 1 >= 0){
                if(maze[Row - 1][Col] == 2 || maze[Row - 1][Col] == 3){
                    return true;
                }
            }
        }
        return false;
    }

    static List<Cloneable> generateMatrix(String inp){
        //split the data into its important points
        //0: num rows and columns. - 1: start - 2: End. 3: Maze data.
        String[] data = inp.split(":");
        String[] MazeInf = data[3].split("");
        //System.out.println(Arrays.toString(MazeInf));
        String[] size = data[0].split(",");
        int[][] maze = new int[Integer.parseInt(size[0])][Integer.parseInt(size[1])];
        int[] startCoords = new int[2];
        int[] endCoords = new int[2];
        int i,j;
        //System.out.println(data[1]);
        for(i = 0; i < maze.length; i++){
            for(j = 0; j < maze[0].length; j++){
                int coordval = i * maze[0].length + j;
                maze[i][j] = Integer.parseInt(MazeInf[coordval]);
                //System.out.println(coordval + " " + data[1]);
                if(coordval == Integer.parseInt(data[1]) - 1){
                    //System.out.println(coordval);
                    //System.out.println("boop");
                    startCoords[0] = i;
                    startCoords[1] = j;
                }
                if(coordval == Integer.parseInt(data[2]) - 1){
                    endCoords[0] = i;
                    endCoords[1] = j;
                }
                //System.out.println(maze[i][j]);
            }
        }
        //System.out.println(Arrays.deepToString(maze));
        return Arrays.asList(maze, startCoords, endCoords);
    }

    // Returns false if you cannot move to it.
    static boolean isPossible(int[][] maze, int dir, int Row, int Col, boolean[][] visList){
        switch(dir) {
            //Right
            case 0:
                if (Col + 1 < maze[0].length) {
                    if (maze[Row][Col] != 0 && maze[Row][Col] != 2) {
                        if(!visList[Row][Col + 1]){
                            return true;
                        }
                    }
                }
                break;
            //Down
            case 1:
                if (Row + 1 < maze.length){
                    if (maze[Row][Col] != 0 && maze[Row][Col] != 1) {
                        if(!visList[Row + 1][Col]){
                            return true;
                        }
                    }
                }
                break;
            //Left
            case 2:
                // check if the move is possible
                if(Col - 1 >= 0){
                    if (maze[Row][Col - 1] == 1 || maze[Row][Col - 1] == 3){
                        if(!visList[Row][Col - 1]){
                            return true;
                        }
                    }
                }
                break;
            //Up
            case 3:
                //checking if the move is possible
                if(Row - 1 >= 0){
                    if(maze[Row - 1][Col] == 2 || maze[Row - 1][Col] == 3){
                        if(!visList[Row - 1][Col]){
                            return true;
                        }
                    }
                }
                break;
        }
        // returns false by default
        return false;
    }
    public static void PrinttextMaze(int [][] Maze, int[] S, int[] F, int[] Solution){
        //constructing the walls
        List<Integer> sol = Arrays.stream(Solution).boxed().toList();
        String onLine = "-";
        String betweenLine = "|";
        boolean start = false;
        boolean finish = false;
        boolean populated = false;
        String[] returnHolder;
        for(int i = 0 ; i < Maze[1].length; i++){
            onLine += "---";
        }
        System.out.println(onLine);
        for(int i = 0; i < Maze.length; i++){
            onLine = "|";
            betweenLine = "|";
            for(int j = 0; j < Maze[1].length; j++){
                if( i == S[0] && j == S[1]) {
                    start = true;
                }
                if( i == F[0] && j == F[1]) {
                    finish = true;
                }
                if( sol.contains((i * Maze[1].length + j) + 1) && (!finish && !start)){ // seeing if it is populated
                    //System.out.println("boop");
                    populated = true;
                }
                returnHolder = PrintWalls(Maze[i][j], onLine, betweenLine, start, finish, populated);
                onLine = returnHolder[0];
                betweenLine = returnHolder[1];
                start = false;
                populated = false;
                finish = false;
            }
            System.out.println(onLine);
            if(i + 1 != Maze.length){
                System.out.println(betweenLine);
            }
        }
        onLine = "-";
        for(int i = 0 ; i < Maze[1].length; i++){
            onLine += "---";
        }
        System.out.println(onLine);
    }

    public static String[] PrintWalls(int direction, String onLine, String betweenLine, boolean start, boolean finish, boolean populated){
        String[] Lines = new String[]{onLine,betweenLine};
        //[0] is online
        //[1] is betweenLines
        switch(direction){
            case 0:{
                //will create one below and to the right
                if(start){
                    Lines[0] += "S |";
                }
                else if(finish){
                    Lines[0] += "F |";
                }
                else if(populated){
                    Lines[0] += "* |";
                }
                else{
                    Lines[0] += "  |";
                }
                Lines[1] += "--";
                break;
            }
            case 1:{
                //will create one below
                if(start){
                    Lines[0] += "S  ";
                }
                else if(finish){
                    Lines[0] += "F  ";
                }
                else if(populated){
                    Lines[0] += "*  ";
                }
                else{
                    Lines[0] += "   ";
                }
                Lines[1] += "--";
                break;
            }
            case 2:{
                if(start){
                    Lines[0] += "S |";
                }
                else if(finish){
                    Lines[0] += "F |";
                }
                else if(populated){
                    Lines[0] += "* |";
                }
                else{
                    Lines[0] += "  |";
                }
                Lines[1] += "  ";
                //will create one to the right
                break;
            }
            default:{
                //case 3 has right and down open so it wont need to create walls

                if(start){
                    Lines[0] += "S  ";
                }
                else if(finish){
                    Lines[0] += "F  ";
                }
                else if(populated){
                    Lines[0] += "*  ";
                }
                else{
                    Lines[0] += "   ";
                }
                Lines[1] += "  ";
            }
        }
        Lines[1] += "|";
        return Lines;
    }
}
