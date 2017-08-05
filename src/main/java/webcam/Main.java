/*
 * JCameraControl - Java camera control.
 * Copyright (c) 2017, Sebastien Vincent
 *
 * Distributed under the terms of the BSD 3-clause License.
 * See the LICENSE file for details.
 */

package webcam;

import java.util.*;

import javax.swing.*;

import javafx.application.*;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.paint.*;
import javafx.geometry.*;
import javafx.beans.value.*;
import javafx.stage.*;
import javafx.embed.swing.*;

import com.github.sarxos.webcam.*;

/**
 * UI class of the program that contains entry point.
 *
 * @author Sebastien Vincent
 */
public class Main extends Application
    implements WebcamDiscoveryListener, WebcamListener
{
    /**
     * The layout to where the panes are added/removed.
     */
    private final FlowPane root = new FlowPane();

    /**
     * List of panes that show webcam name and display content.
     */
    private final List<TitledPane> panes = new ArrayList<TitledPane>();

    /**
     * Entry point of the program.
     *
     * @param args array of arguments.
     */
    public static void main(String[] args)
    {
		launch(args);
    }

    /**
     * Adds a webcam UI element into layout.
     *
     * @param webcam the webcam to add.
     */
    private void addWebcam(Webcam webcam)
    {
        final SwingNode swingNode = new SwingNode();
        final TitledPane pane = new TitledPane(webcam.getName(), swingNode);

        System.out.println("Adds new webcam: " + webcam.getName());

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run()
            {
                WebcamPanel webcamPanel = null;
                webcam.addWebcamListener(Main.this);
                webcam.setViewSize(WebcamResolution.VGA.getSize());

                // associate camera after setting resolution!
                webcamPanel = new WebcamPanel(webcam);

                // associate swing UI to JavaFX node
                swingNode.setContent(webcamPanel);
            }
        });

        pane.expandedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(
                    ObservableValue<? extends Boolean> obs,
                    Boolean oldValue, Boolean newValue)
            {
                // run the start/stop in Swing thread just to be sure
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run()
                    {
                        final WebcamPanel webcamPanel =
                            (WebcamPanel)swingNode.getContent();

                        // stop the camera is titledpane is
                        // closed to avoid wasting CPU
                        if(!newValue)
                        {
                            System.out.println("Pause webcam: " +
                                    webcam.getName());
                            webcamPanel.stop();
                        }
                        else
                        {
                            System.out.println("Resume webcam: " +
                                    webcam.getName());
                            webcamPanel.start();
                        }
                    }
                });
            }
        });

        // adds the JavaFX UI in JavaFX thread
        Platform.runLater(new Runnable() {
            @Override
            public void run()
            {
                panes.add(pane);
                root.getChildren().add(pane);
            }
        });
    }

    /**
     * Removes a webcam UI element from layout.
     *
     * @param webcam the webcam to remove.
     */
    private void removeWebcam(Webcam webcam)
    {
        System.out.println("Removes webcam: " + webcam.getName());

        // removes UI element
        for(TitledPane pane : panes)
        {
            SwingNode swingNode = (SwingNode)pane.getContent();
            WebcamPanel panel = (WebcamPanel)swingNode.getContent();

            if(panel.getWebcam().getName().equals(webcam.getName()))
            {
                panel.stop();
                panel = null;

                // run in JavaFX thread
                Platform.runLater(new Runnable() {
                    @Override
                    public void run()
                    {
                        root.getChildren().remove(pane);
                    }
                });
                break;
            }
        }
    }

    /**
     * Starts the UI.
     *
     * @param primaryStage stage.
     */
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        root.setStyle("-fx-background-color: #000000;");
        root.setRowValignment(VPos.TOP);

        // adds initial webcams to UI
        for(Webcam webcam : Webcam.getWebcams())
        {
            addWebcam(webcam);
        }

        // register for add/remove webcams callback
        Webcam.addDiscoveryListener(this);

        Scene scene = new Scene(root, 400, 400, Color.BLACK);
        primaryStage.setTitle("JCameraControl");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream(
                        "/images/logo.png")));
        // cleanup webcams when exiting
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event)
            {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run()
                    {
                        for(TitledPane pane : panes)
                        {
                            SwingNode swingNode = (SwingNode)pane.getContent();
                            WebcamPanel webcamPanel =
                                (WebcamPanel)swingNode.getContent();
                            webcamPanel.stop();
                        }
                        panes.clear();
                    }
                });
            }
        });

        // at least display fully one VGA size webcam
        primaryStage.setMinWidth(WebcamResolution.VGA.getSize().width);
        primaryStage.setMinHeight(WebcamResolution.VGA.getSize().height + 100);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void webcamFound(WebcamDiscoveryEvent event)
    {
        addWebcam(event.getWebcam());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void webcamGone(WebcamDiscoveryEvent event)
    {
        removeWebcam(event.getWebcam());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void webcamOpen(WebcamEvent event)
    {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void webcamClosed(WebcamEvent event)
    {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void webcamDisposed(WebcamEvent event)
    {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void webcamImageObtained(WebcamEvent event)
    {
    }
}

