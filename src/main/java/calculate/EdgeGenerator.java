package calculate;

import java.util.concurrent.CountDownLatch;

public class EdgeGenerator implements Runnable {
    private KochFractal koch;
    private EdgeSides side;
    private CountDownLatch doneSignal;

    public EdgeGenerator(KochFractal kochFractal, EdgeSides side, CountDownLatch doneSignal){
        this.koch = kochFractal;
        this.side = side;
        this.doneSignal = doneSignal;
    }

    @Override
    public void run() {
        switch(side){
            case Left:
                koch.generateLeftEdge();
                doneSignal.countDown();
                break;
            case Right:
                koch.generateRightEdge();
                doneSignal.countDown();
                break;
            case Bottom:
                koch.generateBottomEdge();
                doneSignal.countDown();
                break;
        }
    }
}
