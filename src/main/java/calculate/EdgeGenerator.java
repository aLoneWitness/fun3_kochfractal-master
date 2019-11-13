package calculate;

import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class EdgeGenerator implements Runnable {
    private KochManager manager;
    private KochFractal koch;
    private ArrayList<Edge> edges;
    private EdgeSides side;
    private CountDownLatch doneSignal;

    public EdgeGenerator(EdgeSides side, KochManager manager){
        this.manager = manager;
        this.koch = new KochFractal(this);
        this.side = side;
        this.edges = new ArrayList<>();
    }

    void setLevel(int level){
        koch.setLevel(level);
    }

    void setLatch(CountDownLatch latch){
        this.doneSignal = latch;
    }

    @Override
    public void run() {
        this.edges.clear();
        switch(side){
            case Left:
                koch.generateLeftEdge();
                this.edges.forEach(edge -> this.manager.addEdge(edge));
                doneSignal.countDown();
                break;
            case Right:
                koch.generateRightEdge();
                this.edges.forEach(edge -> this.manager.addEdge(edge));
                doneSignal.countDown();
                break;
            case Bottom:
                koch.generateBottomEdge();
                this.edges.forEach(edge -> this.manager.addEdge(edge));
                doneSignal.countDown();
                break;
        }
    }

    void addEdge(Edge e){
        this.edges.add(e);
    }
}
