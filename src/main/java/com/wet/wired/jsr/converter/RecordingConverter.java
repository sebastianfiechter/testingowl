/*
 * Original code: Copyright 2000-2001 by Wet-Wired.com Ltd., Portsmouth England
 * This class is distributed under the MIT License (MIT)
 * Download original code from: http://code.google.com/p/java-screen-recorder/
 * 
 * The current version of this class is heavily refactored by Sebastian Fiechter.
 * 
 */

package com.wet.wired.jsr.converter;

import java.io.File;

import javax.media.ConfigureCompleteEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.DataSink;
import javax.media.EndOfMediaEvent;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.PrefetchCompleteEvent;
import javax.media.Processor;
import javax.media.RealizeCompleteEvent;
import javax.media.ResourceUnavailableEvent;
import javax.media.control.TrackControl;
import javax.media.datasink.DataSinkEvent;
import javax.media.datasink.DataSinkListener;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.protocol.FileTypeDescriptor;

import org.springframework.stereotype.Component;

@Component
public class RecordingConverter implements ControllerListener, DataSinkListener {

   private static boolean finished = false;
   private Object waitSync = new Object();
   private boolean stateTransitionOK = true;

   public void init(String[] args) {

      if ((args.length != 1) || !args[0].endsWith("cap")) {
         System.out
               .println("Usage: java -jar screen_cap_to_video.jar <screen_cap_file.owl.cap>");
         return;
      }

      String movieFile = new String(args[0]);
      movieFile = movieFile.replace("cap", "mov");

      try {
         process(args[0], movieFile);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void process(String recordingFile, String movieFile) throws Exception {

      MediaLocator mediaLocator = new MediaLocator(new File(movieFile).toURI()
            .toURL());
      PlayerDataSource playerDataSource = new PlayerDataSource(recordingFile);
      Processor processor = Manager.createProcessor(playerDataSource);
      processor.addControllerListener(this);
      processor.configure();

      if (!waitForState(processor, Processor.Configured)) {
         System.err.println("Failed to configure the processor.");
         return;
      }

      processor.setContentDescriptor(new ContentDescriptor(
            FileTypeDescriptor.QUICKTIME));

      TrackControl trackControl[] = processor.getTrackControls();
      Format format[] = trackControl[0].getSupportedFormats();
      trackControl[0].setFormat(format[0]);
      processor.realize();
      if (!waitForState(processor, Processor.Realized)) {
         System.err.println("Failed to realize the processor.");
         return;
      }

      DataSource dataSource = processor.getDataOutput();
      DataSink dataSink = Manager.createDataSink(dataSource, mediaLocator);
      dataSink.open();
      processor.start();
      dataSink.start();
      waitForFileDone();
      dataSink.close();
      processor.removeControllerListener(this);
   }

   void waitForFileDone() {

      while (!finished) {
         try {
            Thread.sleep(50);
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }

      return;
   }

   public void dataSinkUpdate(DataSinkEvent evt) {

   }

   boolean waitForState(Processor p, int state) {
      synchronized (waitSync) {
         try {
            while (p.getState() < state && stateTransitionOK)
               waitSync.wait();
         } catch (Exception e) {
         }
      }
      return stateTransitionOK;
   }

   public void controllerUpdate(ControllerEvent evt) {

      if (evt instanceof ConfigureCompleteEvent
            || evt instanceof RealizeCompleteEvent
            || evt instanceof PrefetchCompleteEvent) {
         synchronized (waitSync) {
            stateTransitionOK = true;
            waitSync.notifyAll();
         }
      } else if (evt instanceof ResourceUnavailableEvent) {
         synchronized (waitSync) {
            stateTransitionOK = false;
            waitSync.notifyAll();
         }
      } else if (evt instanceof EndOfMediaEvent) {
         evt.getSourceController().stop();
         evt.getSourceController().close();
         finished = true;
      }
   }
}
