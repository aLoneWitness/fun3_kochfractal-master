package calculate;

import javafx.concurrent.Task;
import observer.IListener;
import observer.ISubject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class EdgeGenerator implements Runnable, ISubject {
    private KochManager manager;
    private KochFractal koch;
    private ArrayList<Edge> edges;
    private EdgeSides side;
    private CountDownLatch doneSignal;
    private List<IListener> listeners = new ArrayList<>();

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
                break;
            case Right:
                koch.generateRightEdge();
                break;
            case Bottom:
                koch.generateBottomEdge();
                break;
        }

        this.notifyListeners(this.edges);
        doneSignal.countDown();
    }

    void addEdge(Edge e){
        this.edges.add(e);
    }

    @Override
    public void addListener(IListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void notifyListeners(Object object) {
        this.listeners.forEach(listener -> listener.update(object));
    }
}
