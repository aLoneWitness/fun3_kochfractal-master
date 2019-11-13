/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package calculate;

import java.util.ArrayList;
import fun3kochfractalfx.FUN3KochFractalFX;
import timeutil.TimeStamp;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

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

    private EdgeGenerator leftGen = new EdgeGenerator(EdgeSides.Left, this);
    private EdgeGenerator rightGen = new EdgeGenerator(EdgeSides.Right, this);
    private EdgeGenerator bottomGen = new EdgeGenerator(EdgeSides.Bottom, this);

    private Thread threadOne = new Thread(leftGen);
    private Thread threadTwo = new Thread(rightGen);
    private Thread threadThree = new Thread(bottomGen);

    public KochManager(FUN3KochFractalFX application) {
        this.edges = new ArrayList<Edge>();
        this.application = application;
        this.tsCalc = new TimeStamp();
        this.tsDraw = new TimeStamp();
    }
    
    public void changeLevel(int nxt) {
        threadOne.interrupt();
        threadTwo.interrupt();
        threadThree.interrupt();

        edges.clear();
        tsCalc.init();
        tsCalc.setBegin("Begin calculating");

        CountDownLatch doneSignal = new CountDownLatch(3);

        threadOne = new Thread(leftGen);
        threadTwo = new Thread(rightGen);
        threadThree = new Thread(bottomGen);

        leftGen.setLevel(nxt);
        rightGen.setLevel(nxt);
        bottomGen.setLevel(nxt);

        leftGen.setLatch(doneSignal);
        rightGen.setLatch(doneSignal);
        bottomGen.setLatch(doneSignal);

        threadOne.start();
        threadTwo.start();
        threadThree.start();

        try {
            doneSignal.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        tsCalc.setEnd("End calculating");
        // TODO: get number of edges and display to GUI.
        application.setTextNrEdges("" + this.edges.size());
        application.setTextCalc(tsCalc.toString());

        drawEdges();
    }
    
    public void drawEdges() {
        tsDraw.init();
        tsDraw.setBegin("Begin drawing");
        application.clearKochPanel();
        for (Edge e : edges) {
            application.drawEdge(e);
        }
        tsDraw.setEnd("End drawing");
        application.setTextDraw(tsDraw.toString());
    }
    
    public synchronized void addEdge(Edge e) {
        edges.add(e);
    }
}
