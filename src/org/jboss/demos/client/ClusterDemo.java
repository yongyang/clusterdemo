package org.jboss.demos.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ClusterDemo implements EntryPoint {

    private static final String upgradeMessage = "Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";
    Canvas canvas;
    Context2d context2d;
    Canvas bufferCanvas;
    Context2d bufferContext2d;

    Cluster cluster;
    // mouse positions relative to canvas
    int mouseX, mouseY;
    //timer refresh rate, in milliseconds
    static final int refreshRate = 25;

    // canvas size, in px
    static final int height = 600;
    static final int width = 600;
    static final CssColor REDRAW_COLOR = CssColor.make("white");

    /**
   * The message displayed to the user when the server cannot be reached or
   * returns an error.
   */
  private static final String SERVER_ERROR = "An error occurred while "
      + "attempting to contact the server. Please check your network "
      + "connection and try again.";

  /**
   * Create a remote service proxy to talk to the server-side Greeting service.
   */
  private final ManagementServiceAsync managementService = GWT.create(ManagementService.class);

  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
      canvas = Canvas.createIfSupported();
      if (canvas == null) {
          RootPanel.get().add(new Label(upgradeMessage));
          return;
      }
      bufferCanvas = Canvas.createIfSupported();

      initCanvas();

      RootPanel.get("cluster-canvas").add(canvas);
      addButtons();
  }

    private void addButtons() {
        Button sendButton = new Button("Send", new ClickHandler() {
            public void onClick(ClickEvent event) {
                managementService.getClusterInfo("", new AsyncCallback<String>() {
                    public void onFailure(Throwable caught) {
                        Window.alert("getClusterInfo fail!" + caught.toString());
                    }

                    public void onSuccess(String result) {
                        Window.alert("getClusterInfo success! \n" + result);
                    }
                });

            }
        });

        Button addButton = new Button("Add", new ClickHandler() {
            public void onClick(ClickEvent event) {
                Window.alert("Test Management API");
            }
        });

        Button removeButton = new Button("Remove", new ClickHandler() {
            public void onClick(ClickEvent event) {
                Window.alert("Test Management API");
            }
        });

        RootPanel.get("cluster-buttons").add(sendButton);
        RootPanel.get("cluster-buttons").add(addButton);
        RootPanel.get("cluster-buttons").add(removeButton);

    }

    private void initCanvas() {
        // init the canvases
        canvas.setWidth(width + "px");
        canvas.setHeight(height + "px");
        canvas.setCoordinateSpaceWidth(width);
        canvas.setCoordinateSpaceHeight(height);
        bufferCanvas.setCoordinateSpaceWidth(width);
        bufferCanvas.setCoordinateSpaceHeight(height);

        context2d = canvas.getContext2d();
        bufferContext2d = bufferCanvas.getContext2d();

        cluster = new Cluster(width-60, height-60, 6, 250);

        final Timer redrawTimer = new Timer() {
            @Override
            public void run() {
                redraw();
            }
        };
        redrawTimer.scheduleRepeating(refreshRate);

        final Timer updateClusterInfoTimer = new Timer() {
            @Override
            public void run() {
                updateClusterInfo();
            }
        };
        updateClusterInfoTimer.scheduleRepeating(refreshRate * 40);

    }

    private void redraw() {

        // reset bufferContext2d
        bufferContext2d.setFillStyle(REDRAW_COLOR);
        bufferContext2d.fillRect(0, 0, width, height);

        // draw image to bufferContext2d
        cluster.update(0, 0);
        cluster.draw(bufferContext2d);

        // draw bufferContext2d to front
        context2d.drawImage(bufferContext2d.getCanvas(), 0 ,0);

    }

    private void updateClusterInfo() {
        managementService.getClusterInfo("", new AsyncCallback<String>() {
            public void onFailure(Throwable caught) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            public void onSuccess(String result) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
    }

}
