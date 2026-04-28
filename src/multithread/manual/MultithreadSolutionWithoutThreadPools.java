package multithread.manual;

import sequential.Utils;

import java.awt.*;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class MultithreadSolutionWithoutThreadPools {

    private final static int RBG = 256;

    private final int threadCount;

    public MultithreadSolutionWithoutThreadPools(int threadCount) {
        this.threadCount = threadCount;
    }

    public Color[][] equalizer(Color[][] image) {

        int width = image[0].length;
        int height = image.length;
        int numThreads= Math.max(1, Math.min(threadCount, height));

        AtomicIntegerArray histogram = new AtomicIntegerArray(RBG);
        Thread[] histogramThreads= new Thread[numThreads];

        for (int i = 0; i < numThreads; i++) {
            int firstPosition =  i*height/numThreads;
            int finalPosition =  (i+1)*height/numThreads;

            histogramThreads[i]= new Thread(() -> {
                for (int j = firstPosition; j < finalPosition; j++) {
                    for (int col=0; col<width; col++) {
                        Color pixel = image[j][col];
                        int luminance = Utils.computeLuminosity(pixel.getRed(), pixel.getGreen(), pixel.getBlue());
                        histogram.incrementAndGet(luminance);
                    }
                }
            });
            histogramThreads[i].start();
        }

        waitForThreads(histogramThreads);
        return null;
    }

    private static void waitForThreads(Thread[] threads) {
        for (Thread t : threads) {
            try {
                if (t != null) t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(e);
            }
        }
    }
}
