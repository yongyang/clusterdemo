package org.jboss.demos.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.jboss.demos.shared.ClusterInfo;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ClusterDemo implements EntryPoint {

    private static final String upgradeMessage = "Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";
    Canvas canvas;
    Context2d context2d;
    Canvas bufferCanvas;
    Context2d bufferContext2d;

    final TextBox textBox = new TextBox();
    final Button reloadButton = new Button("Reload");
    final Button shutdownButton = new Button("Shutdown");
    final Button consoleButton = new Button("Open Console");
    final Button helpButton = new Button("?");

    NodeGroup nodeGroup;
    // mouse positions relative to canvas
    int mouseX, mouseY;
    //timer refresh rate, in milliseconds
    static final int refreshRate = 25;

    // canvas size, in px
    public static final int height = 600;
    public static final int width = 600;
    static final CssColor REDRAW_COLOR = CssColor.make("white");

    private Timer redrawTimer;
    private Timer updateClusterInfoTimer;

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
      textBox.setReadOnly(true);
      addButtons();
  }

    private void addButtons() {

        reloadButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                String value = textBox.getValue();
                if (value != null && !value.isEmpty()) {
                    value = value.trim();
                }
                else {
                    return;
                }
                String ip = value.substring(0, value.indexOf(":"));

                reloadButton.setEnabled(false);
                managementService.invokeOperation(ip, "reload", new String[0], new AsyncCallback<Boolean>() {
                    public void onFailure(Throwable caught) {
                        new Timer() {
                            @Override
                            public void run() {
                                reloadButton.setEnabled(true);
                            }
                        }.schedule(2000);

                        Window.alert("Fail to invoke operation reload, " + caught.getMessage());
                    }

                    public void onSuccess(Boolean result) {
                        new Timer() {
                            @Override
                            public void run() {
                                reloadButton.setEnabled(true);
                            }
                        }.schedule(2000);

                        if (!result) {
                            Window.alert("Reload operation sent, but return false, please check server side logs!");
                        }
                    }
                });
            }
        });

        shutdownButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                String value = textBox.getValue();
                if (value != null && !value.isEmpty()) {
                    value = value.trim();
                }
                else {
                    return;
                }
                String ip = value.substring(0, value.indexOf(":"));

                shutdownButton.setEnabled(false);
                managementService.invokeOperation( ip,"shutdown", new String[0], new AsyncCallback<Boolean>() {
                    public void onFailure(Throwable caught) {
                        new Timer() {
                            @Override
                            public void run() {
                                shutdownButton.setEnabled(true);
                            }
                        }.schedule(2000);
                        Window.alert("Fail to invoke operation shutdown, " + caught.getMessage());
                    }

                    public void onSuccess(Boolean result) {
                        new Timer() {
                            @Override
                            public void run() {
                                shutdownButton.setEnabled(true);
                            }
                        }.schedule(2000);

                        if(!result) {
                            Window.alert("Shutdown operation sent, but return false, please check server side logs!");
                        }
                    }
                } );
            }
        });

        consoleButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                String value = textBox.getValue();
                if (value != null && !value.isEmpty()) {
                    value = value.trim();
                }
                else {
                    return;
                }
                String ip = value.substring(0, value.indexOf(":"));
                Window.open("http://" + ip + ":9990/console", "_blank", "");
            }
        });


        final DialogBox dialogBox = new DialogBox();
        dialogBox.setModal(false);
        dialogBox.setText("Help");
        // Create a table to layout the content
        VerticalPanel dialogContents = new VerticalPanel();
        dialogContents.setSpacing(4);
        dialogContents.add(new HTML("&nbsp;Green lighting: Starting"));
        dialogContents.add(new HTML("Yellow lighting: Reloading"));
        dialogContents.add(new HTML("&nbsp;&nbsp;&nbsp;Red lighting: Shutdowning"));
        dialogContents.add(new HTML("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Left bar: Memory usage"));
        dialogContents.add(new HTML("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Right bar: Thread usage"));
        dialogContents.add(new HTML("&nbsp;Bar color <30%: Green"));
        dialogContents.add(new HTML("&nbsp;Bar color <60%: Orange"));
        dialogContents.add(new HTML("&nbsp;Bar color >60%: Red"));
        dialogBox.setWidget(dialogContents);
        helpButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if(!dialogBox.isShowing()) {
                    dialogBox.showRelativeTo(helpButton);
                }
                else {
                    dialogBox.hide();
                }
            }
        });

        RootPanel.get("cluster-operations").add(textBox);
        RootPanel.get("cluster-operations").add(reloadButton);
        RootPanel.get("cluster-operations").add(shutdownButton);
        RootPanel.get("cluster-operations").add(consoleButton);
        RootPanel.get("cluster-operations").add(helpButton);
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

        redrawTimer = new Timer() {
            @Override
            public void run() {
                redraw();
            }
        };
        redrawTimer.scheduleRepeating(refreshRate);

        updateClusterInfoTimer = new Timer() {
            @Override
            public void run() {
                updateClusterInfo();
            }
        };
        updateClusterInfoTimer.scheduleRepeating(refreshRate * 40); // 1s
//        updateClusterInfoTimer.schedule(refreshRate * 40);

    }

    private void redraw() {
        // reset bufferContext2d
        bufferContext2d.setFillStyle(REDRAW_COLOR);
        bufferContext2d.fillRect(0, 0, width, height);
//        bufferContext2d.clearRect(0, 0, width, height);

        // draw image to bufferContext2d
        nodeGroup.draw(bufferContext2d,  mouseX, mouseY);
        // draw bufferContext2d to front
        context2d.drawImage(bufferContext2d.getCanvas(), 0, 0);

        // update current node info to textbox
        Node node = nodeGroup.getCurrentNode();
        if(node != null) {
            String value = node.getClusterNode().getIp() + ":" + node.getClusterNode().getPort();
            if(!value.equals(textBox.getValue())) {
                textBox.setValue(value);
            }
        }

    }

    private void updateClusterInfo() {
        managementService.getClusterInfo("", new AsyncCallback<ClusterInfo>() {
            public void onFailure(Throwable caught) {
//                redrawTimer.cancel();
//                updateClusterInfoTimer.cancel();
                // ATTENTION: reload the host node of this GWT application will cause failure to update cluster info
                Window.alert("Fail to update cluster info, " + caught.getMessage());
            }

            public void onSuccess(ClusterInfo result) {
                nodeGroup.updateClusterInfo(result);
            }
        });
    }

}
