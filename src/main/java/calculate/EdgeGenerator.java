package calculate;

import javafx.application.Platform;
import javafx.concurrent.Task;
import observer.IListener;
import timeutil.TimeStamp;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public class EdgeGenerator extends Task implements Callable<List<Edge>>, IListener {
    private KochFractal koch;
    private ArrayList<Edge> edges;
    private EdgeSides side;
    private int maxEdges;
    private KochManager manager;

    private TimeStamp tsCalc = new TimeStamp();

    public EdgeGenerator(EdgeSides side, int level, KochManager manager){
        this.koch = new KochFractal(this);
        this.side = side;
        this.edges = new ArrayList<>();
        this.koch.setLevel(level);
        this.koch.addListener(this);
        this.manager = manager;

        maxEdges = this.koch.getNrOfEdges() / 3;
    }

//    void setLevel(int level){
//        koch.setLevel(level);
//    }
//
//    void setLatch(CountDownLatch latch){
//        this.doneSignal = latch;
//    }

    void addEdge(Edge e){
        this.edges.add(e);
    }

    @Override
    public List<Edge> call() throws Exception {
        tsCalc.init();
        tsCalc.setBegin("Begin calculating.");

        this.edges.clear();
        switch(side){
            case Left:
                koch.generateLeftEdge();
                break;
            case Right:
                koch.generateRightEdge();
                break;
            case Bottom:
                koch.generateBottomEdge();
                break;
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                manager.onDone();
            }
        });

        return this.edges;
    }




    @Override
    public void update(Object object) {
        addEdge((Edge) object);
        updateProgress(this.edges.size(), maxEdges);
        updateMessage("Nr of edges: " + this.edges.size());
    }
}
