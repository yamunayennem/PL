
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

class Puzzle
{
    public int pegCount;
    public int[] cells;

    public Puzzle(int emptyCell)
    {
        cells = new int[15];
        pegCount = 14;
        for (int i = 0; i < 15; i++)
            cells[i] = i == emptyCell ? 0 : 1;
    }

    public Puzzle(int pegCount, int[] cells)
    {
        this.pegCount = pegCount;
        this.cells    = cells.clone();
    }

    public Puzzle move(Move m)
    {
        if (cells[m.getF()] == 1 &&
                cells[m.getO()] == 1 &&
                cells[m.getT()]   == 0)
        {
            Puzzle puzzleLater = new Puzzle(pegCount-1, cells.clone());
            puzzleLater.cells[m.getF()] = 0;
            puzzleLater.cells[m.getO()] = 0;
            puzzleLater.cells[m.getT()]   = 1;

            return puzzleLater;
        }

        return null;
    }
}

class Move
{
    private int f, o, t;
    public Move(int f, int o, int t)
    {
        this.f = f;
        this.o = o;
        this.t   = t;
    }

    public Move reversed()
    { return new Move(t, o, f); }

    public int getF() {
        return f;
    }

    public int getO() {
        return o;
    }

    public int getT() {
        return t;
    }

    @Override
    public String toString()
    {
        return "(" + f + ", " + o + ", " + t + ")";
    }
}

class MoveIterator implements Iterator<Move>
{
    private Move[] moves;
    private Move reversed;
    private int counter;

    public MoveIterator(Move[] moves)
    {
        this.moves = moves;
        this.counter = 0;
    }

    @Override
    public boolean hasNext()
    { return counter < moves.length || (counter == moves.length && reversed != null); }

    @Override
    public Move next()
    {
        if (reversed != null)
        {
            Move result = reversed;
            reversed = null;
            return result;
        }
        Move m = moves[counter++];
        reversed = m.reversed();
        return m;
    }
}

class MoveList implements Iterable<Move> {
    public static final Move[] moves =
            {
                    new Move(0, 1, 3),
                    new Move(0, 2, 5),
                    new Move(1, 3, 6),
                    new Move(1, 4, 8),
                    new Move(2, 4, 7),
                    new Move(2, 5, 9),
                    new Move(3, 6, 10),
                    new Move(3, 7, 12),
                    new Move(4, 7, 11),
                    new Move(4, 8, 13),
                    new Move(5, 8, 12),
                    new Move(5, 9, 14),
                    new Move(3, 4, 5),
                    new Move(6, 7, 8),
                    new Move(7, 8, 9),
                    new Move(10, 11, 12),
                    new Move(11, 12, 13),
                    new Move(12, 13, 14)
            };

    @Override
    public MoveIterator iterator()
    { return new MoveIterator(moves); }
}

public class Cracker {

    private static void go() {
        for (int i = 0; i < 5; i++)
        {
            System.out.println("=== " + i + " ===");
            Puzzle p = new Puzzle(i);
            replay(prev(p), p);
            System.out.println();
        }
    }

    static LinkedList<Move> prev(Puzzle p)
    {
        ArrayList<LinkedList<Move>> outList = new ArrayList<>();
        solve(p, outList, 1);
        if (outList.isEmpty())
            return null;
        return outList.get(0);
    }


    static void solve(Puzzle p, ArrayList<LinkedList<Move>> solutions, int count)
    {
        if (p.pegCount == 1)
        {
            solutions.add(new LinkedList<>());
            return;
        }

        for (Move m : moves())
        {
            Puzzle puzzleLater = p.move(m);
            if (puzzleLater == null) {
                continue;
            }

            ArrayList<LinkedList<Move>> tailSolutions = new ArrayList<>();
            solve(puzzleLater, tailSolutions, count);

            for (int i=0; i<tailSolutions.size(); i++)
            {
                tailSolutions.get(i).add(0, m);
                solutions.add(tailSolutions.get(i));

                if (solutions.size() == count)
                    return;
            }
        }
    }
    static MoveList moves()
    { return new MoveList(); }

    private static void replay(List<Move> moves, Puzzle p)
    {
        show(p);
        for (int i=0; i<moves.size(); i++)
        {
            p = p.move(moves.get(i));
            show(p);
        }
    }

    static void show(Puzzle p)
    {
        int[][] lines = { {4,0,0}, {3,1,2}, {2,3,5}, {1,6,9}, {0,10,14} };
        for (int i=0; i<lines.length; i++)
        {
            int blanks = lines[i][0], start = lines[i][1], end = lines[i][2];
            StringBuilder blank = new StringBuilder();
            for (int j = 0; j < blanks; j++){
                blank.append(" ");
            }
            System.out.print(blank);
            for (int k = start; k <= end; k++){
                System.out.print(p.cells[k] == 0 ? ". " : "x ");
            }

            System.out.println();
        }

        System.out.println();
    }

    static void printPuzzle(Puzzle p)
    {
        System.out.print("(" + p.pegCount + ", [");
        for (int k = 0; k < p.cells.length; k++){
            System.out.print(k < p.cells.length-1 ? p.cells[k] + ", " : p.cells[k] + "])");
        }
        System.out.println();
    }
    static void terse()
    {
        for (int j = 0; j < 15; j++)
        {
            Puzzle p = new Puzzle(j);
            printPuzzle(p);
            List<Move> moves = prev(p);
            for (Move m : moves)
            {
                System.out.println(m);
                p = p.move(m);
            }
            printPuzzle(p);
            System.out.println();
        }
    }

    public static void main(String[] args)
    {
        go();
        terse();
    }

}