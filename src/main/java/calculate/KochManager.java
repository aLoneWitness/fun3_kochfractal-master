/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package calculate;

import java.util.ArrayList;
import fun3kochfractalfx.FUN3KochFractalFX;
import javafx.concurrent.Task;
import observer.IListener;
import timeutil.TimeStamp;

import java.util.List;
import java.util.concurrent.*;

/**
 *
 * @author Nico Kuijpers
 * Modified for FUN3 by Gertjan Schouten
 */
public class KochManager {
    
    private ArrayList<Edge> edges;
    private FUN3KochFractalFX application;
    private TimeStamp tsCalc;
    private TimeStamp tsDraw;

    private Future<List<Edge>> f1;
    private Future<List<Edge>> f2;
    private Future<List<Edge>> f3;

//    private EdgeGenerator leftGen = new EdgeGenerator(EdgeSides.Left);
//    private EdgeGenerator rightGen = new EdgeGenerator(EdgeSides.Right);
//    private EdgeGenerator bottomGen = new EdgeGenerator(EdgeSides.Bottom);
//
//    private Thread threadOne = new Thread(leftGen);
//    private Thread threadTwo = new Thread(rightGen);
//    private Thread threadThree = new Thread(bottomGen);

    public KochManager(FUN3KochFractalFX application) {
        this.edges = new ArrayList<Edge>();
        this.application = application;
        this.tsCalc = new TimeStamp();
        this.tsDraw = new TimeStamp();
    }
    
    public synchronized void changeLevel(int nxt) {
        ExecutorService pool = Executors.newFixedThreadPool(3);

        edges.clear();
        tsCalc.init();
        tsCalc.setBegin("Begin calculating");

        EdgeGenerator edgeGenLeft = new EdgeGenerator(EdgeSides.Left, nxt, this);
        EdgeGenerator edgeGenRight = new EdgeGenerator(EdgeSides.Right, nxt, this);
        EdgeGenerator edgeGenBottom = new EdgeGenerator(EdgeSides.Bottom, nxt, this);

        this.application.getProgressBarLeft().progressProperty().bind(edgeGenLeft.progressProperty());
        this.application.getProgressBarRight().progressProperty().bind(edgeGenRight.progressProperty());
        this.application.getProgressBarBottom().progressProperty().bind(edgeGenBottom.progressProperty());

        this.application.getProgressBarLeftLabel().textProperty().bind(edgeGenLeft.messageProperty());
        this.application.getProgressBarRightLabel().textProperty().bind(edgeGenRight.messageProperty());
        this.application.getProgressBarBottomLabel().textProperty().bind(edgeGenBottom.messageProperty());

        f1 = pool.submit((Callable<List<Edge>>) edgeGenLeft);
        f2 = pool.submit((Callable<List<Edge>>) edgeGenRight);
        f3 = pool.submit((Callable<List<Edge>>) edgeGenBottom);
        pool.shutdown();

//        try {
//            this.edges.addAll(f1.get());
//            this.edges.addAll(f2.get());
//            this.edges.addAll(f3.get());
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//        }

//        threadOne.interrupt();
//        threadTwo.interrupt();
//        threadThree.interrupt();



//        CountDownLatch doneSignal = new CountDownLatch(3);

//        threadOne = new Thread(leftGen);
//        threadTwo = new Thread(rightGen);
//        threadThree = new Thread(bottomGen);
//
//        leftGen.setLevel(nxt);
//        rightGen.setLevel(nxt);
//        bottomGen.setLevel(nxt);
//
//        leftGen.setLatch(doneSignal);
//        rightGen.setLatch(doneSignal);
//        bottomGen.setLatch(doneSignal);
//
//        threadOne.start();
//        threadTwo.start();
//        threadThree.start();

//        try {
//            doneSignal.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

//        tsCalc.setEnd("End calculating");
//        application.setTextNrEdges("" + this.edges.size());
//        application.setTextCalc(tsCalc.toString());
//
//        drawEdges();
    }

    void onDone(){
        if(f1 == null || f2 == null || f3 == null) return;
        
        if(f1.isDone() && f2.isDone() && f3.isDone()){
            System.out.println("hi");
            edges.clear();

            try {
                edges.addAll(f1.get());
                edges.addAll(f2.get());
                edges.addAll(f3.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            tsCalc.setEnd("End calculating");
            application.setTextNrEdges("" + this.edges.size());
            application.setTextCalc(tsCalc.toString());
            drawEdges();

            f1 = null;
            f2 = null;
            f3 = null;
        }
    }

    public FUN3KochFractalFX getApplication() {
        return this.application;
    }
    
    public synchronized void drawEdges() {
        tsDraw.init();
        tsDraw.setBegin("Begin drawing");
        application.clearKochPanel();
        for (Edge e : edges) {
            application.drawEdge(e);
        }
        tsDraw.setEnd("End drawing");
        application.setTextDraw(tsDraw.toString());
    }
}
