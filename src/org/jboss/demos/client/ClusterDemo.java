package org.jboss.demos.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
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
import com.google.gwt.user.client.ui.TextBox;
import org.jboss.demos.shared.ClusterNode;

import java.util.List;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ClusterDemo implements EntryPoint {

    private static final String upgradeMessage = "Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";
    Canvas canvas;
    Context2d context2d;
    Canvas bufferCanvas;
    Context2d bufferContext2d;

    TextBox textBox = new TextBox();

    NodeGroup nodeGroup;
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
        Button reloadButton = new Button("Reload", new ClickHandler() {
            public void onClick(ClickEvent event) {
                Window.alert("Test Management API");
            }
        });

        Button shutdownButton = new Button("Shutdown", new ClickHandler() {
            public void onClick(ClickEvent event) {
                Window.alert("Test Management API");
            }
        });

        RootPanel.get("cluster-operations").add(textBox);
        RootPanel.get("cluster-operations").add(reloadButton);
        RootPanel.get("cluster-operations").add(shutdownButton);

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

        canvas.addMouseMoveHandler(new MouseMoveHandler() {
            public void onMouseMove(MouseMoveEvent event) {
                mouseX = event.getRelativeX(canvas.getElement());
                mouseY = event.getRelativeY(canvas.getElement());

            }
        });

        canvas.addMouseOutHandler(new MouseOutHandler() {
            public void onMouseOut(MouseOutEvent event) {
                mouseX = -200;
                mouseY = -200;
                textBox.setText("");
            }
        });

        canvas.addTouchMoveHandler(new TouchMoveHandler() {
            public void onTouchMove(TouchMoveEvent event) {
                event.preventDefault();
                if (event.getTouches().length() > 0) {
                    Touch touch = event.getTouches().get(0);
                    mouseX = touch.getRelativeX(canvas.getElement());
                    mouseY = touch.getRelativeY(canvas.getElement());
                }
                event.preventDefault();
            }
        });

        canvas.addTouchEndHandler(new TouchEndHandler() {
            public void onTouchEnd(TouchEndEvent event) {
                event.preventDefault();
                mouseX = -200;
                mouseY = -200;
            }
        });

        canvas.addMouseDownHandler(new MouseDownHandler() {
            public void onMouseDown(MouseDownEvent event) {
                //TODO: select cluster node, show it on cluster-operations text-box
            }
        });

        nodeGroup = new NodeGroup(width-80, height-80, 200);

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
//        updateClusterInfoTimer.schedule(refreshRate * 40);

    }

    private void redraw() {
        // reset bufferContext2d
        bufferContext2d.setFillStyle(REDRAW_COLOR);
        bufferContext2d.fillRect(0, 0, width, height);

        // draw image to bufferContext2d
        nodeGroup.draw(bufferContext2d,  mouseX, mouseY);

        // update current node info to textbox
        Node node = nodeGroup.getCurrentNode();
        if(node != null) {
            String value = node.getClusterNode().getIp() + ":" + node.getClusterNode().getPort();
            if(!value.equals(textBox.getValue())) {
                textBox.setValue(value);
            }
        }
        else {
            textBox.setValue("");
        }

        // draw bufferContext2d to front
        context2d.drawImage(bufferContext2d.getCanvas(), 0, 0);

    }

    private void updateClusterInfo() {
        managementService.getClusterInfo("", new AsyncCallback<List<ClusterNode>>() {
            public void onFailure(Throwable caught) {
                GWT.log("faile to get cluster info");
            }

            public void onSuccess(List<ClusterNode> result) {
                nodeGroup.updateClusterInfo(result);
            }
        });
    }

}
